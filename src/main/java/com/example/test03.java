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

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class test03 extends SimpleListenerHost {
    public static MessageEvent getEvent() {
        return event;
    }

    public void setEvent(MessageEvent event) {
        test03.event = event;
    }

    private static MessageEvent event;

    @EventHandler
    private ListeningStatus onEvent(MessageEvent event) throws Exception {

        MessageChain message=event.getMessage();
        String s = message.contentToString();
//        System.out.println(message.serializeToMiraiCode());
        if(s.startsWith("查番")){
            setEvent(event);
            s=s.trim().replace("查番","");
            if(s.contains("[图片]")){
                String imgUrl = Image.queryUrl(Image.fromId(message.get(Image.Key).getImageId()));
//                System.out.println(imgUrl);
                GetAnime(imgUrl,event);
            }

        }
        return ListeningStatus.LISTENING;
    }

    public static String execCurl(String[] cmds) {
        ProcessBuilder process = new ProcessBuilder(cmds).directory(new File("D:\\Downloads\\curl-7.86.0-win64-mingw\\bin"));
        Process p;
        try {
            p = process.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            return builder.toString();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.print("error " + e);
        }
        System.out.print("execCurl失败");
        return null;
    }

        public static String second2Time(double second) {
            if (second < 0) {
                return "00:00";
            }

            int h = (int) (second / 3600);
            int m = (int) ((second % 3600) / 60);
            int s = (int) (second % 60);
            String str = "";
            if (h > 0) {
                str = (h < 10 ? ("0" + h) : h) + ":";
            }
            str += (m < 10 ? ("0" + m) : m) + ":";
            str += (s < 10 ? ("0" + s) : s);
            return str;
        }

        public static void GetAnime(String imgUrl,MessageEvent event) throws Exception {
//		String img = "http://gchat.qpic.cn/gchatpic_new/3091325189/0-0-804244FED3DC7140DBA28FB6A743C7C2/0?term=2";
            String url = "https://api.trace.moe/search?cutBorders&url=" + imgUrl;
            event.getSubject().sendMessage("请稍等正在搜索");
            System.out.println(url);
            String[] cmds1 = {"curl", url ,
//				"-H","x-trace-key: RwTYYUMsLOpbSUzbB9abLvgjYNVsPhkan5qWMlkozlg"
            };
//		String[] cmds2 = { "curl", "https://api.trace.moe/me" };
//        System.out.println(execCurl(cmds1));
//        System.out.println(execCurl(cmds2));
            String ec1 = execCurl(cmds1);
//            System.out.println(ec1);
            JSONObject json1 = JSON.parseObject(ec1);
//		JSONObject json1 = JSON.parseObject("{\"result\":[{\"anilist\":108577,\"image\":\"https://media.trace.moe/image/108577/%5BOhys-Raws%5D%20Sora%20no%20Aosa%20o%20Shiru%20Hito%20yo%20(BD%201280x720%20x264%20AACx2).mp4.jpg?t=3591.585&now=1667300400&token=RvW70iBk0ITSTtBiPYDrld2VQ\",\"filename\":\"[Ohys-Raws] Sora no Aosa o Shiru Hito yo (BD 1280x720 x264 AACx2).mp4\",\"similarity\":0.9190475894615223,\"from\":3590.92,\"to\":3592.25,\"video\":\"https://media.trace.moe/video/108577/%5BOhys-Raws%5D%20Sora%20no%20Aosa%20o%20Shiru%20Hito%20yo%20(BD%201280x720%20x264%20AACx2).mp4?t=3591.585&now=1667300400&token=81BV2hbHCd6PL4bVHkGMwv7nT7g\"},{\"anilist\":99263,\"image\":\"https://media.trace.moe/image/99263/Tate%20no%20Yuusha%20no%20Nariagari%20-%2025%20(BD%201280x720%20x264%20AAC).mp4.jpg?t=800.96&now=1667300400&token=3m9dgH1ItcHQquwfP6PUIBQoKgc\",\"filename\":\"Tate no Yuusha no Nariagari - 25 (BD 1280x720 x264 AAC).mp4\",\"similarity\":0.8038156939663161,\"episode\":25,\"from\":800.75,\"to\":801.17,\"video\":\"https://media.trace.moe/video/99263/Tate%20no%20Yuusha%20no%20Nariagari%20-%2025%20(BD%201280x720%20x264%20AAC).mp4?t=800.96&now=1667300400&token=5kTmWgE4qla161Ge6fhPXfmdUAE\"},{\"anilist\":8425,\"image\":\"https://media.trace.moe/image/8425/%5BDA%5D%5BGosick%5D%5B17%5D%5BH264_AAC%5D%5B720P%5D%5BBIG5%5D.mp4.jpg?t=682.455&now=1667300400&token=R4YRDWrzCV1uFwx1axheBaY2SBg\",\"filename\":\"[DA][Gosick][17][H264_AAC][720P][BIG5].mp4\",\"similarity\":0.8015418614159825,\"episode\":17,\"from\":682.33,\"to\":682.58,\"video\":\"https://media.trace.moe/video/8425/%5BDA%5D%5BGosick%5D%5B17%5D%5BH264_AAC%5D%5B720P%5D%5BBIG5%5D.mp4?t=682.455&now=1667300400&token=rQyKQyRieBMiVaFviglrdy65gPI\"},{\"anilist\":20799,\"image\":\"https://media.trace.moe/image/20799/%5BLeopard-Raws%5D%20JoJo%20no%20Kimyou%20na%20Bouken%20-%20Stardust%20Crusaders%20-%20Egypt%20Hen%20-%2013%20RAW%20(MBS%201280x720%20x264%20AAC).mp4.jpg?t=646.46&now=1667300400&token=glvcxYsMLhWEFHotXu5ZNWECx6Y\",\"filename\":\"[Leopard-Raws] JoJo no Kimyou na Bouken - Stardust Crusaders - Egypt Hen - 13 RAW (MBS 1280x720 x264 AAC).mp4\",\"similarity\":0.8000677405692801,\"episode\":13,\"from\":646,\"to\":646.92,\"video\":\"https://media.trace.moe/video/20799/%5BLeopard-Raws%5D%20JoJo%20no%20Kimyou%20na%20Bouken%20-%20Stardust%20Crusaders%20-%20Egypt%20Hen%20-%2013%20RAW%20(MBS%201280x720%20x264%20AAC).mp4?t=646.46&now=1667300400&token=374ctI1apfFhzQU93JdUbmCo\"},{\"anilist\":112936,\"image\":\"https://media.trace.moe/image/112936/%5B%E6%A1%9C%E9%83%BD%E5%AD%97%E5%B9%95%E7%BB%84%5D%5B200131%5D%5B720P%5D%5B%E9%AD%94%E4%BA%BA%5D%E2%97%8B%E2%97%8B%E4%BA%A4%E9%85%8D%20%E7%AC%AC%E4%B8%80%E8%A9%B1%20%E5%84%AA%E7%AD%89%E7%94%9F%E3%81%AE%E5%BD%BC%E5%A5%B3%E3%81%AF%E3%82%A8%E3%83%AB%E3%83%95%E3%81%AE%E3%81%8A%E5%A7%AB%E6%A7%98.mp4.jpg?t=0.75&now=1667300400&token=FJgbhhmrXUtLu4XUb1oWxQ7Rk\",\"filename\":\"[桜都字幕组][200131][720P][魔人]○○交配 第一話 優等生の彼女はエルフのお姫様.mp4\",\"similarity\":0.799187474109921,\"episode\":1,\"from\":0.75,\"to\":0.75,\"video\":\"https://media.trace.moe/video/112936/%5B%E6%A1%9C%E9%83%BD%E5%AD%97%E5%B9%95%E7%BB%84%5D%5B200131%5D%5B720P%5D%5B%E9%AD%94%E4%BA%BA%5D%E2%97%8B%E2%97%8B%E4%BA%A4%E9%85%8D%20%E7%AC%AC%E4%B8%80%E8%A9%B1%20%E5%84%AA%E7%AD%89%E7%94%9F%E3%81%AE%E5%BD%BC%E5%A5%B3%E3%81%AF%E3%82%A8%E3%83%AB%E3%83%95%E3%81%AE%E3%81%8A%E5%A7%AB%E6%A7%98.mp4?t=0.75&now=1667300400&token=ikpRiFvdwxhXdww7KJVPiT8hTk\"},{\"anilist\":2894,\"image\":\"https://media.trace.moe/image/2894/%E7%B7%8A%E7%B8%9B%E3%81%AE%E9%A4%A8%20%E7%95%A5%E5%A5%AA%20%E5%89%8D%E7%B7%A8.mp4.jpg?t=1255.705&now=1667300400&token=lGNDKc0jMlxlPg47KAMO6rBYEE\",\"filename\":\"緊縛の館 略奪 前編.mp4\",\"similarity\":0.7966260475029248,\"from\":1255.33,\"to\":1256.08,\"video\":\"https://media.trace.moe/video/2894/%E7%B7%8A%E7%B8%9B%E3%81%AE%E9%A4%A8%20%E7%95%A5%E5%A5%AA%20%E5%89%8D%E7%B7%A8.mp4?t=1255.705&now=1667300400&token=q9h3mgXQ3G51YNNGIRMQniKQizo\"},{\"anilist\":20725,\"image\":\"https://media.trace.moe/image/20725/%5BLeopard-Raws%5D%20Kuroko%20no%20Basuke%203%20-%2014%20RAW%20(MBS%201280x720%20x264%20AAC).mp4.jpg?t=592.17&now=1667300400&token=14EKfHMcQEQ8R6BVgmWjQlR0JFk\",\"filename\":\"[Leopard-Raws] Kuroko no Basuke 3 - 14 RAW (MBS 1280x720 x264 AAC).mp4\",\"similarity\":0.7949761930307514,\"episode\":14,\"from\":588.92,\"to\":595.42,\"video\":\"https://media.trace.moe/video/20725/%5BLeopard-Raws%5D%20Kuroko%20no%20Basuke%203%20-%2014%20RAW%20(MBS%201280x720%20x264%20AAC).mp4?t=592.17&now=1667300400&token=3ROJyq8T3weIlsUE1nMfzwpaY88\"},{\"anilist\":20635,\"image\":\"https://media.trace.moe/image/20635/%5B%E7%95%B0%E5%9F%9F%E5%AD%97%E5%B9%95%E7%B5%84%5D%5B%E9%BE%8D%E7%8F%A0%20%E6%94%B9%5D%5B17%5D%5BDragon%20Ball%20Kai%5D%5B1280x720%5D%5B%E7%B9%81%E9%AB%94%5D.mp4.jpg?t=1206.33&now=1667300400&token=u3L24qgRyORJvk1DE2tigfPRnVI\",\"filename\":\"[異域字幕組][龍珠 改][17][Dragon Ball Kai][1280x720][繁體].mp4\",\"similarity\":0.7941856185683176,\"episode\":17,\"from\":1206.08,\"to\":1206.58,\"video\":\"https://media.trace.moe/video/20635/%5B%E7%95%B0%E5%9F%9F%E5%AD%97%E5%B9%95%E7%B5%84%5D%5B%E9%BE%8D%E7%8F%A0%20%E6%94%B9%5D%5B17%5D%5BDragon%20Ball%20Kai%5D%5B1280x720%5D%5B%E7%B9%81%E9%AB%94%5D.mp4?t=1206.33&now=1667300400&token=MZzjzRDtCM2P49tiAnAIO4xjamw\"},{\"anilist\":132456,\"image\":\"https://media.trace.moe/image/132456/%5BOhys-Raws%5D%20Jahy-sama%20wa%20Kujikenai!%20-%2006%20(AT-X%201280x720%20x264%20AAC).mp4.jpg?t=1421.625&now=1667300400&token=vm4D66qV9Z4xDGamXINAlVVCq38\",\"filename\":\"[Ohys-Raws] Jahy-sama wa Kujikenai! - 06 (AT-X 1280x720 x264 AAC).mp4\",\"similarity\":0.7935328984537873,\"episode\":6,\"from\":1421.25,\"to\":1422,\"video\":\"https://media.trace.moe/video/132456/%5BOhys-Raws%5D%20Jahy-sama%20wa%20Kujikenai!%20-%2006%20(AT-X%201280x720%20x264%20AAC).mp4?t=1421.625&now=1667300400&token=e4K6AqdFMwUPssFepjVomCtWs\"},{\"anilist\":14719,\"image\":\"https://media.trace.moe/image/14719/%5BKamigami%5D%20JoJo%20no%20Kimyou%20na%20Bouken%20-%2007%20%5Bx264%201280x720%20AAC%20Sub(Chs%2CJap)%5D.mp4.jpg?t=835.83&now=1667300400&token=8DRyUZgBcDSGINmrh34W4psq8c\",\"filename\":\"[Kamigami] JoJo no Kimyou na Bouken - 07 [x264 1280x720 AAC Sub(Chs,Jap)].mp4\",\"similarity\":0.7922821787145751,\"episode\":7,\"from\":835.83,\"to\":835.83,\"video\":\"https://media.trace.moe/video/14719/%5BKamigami%5D%20JoJo%20no%20Kimyou%20na%20Bouken%20-%2007%20%5Bx264%201280x720%20AAC%20Sub(Chs%2CJap)%5D.mp4?t=835.83&now=1667300400&token=7Xl6xlLrk08wLnHCRb4SIIJaeB4\"}],\"frameCount\":8013881,\"error\":\"\"}\r\n");
//            System.out.println(json1);
            if(ec1!=null||!ec1.equals("")){
            JSONArray result = json1.getJSONArray("result");
		System.out.println(result);
            MessageChain chain = MessageUtils.newChain();
            for (int i = 0; i < 3; i++) {
                System.out.println(i + " " + result.get(i));
                JSONObject items = JSON.parseObject(result.get(i).toString());
//                System.out.println(items.get("image"));
                Image img = Contact.uploadImage(event.getSubject(), GetInputStream(items.get("image").toString()));

                chain = new MessageChainBuilder()
                        .append((i+1)+"/3")
//                        .append(chain)
                        .append(img)
//                        .append("图片:\n" + items.get("image").toString() + "\n")
                        .append("番名:\n" + items.get("filename").toString() + "\n")
                        .append("时间:" + second2Time(Double.parseDouble(items.get("to").toString())) + "\n")
                        .build();
                if (items.toString().contains("episode"))
                    chain = chain.plus("集数:" + items.get("episode").toString() + "\n");
//                chain = chain.plus("\n");
//			System.out.println(items.get("similarity").toString());
//                in.close();
                event.getSubject().sendMessage(chain.plus(new QuoteReply(event.getSource())));
            }
        }
    }

    public static InputStream GetInputStream(String ImgUrl) throws IOException {
        InputStream inputStream;
        HttpURLConnection httpurlconn;
        URL url = new URL(ImgUrl);
        httpurlconn = (HttpURLConnection) url.openConnection();
        httpurlconn.setConnectTimeout(30000);
        httpurlconn.setRequestMethod("GET");
        httpurlconn.setRequestProperty("accpet",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        httpurlconn.setRequestProperty("content-type", "image/jpg");
        httpurlconn.setRequestProperty("accept-encoding", "gzip, deflate, br");
        httpurlconn.setRequestProperty("accept-language", "zh-CN,zh;q=0.9");
        httpurlconn.setRequestProperty("cache-control", "max-age=0");
        httpurlconn.setRequestProperty("sec-fetch-dest", "document");
        httpurlconn.setRequestProperty("sec-fetch-mode", "navigate");
        httpurlconn.setRequestProperty("sec-fetch-site", "none");
        httpurlconn.setRequestProperty("sec-fetch-user", "?1");
        httpurlconn.setRequestProperty("upgrade-insecure-requests", "1");
        httpurlconn.setRequestProperty("user-agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36");
        try {
            inputStream = httpurlconn.getInputStream();
        } catch (IOException e) {
            errorEvent(String.valueOf(e));
            throw new RuntimeException(e);
        }
        return inputStream;
    }

//    public static byte[] readInputStream(InputStream inStream) throws Exception{
//        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//        //创建一个Buffer字符串
//        byte[] buffer = new byte[1024];
//        //每次读取的字符串长度，如果为-1，代表全部读取完毕
//        int len = 0;
//        //使用一个输入流从buffer里把数据读取出来
//        while( (len=inStream.read(buffer)) != -1 ){
//            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
//            outStream.write(buffer, 0, len);
//        }
//        //关闭输入流
//        inStream.close();
//        //把outStream里的数据写入内存
//        return outStream.toByteArray();
//    }

    public static void errorEvent(String e){
        getEvent().getSubject().sendMessage(e);
    }
}


