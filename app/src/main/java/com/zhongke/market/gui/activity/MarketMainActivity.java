package com.zhongke.market.gui.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhongke.market.R;
import com.zhongke.market.base.BaseActivity;
import com.zhongke.market.gui.fragment.MarketFragment;
import com.zhongke.market.gui.fragment.MineAppFragment;
import com.zhongke.market.gui.fragment.SystemAppFragment;
import com.zhongke.market.gui.view.SlideShowView;
import com.zhongke.market.model.Carousel;

import java.util.List;

/**
 * Created by ${xingen} on 2017/10/30.
 */

public class MarketMainActivity extends BaseActivity implements View.OnClickListener {


    private SlideShowView slideShowView;

    private RelativeLayout contentLay;

    private TextView[] tabs = new TextView[3];

    private Fragment[] mFragments = new Fragment[3];
    private String[] tags = {MineAppFragment.TAG, MarketFragment.TAG, SystemAppFragment.TGA};

    private int curTab = -1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_market_main;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        tabs[0] = (TextView) findViewById(R.id.market_main_tab_0);
        tabs[1] = (TextView) findViewById(R.id.market_main_tab_1);
        tabs[2]=(TextView) findViewById(R.id.market_main_tab_2);
        tabs[0].setOnClickListener(this);
        tabs[1].setOnClickListener(this);
        tabs[2].setOnClickListener(this);
     //   this.slideShowView = (SlideShowView) findViewById(R.id.market_slide_show_view);
       // this.slideShowView.initDate(Carousel.newInstance());
        this.contentLay = (RelativeLayout) findViewById(R.id.market_content_layout);
        changeTab(0);
    }

    @Override
    protected void init() {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.i("llj","dddd");
        super.onConfigurationChanged(newConfig);
    }

/*    @Override
    protected void onResume() {
        super.onResume();
        if (this.slideShowView != null) {
            this.slideShowView.startSlide();
        }
    }*/

/*    @Override
    protected void onPause() {
        super.onPause();
        if (this.slideShowView != null) {
            this.slideShowView.stopSlide();
        }
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.market_main_tab_0:
                changeTab(0);
                break;
            case R.id.market_main_tab_1:
                changeTab(1);
                break;
            case R.id.market_main_tab_2:
                changeTab(2);
                break;
            default:
                break;
        }
    }

    private void changeTab(int tab) {
        if (curTab == tab) {
            return;
        }
        curTab = tab;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideFragment(transaction);
        if (mFragments[tab] == null) {
            mFragments[tab] = createFragment(tab);
            transaction.add(R.id.market_content_layout, mFragments[tab], tags[curTab]);
        } else {
            transaction.show(mFragments[tab]);
        }
        for (int i = 0; i < tabs.length; ++i) {
            tabs[i].setSelected(i == tab);
        }
        transaction.commitAllowingStateLoss();
    }
    private Fragment createFragment(int tab) {
        return tab == 0 ? new MineAppFragment() :( tab==1? new MarketFragment():SystemAppFragment.newInstance());
    }
    private void hideFragment(FragmentTransaction transaction) {
        List<Fragment> list = getSupportFragmentManager().getFragments();
        for (Fragment fragment : list) {
            if (fragment != null) {
                transaction.hide(fragment);
            }
        }
    }

}
