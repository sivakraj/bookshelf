package in.techunthink.bookshelf.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.techunthink.bookshelf.entity.Book;
import in.techunthink.bookshelf.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
    }

    @Test
    void testCreateBook() throws Exception {
        Book book = new Book("Test Book", "Test Author", "1234567890");
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title", is("Test Book")))
                .andExpect(jsonPath("$.author", is("Test Author")))
                .andExpect(jsonPath("$.isbn", is("1234567890")));
    }

    @Test
    void testGetAllBooks() throws Exception {
        Book book1 = new Book("Book 1", "Author 1", "1111111111");
        Book book2 = new Book("Book 2", "Author 2", "2222222222");
        bookRepository.save(book1);
        bookRepository.save(book2);

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Book 1")))
                .andExpect(jsonPath("$[1].title", is("Book 2")));
    }

    @Test
    void testGetBookById() throws Exception {
        Book book = bookRepository.save(new Book("Test Book", "Test Author", "1234567890"));

        mockMvc.perform(get("/api/books/{id}", book.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(book.getId().intValue())))
                .andExpect(jsonPath("$.title", is("Test Book")));
    }

    @Test
    void testGetBookByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/books/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateBook() throws Exception {
        Book book = bookRepository.save(new Book("Old Title", "Old Author", "0000000000"));
        Book updatedBook = new Book("New Title", "New Author", "1111111111");

        mockMvc.perform(put("/api/books/{id}", book.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(book.getId().intValue())))
                .andExpect(jsonPath("$.title", is("New Title")))
                .andExpect(jsonPath("$.author", is("New Author")))
                .andExpect(jsonPath("$.isbn", is("1111111111")));
    }

    @Test
    void testUpdateBookNotFound() throws Exception {
        Book updatedBook = new Book("New Title", "New Author", "1111111111");

        mockMvc.perform(put("/api/books/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedBook)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteBook() throws Exception {
        Book book = bookRepository.save(new Book("Test Book", "Test Author", "1234567890"));

        mockMvc.perform(delete("/api/books/{id}", book.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/books/{id}", book.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteBookNotFound() throws Exception {
        mockMvc.perform(delete("/api/books/999"))
                .andExpect(status().isNotFound());
    }
}