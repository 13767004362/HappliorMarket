package com.zhongke.market.gui.adapter;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhongke.market.R;
import com.zhongke.market.control.SoftwareManager;
import com.zhongke.market.model.InstalledAppInfo;

import java.util.List;

/**
 * Created by ${xingen} on 2017/11/3.
 */

public class CollectionAppAdapter extends RecyclerView.Adapter<CollectionAppAdapter.ViewHolder> {
    private List<InstalledAppInfo> appInfos;
    /**
     * 是否是管理模式
     */
    private boolean isManage = false;

    /**
     * 是否显示圆盘上的app
     */
    private boolean isShowDesk = true;

    public void setShowDesk(boolean showDesk) {
        isShowDesk = showDesk;
    }

    public boolean isShowDesk() {
        return isShowDesk;
    }

    public CollectionAppAdapter() {
        this.isShowDesk=true;
    }
    public CollectionAppAdapter setManage(boolean manage) {
        isManage = manage;
        this.notifyDataSetChanged();
        return this;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.applist_item_view, parent, false));
    }
    public List<InstalledAppInfo> getAppInfos() {
        return appInfos;
    }

    public void changeSourceData(List<InstalledAppInfo> appInfos) {
        this.appInfos=appInfos;
        this.notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
         int visible=appInfos.get(position).visible;
        //visible为0，显示在圆盘上。
        if (visible==1) {
            // 不显示圆盘上的app
            holder.deskLab.setVisibility(View.GONE);
        } else {
            holder.deskLab.setVisibility(View.VISIBLE);
        }
        holder.imageView.setImageDrawable(appInfos.get(position).getIcon());
        holder.textView.setText(appInfos.get(position).getName());
        holder.cancel.setVisibility(isManage ? View.VISIBLE : View.GONE);
        holder.cancel.setTag(position);
        holder.itemLay.setTag(position);
    }
    @Override
    public int getItemCount() {
        return  appInfos==null? 0:appInfos.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout itemLay;
        public ImageView imageView;
        public TextView textView;
        public ImageView cancel;
        public TextView deskLab;

        public ViewHolder(View itemView) {
            super(itemView);
            itemLay = itemView.findViewById(R.id.item_lay);
            textView = itemView.findViewById(R.id.item_name);
            imageView = itemView.findViewById(R.id.item_icon);
            cancel = itemView.findViewById(R.id.item_cancel_btn);
            deskLab = itemView.findViewById(R.id.item_desk_lab);
            itemLay.setOnClickListener(itemClickListener);
            cancel.setOnClickListener(cancelClickListener);
        }
    }

    /**
     * 整个item的点击事件
     */
    private View.OnClickListener itemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 在管理状态下 点击不打开应用
            if (isManage) {
                return;
            }
            int position = (int) v.getTag();
            InstalledAppInfo appInfo = appInfos.get(position);
            if (appInfo == null) {
                return;
            }
            SoftwareManager.getInstance().openSoftware(appInfo.getPackageName());
        }
    };

    /**
     * 取消、卸载按钮点击事件
     */
    private View.OnClickListener cancelClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            InstalledAppInfo appInfo = appInfos.get(position);
            if (appInfo == null) {return;}
            SoftwareManager.getInstance().uninstallApk(v.getContext(), appInfo.getPackageName());
        }
    };
}
