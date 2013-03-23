
package com.proinlab.gdlapp;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.RejectedExecutionException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
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

    public static final int ARRAY_INDEX_TITLE = 0;
    public static final int ARRAY_INDEX_DATE = 1;
    public static final int ARRAY_INDEX_THUMBNAIL = 2;

    public ScheduleListViewAdapter(Context context,
            ArrayList<ArrayList<String>> aarSrc) {
        Inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        arSrc = aarSrc;
        layout = R.layout.schedule_content;
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

    @SuppressLint("NewApi")
	public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = Inflater.inflate(layout, parent, false);
        }

        ImageView thumbnail = (ImageView) convertView
                .findViewById(R.id.schedule_content_thumbnails);
        thumbnail.setImageBitmap(null);

        TextView title = (TextView) convertView
                .findViewById(R.id.schedule_content_name);
        title.setText(arSrc.get(position).get(ARRAY_INDEX_TITLE));

        TextView date = (TextView) convertView
                .findViewById(R.id.schedule_content_date);
        date.setText(arSrc.get(position).get(ARRAY_INDEX_DATE));

        convertView.setTag(position);
        convertView.setOnClickListener(this);

        final ThumbnailAsyncTask oldTask = (ThumbnailAsyncTask) thumbnail.getTag();
		if (oldTask != null) {
			oldTask.cancel(false);
		}

		final ThumbnailAsyncTask task = new ThumbnailAsyncTask(thumbnail);
		thumbnail.setTag(task);
		try {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, position);
			} else {
				task.execute(position);
			}
		} catch (RejectedExecutionException e) {
		} catch (OutOfMemoryError e) {

		}
        
        return convertView;
    }

	private class ThumbnailAsyncTask extends AsyncTask<Integer, Void, Bitmap> {
		private final ImageView mTarget;

		public ThumbnailAsyncTask(ImageView target) {
			mTarget = target;
		}

		@Override
		protected void onPreExecute() {
			mTarget.setTag(this);
		}

		@Override
		protected Bitmap doInBackground(Integer... params) {
			final int pos = params[0];
			final String url = arSrc.get(pos).get(ARRAY_INDEX_THUMBNAIL);

			try {
				InputStream is = new URL(url).openStream();
				final Bitmap result = BitmapFactory.decodeStream(is);
				is.close();
				return result;
			} catch (Exception e) {

			}

			return null;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (mTarget.getTag() == this) {
				mTarget.setImageBitmap(result);
				mTarget.setBackgroundDrawable(null);
				mTarget.setTag(null);
			}
		}
	}
    
    @Override
    public void onClick(View v) {

    }

}
