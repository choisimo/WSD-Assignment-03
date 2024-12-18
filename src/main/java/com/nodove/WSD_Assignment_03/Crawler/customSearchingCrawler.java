package com.nodove.WSD_Assignment_03.Crawler;

import com.nodove.WSD_Assignment_03.dto.Crawler.JobPostingsDto;
import com.nodove.WSD_Assignment_03.service.CrawlerService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class customSearchingCrawler {

    @Value("${crawler.saramin.url}")
    private String baseUrl;

    private final CrawlerService crawlerService;

    @Transactional
    public void customSearchingCrawling(List<String> keywords, int totalPage) {
        log.info("Starting custom crawling for keywords: {}, total pages: {}", keywords, totalPage);

        for (String keyword : keywords) {
            log.info("Starting custom crawling for keyword: '{}', total pages: {}", keyword, totalPage);

            for (int page = 1; page <= totalPage; page++) {
                String url = String.format("%s?searchType=search&searchword=%s&recruitPage=%d", baseUrl, keyword, page);

                try {
                    // Connect to the URL and fetch HTML content
                    Document doc = Jsoup.connect(url)
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.5735.199 Safari/537.36")
                            .timeout(5000)
                            .get();

                    log.info("HTML content fetched for page {}: {}", page, doc.outerHtml());

                    // Extract job elements from the page
                    org.jsoup.select.Elements jobElements = doc.select(".item_recruit");
                    if (jobElements.isEmpty()) {
                        log.info("No job elements found on page {}", page);
                        continue;
                    }

                    for (Element job : jobElements) {
                        try {
                            // Parse job details
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

                            // Create DTO
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

                            // Save the parsed data
                            crawlerService.saveJobPosting(jobPostingsDto);

                        } catch (Exception e) {
                            log.warn("Error parsing job element: {}", e.getMessage());
                        }
                    }

                    log.info("Custom crawling for page {} completed.", page);
                    Thread.sleep(1000); // Rate-limiting delay between requests

                } catch (IOException e) {
                    log.error("Error connecting to URL {}: {}", url, e.getMessage());
                } catch (InterruptedException e) {
                    log.error("Crawling interrupted: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            log.info("Custom crawling for keyword '{}' completed.", keyword);
        }
    }
}
