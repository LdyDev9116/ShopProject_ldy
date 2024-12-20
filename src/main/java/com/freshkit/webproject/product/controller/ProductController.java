package com.freshkit.webproject.product.controller;

import com.freshkit.webproject.product.dto.OrderItemDto;
import com.freshkit.webproject.product.dto.ProductDto;
import com.freshkit.webproject.product.dto.UpdateCartRequest;
import com.freshkit.webproject.product.dto.UpdateCartResponse;
import com.freshkit.webproject.product.service.CartService;
import com.freshkit.webproject.product.service.ProductService;
import com.freshkit.webproject.user.service.AuthService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Controller
public class ProductController {

    // 서비스 객체들 선언
    private final ProductService productService;
    private final CartService cartService;
    private final AuthService authService;

    // 생성자를 통해 필요한 서비스 객체를 주입
    public ProductController(ProductService productService, CartService cartService, AuthService authService) {
        this.productService = productService;
        this.cartService = cartService;
        this.authService = authService;
    }

    /**
     * 전체 제품 목록을 가져와 "prd3" 페이지에 전달합니다.
     * @param model 뷰에 데이터를 전달하기 위한 모델 객체
     * @param request 현재 HTTP 요청 객체로 로그인 여부 확인에 사용
     * @return "prd3" 페이지 이름
     */
    @GetMapping("/prd3")
    public String getAllProducts(Model model, HttpServletRequest request) {
        log.info("크롤링 데이터");
        List<ProductDto> products = productService.getProducts();
        model.addAttribute("products", products);

        Boolean isLoggedIn = authService.isLoggedIn(request);
        model.addAttribute("isLoggedIn", isLoggedIn);
        return "prd3";
    }

    /**
     * 에러 페이지로 이동 (404 에러)
     * @param model 뷰에 데이터를 전달하기 위한 모델 객체
     * @param request 현재 HTTP 요청 객체로 로그인 여부 확인에 사용
     * @return "error-404" 페이지 이름
     */
    @GetMapping("/error-404")
    public String getAllProducts11(Model model, HttpServletRequest request) {
        log.info("크롤링 데이터");
        List<ProductDto> products = productService.getProducts();
        model.addAttribute("products", products);

        Boolean isLoggedIn = authService.isLoggedIn(request);
        model.addAttribute("isLoggedIn", isLoggedIn);
        return "error-404";
    }

    /**
     * 특정 제품의 상세 정보를 가져와 "shop-single" 페이지에 전달합니다.
     * @param id 상세 정보를 조회할 제품 ID
     * @param model 뷰에 데이터를 전달하기 위한 모델 객체
     * @param request 현재 HTTP 요청 객체로 로그인 여부 확인에 사용
     * @return "shop-single" 페이지 이름 또는 제품이 없을 경우 "error-404" 페이지 이름
     */
    @GetMapping("/shop-single")
    public String getProductDetails(@RequestParam Long id, Model model, HttpServletRequest request) {
        log.info("shop-single 출력, ID: " + id);

        // 제품 정보를 ID로 조회
        Optional<ProductDto> productOpt = productService.getProductById(id);
        if (productOpt.isPresent()) {
            model.addAttribute("product", productOpt.get());
        } else {
            log.error("Product not found with ID: " + id);
            return "error-404";
        }

        Boolean isLoggedIn = authService.isLoggedIn(request);
        model.addAttribute("isLoggedIn", isLoggedIn);

        List<ProductDto> relatedProducts = productService.getProducts();
        model.addAttribute("relatedProducts", relatedProducts);
        return "shop-single";
    }

    /**
     * 장바구니 페이지를 가져와 "shop-cart"에 전달합니다.
     * @param model 뷰에 데이터를 전달하기 위한 모델 객체
     * @param request 현재 HTTP 요청 객체로 로그인 여부 확인에 사용
     * @return "shop-cart" 페이지 이름
     */
    @GetMapping("/shop-cart")
    public String getShopCart(Model model, HttpServletRequest request) {
        log.info("shop-cart 출력 ");
        Boolean isLoggedIn = authService.isLoggedIn(request);
        model.addAttribute("isLoggedIn", isLoggedIn != null && isLoggedIn);

        String userIdStr = authService.getUserId(request);
        double totalCartPrice = 0.0;

        if (userIdStr != null) {
            List<OrderItemDto> orderItems = cartService.getorderItems(userIdStr);
            model.addAttribute("orderItems", orderItems);

            // 총 가격 계산
            for (OrderItemDto item : orderItems) {
                totalCartPrice += item.getTotalPrice();
            }
        } else {
            model.addAttribute("orderItems", List.of());
        }

        model.addAttribute("totalCartPrice", totalCartPrice);
        return "shop-cart";
    }

    /**
     * 제품을 장바구니에 추가하고, 성공 시 장바구니 페이지로 리다이렉트합니다.
     * @param productId 추가할 제품의 ID
     * @param productName 추가할 제품의 이름
     * @param quantity 제품 수량
     * @param price 제품 가격
     * @param request 현재 HTTP 요청 객체로 사용자 인증에 사용
     * @param model 뷰에 에러 메시지를 전달하기 위한 모델 객체
     * @return "shop-cart" 페이지로 리다이렉트 또는 에러 발생 시 "error" 페이지 이름
     */
    @PostMapping("/add-to-cart")
    public String addToCart(
            @RequestParam Long productId,
            @RequestParam String productName,
            @RequestParam int quantity,
            @RequestParam int price,
            HttpServletRequest request,
            Model model) {
        log.info("장바구니에 추가 요청 - Product ID: {}, Product Name: {}, Quantity: {}, Price: {}", productId, productName, quantity, price);

        String userIdStr = authService.getUserId(request);
        if (userIdStr != null) {
            try {
                cartService.addToCart(userIdStr, productId, productName, quantity, price);
                return "redirect:/shop-cart";
            } catch (Exception e) {
                log.error("장바구니 추가 중 오류 발생: ", e);
                model.addAttribute("errorMessage", "장바구니 추가 중 오류가 발생했습니다.");
                return "error";
            }
        } else {
            log.info("사용자 ID 없음");
            return "redirect:/account-signin";
        }
    }

    /**
     * 장바구니의 제품 수량을 업데이트 (증가/감소)
     * @param request 장바구니 항목 업데이트 요청 객체
     * @param httpRequest 현재 HTTP 요청 객체로 사용자 인증에 사용
     * @return 업데이트된 항목의 수량을 포함한 응답
     */
    @PostMapping("/update-cart")
    @ResponseBody
    public ResponseEntity<?> updateCart(@RequestBody UpdateCartRequest request, HttpServletRequest httpRequest) {
        String userIdStr = authService.getUserId(httpRequest);
        if (userIdStr == null) {
            return ResponseEntity.badRequest().body(new UpdateCartResponse(false, "User not authenticated", 0));
        }

        try {
            if ("increment".equals(request.getAction())) {
                cartService.incrementCartItem(userIdStr, request.getProductId());
            } else if ("decrement".equals(request.getAction())) {
                cartService.decrementCartItem(userIdStr, request.getProductId());
            } else {
                return ResponseEntity.badRequest().body(new UpdateCartResponse(false, "Invalid action", 0));
            }

            List<OrderItemDto> orderItems = cartService.getorderItems(userIdStr);
            Optional<OrderItemDto> updatedItem = orderItems.stream()
                    .filter(item -> item.getProductId().equals(request.getProductId()))
                    .findFirst();

            if (updatedItem.isPresent()) {
                return ResponseEntity.ok(new UpdateCartResponse(true, "Success", updatedItem.get().getQuantity()));
            } else {
                return ResponseEntity.badRequest().body(new UpdateCartResponse(false, "Item not found in cart", 0));
            }
        } catch (Exception e) {
            log.error("Error updating cart:", e);
            return ResponseEntity.status(500).body(new UpdateCartResponse(false, "Error updating cart", 0));
        }
    }

    /**
     * 선택한 제품을 장바구니에서 제거합니다.
     * @param productIds 장바구니에서 제거할 제품 ID 리스트
     * @param request 현재 HTTP 요청 객체로 사용자 인증에 사용
     * @return 제거 상태를 포함한 응답
     */
    @PostMapping("/remove-selected-from-cart")
    @ResponseBody
    public ResponseEntity<Map<String, String>> removeSelectedFromCart(@RequestBody List<Long> productIds, HttpServletRequest request) {
        String userIdStr = authService.getUserId(request);
        if (userIdStr == null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "User not authenticated");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        try {
            cartService.removeSelectedFromCart(userIdStr, productIds);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Selected items removed");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error removing selected items from cart:", e);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Error removing selected items from cart");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 장바구니에서 단일 제품 제거합니다.
     * @param payload 제품 ID가 포함된 요청 데이터
     * @param request 현재 HTTP 요청 객체로 사용자 인증에 사용
     * @return 제거 상태를 포함한 응답
     */
    @PostMapping("/remove-from-cart")
    @ResponseBody
    public ResponseEntity<Map<String, String>> removeFromCart(@RequestBody Map<String, Long> payload, HttpServletRequest request) {
        String userIdStr = authService.getUserId(request);
        if (userIdStr == null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "User not authenticated");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        Long productId = payload.get("productId");
        if (productId == null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Invalid product ID");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            cartService.removeFromCart(userIdStr, productId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Item removed");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error removing item from cart:", e);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Error removing item from cart");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 장바구니의 모든 제품을 제거합니다.
     * @param request 현재 HTTP 요청 객체로 사용자 인증에 사용
     * @return 모든 항목 제거 상태를 포함한 응답
     */
    @PostMapping("/remove-all-from-cart")
    @ResponseBody
    public ResponseEntity<Map<String, String>> removeAllFromCart(HttpServletRequest request) {
        String userIdStr = authService.getUserId(request);
        if (userIdStr == null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "User not authenticated");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        try {
            cartService.clearCart(userIdStr);
            Map<String, String> response = new HashMap<>();
            response.put("message", "All items removed");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error removing all items from cart:", e);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Error removing all items from cart");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * API를 통해 모든 제품을 JSON 형식으로 반환합니다. (크롤링 상품 리스트 데이터)
     * @return 모든 제품 목록을 포함한 응답
     */
    @GetMapping("/api/products")
    @ResponseBody
    public ResponseEntity<List<ProductDto>> getProductsApi() {
        try {
            List<ProductDto> products = productService.getProducts();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            log.error("Error fetching products:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
