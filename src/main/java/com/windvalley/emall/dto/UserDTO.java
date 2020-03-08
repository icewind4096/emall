package com.windvalley.emall.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserDTO implements Serializable {
    @JsonIgnore
    private Integer id;

    private String username;

    @JsonIgnore
    private String password;

    private String email;

    private String phone;

    private String question;

    private String answer;

    @JsonIgnore
    private Integer role;

    @JsonIgnore
    private Date createTime;

    @JsonIgnore
    private Date updateTime;
}
