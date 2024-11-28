package com.example.samuraitravel.controller;

 import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

 /* SpringBootTest　：　テスト時にアプリのコンテキストを軌道
  * AutoConfigreMockMvc　：　MockMvcインスタンスを注入できるようになる
  * フィールドに対し直接依存性の注入(DI)を行う手法をフィールとインジェクションという
  * 		コードを簡潔に書けるメリットがある
  * ActiveProfiles　：　テスト時に異なる設定ファイルを適用したい場合につける */
 @SpringBootTest
 @AutoConfigureMockMvc
 @ActiveProfiles("test")
public class HouseControllerTest {
	// 使いたいクラスをインスタンス化する
     @Autowired
     private MockMvc mockMvc;
 
    // Test　：　メソッドがテストメソッドとして認識され、テスト時に実行されるようになる
 	// perform()　：　MockMvcクラスのメソッド。HTTPリクエストを送信する振る舞いを実現する
 	// andExpect()　：　MockMvcクラスのメソッド。HTTPレスポンスが期待する内容か検証する
     @Test
     public void 未ログインの場合は会員用の民宿一覧ページが正しく表示される() throws Exception {
         mockMvc.perform(get("/houses"))
                .andExpect(status().isOk())
                .andExpect(view().name("houses/index"));
     }
 
     // WithUserDetails　：　引数にログイン時に利用するユーザー名を指定し、そのユーザーとしてログインする振る舞いを実現する
     @Test
     @WithUserDetails("taro.samurai@example.com")
     public void ログイン済みの場合は会員用の民宿一覧ページが正しく表示される() throws Exception {
         mockMvc.perform(get("/houses"))
                .andExpect(status().isOk())
                .andExpect(view().name("houses/index"));
     }
}