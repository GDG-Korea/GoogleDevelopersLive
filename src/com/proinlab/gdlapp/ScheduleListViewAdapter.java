package com.proinlab.gdlapp;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
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

@SuppressLint("HandlerLeak")
class ScheduleListViewAdapter extends BaseAdapter implements OnClickListener {

	private LayoutInflater Inflater;
	private ArrayList<ArrayList<String>> arSrc;
	private int layout;
	private Bitmap[] bitmap;
	private ImageView[] Thumbnail;

	public static final int ARRAY_INDEX_TITLE = 0;
	public static final int ARRAY_INDEX_DATE = 1;
	public static final int ARRAY_INDEX_THUMBNAIL = 2;

	public ScheduleListViewAdapter(Context context,
			ArrayList<ArrayList<String>> aarSrc) {
		Inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		arSrc = aarSrc;
		layout = R.layout.schedule_content;

		Thumbnail = new ImageView[aarSrc.size()];
		bitmap = new Bitmap[aarSrc.size()];
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

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = Inflater.inflate(layout, parent, false);
		}

		Thumbnail[position] = (ImageView) convertView
				.findViewById(R.id.schedule_content_thumbnails);
		Thumbnail[position].setImageBitmap(null);
		
		TextView title = (TextView) convertView
				.findViewById(R.id.schedule_content_name);
		title.setText(arSrc.get(position).get(ARRAY_INDEX_TITLE));

		TextView date = (TextView) convertView
				.findViewById(R.id.schedule_content_date);
		date.setText(arSrc.get(position).get(ARRAY_INDEX_DATE));

		convertView.setTag(position);
		convertView.setOnClickListener(this);
		
		process(arSrc.get(position).get(ARRAY_INDEX_THUMBNAIL), position);
		
		return convertView;
	}

	private final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (bitmap[msg.what] == null)
				return;
			Thumbnail[msg.what].setImageBitmap(bitmap[msg.what]);
			Thumbnail[msg.what].setBackgroundDrawable(null);
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

	@Override
	public void onClick(View v) {
		
	}

}