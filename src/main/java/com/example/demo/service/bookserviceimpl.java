package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.example.demo.exception.bookexception;
import com.example.demo.model.Book;
import com.example.demo.respository.bookrespository;

@Service
public class bookserviceimpl implements bookservice{

	@Autowired
	@Qualifier("bookrespositoryimpl")
	private bookrespository bookrespository;
	@Override
	public List<Book> findAllBooks() {
		return bookrespository.findAllBooks();
	}

	@Override
	public Book getBookById(Integer id) throws bookexception {
		Optional<Book> optbook = bookrespository.getBookById(id);
		if(optbook.isEmpty()) {
			throw new bookexception("id:" + id + ",查無此書");
		}
		return optbook.get();
	}

	@Override
	public void addBook(Book book) throws bookexception {
		if(!bookrespository.addBook(book)) {
			throw new bookexception("新增失敗, " + book);
		}
	}

	@Override
	public void updateBook(Integer id, Book book) throws bookexception {
		if(!bookrespository.updatebook(id, book)) {
			throw new bookexception("修改失敗, id: " + id + ", " + book);
		}
	}

	@Override
	public void updateBookName(Integer id, String name) throws bookexception {
		Book book = getBookById(id);
		book.setName(name);
		updateBook(book.getId(), book);
	}

	@Override
	public void updateBookPrice(Integer id, Double price) throws bookexception {
		Book book = getBookById(id);
		book.setPrice(price);
		updateBook(book.getId(), book);
	}

	@Override
	public void updateBookNameAndPrice(Integer id, String name, Double price) throws bookexception {
		Book book = getBookById(id);
		book.setName(name);
		book.setPrice(price);
		updateBook(book.getId(), book);
	}

	@Override
	public void deleteBook(Integer id) throws bookexception {
		if(!bookrespository.deleteBook(id)) {
			throw new bookexception("刪除失敗, id: " + id);
		}
	}
}
