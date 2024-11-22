package com.example.samuraitravel.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.service.HouseService;

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
	
	@Autowired
    private HouseService houseService;
	
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
    
    @Test
    @Transactional
    public void 未ログインの場合は民宿を登録せずにログインページにリダイレクトする() throws Exception {
        // テスト前のレコード数を取得する
        long countBefore = houseService.countHouses();

        // テスト用の画像ファイルデータを準備する
        Path filePath = Paths.get("src/main/resources/static/storage/house01.jpg");
        String fileName = filePath.getFileName().toString();
        String fileType = Files.probeContentType(filePath);
        byte[] fileBytes = Files.readAllBytes(filePath);

        MockMultipartFile imageFile = new MockMultipartFile(
            "imageFile",  // フォームのname属性の値
            fileName,     // ファイル名
            fileType,     // ファイルの形式
            fileBytes     // ファイルのバイト配列
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/admin/houses/create").file(imageFile)
                .with(csrf())
                .param("name", "テスト民宿名")
                .param("description", "テスト説明")
                .param("price", "5000")
                .param("capacity", "5")
                .param("postalCode", "000-0000")
                .param("address", "テスト住所")
                .param("phoneNumber", "000-000-000"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("http://localhost/login"));

        // テスト後のレコード数を取得する
        long countAfter = houseService.countHouses();

        // レコード数が変わっていないことを検証する
        assertThat(countAfter).isEqualTo(countBefore);
    }

    @Test
    @WithUserDetails("taro.samurai@example.com")
    @Transactional
    public void 一般ユーザーとしてログイン済みの場合は民宿を登録せずに403エラーが発生する() throws Exception {
        // テスト前のレコード数を取得する
        long countBefore = houseService.countHouses();

        // テスト用の画像ファイルデータを準備する
        Path filePath = Paths.get("src/main/resources/static/storage/house01.jpg");
        String fileName = filePath.getFileName().toString();
        String fileType = Files.probeContentType(filePath);
        byte[] fileBytes = Files.readAllBytes(filePath);

        MockMultipartFile imageFile = new MockMultipartFile(
            "imageFile",  // フォームのname属性の値
            fileName,     // ファイル名
            fileType,     // ファイルの形式
            fileBytes     // ファイルのバイト配列
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/admin/houses/create").file(imageFile)
                .with(csrf())
                .param("name", "テスト民宿名")
                .param("description", "テスト説明")
                .param("price", "5000")
                .param("capacity", "5")
                .param("postalCode", "000-0000")
                .param("address", "テスト住所")
                .param("phoneNumber", "000-000-000"))
            .andExpect(status().isForbidden());

        // テスト後のレコード数を取得する
        long countAfter = houseService.countHouses();

        // レコード数が変わっていないことを検証する
        assertThat(countAfter).isEqualTo(countBefore);
    }

    @Test
    @WithUserDetails("hanako.samurai@example.com")
    @Transactional
    public void 管理者としてログイン済みの場合は民宿登録後に民宿一覧ページにリダイレクトする() throws Exception {
        // テスト前のレコード数を取得する
        long countBefore = houseService.countHouses();

        // テスト用の画像ファイルデータを準備する
        Path filePath = Paths.get("src/main/resources/static/storage/house01.jpg");
        String fileName = filePath.getFileName().toString();
        String fileType = Files.probeContentType(filePath);
        byte[] fileBytes = Files.readAllBytes(filePath);

        MockMultipartFile imageFile = new MockMultipartFile(
            "imageFile",  // フォームのname属性の値
            fileName,     // ファイル名
            fileType,     // ファイルの形式
            fileBytes     // ファイルのバイト配列
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/admin/houses/create").file(imageFile)
                .with(csrf())
                .param("name", "テスト民宿名")
                .param("description", "テスト説明")
                .param("price", "5000")
                .param("capacity", "5")
                .param("postalCode", "000-0000")
                .param("address", "テスト住所")
                .param("phoneNumber", "000-000-000"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/houses"));

        // テスト後のレコード数を取得する
        long countAfter = houseService.countHouses();

        // レコード数が1つ増加していることを検証する
        assertThat(countAfter).isEqualTo(countBefore + 1);

        House house = houseService.findFirstHouseByOrderByIdDesc();
        assertThat(house.getName()).isEqualTo("テスト民宿名");
        assertThat(house.getDescription()).isEqualTo("テスト説明");
        assertThat(house.getPrice()).isEqualTo(5000);
        assertThat(house.getCapacity()).isEqualTo(5);
        assertThat(house.getPostalCode()).isEqualTo("000-0000");
        assertThat(house.getAddress()).isEqualTo("テスト住所");
        assertThat(house.getPhoneNumber()).isEqualTo("000-000-000");
    }
    
    @Test
    public void 未ログインの場合は管理者用の民宿編集ページからログインページにリダイレクトする() throws Exception {
        mockMvc.perform(get("/admin/houses/1/edit"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithUserDetails("taro.samurai@example.com")
    public void 一般ユーザーとしてログイン済みの場合は管理者用の民宿編集ページが表示されずに403エラーが発生する() throws Exception {
        mockMvc.perform(get("/admin/houses/1/edit"))
               .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("hanako.samurai@example.com")
    public void 管理者としてログイン済みの場合は管理者用の民宿編集ページが正しく表示される() throws Exception {
        mockMvc.perform(get("/admin/houses/1/edit"))
               .andExpect(status().isOk())
               .andExpect(view().name("admin/houses/edit"));
    }
    
    @Test
    @Transactional
    public void 未ログインの場合は民宿を更新せずにログインページにリダイレクトする() throws Exception {
    	// テスト用の画像ファイルデータを準備する
    	Path filePath = Paths.get("src/main/resources/static/storage/house01.jpg");
    	String fileName = filePath.getFileName().toString();
    	String fileType = Files.probeContentType(filePath);
    	byte[] fileBytes = Files.readAllBytes(filePath);
    	
    	MockMultipartFile imageFile = new MockMultipartFile(
    			"imageFile", // フォームのname属性の値
    			fileName,	 // ファイル名
    			fileType,	 // ファイルの形式
    			fileBytes	 // ファイルのバイト配列
    		);
    	
    	mockMvc.perform(MockMvcRequestBuilders.multipart("/admin/houses/1/update").file(imageFile)
    			.with(csrf())
    			.param("name", "テスト民宿名")
    			.param("description", "テスト説明")
    			.param("price", "5000")
    			.param("capacity", "5")
    			.param("postalCode", "000-0000")
    			.param("address", "テスト住所")
    			.param("phoneNumber", "000-000-000"))
    		.andExpect(status().is3xxRedirection())
    		.andExpect(redirectedUrl("http://localhost/login"));
    	
    	/* サービスクラスに定義したfindHouseById()メソッドを使い、idが1の民宿データ
    	 * （Optional<House>型のオブジェクト）を取得 
    	 * Optional : nullを持つ可能性のあるオブジェクトをより安全かつ便利に扱うクラス
    	 * isPresent()メソッドを使いOptional<T>型（Tは任意の型）のオブジェクトが存在するかどうかを検証
    	 * assertThat()メソッドを使い「データベースのレコードを正しく更新できている、またはできていないこと」を検証*/
    	Optional<House> optionalHouse = houseService.findHouseById(1);
    	assertThat(optionalHouse).isPresent();
    	House house = optionalHouse.get();
    	assertThat(house.getName()).isEqualTo("SAMURAIの宿");
    	assertThat(house.getDescription()).isEqualTo("最寄り駅から徒歩10分。自然豊かで閑静な場所にあります。長期滞在も可能です。");
    	assertThat(house.getPrice()).isEqualTo(6000);
    	assertThat(house.getCapacity()).isEqualTo(2);
    	assertThat(house.getPostalCode()).isEqualTo("073-0145");
    	assertThat(house.getAddress()).isEqualTo("北海道砂川市西五条南X-XX-XX");
    	assertThat(house.getPhoneNumber()).isEqualTo("012-345-678");
    }
    
    @Test
    @WithUserDetails("taro.samurai@example.com")
    @Transactional
    public void 一般ユーザーとしてログイン済みの場合は民宿を更新せずに403エラーが発生する() throws Exception {
    	// テスト用の画像ファイルデータを準備する
    	Path filePath = Paths.get("src/main/resources/static/storage/house01.jpg");
    	String fileName = filePath.getFileName().toString();
    	String fileType = Files.probeContentType(filePath);
    	byte[] fileBytes = Files.readAllBytes(filePath);
    	
    	MockMultipartFile imageFile = new MockMultipartFile(
    			"imageFile", // フォームのname属性の値
    			fileName,	 // ファイル名
    			fileType,	 // ファイルの形式
    			fileBytes	 // ファイルのバイト配列
    		);
    	
    	mockMvc.perform(MockMvcRequestBuilders.multipart("/admin/houses/1/update").file(imageFile)
    			.with(csrf())
    			.param("name", "テスト民宿名")
    			.param("description", "テスト説明")
    			.param("price", "5000")
    			.param("capacity", "5")
    			.param("postalCode", "000-0000")
    			.param("address", "テスト住所")
    			.param("phoneNumber", "000-000-000"))
    		.andExpect(status().isForbidden());
    	
    	/* サービスクラスに定義したfindHouseById()メソッドを使い、idが1の民宿データ
    	 * （Optional<House>型のオブジェクト）を取得 
    	 * Optional : nullを持つ可能性のあるオブジェクトをより安全かつ便利に扱うクラス
    	 * isPresent()メソッドを使いOptional<T>型（Tは任意の型）のオブジェクトが存在するかどうかを検証
    	 * assertThat()メソッドを使い「データベースのレコードを正しく更新できている、またはできていないこと」を検証*/
    	Optional<House> optionalHouse = houseService.findHouseById(1);
    	assertThat(optionalHouse).isPresent();
    	House house = optionalHouse.get();
    	assertThat(house.getName()).isEqualTo("SAMURAIの宿");
    	assertThat(house.getDescription()).isEqualTo("最寄り駅から徒歩10分。自然豊かで閑静な場所にあります。長期滞在も可能です。");
    	assertThat(house.getPrice()).isEqualTo(6000);
    	assertThat(house.getCapacity()).isEqualTo(2);
    	assertThat(house.getPostalCode()).isEqualTo("073-0145");
    	assertThat(house.getAddress()).isEqualTo("北海道砂川市西五条南X-XX-XX");
    	assertThat(house.getPhoneNumber()).isEqualTo("012-345-678");
    }
    
    @Test
    @WithUserDetails("hanako.samurai@example.com")
    @Transactional
    public void 管理者としてログイン済みの場合は民宿更新後に民宿詳細ページにリダイレクトする() throws Exception {
    	// テスト用の画像ファイルデータを準備する
    	Path filePath = Paths.get("src/main/resources/static/storage/house01.jpg");
    	String fileName = filePath.getFileName().toString();
    	String fileType = Files.probeContentType(filePath);
    	byte[] fileBytes = Files.readAllBytes(filePath);
    	
    	MockMultipartFile imageFile = new MockMultipartFile(
    			"imageFile", // フォームのname属性の値
    			fileName,	 // ファイル名
    			fileType,	 // ファイルの形式
    			fileBytes	 // ファイルのバイト配列
    		);
       
    	mockMvc.perform(MockMvcRequestBuilders.multipart("/admin/houses/1/update").file(imageFile)
    			.with(csrf())
    			.param("name", "テスト民宿名")
    			.param("description", "テスト説明")
    			.param("price", "5000")
    			.param("capacity", "5")
    			.param("postalCode", "000-0000")
    			.param("address", "テスト住所")
				.param("phoneNumber", "000-000-000"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin/houses"));
    	
    	/* サービスクラスに定義したfindHouseById()メソッドを使い、idが1の民宿データ
    	 * （Optional<House>型のオブジェクト）を取得 
    	 * Optional : nullを持つ可能性のあるオブジェクトをより安全かつ便利に扱うクラス
    	 * isPresent()メソッドを使いOptional<T>型（Tは任意の型）のオブジェクトが存在するかどうかを検証
    	 * assertThat()メソッドを使い「データベースのレコードを正しく更新できている、またはできていないこと」を検証*/
    	Optional<House> optionalHouse = houseService.findHouseById(1);
    	assertThat(optionalHouse).isPresent();
    	House house = optionalHouse.get();
    	assertThat(house.getName()).isEqualTo("テスト民宿名");
    	assertThat(house.getDescription()).isEqualTo("テスト説明");
    	assertThat(house.getPrice()).isEqualTo(5000);
    	assertThat(house.getCapacity()).isEqualTo(5);
    	assertThat(house.getPostalCode()).isEqualTo("000-0000");
    	assertThat(house.getAddress()).isEqualTo("テスト住所");
    	assertThat(house.getPhoneNumber()).isEqualTo("000-000-000");
    }
}