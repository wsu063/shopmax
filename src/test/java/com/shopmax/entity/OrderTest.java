package com.shopmax.entity;

import com.shopmax.constant.ItemSellStatus;
import com.shopmax.repository.ItemRepository;
import com.shopmax.repository.MemberRepository;
import com.shopmax.repository.OrderRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class OrderTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MemberRepository memberRepository;

    @PersistenceContext
    EntityManager em;

    public Item createItem(){
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("상세설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
//        item.setRegTime(LocalDateTime.now());
//        item.setUpdateTime(LocalDateTime.now());
        return item;
    }
    
    @Test
    @Transactional
    @Rollback(value=false)
    @DisplayName("영속성 전이 테스트")
    public void CascadeTest() {

        Order order = new Order();

        for (int i = 0; i < 3; i++) {
            Item item = createItem(); // 3개의 item생성
            itemRepository.save(item); // 3개의 item생성 후 insert
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
        }
        orderRepository.save(order); // 영속성 전이 상태를 만들었기 때문에 부모만 insert한다

        em.clear(); // 영속성 컨텍스트에서 분리한다.
        //DB에서 select하는 쿼리문을 실행하기 위해서 분리하는것.

        Order savedOrder = orderRepository.findById(order.getId())
                .orElseThrow(EntityNotFoundException::new);

//        System.out.println(savedOrder);

        List<OrderItem> orderItems = savedOrder.getOrderItems();

        for(OrderItem orderItem: orderItems) {
            System.out.println("orderItem ID: " + orderItem.getId());
        }

//        저장된 orderItems의 갯수가 3개면 테스트 통과
        Assertions.assertEquals(3, savedOrder.getOrderItems().size());
        
    }

    public Order createOrder() {
        Order order = new Order();

        for (int i = 0; i < 3; i++) {
            Item item = createItem();
            itemRepository.save(item);

            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item); // orderItem객체가 참조하는 item을 넣어준다.
            orderItem.setCount(10);
            orderItem.setOrder(order); // orderItem객체가 참조하는 order를 넣어준다.
            order.getOrderItems().add(orderItem); // order 객체가 참조하는 orderItem을 넣어준다.
        }

        Member member = new Member();

        memberRepository.save(member);

        order.setMember(member); // order가 참조하는 member를 넣어준다.
        orderRepository.save(order);
        //이때 orderItem은 order랑 cacade되서 잘 저장된다.

        return order;

    }

    @Test
    @Transactional
    @Rollback(value=false)
    @DisplayName("고아객체 제거 테스트")
    public void orphanRemovalTest() {
        Order order = createOrder();
        List<OrderItem> orderItems = order.getOrderItems();
        
        //고아객체로 만들어서 자식 레코드만 따로 제거
        orderItems.remove(0);

        em.flush(); // 강제커밋
    }
}
