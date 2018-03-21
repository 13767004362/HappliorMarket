package com.zhongke.market.gui.fragment;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.zhongke.market.R;
import com.zhongke.market.base.BaseFragment;
import com.zhongke.market.gui.adapter.SystemAppAdapter;
import com.zhongke.market.util.rxjava.ObservableBuilder;

import rx.Scheduler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ${xinGen} on 2018/3/9.
 */

public class SystemAppFragment extends BaseFragment {
    public static final String TGA = SystemAppFragment.class.getSimpleName();
    private final CompositeSubscription compositeSubscription = new CompositeSubscription();
    private RecyclerView recyclerView;
    private SystemAppAdapter systemAppAdapter;
    public static SystemAppFragment newInstance() {
        return new SystemAppFragment();
    }
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_systemapp;
    }
    @Override
    protected void initAfterActivityCreated(View rootView, Bundle savedInstanceState) {
        querySystemApp();
        initView(rootView);
    }
    private void querySystemApp() {
        //包名前缀
        String packageName = "com.android";
        Subscription subscription = ObservableBuilder.querySpecifiedPackageName(getContext(), packageName)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    systemAppAdapter.changeSourceData(result);
                }, error -> {
                    Toast.makeText(getContext().getApplicationContext(),"查询系统程序出错",Toast.LENGTH_SHORT).show();
                });
        this.compositeSubscription.add(subscription);
    }

    /**
     * 初始化控件
     *
     * @param rootView
     */
    private void initView(View rootView) {
        this.recyclerView = rootView.findViewById(R.id.system_app_recycler_view);
        this.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 8));
        this.systemAppAdapter=new SystemAppAdapter();
        this.recyclerView.setAdapter(this.systemAppAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.compositeSubscription.clear();
    }
}
