package com.zhongke.market.gui.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.zhongke.market.R;
import com.zhongke.market.base.BaseFragment;
import com.zhongke.market.db.ContentProviderConfig;
import com.zhongke.market.gui.adapter.CollectionAppAdapter;
import com.zhongke.market.model.InstalledAppInfo;
import com.zhongke.market.util.rxjava.ObservableBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by llj on 2017/11/2.
 * <p>
 * 我的APP界面
 */

public class MineAppFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
    public static final String TAG = MineAppFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private final CompositeSubscription compositeSubscription = new CompositeSubscription();
    private CollectionAppAdapter collectionAppAdapter;

    private TextView showOrHideBtn, manageOrCancelBtn;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine_app;
    }

    private LoaderManager loaderManager;

    @Override
    protected void initAfterActivityCreated(View rootView, Bundle savedInstanceState) {
        this.loaderManager = this.getActivity().getSupportLoaderManager();
        this.loaderManager.initLoader(LODER_ID, null, this);
        this.mRecyclerView = rootView.findViewById(R.id.fragment_mine_app_recycler_view);
        this.mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 8));
        this.collectionAppAdapter = new CollectionAppAdapter();
        this.mRecyclerView.setAdapter(this.collectionAppAdapter);
        this.itemTouchHelper = new ItemTouchHelper(new RecyclerViewCallBack());
        this.itemTouchHelper.attachToRecyclerView(this.mRecyclerView);
        this.showOrHideBtn = rootView.findViewById(R.id.mine_app_show_or_hide_btn);
        this.manageOrCancelBtn = rootView.findViewById(R.id.mine_app_manage_or_cancel_btn);
        this.showOrHideBtn.setOnClickListener(this);
        this.manageOrCancelBtn.setOnClickListener(this);
    }


    private ItemTouchHelper itemTouchHelper;

    private static final int LODER_ID = 10;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LODER_ID) {
            return new CursorLoader(getActivity(), ContentProviderConfig.URI_ALL_INSTANCE_APP, null, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LODER_ID) {
            Log.i(TAG, " MineAppFragment onLoadFinished ");
            excuteTask(data);
        }
    }

    private List<InstalledAppInfo> all_instanceAppInfo_list;

    /**
     * 执行查询数据库，且匹配安装程序数据的操作
     * @param data
     */
    private void excuteTask(Cursor data) {
        Subscription subscription = ObservableBuilder.createInstalledAppInfo(getContext().getApplicationContext(), data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(installedAppInfos -> {
                            all_instanceAppInfo_list = installedAppInfos;
                            filterConditionTask();
                        }
                );
        this.compositeSubscription.add(subscription);
    }

    /**
     * 执行是否显示桌面程序的筛选任务
     */
    private void filterConditionTask() {
        Subscription subscription = ObservableBuilder.createFilterAction(collectionAppAdapter.isShowDesk(),all_instanceAppInfo_list)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list ->
                        collectionAppAdapter.changeSourceData( list)
                );
        this.compositeSubscription.add(subscription);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        loaderManager.destroyLoader(LODER_ID);
        this.compositeSubscription.clear();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mine_app_show_or_hide_btn:
                // 显示/隐藏桌面app
                if (showOrHideBtn.isSelected()) {
                    // 当前是"显示"文本，点击显示圆盘app
                    collectionAppAdapter.setShowDesk(true);
                    showOrHideBtn.setText("隐藏");
                } else {
                    // 当前是"隐藏"文本，点击隐藏圆盘app
                    collectionAppAdapter.setShowDesk(false);
                    showOrHideBtn.setText("显示");
                }
                filterConditionTask();
                showOrHideBtn.setSelected(!showOrHideBtn.isSelected());
                break;
            case R.id.mine_app_manage_or_cancel_btn:
                // 管理/取消按钮
                collectionAppAdapter.setManage(!manageOrCancelBtn.isSelected());
                manageOrCancelBtn.setSelected(!manageOrCancelBtn.isSelected());
                if (manageOrCancelBtn.isSelected()) {
                    manageOrCancelBtn.setText("取消");
                } else {
                    manageOrCancelBtn.setText("管理");
                }
                break;
            default:
                break;
        }
    }

    /**
     * 定义一个CallBack对象，适用于拖拽或者剔除状态的回调.
     */
    private class RecyclerViewCallBack extends ItemTouchHelper.Callback {
        private List<InstalledAppInfo> changeList = new ArrayList<>();
        private int fromPosition, toPosition;
        private Subscription subscription;
        /**
         * 用于标记位置发生改变的标识
         */
        private  boolean isMoved=false;
        /**
         * 设置是否滑动时间，以及拖拽的方向
         *
         * @param recyclerView
         * @param viewHolder
         * @return
         */
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            Log.i(TAG, " getMovementFlags ");
            //网格布局，拖拽方向是down,up,Right
            if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                final int swipeFlags = 0;
                return makeMovementFlags(dragFlags, swipeFlags);
            }//列表布局，拖拽方向是down,up
            else {
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                final int swipeFlags = 0;
                return makeMovementFlags(dragFlags, swipeFlags);
            }
        }
        /**
         * 拖动时候，不断回调：
         * <p>
         * 进行正在拖动的Item和集合的item进行交换元素，通知适配器。
         *
         * @param recyclerView
         * @param viewHolder
         * @param target
         * @return
         */
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            Log.i(TAG, " onMove ");
            changeList.clear();
            isMoved=true;
            //得到当拖拽的viewHolder的Position
            fromPosition = viewHolder.getAdapterPosition();
            //拿到当前拖拽到的item的viewHolder
            toPosition = target.getAdapterPosition();
            //往后拖
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(collectionAppAdapter.getAppInfos(), i, i + 1);
                }
                changeList.addAll(collectionAppAdapter.getAppInfos());
            }//往前拖
            else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(collectionAppAdapter.getAppInfos(), i, i - 1);
                }
                changeList.addAll(collectionAppAdapter.getAppInfos());
            }
            collectionAppAdapter.notifyItemMoved(fromPosition, toPosition);
            return true;
        }
        private InstalledAppInfo createAppfo(int i, int change) {
            InstalledAppInfo installedAppInfo = new InstalledAppInfo();
            installedAppInfo.position = collectionAppAdapter.getAppInfos().get(i).position;
            installedAppInfo.setPackageName(collectionAppAdapter.getAppInfos().get(change).getPackageName());
            installedAppInfo.visible = collectionAppAdapter.getAppInfos().get(change).visible;
            return installedAppInfo;
        }

        /**
         * 完成替换操作后调用的方法
         *
         * @param viewHolder
         * @param direction
         */
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            Log.i(TAG, " onSwiped ");
            writeDB();
        }

        /**
         * 长按选中Item时候调用，可以进行改变背景，或者放大等动作
         *
         * @param viewHolder
         * @param actionState
         */
        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            Log.i(TAG, " onSelectedChanged ");
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                ((CollectionAppAdapter.ViewHolder) viewHolder).itemLay.setBackgroundResource(R.color.drag_item_bg);
//                viewHolder.itemView.setBackgroundResource(R.color.drag_item_bg);
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        /**
         * 手指松开时候，进行长按进行的动作，的还原。
         *
         * @param recyclerView
         * @param viewHolder
         */
        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            Log.i(TAG, " clearView ");
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setBackgroundResource(android.R.color.transparent);
            // collectionAppAdapter.notifyDataSetChanged();
            Log.i("llj", "changeList.size()------>>>" + changeList.size());
            if (isMoved){
                isMoved=false;
                writeDB();
            }
        }

        private  void  writeDB(){
            if (changeList.size() > 0) {
                if (subscription!=null&&!subscription.isUnsubscribed()) {
                    compositeSubscription.remove(subscription);
                }
                subscription = ObservableBuilder.createUpdateAppInfo(getContext().getApplicationContext(), changeList)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(b ->
                                        changeList.clear()
                                , error ->
                                        changeList.clear()
                        );

                compositeSubscription.add(subscription);
            }
        }
    }
}
