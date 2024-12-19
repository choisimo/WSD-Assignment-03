package com.nodove.WSD_Assignment_03.Crawler;

import com.nodove.WSD_Assignment_03.dto.Crawler.JobPostingsDto;
import com.nodove.WSD_Assignment_03.service.CrawlerService;
import com.nodove.WSD_Assignment_03.service.redisService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.http.HttpResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobCrawler {

    @Value("${crawler.saramin.url}")
    private String baseUrl;

    private final CrawlerService crawlerService;

    // @PostConstruct 어노테이션을 사용하여 스프링 빈이 생성된 후에 해당 메소드가 실행 되도록 함
    @PostConstruct @Transactional
    public void startCrawling() {
        log.info("Starting Saramin crawling...");

        String searchKeyword = "웹 개발자"; // 검색 키워드
        int totalPage = 5; // 크롤링할 페이지 수

        for (int page = 1; page <= totalPage; page++) {
            String url = String.format("%s?searchType=search&searchword=%s&recruitPage=%d", baseUrl, searchKeyword, page);

            try {

                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.5735.199 Safari/537.36")
                        .timeout(5000)
                        .get();
                log.info("HTML Content: {}", doc.outerHtml());


                // 크롤링할 페이지에서 채용 공고 리스트 가져오기
                Elements jobElements = doc.select(".item_recruit");
                if (jobElements.isEmpty()) {
                    log.info(jobElements.toString());
                    log.info("No job elements found on page {}", page);
                    continue;
                }


                for (Element job : jobElements) {
                    try {
                        Elements conditions = job.select(".job_condition span");

                        String companyName = job.select(".corp_name a").text().strip();
                        String title = job.select(".job_tit a").text().strip();
                        String link = "https://www.saramin.co.kr" + job.select(".job_tit a").attr("href").strip();
                        String logo = job.select(".logo img").attr("src").strip();
                        String location = !conditions.isEmpty() ? conditions.get(0).text().strip() : "";
                        String experience = conditions.size() > 1 ? conditions.get(1).text().strip() : "";
                        String education = conditions.size() > 2 ? conditions.get(2).text().strip() : "";
                        String employmentType = conditions.size() > 3 ? conditions.get(3).text().strip() : "";
                        String salary = conditions.size() > 4 ? conditions.get(4).text().strip() : null;
                        String deadline = job.select(".job_date .date").text().strip();
                        String sector = job.select(".job_sector").text().strip();

                        JobPostingsDto jobPostingsDto = JobPostingsDto.builder()
                                .title(title)
                                .companyName(companyName)
                                .location(location)
                                .logo(logo)
                                .experience(experience)
                                .education(education)
                                .employmentType(employmentType)
                                .salary(salary)
                                .deadline(deadline)
                                .sector(sector)
                                .link(link)
                                .build();

                        // Save data via CrawlerService
                        crawlerService.saveJobPosting(jobPostingsDto);

                    } catch (Exception e) {
                        log.warn("Error parsing job element: {}", e.getMessage());
                    }
                }

                log.info("Page {} crawling completed.", page);
                Thread.sleep(1000); // Delay between page requests for rate-limiting

            } catch (IOException e) {
                log.error("Error connecting to URL {}: {}", url, e.getMessage());
            } catch (InterruptedException e) {
                log.error("Crawling interrupted: {}", e.getMessage());
                Thread.currentThread().interrupt();
                break;
            }
        }

        log.info("Saramin crawling completed.");
    }
}
