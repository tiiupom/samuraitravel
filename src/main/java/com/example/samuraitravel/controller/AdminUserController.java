package com.example.samuraitravel.controller;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.service.UserService;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {
	private final UserService userService;
	
	public AdminUserController(UserService userService) {
		this.userService = userService;
	}
	
	/* @RequestParam : ブラウザからのリクエストの値（パラメータ）を取得することができる
	 * required : リクエストパラメータが必須かどうか(デフォルトはtrue)
	 * @PageableDefault : Pageable をコントローラーメソッドに注入するときにデフォルトを設定する
	 */
	@GetMapping
	public String index(@RequestParam(name = "keyword", required = false) String keyword,
						@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
						Model model)
	{
		Page<User> userPage;
		
		// キーワードが空白でない且つキーワードが初期化されていないか
		if (keyword != null && !keyword.isEmpty()) {
			// falseなら
			userPage = userService.findUserByNameLikeOrFuriganaLike(keyword, keyword, pageable);
		} else {
			// trueなら
			userPage = userService.findAllUsers(pageable);
		}
		
		// 
		model.addAttribute("userPage", userPage);
		model.addAttribute("keyword", keyword);
		
		return "admin/users/index";
	}
	
	// アクセスしたページの「id」に当たる値がshow()メソッドの引数idにバインドされる
	// Pathvariable : name属性にバインドさせたいURLの{}内の文字列（id）を指定
	// Optional　：　指定されたマッピング関数を値に適用し、nullでなければUserの値を返す
	@GetMapping("/{id}")
	public String show(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, Model model) {
		Optional<User> optionalUser = userService.findUserById(id);
		
		// ↑でインスタンス生成したOptionalクラスのisEmpty()メソッドを使用
		// 中身が空であればtrue、空でなければfalseを返す
		if (optionalUser.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "ユーザーが存在しません。");

            return "redirect:/admin/users";
        }
		
		/* Optional型をUser型に変換
		 * Optinalクラスのgetメソッドを使用し、User型に変換してからビューに渡す */
		User user = optionalUser.get();
		model.addAttribute("user", user);
		
		return "admin/users/show";
	}
	
	
}
