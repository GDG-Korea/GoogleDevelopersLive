package com.proinlab.gdlapp;

import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.youtube.player.YouTubeIntents;

@SuppressLint("HandlerLeak")
public class Contents extends FragmentActivity {

	public static final String EXTRAS_CONTENTS_LINK = "EXTRAS_CONTENTS_LINK";
	public static final String EXTRAS_CONTENTS_TITLE = "EXTRAS_CONTENTS_TITLE";
	public static final String EXTRAS_CONTENTS_DATE = "EXTRAS_CONTENTS_DATE";
	public static final String EXTRAS_CONTENTS_THUMBNAIL = "EXTRAS_CONTENTS_THUMBNAIL";

	private String youtubelink;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contents);

		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		getYouTubeUrl();

	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			Intent intent = YouTubeIntents.createPlayVideoIntentWithOptions(
					Contents.this, youtubelink, true, false);
			startActivity(intent);
			finish();
		}
	};

	private void getYouTubeUrl() {
		new Thread(new Runnable() {
			@Override
			public void run() {

				String link = getIntent().getExtras().getString(
						EXTRAS_CONTENTS_LINK);
				String htmldata = HtmlToString(link, "utf-8");

				if (htmldata != null)
					if (htmldata
							.indexOf("<iframe id=\"ytplayer\" type=\"text/html\"") != -1) {
						htmldata = htmldata.substring(htmldata
								.indexOf("<iframe id=\"ytplayer\" type=\"text/html\""));
						youtubelink = htmldata.substring(htmldata
								.indexOf("src=\"") + 5);
						youtubelink = "https:"
								+ youtubelink.substring(0,
										youtubelink.indexOf("\""));
						youtubelink = youtubelink.substring(youtubelink
								.lastIndexOf("/") + 1);
						Log.i("TAG", youtubelink);
					}

				mHandler.post(new Runnable() {
					public void run() {
						mHandler.sendEmptyMessage(0);
					}
				});

			}
		}).start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
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
