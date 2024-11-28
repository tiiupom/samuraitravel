package com.example.samuraitravel.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.form.UserEditForm;
import com.example.samuraitravel.security.UserDetailsImpl;
import com.example.samuraitravel.service.UserService;

/* @AuthenticationPrincipal : 現在ログイン中のユーザー情報を取得（SpringSecurityが提供）
 * 		引数はUserDetailsインターフェースを実装したクラスのオブジェクト（UserDetailsImplクラス）
 * 		UserDetailsImplクラスはユーザー情報を保持する役割を持つ
 * 		getUser()メソッドを定義したので、それを使用しログイン中のユーザー（エンティティであるUserクラスのオブジェクト）を取得できる */

@Controller
@RequestMapping("/user")
public class UserController {
	private final UserService userService;
	 
    public UserController(UserService userService) {
        this.userService = userService;
    } 
	
    @GetMapping
    public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
        User user = userDetailsImpl.getUser();

        model.addAttribute("user", user);

        return "user/index";
    }
    
    @GetMapping("/edit")
    public String edit(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
        User user = userDetailsImpl.getUser();
        UserEditForm userEditForm = new UserEditForm(user.getName(), user.getFurigana(), user.getPostalCode(), user.getAddress(), user.getPhoneNumber(), user.getEmail());

        model.addAttribute("userEditForm", userEditForm);

        return "user/edit";
    }
    
    // フォームの入力内容をPOSTメソッドで送信するためPostMappingアノテーションをつける
    // ModelAttribute　フォームから送信されたデータを引数にバインド（割り当てる）
    // Validated　引数（フォームクラスのインスタンス）に対しバリデーション（入力のチェック）を行う
    /* BindResult　バリデーション結果を保持するインターフェースでエラー内容が引数に格納される
    			  62行目でオブジェクト生成 */
    /* RedirectAttribute　リダイレクト先にデータを渡すインターフェース
       まずupdateメソッドに引数を設定し、92行目addFlashAttributeメソッドを使用しリダイレクト先にデータを渡せる*/
    @PostMapping("/update")
    public String update(@ModelAttribute @Validated UserEditForm userEditForm,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                         RedirectAttributes redirectAttributes,
                         Model model)
    {
        User user = userDetailsImpl.getUser();
    	
    	// メールアドレスが変更済、かつ登録済ならBindingResultオブジェクトにエラー内容を追加
    	/* FieldErrorクラスのインスタンスを作成しaddErrorに渡す
    	 * 第１引数：エラー内容を格納するオブジェクト名
    	 * 第２引数：エラーを発生させるフィールド名
    	 * 第３引数：エラーメッセージ */
		// FirldErrorオブジェクトを渡したaddErrorメソッドにエラー内容を渡しBindingRedultオブジェクトにエラー内容を追加
        if (userService.isEmailChanged(userEditForm, user) && userService.isEmailRegistered(userEditForm.getEmail())) {
            FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "すでに登録済みのメールアドレスです。");
            bindingResult.addError(fieldError);
        }
    	
    	/* bindingResultインターフェースにエラーが存在(hasErrors)する場合は
    	 * userEditFormに属性（エラー内容）を追加してedit(会員情報編集画面)を表示 */
        if (bindingResult.hasErrors()) {
            model.addAttribute("userEditForm", userEditForm);

            return "user/edit";
        }
    	
        userService.updateUser(userEditForm, user);
        redirectAttributes.addFlashAttribute("successMessage", "会員情報を編集しました。");
    	
    	// リダイレクト先をreturnで返す（ "reidrect:ルートパス" )
        return "redirect:/user";
    }
}
