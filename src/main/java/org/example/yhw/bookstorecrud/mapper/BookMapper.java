package org.example.yhw.bookstorecrud.mapper;

import org.example.yhw.bookstorecrud.dto.AuthorDTO;
import org.example.yhw.bookstorecrud.dto.BookDTO;
import org.example.yhw.bookstorecrud.model.Author;
import org.example.yhw.bookstorecrud.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface BookMapper extends BaseMapper<BookDTO, Book>{
    @Override
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorName", source = "author.name")
    BookDTO toDto(Book book);

    @Override
    @Mapping(target = "author", source = "authorId", qualifiedByName = "authorIdToAuthor")
    Book toEntity(BookDTO bookDTO);

    @Named("authorIdToAuthor")
    default Author authorIdToAuthor(Long authorId) {
        if (authorId == null) {
            return null;
        }
        Author author = new Author();
        author.setId(authorId);
        return author;
    }

    default void setDates(Book book){
        if (book.getPublicationDate() == null) {
            book.setPublicationDate(LocalDate.now());
        }
        book.setLastUpdated(LocalDateTime.now());
    }

}
