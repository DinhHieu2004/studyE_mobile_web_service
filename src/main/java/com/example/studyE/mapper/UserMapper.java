package com.example.studyE.mapper;

import com.example.studyE.dto.response.UserResponse;
import com.example.studyE.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toUserResponse(User user);
}
