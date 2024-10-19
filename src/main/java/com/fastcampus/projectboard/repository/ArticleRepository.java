package com.fastcampus.projectboard.repository;

import com.fastcampus.projectboard.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

// CMD + N -> 테스트 코드 작성
public interface ArticleRepository extends JpaRepository<Article, Long> {
}