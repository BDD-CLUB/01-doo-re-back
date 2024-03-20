//package doore.config;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@RequiredArgsConstructor
//@EnableWebSecurity
//@EnableMethodSecurity(securedEnabled = true)
//public class SecurityConfig {
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.authorizeHttpRequests(authorize ->
//                        authorize
//                                .requestMatchers("/teams/**").hasAnyRole("팀장", "팀원")
//                                .requestMatchers("/studies/**").hasAnyRole("스터디장", "스터디원")
//                                .anyRequest().permitAll()
//                );
//
//        return http.build();
//    }
//}
