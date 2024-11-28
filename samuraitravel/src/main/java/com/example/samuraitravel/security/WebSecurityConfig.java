package com.example.samuraitravel.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration				// 設定用のクラスとして機能するようになる
@EnableWebSecurity		// Spring Securityによるセキュリティ機能を有効にし、認証・認可のルールやログイン・アウト処理など各種設定を行えるようになる
@EnableMethodSecurity	// メソッドレベルでのセキュリティ機能を有効にする
// アプリ起動時にそのメソッドの戻り値（インスタンス）がDIコンテナに登録される
// DIコンテナ　：　DI（依存性の注入）用のオブジェクトを入れておく領域のこと
public class WebSecurityConfig {
	@Bean("customSecurityFilterChain")
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests((requests) -> requests
				// すべてのユーザーにアクセスを許可するURL
				// 旧コード： .requestMatchers("/css/**", "/images/**", "/js/**","/storage/**", "/").permitAll()
				.requestMatchers("/css/**", "/images/**", "/js/**", "/storage/**", "/", "/signup/**").permitAll()
				.requestMatchers("/admin/**").hasRole("ADMIN")  // 管理者にのみアクセスを許可するURL
				.anyRequest().authenticated()	// 上記外のURLはログインが必要（会員または管理者ならOK）
			)
			.formLogin((form) -> form
					.loginPage("/login")							// ログインページのURL
					.loginProcessingUrl("/login")				// ログインフォームの送信先URL
					.defaultSuccessUrl("/?loggedIn")		// ログイン成功時のリダイレクト先URL
					.failureUrl("/login?error")					// ログイン失敗時のリダイレクト先URL
					.permitAll()
			)
			.logout((logout) -> logout
					.logoutSuccessUrl("/?loggedOut")
					.permitAll()
			);

			return http.build();
	}

	/* このメソッドで「BCryptPasswordEncoder」クラスのインスタンスを返すことで、
	パスワードのハッシュアルゴリズム（ハッシュ化のルール）を「BCrypt」に設定している　*/

	@Bean("customPasswordEncoder")
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
