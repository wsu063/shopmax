package com.shopmax.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="cart")
@Getter
@Setter
@ToString
public class Cart extends BaseEntity{
    @Id
    @Column(name="cart_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Cart가 Member를 참조
    // Member가 부모, Cart가 자식
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id") // member에 있는 값 중 member_id를 참조한다. 외래키로 사용한다.
    private Member member;


}
