package com.hrm.Mapper.user;

import com.hrm.Entity.user.User;
import com.hrm.dto.request.user.user.UserCreationRequest;
import com.hrm.dto.request.user.user.UserUpdateRequest;
import com.hrm.dto.response.user.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User user);

    @Mapping(target = "role", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
