package com.windvalley.emall.converter;

import com.windvalley.emall.dto.UserDTO;
import com.windvalley.emall.form.UserInformationForm;
import com.windvalley.emall.form.UserRegisterForm;
import com.windvalley.emall.pojo.User;
import org.springframework.beans.BeanUtils;

public class User2UserDTO {
    public static UserDTO convert(User user){
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }

    public static UserDTO convert(UserRegisterForm userRegisterForm){
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(userRegisterForm, userDTO);
        return userDTO;
    }

    public static UserDTO convert(UserInformationForm userInformationForm){
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(userInformationForm, userDTO);
        return userDTO;
    }
}
