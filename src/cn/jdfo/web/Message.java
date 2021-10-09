package cn.jdfo.web;

import cn.jdfo.tool.View;
import com.fasterxml.jackson.annotation.JsonView;

/**
 * 在发送错误相应的时候自带文字
 */
public class Message {
    @JsonView(View.Message.class)
    private int status;
    @JsonView(View.Message.class)
    private String message;

    Message(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public Message() {
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
