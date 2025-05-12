package org.example.yhw.bookstorecrud.mapper;

import org.example.yhw.bookstorecrud.dto.AuthorDTO;
import org.example.yhw.bookstorecrud.model.Author;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthorMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    AuthorDTO toAuthorDto(Author author);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    Author toAuthor(AuthorDTO authorDTO);
}
