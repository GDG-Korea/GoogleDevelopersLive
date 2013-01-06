package com.proinlab.gdlapp;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeIntents;

@SuppressLint("HandlerLeak")
class ListViewCustomAdapter extends BaseAdapter implements OnClickListener {
	private Context maincon;
	private LayoutInflater Inflater;
	private ArrayList<ArrayList<String>> arSrc;
	private int layout;
	private Bitmap[] bitmap;
	private ImageView[] Thumbnail;

	public static final int ARRAY_INDEX_TITLE = 0;
	public static final int ARRAY_INDEX_DATE = 1;
	public static final int ARRAY_INDEX_THUMBNAIL = 2;
	public static final int ARRAY_INDEX_LINK = 3;

	public ListViewCustomAdapter(Context context,
			ArrayList<ArrayList<String>> aarSrc) {
		maincon = context;
		Inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		arSrc = aarSrc;
		layout = R.layout.listview_contents;

		Thumbnail = new ImageView[10000];
		bitmap = new Bitmap[10000];

	}

	public int getCount() {
		return arSrc.size();
	}

	public ArrayList<String> getItem(int position) {
		return arSrc.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	// 각 항목의 뷰 생성
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = Inflater.inflate(layout, parent, false);
		}
		
		Thumbnail[position] = (ImageView) convertView
				.findViewById(R.id.listview_content_thumbnail);
		Thumbnail[position].setImageBitmap(null);
		
		TextView title = (TextView) convertView
				.findViewById(R.id.listview_content_name);
		title.setText(arSrc.get(position).get(ARRAY_INDEX_TITLE));

		TextView date = (TextView) convertView
				.findViewById(R.id.listview_content_date);
		date.setText(arSrc.get(position).get(ARRAY_INDEX_DATE));

		convertView.setTag(position);
		convertView.setOnClickListener(this);

		process(arSrc.get(position).get(ARRAY_INDEX_THUMBNAIL), position);

		return convertView;
	}

	public void onClick(View v) {
		int position = (Integer) v.getTag();
		
		ArrayList<String> data = getItem(position);
		MainActivity.alert.show();
		getYouTubeUrl(data);
//		Intent intent = new Intent(maincon, Contents.class);
//		intent.putExtra(Contents.EXTRAS_CONTENTS_LINK,
//				data.get(ARRAY_INDEX_LINK));
//		intent.putExtra(Contents.EXTRAS_CONTENTS_TITLE,
//				data.get(ARRAY_INDEX_TITLE));
//		intent.putExtra(Contents.EXTRAS_CONTENTS_DATE,
//				data.get(ARRAY_INDEX_DATE));
//		intent.putExtra(Contents.EXTRAS_CONTENTS_THUMBNAIL,
//				data.get(ARRAY_INDEX_THUMBNAIL));
//		maincon.startActivity(intent);
	}

	private String youtubelink;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			Intent intent = YouTubeIntents.createPlayVideoIntentWithOptions(
					maincon, youtubelink, true, false);
			maincon.startActivity(intent);
			
			if(MainActivity.alert.isShowing())
				MainActivity.alert.dismiss();
		}
	};
	
	private void getYouTubeUrl(final ArrayList<String> data) {
		new Thread(new Runnable() {
			@Override
			public void run() {

				String htmldata = HtmlToString(data.get(ARRAY_INDEX_LINK), "utf-8");

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
					}

				mHandler.post(new Runnable() {
					public void run() {
						mHandler.sendEmptyMessage(0);
					}
				});

			}
		}).start();
	}
	
	private final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (bitmap == null)
				return;
			Thumbnail[msg.what].setImageBitmap(bitmap[msg.what]);
		}
	};
	
	private void process(final String url, final int position) {
		new Thread() {
			@Override
			public void run() {
				try {
					InputStream is = new URL(url).openStream();
					bitmap[position] = BitmapFactory.decodeStream(is);
					is.close();

					handler.post(new Runnable() {
						public void run() {
							handler.sendEmptyMessage(position);
						}
					});
				} catch (Exception e) {
				}
			}
		}.start();
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