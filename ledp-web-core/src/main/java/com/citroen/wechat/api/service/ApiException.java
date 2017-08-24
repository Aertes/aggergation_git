package com.citroen.wechat.api.service;

public class ApiException extends Exception{
	private int    errcode;
    public ApiException(Throwable cause) {
        super(cause);
    }
    public ApiException(String message) {
        super(message);
    }
    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }
	public ApiException(int errcode,String message){
		super(message);
		this.errcode = errcode;
	}
	public int getErrcode() {
		return errcode;
	}
	
}
