package com.example.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseResponse<T> implements Serializable {
    private int code;

    private T data;

    private String message;

    private String desctiprion;

    public BaseResponse(int code, T data, String message,String desctiprion) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.desctiprion = desctiprion;
    }

    public BaseResponse(int code, T data,String message) {
        this(code,data,message,"");
    }

    public BaseResponse(int code, T data) {
        this(code,data,"","");
    }

    public BaseResponse(ErrorCode errorCode){
        this(errorCode.getCode(),null,errorCode.getMessage(),errorCode.getDescription());
    }
}
