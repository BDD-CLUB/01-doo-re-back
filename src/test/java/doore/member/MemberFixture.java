package doore.member;

import doore.member.domain.Member;

public class MemberFixture {
    public static Member 회원() {
        return Member.builder()
                .name("아마란스")
                .googleId("1234")
                .email("bbb@gmail.com")
                .imageUrl("https://aaa")
                .build();
    }
}
