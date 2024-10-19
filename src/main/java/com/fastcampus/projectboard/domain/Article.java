package com.fastcampus.projectboard.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@ToString
@Table(indexes = {
        @Index(columnList = "title"),
        @Index(columnList = "hashtag"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Article {
    //    Mysql은 자동 숫자 증가는 Identity만가능, 시퀀스 사용시 오류
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false)
    private String title; // 제목

    @Setter
    @Column(nullable = false, length = 10000)
    private String content; // 내용

    @Setter
    private String hashtag; // 해시태그

    //양방향 바인딩(실무에서는 일부러 풀고 작업함, 데이터 마이닝이불편해지거나, 원치 않는 데이터 삭제됨)
    //운영시 게시글은 사라져도 댓글은 사라지지 않게 하는 경우가 있음
    //중복을 허용하지 않음(Set)
    //mappedBy를 하지 않는 경우 기본값으로 테이블끼리 _ 형식으로 연결하는 불상사, 근데 왜 article 이지? ->
    //ArticleComment 클래스에서 Article을 참조하는 필드 이름이 article을 의미함!! 즉 자식객체
    @ToString.Exclude //순환 참조를 발생시켜서 시스템이 중단되는것을 방지하고자 순환참조를 끊음
    @OrderBy("id")
    //
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private final Set<ArticleComment> articleComments = new LinkedHashSet<>();


    //메타 데이터 필드
    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt; // 생성 일시

    @CreatedBy
    @Column(nullable = false)
    private String createdBy; // 생성자

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime modifiedAt;

    @LastModifiedBy
    @Column(nullable = false, length = 100)
    private String modifiedBy; // 수정자

    // 하이버네이트는 기본적으로 생성자를 public과 protected만 가능함
    protected Article() {
    }

    // 기본 생성자 생성시 기본으로 입력되는 값들을 넣을경우 불필요함...
    public Article(String title, String content, String hashtag) {
        this.title = title;
        this.content = content;
        this.hashtag = hashtag;
    }

    // 정적 팩토리 메서드(static 메서드로 객체를 생성하고 반환하는 역할, new 키워드를 직접 사용하지 않고도
    // 객체를 생성할수 있도록 함으로써 코드의 가독성을 높이고, 객체 생성에 관한 로직을 캡슐화 할수 있음
    // 메서드 이름을 통해 객체 의도를 생성 할 수 있기 때문에 코드의 이해를 돕는데 유리
    public static Article of(String title, String content, String hashtag) {
        return new Article(title, content, hashtag);
    }


    @Override
    public boolean equals(Object o) { //두 객체가 같은지를 비교하는 메서드.. 객체의 동등성을 정의할 수 있음
        if (this == o) return true; // 같은 객체를 참조하고 있으면 true
        // instanceof -> 타입을 검사하고 article 변수에 타입 캐스팅된 o 객체를 할당
        if (!(o instanceof Article article)) return false; //비교 대상이 Article 클래스의 인스턴스가 아니면 false
        // 현재 객체의 id가 비교 대상 객체의 Id가 같으면 true를 반환
        return id != null && id.equals(article.id); // id가 null이 아니고, 두 객체의 id가 같으면 true 반환

    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
