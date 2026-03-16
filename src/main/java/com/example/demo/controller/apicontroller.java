package com.example.demo.controller;

import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.bmi;
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
	
	/** 4. 回傳 json 結構
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
	
	@GetMapping(value = "/json/bmi")
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
}