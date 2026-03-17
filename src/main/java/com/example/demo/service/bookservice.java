package com.example.demo.service;

import java.util.List;

import com.example.demo.exception.bookexception;
import com.example.demo.model.Book;

public interface bookservice {
	
	List<Book> findAllBooks();
	Book getBookById(Integer id) throws bookexception;
	void addBook(Book book) throws bookexception;
	
	void updateBook(Integer id, Book book) throws bookexception;
	void updateBookName(Integer id, String name) throws bookexception;
	void updateBookPrice(Integer id, Double price) throws bookexception;
	void updateBookNameAndPrice(Integer id, String name, Double price) throws bookexception;
	
	void deleteBook(Integer id) throws bookexception;
	
}