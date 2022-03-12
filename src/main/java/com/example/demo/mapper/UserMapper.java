package com.example.demo.mapper;

import com.example.demo.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUserProfileDto(User user);
}
