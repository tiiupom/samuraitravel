/* 認証・認可用のモデル
 * 「リポジトリ」…データベースにアクセスし、CRUD処理を行うインターフェース
 	インターフェース
 	メソッドの名前、引数の型、戻り値の型のみを定義したもの*/

package com.example.samuraitravel.repository;

 import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraitravel.entity.Role;

 //public interface RoleRepository {
 public interface RoleRepository extends JpaRepository<Role, Integer> {
	 public Role findByName(String name);
}

	
/*	JpaRepositoryインターフェースの継承　２つの引数を指定する
 *	 	JpaRepository<エンティティのクラス型, 主キーのデータ型>
 *	
 *	継承することで基本的なCRUD操作を行うためのメソッドが利用可能となる
 *	findAll()：テーブル内のすべてのエンティティを取得する
	findById()：引数に指定したidのエンティティを取得する
	save()：引数に指定したエンティティを保存または更新する
	delete()：引数に指定したエンティティを削除する
	deleteById()：引数に指定したidのエンティティを削除する
 */