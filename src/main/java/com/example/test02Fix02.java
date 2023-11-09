package com.example;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.LinkedHashMap;

public class test02Fix02 extends SimpleListenerHost {
	public static MessageEvent getEvent() {
		return event;
	}

	public static void setEvent(MessageEvent event) {
		test02Fix02.event = event;
	}

	static MessageEvent event = null;
	static String[] commandList = new String[]{"查询", "进化", "获取", "闪光","种族值"};

	@EventHandler
	private ListeningStatus onEvent(MessageEvent event){
		String s = event.getMessage().contentToString().trim();
		if (startNumber(commandList,s)!=-1) {
			setEvent(event);
			try {
				GetMethod(s);
			} catch (Exception e) {
//				throw new RuntimeException(e);
				sendError(e);
			}
		}
		return ListeningStatus.LISTENING;
	}

	public static void GetMethod(String input) throws Exception {
		System.out.println(input);
		String name = "";
		String str = input;
		if (input.trim().startsWith("查询")) {
			str = input.replaceAll("查询", "").trim();
		}

		int listNumber = startNumber(commandList, str);
		if(listNumber==-1)
			return;
		String method = commandList[listNumber];
		if (listNumber > 0) {
			name = str.replaceAll(method, "").trim();
//			System.out.println(name);
			if (name.length() > 0 && isPoke(name.substring(0, Math.max(2,name.length()-1)))) {
				event.getSubject().sendMessage("请稍等正在查询"+method);
//				System.out.println(method);
				if (listNumber == 1)
					GetLevel(name);
				else if (listNumber == 2)
					GetPokemon(name);
				else if (listNumber == 3)
//					GetShiny(name);
					event.getSubject().sendMessage("该功能暂未重构");
				else if (listNumber == 4) {
					GetBase(name);
				}
			}
//					String id = GetPokeId(name);
		}
	}

	public String GetPokeId(String name) throws Exception {
		String Pid = "";
		String u = "https://wiki.52poke.com/wiki/%E5%AE%9D%E5%8F%AF%E6%A2%A6%E5%88%97%E8%A1%A8%EF%BC%88%E6%8C%89%E5%85%A8%E5%9B%BD%E5%9B%BE%E9%89%B4%E7%BC%96%E5%8F%B7%EF%BC%89/%E7%AE%80%E5%8D%95%E7%89%88";
		Document document = Jsoup.connect(u).timeout(30000).get();
		Elements trs = document.getElementsByClass("a-c roundy eplist bgl-神奇宝贝百科 b-神奇宝贝百科 bw-2").select("tr");
		for (Element tr : trs) {
			Elements tds = tr.select("td");
			for (int j = 0; j < tds.size(); j++) {
				String text = tds.get(j).text();
//					System.out.print(name+" "+text);
				if (text.equals(name)) {
					Pid = tds.get(j - 1).text().replaceAll("#", "");
					System.out.println(Pid);
				}
			}
		}
		return Pid;
	}

	public static MessageChain GetPokemon(String name) throws Exception {
		MessageChain chain = MessageUtils.newChain();
		String u = "https://wiki.52poke.com/wiki/";
		Document document = Jsoup.connect(u + name).timeout(50000).get();
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

	public static void GetLevel(String name) throws Exception {
		String u = "https://wiki.52poke.com/wiki/";
		System.setProperty("webdriver.chrome.driver","D:/Downloads/chromedriver.exe");
		ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
		options.addArguments("--disable-gpu");
		options.addArguments("--no-sandbox");
		options.addArguments("--log-level=3");
		options.addArguments("--remote-allow-origins=*");

		ChromeDriver driver = new ChromeDriver(options);
		driver.get(u+name);
		try {
			String className =(String) driver.executeScript("return document.getElementsByClassName(\"mw-selflink selflink\")[0].parentElement.parentElement.parentElement.parentElement.parentElement.parentElement.parentElement.parentElement.className");
			extracted("//table[@class=\""+className+"\"]",driver);
		} catch (InterruptedException | IOException e) {
//			e.printStackTrace();
			sendError(e);
		}

		driver.quit();
	}

//	public static void GetShiny(String name) throws Exception {
//		char c = name.charAt(name.length() - 1);
//		boolean hasG = Character.isDigit(c);
//		if (hasG) {
//			name = name.replace(String.valueOf(c), "");
//		}
////		System.out.println(c);
//		String u = "https://wiki.52poke.com/wiki/";
//		Document document = Jsoup.connect(u + name).timeout(50000).get();
////		System.out.println(document);
//
//		Element parent = null;
//		Element element = null;
//		Elements alla = document.select("a");
//		for (Element value : alla) {
//			if (value.attr("title").equals(GenName(c))) {
//				parent = value;
//				break;
//			}
//		}
//		if (parent == null&&hasG)
//			throw new Exception("parent可能不存在");
//
//
//		if (hasG) {
////			String cname = GenName(c);
////			parent = document.select("table.bl-"+cname+".bw-2.bg-"+cname).first();
//			parent = parent.parent().parent().parent();
//			element = parent;
//		} else {
//			parent = document.getElementById("形象").parent().nextElementSibling();
//			String tag = parent.tagName();
////			System.out.println(tag);
//
//			if (tag.equals("div")) {
//				element = parent.children().last().child(0);
//			} else {
//				element = parent.nextElementSibling().children().last().child(0);
//			}
//		}
////		System.out.println(parent.children().last());
////		System.out.println(element);
//		int numShiny = (element.childrenSize() / 2) + 1;
////		Element shiny = element.children().get(numShiny);
//		Elements shinys = element.children();
//		for (int j = 0; j < shinys.size(); j++) {
//			Element shiny = shinys.get(j);
//			for (int i = 1; i <= shiny.childrenSize(); i++) {
////				#mw-content-text > div > div.bd-电.bgl-电.roundy.a-c > table.bl-伽勒尔.bw-2.bg-伽勒尔.roundy.textwhite.mw-collapsible.fulltable.at-c.mw-made-collapsible > tbody > tr:nth-child(3) > td:nth-child(1) > a > img
////				#mw-content-text > div > div.bd-电.bgl-电.roundy.a-c > table.bl-伽勒尔.bw-2.bg-伽勒尔.roundy.textwhite.mw-collapsible.fulltable.at-c.mw-made-collapsible > tbody > tr:nth-child(5) > td:nth-child(2) > a > img
//				Elements imgs = shiny.select("td:nth-child(" + i + ") > a:nth-child(1) > img");
//				imgs.attr("src", "https:" + imgs.attr("data-url"));
//				if (j >= numShiny) {
//					Element star = shiny.select("td:nth-child(" + i + ")").last();
////			System.out.println(star.childrenSize());
//					Elements stars = shiny
//							.select("td:nth-child(" + i + ") > a:nth-child(" + star.childrenSize() + ") > img");
//					stars.attr("src", "https:" + stars.attr("data-url"));
//				}
//			}
//		}
//		eventSend(shinys);
//	}

	public static String GenName(char c) {
		String G = null;
		LinkedHashMap<String, Character> lhm = new LinkedHashMap<String, Character>();
		lhm.put("一", '1');
		lhm.put("二", '2');
		lhm.put("三", '3');
		lhm.put("四", '4');
		lhm.put("五", '5');
		lhm.put("六", '6');
		lhm.put("七", '7');
		lhm.put("八", '8');
		lhm.put("九", '9');
		for (String key : lhm.keySet()) {
			if (lhm.get(key) == c) {
				G = key;
			}
		}
		return "第" + G + "世代";
	}

//	public static void eventSend(Object element){
//		MessageEvent event = getEvent();
//		HtmlImageGenerator imageGenerator = new HtmlImageGenerator();
//
//		String htmlstr = "<span>test</span>";
//		htmlstr=element.toString().replaceAll("\\{\\{\\{7}}}", "");
//
//		imageGenerator.loadHtml(htmlstr);
//		BufferedImage bufferedImage = imageGenerator.getBufferedImage();
//		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//		try {
//			ImageIO.write(bufferedImage, "png", outputStream);
//			Image image = Contact.uploadImage(event.getSubject(),new ByteArrayInputStream(outputStream.toByteArray()));
//			event.getSubject().sendMessage(image.plus(new At(event.getSender().getId())));
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				outputStream.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}

	public static void GetBase(String name){
		String u = "https://wiki.52poke.com/wiki/";
		System.setProperty("webdriver.chrome.driver","D:/Downloads/chromedriver.exe");
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless");
		options.addArguments("--disable-gpu");
		options.addArguments("--no-sandbox");
		options.addArguments("--log-level=3");

		ChromeDriver driver = new ChromeDriver(options);
		driver.get(u+name);
		try {
			extracted("//table[@style='white-space:nowrap']",driver);
		} catch (InterruptedException | IOException e) {
//			e.printStackTrace();
			sendError(e);
		}

		driver.quit();
	}

	public static void extracted(String screenXpath, ChromeDriver driver) throws InterruptedException, IOException {
		//找图表
		WebElement img = driver.findElement(By.xpath(screenXpath));
		Thread.sleep(3000);
		// 通过执行脚本解决Selenium截图不全问题
		long maxWidth = (long) driver.executeScript(
				"return Math.max(document.body.scrollWidth, document.body.offsetWidth, document.documentElement.clientWidth, document.documentElement.scrollWidth, document.documentElement.offsetWidth);");
		long maxHeight = (long) driver.executeScript(
				"return Math.max(document.body.scrollHeight, document.body.offsetHeight, document.documentElement.clientHeight, document.documentElement.scrollHeight, document.documentElement.offsetHeight);");
		Dimension targetSize = new Dimension((int)maxWidth, (int)maxHeight);
		driver.manage().window().setSize(targetSize);
		//获取窗口高度
		Object o1 = ((JavascriptExecutor) driver).executeScript("return document.documentElement.clientHeight");
		int windowsHeight = Integer.parseInt(String.valueOf(o1));

		//图片位置
		Point location = img.getLocation();
		//图片y坐标
		int y = location.getY();
		//窗口滑动高度
		int move = y + img.getSize().height > windowsHeight ? y + img.getSize().height - windowsHeight : 0;
		//滑动窗口
		if (move > 0) {
			((JavascriptExecutor) driver).executeScript("window.scrollTo(0, " + move + ")");
		}
		Thread.sleep(2000);
		File imgPath = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		SendImageFile(Files.newInputStream(captureElement(imgPath, img, move).toPath()));
//        imgPath.deleteOnExit();
	}
	public static File captureElement(File filePath, WebElement element, int move) throws IOException {
		BufferedImage img = ImageIO.read(filePath);
		int width = element.getSize().getWidth();
		int height = element.getSize().getHeight();
		Point point = element.getLocation();
		//从元素左上角坐标开始，按照元素的高宽对img进行裁剪为符合需要的图片
		BufferedImage dest = img.getSubimage(point.getX(), point.getY() - move, width, height);
		ImageIO.write(dest, "png", filePath);
		return filePath;
	}

	public static void SendImageFile(InputStream inputStream){
		try {
			Image image = Contact.uploadImage(event.getSubject(),inputStream);
			event.getSubject().sendMessage(image.plus(new At(event.getSender().getId())));
		} catch (Exception e) {
//            e.printStackTrace();
			sendError(e);
		}
	}
	public static void sendError(Exception e){
		event.getSubject().sendMessage(String.valueOf(e));
	}

	public static int startNumber(String[] array, String str) {
		for (int i = 0; i < array.length; i++) {
			if (str.startsWith(array[i])) {
				return i;
			}
		}
		return -1;
	}

	public static boolean isPoke(String input) {
		System.out.println(input);
		String list = "妙蛙种子	妙蛙草	妙蛙花	小火龙	火恐龙	喷火龙	杰尼龟	卡咪龟	水箭龟	绿毛虫	铁甲蛹	巴大蝶	独角虫	铁壳蛹	大针蜂	波波	比比鸟	大比鸟	小拉达	拉达	烈雀	大嘴雀	阿柏蛇	阿柏怪	皮卡丘	雷丘	穿山鼠	穿山王	尼多兰	尼多娜	尼多后	尼多朗	尼多力诺	尼多王	皮皮	皮可西	六尾	九尾	胖丁	胖可丁	超音蝠	大嘴蝠	走路草	臭臭花	霸王花	派拉斯	派拉斯特	毛球	摩鲁蛾	地鼠	三地鼠	喵喵	猫老大	可达鸭	哥达鸭	猴怪	火暴猴	卡蒂狗	风速狗	蚊香蝌蚪	蚊香君	蚊香泳士	凯西	勇基拉	胡地	腕力	豪力	怪力	喇叭芽	口呆花	大食花	玛瑙水母	毒刺水母	小拳石	隆隆石	隆隆岩	小火马	烈焰马	呆呆兽	呆壳兽	小磁怪	三合一磁怪	大葱鸭	嘟嘟	嘟嘟利	小海狮	白海狮	臭泥	臭臭泥	大舌贝	刺甲贝	鬼斯	鬼斯通	耿鬼	大岩蛇	催眠貘	引梦貘人	大钳蟹	巨钳蟹	霹雳电球	顽皮雷弹	蛋蛋	椰蛋树	卡拉卡拉	嘎啦嘎啦	飞腿郎	快拳郎	大舌头	瓦斯弹	双弹瓦斯	独角犀牛	钻角犀兽	吉利蛋	蔓藤怪	袋兽	墨海马	海刺龙	角金鱼	金鱼王	海星星	宝石海星	魔墙人偶	飞天螳螂	迷唇姐	电击兽	鸭嘴火兽	凯罗斯	肯泰罗	鲤鱼王	暴鲤龙	拉普拉斯	百变怪	伊布	水伊布	雷伊布	火伊布	多边兽	菊石兽	多刺菊石兽	化石盔	镰刀盔	化石翼龙	卡比兽	急冻鸟	闪电鸟	火焰鸟	迷你龙	哈克龙	快龙	超梦	梦幻	菊草叶	月桂叶	大竺葵	火球鼠	火岩鼠	火暴兽	小锯鳄	蓝鳄	大力鳄	尾立	大尾立	咕咕	猫头夜鹰	芭瓢虫	安瓢虫	圆丝蛛	阿利多斯	叉字蝠	灯笼鱼	电灯怪	皮丘	皮宝宝	宝宝丁	波克比	波克基古	天然雀	天然鸟	咩利羊	茸茸羊	电龙	美丽花	玛力露	玛力露丽	树才怪	蚊香蛙皇	毽子草	毽子花	毽子棉	长尾怪手	向日种子	向日花怪	蜻蜻蜓	乌波	沼王	太阳伊布	月亮伊布	黑暗鸦	呆呆王	梦妖	未知图腾	果然翁	麒麟奇	榛果球	佛烈托斯	土龙弟弟	天蝎	大钢蛇	布鲁	布鲁皇	千针鱼	巨钳螳螂	壶壶	赫拉克罗斯	狃拉	熊宝宝	圈圈熊	熔岩虫	熔岩蜗牛	小山猪	长毛猪	太阳珊瑚	铁炮鱼	章鱼桶	信使鸟	巨翅飞鱼	盔甲鸟	戴鲁比	黑鲁加	刺龙王	小小象	顿甲	多边兽Ⅱ	惊角鹿	图图犬	无畏小子	战舞郎	迷唇娃	电击怪	鸭嘴宝宝	大奶罐	幸福蛋	雷公	炎帝	水君	幼基拉斯	沙基拉斯	班基拉斯	洛奇亚	凤王	时拉比	木守宫	森林蜥蜴	蜥蜴王	火稚鸡	力壮鸡	火焰鸡	水跃鱼	沼跃鱼	巨沼怪	土狼犬	大狼犬	蛇纹熊	直冲熊	刺尾虫	甲壳茧	狩猎凤蝶	盾甲茧	毒粉蛾	莲叶童子	莲帽小童	乐天河童	橡实果	长鼻叶	狡猾天狗	傲骨燕	大王燕	长翅鸥	大嘴鸥	拉鲁拉丝	奇鲁莉安	沙奈朵	溜溜糖球	雨翅蛾	蘑蘑菇	斗笠菇	懒人獭	过动猿	请假王	土居忍士	铁面忍者	脱壳忍者	咕妞妞	吼爆弹	爆音怪	幕下力士	铁掌力士	露力丽	朝北鼻	向尾喵	优雅猫	勾魂眼	大嘴娃	可可多拉	可多拉	波士可多拉	玛沙那	恰雷姆	落雷兽	雷电兽	正电拍拍	负电拍拍	电萤虫	甜甜萤	毒蔷薇	溶食兽	吞食兽	利牙鱼	巨牙鲨	吼吼鲸	吼鲸王	呆火驼	喷火驼	煤炭龟	跳跳猪	噗噗猪	晃晃斑	大颚蚁	超音波幼虫	沙漠蜻蜓	刺球仙人掌	梦歌仙人掌	青绵鸟	七夕青鸟	猫鼬斩	饭匙蛇	月石	太阳岩	泥泥鳅	鲶鱼王	龙虾小兵	铁螯龙虾	天秤偶	念力土偶	触手百合	摇篮百合	太古羽虫	太古盔甲	丑丑鱼	美纳斯	飘浮泡泡	变隐龙	怨影娃娃	诅咒娃娃	夜巡灵	彷徨夜灵	热带龙	风铃铃	阿勃梭鲁	小果然	雪童子	冰鬼护	海豹球	海魔狮	帝牙海狮	珍珠贝	猎斑鱼	樱花鱼	古空棘鱼	爱心鱼	宝贝龙	甲壳龙	暴飞龙	铁哑铃	金属怪	巨金怪	雷吉洛克	雷吉艾斯	雷吉斯奇鲁	拉帝亚斯	拉帝欧斯	盖欧卡	固拉多	烈空坐	基拉祈	代欧奇希斯	草苗龟	树林龟	土台龟	小火焰猴	猛火猴	烈焰猴	波加曼	波皇子	帝王拿波	姆克儿	姆克鸟	姆克鹰	大牙狸	大尾狸	圆法师	音箱蟀	小猫怪	勒克猫	伦琴猫	含羞苞	罗丝雷朵	头盖龙	战槌龙	盾甲龙	护城龙	结草儿	结草贵妇	绅士蛾	三蜜蜂	蜂女王	帕奇利兹	泳圈鼬	浮潜鼬	樱花宝	樱花儿	无壳海兔	海兔兽	双尾怪手	飘飘球	随风球	卷卷耳	长耳兔	梦妖魔	乌鸦头头	魅力喵	东施喵	铃铛响	臭鼬噗	坦克臭鼬	铜镜怪	青铜钟	盆才怪	魔尼尼	小福蛋	聒噪鸟	花岩怪	圆陆鲨	尖牙陆鲨	烈咬陆鲨	小卡比兽	利欧路	路卡利欧	沙河马	河马兽	钳尾蝎	龙王蝎	不良蛙	毒骷蛙	尖牙笼	荧光鱼	霓虹鱼	小球飞鱼	雪笠怪	暴雪王	玛狃拉	自爆磁怪	大舌舔	超甲狂犀	巨蔓藤	电击魔兽	鸭嘴炎兽	波克基斯	远古巨蜓	叶伊布	冰伊布	天蝎王	象牙猪	多边兽Ｚ	艾路雷朵	大朝北鼻	黑夜魔灵	雪妖女	洛托姆	由克希	艾姆利多	亚克诺姆	帝牙卢卡	帕路奇亚	席多蓝恩	雷吉奇卡斯	骑拉帝纳	克雷色利亚	霏欧纳	玛纳霏	达克莱伊	谢米	阿尔宙斯	比克提尼	藤藤蛇	青藤蛇	君主蛇	暖暖猪	炒炒猪	炎武王	水水獭	双刃丸	大剑鬼	探探鼠	步哨鼠	小约克	哈约克	长毛狗	扒手猫	酷豹	花椰猴	花椰猿	爆香猴	爆香猿	冷水猴	冷水猿	食梦梦	梦梦蚀	豆豆鸽	咕咕鸽	高傲雉鸡	斑斑马	雷电斑马	石丸子	地幔岩	庞岩怪	滚滚蝙蝠	心蝙蝠	螺钉地鼠	龙头地鼠	差不多娃娃	搬运小匠	铁骨土人	修建老匠	圆蝌蚪	蓝蟾蜍	蟾蜍王	投摔鬼	打击鬼	虫宝包	宝包茧	保姆虫	百足蜈蚣	车轮球	蜈蚣王	木棉球	风妖精	百合根娃娃	裙儿小姐	野蛮鲈鱼	黑眼鳄	混混鳄	流氓鳄	火红不倒翁	达摩狒狒	沙铃仙人掌	石居蟹	岩殿居蟹	滑滑小子	头巾混混	象征鸟	哭哭面具	死神棺	原盖海龟	肋骨海龟	始祖小鸟	始祖大鸟	破破袋	灰尘山	索罗亚	索罗亚克	泡沫栗鼠	奇诺栗鼠	哥德宝宝	哥德小童	哥德小姐	单卵细胞球	双卵细胞球	人造细胞卵	鸭宝宝	舞天鹅	迷你冰	多多冰	双倍多多冰	四季鹿	萌芽鹿	电飞鼠	盖盖虫	骑士蜗牛	哎呀球菇	败露球菇	轻飘飘	胖嘟嘟	保姆曼波	电电虫	电蜘蛛	种子铁球	坚果哑铃	齿轮儿	齿轮组	齿轮怪	麻麻小鱼	麻麻鳗	麻麻鳗鱼王	小灰怪	大宇怪	烛光灵	灯火幽灵	水晶灯火灵	牙牙	斧牙龙	双斧战龙	喷嚏熊	冻原熊	几何雪花	小嘴蜗	敏捷虫	泥巴鱼	功夫鼬	师父鼬	赤面龙	泥偶小人	泥偶巨人	驹刀小兵	劈斩司令	爆炸头水牛	毛头小鹰	勇士雄鹰	秃鹰丫头	秃鹰娜	熔蚁兽	铁蚁	单首龙	双首暴龙	三首恶龙	燃烧虫	火神蛾	勾帕路翁	代拉基翁	毕力吉翁	龙卷云	雷电云	莱希拉姆	捷克罗姆	土地云	酋雷姆	凯路迪欧	美洛耶塔	盖诺赛克特	哈力栗	胖胖哈力	布里卡隆	火狐狸	长尾火狐	妖火红狐	呱呱泡蛙	呱头蛙	甲贺忍蛙	掘掘兔	掘地兔	小箭雀	火箭雀	烈箭鹰	粉蝶虫	粉蝶蛹	彩粉蝶	小狮狮	火炎狮	花蓓蓓	花叶蒂	花洁夫人	坐骑小羊	坐骑山羊	顽皮熊猫	流氓熊猫	多丽米亚	妙喵	超能妙喵	独剑鞘	双剑鞘	坚盾剑怪	粉香香	芳香精	绵绵泡芙	胖甜妮	好啦鱿	乌贼王	龟脚脚	龟足巨铠	垃垃藻	毒藻龙	铁臂枪虾	钢炮臂虾	伞电蜥	光电伞蜥	宝宝暴龙	怪颚龙	冰雪龙	冰雪巨龙	仙子伊布	摔角鹰人	咚咚鼠	小碎钻	黏黏宝	黏美儿	黏美龙	钥圈儿	小木灵	朽木妖	南瓜精	南瓜怪人	冰宝	冰岩怪	嗡蝠	音波龙	哲尔尼亚斯	伊裴尔塔尔	基格尔德	蒂安希	胡帕	波尔凯尼恩	木木枭	投羽枭	狙射树枭	火斑喵	炎热喵	炽焰咆哮虎	球球海狮	花漾海狮	西狮海壬	小笃儿	喇叭啄鸟	铳嘴大鸟	猫鼬少	猫鼬探长	强颚鸡母虫	虫电宝	锹农炮虫	好胜蟹	好胜毛蟹	花舞鸟	萌虻	蝶结萌虻	岩狗狗	鬃岩狼人	弱丁鱼	好坏星	超坏星	泥驴仔	重泥挽马	滴蛛	滴蛛霸	伪螳草	兰螳花	睡睡菇	灯罩夜菇	夜盗火蜥	焰后蜥	童偶熊	穿着熊	甜竹竹	甜舞妮	甜冷美后	花疗环环	智挥猩	投掷猴	胆小虫	具甲武者	沙丘娃	噬沙堡爷	拳海参	属性：空	银伴战兽	小陨星	树枕尾熊	爆焰龟兽	托戈德玛尔	谜拟Ｑ	磨牙彩皮鱼	老翁龙	破破舵轮	心鳞宝	鳞甲龙	杖尾鳞甲龙	卡璞・鸣鸣	卡璞・蝶蝶	卡璞・哞哞	卡璞・鳍鳍	科斯莫古	科斯莫姆	索尔迦雷欧	露奈雅拉	虚吾伊德	爆肌蚊	费洛美螂	电束木	铁火辉夜	纸御剑	恶食大王	奈克洛兹玛	玛机雅娜	玛夏多	毒贝比	四颚针龙	垒磊石	砰头小丑	捷拉奥拉	美录坦	美录梅塔	敲音猴	啪咚猴	轰擂金刚猩	炎兔儿	腾蹴小将	闪焰王牌	泪眼蜥	变涩蜥	千面避役	贪心栗鼠	藏饱栗鼠	稚山雀	蓝鸦	钢铠鸦	索侦虫	天罩虫	以欧路普	偷儿狐	狐大盗	幼棉棉	白蓬蓬	毛辫羊	毛毛角羊	咬咬龟	暴噬龟	来电汪	逐电犬	小炭仔	大炭车	巨炭山	啃果虫	苹裹龙	丰蜜龙	沙包蛇	沙螺蟒	古月鸟	刺梭鱼	戽斗尖梭	毒电婴	颤弦蝾螈	烧火蚣	焚焰蚣	拳拳蛸	八爪武师	来悲茶	怖思壶	迷布莉姆	提布莉姆	布莉姆温	捣蛋小妖	诈唬魔	长毛巨魔	堵拦熊	喵头目	魔灵珊瑚	葱游兵	踏冰人偶	死神板	小仙奶	霜奶仙	列阵兵	啪嚓海胆	雪吞虫	雪绒蛾	巨石丁	冰砌鹅	爱管侍	莫鲁贝可	铜象	大王铜象	雷鸟龙	雷鸟海兽	鳃鱼龙	鳃鱼海兽	铝钢龙	多龙梅西亚	多龙奇	多龙巴鲁托	苍响	藏玛然特	无极汰那	熊徒弟	武道熊师	萨戮德	雷吉艾勒奇	雷吉铎拉戈	雪暴马	灵幽马	蕾冠王	诡角鹿	劈斧螳螂	月月熊	幽尾玄鱼	大狃拉	万针鱼	眷恋云	新叶喵	蒂蕾喵	魔幻假面喵	呆火鳄	炙烫鳄	骨纹巨声鳄	润水鸭	涌跃鸭	狂欢浪舞鸭	爱吃豚	飘香豚	团珠蛛	操陷蛛	豆蟋蟀	烈腿蝗	布拨	布土拨	巴布土拨	一对鼠	一家鼠	狗仔包	麻花犬	迷你芙	奥利纽	奥利瓦	怒鹦哥	盐石宝	盐石垒	盐石巨灵	炭小侍	红莲铠骑	苍炎刃鬼	光蚪仔	电肚蛙	电海燕	大电海燕	偶叫獒	獒教父	滋汁鼹	涂标客	纳噬草	怖纳噬草	原野水母	陆地水母	毛崖蟹	热辣娃	狠辣椒	虫滚泥	虫甲圣	飘飘雏	超能艳鸵	小锻匠	巧锻匠	巨锻匠	海地鼠	三海地鼠	下石鸟	波普海豚	海豚侠	噗隆隆	普隆隆姆	摩托蜥	拖拖蚓	晶光芽	晶光花	墓仔狗	墓扬犬	缠红鹤	走鲸	浩大鲸	轻身鳕	吃吼霸	米立龙	弃世猴	土王	奇麒麟	土龙节节	仆斩将军	雄伟牙	吼叫尾	猛恶菇	振翼发	爬地翅	沙铁皮	铁辙迹	铁包袱	铁臂膀	铁脖颈	铁毒蛾	铁荆棘	凉脊龙	冻脊龙	戟脊龙	索财灵	赛富豪	古简蜗	古剑豹	古鼎鹿	古玉鱼	轰鸣月	铁武者	故勒顿	密勒顿";
		return list.contains(input);
	}
}
