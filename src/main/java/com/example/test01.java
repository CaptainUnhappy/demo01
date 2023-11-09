package com.example;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.Base64;
import java.util.regex.Pattern;

public class test01 extends SimpleListenerHost {
    ShopWeb sw = new ShopWeb();
    JSONObject resultJ;

    @EventHandler
    private ListeningStatus onEvent(MessageEvent event) throws Exception {

        String s = event.getMessage().contentToString();

        if (s.startsWith("game")) {
            String str = s.replaceAll("game", "");
            String totalS = null;
            try {

//                System.out.println(str);
//                if(isNumeric(str)||str.equals("")) {
                if (str.equals(""))
                    str = LocalDate.now().plusDays(1).toString();
//                System.out.println(str);
                resultJ = sw.GetStringJson(sw.GetFullDate(str));

                totalS = JSON.toJSONString(resultJ.get("total"));

                int total = Integer.parseInt(totalS);
                if (total > 0) {
                    MessageChain totalgame = MessageUtils.newChain(new PlainText(str + "发售数:" + total + "\n请等待图片加载"));
                    event.getSubject().sendMessage(totalgame);
                    GetMessage(str, event);
                } else
                    event.getSubject().sendMessage(new PlainText(str + "发售数:0"));
//                }
            } catch (Exception e) {
//                SendError.send(e,event);
            }

        }
        return ListeningStatus.LISTENING;
    }

    public static InputStream base64ToInputStream(String base64) {
        ByteArrayInputStream stream = null;
        try {
            byte[] bytes = Base64.getDecoder().decode(base64);
            stream = new ByteArrayInputStream(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stream;
    }

    public static InputStream ImageNull() {
        String base64Data = "iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsQAAA7EAZUrDhsAAAEJSURBVFhHzZRBDsIwDAR5Azd+yI1f8Uqk0FR1ZY3XddpLM9IccOy1hQSPNsjv8RryLPMfoJZcMWP+A96fp7R9l1FnqA9SdvqlXr+8G+qDpJ0MDAtg9Z6RvjCwWlC9Z6QvDLTPrNPqncTKBoPsM+u0eiexsuGXHQZusp9mLNOaECCWetlPM5bpQcTSIQvqDkOFj1hQdvCrtL9W1s2zzHuACu9mB1w9bL4D1LDXFmWqGS+Z7wD+jGzQFljdL1X1feFW3wWxgoHbDzD9shFVxiqIFTW0qJYcqTJWQaxgQIWfkXkkVjCgQs/IPBIrQIV2ierpVsx7gP2MaIWa6WbMd4Aa9laoGS+5+YDW/iIE7CaEzPVwAAAAAElFTkSuQmCC";
        return base64ToInputStream(base64Data);
    }

    public void GetMessage(String str, MessageEvent event) throws Exception {
        if (str == null || str.equals(""))
            str = LocalDate.now().plusDays(1).toString();
        resultJ = sw.GetStringJson(sw.GetFullDate(str));
        String totalS = JSON.toJSONString(resultJ.get("total"));
        int total = Integer.parseInt(totalS);
        JSONArray items = JSON.parseArray(JSON.toJSONString(resultJ.get("items")));
        int MaxTotal = 5;
        MessageChain chain = MessageUtils.newChain();
        ForwardMessageBuilder builder = new ForwardMessageBuilder(event.getSubject());
        for (int i = 0; i < total; i++) {
            System.out.println(i + " ");
            JSONObject itemsJ = JSONArray.parseObject(items.get(i).toString());
            Image img = null;
            if (itemsJ.getString("iurl") == null || itemsJ.getString("hard").equals("9_amiibo")) {
                System.out.println(itemsJ.getString("url"));
                if (itemsJ.getString("url").endsWith("index.html") && !itemsJ.getString("url").endsWith("set/index.html")) {
//                        img = Contact.uploadImage(event.getSubject(), new URL(itemsJ.getString("url").replace("index.html", "") + "img/package.jpg").openConnection().getInputStream());
                    String u = itemsJ.getString("url").replace("/index.html", "").replace("https://www.nintendo.co.jp/hardware/amiibo/lineup/", "");
                    img = Contact.uploadImage(event.getSubject(), new URL("https://www.nintendo.co.jp/hardware/amiibo/lineup/img/thumb/" + u + ".jpg").openConnection().getInputStream());
                }
                if (img == null) {
                    img = Contact.uploadImage(event.getSubject(), ImageNull());
                }
            } else if (itemsJ.getString("iurl").startsWith("http")) {
                img = Contact.uploadImage(event.getSubject(), new URL(itemsJ.getString("iurl")).openConnection().getInputStream());
            } else {
                img = Contact.uploadImage(event.getSubject(), new URL("https://img-eshop.cdn.nintendo.net/i/" + itemsJ.getString("iurl") + ".jpg?w=640&h=360").openConnection().getInputStream());
                //w=640&h=360 w=320&h=180 w=280&h=158 w=160&h=90
            }

            MessageChainBuilder chainBuilder = new MessageChainBuilder().append(img);
            JSONArray sslurls = itemsJ.getJSONArray("sslurl");
            if (sslurls != null && total <= 25) {
                chainBuilder.append(Contact.uploadImage(event.getSubject(), new URL("https://img-eshop.cdn.nintendo.net/i/" + sslurls.get(0) + ".jpg?w=320&h=180").openConnection().getInputStream()));
            }

            chainBuilder.append("\n").append(itemsJ.getString("title"));

            String price = itemsJ.getString("price");
            if (price != null) {
                chainBuilder.append("  ").append(price).append("円");
            }

            if (total < MaxTotal)
                chain = chain.plus(chainBuilder.build());
            else
                builder.add(event.getBot(), chainBuilder.build());
        }
        if (total < MaxTotal) {
            event.getSubject().sendMessage(chain);
        } else {
            ForwardMessage forward = builder.build();
            event.getSubject().sendMessage(forward);
        }
    }
}