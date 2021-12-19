package com.iss.rabbit.api.exception;

/**
 * 自定义异常：封装运行时异常
 */
public class MessageRunTimeException extends RuntimeException {

    private static final long serialVersionUID = 5414748904064380289L;

    public MessageRunTimeException(){
        super();
    }

    public MessageRunTimeException(String message){
        super(message);
    }

    public MessageRunTimeException(String message,Throwable cause){
        super(message,cause);
    }

    public MessageRunTimeException(Throwable cause){
        super(cause);
    }
}


