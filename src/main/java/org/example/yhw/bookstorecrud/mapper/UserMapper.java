package org.example.yhw.bookstorecrud.mapper;

import org.example.yhw.bookstorecrud.dto.UserDTO;
import org.example.yhw.bookstorecrud.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "role", source = "role")
    UserDTO toUserDto(User user);

    User toUser(UserDTO userDTO);

}
