package com.freshkit.webproject.user.mapper;

import com.freshkit.webproject.product.dto.OrderDto;
import com.freshkit.webproject.product.dto.OrderItemDto;
import com.freshkit.webproject.product.dto.ProductDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * MyPageMapper는 마이페이지와 관련된 데이터베이스 작업을 처리하는 MyBatis 매퍼 인터페이스입니다.
 * - 특정 사용자의 주문 내역, 주문 상품 목록, 주문 아이템 정보를 조회하는 기능을 제공합니다.
 */
@Mapper
public interface MyPageMapper {

    /**
     * 특정 사용자의 모든 주문 정보를 조회합니다.
     *
     * @param memberId 조회할 사용자의 고유 ID
     * @return List<OrderDto> 형태의 주문 목록
     */
    List<OrderDto> getOrdersByUserId(@Param("memberId") String memberId);

    /**
     * 특정 사용자의 주문에 포함된 모든 제품 정보를 조회합니다.
     *
     * @param memberId 조회할 사용자의 고유 ID
     * @return List<ProductDto> 형태의 제품 목록
     */
    List<ProductDto> getProductsByOrderId(@Param("memberId") String memberId);

    /**
     * 특정 주문에 포함된 주문 아이템 정보를 조회합니다.
     *
     * @param orderId 조회할 주문의 고유 ID
     * @return List<OrderItemDto> 형태의 주문 아이템 목록
     */
    List<OrderItemDto> getOrderItemsByOrderId(String orderId);
}
