package com.iss.rabbit.api.exception;



/**
 * 自定义异常封装类：参数传递错误、编译时异常
 */
public class MessageException extends Exception{

    private static final long serialVersionUID = 3976385338776884393L;

    public MessageException(){
        super();
    }

    public MessageException(String message){
       super(message);
    }

    public MessageException(String message,Throwable cause){
        super(message,cause);
    }

    public MessageException(Throwable cause){
        super(cause);
    }
}
