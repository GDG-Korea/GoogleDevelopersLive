package com.proinlab.gdlapp;

import java.net.URI;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.proinlab.gdlapp.adapter.SectionsPagerAdapter;

public class MainActivity extends SherlockFragmentActivity implements ActionBar.TabListener {

	public static String[] categoryList = null;
	public static HashMap<String, String> categoryNames = new HashMap<String, String>();

	private SectionsPagerAdapter mSectionsPagerAdapter;
	private int CurrentItem = 0;

	private ViewPager mViewPager;
	public static AlertDialog alert = null;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0) {
				start();
			}
		};
	};

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			mViewPager.setCurrentItem(CurrentItem + 1);
			CurrentItem++;
			if (CurrentItem < mSectionsPagerAdapter.getCount()) {
				mViewPager.setCurrentItem(CurrentItem + 1);
				CurrentItem++;
			}
			return true;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			if (CurrentItem != 0) {
				mViewPager.setCurrentItem(CurrentItem - 1);
				CurrentItem--;
			}
			return true;
		default:
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading);

		new Thread(new Runnable() {
			@Override
			public void run() {
				String html = HtmlToString("https://developers.google.com/live/browse", "UTF-8");
				Elements categorys = Jsoup.parse(html).select("div#gdl-product-filter-container select option");
				categoryList = new String[categorys.size()];
				for (int i = 0; i < categorys.size(); i++) {
					if (categorys.get(i).attr("value").length() == 0) {
						categoryList[i] = "all";
						categoryNames.put(categoryList[i], "All");
					} else {
						categoryList[i] = categorys.get(i).attr("value");
						categoryNames.put(categoryList[i], categorys.get(i).text());
					}
				}

				handler.post(new Runnable() {
					@Override
					public void run() {
						handler.sendEmptyMessage(0);
					}
				});
			}
		}).start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case R.id.menu_developer:
			intent = new Intent(this, DevInfo.class);
			startActivity(intent);
			return true;
		}
		return false;
	}

	@Override
	public void onTabSelected(Tab tab, android.support.v4.app.FragmentTransaction ft) {
		mViewPager.setCurrentItem(tab.getPosition());
		CurrentItem = tab.getPosition();
	}

	@Override
	public void onTabUnselected(Tab tab, android.support.v4.app.FragmentTransaction ft) {
	}

	@Override
	public void onTabReselected(Tab tab, android.support.v4.app.FragmentTransaction ft) {
	}

	public void start() {
		setContentView(R.layout.activity_main);

		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowCustomEnabled(true);

		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), categoryList);

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab().setText(categoryNames.get(mSectionsPagerAdapter.getPageTitle(i))).setTabListener(this));
		}

		// YouTube Dialog
		LinearLayout linear = (LinearLayout) View.inflate(this, R.layout.loading_dialog, null);
		AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
		alt_bld.setView(linear);
		alert = alt_bld.create();
		alert.setCancelable(false);
	}

	private String HtmlToString(String addr, String incoding) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		String htmlSource;
		try {
			HttpGet request = new HttpGet();
			request.setURI(new URI(addr));
			HttpResponse response = httpclient.execute(request);
			HttpEntity entity = response.getEntity();
			htmlSource = EntityUtils.toString(entity, incoding);
		} catch (Exception e) {
			htmlSource = null;
		}
		return htmlSource;
	}
}
