package com.nodove.WSD_Assignment_03.repository.CrawlerRepository;

import com.nodove.WSD_Assignment_03.domain.SaramIn.Comment;
import com.nodove.WSD_Assignment_03.domain.SaramIn.JobPosting;
import com.nodove.WSD_Assignment_03.domain.users;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByUserId(users userId);
    List<Comment> findByJobPosting(JobPosting jobPostingById, Pageable pageable);

    Optional<Comment> findByJobPostingAndId(JobPosting id, Long commentId);
}
