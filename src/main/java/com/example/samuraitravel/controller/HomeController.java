package com.example.samuraitravel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// コントローラ　：　モデルとビューを制御する役割
@Controller		// クラスがコントローラとして機能する
public class HomeController {
	/* HTTPリクエストのGETメソッドをそのメソッドにマッピングできる。
	引数にはマッピングするルートパス（ドメイン名を省略したパス）を指定 */
		@GetMapping("/") 
		public String index() {
			/* リポジトリで作成した民宿を新着順に10件取得するメソッドに対応させる
			 * リスト型Houseのオブジェクト名newHousesにhouseServiceに格納したデータを代入
			 	modelに値が代入されたnewHousesを渡してビューに表示されるようにする*/
		
			return "index";
		}
}

/* HTTPリクエストメソッド　クライアントがサーバーに行ってほしい操作を伝える目的で使用
 *		GET　単純にページを表示する場合など、サーバーから情報を取得するために使う
 *		POST　フォームの入力内容を送信してデータの作成や行進を行う場合などサーバー上のデータを変更するために使う
 *
 * 	HTTP通信　：　インターネットを利用しWebページやデータを送受信するためのプロトコル（通信規約）
 * 						 クライアント（ブラウザ）とサーバー（Webサイトやアプリをホストするコンピュータ）の間でやり取りされる
 */