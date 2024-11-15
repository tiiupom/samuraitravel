package com.example.samuraitravel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraitravel.entity.VerificationToken;

public interface VerificationTokenRepository  extends JpaRepository< VerificationToken, Integer>{
	// findByToken = トークンに一致するデータをverification_tokensテーブルから探すメソッド
	public VerificationToken findByToken(String token);
}
