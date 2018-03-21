package com.zhongke.market.gui.view.download;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.zhongke.market.control.SoftwareManager;
import com.zhongke.market.download.DownloadInfo;
import com.zhongke.market.download.DownloadManager;


public class DownloadButton extends AppCompatButton
{
	private DownloadManager manager = null;

	private SoftwareManager softwareManager = null;

	private int appState = AppStateConstants.APP_NO_INSTALL;

	public DownloadButton(Context context)
	{
		super(context);
		init(context);
	}

	public DownloadButton(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	private void init(Context context)
	{
		setPadding(0, 0, 0, 0);
		manager = DownloadManager.shareInstance();
		softwareManager = SoftwareManager.getInstance();

		setSingleLine();
		setTextColor(Color.parseColor(AppStateConstants
				.getAppStateTextColor(appState)));
		setText(AppStateConstants.getAppStateText(appState));
		setBackgroundResource(AppStateConstants
				.getAppStateBackgroundResource(appState));
		setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
	}

	/**
	 * 设置按钮的背景图片和文字
	 * 
	 * @param packageName
	 */

	public void setInfo(String packageName)
	{
		DownloadInfo info = manager.queryDownload(packageName);
		appState = AppStateConstants.APP_NO_INSTALL;

		if (info != null)
		{
			appState = AppStateConstants.getAppState(
					AppStateConstants.TYPE_DOWNLOAD, info.getState());
		} else
		{
			appState = AppStateConstants.getAppState(
					AppStateConstants.TYPE_SOFTMANAGER,
					softwareManager.getStateByPackageName(packageName));
		}
		setTextColor(Color.parseColor(AppStateConstants
				.getAppStateTextColor(appState)));
		setText(AppStateConstants.getAppStateText(appState));
		setBackgroundResource(AppStateConstants
				.getAppStateBackgroundResource(appState));
	}

}
