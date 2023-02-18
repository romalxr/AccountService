package account.mapper;

import account.dto.UserDTO;
import account.entity.User;

import java.util.stream.Collectors;

public class UserMapper {

    public static User toEntity(UserDTO userDTO){
        return User.builder()
                .name(userDTO.getName())
                .lastname(userDTO.getLastname())
                .email(userDTO.getEmail().toLowerCase())
                .password(userDTO.getPassword())
                .build();
    }

    public static UserDTO toDTO(User userEntity){
        return UserDTO.builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .lastname(userEntity.getLastname())
                .email(userEntity.getEmail())
                .roles(userEntity.getUserGroups().stream()
                        .map(el -> "ROLE_" + el)
                        .collect(Collectors.toSet()))
                .build();
    }
}