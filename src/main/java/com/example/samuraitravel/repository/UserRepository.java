package com.example.samuraitravel.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraitravel.entity.User;

 //public interface UserRepository {
//JpaRepositoryインターフェースを継承すると基本的なCRUD操作を行うためのメソッドが利用可能になる

/* リポジトリではfindBy○○○And●●●()やfindBy○○○Or●●●()といったメソッドを定義することで
 * SQLのAND検索・OR検索を行える
 * 各カラム名の末尾にLikeキーワードを付けたストSQLのLIKE句と同様のクエリを実行できる
 * Pageable : ページ番号とページサイズを保持するクラス */
public interface UserRepository extends JpaRepository<User, Integer> {
	 public User findByEmail(String email);
	 public Page<User> findByNameLikeOrFuriganaLike(String nameKeyword, String furiganaKeyword, Pageable pageable);
}