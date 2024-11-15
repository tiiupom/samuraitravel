package com.example.samuraitravel.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.repository.UserRepository;

@Service	// クラスがサービスクラスとして機能する
// implementsキーワードを使用しUserDetailsServiceインターフェースを実装
		//　UserRepositoryインターフェースを利用するため、依存性の注入(DI)を行う
		// 　あるクラス(A)が他のクラス(B)のオブジェクトを利用している場合、「AはBに依存している」という
public class UserDetailsServiceImpl implements UserDetailsService {
	private final UserRepository userRepository;
	//　コンストラクタインジェクション　finalで宣言すると一度初期化されたあと変更されないため安全性が向上する
	// コンストラクタに「@Autowired」アノテーションをつけるが、今回のように１つしかない場合は省略できる

	public UserDetailsServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		try {
				User user = userRepository.findByEmail(email);			// フォームから送信されたメールアドレスに一致するユーザーを取得
				String userRoleName = user.getRole().getName();		// そのユーザーのロールを取得
				Collection<GrantedAuthority> authorities = new ArrayList<>();
				authorities.add(new SimpleGrantedAuthority(userRoleName));
				return new UserDetailsImpl(user, authorities);			// メールアドレスに一致したユーザー、そのロールの情報をUserDetailsimplクラスのコンストラクタに渡してインスタンス作成
			} catch (Exception e) {
				throw new UsernameNotFoundException("ユーザーが見つかりませんでした。");
			}
	}
}
