package miniproject.fintech.controller;

import miniproject.fintech.config.JwtTokenUtil;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.dto.BankMemberDto;
import miniproject.fintech.dto.EntityConverter;
import miniproject.fintech.dto.LoginRequest;
import miniproject.fintech.dto.LoginResponse;
import miniproject.fintech.repository.MemberRepository;
import miniproject.fintech.service.MemoryMemberService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import javax.sql.DataSource;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class MemberControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MemoryMemberService memberService;

    @Autowired
    private EntityConverter entityConverter;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DataSource dataSource;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private String getJwtToken() {
        // 로그인하여 JWT 토큰을 발급받습니다.
        LoginRequest loginRequest = new LoginRequest("345", "345");  // 실제 로그인 정보를 입력합니다.

        ResponseEntity<LoginResponse> response = restTemplate.postForEntity(
                "/api/login",  // JWT 토큰을 발급하는 API 경로
                loginRequest,
                LoginResponse.class
        );

        // 응답에서 JWT 토큰 추출
        return response.getBody().getToken();  // JWT 토큰 필드를 정확하게 설정하세요.
    }

    private String getJwtTokenForRoleUser() {
        LoginRequest loginRequest = new LoginRequest("345", "345");  // ROLE_USER 권한을 가진 사용자

        ResponseEntity<LoginResponse> response = restTemplate.postForEntity(
                "/api/login",
                loginRequest,
                LoginResponse.class
        );

        return response.getBody().getToken();  // JWT 토큰 반환
    }

    @BeforeEach
    public void setUp() {
        memberRepository.deleteByUserId("345");  // 345를 삭제합니다.
        BankMemberDto bankMemberDto = BankMemberDto.builder()
                .userId("345")  // 로그인에서 사용하는 userId와 일치하도록 설정
                .roles("ROLE_USER")
                .password("345")
                .build();

        BankMemberDto roleUser = memberService.createBankMember(bankMemberDto, "ROLE_USER");
        memberRepository.save(entityConverter.convertToBankMember(roleUser));
    }


    @Test
    public void testGetMemberByUserId_withUserRole_shouldReturnBankMember() {

        String url = UriComponentsBuilder.fromPath("/member/{userId}")
                .buildAndExpand("345")  // 조회할 userId를 "345"로 설정
                .toUriString();
        // JWT 토큰 발급받기
        String jwtToken = getJwtToken();

        // Authorization 헤더에 JWT 토큰 추가
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);  // Bearer 토큰 방식

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // API 요청
        ResponseEntity<BankMember> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                BankMember.class
        );

        // 응답 검증
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("345", response.getBody().getUserId());
    }

    @Test
    public void testGetMemberByUserId_withRoleUser_shouldReturn200() {
        // 권한 있는 사용자로 JWT 토큰 발급 (ROLE_USER 권한을 가진 사용자)
        String jwtToken = getJwtTokenForRoleUser();

        // Authorization 헤더에 JWT 토큰 추가
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        String url = UriComponentsBuilder.fromPath("/member/{userId}")
                .buildAndExpand("345")
                .toUriString();

        // API 요청
        ResponseEntity<BankMember> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                BankMember.class
        );

        // 응답 검증: 200 OK
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
    }

}
