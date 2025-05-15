package org.example.yhw.bookstorecrud.mapper;

import org.example.yhw.bookstorecrud.dto.UserDTO;
import org.example.yhw.bookstorecrud.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper extends BaseMapper<UserDTO, User>{

    @Override
    UserDTO toDto(User user);

    User toEntity(UserDTO userDTO);

}
