package com.example;

import java.io.IOException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.*;

public class test02 extends SimpleListenerHost {

    @EventHandler
    private ListeningStatus onEvent(GroupMessageEvent event) throws IOException {
        String s = event.getMessage().contentToString().trim();
        Group g = event.getGroup();
        if (s.trim().startsWith("查询")) {
            GetMethod(s,event);
        }
        return ListeningStatus.LISTENING;
    }

    public void GetMethod(String input,GroupMessageEvent event) throws IOException {
        MessageChain chain = null;
        System.out.println(input);
        String name = "";
        if (input.trim().startsWith("查询")) {
            String str = input.replaceAll("查询", "").trim();

            if (str.startsWith("进化")) {
                name = str.replaceAll("进化", "").trim();
                System.out.println(name);
                chain = GetLevel(name,event);
//				String id = GetPokeId(name);
            }
            else if (str.startsWith("获取")) {
                name = str.replaceAll("获取", "").trim();
                System.out.println(name);
                chain = GetPokemon(name);
//				String id = GetPokeId(name);
            }
        }
        event.getGroup().sendMessage(chain);
    }

    /*
    public String GetPokeId(String name) throws IOException {
        String Pid = "";
        String u = "https://wiki.52poke.com/wiki/%E5%AE%9D%E5%8F%AF%E6%A2%A6%E5%88%97%E8%A1%A8%EF%BC%88%E6%8C%89%E5%85%A8%E5%9B%BD%E5%9B%BE%E9%89%B4%E7%BC%96%E5%8F%B7%EF%BC%89/%E7%AE%80%E5%8D%95%E7%89%88";

//         URL url=new URL(u); InputStreamReader is = new
//         InputStreamReader(url.openStream()); BufferedReader br = new
//         BufferedReader(is); String line =""; while ((line = br.readLine()) != null) {
//         System.out.println(line); } br.close(); is.close();

        Document document = Jsoup.connect(u).timeout(30000).get();
        Elements trs = document.getElementsByClass("a-c roundy eplist bgl-神奇宝贝百科 b-神奇宝贝百科 bw-2").select("tr");
        for (int i = 0; i < trs.size(); i++) {
            Elements tds = trs.get(i).select("td");
            for (int j = 0; j < tds.size(); j++) {
                String text = tds.get(j).text();
//				System.out.print(name+" "+text);
//				bufw.write(text+" ");
                if (text.equals(name)) {
                    Pid = tds.get(j - 1).text().replaceAll("#", "");
                    System.out.println(Pid);
                }
            }
//			System.out.println();
//			bufw.newLine();
        }
//		bufw.close();
        return Pid;
    }
    */

    public MessageChain GetLevel(String name,GroupMessageEvent event) throws IOException {
        MessageChain chain = MessageUtils.newChain();
        String u = "https://wiki.52poke.com/wiki/";
        Document document = Jsoup.connect(u + name).timeout(30000).get();
//		System.out.println(document);

        Element elementClass = document.getElementsByClass("mw-selflink selflink").first().parent();
        String className = elementClass.parent().parent().parent().className();
        System.out.println(className);

        Element tableClass = document.getElementsByClass(className).parents().parents().parents().first();

        for (int i = 0; i < tableClass.childrenSize(); i++) {
            Element trs = tableClass.children().get(i);
//			System.out.println(trs.text());
            for (int j = 0; j < trs.childrenSize(); j++) {
                System.out.println((i+1)+" "+(j+1));
               Elements tds = trs.select("tr:nth-child(" + (i + 1) + ") > td:nth-child(" + (j + 1) + ")");
                if(!tds.select("table > tbody > tr:nth-child(3)").text().equals("")) {
//                    System.out.println(tds.select("table > tbody > tr:nth-child(2) > td > a > img").attr("data-url"));
                    System.out.println(tds.select("table > tbody > tr:nth-child(3)").text());
                    System.out.println(tds.select("table > tbody > tr:nth-child(4)").text()+"\n");
                    Image img = Contact.uploadImage(event.getSender(), new URL("https:"+tds.select("table > tbody > tr:nth-child(2) > td > a > img").attr("data-url")).openConnection().getInputStream());
                    chain = new MessageChainBuilder()
                            .append(chain)

                            .append(tds.select("table > tbody > tr:nth-child(3)").text()+"\n")
                            .append(tds.select("table > tbody > tr:nth-child(4)").text()+"\n")
                            .append(img)
                            .build();
                }
                //#mw-content-text > div > table:nth-child(49) > tbody > tr:nth-child(1) > td.textblack
                else if(!tds.select("td.textblack").text().equals("")){
//                    System.out.println(tds.select("td.textblack").text());
                    chain = new MessageChainBuilder()
                            .append(chain)
                            .append(tds.select("td.textblack").text()+"\n")
                            .build();
                }
//                chain = new MessageChainBuilder()
//                        .append(chain)
//                        .append(trs.select("tr:nth-child(" + (i + 1) + ") > td:nth-child(" + (j + 1) + ")").text())
//                        .build();
//                if (trs.select("tr:nth-child(" + (i + 1) + ") > td:nth-child(" + (j + 1)
//                        + ") > table > tbody > tr:nth-child(2) > td > a > img").attr("data-url") != "") {
////                    System.out.println(trs.select("tr:nth-child(" + (i + 1) + ") > td:nth-child(" + (j + 1) + ") > table > tbody > tr:nth-child(2) > td > a > img").attr("data-url"));
//                    Image img = Contact.uploadImage(event.getSender(), new URL("https:"+trs.select("tr:nth-child(" + (i + 1) + ") > td:nth-child(" + (j + 1) + ") > table > tbody > tr:nth-child(2) > td > a > img").attr("data-url")).openConnection().getInputStream());
//                    chain = new MessageChainBuilder()
//                            .append(chain)
//                            .append(img)
//                            .build();
//                }
            }
        }
        return chain;
    }

    public MessageChain GetPokemon(String name) throws IOException {
        MessageChain chain = MessageUtils.newChain();
        String u = "https://wiki.52poke.com/wiki/";
        Document document = Jsoup.connect(u + name).timeout(30000).get();
//		System.out.println(document);
        Element element = document.getElementById("获得方式").parent().nextElementSibling().child(0);
        System.out.println(element.childrenSize());
        for (int i = 1; i < element.childrenSize() - 1; i++) {
            System.out.println(element.select("tr:nth-child(" + (i + 1) + ")").text());
            chain = new MessageChainBuilder()
                    .append(chain)
                    .append(element.select("tr:nth-child(" + (i + 1) + ")").text()+"\n")
                    .build();
        }
        return chain;
    }
}

