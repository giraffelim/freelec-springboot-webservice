package com.girrafelim.book.springboot.config.auth;

import com.girrafelim.book.springboot.domain.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@RequiredArgsConstructor
@EnableWebSecurity // Spring Security 설정들을 활성화 시켜줌
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .headers().frameOptions().disable() // h2-console 화면을 사용하기 위해 해당 옵션을 disable 처리
                .and()
                    .authorizeRequests() // URL별 권한관리를 설정하는 옵션의 시작점, 이 옵션이 있어야만 antMachers 옵션을 사용할 수 있다.
                    // 권한 관리를 지정하는 옵션, URL, HTTP 메소드별로 관리가 가능, "/"등 지정된 URL은 permitAll() 옵션을 통해 전체 열람 권한을 주고,
                    // /api/v1/** 주소를 가진 API는 USER 권한을 가진 사람만 가능
                    .antMatchers("/", "/css/**", "/images/**", "/js/**", "/h2-console/**").permitAll()
                    .antMatchers("/api/v1/**").hasRole(Role.USER.name())
                    .anyRequest().authenticated() // 설정된 값들 이외의 URL은 인증된(로그인된) 사용자들만 허용
                .and()
                    .logout()
                        .logoutSuccessUrl("/") // 로그 아웃시 "/" 주소로 이동
                .and()
                    .oauth2Login()
                        .userInfoEndpoint() // OAuth2 로그인 성공 이후 사용자 정보를 가져올 떄의 설정들을 담당
                                            .userService(customOAuth2UserService); // 소셜 로그인 성공 시 후속 조치를 진행할 UserService 인터페이스의 구현체를 작성, 추가로 하고자하는 기능 명시 가능
    }
}
