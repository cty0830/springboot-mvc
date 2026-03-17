package com.example.demo.respository;

import java.util.List;
import java.util.Optional;

import com.example.demo.model.Book;

public interface bookrespository {
	
	// 查找全部書籍
	List<Book> findAllBooks();
	
	// 查找指定書籍
	Optional<Book> getBookById(Integer id);
	
	// 新增書籍
	boolean addBook(Book book);
	
	// 修改書籍
	boolean updatebook(Integer id, Book book);
	
	// 刪除書籍
	boolean deleteBook(Integer id);
	
}