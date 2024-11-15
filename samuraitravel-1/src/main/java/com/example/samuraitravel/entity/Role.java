package com.example.samuraitravel.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

	// アノテーション（＠から始まる短いコードのこと。英語訳は注釈）
	@Entity	// このクラスがエンティティとして機能するようになる
	@Table(name = "roles")	// マッピング（対応づけ）テーブル名を指定する
	@Data	// ゲッターやセッターなどを自動生成できる

public class Role {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")	// フィールドにマッピングされるカラム名を指定
	private Integer id;
	
	@Column(name = "name")	// フィールドにマッピングされるカラム名を指定
	private String name;
}
