package com.example.samuraitravel.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.example.samuraitravel.entity.User;

@Component	// DIコンテナに登録
	// Publisherクラス　：　イベントを発生させたい処理の中で呼び出して使う
	// 処理＝（AuthControllerクラスのsignupメソッド）
public class SignupEventPublisher {
	private final ApplicationEventPublisher applicationEventPublisher;
	
	public SignupEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}
	
	// イベントを発生させるメソッド。引数に発行したいEventクラスのインスタンスを渡す
	public void publishSignupEvent(User user, String requestUrl) {
		applicationEventPublisher.publishEvent(new SignupEvent(this, user, requestUrl));
	}
}
