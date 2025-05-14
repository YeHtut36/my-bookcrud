package org.example.yhw.bookstorecrud.mapper;

import org.example.yhw.bookstorecrud.dto.AuthorDTO;
import org.example.yhw.bookstorecrud.model.Author;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthorMapper extends BaseMapper<AuthorDTO, Author >{

    @Override
    @Mapping(target = "booksCount", expression = "java(author.getBooks() != null ? author.getBooks().size() : 0)")
    AuthorDTO toDto(Author author);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    Author toEntity(AuthorDTO authorDTO);

}
