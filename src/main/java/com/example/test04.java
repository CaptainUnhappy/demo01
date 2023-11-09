package com.example;

//import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.MessageEvent;
//import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;

public class test04 extends SimpleListenerHost {
    public static MessageEvent getEvent() {
        return event;
    }

    public void setEvent(MessageEvent event) {
        test04.event = event;
    }
    private static MessageEvent event;

    public static String getCharid() {
        return charid;
    }

    public static void setCharid(String charid) {
        test04.charid = charid;
    }

    private static String charid;

    public static String getMoveid() {
        return moveid;
    }

    public static void setMoveid(String moveid) {
        test04.moveid = moveid;
    }

    private static String moveid;

    public static Boolean getHasgif() {
        return hasgif;
    }

    public static void setHasgif(Boolean hasgif) {
        test04.hasgif = hasgif;
    }

    private static Boolean hasgif;

    @EventHandler
    private ListeningStatus onEvent(MessageEvent event) {

        String input = event.getMessage().contentToString();
        if(input.startsWith("查斗帮助")) {
            setEvent(event);
            event.getSubject().sendMessage("查斗 斗士 动作 (gif)");
        }else if(input.startsWith("查斗")) {
            setEvent(event);
            System.out.println(input);
            String[] inputs = input.toLowerCase().replace("查斗", "").split(" |-|\\.|//|🥵");
            try {
                GetInput(inputs);
            } catch (IOException e) {
                event.getSubject().sendMessage(String.valueOf(e));
            }
        }
        return ListeningStatus.LISTENING;
    }

    public static void GetInput(String[] inputs) throws IOException {
        String inputcharid =inputs[inputs.length-2];
        String charid = null;
        charid = GetCharId(inputcharid);

        String inputmoveid =inputs[inputs.length-1];
        String moveid  = null;
        moveid  =GetMoveId(inputmoveid);

        Boolean hasgif=false;
        if(inputs.length>2){
            charid = GetCharId(inputs[inputs.length-3]);
            inputmoveid =inputs[inputs.length-2];
            moveid  =GetMoveId(inputmoveid);
            hasgif = hasGif(inputs[inputs.length-1]);
            System.out.println(hasgif);
        }

//        if(charid == null) {
//            getEvent().getSubject().sendMessage("Character Error");
//        }
//        else
            if(charid.equals("kazuya")){
            moveid = GetKaZuYaMoveId(inputmoveid);
        }

        if(charid.equals("kazuya")&&moveid == null){
            getEvent().getSubject().sendMessage("kazuya: 轻攻击|强攻击|蹲攻击|冲刺攻击|猛击|输入攻击");
        }
        else if(moveid == null) {
            getEvent().getSubject().sendMessage("Move Error: 地面攻击|空中攻击|特殊攻击|抓/投|闪/滚|其他");
        }

        System.out.println(charid);
        System.out.println(moveid);
        setCharid(charid);
        setMoveid(moveid);
        setHasgif(hasgif);
        String u = "https://ultimateframedata.com/";
        Document document = Jsoup.connect(u+charid).timeout(30000).get();
//		System.out.println(document);
        Element all = document.getElementById("contentcontainer");
        Elements ids = null;
        if (all != null) {
                ids = all.getElementById(moveid).nextElementSibling().children();
        }
        MessageChain chain = MessageUtils.newChain();

        if (ids != null) {
            getEvent().getSubject().sendMessage("正在加载"+charid);
            chain = ChainByClass(ids);
        }
        if(moveid.equals("misc")) {
            Element el = all.select("div:nth-child(" + all.childrenSize() + ") > div.movecontainer.plain.misc").first();
            String s = "";
            for (int i = 1; i < el.childrenSize(); i++) {
//                System.out.println(el.child(i).text());
                s += el.child(i).text() + "\n";
            }
            chain = MessageUtils.newChain(new PlainText(s)).plus(chain);
        }


//        System.out.println(chain);
        getEvent().getSubject().sendMessage(chain);
    }

    public static String GetCharId(String input) {
        String id = null;
        LinkedHashMap<String, String> charId =new LinkedHashMap<String, String>(){{
            put("banjo_and_kazooie","banjo_and_kazooie/banjo&kazooie/banjoandkazooie/banjokazooie/班卓熊/熊鸟/鸟熊");
            put("bayonetta","bayonetta/贝优妮塔/贝姐/大长腿");

            put("bowser_jr","bowser_jr/bowserjr/小库巴/小库霸");
            put("byleth","byleth/贝雷特/贝雷丝/贝蕾特/贝蕾丝/贝老师");
            put("captain_falcon","captain_falcon/captainfalcon/飞隼队长");
            put("chrom","chrom/库洛姆");
            put("cloud","cloud/云哥/克劳德");
            put("corrin","corrin/神威/kamuyi");
            put("daisy","daisy/黛西公主/黛丝公主/菊花公主");
            put("dark_pit","dark_pit/darkpit/黑皮特/黑暗皮特");
            put("dark_samus","dark_samus/darksamus/黑暗萨姆斯/黑萨姆斯");
            put("diddy_kong","diddy_kong/diddykong/ddk/迪迪刚/小猴子");
            put("donkey_kong","donkey_kong/donkeykong/dkk/森喜刚/大金刚");
            put("dr_mario","dr_mario/drmario/马医");
            put("duck_hunt","duck_hunt/duckhunt/打猎鸭/鸭狗/狗鸭");
            put("falco","falco/老鹰");
            put("fox","fox/狐狸");
            put("ganondorf","ganondorf/盖农多夫/盖侬多夫");
            put("greninja","greninja/甲贺忍蛙");
            put("hero","hero/dq/勇者");
            put("ice_climbers","ice_climbers/iceclimbers/冰山翻越者/冰山攀登者/雪人兄弟");
            put("ike","ike/艾克");
            put("incineroar","incineroar/炽焰咆啸虎/老虎");
            put("inkling","inkling/乌贼娘/墨灵");
            put("isabelle","isabelle/西施慧/西施惠/小母狗");
            put("jigglypuff","jigglypuff/胖丁/粉气球");
            put("joker","joker/雨宫莲/小丑");

            put("ken","ken/肯");
            put("king_dedede","king_dedede/kingdedede/ddd大王/企鹅");
            put("king_k_rool","king_k_rool/kingkrool/鳄鱼王");
            put("kirby","kirby/卡比/粉团子");

            put("little_mac","little_mac/littlemac/小麦克");
            put("lucario","lucario/路卡利欧");
            put("lucas","lucas/卢卡斯");
            put("lucina","lucina/露琪娜");
            put("luigi","luigi/路易吉");
            put("mario","mario/马力欧/马力奥/马利欧/马利奥");
            put("marth","marth/马尔斯");
            put("mega_man","mega_man/megaman/洛克人");
            put("meta_knight","meta_knight/metaknight/梅塔骑士/魅塔骑士");
            put("mewtwo","mewtwo/超梦");
            put("mii_brawler","mii_brawler/miibrawler/拳击mii/mii拳击/拳mii/mii拳");
            put("mii_gunner","mii_gunner/miigunner/枪mii/mii枪");
            put("mii_swordfighter","mii_swordfighter/miiswordfighter/剑mii/mii剑");
            put("minmin","minmin/面面");
            put("mr_game_and_watch","mr_game_and_watch/mrgameandwatch/mrgame&watch/小黑人/代码人");
            put("mythra","mythra/光");
            put("ness","ness/奈斯");
            put("olimar","olimar/欧力马/欧利马/奥力马/奥利马/欧力玛/欧利玛/奥力玛/奥利玛/皮克敏");
            put("pac_man","pac_man/pacman/吃豆人");
            put("palutena","palutena/帕鲁提娜/帕露提娜/帕鲁蒂娜/帕露蒂娜/女神");
            put("peach","peach/桃花公主/碧琪公主/桃子公主");
            put("pichu","pichu/皮丘");
            put("pikachu","pikachu/皮卡丘");
            put("piranha_plant","piranha_plant/piranhaplant/食人花");
            put("pit","pit/白皮特");
            put("pt_squirtle","pt_squirtle/杰尼龟");
            put("pt_ivysaur","pt_ivysaur/妙蛙草");
            put("pt_charizard","pt_charizard/喷火龙");
            put("pyra","pyra/焰");
            put("richter","richter/里希特");
            put("ridley","ridley/利德雷");
            put("rob","rob/机器人/萝卜");
            put("robin","robin/军师/鲁弗莱");
            put("rosalina_and_luma","rosalina_and_luma/rosalinaandluma/rosalina&luma/罗莎塔/银河公主/琪琪/罗泽塔");
            put("roy","roy/罗伊");
            put("ryu","ryu/隆");

            put("sephiroth","sephiroth/萨菲罗斯");
            put("sheik","sheik/希克");
            put("shulk","shulk/修尔克/秀尔克");
            put("simon","simon/西蒙");
            put("snake","snake/斯内克/史内克/蛇叔");
            put("sonic","sonic/索尼克/蓝刺猬");
            put("sora","sora/索拉");
            put("steve","steve/史蒂夫/mc/minecraft");
            put("terry","terry/特瑞");
            put("toon_link","toon_link/卡通林克/风林");
            put("villager","villager/村民/岛民");
            put("wario","wario/瓦力欧/瓦力奥/瓦利欧/瓦利奥/大胡子");
            put("wii_fit_trainer","wii_fit_trainer/wiifittrainer/wiitrainer/wii教练");
            put("wolf","wolf/狼");
            put("yoshi","yoshi/耀西/小恐龙");
            put("young_link","young_link/年轻林克/杨林");
            put("zelda","zelda/塞尔达");
            put("zero_suit_samus","zero_suit_samus/zerosuitsamus/zerosamus/零装甲萨姆斯/零萨");

            put("bowser","bowser/库巴王/库霸王");
            put("link","link/大林克/野炊");
            put("samus","samus/萨姆斯");
            put("kazuya","kazuya/一八/18");
        }};
        for(String key : charId.keySet()) {
            if(charId.get(key).contains(input)) {
                id=key;
            }
        }
        return id;
    }

    public static String GetKaZuYaMoveId(String input) {
        String id = null;
        LinkedHashMap<String, String> moveids =new LinkedHashMap<String, String>();
        moveids.put("jabattacks"	    , "jab      /ja/轻攻击/jabattacks");
        moveids.put("tiltattacks"	    , "tilt     /ta/强攻击/tiltattacks");
        moveids.put("crouchingattacks"	, "crouch   /ca/蹲攻击/crouchingattacks");
        moveids.put("dashattacks"	    , "dash     /da/冲刺攻击/dashattacks");
        moveids.put("smashattacks"	    , "smash    /sa/吹飞攻击/猛击/smashattacks");
        moveids.put("inputattacks"	    , "input    /ia/输入攻击/inputattacks");
        moveids.put("aerialattacks"	    , "空中攻击/空中/空/aerialattacks/aerial/air/aa");
        moveids.put("specialattacks"	, "特殊攻击/特殊/特/specialattacks/special/sp");
        moveids.put("grabs"			    , "抓投/抓/投/grabs/throws");
        moveids.put("dodges"			, "闪滚/闪/滚/dodges/rolls");
        moveids.put("misc"				, "其他/杂项/miscinfo/misc/other");
        for(String key : moveids.keySet()) {
            if(moveids.get(key).contains(input)) {
                id=key;
            }
        }
        return id;
    }
    public static String GetMoveId(String input) {
        String id = null;
        LinkedHashMap<String, String> input2id =new LinkedHashMap<String, String>();
        input2id.put("groundattacks"	, "地面攻击/地面/地/groundattacks/ground");
        input2id.put("aerialattacks"	, "空中攻击/空中/空/aerialattacks/aerial/air");
        input2id.put("specialattacks"	, "特殊攻击/特殊/特/specialattacks/special");
        input2id.put("grabs"			, "抓投/抓/投/grabs/throws");
        input2id.put("dodges"			, "闪滚/闪/滚/dodges/rolls");
        input2id.put("misc"				, "其他/杂项/miscinfo/misc/other");
        for(String key : input2id.keySet()) {
            if(input2id.get(key).contains(input)) {
                id=key;
            }
        }
        return id;
    }

    public static MessageChain ChainByClass(Elements els) {

        LinkedHashMap<String, String> moves =new LinkedHashMap<String, String>();
        moves.put("movename"	    , "");
        moves.put("startup"		    , "起始帧");
        moves.put("activeframes"    , "持续帧");
        moves.put("endlag"		    , "结束硬直");
        moves.put("advantage"   	, "对盾硬直");
        moves.put("hopsautocancel"	, "自动取消");
        moves.put("hopsactionable"	, "落地前");
        moves.put("totalframes" 	, "全体帧");
        moves.put("landinglag"  	, "落地硬直");
        moves.put("notes"		    , "备注");
        moves.put("basedamage"	    , "基础伤害");
        moves.put("shieldlag"	    , "冻结帧");
        moves.put("shieldstun"  	, "盾僵");
//        moves.put("whichhitbox"	    , "判定");
        MessageChain chain = MessageUtils.newChain();
        int index = 0;
        if(getCharid().equals("olimar")&&getMoveid().equals("grabs")){
            index = 3;
        }
        for (int i = index;i<els.size();i++) {
//            getEvent().getSubject().sendMessage((i+1)+"/"+els.size());
            System.out.println((i+1)+"/"+els.size());
            String imgurl = null;
            Image img = null;
            if(els.get(i).getElementsByClass("hitboximg").hasClass("hitboximg")&&getHasgif()) {
                for(int j=0;j<els.get(i).getElementsByClass("hitboximg").size();j++){
                imgurl = els.get(i).getElementsByClass("hitboximg").get(j).attr("data-featherlight");
//            System.out.println(imgurl);

                try {
                    InputStream inputStream = GetInputStream(imgurl);
                    img = Contact.uploadImage(getEvent().getSender(), inputStream);
                } catch (IOException e) {
                    getEvent().getSubject().sendMessage(String.valueOf(e));
//                throw new RuntimeException(e);
                }
                if (img != null) {
                    chain = chain.plus(img);
                }}
            }

            for (String s : moves.keySet()) {
                Elements divs = els.get(i).getElementsByClass(s);
                if (!divs.text().equals("--") && !divs.text().equals("")) {
//                    System.out.println(moves.get(s) + " - " + divs.text());
                    chain = new MessageChainBuilder()
                            .append(chain)
                            .append(moves.get(s))
                            .append(" - ")
                            .append(divs.text())
                            .append("\n")
                            .build();
                }
            }
        }
        return chain;
    }

    public static InputStream GetInputStream(String ImgUrl) throws IOException {
        InputStream inputStream = null;
        HttpURLConnection httpurlconn;
        String u = "https://ultimateframedata.com/";
        URL url = new URL(u+ImgUrl);
            httpurlconn = (HttpURLConnection) url.openConnection();
            httpurlconn.setConnectTimeout(50000);
            httpurlconn.setRequestMethod("GET");

            inputStream = httpurlconn.getInputStream();
        return inputStream;
    }

    public static Boolean hasGif(String input) {
        String s = "gif动图";
        if (s.contains(input))
            return true;
        else
            return false;
    }
}
