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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

		Thumbnail = new ImageView[arSrc.size()];
		bitmap = new Bitmap[arSrc.size()];
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
		Toast.makeText(maincon, data.get(ARRAY_INDEX_LINK), Toast.LENGTH_SHORT)
				.show();
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

}