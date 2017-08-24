package com.citroen.wechat.exception;

/**
 * 异常处理
 * @author 何海粟
 * @date2015年6月4日
 */
public class WechatException extends Exception{

    public WechatException() {
        super();
    }

    public WechatException(String message) {
        super(message);
    }

    public WechatException(String message, Throwable cause) {
        super(message, cause);
    }

    public WechatException(Throwable cause) {
        super(cause);
    }
}