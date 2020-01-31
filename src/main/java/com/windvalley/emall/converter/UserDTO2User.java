package com.windvalley.emall.converter;

import com.windvalley.emall.dto.UserDTO;
import com.windvalley.emall.pojo.User;
import org.springframework.beans.BeanUtils;

public class UserDTO2User {
    public static User convert(UserDTO userDTO){
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        return user;
    }
}
