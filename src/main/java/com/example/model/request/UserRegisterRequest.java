package com.example.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 297831187860053982L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;
}
