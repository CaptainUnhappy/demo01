package com.example;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.ChromedriverManager.countChromedriverInstances;
import static com.example.ChromedriverManager.killExcessChromedriverProcesses;

public class kazuya extends SimpleListenerHost {
    private static final String senderName = "ᕕ(◠ڼ◠)ᕗ";
    private static final String QQ_GROUP_ID = "261965114";
    private static final long ADMIN_USER_ID = 793888025L;
    static String[] commandList = new String[]{"18", "一八"};

    static String[] sarr = {"mario", "donkey_kong", "link", "dark_samus", "samus", "yoshi", "kirby", "fox", "pikachu", "luigi", "ness", "captain_falcon", "jigglypuff", "daisy", "peach", "bowser", "ice_climbers", "sheik", "zelda", "dr_mario", "pichu", "falco", "lucina", "marth", "young_link", "ganondorf", "mewtwo", "chrom", "roy", "gnw", "meta_knight", "dark_pit", "pit", "zss", "wario", "snake", "ike", "squirtle", "ivysaur", "charizard", "diddy_kong", "lucas", "sonic", "king_dedede", "olimar", "lucario", "rob", "toon_link", "wolf", "villager", "mega_man", "wii_fit_trainer", "rosalina", "little_mac", "greninja", "mii_brawler", "mii_sword", "mii_gunner", "palutena", "pac_man", "robin", "shulk", "bowser_jr", "duck_hunt", "ken", "ryu", "cloud", "corrin", "bayonetta", "inkling", "ridley", "richter", "simon", "king_k_rool", "isabelle", "incineroar", "piranha_plant", "joker", "hero", "banjo", "terry", "byleth", "min_min", "steve", "sephiroth", "pyra", "mythra", "kazuya", "sora"};
    static String url = "http://kazuyamishima.com/details/";

    @EventHandler
    private ListeningStatus onEvent(MessageEvent event) {
//        System.out.println(event.getSubject().getId());
        String qqid = String.valueOf(event.getSubject().getId());
        String input = event.getMessage().contentToString().trim();
        if (input.equalsIgnoreCase("vip")) {
            String url = "https://kumamate.net/vip/";
            Document document = null;
            try {
                document = Jsoup.connect(url).get();
            } catch (IOException e) {
//                throw new RuntimeException(e);
                SendError.send("IOException error", event);
            }
//        System.out.println(document.html());
            StringBuilder sb = new StringBuilder();
            for (int i = 2; i <= 17; i++) {
                assert document != null;
                sb.append(document.select("div.RecentMatch > table > tbody > tr:nth-child(" + i + ") > td:nth-child(2)").text()).append("\t").append(document.select("div.RecentMatch > table > tbody > tr:nth-child(" + i + ") > td:nth-child(3)").text()).append("\n");
            }
//        System.out.println(sb.toString());
            sb.append("来源于").append(url);
            event.getSubject().sendMessage(sb.toString());
        }
        if (input.matches(".*群(号)?.*") && !input.matches("群(号)?")) {
            input = input.replaceAll("群(号)?", "").trim();
            String GroupId = GetPlaceGroup(input);
            String name = GetCharId(input);
            if (name != null) {
                GroupId = GetCharacterGroup(name);
            }
            if (qqid.equals(GroupId)) {
                event.getSubject().sendMessage("不许原地tp");
            } else if (GroupId != null && !GroupId.isEmpty()) {
                event.getSubject().sendMessage((name != null ? name : input) + " : " + GroupId);
            }
        } else if (startNumber(commandList, input) != -1 && (qqid.equals(QQ_GROUP_ID) || event.getSender().getId() == ADMIN_USER_ID || qqid.equals("1613341351"))) {
            try {
//                if (ChromeDriver == null) CreateChromeDriver();
                input = replaceCommand(commandList, input.trim());
//                System.out.println(input);

                assert input != null;
                String[] tmpList = input.split(" ");
                if (input.equals("菜单")) {
                    Menu(event);
                }
                if (input.equals("列表") || input.equals("方向") || input.equals("简易") || input.equals("下投") || input.equalsIgnoreCase("横S") || input.equalsIgnoreCase("空n") || input.equalsIgnoreCase("蹲a") || input.equalsIgnoreCase("1a")|| input.equalsIgnoreCase("魔神")) {
                    ImageList(event, input);
                } else if (tmpList.length > 0) {
                    Set<String> nameSet = new LinkedHashSet<>();

                    for (int i = 0; i < tmpList.length; i++) {
                        tmpList[i] = GetSpecialCharId(tmpList[i]);
                    }
                    for (String str : tmpList) {
                        for (String s : str.split("/"))
                            nameSet.add(GetCharId(s));
                    }
//                    System.out.println(nameSet);
                    nameSet.remove(null);
                    if (!nameSet.isEmpty()) {
                        event.getSubject().sendMessage("正在查询" + nameSet);
                        GetAll(nameSet.toArray(new String[]{}), event, CreateChromeDriver());
                    }
                }
            } catch (Exception e) {
                try {
                    int maxInstances = 5; // Maximum number of allowed chromedriver instances
                    int currentInstances = countChromedriverInstances();

                    if (currentInstances > maxInstances) {
                        killExcessChromedriverProcesses(currentInstances - maxInstances);
                    }
                } catch (IOException ex) {
                    // Handle exception
                    ex.printStackTrace();
                }
                SendError.send("Main Exception", event);
            }
        }
        return ListeningStatus.LISTENING;
    }


    public static ChromeDriver CreateChromeDriver() {
        try {
            System.setProperty("webdriver.chrome.driver", "D:/Downloads/chromedriver-win32/chromedriver.exe");
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            options.addArguments("--log-level=OFF");
            options.addArguments("--remote-allow-origins=*");
            options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-logging"));

            ChromeDriver driver = new ChromeDriver(options);
            if (isPortInUse(9000)) {
                System.out.println("driver :9000");
                driver.get("http://127.0.0.1:9000/");
            } else {
                System.out.println("driver .com");
                driver.get("http://kazuyamishima.com/");
            }
//            setDriver(driver);
            return driver;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isPortInUse(int port) {
        try (ServerSocket ignored = new ServerSocket(port)) {
            // If we can create a ServerSocket on the specified port, it means the port is not in use.
            return false;
        } catch (IOException e) {
            // Port is already in use
            return true;
        }
    }

    public static void startLiveServer() {
        try {
            Runtime.getRuntime().exec("cmd /k d: & cd D:\\Downloads\\kazuya.com & live-server --port=9000");
//            process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void GetAll(String[] list, MessageEvent event, ChromeDriver driver) {
//        ChromeDriver driver = getChromeDriver();
        MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
        ForwardMessageBuilder forwardMessageBuilder = new ForwardMessageBuilder(event.getSubject());
        int MaxTotal = 3;
        for (String name : list) {
            driver.get(url + name);

            int numBtn = Integer.parseInt(String.valueOf(((JavascriptExecutor) driver).executeScript("return document.getElementsByClassName('c-fighter-details__body-heading').length")));
            for (int i = 0; i < numBtn; i++)
                ((JavascriptExecutor) driver).executeScript("document.getElementsByClassName('c-fighter-details__body-heading')[" + i + "].outerHTML = document.getElementsByClassName('c-fighter-details__body-heading')[" + i + "].outerHTML.replaceAll('Combo Tree','连招表').replaceAll('EWGF-&gt;Nair-&gt;DGF %s','电风空n魔神真连区间表').replaceAll('Other Information','关于其他')");

            String[] notes = {};
            String note = GetNotes(name);
            if (note != null) notes = note.split("\\|");
//        System.out.println(note);
            int webnotesize = Integer.parseInt(String.valueOf(((JavascriptExecutor) driver).executeScript("return document.getElementsByClassName('c-fighter-details__table-notes').length")));
            StringBuilder replacestr = new StringBuilder();
            for (int i = 0; i < notes.length; i += 2) {
                replacestr.append(".replace(\"").append(notes[i]).append("\".replace(\"NOTES: \",\"\"),\"").append(notes[i + 1]).append("\")");
            }
//	        System.out.println(replace);
            for (int i = 0; i < webnotesize; i++) {
                if (name.equals("samus") || name.equals("dark_samus")) {
                    ((JavascriptExecutor) driver).executeScript("document.querySelector(\"#details-combo-tree > table:nth-child(2) > tbody > tr:nth-child(2) > td:nth-child(2)\").innerHTML ='<i class=\"icon-false text-no\" title=\"No\"></i>'");
                } else if (name.equals("shulk")) {
                    ((JavascriptExecutor) driver).executeScript("document.querySelector(\"body > div.fade.c-fighter-details.modal.show > div > div > div.c-fighter-details__body > button:nth-child(1) > h3\").innerHTML='对局中特定技能 注意事项'\n" +
                            "document.querySelector(\"#details-specific-considerations > h4:nth-child(1)\").innerHTML = '莫纳德-武技'\n" +
                            "document.querySelector(\"#details-specific-considerations > p:nth-child(2)\").innerHTML='由于修尔克能在受击状态下切换武技来脱连 不同的武技对不同的招式效果也不同'\n" +
                            "document.querySelector(\"#details-specific-considerations > p:nth-child(3)\").innerHTML='由于修尔克有这种选项，在大多数情况下\\n'+\n" +
                            "'一八最喜欢看到的是修尔克进行简单的切换，这样可以讲修尔克至于不利的位置，比如简单的电风确认在修尔克没盾的时候也可以生效'\n" +
                            "'由于修尔克能在受击状态下切换武技来脱连 不同的武技对不同的招式效果也不同'\n" +
                            "document.querySelector(\"#details-specific-considerations > p:nth-child(4)\").innerHTML=\n" +
                            "'简单的对策，简单的科普：修尔克不能在被抓住或者被绊倒或埋地的时候使用武技脱连，电风打中他武技状态是第一次电风命中的时候被击中的状态，而不是启动时的状态'\n" +
                            "document.querySelector(\"#details-specific-considerations > h4:nth-child(6)\").innerHTML='对修尔克特定连段'\n" +
                            "document.querySelector(\"#details-specific-considerations > p:nth-child(7)\").innerHTML='如果修尔克在一八的电风中开盾武技，最好的办法就是电风电风电风，你电风的次数取决于修尔克盾buff的持续时间或者修尔克想要关掉盾不给你连之前，你都是可以电风接电风的'\n" +
                            "document.querySelector(\"#details-specific-considerations > h4:nth-child(9)\").innerHTML='电风被对策的情况'\n" +
                            "document.querySelector(\"#details-specific-considerations > p.small.mb-0\").innerHTML='轮盘储存了疾之后，修尔克拥有了惩罚电风的能力，修尔克盾buff切疾buff的瞬间，缓冲了轮盘通过切换武技的无敌帧，修尔克会提前落地获得优势帧，便可以惩罚电风，一八可以反应后用魔神拳读他切换 跟读闪一样。听起来像打宝可梦训练家，一八可以在1a3a后放帧等宝可梦切换 再魔神，打修尔克同理'");
                }
                ((JavascriptExecutor) driver).executeScript("document.getElementsByClassName('c-fighter-details__table-notes')[" + i + "].outerHTML=document.getElementsByClassName('c-fighter-details__table-notes')[" + i + "].outerHTML" + replacestr);
                ((JavascriptExecutor) driver).executeScript("document.getElementsByClassName('c-fighter-details__table-notes')[" + i + "].outerHTML=document.getElementsByClassName('c-fighter-details__table-notes')[" + i + "].outerHTML" + replacestr.toString().replace(". ", ".  "));

                ((JavascriptExecutor) driver).executeScript("document.getElementsByClassName('c-fighter-details__table-notes')[" + i + "].outerHTML=document.getElementsByClassName('c-fighter-details__table-notes')[" + i + "].outerHTML\r\n" + "    .replace(\"NOTES: Utilt is inconsistent\".replace(\"NOTES: \",\"\"),\"上T1不适用\")\r\n" + ".replace(\"NOTES: Too far to hit with buffered electric. Dthrow sends onto the BF plat starting at 3%.\".replace(\"NOTES: \",\"\"),\"太远了 缓冲电风打不到 3%开始，下投会把人摔到板上（小战场）\")\r\n" + ".replace(\"NOTES: Too far to hit with buffered electric\".replace(\"NOTES: \",\"\"),\"太远了 缓冲电风打不到\")\r\n" + ".replace(\"NOTES: Tippers when charged 6-9 frames\".replace(\"NOTES: \",\"\"),\"蓄力6-9帧的时候才能打到甜点\")\r\n" + ".replace(\"NOTES: Tippers when charged 0-3 frames\".replace(\"NOTES: \",\"\"),\"蓄力0-3帧的时候才能打到甜点\")\r\n" + ".replace(\"NOTES: Only hits when up against a ledge. Can charge slightly.\".replace(\"NOTES: \",\"\"),\"只在板边生效 可以稍微蓄力一会\")\r\n" + ".replace(\"NOTES: Only hits when up against a ledge\".replace(\"NOTES: \",\"\"),\"只在板边生效\")\r\n" + ".replace(\"NOTES: Only hits when it tippers. Does not tipper when up against ledge. Tippers when charged 3-8 frames.\".replace(\"NOTES: \",\"\"),\"只在甜点时生效 板边不适用 蓄力3-8帧的时候才能打到甜点\")\r\n" + ".replace(\"NOTES: Only 2-frames if doing up b from as far from ledge as possible\".replace(\"NOTES: \",\"\"),\"非贴边上B才能抓2帧\")\r\n" + ".replace(\"NOTES: If they face away you need to dash for 3-5 frames then EWGF\".replace(\"NOTES: \",\"\"),\"落地受击模型背朝一八时 确保推前3-5帧再电风\")\r\n" + ".replace(\"NOTES: Have a 3 frame window to walk then EWGF\".replace(\"NOTES: \",\"\"),\"确保推前3帧再电风\")\r\n" + ".replace(\"NOTES: Electric whiffs if they face away when they land\".replace(\"NOTES: \",\"\"),\"落地受击模型背朝一八时 电风无法打中\")\r\n" + ".replace(\"NOTES: Dthrow sends onto the BF plat even at 0\".replace(\"NOTES: \",\"\"),\"0%开始，下投会把人摔到板上（小战场）\")\r\n" + ".replace(\"NOTES: Does not work if they face in when they land\".replace(\"NOTES: \",\"\"),\"落地受击模型面朝一八时 电风无法打中\")\r\n" + ".replace(\"NOTES: Does not work if they face away when they land\".replace(\"NOTES: \",\"\"),\"落地受击模型背朝一八时 电风无法打中\")\r\n" + ".replace(\"NOTES: Can SDI in and air dodge away/down and away. Possible to hit with specific spacing.\".replace(\"NOTES: \",\"\"),\"打点不好会被向内sdi后空闪脱连 确保有把握再出手\")\r\n" + ".replace(\"NOTES: Can charge slightly at ledge\".replace(\"NOTES: \",\"\"),\"在板边的话 可以多蓄力一会\")\r\n" + ".replace(\"NOTES: Can charge slightly\".replace(\"NOTES: \",\"\"),\"可以多蓄力一会\")\r\n" + ".replace(\"*Couldn't get SH consistently go for FH almost exclusively\".replace(\"NOTES: \",\"\"),\"小跳难稳定，只用大跳就好\")");
            }
            for (int i = 0; i < webnotesize; i++) {
                String str = (String) ((JavascriptExecutor) driver).executeScript("return  document.getElementsByClassName(\"c-fighter-details__table-notes\")[" + i + "].outerText");
                String regex = "[a-zA-Z]+\\s[a-zA-Z]+";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(str);
                boolean result = matcher.find();
                if (result) {
//                System.out.println(name + "\t" + str);
                    event.getSubject().sendMessage("notes可能异常:\n" + str.replace("NOTES:", ""));
                }
            }
            ((JavascriptExecutor) driver).executeScript("document.getElementsByClassName('c-fighter-details__stats')[0].outerHTML = document.getElementsByClassName('c-fighter-details__stats')[0].outerHTML.replace('weight','体重').replace('Fall Speed','落速').replaceAll('Airdodge','空闪').replace('Fastest Escape','最快脱离')");
            ((JavascriptExecutor) driver).executeScript("document.getElementById('details-combo-tree').outerHTML = document.getElementById('details-combo-tree').outerHTML.replace('Dthrow','下投').replace('EWGF','电风').replace('Tsunami','TSUNAMI(3a)系列').replace('Electric','电风').replace('Utilt','上T1').replace('C.Jab','蹲A').replaceAll('N/A','不适当').replaceAll('Nair','NAIR(空n)系列').replaceAll('Stature Smash','STATURE(1a)系列').replaceAll('Crouch Jab','CROUCH JAB(蹲A)系列').replaceAll('DGF','魔神').replaceAll('Fsmash','横S').replaceAll('Hits?','命中?').replaceAll('Tippers?','甜点?').replace('Works Normally?','是否可行')");
            ((JavascriptExecutor) driver).executeScript("document.getElementById('details-other-info').outerHTML = document.getElementById('details-other-info').outerHTML.replace('EWGF On Block','电风推盾').replaceAll('Advantage','有利帧').replaceAll('Chance to Trip?','推盾摔倒?').replaceAll('Dragon Uppercut','雷神拳选项').replaceAll('Hits Ledge Hang','打挂边').replaceAll('2-Frames','抓2帧').replaceAll('Rapid Jab','百裂/Jab连打').replaceAll('Can Armor?','霸体').replaceAll('Armors Until','护甲')");
            ((JavascriptExecutor) driver).executeScript("document.getElementById('details-ewgf-nair-dgf').outerHTML = document.getElementById('details-ewgf-nair-dgf').outerHTML.replaceAll('Lowest Achieved','最低点空n速降').replaceAll('Consistent Low','上升空n速降(小跳到大跳之间缓冲)').replaceAll('Full Hop Max','最大值满跳空n速降(打头顶)').replaceAll('Short Hop Max','最小值小跳空n速降(打裆部)')");


            String escape = GetEscape(name);
            if (escape != null) {
                ((JavascriptExecutor) driver).executeScript("document.getElementsByClassName('c-fighter-details__stats')[0].children[4].children[1].outerHTML = '<span>" + escape + "</span>'");
            }

            try {
                //qq群
                ((JavascriptExecutor) driver).executeScript("document.getElementsByClassName('c-fighter-details__body')[0].append('欢迎加入一八QQ群:261965114')");
            } catch (Exception e) {
                SendError.send("群号err", event);
            }
            try {
                Image image = Contact.uploadImage(event.getSubject(), WebImg.extracted("//div[@class='modal-content']", driver));
                messageChainBuilder.append(image);
                forwardMessageBuilder.add(event.getBot().getId(), senderName, image);
            } catch (InterruptedException | IOException e) {
                SendError.send("Interrupted|IO Exception", event);
            }
        }
        if (list.length <= MaxTotal) {
            event.getSubject().sendMessage(messageChainBuilder.build().plus(new At(event.getSender().getId())));
        } else {
            ForwardMessage forward = forwardMessageBuilder.build();
            event.getSubject().sendMessage(forward);
        }
        driver.quit();
    }

    public static int startNumber(String[] array, String str) {
        for (int i = 0; i < array.length; i++) {
            if (str.startsWith(array[i])) {
                return i;
            }
        }
        return -1;
    }

    public static String replaceCommand(String[] array, String str) {
        for (String s : array) {
            if (str.startsWith(s)) {
                return str.replaceFirst(s, "").trim();
            }
        }
        return null;
    }

    public static String GetSpecialCharId(String input) {
        LinkedHashMap<String, String> specialId = new LinkedHashMap<String, String>() {{
            put("squirtle/ivysaur/charizard", "pokemontrainer/pt/宝可梦训练家/宝可梦训练师/训练家/训练师");
            put("mythra/pyra", "光焰");
            put("simon/richter", "恶魔城人/恶魔城");
            put("ryu/ken", "街霸人/街霸");
        }};
        for (String key : specialId.keySet()) {
            for (String str : specialId.get(key).split("/")) {
                if (str.equalsIgnoreCase(input)) {
                    return key;
                }
            }
        }
        return input;
    }

    public static String GetCharId(String input) {
        String id = null;
        LinkedHashMap<String, String> charId = new LinkedHashMap<String, String>() {{
            put("banjo", "banjo_and_kazooie/banjo&kazooie/banjoandkazooie/banjokazooie/班卓熊/熊鸟/鸟熊");
            put("bayonetta", "bayonetta/贝优妮塔/贝姐/大长腿");
            put("bowser_jr", "bowser_jr/bowserjr/小库巴/小库霸");
            put("byleth", "byleth/贝雷特/贝雷丝/贝蕾特/贝蕾丝/贝老师");
            put("captain_falcon", "captain_falcon/captainfalcon/飞隼队长/鹰隼");
            put("chrom", "chrom/库洛姆");
            put("cloud", "cloud/云哥/克劳德");
            put("corrin", "corrin/神威/kamui");
            put("daisy", "daisy/黛西公主/黛丝公主/菊花公主");
            put("dark_pit", "dark_pit/darkpit/黑皮特/黑暗皮特");
            put("dark_samus", "dark_samus/darksamus/黑暗萨姆斯/黑萨姆斯/黑萨");


            put("dr_mario", "dr_mario/drmario/马医/蚂蚁/马里奥医生/🐜");
            put("duck_hunt", "duck_hunt/duckhunt/打猎鸭/鸭狗/狗鸭/鸭子");
            put("falco", "falco/老鹰/鹰/阿鹰/鸡");
            put("fox", "fox/狐狸/火狐/光哥");
            put("ganondorf", "ganondorf/盖农多夫/盖侬多夫/加农道夫");
            put("greninja", "greninja/甲贺忍蛙");
            put("ice_climbers", "ice_climbers/iceclimbers/冰山翻越者/冰山攀登者/雪人兄弟/雪人兄妹/冰人");
            put("ike", "ike/艾克/苍炎勇者");
            put("incineroar", "incineroar/炽焰咆啸虎/炽焰咆哮虎/老虎/宇宙最强");


            put("jigglypuff", "jigglypuff/胖丁/粉气球");
            put("joker", "joker/雨宫莲/小丑/周可儿");
            put("ken", "ken/肯");
            put("king_dedede", "king_dedede/kingdedede/ddd大王/企鹅");
            put("king_k_rool", "king_k_rool/kingkrool/鳄鱼王/库鲁鲁王");
            put("kirby", "kirby/卡比/粉团子");
            put("little_mac", "little_mac/littlemac/小麦克");
            put("lucario", "lucario/路卡利欧");
            put("lucas", "lucas/卢卡斯/特朗普");
            put("lucina", "lucina/露琪娜");
            put("mario", "mario/马力欧/马力奥/马利欧/马利奥/马里奥");
            put("marth", "marth/马尔斯/甜点王子/英雄王");
            put("luigi", "luigi/路易吉/路老二/路易基");
            put("mega_man", "mega_man/megaman/洛克人");
            put("meta_knight", "meta_knight/metaknight/梅塔骑士/魅塔骑士");
            put("mewtwo", "mewtwo/超梦");
            put("mii_brawler", "mii_brawler/miibrawler/拳击mii/mii拳击/拳mii/mii拳");
            put("mii_gunner", "mii_gunner/miigunner/枪mii/mii枪");
            put("mii_sword", "mii_swordfighter/miiswordfighter/剑mii/mii剑");
            put("min_min", "minmin/面面");
            put("gnw", "gnw/mrgameandwatch/mrgame&watch/小黑人/代码人/gw/gamewatch");
            put("mythra", "mythra/光/光焰");
            put("ness", "ness/奈斯/naisi");
            put("olimar", "olimar/欧力马/欧利马/奥力马/奥利马/欧力玛/欧利玛/奥力玛/奥利玛/皮克敏");
            put("pac_man", "pac_man/pacman/吃豆人");
            put("palutena", "palutena/帕鲁提娜/帕露提娜/帕鲁蒂娜/帕露蒂娜/女神");
            put("peach", "peach/桃花公主/碧琪公主/桃子公主");
            put("pichu", "pichu/皮丘");
            put("pikachu", "pikachu/皮卡丘");
            put("piranha_plant", "piranha_plant/piranhaplant/食人花/吞噬花");
            put("pit", "pit/白皮特");

            put("charizard", "charizard/喷火龙");
            put("ivysaur", "ivysaur/妙蛙草/蒜头王八");
            put("squirtle", "squirtle/杰尼龟/王八/pt");

            put("pyra", "pyra/焰/吼姆拉");

            put("ridley", "ridley/利德雷/翼龙");

            put("robin", "robin/军师/鲁弗莱");
            put("rosalina", "rosalina_and_luma/rosalinaandluma/rosalina&luma/罗莎塔/银河公主/琪琪/罗泽塔/罗妈");
            put("roy", "roy/罗伊");
            put("ryu", "ryu/隆");
            put("sephiroth", "sephiroth/萨菲罗斯/片翼");
            put("sheik", "sheik/希克");
            put("shulk", "shulk/修尔克/秀尔克");
            put("simon", "simon/西蒙/恶魔城人");
            put("snake", "snake/斯内克/史内克/蛇叔");
            put("sonic", "sonic/索尼克/蓝刺猬");
            put("sora", "sora/索拉");
            put("steve", "steve/史蒂夫/mc/minecraft/西瓜");
            put("terry", "terry/特瑞");
            put("toon_link", "toon_link/卡通林克/通林");
            put("villager", "villager/村民/岛民");
            put("wario", "wario/瓦力欧/瓦力奥/瓦利欧/瓦利奥/瓦里奥");
            put("wii_fit_trainer", "wii_fit_trainer/wiifittrainer/wiitrainer/wii教练/瑜伽教练/云佬");
            put("wolf", "wolf/狼狗");
            put("yoshi", "yoshi/耀西/小恐龙/绿色恐龙");
            put("young_link", "young_link/年轻林克/杨林/younglink");
            put("zelda", "zelda/塞尔达");
            put("zss", "zero_suit_samus/zerosuitsamus/zerosamus/零装甲萨姆斯/零萨/zss");
            put("bowser", "bowser/库巴/库霸王/酷霸王");
            put("link", "link/大林克/野炊");
            put("inkling", "inkling/乌贼娘/墨灵/喷喷/墨鱼/鱿鱼");
            put("samus", "samus/萨姆斯");
            put("hero", "hero/dq/勇者");
            put("rob", "rob/机器人/萝卜");
            put("richter", "richter/里希特/李猴子");
            put("diddy_kong", "diddy_kong/diddykong/ddk/迪迪刚/小猴子");
            put("donkey_kong", "donkey_kong/donkeykong/dkk/森喜刚/大金刚/大猩猩/咚奇刚");
            put("isabelle", "isabelle/西施慧/西施惠/小母狗");
            put("kazuya", "kazuya/三岛一八/18/红眼恶魔");
        }};
        for (String key : charId.keySet()) {
//            System.out.println(charId.get(key));
            if (charId.get(key).contains(input.toLowerCase())) {
                id = key;
            }
//            System.out.println(key);
        }
        return id;
    }


    public static String GetNotes(String name) {
        String notes = null;
        LinkedHashMap<String, String> allnotes = new LinkedHashMap<String, String>() {
            /**
             *
             */
            private static final long serialVersionUID = -2584586580903036518L;

            {
                put("mario", "NOTES: EWGF must be buffered|必须是缓冲电风");
                put("donkey_kong", "NOTES: Tippers when charged 6-9 frames|需要蓄力6-9帧才能打到甜点|NOTES: DK's Ledge Grab animation varies greatly on how he Up-B's to ledge.|打大猩猩的抓边动画取决于他上B回来的角度");
                put("link", "NOTES: Can SDI in and air dodge away/down and away. Possible to hit with specific spacing.|打点不好会被向内sdi后空闪脱连 确保有把握再出手|NOTES: Can charge slightly|可以稍微蓄力一会|NOTES: Only 2-frames if Link recovers straight up; will either trade or go through if they opt to hold down to not grab ledge|垂直上B的时候可以相杀来抓2帧 如果林克不选择直接抓边还是可以用雷神拳轰死他");
                put("dark_samus", "NOTES: Can SDI in and air dodge away/down and away. Possible to hit with specific spacing.|可被准备空闪的姿势躲开");
                put("samus", "NOTES: Can SDI in and air dodge away/down and away. Possible to hit with specific spacing.|可被准备空闪的姿势躲开");
                put("yoshi", "NOTES: Dthrow sends onto the BF plat even at 0|0%开始，下投会把人摔到板上（小战场）|NOTES: Electric whiffs if they face away when they land|落地受击模型背朝一八时 电风无法打中|NOTES: Does not work if they face away when they land|落地受击模型背朝一八时没法打中|NOTES: There's a small window in Yoshi's ledge hang animation where they can't be hit|有一个很小的窗口可以使耀西不被抓2帧|*Couldn't get SH consistently go for FH almost exclusively|小跳难稳定，只用大跳就好");
                put("kirby", "NOTES: Too far to hit with buffered electric starting at 8%. Dthrow sends onto the BF plat even at 0.|太远了 缓冲电风在8%的时候不生效 0%开始，下投会把人摔到板上（小战场）|NOTES: Utilt and Crouch Jab are inconsistent|上T1和蹲A不适用");
                put("fox", "NOTES: 2-Frames both Up- and Side-B. Only safe Up-B angle is stage hug.|非贴边的上B可以被抓2帧 火狐横B回场也可以抓2帧");
                put("pikachu", "NOTES: Dthrow sends onto the BF plat starting at 9%|9%开始，下投会把人摔到板上（小战场）|NOTES: Electric, Utilt, and Crouch Jab all inconsistent|电风 上T1 蹲A都不适用|NOTES: EWGF must be buffered. If Kazuya is on the left, facing right, and Pikachu is facing away, then EWGF will whiff|电风必须是缓冲的 落地受击模型背朝一八时 电风无法打中|NOTES: Does not work if they face in when they land|落地受击模型面朝一八时 电风无法打中|NOTES: Can charge slightly. Only tippers when up against a ledge and charged 6-9 frames.|蓄力6-9帧的时候才能打到甜点|NOTES: Can't be 2-framed with Side-B, or when doing Up-B horizontally.|横向上B和横B都不被抓2帧");
                put("luigi", "NOTES: Can charge slightly at ledge|可以稍微蓄力一会|NOTES: Might be possible to 2-frame on very specific Up-B ledge snap distances, but seems very unlikely.|有特定的距离才能偶尔抓上B的2帧 还是默认抓不了罢");
                put("ness", "NOTES: Too far to hit with buffered electric. Dthrow sends onto the BF plat starting at 3%.|太远了 缓冲电风打不中 3%开始，下投会把人摔到板上（小战场）|NOTES: Tippers when charged 0-3 frames|蓄力0-3帧的时候才能打到甜点|NOTES: Some angles going straight up or straight to the side can't be 2-framed; hyperspecific.|贴边回场和垂直上B不能被2帧");
                put("captain_falcon", "NOTES: Phantom sparks ledge hang unless you're exactly 1 extra pixel away from ledge. 2-Frame possible only if Falcon grabs ledge from an ultraspecific distance that results in his knuckle just barely touching the ledge in a way that Kazuya's left big toe grazes him slightly.|不要贴着板边抓2帧 飞隼的横B特效带判定的 飞隼的上B可以往后拉来挂边 看情况抓2帧");
                put("jigglypuff", "NOTES: Can SDI in and air dodge away/down and away. Possible to hit with specific spacing. Rest technically avoids the electric, but also whiffs leaving Puff vulnerable.|打点不好会被向内sdi后空闪脱连 确保有把握再出手  胖丁下B虽然是1帧脱连 但是电风的无敌帧刚好躲过|NOTES: Also goes through pound at ledge, unless they do it too far below stage to not hit you|当然胖丁可以选择横B抓边 或者低位横B 都打不中你");
                put("daisy", "NOTES: If they face away you need to dash for 3-5 frames then EWGF|如果菊花公主背对你确保推前3-5帧再电风|NOTES: Can 2-Frame Side-B, but not Up-B|抓不了上B的2帧 如果菊花公主选择横B挂边 可以抓2帧");
                put("ice_climbers", "NOTES: Partner can interrupt|在你连的时候 另外一个雪人可以揍你|NOTES: Tippers when charged 5-7 frames|蓄力5-7帧的时候才能打到甜点|NOTES: Nana usually dodges via Z Axis during ledge hang. Can only 2-frame the solo climber Up-B.|只能抓单雪人的上B挂边2帧 两个雪人都在的话其中一个会闪回来");
                put("sheik", "NOTES: If they face away you need to dash for 3-6 frames then EWGF|如果希克背对你确保推前2-6帧再电风|NOTES: Doesn't get 2 framed recovering straight up|贴边上B不会被2帧");
                put("zelda", "NOTES: Only hits when up against a ledge. Can charge slightly.|只在板边生效 可以稍微蓄力一会|NOTES: Pixel perfect positioning needed to hit ledge hang.|需要贴边用雷神拳才能抓2帧");
                put("dr_mario", "NOTES: EWGF must be buffered. Utilt only hits from the right.|电风必须是缓冲的 上T1接电风只在右边适用");
                put("pichu", "NOTES: Too far to hit with buffered electric. Dthrow sends onto the BF plat even at 0.|太远了缓冲电风打不到 0%开始，下投会把人摔到板上（小战场）|NOTES: Very easy|非常轻松 随便用");
                put("young_link", "NOTES: 2-Frame possibility depends on the animation frame of Up-B during ledge snap. Can trade with Uppercut.|取决于杨林上B从哪个角度抓边 垂直上B可以被相杀");
                put("mewtwo", "NOTES: Must dash electric, cannot buffer. Utilt hits if timed instead of buffered, but can still whiff at max range.|确保推前再电风，不能缓冲。上T1必须目押，不能缓冲，但仍然可以在最大范围内打中|NOTES: Tippers when charged 1-3 Frames or 0-9 frames when up against a ledge|蓄力1-3帧的时候才能打到甜点 板边的话0-9帧都可以|NOTES: Doesn't 2-frame vs max distance vertical teleport.|极限上B传送挂边不能被抓2帧");
                put("chrom", "NOTES: Pixel perfect positioning needed to hit ledge hang. Likely to trade when going for 2-frame.|需要贴近板边 和库洛姆上B相杀抓2帧");
                put("meta_knight", "NOTES: Side-B never gets 2-framed, and Up-B is animation frame dependant.|没办法抓魅塔横B抓边2帧 看第二刀的动画来抓上B的2帧");
                put("zss", "NOTES: 2-frames Zair/Side-B and Down-B. Whiffs vs Up-B when she hugs the stage.|锁链和下B可以被抓 垂直上B可以尝试抓");
                put("wario", "NOTES: Does not work on the left side if facing in|落地受击模型面朝一八时 电风无法打中");
                put("snake", "NOTES: You can 2 frame airdodge if he goes straight up. Just Down Smash instead.|蛇一般上B向上空闪回来 用下S就行");
                put("ike", "NOTES: Can't 2 frame, but you can Dragon Uppercut trade with aether.|没办法抓2帧 但是可以过去硬碰硬");
                put("squirtle", "NOTES: Electric and Utilt can whiff if Squirtle lands facing away|落地受击模型背朝一八时 电风和上T1无法打中");
                put("charizard", "NOTES: Electric and Utilt can whiff if Charizard lands facing away|落地受击模型背朝一八时 电风和上T1无法打中|NOTES: Doesn't 2-frame if they Up-B straight up. Flare Blitz can be 2 framed. Can whiff vs ledge hang if their head is down.|垂直上B没办法被抓2帧");
                put("diddy_kong", "NOTES: Too far to hit with buffered electric starting at 1%|缓冲电风在1%的时候就连不上了|NOTES: Does not work on the right side if they land facing away unless you dash for 5 frames|如果迪迪刚落地背朝你时 确保推前5帧再电风|NOTES: Only tippers when up against a ledge and charged 4-6 frames|只在板边生效 蓄力4-6帧打到甜点");
                put("lucas", "NOTES: Can't be 2-framed when Up-B sends at a diagonal (No stage hug). Zair can't be 2 framed.|锁链回场可以被抓2帧 贴边上B不能");
                put("sonic", "NOTES: Too far to hit with buffered electric|太远了 缓冲电风打不到|NOTES: Only hits when it tippers. When up against a ledge it does not tipper. Tippers when charged 3-6 frames.|只在板边生效 需要蓄力3-6帧打到甜点");
                put("lucario", "NOTES: Electric and Utilt can whiff if Lucario lands facing away|当路卡背朝你的时候 上T1和电风不适用|NOTES: Stage hug Up-B can sometimes avoid being 2-frameable|路卡的上B根据不同的位置抓边可以避免被抓2帧");
                put("rob", "NOTES: Only hits ledge hang while they're in the animation cycle with their head raised. Only 2-frames on stage hug. Uppercut also goes through/trades with Rob Up Air.|贴近板边雷神可以打机器人脱手空上 抓2帧不稳定");
                put("toon_link", "NOTES: Electric and Utilt can whiff if Toon Link lands facing away|当通林背朝你的时候 上T1和电风不适用|NOTES: If they face away you need to dash for 4-5 frames then EWGF|如果通林背朝你 确保推前4-5帧再电风|NOTES: Only hits when it tippers. Tippers when charged 0 frames. Also hits when up against a ledge.|只在甜点生效 不需要蓄力直接横S|NOTES: Only 2 frames if Toon Link recovers straight up; will either trade or go through if they opt to hold down to not grab ledge. Zair can't be 2-framed.|锁链回场不能被抓2帧 如果直接上B抓边不控距虚空抓边可以被抓2帧");
                put("wolf", "NOTES: Electric whiffs if Wolf lands facing in. Utilt can whiff if Wolf lands facing in.|落地受击模型面朝一八时 电风无法打中 上T1会挥空|NOTES: Have a 3 frame window to walk then EWGF|确保推前3帧再电风（千万不要推多 最好完美3帧）|NOTES: Uppercut can go through and/or trade with Side-B if it doesn't ledge snap.|贴边雷神拳可以和狼横B相杀");
                put("villager", "NOTES: Too far to hit with buffered electric. Dthrow sends onto the plat starting at 8%.|太远了 缓冲电风打不到 8%开始，下投会把人摔到板上（小战场）|NOTES: Can SDI in and air dodge away/down and away. Possible to hit with specific spacing. Lloid Rocket technically comes out frame 1, but Kazuya can still convert without being hit.|打点不好会被向内sdi后空闪脱连 确保有把握再出手 村民横B可以在第一帧出现动画 但是不影响你连他|NOTES: Electric and Utilt whiff if Villager lands facing away|落地受击模型背朝一八时 电风和上T1无法打中|NOTES: Only hits when up against a ledge|只在板边生效");
                put("mega_man", "NOTES: Have a 3 frame window to walk then EWGF. Utilt is inconsistent.|确保推前3帧再电风 上T1不适用|NOTES: Doesn't get 2-framed when recovering from far away.|从远处上B虚空挂边时 不会被抓2帧");
                put("wii_fit_trainer", "NOTES: Can SDI in and air dodge away/down and away. Possible to hit with specific spacing. Header technically comes out frame 1, but Kazuya can still convert.|打点不好会被向内sdi后空闪脱连 确保有把握再出手 教练横B可以在第一帧出现动画 但是不影响你连他|NOTES: Utilt must be slightly delayed|稍微等一会再上T1 教练动画判定会变高|NOTES: Does not tipper at ledge|板边时 甜点不生效|NOTES: Only gets hit during ledge hang while at the top of their pull up. Only gets 2-framed if grabbing ledge with Side-B.|教练用横B抓边会被抓2帧 上B垂直也会");
                put("rosalina", "NOTES: Partner can interrupt. Have a 3 frame window to walk then EWGF|星星会被打断 确保推前3帧再电风|NOTES: Only hits when it tippers. Tippers when charged 0-2 frames or 1-2 frames when up against a ledge.|只在甜点生效 需要蓄力0-2帧打到甜点 板边时蓄力1-2帧|NOTES: Cannot armor Luma's rapid jab|对罗莎塔的百裂没有护甲抗性 根本动不了");
                put("little_mac", "NOTES: Going for 2-frame can go though/trade with both Mac Side-B and Up-B.|小麦克的横B和上B抓边都可以被抓2帧");
                put("greninja", "NOTES: Utilt whiffs if Greninja lands facing away|背朝一八时 上T1不适用|NOTES: Only hits when it tippers. Tippers when up against a ledge and charged 0 frames.|只在甜点生效  不用蓄力直接打");
                put("mii_brawler", "NOTES: Feint Jump technically comes out frame 2 but the lingering hit of electric connects when its intangibility runs out.|伪零萨踢会在第2帧出现 但是无形帧没办法躲开|NOTES: Side-B 3 is the only special that can't be 2-framed.|是第三种横B配置不能被2帧 我没玩过mii 不太懂");
                put("mii_sword", "NOTES: Up-B 2, Side B-2, and Down-B 2 can't be 2-framed. Side-B 1 can avoid being 2-framed if done from far away. Up-B 3 can only be 2-framed if recovering from a diagonal far from ledge.|Mii剑的第二种上b，第二种横B和第二种下B不能被抓2帧数 第三种上B可以被抓2帧");
                put("mii_gunner", "NOTES: Up-B 1/3 can avoid 2-frame when recovering from as far from ledge as possible. Up-B 3 can dodge 2-frame by doing a stage hug.|贴边的第一和第三种上B不能被抓2帧 但是第三种可以被贴边雷神拳相杀");
                put("palutena", "NOTES: Only tippers when up against a ledge and charged 0 frames|只在板边会打到甜点 不需要蓄力");
                put("pac_man", "NOTES: Trampoline technically comes out frame 1, but gets deleted by electric|吃豆人上B动画会从第1帧出现 不影响你连他");
                put("robin", "NOTES: Due to a bug, Robin cannot be tripped if they use a fully charged neutral B|如果军师使用过雷4 不能用1a绊倒他 这是bug|NOTES: I don't think they can be 2-framed by uppercut, but there might be an ultra specific recovery angle that allows for it. Not worth going for.|洗洗睡吧 抓军师2帧不太可能");
                put("shulk", "NOTES: Always goes through and hits/trades with Shulk Up-B if they don't ledge snap. If they snap ledge from far away without doing the second hit of Up-B, they don't get 2-framed.|如果贴板边雷神可以和他上B相杀 如果他们从远处上B虚空挂边就抓不到2帧");
                put("bowser_jr", "NOTES: Tippers when charged 2-9 frames on the left side and 8-9 frames on the right side|左边板边蓄力2-9帧才能打到甜点 右边板边蓄力8-9帧|NOTES: Hits top half of body. Only gets 2-framed if they hug the stage with Up-B.|只能在上b抓边的时候可以被抓2帧.");
                put("duck_hunt", "NOTES: Can 2 frame air dodge.|上B加空闪回来没办法抓2帧");
                put("ken", "NOTES: Will do less damage due to focus|可以给与下B当身伤害 惩罚当身后续|NOTES: Trades with Shoryu when it hits through ledge.|可以和肯的升龙拳相杀");
                put("cloud", "NOTES: Only 2-frames Limit Up-B|只能抓limit(蓝光上B)2帧");
                put("corrin", "NOTES: If they face in you need to dash for 3-5 frames then EWGF|如果他面朝你 确保推前3-5帧再电风");
                put("bayonetta", "NOTES: Very specific timings for Bayo to be stuck in 2-frame animations at all, much more likely that they'll be hit during Up-B.|贝姐的上升上B可以打中板边 抓贝姐的2帧比较玄学");
                put("inkling", "NOTES: Electric whiffs if Inkling lands facing in|受击模型面朝一八时 电风无法打中|NOTES: Sometimes can't be 2 framed during stage hug.|偶尔不能被抓2帧 比较玄学");
                put("ridley", "NOTES: Tippers when charged 7-9 frames on the left side and 6-9 frames on the right side|左边板边蓄力7-9帧才能打到甜点 右边板边蓄力6-9帧|NOTES: Only 2-framed when doing side b very close to ledge.|只能抓利德雷横B抓边的2帧");
                put("richter", "NOTES: Only hits when it tippers. Does not tipper when up against ledge. Tippers when charged 3-8 frames.|只在甜点时生效 板边不适用 需要蓄力3-8帧打到甜点|NOTES: Only 2-frames tether. Can trade/go through Up-B.|和里希特上B相杀可以打到2帧");
                put("isabelle", "NOTES: Too far to hit with buffered electric starting at 6%. Dthrow sends onto the BF plat even at 0.|太远了 缓冲电风再6%开始不生效 0%开始，下投会把人摔到板上（小战场）|NOTES: Electric whiffs if Isabelle lands facing in|落地受击模型面朝一八时 电风无法打中");
                put("incineroar", "NOTES: Tippers when charged 4-5 frames on the left side and 3-4 frames on the right side|左边板边蓄力4-5帧才能打到甜点 右边板边蓄力3-4帧|NOTES: Only 2-frames Side-B at an ultra specific height value only while doing the move super close to ledge. More likely to hit Side-B outright. Maybe possible to hit ledge hang.|锁链回场可以抓2帧 上B的2帧很难抓");
                put("piranha_plant", "NOTES: Dthrow sends onto the BF plat starting at 11%.|11%开始，下投会把人摔到板上（小战场）|NOTES: Tippers when charged 0-1 frames|需要蓄力0-1帧打到甜点");
                put("joker", "NOTES: Only hits ledge hang during Joker's pullup animation. Can only 2-frame Arsene Up-B and can't if Arsene flies straight up.|只有锁链收回来的时候可以抓2帧 亚森的上B露头可以被抓2帧 贴边上B回来的不行");
                put("hero", "NOTES: Small chance to whiff vs ledge hang if you're too close. Animation specific.|贴边雷神拳更有机会抓到2帧");
                put("banjo", "NOTES: Up-b technically comes out Frame 1, but does not affect conversion|班卓的上B动画会在第1帧出来 不影响连段|NOTES: Only hits when it tippers. Does not tipper when up against ledge. Tippers when charged 4-9 frames.|只在甜点时生效 板边不适用 需要蓄力4-9帧打到甜点|NOTES: Only hits ledge hang at the top of Banjo's pullup. Wonderwing only gets 2-framed if Banjo smacks the wall before grabbing the ledge.|横B在抓边前可以被抓2帧 垂直上B可以被抓2帧 虽然班卓通常都闪回来");
                put("terry", "NOTES: Although none of his specials directly snap ledge from below, they'll all lose out to dragon uppercut, barring moves with intangibility frames.|特瑞上B会漏出来很多 雷神拳直接轰就行");
                put("byleth", "NOTES: Only hits when charged 2-5 frames or 0-9 frames when up against a ledge|蓄力2-5帧可以打到甜点 板边需要蓄力0-9帧");
                put("min_min", "NOTES: Only hits when charged 0-1 frames|蓄力0-1帧即可");
                put("steve", "NOTES: Too far to hit with buffered electric starting at 5%|太远了 从5%开始缓冲电风打不中|NOTES: Tippers when charged 1-4 frames on the left side and 0-4 frames on the right side|左边板边蓄力1-4帧才能打到甜点 右边板边蓄力0-4帧");
                put("sephiroth", "NOTES: Too far to hit with buffered electric starting at 9%|太远了 从9%开始缓冲电风打不中");
                put("kazuya", "NOTES: Electric can whiff if Kazuya turns around to face away during hard landing lag|落地受击判定背对我方时 电风无法打中|NOTES: Doesn't get 2-framed when grabbing ledge from as far as possible (Both Up- and Side-B).|从远的地方用上B或者横B虚空抓边 可以避免被抓2帧");

                put("simon", "NOTES: Only 2-frames tether. Can trade/go through Up-B.|和西蒙上B相杀可以打到2帧");
                put("ryu", "NOTES: Will do less damage due to focus|可以给与下B当身伤害 惩罚当身后续|NOTES: Trades with Shoryu when it hits through ledge.|可以和隆的升龙拳相杀");
                put("peach", "NOTES: If they face away you need to dash for 3-5 frames then EWGF|如果桃子公主背对你确保推前3-5帧再电风|NOTES: Can 2-Frame Side-B, but not Up-B|抓不了上B的2帧 如果桃子公主选择横B挂边 可以抓2帧");
                put("marth", "NOTES: Pixel perfect positioning needed to hit ledge hang. 2-Frame depends on Up-B Distance.  Can trade with Up-B as well.|需要贴近板边 和马尔斯上B相杀抓2帧");
                put("lucina", "NOTES: Pixel perfect positioning needed to hit ledge hang. 2-Frame depends on Up-B Distance.  Can trade with Up-B as well.|需要贴近板边 和露琪娜上B相杀抓2帧");
                put("roy", "NOTES: Pixel perfect positioning needed to hit ledge hang. Likely to trade when going for 2-frame.|需要贴近板边 和罗伊上B相杀抓2帧");

                put("olimar", "NOTES: Electric and Utilt can whiff if Olimar lands facing away|当欧力马背朝你的时候 上T1和电风不适用|NOTES: Tippers when charged 0-3 frames. Does not tipper at ledge. Can charge slightly at ledge.|蓄力0-3帧的时候才能打到甜点|NOTES: Up-B -&gt; ledge snap angle for 2-frame is hyperspecific.|偶尔能中 不稳定");
                put("gnw", "NOTES: Dthrow sends onto the BF plat starting at 17%|17%开始，下投会把人摔到板上（小战场）|NOTES: Need to dash exactly 3 frames then EWGF|确保推前3帧再电风（千万不要推多 最好完美3帧）|NOTES: Doesn't 2-frame when G&amp;W recovers at a diagonal|贴边上B抓边的时候没办法抓2帧");

            }
        };
        for (String key : allnotes.keySet()) {
            if (key.equals(name)) notes = allnotes.get(key);
        }
        return notes;
    }

    public static String GetEscape(String name) {
        String escape = null;
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>() {
            /**
             *
             */
            private static final long serialVersionUID = -2584586580903036518L;

            {
                put("yoshi", "(二段跳)");
                put("luigi", "(路易吉旋风下B)");
                put("jigglypuff", "(睡觉砸人下B)");
                put("lucina", "(海豚斩上B)");
                put("marth", "(海豚斩上B)");
                put("snake", "(拔雷普B)");
                put("squirtle", "(宝可梦交替下B)");
                put("ivysaur", "(宝可梦交替下B)");
                put("charizard", "(宝可梦交替下B)");
                put("olimar", "(口哨下B)");
                put("villager", "(火箭横B)");
                put("wii_fit_trainer", "(排球横B)");
                put("little_mac", "(上B)");
                put("mii_brawler", "(仿零萨下B)");
                put("pac_man", "(蹦床上B)");
                put("duck_hunt", "(罐头普B)");
                put("ken", "(当身下B)");
                put("ryu", "(当身下B)");
                put("bayonetta", "(蝙蝠闪)");
                put("banjo", "(上B)");
                put("mythra", "(因果律)");
                put("robin", "(丢书)");
            }
        };
        for (String key : map.keySet()) {
            if (key.equals(name)) escape = map.get(key);
        }
        return escape;
    }

    public static String GetCharacterGroup(String name) {
        String group = null;
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>() {
            /**
             *
             */
            private static final long serialVersionUID = -2584586580903036518L;

            {
                put("910742370", "pac_man");
                put("668429447", "dark_pit/pit");
                put("789230769", "mii_brawler/mii_sword/mii_gunner");

                put("965890960", "donkey_kong");
                put("44849796", "dark_samus/samus");
                put("892477253", "yoshi");
                put("693213805", "kirby");
                put("934359739", "fox");
                put("309517633", "pikachu/pichu");
                put("331910424", "luigi");
                put("739033553", "ness/lucas");
                put("1040237002", "captain_falcon");
                put("691608173", "jigglypuff");
                put("1003505057", "daisy/peach");
                put("995265098", "ice_climbers");
                put("712849231", "sheik");
                put("206265586", "zelda");
                put("216382607", "dr_mario");
                put("877268671", "falco");
                put("374444534", "marth /lucina");
                put("702472112", "young_link");
                put("761680017", "ganondorf");
                put("1009210193", "mewtwo");
                put("718644675", "chrom/roy");
                put("932617414", "gnw");
                put("913326650", "meta_knight");
                put("728731645", "zss");
                put("459688861", "wario");
                put("532875316", "snake");
                put("611657067", "ike");
                put("1061419420", "squirtle/ivysaur/charizard");
                put("746599849", "diddy_kong");
                put("1003554541", "sonic");
                put("656168440", "king_dedede");
                put("862621550", "olimar");
                put("1007101846", "lucario");
                put("722081525", "wolf");
                put("790453692", "villager");
                put("156315760", "mega_man");
                put("677939829", "wii_fit_trainer");
                put("346644024", "rosalina");
                put("959276327", "little_mac");
                put("935645151", "greninja");
                put("522628269", "palutena");
                put("856874501", "robin");
                put("762994183", "rob");
                put("629566304", "shulk");
                put("微信群，管理员Q1053036568", "bowser_jr");
                put("700724670", "duck_hunt");
                put("23438098", "ryu/ken");
                put("791150931", "cloud");
                put("963784131", "corrin");
                put("852588156", "bayonetta");
                put("726637286", "inkling");
                put("739089996", "ridley");
                put("565242057", "simon/richter");
                put("1056144568", "king_k_rool");
                put("711311747", "isabelle");
                put("494626229", "incineroar");
                put("1030780768", "piranha_plant");
                put("301238706", "joker");
                put("468805906", "hero");
                put("979861354", "banjo");
                put("476933736", "terry");
                put("689681426", "byleth");
                put("789399185", "min_min");
                put("864678461", "steve");
                put("832407366", "sephiroth");
                put("556522192", "pyra/mythra");
                put("261965114", "kazuya");
                put("830884666", "sora");

                put("711230644", "mario");
                put("607531104", "link/toon_link");
                put("651319098", "bowser");


            }
        };
        for (String key : map.keySet()) {
            if (map.get(key).contains(name.toLowerCase())) {
                group = key;
//                group = map.get(key)+" : "+key;
            }
        }
        return group;
    }

    public static String GetPlaceGroup(String input) {
        StringBuilder group = new StringBuilder();
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>() {
            /**
             *
             */
            private static final long serialVersionUID = -2584586580903036518L;

            {
                put("湖北武汉", "974488579");
                put("湖南长沙", "750989552");
                put("河南郑州", "1061190117");
                put("河北石家庄", "947754844");
                put("山东济南", "814380074");
                put("山西太原", "868674835");
                put("辽宁沈阳", "653745141");
                put("吉林长春", "908158005");
                put("黑龙江哈尔滨", "");
                put("江西南昌", "1020445376");
                put("江苏南京", "1170272817");
                put("浙江杭州", "967431986");
                put("安徽合肥", "1156569477");
                put("福建福州", "533945014");
                put("四川成都", "897411161");
                put("云南昆明", "610717346");
                put("贵州贵阳", "742457225");
                put("青海西宁", "");
                put("甘肃兰州", "");
                put("陕西西安", "261281526");
                put("海南海口", "");
                put("台湾台北", "");
                put("广东广州", "870289602");
                put("广西南宁", "753198742");
                put("西藏拉萨", "");
                put("宁夏银川", "");
                put("内蒙古呼和浩特", "");
                put("新疆乌鲁木齐", "893138921");
                put("北京", "微信好友 koshima5");
                put("天津", "863869001");
                put("上海", "942719408");
                put("重庆", "27810936");

                put("辽宁大连", "707453848");
                put("江西赣州", "953885189");
                put("江苏常州", "675535752");
                put("浙江温州", "936429909");
                put("广东深圳", "433435866");
                put("江苏苏州", "731583899");
                put("浙江宁波", "761258230");
                put("吉林市", "736226148");


                put("大师", "875979025");
                put("马国", "175830106");
                put("玩蛋", "739105055");
            }
        };
        for (String key : map.keySet()) {
            if (key.contains(input.toLowerCase())) {
//                group = key;
                group.append("\n").append(key).append("\t").append(map.get(key));
            }
        }
        return group.toString();
    }

    public static void ImageList(MessageEvent event, String string) {
        Image image = null;
        String image_id = null;
        switch (string) {
            case "列表":
                image_id = "20230821";
                break;
            case "方向":
                image_id = "numpad";
                break;
            case "简易":
                image_id = "merge_from_ofoct";
                break;
            case "下投":
                image_id = "down_throw";
                break;
            case "横s":
            case "横S":
                image_id = "f_smash";
                break;
            case "空n":
            case "空N":
                image_id = "air_n";
                break;
            case "蹲a":
            case "蹲A":
                image_id = "crouch_jab";
                break;
            case "1a":
            case "1A":
                image_id = "1a";
                break;
            case "魔神":
                image_id = "demon_god_fist";
                break;
        }
        try {
            image = Contact.uploadImage(event.getSubject(), new URL("https://captainunhappy.github.io/input/" + image_id + ".png").openConnection().getInputStream());
        } catch (IOException e) {
//            throw new RuntimeException(e);
            SendError.send(e, event);
        }
        assert image != null;
        event.getSubject().sendMessage(image.plus(new At(event.getSender().getId())));
    }

    private static void Menu(MessageEvent event) {
//        String qq_id = String.valueOf(event.getSender().getId());
        MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
        messageChainBuilder.append("菜单:" + "\n" + "前缀为 18 或 一八 " + "\n" + "以下内容为 <前缀>+[指令]" + "\n" + "如 18/一八 菜单" +
                "\n " + "-列表" +
                "\n " + "-(角色名 使用[列表]中的可用值)" +
                "\n " + "-方向" +
                "\n " + "-简易" +
                "\n " + "-下投" +
                "\n " + "-横s" +
                "\n " + "-空n" +
                "\n " + "-蹲a" +
                "\n " + "-1a" +
                "\n " + "-魔神" +
                "\n" + "以下内容为 直接使用[指令]" +
                "\n " + "vip" +
                "\n " + "喜报 (内容)" +
                "\n " + "悲报 (内容)" +
                "\n " + "重锤{@}" +
                "\n " + "吸入{@}" +
                "\n " + "(角色名)/(地区) {(群/群号) 必须在开头或结尾}");
//        if (qq_id.equals("793888025") || qq_id.equals("1613341351"))
            event.getSubject().sendMessage(messageChainBuilder.build());
    }


}
