package account.mapper;

import account.dto.UserDTO;
import account.entity.User;

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
                .build();
    }
}
