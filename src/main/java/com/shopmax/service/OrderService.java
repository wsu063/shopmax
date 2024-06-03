package com.shopmax.service;

import com.shopmax.dto.OrderDto;
import com.shopmax.dto.OrderHistDto;
import com.shopmax.dto.OrderItemDto;
import com.shopmax.entity.*;
import com.shopmax.repository.ItemImgRepository;
import com.shopmax.repository.ItemRepository;
import com.shopmax.repository.MemberRepository;
import com.shopmax.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final ItemRepository itemRepository;
    private final ItemImgRepository itemImgRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;

    //주문하기
    public Long order(OrderDto orderDto, String email) {
        //주문한 상품의 item객체를 가져온다.
        Item item = itemRepository.findById(orderDto.getItemId()).orElseThrow(EntityNotFoundException::new);

        //2. 현재 로그인한 회원의 이메일을 이용해  member 엔티티를 가져온다.
        Member member = memberRepository.findByEmail(email);


        //양방향 관계일때 save
        List<OrderItem> orderItemList = new ArrayList<>();
        //주문된 상품
        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
        orderItemList.add(orderItem);

        //양방향이든 단방향이든 참조하는 객체를 무조건 넣은 후 save
        //양방향일때는 부모 엔티티 객체가 참조하는 자식 객체를 넣은 후 save
        //단방향일때는 자식 엔티티 객체가 참조하는 부모 객체를 넣은 후 save
        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);


        return order.getId();
    }

    //주문 목록을 가져오기
    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderList(String email, Pageable pageable) {
        //1. 유저 아이디를 이용해서 주문 목록을 조회
        List<Order> orders = orderRepository.findOrders(email, pageable);

        //2.유저의 총 주문 갯수를 구한다
        Long totalCount = orderRepository.countOrder(email);

        //주문 내역이 여러개가 있다.
        List<OrderHistDto> orderHistDtos = new ArrayList<>();

        //3. 주문리스트 orders를 순회하면서 컨트롤러에 전달할 Dto(OrderHistDto)를 생성
        for (Order order : orders) {
            OrderHistDto orderHistDto = new OrderHistDto(order);

            //양방향 참조를 하고있으므로 order를 통해 orderItem을 가져온다.
            //JPA의 특성상 join을 하지 않아도 order안에 orderItem 레코드가 들어있다.
            List<OrderItem> orderItems = order.getOrderItems();

            //☆최종적으로 orderItems 리스트를 orderHistDto에 넣어줘야한다.
            for (OrderItem orderItem : orderItems) {
                ItemImg itemImg = itemImgRepository.findByItemIdAndRepImgYn(orderItem.getItem().getId(), "Y");
                OrderItemDto orderItemDto = new OrderItemDto(orderItem, itemImg.getImgUrl());

                orderHistDto.addOrderItemDto(orderItemDto);
            }
            orderHistDtos.add(orderHistDto); // 주문내역
        }
        return new PageImpl<>(orderHistDtos, pageable, totalCount);
    }
    
    //본인확인
    @Transactional(readOnly = true)
    public boolean validateOrder(Long orderId, String email) {
        //로그인한 사용자 찾기
        Member curMember = memberRepository.findByEmail(email);
        //주문내역 찾기
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        
        //주문한 사용자 찾기
        Member savedMember = order.getMember();

        if (!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())) {
            return false;
        }
        return true;
    }

    //주문 취소
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);

        order.cancelOrder(); // update
    }

    //주문 삭제
    public void deleteOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);

        // CasCade 설정을 통해 order의 자식 엔티티에 해당하는 orderItem도 같이 삭제된다.
        orderRepository.delete(order);
    }


    

}
