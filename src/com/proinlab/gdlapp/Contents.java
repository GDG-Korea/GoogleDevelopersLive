/*
 * Copyright 2012 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.proinlab.gdlapp;

import android.view.ViewGroup.LayoutParams;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

@TargetApi(11)
public class Contents extends YouTubeFailureRecoveryActivity implements
		YouTubePlayer.OnFullscreenListener {

	private ActionBarPaddedFrameLayout viewContainer;
	private YouTubePlayerFragment playerFragment;

	private String Title;
	private String YouTubeId;

	public static final String EXTRAS_CONTENTS_LINK = "EXTRAS_CONTENTS_LINK";
	public static final String EXTRAS_CONTENTS_TITLE = "EXTRAS_CONTENTS_TITLE";
	public static final String EXTRAS_CONTENTS_DATE = "EXTRAS_CONTENTS_DATE";
	public static final String EXTRAS_CONTENTS_THUMBNAIL = "EXTRAS_CONTENTS_THUMBNAIL";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contents);

		YouTubeId = getIntent().getExtras().getString(EXTRAS_CONTENTS_LINK);
		Title = getIntent().getExtras().getString(EXTRAS_CONTENTS_TITLE);
		
		getActionBar().setBackgroundDrawable(new ColorDrawable(0xAA000000));
		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		getActionBar().setTitle(Title);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		viewContainer = (ActionBarPaddedFrameLayout) findViewById(R.id.view_container);
		playerFragment = (YouTubePlayerFragment) getFragmentManager()
				.findFragmentById(R.id.player_fragment);

		playerFragment.initialize(DeveloperKey.DEVELOPER_KEY, this);
		viewContainer.setActionBar(getActionBar());

	}

	@Override
	public void onInitializationSuccess(YouTubePlayer.Provider provider,
			YouTubePlayer player, boolean wasRestored) {
		player.setFullscreen(true);
		player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
		player.setOnFullscreenListener(this);

		if (!wasRestored) {
			player.loadVideo(YouTubeId);
		}
	}

	@Override
	protected YouTubePlayer.Provider getYouTubePlayerProvider() {
		return (YouTubePlayerFragment) getFragmentManager().findFragmentById(
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
