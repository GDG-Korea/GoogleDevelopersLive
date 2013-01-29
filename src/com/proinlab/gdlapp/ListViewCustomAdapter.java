
package com.proinlab.gdlapp;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
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
import android.graphics.BitmapFactory.Options;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
class ListViewCustomAdapter extends BaseAdapter implements OnClickListener {
    private Context maincon;
    private LayoutInflater Inflater;
    private ArrayList<ArrayList<String>> arSrc;
    private int layout;
    private LruCache<Integer, Bitmap> bitmapCache;

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

        bitmapCache = new LruCache<Integer, Bitmap>(10 * 1024 * 1024) {
            @Override
            protected int sizeOf(Integer key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
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

        ImageView imageView = (ImageView) convertView
                .findViewById(R.id.listview_content_thumbnail);
        imageView.setImageBitmap(null);
        imageView.setTag(Integer.valueOf(position));

        TextView title = (TextView) convertView
                .findViewById(R.id.listview_content_name);
        title.setText(arSrc.get(position).get(ARRAY_INDEX_TITLE));

        TextView date = (TextView) convertView
                .findViewById(R.id.listview_content_date);
        date.setText(arSrc.get(position).get(ARRAY_INDEX_DATE));

        convertView.setTag(position);
        convertView.setOnClickListener(this);

        process(arSrc.get(position).get(ARRAY_INDEX_THUMBNAIL), position, imageView);

        return convertView;
    }

    public void onClick(View v) {
        int position = (Integer) v.getTag();
        ArrayList<String> data = getItem(position);
        MainActivity.alert.show();
        title = data.get(ARRAY_INDEX_TITLE);
        getYouTubeUrl(data);
    }

    private String youtubelink, title;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {

            Intent intent = new Intent(maincon, Contents.class);
            intent.putExtra(Contents.EXTRAS_CONTENTS_LINK, youtubelink);
            intent.putExtra(Contents.EXTRAS_CONTENTS_TITLE, title);
            maincon.startActivity(intent);

            if (MainActivity.alert.isShowing())
                MainActivity.alert.dismiss();
        }
    };

    public void getYouTubeUrl(final ArrayList<String> data) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                String htmldata = HtmlToString(data.get(ARRAY_INDEX_LINK),
                        "utf-8");

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
    };

    private void process(final String url, final int position, final ImageView imageView) {
        new Thread() {
            private int calculateSampleSize(Options options, ImageView imageView) {
                final int width = options.outWidth;
                final int height = options.outHeight;
                final int viewWidth = imageView.getWidth();
                final int viewHeight = imageView.getHeight();
                final int widthRatio = Math.round((float) width / (float) viewWidth);
                final int heightRatio = Math.round((float) height / (float) viewHeight);
                final int inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
                return inSampleSize;
            }

            @Override
            public void run() {
                Bitmap bitmap = bitmapCache.get(position);
                if (bitmap == null) {
                    try {
                        // Because we down-scale list view's images, input
                        // stream have to be given to
                        // the decoder twice. InputStream cannot reuse. We will
                        // use buffered stream instead.
                        InputStream rawInputStream = new URL(url).openStream();
                        InputStream bufferedInputStream = new BufferedInputStream(rawInputStream);

                        // get the image bounds
                        Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeStream(bufferedInputStream, null, options);

                        // rewind input stream and retrieve the down-scaled
                        // image.
                        bufferedInputStream.reset();
                        options.inSampleSize = calculateSampleSize(options,
                                imageView);
                        options.inJustDecodeBounds = false;
                        bitmap = BitmapFactory.decodeStream(bufferedInputStream, null, options);

                        if (bitmapCache.get(position) == null) {
                            bitmapCache.put(position, bitmap);
                        }
                    } catch (MalformedURLException e) {
                    } catch (IOException e) {
                    }
                }

                final Bitmap runnableBitmap = bitmap;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (position == (Integer) imageView.getTag()) {
                            imageView.setImageBitmap(runnableBitmap);
                        }
                    }
                });
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
