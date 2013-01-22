package com.proinlab.gdlapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

public class Contents extends YouTubeFailureRecoveryActivity implements
		YouTubePlayer.OnFullscreenListener {

	private ActionBarPaddedFrameLayout viewContainer;
	private YouTubePlayerSupportFragment playerFragment;
	private YouTubePlayer player;

	private String Title;
	private String YouTubeId;

	public static final String EXTRAS_CONTENTS_LINK = "EXTRAS_CONTENTS_LINK";
	public static final String EXTRAS_CONTENTS_TITLE = "EXTRAS_CONTENTS_TITLE";
	public static final String EXTRAS_CONTENTS_DATE = "EXTRAS_CONTENTS_DATE";
	public static final String EXTRAS_CONTENTS_THUMBNAIL = "EXTRAS_CONTENTS_THUMBNAIL";

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_MEDIA_PAUSE:
			if (player.isPlaying())
				player.pause();
			return true;
		case KeyEvent.KEYCODE_MEDIA_PLAY:
			if (!player.isPlaying())
				player.play();
			return true;
		case KeyEvent.KEYCODE_MEDIA_STOP:
			finish();
			return true;
		case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
			player.seekToMillis(player.getCurrentTimeMillis() + 5000);
			return true;
		case KeyEvent.KEYCODE_MEDIA_REWIND:
			player.seekToMillis(player.getCurrentTimeMillis() - 5000);
			return true;
		default:
			return super.onKeyDown(keyCode, event);
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contents);

		YouTubeId = getIntent().getExtras().getString(EXTRAS_CONTENTS_LINK);
		Title = getIntent().getExtras().getString(EXTRAS_CONTENTS_TITLE);

		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xAA000000));
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		getSupportActionBar().setTitle(Title);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		viewContainer = (ActionBarPaddedFrameLayout) findViewById(R.id.view_container);
		playerFragment = (YouTubePlayerSupportFragment) getSupportFragmentManager()
				.findFragmentById(R.id.player_fragment);

		playerFragment.initialize(DeveloperKey.DEVELOPER_KEY, this);
		viewContainer.setActionBar(getSupportActionBar());

	}

	@Override
	public void onInitializationSuccess(YouTubePlayer.Provider provider,
			YouTubePlayer player, boolean wasRestored) {
		player.setFullscreen(true);
		player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
		player.setOnFullscreenListener(this);
		this.player = player;

		if (!wasRestored) {
			player.loadVideo(YouTubeId);
		}
	}

	@Override
	protected YouTubePlayer.Provider getYouTubePlayerProvider() {
		return (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(
				R.id.player_fragment);
	}

	@Override
	public void onFullscreen(boolean fullscreen) {
		viewContainer.setEnablePadding(!fullscreen);

		ViewGroup.LayoutParams playerParams = playerFragment.getView()
				.getLayoutParams();
		if (fullscreen) {
			playerParams.width = LayoutParams.MATCH_PARENT;
			playerParams.height = LayoutParams.MATCH_PARENT;
		} else {
			playerParams.width = LayoutParams.MATCH_PARENT;
			playerParams.height = LayoutParams.WRAP_CONTENT;
		}
	}

	public static final class ActionBarPaddedFrameLayout extends FrameLayout {

		private ActionBar actionBar;
		private boolean paddingEnabled;

		public ActionBarPaddedFrameLayout(Context context) {
			this(context, null);
		}

		public ActionBarPaddedFrameLayout(Context context, AttributeSet attrs) {
			this(context, attrs, 0);
		}

		public ActionBarPaddedFrameLayout(Context context, AttributeSet attrs,
				int defStyle) {
			super(context, attrs, defStyle);
			paddingEnabled = true;
		}

		public void setActionBar(ActionBar actionBar) {
			this.actionBar = actionBar;
			requestLayout();
		}

		public void setEnablePadding(boolean enable) {
			paddingEnabled = enable;
            requestLayout();
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			int topPadding = paddingEnabled && actionBar != null
					&& actionBar.isShowing() ? actionBar.getHeight() : 0;
			setPadding(0, topPadding, 0, 0);

			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
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
}
