package com.zhongke.market.gui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.zhongke.market.model.InstalledAppInfo;

import java.util.List;

/**
 * Created by llj on 2017/11/2.
 */

public class MineAppAdapter extends BaseAdapter {
    private List<InstalledAppInfo> mList;

    public MineAppAdapter(List<InstalledAppInfo> list) {
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
