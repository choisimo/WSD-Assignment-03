package com.nodove.WSD_Assignment_03.constants;

import com.nodove.WSD_Assignment_03.domain.SaramIn.JobPosting;
import org.springframework.data.jpa.domain.Specification;

public class JobPostingSpecifications {
    public static Specification<JobPosting> hasLocation(String location) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("location"), location);
    }

    public static Specification<JobPosting> hasExperience(String experience) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("experience"), experience);
    }

    public static Specification<JobPosting> hasSalary(String salary) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("salary"), salary);
    }

    public static Specification<JobPosting> hasTechStack(String techStack) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("techStack"), "%" + techStack + "%");
    }

    public static Specification<JobPosting> hasKeyword(String keyword) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.or(
                        criteriaBuilder.like(root.get("title"), "%" + keyword + "%"),
                        criteriaBuilder.like(root.get("description"), "%" + keyword + "%")
                );
    }

    public static Specification<JobPosting> hasCompanyName(String companyName) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("company").get("name"), companyName);
    }

    public static Specification<JobPosting> hasPosition(String position) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("position"), "%" + position + "%");
    }
}
