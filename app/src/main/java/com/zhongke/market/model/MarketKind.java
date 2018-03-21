package com.zhongke.market.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${xingen} on 2017/10/30.
 */

public class MarketKind {
    public boolean select;
    public String name;


    public static List<MarketKind> newInstance() {
        List<MarketKind> list = new ArrayList<>();
        for (int i = 0; i < 4; ++i) {
            MarketKind marketKind = new MarketKind();
            switch (i) {
                case 0:
                    marketKind.name = "学习类";
                    marketKind.select = true;
                    break;
                case 1:
                    marketKind.name = "娱乐类";
                    break;
                case 2:
                    marketKind.name = "健康类";
                    break;
                case 3:
                    marketKind.name = "图片处理";
                    break;
                default:
                    break;
            }
            list.add(marketKind);
        }
        return list;
    }
}
