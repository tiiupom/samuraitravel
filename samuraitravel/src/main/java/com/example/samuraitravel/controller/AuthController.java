package com.example.samuraitravel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.entity.VerificationToken;
import com.example.samuraitravel.event.SignupEventPublisher;
import com.example.samuraitravel.form.SignupForm;
import com.example.samuraitravel.service.UserService;
import com.example.samuraitravel.service.VerificationTokenService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class AuthController {
	private final UserService userService;
	private final SignupEventPublisher signupEventPublisher;
	private final VerificationTokenService verificationTokenService;

	//public AuthController(UserService userService) {
	public AuthController(UserService userService, SignupEventPublisher signupEventPublisher, VerificationTokenService verificationTokenService) {
		this.userService = userService;
		this.signupEventPublisher = signupEventPublisher;
		this.verificationTokenService = verificationTokenService;
	}

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

     @GetMapping("/signup")
     public String signup(Model model) {
         model.addAttribute("signupForm", new SignupForm());
         return "auth/signup";
     }

     // フォームの入力内容をHTTPリクエストのPOSTメソッドで送信するため、送信先のメソッドにアノテーションをつける
     @PostMapping("/signup")
     /*　引数に「@ModelAttribute」アノテーションを付け、フォームから送信されたデータ（フォームクラスのインスタンス）をその引数にバインドする
      * 引数に「@Validated」アノテーションを付け、その引数（フォームクラスのインスタンス）に対しバリデーションを行う　*/
     public String signup(@ModelAttribute @Validated SignupForm signupForm,
    		 			  BindingResult bindingResult,
    		 			  RedirectAttributes redirectAttributes,
    		 			  // 動的にURLを取得する
    		 			  HttpServletRequest httpServletRequest,
    		 			  Model model)
     {
    	// メールアドレスが登録済みならBindingResultオブジェクトにエラー内容を追加
    	 if (userService.isEmailRegistered(signupForm.getEmail())) {
    		 FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "すでに登録済みのメールアドレスです");
    		 // adderrorメソッドにエラー内容を渡してBindingResultオブジェクトに独自のエラー内容を追加できる
    		 bindingResult.addError(fieldError);
    	 }

    	 // パスワードとパスワード（確認用）の入力値が一致しなければBindingResultオブジェクトにエラー内容を追加
    	 if (!userService.isSamePassword(signupForm.getPassword(), signupForm.getPasswordConfirmation())) {
    		 FieldError fieldError = new FieldError(bindingResult.getObjectName(), "password", "パスワードが一致しません。");
    		 bindingResult.addError(fieldError);
    	 }

    	 // BindingResultはバリデーションの結果を保持するインターフェース
    	 // hasErrors()メソッドで、エラーが存在するかチェック
    	 if (bindingResult.hasErrors()) {
    		 model.addAttribute("signpuForm", signupForm);

    		 return "auth/signup";
    	 }

    	 // createUserメソッドを実行しデータベースに会員情報を登録
    	 // 引数としてフォームクラスのインスタンスを渡す
    	 //userService.createUser(signupForm);
    	 /*　リダイレクト先にデータを渡す場合はまずメソッドにRedirectAttributes型の引数を設定
    	  * addFlashAttribute()メソッドでリダイレクト先にデータを渡せる
    	  * （リダイレクト先で取得されると自動的に削除さえるため、リダイレクト直後に1回限り利用するデータを渡す際に使用）
    	  * 第１引数：リダイレクト先から参照する変数名、第２引数：リダイレクト先に渡すデータ */
    	 //redirectAttributes.addFlashAttribute("successMessage", "会員登録が完了しました。");

    	 User createdUser = userService.createUser(signupForm);
    	 String requestUrl = new String(httpServletRequest.getRequestURL());
    	 // 会員登録が完了したタイミングでイベント発行できるメソッド
    	 // 引数に会員登録したユーザーのUserオブジェクト、String型のリクエストURLを渡す
    	 signupEventPublisher.publishSignupEvent(createdUser, requestUrl);
    	 redirectAttributes.addFlashAttribute("successMessagee", "ご入力いただいたメールアドレスに認証メールを送信しました。メールに記載されているリンクをクリックし、会員登録を完了してください。");
    	 
    	 
    	 return "redirect:/";
     }
     
     @GetMapping("/signup/verify")
     // メソッドの引数に@RequestParamを付けることでリクエストパラメータの値をその引数にバインドする
     public String verify(@RequestParam(name = "token") String token, Model model) {
    	 VerificationToken verificationToken = verificationTokenService.getVerificationToken(token);
    	 
    	 if (verificationToken != null) {
    		 User user = verificationToken.getUser();
    		 userService.enableUser(user);
    		 String successMessage = "会員登録が完了しました。";
    		 model.addAttribute("successMessage", successMessage);
    	 } else {
    		 String errorMessage = "トークンが無効です。";
    		 model.addAttribute("errorMessage", errorMessage);
    	 }
    	 
    	 return "auth/verifi";
     }
}
