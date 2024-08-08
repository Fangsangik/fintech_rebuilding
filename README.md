# fintech_rebuilding
## 회원 관리 및 계좌 관리 

## 제작 기간 : 2024/08/06 ~
### 사용 기술 (Java, H2Database, SpringBoot, JPA)

24/08/06
#### [1] 회원 가입 서비스 
- 회원 저장
- 회원 id 조회
- 회원 가입
- 회원 탈퇴
  - testCode
  - troubleShooting
    실행결과 -> h2 database에 값이 정확히 올라기지 않음(o) / 해결 : (@Transaction)을 걸어놨었음..

24/08/07
#### [2] 계좌 생성 및 계좌 번호 중복 방지 
- 계좌 조회
- 계좌 삭제
  - testCode
  - troubleShooting
    - delete문 진행시 NullPointException 발생   
    
- 계좌 생성시 제약

#### [3] 은행 업무 서비스 (24/08/09)
- 송금
- 출금
- 회원 등급에 따른 수수료 할인 (24/08/06)

TroubelShooting
- 순환 의존성 문제 발생 
    - RepositoryImpl 클레스들 끼리 양방향으로 설정이 되어 있어 삭제 -> Service 부분에 필요한 비즈니스 로직 추가 
      찌꺼기가 남아있어 직접 파일 내부에서 삭제 처리 
      -> 해결 
