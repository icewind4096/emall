package com.windvalley.emall.form;

import lombok.Data;

@Data
public class UserInformationForm {
    private Integer id;

    private String username;

    private String email;

    private String phone;

    private String question;

    private String answer;

    private Integer role;
}
