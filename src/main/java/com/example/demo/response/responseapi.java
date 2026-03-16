package com.example.demo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class responseapi<T> {
    private String message;
    private T data; 
    
    //成功回應
    public static <T> responseapi<T> success(String message, T data) {
		return new responseapi<T>(message, data);
	}
    
    //失敗回應
    public static <T> responseapi<T> error(String message) {
		return new responseapi<T>(message, null);
	}
}