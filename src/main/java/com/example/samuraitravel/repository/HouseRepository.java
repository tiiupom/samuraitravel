package com.example.samuraitravel.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraitravel.entity.House;

public interface HouseRepository extends JpaRepository<House, Integer> {
	public Page<House> findByNameLike(String keyword, Pageable pageable);
	// idカラムの値で降順に並べ替え、最初の1件を取得するメソッド
	public House findFirstByOrderByIdDesc();
	/* リポジトリではfindBy○○○And●●●()やfindBy○○○Or●●●()といったメソッドを定義することで
	 * SQLのAND検索・OR検索を行える
	 * 各カラム名の末尾にLikeキーワードを付けたストSQLのLIKE句と同様のクエリを実行できる
	 * Pageable : ページ番号とページサイズを保持するクラス */
	/*public Page<House> findByNameLikeOrAddressLike(String nameKeyword, String addressKeyword, Pageable pageable);
    public Page<House> findByAddressLike(String area, Pageable pageable);
	// findByPriceLessThanEqual : priceカラムが指定された値以下であるデータを検索できる
    public Page<House> findByPriceLessThanEqual(Integer price, Pageable pageable);    
	*/

	/* OrderByキーワードを使ったメソッドを定義することでSQLのORDER BY句と同様の並べ替えができる
	 * findBy●●●OrderBy●●●Asc()　昇順で検索
	 * findBy●●●OrderBy●●●Desc() 降順で検索
	 * すべてのデータを取得する場合、findAll By Order ByのようにfindAllとOrderByの間にもByを入れる　*/
	
	// 民宿名または目的地で検索する（新着順）
	public Page<House> findByNameLikeOrAddressLikeOrderByCreatedAtDesc(String nameKeyword, String addressKeyword, Pageable pageable);
	
	// 民宿名または目的地で検索する（宿泊料金が安い順）
	public Page<House> findByNameLikeOrAddressLikeOrderByPriceAsc(String nameKeyword, String addressKeyword, Pageable pageable);
	
	// エリアで検索する（新着順）
	public Page<House> findByAddressLikeOrderByCreatedAtDesc(String area, Pageable pageable);
	
	// エリアで検索する（宿泊料金が安い順）
	public Page<House> findByAddressLikeOrderByPriceAsc(String area, Pageable pageable);
	
	// 1泊あたりの予算で検索する（新着順）
	public Page<House> findByPriceLessThanEqualOrderByCreatedAtDesc(Integer price, Pageable pageable);
	
	// 1泊あたりの予算で検索する（宿泊料金が安い順）
	public Page<House> findByPriceLessThanEqualOrderByPriceAsc(Integer price, Pageable pageable);
	
	// すべてのデータを取得する（新着順）
	public Page<House> findAllByOrderByCreatedAtDesc(Pageable pageable);
	
	// すべてのデータを取得する（宿泊料金が安い順）
	public Page<House> findAllByOrderByPriceAsc(Pageable pageable);
	
	// 民宿を新着順に10件取得する
	public List<House> findTop10ByOrderByCreatedAtDesc();
}
