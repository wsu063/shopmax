package com.shopmax.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name="order_item")
@Getter
@Setter
@ToString
public class OrderItem extends BaseEntity{
    @Id
    @Column(name = "order_item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int orderPrice;

    private int count;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    public static OrderItem createOrderItem(Item item, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setCount(count);
        orderItem.setOrderPrice(item.getPrice());
//        orderItem.setOrder(orderItem.order);

        item.removeStock(count); // item객체 안의 재고 변경

        return orderItem;
    }

    public int getTotalPrice() {

        return orderPrice * count; // 총 가격 구해주는 함수
    }

    //주문취소. 재고를 원래대로
    public void cancel() {
        //주문을 한만큼(count) 다시 item의 stock을
        this.getItem().addStock(this.count);
    }

}
