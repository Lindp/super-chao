package com.lindp.chao.processor;

import com.lindp.chao.common.ChaoConsts;
import com.lindp.chao.utils.AlibabaSmsUtils;
import htjar.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.Calendar;
import java.util.Date;

/**
 * 星座今日运势处理器
 *
 * @author lindp
 * @date 2017/11/19
 */
public class EveryDayHelloProcessor implements PageProcessor {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public void process(Page page) {
        String fortune = page.getHtml().xpath("//div[@class='c_cont']//span/html()").get();
        String url = page.getUrl().get();
        String flag = url.substring(url.lastIndexOf("/") + 1, url.length());
        int dayNumber = differentDays(DateUtils.stringToDate("2017-8-17 08:00:00"), new Date());
        dayNumber++; // 包含当天

//        String star = page.getHtml().xpath("//div[@class='c_main']//ul//li/html()").get();
//
//        Document doc = Jsoup.parseBodyFragment(star);
//        String starWidth = doc.getElementsByTag("em").get(0).attr("style");
//        String starNumStr = starWidth.substring(starWidth.indexOf(":") + 1, starWidth.lastIndexOf("p"));
//        Integer starNum = Integer.parseInt(starNumStr);
//        String starPrint = "今日运势评星" + (starNum / 16) + "/5";

        String goodLuckHtml = page.getHtml().xpath("//div[@class='c_main']//ul/html()").get();
        Document goodLuckDom = Jsoup.parseBodyFragment(goodLuckHtml);
        String goodLuckItemHtml = goodLuckDom.getElementsByTag("li").get(7).html();
        String goodLuck = goodLuckItemHtml.substring(goodLuckItemHtml.length() - 1, goodLuckItemHtml.length());

        fortune = StringUtils.removeEnd(fortune, "。");
        fortune = StringUtils.removeEnd(fortune, "！");
        String fortuneContent = String.format("%s。今日运势：%s", goodLuck, fortune);
        if (StringUtils.isNotBlank(flag) && flag.equals("aquarius") && StringUtils.isNotBlank(fortuneContent)) { // 水瓶座
            AlibabaSmsUtils.sendGoodMorning(ChaoConsts.LINDP_PHONE, fortuneContent, dayNumber);
        }
        if (StringUtils.isNotBlank(flag) && flag.equals("leo") && StringUtils.isNotBlank(fortuneContent)) { // 狮子座
            AlibabaSmsUtils.sendGoodMorning(ChaoConsts.CHAO_PHONE, fortuneContent, dayNumber);
        }

        log.info("早上好亲爱的！今日的幸运数字是{}。今天是我们在一起的第{}天。愿你一天好心情！", fortuneContent, dayNumber);
    }

    public Site getSite() {
        return Site.me().setRetryTimes(3).setSleepTime(1000);
    }

    public static void main(String[] args) {
        Spider.create(new EveryDayHelloProcessor())
                .addUrl("http://www.xzw.com/fortune/aquarius")
                .addUrl("http://www.xzw.com/fortune/leo")
                //开启5个线程抓取
                .thread(1)
                //启动爬虫
                .run();
    }

    /**
     * date2比date1多的天数
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDays(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if (year1 != year2)   //同一年
        {
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0)    //闰年
                {
                    timeDistance += 366;
                } else    //不是闰年
                {
                    timeDistance += 365;
                }
            }

            return timeDistance + (day2 - day1);
        } else    //不同年
        {
            return day2 - day1;
        }
    }
}
