package com.growth.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * 컨트롤러 단위 테스트용 공통 베이스 클래스
 * - MockMvc standalone setup 사용
 * - JSON 변환, 헤더 생성 유틸 제공
 */
public abstract class WebIntegrationTestBase {

    protected MockMvc mockMvc;
    protected ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    /**
     * 테스트할 컨트롤러를 기반으로 MockMvc 생성
     */
    protected void setupMockMvc(Object controller) {
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    /**
     * JSON Content-Type 헤더 생성
     */
    protected HttpHeaders createJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    /**
     * 인증 토큰 포함 JSON 헤더 생성
     */
    protected HttpHeaders createAuthHeaders(String token) {
        HttpHeaders headers = createJsonHeaders();
        headers.setBearerAuth(token);
        return headers;
    }

    /**
     * 객체를 JSON 문자열로 변환
     */
    protected String toJson(Object object) throws Exception {
        return objectMapper.writeValueAsString(object);
    }
}
