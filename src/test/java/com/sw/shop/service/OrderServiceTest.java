package com.sw.shop.service;

import com.sw.shop.domain.Address;
import com.sw.shop.domain.Member;
import com.sw.shop.domain.Order;
import com.sw.shop.domain.OrderStatus;
import com.sw.shop.domain.item.Book;
import com.sw.shop.domain.item.Item;
import com.sw.shop.exception.NotEnoughStockException;
import com.sw.shop.repository.OrderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    public void 상품_주문() throws Exception{
        //given
        Member member = createMember();

        Item book = createBook("오직 두사람", 12000, 10);

        int orderCount = 3;

        //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals("상품 주문시 상태는 ORDER", OrderStatus.ORDER ,getOrder.getStatus());
        assertEquals("주문한 상품 종류수가 동일해야 한다", 1, getOrder.getOrderItems().size());
        assertEquals("주문가격은 수량 * 가격이다.", 12000 * orderCount, getOrder.getTotalPrice());
        assertEquals("주문 수량만큼 재고가 줄어야한다.", 10 - orderCount, book.getStockQuantity());
    }

    @Test(expected = NotEnoughStockException.class)
    public void 재고_수량_초과() throws Exception{
        //given
        Member member = createMember();
        Item book = createBook("오직 두사람", 12000, 10);

        int orderCount = 11;

        //when
        orderService.order(member.getId(), book.getId(), orderCount);

        //then
        fail("재고 수량 오류가 발생해야한다.");
    }

    @Test
    public void 주문_취소() throws Exception{
        //given
        Member member = createMember();
        Item book = createBook("살인자의 기억법", 10000, 10);

        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //when
        orderService.cancelOrder(orderId);

        //then
        Order order = orderRepository.findOne(orderId);

        assertEquals("취문 취소시 상태는 CANCEL", OrderStatus.CANCEL, order.getStatus());
        assertEquals("주문이 취소된 상품은 재고가 되돌아와야한다.", 10, book.getStockQuantity());
    }

    private Item createBook(String name, int price, int quantity) {
        Item book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(quantity);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("kim");
        member.setAddress(new Address("서울", "강서구", "123-123"));
        em.persist(member);
        return member;
    }
}