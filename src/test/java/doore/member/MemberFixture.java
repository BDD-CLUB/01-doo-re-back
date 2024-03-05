package doore.member;

import doore.member.domain.Member;

public class MemberFixture {
    public static Member 아마란스() {
        return Member.builder()
                .name("아마란스")
                .googleId("1234")
                .email("bbb@gmail.com")
                .imageUrl("https://aaa")
                .googleId("48729483")
                .build();
    }

    public static Member 아마어마어마() {
        return Member.builder()
                .name("아마어마어마")
                .googleId("1234")
                .email("bcde@gmail.com")
                .imageUrl("https://aaa")
                .googleId("48729483")
                .build();
    }

    public static Member 아마스() {
        return Member.builder()
                .name("아마스")
                .googleId("1234")
                .email("test4@gmail.com")
                .imageUrl("https://aaa")
                .googleId("48729483")
                .build();
    }

    public static Member 보름() {
        return Member.builder()
                .name("보름")
                .googleId("1234")
                .email("bbb@gmail.com")
                .imageUrl("https://aaa")
                .googleId("48729483")
                .build();
    }

    public static Member 짱구() {
        return Member.builder()
                .name("짱구")
                .googleId("1234")
                .email("testtest@gmail.com")
                .imageUrl("https://aaa")
                .googleId("48729483")
                .build();
    }

    public static Member 비비아마() {
        return Member.builder()
                .name("비비아마")
                .googleId("1234")
                .email("test@gmail.com")
                .imageUrl("https://aaa")
                .googleId("48729483")
                .build();
    }

    public static Member 아마() {
        return Member.builder()
                .name("아마")
                .googleId("1234")
                .email("test@gmail.com")
                .imageUrl("https://aaa")
                .googleId("48729483")
                .build();
    }
}
