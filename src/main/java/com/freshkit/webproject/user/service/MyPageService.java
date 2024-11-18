package com.freshkit.webproject.user.service;

import com.freshkit.webproject.product.dto.OrderDto;
import com.freshkit.webproject.user.mapper.MyPageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * MyPageService는 마이페이지와 관련된 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * - 특정 사용자의 주문 내역 및 상세 주문 아이템 정보를 제공합니다.
 */
@Service
public class MyPageService {

    private final MyPageMapper myPageMapper;

    /**
     * MyPageService 생성자.
     * @param myPageMapper 마이페이지 관련 데이터베이스 작업을 처리하는 매퍼
     */
    @Autowired
    public MyPageService(MyPageMapper myPageMapper) {
        this.myPageMapper = myPageMapper;
    }

    /**
     * 특정 사용자의 주문 내역을 조회합니다.
     * - 주문 정보를 조회한 뒤, 각 주문에 포함된 주문 아이템 정보를 추가로 설정합니다.
     *
     * @param memberId 조회할 사용자의 고유 ID
     * @return List<OrderDto> 형태의 주문 목록
     */
    public List<OrderDto> getUserOrders(String memberId) {
        // 사용자 ID를 기반으로 모든 주문을 조회
        List<OrderDto> orders = myPageMapper.getOrdersByUserId(memberId);

        // 각 주문에 포함된 주문 아이템 정보 설정
        for (OrderDto order : orders) {
            order.setOrderItems(myPageMapper.getOrderItemsByOrderId(order.getOrderId()));
        }

        return orders;
    }
}
