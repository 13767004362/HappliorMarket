package com.zhongke.market.gui.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhongke.market.R;
import com.zhongke.market.download.DownloadInfo;
import com.zhongke.market.gui.view.download.DownloadOnclickListener;
import com.zhongke.market.gui.view.download.DownloadProgressButton;
import com.zhongke.market.model.ListAppInfo;
import com.zhongke.market.util.glide.GlideLoader;

import java.util.ArrayList;

/**
 * 普通列表通用Adapter
 *
 * @author lilijun
 */
public class SoftListAdapter2 extends DownloadBaseAdapter<GridView> {
    private Context mContext;
    public SoftListAdapter2(Context context, GridView listView,
                            ArrayList<ListAppInfo> appInfos) {
        super(context, listView, appInfos);
        this.mContext = context;
        setUpdateProgress(false);
    }

    public class SoftListViewHolder {
        /**
         * 下载图标
         */
        public ImageView icon;

        /**
         * 应用名称
         */
        public TextView appName;

        /**
         * 小编推荐
         */
        public TextView decribe;

        /**
         * 下载状态按钮
         */
        public DownloadProgressButton downloadStateButton;

        /**
         * 下载监听器
         */
        public DownloadOnclickListener listener;

        public void init(View baseView) {
            icon = baseView.findViewById(R.id.soft_list_app_icon);
            appName = baseView.findViewById(R.id.soft_list_app_name);
            decribe = baseView.findViewById(R.id.soft_list_describe_text);
            downloadStateButton = baseView.findViewById(R.id.soft_list_download_btn);
            listener = new DownloadOnclickListener(context);
        }

        public SoftListViewHolder(View baseView) {
            init(baseView);
        }

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SoftListViewHolder holder = null;
        ListAppInfo info = appInfos.get(position);
        if (convertView == null) {
            // 正常视图
            convertView = View.inflate(context,
                    R.layout.soft_list_adapter2, null);
            holder = new SoftListViewHolder(convertView);
            holder.downloadStateButton.downloadbtn
                    .setOnClickListener(holder.listener);
            convertView.setOnClickListener(itemClickListener);
            convertView.setTag(holder);

        } else {
            holder = (SoftListViewHolder) convertView.getTag();
        }

        // 正常视图
        convertView.setTag(R.id.soft_list_app_icon, info);

        holder.appName.setText(info.getName());
        holder.decribe.setText(info.getRecommendDescribe());

        holder.downloadStateButton.setInfo(info.getPackageName());
        holder.listener.setDownloadListenerInfo(info);

        GlideLoader.loadNetWorkResource(parent.getContext(), info.getIconUrl(), holder.icon);

        return convertView;
    }

    /**
     * 整个item的点击事件
     */
    private OnClickListener itemClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            ListAppInfo info = (ListAppInfo) v.getTag(R.id.soft_list_app_icon);
            // TODO 跳转到详情界面
            // 没有积分任务(跳转到应用详情界面)
//            DetailsActivity.startDetailsActivityById(mContext,
//                    info.getSoftId(), action);
        }
    };


    @Override
    protected void refreshData(DownloadInfo info, View view) {
        SoftListViewHolder viewHolder = (SoftListViewHolder) view.getTag();
        if (viewHolder != null) {
            viewHolder.downloadStateButton.setInfo(info.getPackageName());
            viewHolder.listener.setDownloadListenerInfo(info);
        }

    }
}
