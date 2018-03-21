package com.zhongke.market.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${xingen} on 2017/10/30.
 * <p>
 * 广告轮播图的实体类
 */

public class Carousel {
    public String imageUrl;
    private static final String[] imageUrls = {
            "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=980290014,3494954107&fm=27&gp=0.jpg"
            , "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2023148164,2745773808&fm=27&gp=0.jpg"
            , "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=351009418,3128731344&fm=27&gp=0.jpg"
            , "https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=2851674092,3341983635&fm=27&gp=0.jpg"
    };

    public static List<Carousel> newInstance() {
        List<Carousel> carouselList = new ArrayList<>();
        for (int i = 0; i < imageUrls.length; i++) {
            Carousel carousel = new Carousel();
            carousel.imageUrl = imageUrls[i];
            carouselList.add(carousel);
        }
        return carouselList;
    }
}
