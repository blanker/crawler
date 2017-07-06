package com.dongbawen.crawler.webmagic;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;

/**
 * Created by blank on 2017/7/6.
 */
public class Glashon4OicpNet implements PageProcessor {
    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);

    private final static String FIRST_URL = "^http://flashon4\\.oicp\\.net/$";
    private final static String SEED_URL = "^http://flashon4\\.oicp\\.net/[1-6]\\.htm";
    public void process(Page page) {
        if (page.getUrl().regex(FIRST_URL).match()   ) {
            List<String> urls = page.getHtml().css("body").links().regex("\\d+\\.htm").all();
            page.addTargetRequests(urls);
            System.out.println(" * [ ");
            System.out.println(" get pages: " + urls + " from " + page.getUrl());
            System.out.println(" * ] ");

        } else if (page.getUrl().regex(SEED_URL).match()) {
            List<String> urls = page.getHtml().css("body").links().regex("\\d+/.*\\.htm").all();
            page.addTargetRequests(urls);
            System.out.println(" * [[[ ");
            System.out.println(" get pages: " + urls + " from " + page.getUrl());
            System.out.println(" * ]]] ");
        } else {
            List<String> urls = page.getHtml().css("body").links().regex("\\d+/.*\\.htm").all();
            if (urls.size() > 0) {
                page.addTargetRequests(urls);
            } else {
                System.out.println(" * [[ ");
                System.out.println(" get page: " + page.getUrl());
                System.out.println(" * ]] ");
                //page.addTargetRequest("http://flashon4\\.oicp\\.net/\\d{2}/.*\\.htm");
                page.putField("headline", page.getHtml().xpath("//p[@id='headline']/text()").toString());
                if (page.getResultItems().get("headline") == null ) {
                    page.putField("headline", page.getHtml().xpath("//p[@id='headline']/b/span/text()").toString());
                    if (page.getResultItems().get("headline") == null ) {
                        page.setSkip(true);
                    }
                }
                page.putField("desc", page.getHtml().xpath("//div[@id='div1']/text()").toString());
                page.putField("detail", page.getHtml().xpath("//div[@id='div2']/text()").toString());


            }
        }

    }

    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new Glashon4OicpNet()).addUrl("http://flashon4.oicp.net/").addPipeline(new JsonFilePipeline("d:\\data\\webmagic"))
                .thread(5).run();
    }
}
