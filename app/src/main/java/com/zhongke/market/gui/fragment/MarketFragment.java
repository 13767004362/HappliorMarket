package com.zhongke.market.gui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.GridView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zhongke.market.R;
import com.zhongke.market.base.BaseFragment;
import com.zhongke.market.gui.adapter.MarketKindAdapter;
import com.zhongke.market.gui.adapter.SoftListAdapter2;
import com.zhongke.market.model.ListAppInfo;
import com.zhongke.market.model.MarketKind;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by llj on 2017/10/31.
 */

public class MarketFragment extends BaseFragment {
    public static final String TAG=MarketFragment.class.getSimpleName();

    private GridView gridView;
    private SoftListAdapter2 adapter;

    private ArrayList<ListAppInfo> appInfos;

    private String json1 = "{\"versionCode\":72,\"app_name\":\"百合网婚恋交友\", \"pack_name\" : \"com.baihe\",\"icon\":\"http://yun.aiwan.hk/1441972578.png\"" +
            ",\"downloadTime\":142,\"appId\":1441972457,\"size\":7947931,\"downloadUrl\":\"http://yun.aiwan.hk/1441972507.apk\"}";
    private String json2 = "{\"versionCode\":460,\"app_name\":\"美图秀秀\", \"pack_name\" : \"com.mt.mtxx.mtxx\",\"icon\":\"http://yun.aiwan.hk/1442376357.png\"" +
            ",\"downloadTime\":507,\"appId\":1442376214,\"size\":38154111,\"downloadUrl\":\"http://yun.aiwan.hk/1442376228.apk\"}";
    private String json3 = "{\"versionCode\":17,\"app_name\":\"海洋猎人\", \"pack_name\" : \"com.jzsj.game.ocean.fisher\",\"icon\":\"http://yun.aiwan.hk/1441974636.png\"" +
            ",\"downloadTime\":83,\"appId\":1441974477,\"size\":18073835,\"downloadUrl\":\"http://yun.aiwan.hk/1441974504.apk\"}";

    private String json4 = "{\"versionCode\":50,\"app_name\":\"HOT男人\", \"pack_name\" : \"com.yoka.hotman\",\"icon\":\"http://yun.aiwan.hk/1442197921.png\"" +
            ",\"downloadTime\":189,\"appId\":1442197633,\"size\":8674746,\"downloadUrl\":\"http://yun.aiwan.hk/1442197697.apk\"}";

    private String json5 = "{\"versionCode\":73,\"app_name\":\"百度浏览器\", \"pack_name\" : \"com.baidu.browser.apps_sj\",\"icon\":\"http://yun.aiwan.hk/1442313154.png\"" +
            ",\"downloadTime\":58,\"appId\":1442312968,\"size\":11100801,\"downloadUrl\":\"http://yun.aiwan.hk/1442313008.apk\"}";

    private String json6 = "{\"versionCode\":16786712,\"app_name\":\"手机百度\", \"pack_name\" : \"com.baidu.searchbox\",\"icon\":\"http://yun.aiwan.hk/1442315201.png\"" +
            ",\"downloadTime\":47,\"appId\":1442315079,\"size\":8178120,\"downloadUrl\":\"http://yun.aiwan.hk/1442315103.apk\"}";

    private String json7 = "{\"versionCode\":40201,\"app_name\":\"考拉FM\", \"pack_name\" : \"com.itings.myradio\",\"icon\":\"http://yun.aiwan.hk/1442316668.png\"" +
            ",\"downloadTime\":137,\"appId\":1442316545,\"size\":15034682,\"downloadUrl\":\"http://yun.aiwan.hk/1442316568.apk\"}";

    private String json8 = "{\"versionCode\":621,\"app_name\":\"微信\", \"pack_name\" : \"com.tencent.mm\",\"icon\":\"http://yun.aiwan.hk/1442317953.png\"" +
            ",\"downloadTime\":191,\"appId\":1442317695,\"size\":32251172,\"downloadUrl\":\"http://yun.aiwan.hk/1442317737.apk\"}";

    private String json9 = "{\"versionCode\":423,\"app_name\":\"陌陌\", \"pack_name\" : \"com.immomo.momo\",\"icon\":\"http://yun.aiwan.hk/1442318932.png\"" +
            ",\"downloadTime\":6743,\"appId\":1442318759,\"size\":26536917,\"downloadUrl\":\"http://yun.aiwan.hk/1442318783.apk\"}";

    private String json10 = "{\"versionCode\":55,\"app_name\":\"nice\", \"pack_name\" : \"com.nice.main\",\"icon\":\"http://yun.aiwan.hk/1442367262.png\"" +
            ",\"downloadTime\":513,\"appId\":1442366881,\"size\":28560271,\"downloadUrl\":\"http://yun.aiwan.hk/1442366908.apk\"}";


    private String[] result = {json1, json2, json3, json4, json5, json6, json7, json8, json9, json10};

    private RecyclerView recyclerView;
    private MarketKindAdapter marketKindAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_market;
    }

    @Override
    protected void initAfterActivityCreated(View rootView, Bundle savedInstanceState) {
        initView(rootView);
    }

    private void initView(View rootView) {
        this.recyclerView = rootView.findViewById(R.id.market_recycler_view);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        this.marketKindAdapter = new MarketKindAdapter(R.layout.item_market_kind_layout, MarketKind.newInstance());
        this.recyclerView.setAdapter(this.marketKindAdapter);
        this.marketKindAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (position == marketKindAdapter.getCurrentSelect()) {
                    return;
                }
                marketKindAdapter.notifyPositionItem(position);
            }
        });

        gridView = rootView.findViewById(R.id.fragment_market_grid_view);
        appInfos = new ArrayList<>();
        adapter = new SoftListAdapter2(getActivity(), gridView, appInfos);
        initData();
        gridView.setAdapter(adapter);
    }

    private void initData() {

        int length = result.length;
        for (int i = 0; i < length; i++) {
            try {
                JSONObject jsonObject = new JSONObject(result[i]);
                ListAppInfo appInfo = new ListAppInfo();
                appInfo.setVersionCode(jsonObject.getInt("versionCode"));
                appInfo.setName(jsonObject.getString("app_name"));
                appInfo.setIconUrl(jsonObject.getString("icon"));
                appInfo.setDownloadCount(jsonObject.getLong("downloadTime"));
                appInfo.setSoftId(jsonObject.getLong("appId"));
                appInfo.setSize(jsonObject.getLong("size"));
                appInfo.setDownlaodUrl(jsonObject.getString("downloadUrl"));
                appInfo.setPackageName(jsonObject.getString("pack_name"));
                appInfo.setRecommendDescribe("一句话简单描述app相关信息");
                appInfos.add(appInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.unRegisterListener();
    }
}
