package com.proinlab.gdlapp;

import java.net.URI;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class ListFragment extends Fragment {

	public static final String ARG_SECTION_CATE = "ARG_SECTION_CATE";
	private boolean isThreadActive = false;
	private String category;

	private ListViewCustomAdapter mAdapter;
	private ListView mListView;
	private ArrayList<ArrayList<String>> arList;

	private static final int THREAD_HTMLPARSING = 0;

	private int page = 1;

	public ListFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		category = getArguments().getString(ARG_SECTION_CATE);
		arList = new ArrayList<ArrayList<String>>();
		mListView = new ListView(getActivity());
		downloadListThread(category, page);
		return mListView;
	}

	TextView tv;
	String htmldata;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case THREAD_HTMLPARSING:
				isThreadActive = false;
				mAdapter = new ListViewCustomAdapter(getActivity(), arList);
				mListView.setAdapter(mAdapter);
				break;
			}
		}
	};

	private boolean downloadListThread(final String cate, final int page) {
		if (isThreadActive) {
			return false;
		} else {
			new Thread(new Runnable() {
				@Override
				public void run() {
					isThreadActive = true;
					String url;
					if (cate.equals(getActivity().getResources()
							.getStringArray(R.array.category)[0]))
						url = "https://developers.google.com/live/browse#p="
								+ Integer.toString(page);
					else
						url = "https://developers.google.com/live/" + cate
								+ "/browse#p=" + Integer.toString(page);

					htmldata = HtmlToString(url, "utf-8");
					if (htmldata != null) {
						if (htmldata.indexOf("paging-cursor") != -1) {
							htmldata = htmldata.substring(
									htmldata.indexOf("<ol class="),
									htmldata.indexOf("paging-cursor"));
							while (htmldata.indexOf("<li>") != -1) {
								htmldata = htmldata.substring(htmldata
										.indexOf("<li>") + 4);
								String link, thumbnail, title, date;
								
								link = "https://developers.google.com"
										+ htmldata.substring(
												htmldata.indexOf("<a href=") + 9,
												htmldata.indexOf("\">"));

								htmldata = htmldata.substring(htmldata
										.indexOf("video-thumbnail"));
								thumbnail = "https:"
										+ htmldata.substring(
												htmldata.indexOf("src=\"") + 5,
												htmldata.indexOf("\" />"));
		
								htmldata = htmldata.substring(htmldata
										.indexOf("\" />") + 4);
								title = htmldata.substring(0,
										htmldata.indexOf("</a>"));

								title = REMOVE_UNNECESSORY(title);

								htmldata = htmldata.substring(htmldata
										.indexOf("</a>") + 4);
								if (htmldata.indexOf("</li>") == -1)
									date = htmldata.substring(0,
											htmldata.indexOf("<div"));
								else
									date = htmldata.substring(0,
											htmldata.indexOf("</li>"));
								date = REMOVE_UNNECESSORY(date);

								ArrayList<String> data = new ArrayList<String>();
								data.add(title);
								data.add(date);
								data.add(thumbnail);
								data.add(link);

								arList.add(data);
							}
						}
					}

					mHandler.post(new Runnable() {
						public void run() {
							mHandler.sendEmptyMessage(THREAD_HTMLPARSING);
						}
					});

				}
			}).start();
			return true;
		}
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
			page++;
		} catch (Exception e) {
			htmlSource = null;
		}
		return htmlSource;
	}

	public String REMOVE_UNNECESSORY(String data) {
		data = data.replaceAll(System.getProperty("line.separator"), "");
		data = data.replaceAll("<br>", ":");
		data = data.replaceAll("</ br>", ":");
		data = data.replaceAll("</br>", ":");
		data = data.replaceAll("<br/>", ":");
		data = data.replaceAll("<br />", ":");
		data = data.replaceAll("<BR>", ":");
		data = data.replaceAll("</ BR>", ":");
		data = data.replaceAll("</BR>", ":");
		data = data.replaceAll("<BR/>", ":");
		data = data.replaceAll("<BR />", ":");

		while (data.substring(0, 1).equals(" "))
			data = data.substring(1);
		while (data.substring(0, 1).equals("\\p{Space}"))
			data = data.substring(1);
		while (data.substring(0, 1).equals("&nbsp;"))
			data = data.substring(1);
		while (data.substring(0, 1).equals("\\p{Blank}"))
			data = data.substring(1);

		return data;
	}
}