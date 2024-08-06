package miniproject.fintech.type;

/**
 * 회원 등급
 */
public enum Grade {
    VIP("특별회원"),
    NORMAL("일반회원");

    private String memberGrade;

    Grade(String memberGrade) {
        this.memberGrade = memberGrade;
    }
}
