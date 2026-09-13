package com.carrental.modules.user.mapper;

import com.carrental.modules.user.dto.UserDTO;
import com.carrental.modules.user.entity.User;
import com.carrental.modules.user.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    @Mapping(target = "status", expression = "java(user.getStatus() != null ? user.getStatus().toString() : null)")
    @Mapping(target = "role", expression = "java(user.getRoles().stream().map(com.carrental.modules.user.entity.Role::getName).findFirst().orElse(null))")
    UserDTO toDTO(User user);

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "status", expression = "java(dto.getStatus() != null ? User.UserStatus.valueOf(dto.getStatus().toUpperCase()) : User.UserStatus.ACTIVE)")
    User toEntity(UserDTO dto);

    @Mapping(target = "status", expression = "java(dto.getStatus() != null ? User.UserStatus.valueOf(dto.getStatus().toUpperCase()) : user.getStatus())")
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateEntityFromDTO(UserDTO dto, @MappingTarget User user);
}