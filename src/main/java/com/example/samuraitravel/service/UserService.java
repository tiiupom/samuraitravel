package com.example.samuraitravel.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.samuraitravel.entity.Role;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.form.SignupForm;
import com.example.samuraitravel.form.UserEditForm;
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
        // Userクラスをインスタンス化
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
	
	@Transactional
    public void updateUser(UserEditForm userEditForm, User user) {
        user.setName(userEditForm.getName());
        user.setFurigana(userEditForm.getFurigana());
        user.setPostalCode(userEditForm.getPostalCode());
        user.setAddress(userEditForm.getAddress());
        user.setPhoneNumber(userEditForm.getPhoneNumber());
        user.setEmail(userEditForm.getEmail());

        userRepository.save(user);
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
    
    // メールアドレスが変更されたかどうかをチェックする
    public boolean isEmailChanged(UserEditForm userEditForm, User user) {
        return !userEditForm.getEmail().equals(user.getEmail());
    }
    
    // 指定したメールアドレスを持つユーザーを取得
    public User findUserByEmail(String email) {
    	return userRepository.findByEmail(email);
    }
    
    // すべてのユーザーをページングされた状態で取得
    public Page<User> findAllUsers(Pageable pageable) {
    	return userRepository.findAll(pageable);
    }
    
    // 指定されたキーワードを氏名orフリガナに含むユーザーをページングされた状態で取得
    public Page<User> findUserByNameLikeOrFuriganaLike(String nameKeyword, String furiganaKeyword, Pageable pageable) {
    	return userRepository.findByNameLikeOrFuriganaLike("%" + nameKeyword + "%", "%" + furiganaKeyword + "%" , pageable);
    }
    
    // 指定したidを持つユーザーを取得
    // Optional　：　指定されたマッピング関数を値に適用し、nullでなければUserの値を返す
    // findById()メソッド　：　指定したidを持つエンティティを取得する
    public Optional<User> findUserById(Integer id) {
    	return userRepository.findById(id);
    }
}
