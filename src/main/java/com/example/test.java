package com.example;
// 导入必要的类
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.message.data.PlainText;

public class test extends SimpleListenerHost {

    // 注册事件监听器，当好友消息事件发生时调用onEvent方法
    @EventHandler
    private ListeningStatus onEvent(FriendMessageEvent event) {
        // 获取消息内容
        String s = event.getMessage().contentToString();
        // 判断是否为"hi"
        if (s.equals("hi")) {
            // 发送回复消息
            event.getSender().sendMessage(new PlainText("Hello!"));
            // 输出
//            System.out.println("getMessage:"+event.getMessage()+"\ncontent: "+event.getMessage().contentToString());
        }
        // 返回LISTENING表示继续监听事件
        return ListeningStatus.LISTENING;
    }
}
