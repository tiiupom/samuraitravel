package com.example.samuraitravel.event;

import java.util.UUID;

import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.service.VerificationTokenService;

@Component	/* ListenerクラスのインスタンスがDIコンテナに登録されるようにする
				これにより@EventListenerアノテーションのメソッドを、
				Spring Boot側が自動的に検出しイベント発生時に実行する*/
public class SignupEventListener {
	private final VerificationTokenService verificationTokenService;
	private final JavaMailSender javaMailSender;
	
	public SignupEventListener(VerificationTokenService verificationTokenService, JavaMailSender mailSender) {
		this.verificationTokenService = verificationTokenService;
		this.javaMailSender = mailSender;
	}
	
	@EventListener	//　イベント発生時に実行したいメソッドに対し付ける
		// Eventクラス　通知を受け付けるクラスを引数に設定し、どのイベントの発生時か指定
	private void onSignupEvent(SignupEvent signupEvent) {
		User user = signupEvent.getUser();
		// トークンはUUIDで生成し、ユーザーIDと共にデータベースに保存
		// UUID：Universally Unique IDentifer(ﾕﾆﾊﾞｰｻﾘｰ･ﾕﾆｰｸ･ｱｲﾃﾞﾝﾃｨﾌｧｲｱ)
		String token = UUID.randomUUID().toString();
		verificationTokenService.create(user, token);
		
		String senderAddress = "springboot.samuraitravel@example.com";
		String recipientAddress = user.getEmail();
		String subject = "メール認証";
		/* 生成したトークンをメール認証用のURLにパラーメータとして埋め込み
			メッセージ内に記載することでアクセス時データベースの値と一致するか確認できる　*/
		String confirmationUrl = signupEvent.getRequestUrl() + "/verifi?token=" + token;
		String message = "以下のリンクをクリックして会員登録を完了してください。";
		
		// SimpleMailMessage　：　シンプルなメールメッセージをオブジェクトとして生成できる
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setFrom(senderAddress);	// 送信元のメールアドレスをセット
		mailMessage.setTo(recipientAddress);	// 送信先のメールアドレスをセット
		mailMessage.setSubject(subject);	// 件名をセット
		mailMessage.setText(message + "\n" + confirmationUrl); // 本文をセット
		// メールの送信処理を実行　sendメソッドにオブジェクトを渡す
		javaMailSender.send(mailMessage);
	}
}
