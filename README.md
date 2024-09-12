# fintech_rebuilding
## 회원 관리 및 계좌 관리 

## 제작 기간 : 2024/08/06 ~ 2024/09/12
### 사용 기술 (Java, H2Database, SpringBoot, JPA)

24/08/06
#### [1] 회원 가입 서비스 
- 회원 저장
- 회원 id 조회
- 회원 가입
- 회원 탈퇴
- 회원 Update
 
24/08/07
#### [2] 계좌 생성 및 계좌 번호 중복 방지 
- 계좌 조회
- 계좌 삭제
- 계좌 생성
- 계좌 update
    
- 계좌 생성시 제약

#### [3] 은행 업무 서비스 (24/08/09)
- 송금
- 출금
- 회원 등급에 따른 수수료 할인 (24/08/06)

## TroubelShooting
- 순환 의존성 문제 발생 
    - RepositoryImpl 클레스들 끼리 양방향으로 설정이 되어 있어 삭제 -> Service 부분에 필요한 비즈니스 로직 추가 
      찌꺼기가 남아있어 직접 파일 내부에서 삭제 처리 
      -> 해결 
- org.hibernate.LazyInitializationException: failed to lazily initialize a collection of role: miniproject.fintech.domain.BankMember.accounts, could not initialize proxy - no Session 
findById의 경우 단순 조회이기 때문에 Transaction 부분에 readOnly 추가 -> 해결 
- 실행결과 -> h2 database에 값이 정확히 올라기지 않음(o) / 해결 : (@Transaction)을 걸어놨었음..
- delete문 진행시 NullPointException 발생 -> 해결
- PostMan으로 bankMember 생성시 null 값 발생 -> BankMemberDto에 id값 빠져있었음, controller에 null값 방지 추가해서  해결
- springSecurity 권한 설정 진행시 -> permitall의 경우 프로그램이 진행 됬지만, 권한을 hasRole로 지정해준뒤 진행을 했을 경우 권한을 인증 받지 못해 진행이 되지 않음 \
  -> springSecurity에 대해 더 공부를 한 후 진행해야 할듯 하다.
     jwt 토큰 문제일까 아니면, 권한 자체에 대한 지정 문제일지 공부를 한 후 refactoring을 해야 겠다
- service 로직에 dto를 사용하는게 맞을지 entity를 사용하는게 맞을지 고민을 많이 함.. 
  entity를  건드리지 않는게 좋다고 판단이 들었다가, 쿼리에서 join이 되지 않거나, fk가 설정되지 않은 경우를 발견함 -> entity로 변경 
  하지만 다시 깊은 고민이 들었다. dto를 사용하는게 더 좋지 않나, dto는 단순 db 교환 또는 전송이 주를 이루는데, 그러면 보안 적인 문제에서도 훨씬 좋고, entity의 경우 중요한 정보를 소유하거나 각 domain 별로 연관관계를 맺고 있어
  직접 사용하는 것보다는 dto로 변경하는게 좋지 않을까라는 생각이 들었다.
  그래서 다시 dto로 변경후 converter라는 dto를 만들어서 entity가 dto로 변경되게 사용함, join 문제와, 연관관계 문제도 converter에 같이 적용 -> 해결
- api test 진행시 account를 생성할때 bankMemberId가 null로 계속 적으로 처리되는 것 확인 -> dto 부분에 bankMemberId 값을 설정을 안해즘 -> 삽질

- 추후 보안할 내용
  -> 거래 진행시, 비밀번호 인증 요구, sms 요청 로직 작성 해보기, springSecurity 권한 설정 문제 해결하기 (해결?) 

## 추가로 구현해보고 싶은 기능 (다음 프로젝트에...)
- SpringSecurity로 login service 만들어보기 (Success)
- Controller 부분 redirect 설정해보기
