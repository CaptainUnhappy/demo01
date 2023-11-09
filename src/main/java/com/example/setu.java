package com.example;

import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.PlainText;

public class setu  extends SimpleListenerHost {
    static String[] commandList = new String[]{"色图"};
    @EventHandler
    private ListeningStatus onEvent(MessageEvent event) {
        String s = event.getMessage().contentToString().trim();
        if (s.trim().startsWith(commandList[0])) {
            s = s.replaceAll(commandList[0], "").trim();
        }
        if (startNumber(commandList,s)!=-1) {
            String[] sarr = s.split("\\|");
        }

        return ListeningStatus.LISTENING;
    }

//    public static String input2url(String input){
//
//    }
//
//    public static String GetResult(String url){
//
//    }
//
//    public static void SendSeTu(MessageEvent event){
//
//    }

    public static int startNumber(String[] array, String str) {
        for (int i = 0; i < array.length; i++) {
            if (str.startsWith(array[i])) {
                return i;
            }
        }
        return -1;
    }
}
