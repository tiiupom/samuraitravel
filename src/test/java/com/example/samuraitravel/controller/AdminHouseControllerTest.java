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

/*　SpringBootTest　：　テスト時にアプリのコンテキストを起動
 * 		コンテキスト…アプリが実際に実行される時と同様に設定や依存関係などが利用可能な環境のこと
 * 		直訳で「文脈」「背景」「状況」の意
 * 		コントローラのテストのようにHTTPリクエストやHTTPレスポンスの結果などを検証したい場合に便利 
 * 	ActiveProfiles　：　テスト時に異なる設定ファイルを適用したい場合に使用	
 * 					　引数には設定ファイルの「application-●●●.priperties」の●●●を指定	*/

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AdminHouseControllerTest {
	/* MockMvc　：　Spring Bootの機能の1つ。MVCの動作を模倣することで
	 * 			　HTTPリクエストやHTTPレスポンスの結果を検証できる
	 * 			　この機能を使う際はMockMvcクラスのインスタンスを注入する必要がある 
	 * 	依存性の注入（DI)方法　：　コンストラクタインジェクション（コンストラクタを使いDIを行う
	 * 						　フィールドインジェクション（フィールドに対し直接DIを行う	*/
	@Autowired
	private MockMvc mockMvc;
	
	@Test
	public void 未ログインの場合は管理者用の民宿一覧ページからログインページにリダイレクトする() throws Exception {
		mockMvc.perform(get("/admin/house"))
			   .andExpect(status().is3xxRedirection())
			   .andExpect(redirectedUrl("http://localhost/login"));
	}
	
	/*	WithUserDetails　：　この引数にログイン時に利用するユーザー名を指定すると
	 * 					　　「そのユーザーとしてログインする」振る舞いを実現できる。
	 * 	※MockMvcクラスのPerform()メソッドを使用することでHTTPリクエストを送信
	 * 	　引数にはHTTPリクエストメソッドと送信先のルートパスを指定	
	 * 	※MockMvcクラスのandExpect()メソッドを使用することでHTTPレスポンスが期待する内容か検証
	 * 	　引数には期待する内容を指定し、全て満たしていればテストメソッドは成功となる */
	@Test
	@WithUserDetails("taro.samurai@example.com")
	public void 一般ユーザーとしてログイン済みの場合は管理者用の民宿一覧ページが表示されずに403エラーが発生する() throws Exception {
		mockMvc.perform(get("/admin/houses"))
			   .andExpect(status().isForbidden());
	}
	
	@Test
	@WithUserDetails("hanako.samurai@example.com")
	public void 管理者としてログイン済みの場合は管理者用の民宿一覧ページが正しく表示される() throws Exception {
		mockMvc.perform(get("/admin/houses"))
			   .andExpect(status().isOk())
			   .andExpect(view().name("admin/houses/index"));
	}
	
	@Test
    public void 未ログインの場合は管理者用の民宿詳細ページからログインページにリダイレクトする() throws Exception {
        mockMvc.perform(get("/admin/houses/1"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithUserDetails("taro.samurai@example.com")
    public void 一般ユーザーとしてログイン済みの場合は管理者用の民宿詳細ページが表示されずに403エラーが発生する() throws Exception {
        mockMvc.perform(get("/admin/houses/1"))
               .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("hanako.samurai@example.com")
    public void 管理者としてログイン済みの場合は管理者用の民宿詳細ページが正しく表示される() throws Exception {
        mockMvc.perform(get("/admin/houses/1"))
               .andExpect(status().isOk())
               .andExpect(view().name("admin/houses/show"));
    }
    
    @Test
    public void 未ログインの場合は管理者用の民宿登録ページからログインページにリダイレクトする() throws Exception {
        mockMvc.perform(get("/admin/houses/register"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithUserDetails("taro.samurai@example.com")
    public void 一般ユーザーとしてログイン済みの場合は管理者用の民宿登録ページが表示されずに403エラーが発生する() throws Exception {
        mockMvc.perform(get("/admin/houses/register"))
               .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("hanako.samurai@example.com")
    public void 管理者としてログイン済みの場合は管理者用の民宿登録ページが正しく表示される() throws Exception {
        mockMvc.perform(get("/admin/houses/register"))
               .andExpect(status().isOk())
               .andExpect(view().name("admin/houses/register"));
    }
}
