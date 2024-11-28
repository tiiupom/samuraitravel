package com.example.samuraitravel.controller;

 import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.service.HouseService;

 @Controller
 @RequestMapping("/houses")
public class HouseController {
     private final HouseService houseService;
 
     public HouseController(HouseService houseService) {
         this.houseService = houseService;
     }
	
	/* どのパラメータが存在するか（どの検索フォームが送信されたか）によって民宿データの検索方法を条件分岐させる	
	 * RequestParam : ブラウザからのリクエストの値（パラメータ）を取得できる
	 * 					コントローラクラスで使用する場合は「model.addAttribute()」を使用して名前と値を指定する
	 * PageableDefault : Pageable をコントローラーメソッドに注入するときにデフォルトを設定する */
     @GetMapping
     public String index(@RequestParam(name = "keyword", required = false) String keyword,
                         @RequestParam(name = "area", required = false) String area,
                         @RequestParam(name = "price", required = false) Integer price,
                         @RequestParam(name = "order", required = false) String order,
                         @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
                         Model model)
     {
         Page<House> housePage;
		
		/* RequestParamで取得したorderパラメータの値がpriceAsc（安い順）であれば
		 * そのように並べ替えるようにする
		 * また、パラメータの値がそれ以外（null又はcreatedAtDesc）の場合は新着順で並べ替える */
         if (keyword != null && !keyword.isEmpty()) {
             //housePage = houseService.findHousesByNameLikeOrAddressLike(keyword, keyword, pageable);
        	 if (order != null && order.equals("priceAsc")) {
                 housePage = houseService.findHousesByNameLikeOrAddressLikeOrderByPriceAsc(keyword, keyword, pageable);
             } else {
                 housePage = houseService.findHousesByNameLikeOrAddressLikeOrderByCreatedAtDesc(keyword, keyword, pageable);
             }
         } else if (area != null && !area.isEmpty()) {
             //housePage = houseService.findHousesByAddressLike(area, pageable);
        	 if (order != null && order.equals("priceAsc")) {
                 housePage = houseService.findHousesByAddressLikeOrderByPriceAsc(area, pageable);
             } else {
                 housePage = houseService.findHousesByAddressLikeOrderByCreatedAtDesc(area, pageable);
             } 
         } else if (price != null) {
             //housePage = houseService.findHousesByPriceLessThanEqual(price, pageable);
        	 if (order != null && order.equals("priceAsc")) {
                 housePage = houseService.findHousesByPriceLessThanEqualOrderByPriceAsc(price, pageable);
             } else {
                 housePage = houseService.findHousesByPriceLessThanEqualOrderByCreatedAtDesc(price, pageable);
             }
         } else {
             //housePage = houseService.findAllHouses(pageable);
        	 if (order != null && order.equals("priceAsc")) {
                 housePage = houseService.findAllHousesByOrderByPriceAsc(pageable);
             } else {
                 housePage = houseService.findAllHousesByOrderByCreatedAtDesc(pageable);
             }
         }
 
         model.addAttribute("housePage", housePage);
         model.addAttribute("keyword", keyword);
         model.addAttribute("area", area);
         model.addAttribute("price", price);
         model.addAttribute("order", order);
 
         return "houses/index";
     }
}
