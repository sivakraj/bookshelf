package in.techunthink.bookshelf.repository;

import in.techunthink.bookshelf.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    // You can add custom query methods here if needed
}