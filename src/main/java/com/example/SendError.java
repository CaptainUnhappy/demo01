package com.example;

import net.mamoe.mirai.event.events.MessageEvent;

public class SendError {
    /**
     * 异常消息
     * @param e     异常
     * @param event     响应对象
     */
    public static void send(Exception e, MessageEvent event) {
        event.getSubject().sendMessage(String.valueOf(e));
    }
    /**
     * 发送信息
     * @param s     信息
     * @param event     响应对象
     */
    public static void send(String s,MessageEvent event) {
        event.getSubject().sendMessage(s);
    }
}
