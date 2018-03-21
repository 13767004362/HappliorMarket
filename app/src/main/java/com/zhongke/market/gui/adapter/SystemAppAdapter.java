package com.zhongke.market.gui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhongke.market.R;
import com.zhongke.market.model.InstalledAppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${xinGen} on 2018/3/9.
 */

public class SystemAppAdapter extends RecyclerView.Adapter<SystemAppAdapter.ViewHolder> implements View.OnClickListener {
    private List<InstalledAppInfo> appInfos;

    public SystemAppAdapter() {
        this.appInfos = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder = new SystemAppAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.applist_item_view, parent, false));
        viewHolder.setClick(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.imageView.setImageDrawable(appInfos.get(position).getIcon());
        holder.textView.setText(appInfos.get(position).getName());
        holder.cancel.setVisibility(View.GONE);
        holder.deskLab.setText("系统");
    }

    @Override
    public int getItemCount() {
        return appInfos.size();
    }

    public void changeSourceData(List<InstalledAppInfo> appInfos) {
        this.appInfos.clear();
        this.appInfos.addAll(appInfos);
        this.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        int position = ((ViewHolder) v.getTag()).getLayoutPosition();
        InstalledAppInfo appModel = appInfos.get(position);
        Context context = v.getContext();
        //打开对应的运用程序
        if (appModel != null) {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(appModel.getPackageName());
            if (intent != null) {
                context.startActivity(intent);
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public ImageView cancel;
        public TextView deskLab;
        private ConstraintLayout constraintLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.item_name);
            this.imageView = itemView.findViewById(R.id.item_icon);
            this.cancel = itemView.findViewById(R.id.item_cancel_btn);
            this.deskLab = itemView.findViewById(R.id.item_desk_lab);
            this.constraintLayout = itemView.findViewById(R.id.item_lay);
        }

        public void setClick(View.OnClickListener onClickListener) {
            this.constraintLayout.setTag(this);
            this.constraintLayout.setOnClickListener(onClickListener);
        }

    }
}
