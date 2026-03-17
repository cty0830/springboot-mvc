package com.example.demo.controller;

import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.bmi;
import com.example.demo.model.Book;
import com.example.demo.response.responseapi;

@RestController //可以省去寫	@ResponseBody
@RequestMapping("/api") // 在網址增加一層 /api
public class apicontroller{

	/**
	 * 1. api首頁
	 * 路徑 1 : /home
	 * 路徑 2 : /welcome
	 * 網址 1 : http:/localhost:8080/api/home
	 * 網址 2 : http:/localhost:8080/api/welcome
	 */
	@GetMapping(value = {"/home", "/welcome"}, produces = "text/plain;charset = utf-8")
	public String home() {
		return "我是 api 首頁";
	}

	/**
	 * 2. 帶參數
	 * 路徑 1 : /greet?name=john&age=18
	 * 路徑 2 : /greet?name=mary
	 * 網址 1 : http:/localhost:8080/api/greet?name=john&age=18
	 * 結果 1 : hi john, 18(成年)
	 * 網址 2 : http:/localhost:8080/api/greet?name=mary
	 * 結果 1 : hi mary, 0(未成年)
	 * 限制 : name 為必要參數, age 為可選參數(初始值0)
	 */
	@GetMapping(value = "/greet" )
	public String greet(@RequestParam(value = "name", required = true) String username,
			            @RequestParam(value = "age", required = false, defaultValue = "0")  Integer userage) {
		String result = String.format("hi %s, %d(%s)", username, userage, userage >= 18? "成年":"未成年");
		return result;
	}
	
	/**
	 * 3. 上述的精簡
	 * 方法參數名和請求參數名同
	 */
	@GetMapping(value = "/greet2" )
	public String greet2(@RequestParam String name,
			            @RequestParam(required = false, defaultValue = "0")  Integer age) {
		//String result = String.format("hi %s, %d(%s)", username, userage, userage >= 18? "成年":"未成年");
		//return result;
		
		return greet(name, age);
	}
	
	/** 4. pratice:
	 * 路徑 : /bmi?h=170$w=60
	 * 網址 : http:/localhost:8080/api/bmi?h=170&w=60
	 * 判斷 : bmi <= 18 過輕, bmi > 23 過重
	 * 結果 : 身高:170cm 體重:60kg bmi = 20.76(正常)
	 */
	@GetMapping(value = "/bmi" )
	public String bmi(@RequestParam(required = false, defaultValue = "0") Double h,
                      @RequestParam(required = false, defaultValue = "0") Double w) {

		double h1 = h / 100.0;
	    double bmi = w / (h1 * h1);

	    String status;
	    if (bmi < 18) {
	        status = "過輕";
	    } else if (bmi < 23) {
	        status = "正常";
	    } else {
	        status = "過重";
	    }

	    return String.format("身高:%.0fcm 體重:%.0fkg BMI:%.2f(%s)", h, w, bmi, status);
	}
	
	/** 5. 回傳 json 結構
	 * 路徑 : /json/bmi?h=170$w=60
	 * 網址 : http://localhost:8080/api/json/bmi?h=170&w=60
	 * 判斷 : bmi <= 18 過輕, bmi > 23 過重
	 * 結果 : 
	 * {
	 * 		"status": 200,
	 * 		"message: "BMI計算成功",
	 * 		"data": {
	 * 			"height": 170.0,
	 * 			"weight": 60.0,
	 * 			"bmi": 20.76
	 * 		} 
	 *}
	 */
	@GetMapping(value = "/json/bmi", produces = "application/json;charset = utf-8")
	public ResponseEntity<responseapi<bmi>> calcbmi(@RequestParam(required = false) Double h,
            										   @RequestParam(required = false) Double w) {
		
		if(h == null || w == null) {
			return ResponseEntity.badRequest().body(responseapi.error("請輸入身高和體重參數"));
		}
		
		// badrequest => http 400
		if(h <= 0 || w <= 0) {
			//return ResponseEntity.badRequest().body(new responseapi<>("身高體重參數錯誤", null));
			return ResponseEntity.badRequest().body(responseapi.error("身高體重參數錯誤"));
		}
		
		double bmi = w / Math.pow(h / 100, 2);
		bmi b = new bmi(h, w, bmi);
		
		// ok => http 200
		//return ResponseEntity.ok(new responseapi<bmi>("計算成功", b));
		return ResponseEntity.ok(responseapi.success("計算成功", b));
	}
	
	/** 6. 同名多筆資料
	 * 路徑 : /json/age?age=17&age=20&age=21
	 * 網址 : http://localhost:8080/api/json/age?age=17&age=20&age=21
	 * 請判斷平均年齡
	 */
	@GetMapping(value = "/json/age", produces = "application/json;charset = utf-8")
	public ResponseEntity<responseapi<Object>> getaverage(@RequestParam(name = "age", required = false)List<Integer> ages) {
		if(ages == null || ages.size() == 0) {
			return ResponseEntity.badRequest().body(responseapi.error("請輸入年齡(age)"));
		}
		double avg = ages.stream().mapToInt(Integer::valueOf).average().orElseGet(() -> 0);
		Object data = Map.of("年齡", ages, "平均年齡", String.format("%.1f", avg));
		return ResponseEntity.ok(responseapi.success("計算成功", data));
	}

	/** 7. lab practice: 得到多筆 score 資料
	 * 路徑 : /json/score?score=80&score=100&score=50&score=70&score=30
	 * 網址 : http://localhost:8080/api/json/score?score=80&score=100&score=50&score=70&score=30
	 * 請設計一個方法能判斷出: 最高分 最低分 平均 總分 及格分數 不及格分數
	 */
	@GetMapping(value = "/json/score", produces = "application/json;charset = utf-8")
	public ResponseEntity<responseapi<Object>> getscore(@RequestParam(name = "score", required = false)List<Integer> scores) {
		if(scores == null || scores.size() == 0) {
			return ResponseEntity.badRequest().body(responseapi.error("請輸入成績(scores)"));
		}
		//統計資料
		IntSummaryStatistics stat = scores.stream().mapToInt(Integer::valueOf).summaryStatistics(); 
		
		//利用 collectors.partitioningby 分組, key = true 及格, 反之不及格
		Map<Boolean, List<Integer>> resultMap = scores.stream()
				.collect(Collectors.partitioningBy(score -> score >= 60));
		
		Object data = Map.of(
				"最高分: ", stat.getMax(),
				"最低分: ", stat.getMin(),
				"平均成績: ", stat.getAverage(),
				"總分: ", stat.getSum(),
				"及格: ", resultMap.get(true),
				"不及格: ", resultMap.get(false)
				);
		
		return ResponseEntity.ok(responseapi.success("計算成功", data));
	}
	
	/**
	 * 8. 多筆參數轉 Map
	 * name 書名(String), price 價格(Double), amount 數量(Integer), pub 出刊/停刊(Boolean)
	 * 路徑: /json/book?name=Math&price=12.5&amount=10&pub=true
	 * 路徑: /json/book?name=English&price=10.5&amount=20&pub=false
	 * 網址: http://localhost:8080/api/json/book?name=Math&price=12.5&amount=10&pub=true
	 * 網址: http://localhost:8080/api/json/book?name=English&price=10.5&amount=20&pub=false
	 * 讓參數自動轉成 key/value 的 Map 集合
	 * */
	@GetMapping(value = "/json/book", produces = "application/json;charset = utf-8")
	public ResponseEntity<responseapi<Object>> getbookinfo(@RequestParam Map<String, Object> bookMap){
		System.out.printf("bookmap = %s%n", bookMap);
		return ResponseEntity.ok(responseapi.success("成功", bookMap));
	}
	
	/**
	 * 9. 多筆參數轉 model
	 * 路徑: /json/book2?name=Math&price=12.5&amount=10&pub=true
	 * 網址: http://localhost:8080/api/json/book2?name=Math&price=12.5&amount=10&pub=true
	 */
	@GetMapping(value = "/json/book2", produces = "application/json;charset=utf-8")
	public ResponseEntity<responseapi<Book>> getBookInfo2(Book book) {
		book.setId(1); // 設定 id
		System.out.printf("book = %s%n", book);
		return ResponseEntity.ok(responseapi.success("成功", book));
	}
	
	
	/**
	 * 10. 路徑參數
	 * 早期設計風格
	 * 路徑: /json/book?id=1 得到 id=1 的書
	 * 路徑: /json/book?id=2 得到 id=2 的書
	 * 
	 * 現代設計風格(REST)
	 * GET /books  查詢所有書籍
	 * GET /book/1 查詢指定書籍
	 * 
	 * 路徑: /json/book/1 得到 id=1 的書
	 * 路徑: /json/book/2 得到 id=2 的書
	 * 網址: http://localhost:8080/api/json/book/1
	 * 網址: http://localhost:8080/api/json/book/2
	 * */
	@GetMapping(value = "/json/book/{id}", produces = "application/json;charset = utf-8")
	public ResponseEntity<responseapi<Book>> getbookbyid(@PathVariable Integer id){
		//書庫
		List<Book> books = List.of(
				new Book(1, "小叮噹", 12.5, 20, true),
				new Book(2, "老夫子", 10.5, 30, true),
				new Book(3, "好小子", 9.5, 40, true),
				new Book(4, "新樂園", 14.5, 50, false)
				);
		
		//根據 id 搜尋書籍
		Optional<Book> optbook = books.stream().filter(book -> book.getId().equals(id)).findFirst();
		
		//是否找到
		if (optbook.isEmpty()) {
			return ResponseEntity.badRequest().body(responseapi.error("查無此書"));
		}
		
		Book book = optbook.get();
		return ResponseEntity.ok(responseapi.success("查詢成功", book));
	}
	
	/**
	 * Lab
	 * 書庫: 請參考上面的實作
	 * 
	 * 得到已出版(pub:true)的書籍
	 * 網址: http://localhost:8080/api/json/book/pub/true
	 * 
	 * 得到未出版(pub:false)的書籍
	 * 網址: http://localhost:8080/api/json/book/pub/false
	 * */
	@GetMapping(value = "/json/book/pub/{isPub}")
	public ResponseEntity<responseapi<List<Book>>> queryBook(@PathVariable Boolean isPub) {
		// 書庫
		List<Book> books = List.of(
				new Book(1, "小叮噹", 12.5, 20, true),
				new Book(2, "老夫子", 10.5, 30, true),
				new Book(3, "好小子", 9.5, 40, true),
				new Book(4, "新樂園", 14.5, 50, false));
		
		// 過濾出刊/停刊
		List<Book> queryBooks = books.stream().filter(book -> book.getPub().equals(isPub)).toList();
		if(queryBooks.size() == 0) {
			return ResponseEntity.badRequest().body(responseapi.error("查無" + (isPub?"出刊":"停刊") + "書籍資料"));
		}
		return ResponseEntity.ok(responseapi.success("查詢成功:" + (isPub?"出刊":"停刊"), queryBooks));
	}
	
	
}