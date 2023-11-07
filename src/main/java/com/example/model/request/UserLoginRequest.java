package com.example.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求体
 *
 * @author yrares
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = -5303870556038538793L;

    private String userAccount;

    private String userPassword;
}
