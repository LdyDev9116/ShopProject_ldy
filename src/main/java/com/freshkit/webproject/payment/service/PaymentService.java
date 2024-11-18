package com.freshkit.webproject.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freshkit.webproject.payment.dto.PaymentDto;
import com.freshkit.webproject.payment.mapper.PaymentMapper;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    @Value("${portone.api-key}")
    private String apiKey;
    @Value("${portone.api-secret}")
    private String apiSecret;

    private final PaymentMapper paymentMapper;
    private final RestTemplate restTemplate;

    public PaymentService(PaymentMapper paymentMapper, RestTemplate restTemplate) {
        this.paymentMapper = paymentMapper;
        this.restTemplate = restTemplate;
    }

    /**
     * 결제를 검증하는 메서드.
     * @param impUid Iamport에서 제공한 결제 고유 ID
     * @param merchantUid 상점에서 생성한 주문 고유 ID
     * @return IamportResponse<Payment> 형태의 결제 검증 결과
     * @throws Exception 검증 실패 시 발생하는 예외
     */
    public IamportResponse<Payment> verifyPayment(String impUid, String merchantUid) throws Exception {
        String url = "https://api.iamport.kr/payments/" + impUid;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", getAccessToken());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            Map responseBody = response.getBody();

            if (responseBody != null && responseBody.containsKey("response")) {
                Map responseData = (Map) responseBody.get("response");
                if (responseData.containsKey("amount")) {
                    return new IamportResponse<>();
                }
            }
            throw new RuntimeException("Invalid payment verification response");

        } catch (Exception e) {
            logger.error("결제 검증 중 예외 발생: imp_uid={}, merchant_uid={}", impUid, merchantUid, e);
            throw new RuntimeException("결제 검증 중 오류 발생", e);
        }
    }

    /**
     * 결제 API 호출 시 필요한 액세스 토큰을 가져오는 메서드.
     * @return 액세스 토큰 문자열
     * @throws Exception 액세스 토큰을 가져오는 데 실패한 경우 발생하는 예외
     */
    private String getAccessToken() throws Exception {
        String url = "https://api.iamport.kr/users/getToken";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = new HashMap<>();
        body.put("imp_key", apiKey);
        body.put("imp_secret", apiSecret);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(body);

        System.out.println("요청 URL: " + url);
        System.out.println("요청 본문: " + jsonBody);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

        System.out.println("응답 상태 코드: " + response.getStatusCode());
        System.out.println("응답 본문: " + response.getBody());

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody().get("response");
            if (responseBody != null && responseBody.containsKey("access_token")) {
                return (String) responseBody.get("access_token");
            } else {
                logger.error("응답 본문에 'access_token'이 포함되지 않았습니다");
            }
        } else {
            logger.error("PortOne API에서 액세스 토큰을 가져오는 데 실패했습니다, 상태 코드: {}", response.getStatusCode());
        }

        throw new Exception("액세스 토큰을 가져오는 데 실패했습니다");
    }

    /**
     * 결제 정보를 데이터베이스에 저장합니다.
     * @param paymentDto 결제 정보를 담은 객체
     */
    @Transactional
    public void processOrder(PaymentDto paymentDto) {
        paymentMapper.insertOrder(paymentDto);
    }
}
