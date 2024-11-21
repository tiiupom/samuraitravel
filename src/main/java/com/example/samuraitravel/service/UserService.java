package com.example.samuraitravel.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.samuraitravel.entity.Role;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.form.SignupForm;
import com.example.samuraitravel.repository.RoleRepository;
import com.example.samuraitravel.repository.UserRepository;

@Service	// クラスがサービスクラスとして機能する
public class UserService {
	private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

	@Transactional	// メソッドをトランザクション化する（データベースの操作をひとまとまりにしたもの）
		/*　正常に終了　->　トランザクションがコミット(確定)されデータベースの変更を保存
		 * 例外が発生　->　トランザクションがロールバック(取り消し)されデータベースの変更を破棄 */
	//　createUserメソッド内ではまずエンティティ(Userクラス)をインスタンス化する
	public User createUser(SignupForm signupForm) {
        User user = new User();
		// roleフィールドにRoleオブジェクトをセット
        Role role = roleRepository.findByName("ROLE_GENERAL");

        user.setName(signupForm.getName());
        user.setFurigana(signupForm.getFurigana());
        user.setPostalCode(signupForm.getPostalCode());
        user.setAddress(signupForm.getAddress());
        user.setPhoneNumber(signupForm.getPhoneNumber());
        user.setEmail(signupForm.getEmail());
		// パスワードをハッシュ化してセット（Encodeメソッド使用）
        user.setPassword(passwordEncoder.encode(signupForm.getPassword()));
        user.setRole(role);
		// メール認証済みか判定に利用するenabledフィールドにtrueをセット
        //user.setEnabled(true);
        user.setEnabled(false);

		// saveメソッドを使用しエンティティをデータベースに保存
        return userRepository.save(user);
	}

	// メールアドレスが登録済みかどうかをチェックする
	public boolean isEmailRegistered(String email) {
		// メールアドレスでユーザーを検索（findByEmail()）
		// 登録済みであればエンティティのオブジェクトを代入、なければnullを代入
		User user = userRepository.findByEmail(email);
        return user != null;
    }

	// パスワードとパスワード(確認用)の入力値が一致するかチェック
	public boolean isSamePassword(String password, String passwordConfirmation) {
		// equalsメソッドを使用し文字列同士を比較し、一致すればtrue、しなければfalseが返される
		return password.equals(passwordConfirmation);
    }
    
    // ユーザーを有効にする
    @Transactional
    public void enableUser(User user) {
    	user.setEnabled(true);
    	userRepository.save(user);
    }
}
