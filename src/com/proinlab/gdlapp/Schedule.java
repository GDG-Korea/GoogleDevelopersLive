package com.proinlab.gdlapp;

import java.net.URI;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.LinearLayout.LayoutParams;

@SuppressLint("HandlerLeak")
public class Schedule extends Activity {

	private ScheduleListViewAdapter mAdapter;
	private ListView mListView;
	private ArrayList<ArrayList<String>> arList;
	private View footer;
	private LayoutInflater mInflater;

	private String htmldata = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule);

		String titleStr = getResources().getString(R.string.schedule_message);
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(titleStr);

		htmldata = null;

		arList = new ArrayList<ArrayList<String>>();
		LinearLayout entirelayout = (LinearLayout) findViewById(R.id.schedule_layout);
		LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mListView = new ListView(this);
		mListView.setLayoutParams(llp);
		entirelayout.addView(mListView);

		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		footer = mInflater.inflate(R.layout.footer, null);
		mListView.addFooterView(footer);

		mAdapter = new ScheduleListViewAdapter(this, arList);
		mListView.setAdapter(mAdapter);

		downloadListThread();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		default:
			return false;
		}
		return true;
	}

	// update list
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			mAdapter = new ScheduleListViewAdapter(Schedule.this, arList);
			mListView.setAdapter(mAdapter);
			mListView.removeFooterView(footer);
		}
	};

	// loading list
	private boolean downloadListThread() {

		new Thread(new Runnable() {
			@Override
			public void run() {

				htmldata = HtmlToString("https://developers.google.com/live/",
						"utf-8");

				if (htmldata != null) {
					if (htmldata.indexOf("<header class=\"future\">") != -1) {
						htmldata = htmldata.substring(htmldata
								.indexOf("<header class=\"future\">"), htmldata
								.indexOf("<section id=\"previous-shows\">"));
						while (htmldata
								.indexOf("<div class=\"event-timezone\">") != -1) {
							htmldata = htmldata.substring(htmldata
									.indexOf("<div class=\"event-timezone\">") + 28);
							String thumbnail, title, date, location;

							date = htmldata.substring(0,
									htmldata.indexOf("<span title"));
							date = REMOVE_UNNECESSORY(date);
							htmldata = htmldata.substring(htmldata
									.indexOf("span title=") + 12);
							location = htmldata.substring(0,
									htmldata.indexOf("\">"));
							location = REMOVE_UNNECESSORY(location);
							date = date + " (" + location + ")";

							htmldata = htmldata.substring(htmldata
									.indexOf("<summary>") + 9);
							title = htmldata.substring(0,
									htmldata.indexOf("<img src"));
							title = REMOVE_UNNECESSORY(title);

							htmldata = htmldata.substring(htmldata
									.indexOf("<img src=\"") + 10);
							thumbnail = htmldata.substring(0,
									htmldata.indexOf("\""));
							thumbnail = "https://developers.google.com"
									+ thumbnail;

							ArrayList<String> data = new ArrayList<String>();
							data.add(title);
							data.add(date);
							data.add(thumbnail);
							arList.add(data);
						}
					}
				}

				mHandler.post(new Runnable() {
					public void run() {
						mHandler.sendEmptyMessage(0);
					}
				});

			}
		}).start();
		return true;

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

	private String REMOVE_UNNECESSORY(String data) {
		data = data.replaceAll(System.getProperty("line.separator"), "");
		data = data.replaceAll("<br>", "");
		data = data.replaceAll("</ br>", "");
		data = data.replaceAll("</br>", "");
		data = data.replaceAll("<br/>", "");
		data = data.replaceAll("<br />", "");
		data = data.replaceAll("<BR>", "");
		data = data.replaceAll("</ BR>", "");
		data = data.replaceAll("</BR>", "");
		data = data.replaceAll("<BR/>", "");
		data = data.replaceAll("<BR />", "");

		while (data.substring(0, 1).equals(" "))
			data = data.substring(1);
		while (data.substring(0, 1).equals("\\p{Space}"))
			data = data.substring(1);
		while (data.substring(0, 1).equals("&nbsp;"))
			data = data.substring(1);
		while (data.substring(0, 1).equals("\\p{Blank}"))
			data = data.substring(1);
		while (data.substring(data.length() - 1, data.length()).equals(" "))
			data = data.substring(0, data.length() - 1);
		while (data.substring(data.length() - 1, data.length()).equals(
				"\\p{Space}"))
			data = data.substring(0, data.length() - 1);
		while (data.substring(data.length() - 1, data.length())
				.equals("&nbsp;"))
			data = data.substring(0, data.length() - 1);
		while (data.substring(data.length() - 1, data.length()).equals(
				"\\p{Blank}"))
			data = data.substring(0, data.length() - 1);

		return data;
	}

}