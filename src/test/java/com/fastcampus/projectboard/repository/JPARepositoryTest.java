package com.fastcampus.projectboard.repository;

import com.fastcampus.projectboard.config.JpaConfig;
import com.fastcampus.projectboard.domain.Article;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;


@DisplayName("JPA 연결 테스트")
@Import(JpaConfig.class) // 직접 만든 클래스 이기 때문에 이녀석을 알수 없음, 추가해야함
@DataJpaTest //PA 엔티티나 리포지토리 계층만을 테스트할 때 사용, 가벼운테스트로 변경 사항이 자동으로 롤백
class JPARepositoryTest {
    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleCommentRepository articleCommentRepository;


    @DisplayName("Select 테스트")
    @Test
    void given_when_then() {
        // Given

        // When
        List<Article> articles = articleRepository.findAll();

        // Then
        Assertions.assertThat(articles)
                .isNotNull()
                .hasSize(100);
    }

    @DisplayName("Insert 테스트")
    @Test
    void insertTest() {
        // Given
        long previousCount = articleRepository.count();

        // When
        Article article = Article.of("new article", "new content", "#spring");
        articleRepository.save(article);

        // Then
        Assertions.assertThat(articleRepository.count()).isEqualTo(previousCount + 1);
    }

    @DisplayName("Update 테스트")
    @Test
    void updateTest() {
        // Given
        Article article = articleRepository.findById(1L).orElseThrow();
        String updateHashtag = "#updatedSpringBoot";
        article.setHashtag(updateHashtag);

        // When
        Article savedArticle = articleRepository.saveAndFlush(article); //saveAndFlush를 하면 하이버네이트의 update쿼리문을 볼수 있음

        // Then
        Assertions.assertThat(savedArticle).hasFieldOrPropertyWithValue("hashtag", updateHashtag);
    }

    @DisplayName("Delete 테스트")
    @Test
    void deleteTest() {
        // Given
        Article article = articleRepository.findById(1L).orElseThrow();
        long previousArticleCount = articleRepository.count();
        long previousArticleCommentCount = articleCommentRepository.count();
        int deletedCommentsSize = article.getArticleComments().size();

        // When
        articleRepository.delete(article);

        // Then
        Assertions.assertThat(articleRepository.count()).isEqualTo(previousArticleCount - 1);
        Assertions.assertThat(articleCommentRepository.count()).isEqualTo(previousArticleCommentCount - deletedCommentsSize);
    }

}