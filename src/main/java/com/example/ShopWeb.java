package com.example;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class ShopWeb {
//    LocalDate date = LocalDate.now().plusDays(1);
//    String time = date.toString();

    public String GetFullDate(String time) {
        LocalDate date = LocalDate.now().plusDays(1);
        if (time != null && !time.equals("")) {
            String[] timeFull = time.trim().split(" |-|\\.|/|🥵");
            System.out.println(time);

            for (int i = 0; i < timeFull.length; i++) {
                try {
                    int num = Integer.parseInt(timeFull[i]);
                    if (num <= 9 && !timeFull[i].startsWith("0")) {
                        timeFull[i] = "0" + timeFull[i];
                    }
                } catch (NumberFormatException e) {

                }
            }
            StringBuilder now = new StringBuilder();
            for (String str : timeFull) {
                now.append(str).append(" ");
            }
//            System.out.println(now.toString());
            if (timeFull.length == 2) {
                now.insert(0, LocalDate.now().getYear() + " ");
            } else if (timeFull.length == 1) {
                String month = String.valueOf(LocalDate.now().getMonthValue());
                if (!month.startsWith("0") && Integer.parseInt(month) <= 9) {
                    month = "0" + month;
                }
                now.insert(0, LocalDate.now().getYear() + " " + month + " ");
            }
//            System.out.println(now);

            date = LocalDate.parse(now.toString(), DateTimeFormatter.ofPattern("yyyy MM dd "));
        }
        String day = date.format(DateTimeFormatter.ofPattern("dd"));
        String dday = "";
        String month = date.format(DateTimeFormatter.ofPattern("MM"));
        String mmonth = "";
        String year = "" + date.getYear();

        int dd = Integer.parseInt(day);
        if (1 <= dd && dd <= 10) {
            dday = "A";
        } else if (dd <= 20) {
            dday = "B";
        } else if (dd <= 31) {
            dday = "C";
        } else {
            dday = "";
            day = "";
        }

        switch (Integer.parseInt(month)) {
            case 1:
            case 2:
                mmonth = "A";
                break;
            case 3:
            case 4:
                mmonth = "B";
                break;
            case 5:
                mmonth = "C";
                break;
            case 6:
            case 7:
            case 8:
                mmonth = "D";
                break;
            case 9:
            case 10:
            case 11:
                mmonth = "F";
                break;
            case 12:
                mmonth = "G";
                break;
            default:
                mmonth = "";
                month = "";
                break;
        }

        return year + mmonth + month + dday + day;
    }

    public JSONObject GetStringJson(String nowFull) throws IOException {
//        if (nowFull == "" || nowFull.equals("") || nowFull == null) {
////			nowFull = new ShopWeb().GetNextDay();
//        }
        System.out.println(nowFull);
        URL url = new URL(
//				"");
                "https://search.nintendo.jp/nintendo_soft/search.json?opt_sshow=1&xopt_ssitu[]=sales_termination"
                        + "&fq=sodate_s:[" + nowFull + "%20TO%20" + nowFull
                        + "]&limit=300&page=1&c=00000000000000000&sort=sodate%20asc,hards%20asc,sform_s%20asc,titlek%20asc&opt_sche=1");

        InputStreamReader is = new InputStreamReader(url.openStream());
        BufferedReader br = new BufferedReader(is);
        String s = br.readLine();
        br.close();
        is.close();

//		String s="";
        JSONObject json = JSON.parseObject(s);
//		System.out.println(jsonObject);
        String resultS = JSON.toJSONString(json.get("result"));
        //		String totalS = JSON.toJSONString(resultJ.get("total"));
//		int total = Integer.parseInt(totalS);
//		System.out.println(total);
//		JSONArray items = JSON.parseArray(JSON.toJSONString(resultJ.get("items")));
//		//		System.out.println(items);
//		for (int i=0;i<total;i++){
//			JSONObject itemsJ = JSONArray.parseObject(items.get(i).toString());
//			System.out.println(itemsJ.getString("title"));
//        }
        return JSON.parseObject(resultS);

//		JSONObject itemslist = JSON.parseObject(items);
//		    JSONObject jsonObjectTwo = JSON.parseObject(s2);
//		    String strand = JSON.toJSONString(jsonObjectTwo.get("111"));
//		    String in = JSON.toJSONString(jsonObjectTwo.get("222"));
//		    String out = JSON.toJSONString(jsonObjectTwo.get("333"));
//		    System.out.println(strand);
//		    System.out.println(in);
//		    System.out.println(out);
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }
}
