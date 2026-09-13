package com.carrental.modules.user.mapper;

import com.carrental.modules.user.dto.UserDTO;
import com.carrental.modules.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    @Mapping(target = "status", expression = "java(user.getStatus() != null ? user.getStatus().toString() : null)")
    UserDTO toDTO(User user);

    @Mapping(target = "status", expression = "java(dto.getStatus() != null ? User.UserStatus.valueOf(dto.getStatus().toUpperCase()) : User.UserStatus.ACTIVE)")
    User toEntity(UserDTO dto);

    @Mapping(target = "status", expression = "java(dto.getStatus() != null ? User.UserStatus.valueOf(dto.getStatus().toUpperCase()) : user.getStatus())")
    void updateEntityFromDTO(UserDTO dto, @MappingTarget User user);
}