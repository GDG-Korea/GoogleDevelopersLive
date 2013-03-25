
package com.proinlab.gdlapp;

import java.net.URI;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;

// this Fragment act in MainActivity for tab
@SuppressLint("HandlerLeak")
public class ListFragment extends SherlockFragment {

    public static final String ARG_SECTION_CATE = "ARG_SECTION_CATE";
    private boolean isThreadActive = false; // if thread already activate
    private String category;

    private ListViewCustomAdapter mAdapter;
    private ListView mListView;
    private View footer;
    private ArrayList<ArrayList<String>> arList; // refer to
                                                 // ListViewCustomAdapter
    private LayoutInflater mInflater;

    private String htmldata; // parsing string

    private static final int THREAD_HTMLPARSING = 0;
    private String nextpagecode = "1"; // if this code is "none", no more
                                       // loading and remove footer.
    private boolean mLockListView; // unactivate when loading

    public ListFragment() {
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
                // TODO
                int FOCUSED_POSITION = 0;
                ArrayList<String> data = mAdapter.getItem(FOCUSED_POSITION);
                MainActivity.alert.show();
                mAdapter.getYouTubeUrl(data);
                return true;
            default:
                return false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        nextpagecode = "1";

        category = getArguments().getString(ARG_SECTION_CATE);
        arList = new ArrayList<ArrayList<String>>();
        mListView = new ListView(getActivity());

        mInflater = (LayoutInflater) getActivity().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        footer = mInflater.inflate(R.layout.footer, null);
        mListView.addFooterView(footer);

        mAdapter = new ListViewCustomAdapter(getActivity(), arList);
        mListView.setAdapter(mAdapter);

        downloadListThread(category);

        mLockListView = true;

        mListView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount) {
                int count = totalItemCount - visibleItemCount;

                if (firstVisibleItem >= count && totalItemCount != 0
                        && mLockListView == false
                        && !nextpagecode.equals("none")) {
                    downloadListThread(category);
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
        });

        return mListView;
    }

    // update list
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case THREAD_HTMLPARSING:
                    mAdapter.notifyDataSetChanged();
                    isThreadActive = false;
                    mLockListView = false;
                    htmldata = null;
                    if (nextpagecode.equals("none"))
                        mListView.removeFooterView(footer);
                    break;
            }
        }
    };

    // loading list
    private boolean downloadListThread(final String cate) {
        if (isThreadActive) {
            return false;
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    isThreadActive = true;
                    String url;
                    if (cate.equals(getActivity().getResources()
                            .getStringArray(R.array.category)[0])) {
                        if (nextpagecode.equals("1"))
                            url = "https://developers.google.com/live/browse";
                        else
                            url = "https://developers.google.com/live/browse?c="
                                    + nextpagecode;
                    } else {
                        if (nextpagecode.equals("1"))
                            url = "https://developers.google.com/live/" + cate
                                    + "/browse";
                        else
                            url = "https://developers.google.com/live/" + cate
                                    + "/browse?c=" + nextpagecode;
                    }
                    Log.i("TAG", url);

                    htmldata = HtmlToString(url, "utf-8");
                    if (htmldata != null) {
                        if (htmldata.indexOf("<a href=\"?c=") != -1) {
                            nextpagecode = htmldata.substring(htmldata
                                    .indexOf(("<a href=\"?c=")) + 12);
                            nextpagecode = nextpagecode.substring(0,
                                    nextpagecode.indexOf("\">"));
                        } else
                            nextpagecode = "none";

                        if (htmldata
                                .indexOf("<div id=\"create-dialog\" class=\"hidden\">") != -1) {
                            htmldata = htmldata.substring(
                                    htmldata.indexOf("<ol class="),
                                    htmldata.indexOf("<div id=\"create-dialog\" class=\"hidden\">"));
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
                                if (date.indexOf("<div") != -1)
                                    date = date.substring(0,
                                            date.indexOf("<div"));
                                date = REMOVE_UNNECESSORY(date);

                                final ArrayList<String> data = new ArrayList<String>();
                                data.add(title);
                                data.add(date);
                                data.add(thumbnail);
                                data.add(link);
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        arList.add(data);
                                    }
                                });
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

    private synchronized String HtmlToString(String addr, String incoding) {
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
