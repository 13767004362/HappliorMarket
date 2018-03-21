package com.zhongke.market.gui.adapter;

import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhongke.market.R;
import com.zhongke.market.model.MarketKind;

import java.util.List;

/**
 * Created by ${xingen} on 2017/10/30.
 */

public class MarketKindAdapter extends BaseQuickAdapter<MarketKind, BaseViewHolder> {
    public MarketKindAdapter(@LayoutRes int layoutResId, @Nullable List<MarketKind> data) {
        super(layoutResId, data);
        this.currentSelect = 0;
    }

    /**
     * 通知某个Item被选中
     *
     * @param position
     */
    public void notifyPositionItem(int position) {
        if (position == currentSelect) {
            return;
        }
        int before_position = this.currentSelect;
        this.currentSelect = position;
        notifyItemChanged(before_position);
        notifyItemChanged(this.currentSelect);
    }

    private int currentSelect;

    public int getCurrentSelect() {
        return currentSelect;
    }

    @Override
    protected void convert(BaseViewHolder helper, MarketKind item) {
        TextView textView = helper.getView(R.id.market_kind_select_text_iv);
        ImageView imageView = helper.getView(R.id.market_kind_select_iv);
        int position  = helper.getLayoutPosition();
        textView.setText(item.name);
        if (currentSelect == position) {
            textView.setTextColor(Color.parseColor("#f8e3d5"));
            imageView.setImageResource(R.mipmap.market_kind_select_bg);
        } else {
            textView.setTextColor(Color.parseColor("#8e5734"));
            imageView.setImageResource(R.mipmap.market_kind_not_select_bg);
        }

    }
}
