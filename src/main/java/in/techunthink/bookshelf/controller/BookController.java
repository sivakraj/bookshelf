package in.techunthink.bookshelf.controller;

import in.techunthink.bookshelf.entity.Book;
import in.techunthink.bookshelf.repository.BookRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final BookRepository bookRepository;

    // GET all books
    @GetMapping
    public List<Book> getAllBooks() {
        log.info("Fetching all books");
        return bookRepository.findAll();
    }

    // GET a single book by ID
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        log.info("Fetching book with id: {}", id);
        Optional<Book> book = bookRepository.findById(id);
        return book.map(ResponseEntity::ok)
            .orElseGet(() -> {
                log.warn("Book with id: {} not found", id);
                return ResponseEntity.notFound().build();
            });
    }

    // POST a new book
    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        log.info("Creating a new book: {}", book);
        Book savedBook = bookRepository.save(book);
        log.info("Book created with id: {}", savedBook.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
    }

    // PUT to update an existing book
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book bookDetails) {
        log.info("Updating book with id: {}", id);
        Optional<Book> optionalBook = bookRepository.findById(id);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            book.setTitle(bookDetails.getTitle());
            book.setAuthor(bookDetails.getAuthor());
            book.setIsbn(bookDetails.getIsbn());
            Book updatedBook = bookRepository.save(book);
            log.info("Book updated: {}", updatedBook);
            return ResponseEntity.ok(updatedBook);
        } else {
            log.warn("Book with id: {} not found for update", id);
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE a book
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        log.info("Deleting book with id: {}", id);
        Optional<Book> book = bookRepository.findById(id);
        if (book.isPresent()) {
            bookRepository.delete(book.get());
            log.info("Book deleted with id: {}", id);
            return ResponseEntity.noContent().build();
        } else {
            log.warn("Book with id: {} not found for deletion", id);
            return ResponseEntity.notFound().build();
        }
    }
}