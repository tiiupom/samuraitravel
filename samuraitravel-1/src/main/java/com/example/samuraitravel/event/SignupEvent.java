package com.example.samuraitravel.event;

import org.springframework.context.ApplicationEvent;

import com.example.samuraitravel.entity.User;

import lombok.Getter;

// 外部（Listenerクラス）から情報を取得できるようゲッターを定義する
@Getter
	// EventクラスはApplicationEventクラスを継承して作成するのが一般的
	// ApplicationEventクラス：イベントを作成するための基本的なクラス。イベントのソース（発生源）などを保持する
public class SignupEvent extends ApplicationEvent {
	private User user;
	private String requestUrl;
	
	public SignupEvent(Object source, User user, String requestUrl) {
		super(source);	// 親クラスのコンストラクタ（Publisherクラスのインスタンス）呼び出し
		
		this.user = user;
		this.requestUrl = requestUrl;
	}
}
