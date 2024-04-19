package com.shopmax.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(value = {AuditingEntityListener.class})
@MappedSuperclass
@Getter
@Setter
public abstract class BaseTimeEntity {

    @CreatedDate // 게시물 최초로 등록한 날짜를 저장 및 감지
    @Column(updatable = false) // 해당 컬럼에 대한 값은 업데이트 X
    private LocalDateTime regTime;

    @LastModifiedDate // 게시물을 마지막으로 수정한 날짜를 저장 및 감지
    private LocalDateTime updateTime;
}
