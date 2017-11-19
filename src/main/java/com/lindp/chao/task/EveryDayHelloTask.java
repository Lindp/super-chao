package com.lindp.chao.task;

import com.lindp.chao.processor.EveryDayHelloProcessor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;

@Component
public class EveryDayHelloTask {

    /**
     * 每日早安短信
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void startGoodMorning() {
        Spider.create(new EveryDayHelloProcessor())
                .addUrl("http://www.xzw.com/fortune/aquarius")
                .addUrl("http://www.xzw.com/fortune/leo")
                //开启5个线程抓取
                .thread(1)
                //启动爬虫
                .run();
    }
}
