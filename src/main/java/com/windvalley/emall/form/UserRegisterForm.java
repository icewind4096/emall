package com.windvalley.emall.form;

import lombok.Data;

@Data
public class UserRegisterForm {
    private String username;

    private String password;

    private String email;

    private String phone;

    private String question;

    private String answer;

    private Integer role;
}
