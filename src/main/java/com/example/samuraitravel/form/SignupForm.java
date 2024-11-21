package com.example.samuraitravel.form;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/* ゲッターやセッターを自動生成できるアノテーション(Lombokの機能）
 * フォームクラスでは、フィールドに対し@NotBlankなどのアノテーションをつけるとそれに応じたバリデーションを行える */
@Data
public class SignupForm {
    @NotBlank(message = "氏名を入力してください。")
    private String name;

    @NotBlank(message = "フリガナを入力してください。")
    private String furigana;

    @NotBlank(message = "郵便番号を入力してください。")
    private String postalCode;

    @NotBlank(message = "住所を入力してください。")
    private String address;

    @NotBlank(message = "電話番号を入力してください。")
    private String phoneNumber;

    @NotBlank(message = "メールアドレスを入力してください。")
    @Email(message = "メールアドレスは正しい形式で入力してください。")
    private String email;

    @NotBlank(message = "パスワードを入力してください。")
    @Length(min = 8, message = "パスワードは8文字以上で入力してください。")
    private String password;

    @NotBlank(message = "パスワード（確認用）を入力してください。")
    private String passwordConfirmation;
}


/*		バリデーションの種類　　これらにmassage属性を設定するとエラーメッセージを任意のテキストに変更できる
 * @NotBlank												文字列がnullではなく、かつ空文字ではないことを検証
 * @NotNull												値がnullではないことを検証
 * @Max(value = 最大値)								整数がvalue属性に指定した最大値以下であることを検証
 * @Min(value = 最小値)								整数がvalue属性に指定した最小値以上であることを検証
 * @size(min = 最小数,　max = 最大数)			値がmin属性とmax属性に指定した範囲内であることを検証
 * 																文字列や配列、コレクションなどに使える
 * @Length(min = 最小値, max = 最大値)		文字列の長さがmin属性とmax属性に指定した範囲内であることを検証
 * @Email													文字列がメールアドレスの形式であることを検証
 * @pattern(regxp = "正規表現")					文字列が正規表現にマッチすることを検証
 * @Future													日付が現在よりも未来であることを検証
 * @Past													日付が現在よりも過去であることを検証
 */