package com.nyrds.pixeldungeon.support;

import android.os.Build;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.RewardedVideoCallbacks;
import com.appodeal.ads.utils.Log;
import com.nyrds.pixeldungeon.ml.BuildConfig;
import com.nyrds.pixeldungeon.ml.EventCollector;
import com.nyrds.pixeldungeon.ml.R;
import com.watabou.noosa.Game;
import com.watabou.noosa.InterstitialPoint;
import com.watabou.pixeldungeon.PixelDungeon;
import com.watabou.pixeldungeon.utils.GLog;

/**
 * Created by mike on 18.02.2017.
 * This file is part of Remixed Pixel Dungeon.
 */

public class AppodealRewardVideo {
	private static InterstitialPoint returnTo;


	private static boolean isAllowed() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
	}

	public static void initCinemaRewardVideo() {

		if (!isAllowed()) {
			return;
		}

		Game.instance().runOnUiThread(new Runnable() {
			@Override
			public void run() {

				String appKey = Game.getVar(R.string.appodealRewardAdUnitId);

				String disableNetworks[] = {"facebook","flurry","startapp","avocarrot","ogury"};

				for(String net:disableNetworks) {
					Appodeal.disableNetwork(PixelDungeon.instance(), net);
				}
				Appodeal.disableLocationPermissionCheck();

				if(BuildConfig.DEBUG) {
					Appodeal.setLogLevel(Log.LogLevel.verbose);
					//Appodeal.setTesting(true);
				}

				Appodeal.initialize(PixelDungeon.instance(), appKey, Appodeal.REWARDED_VIDEO);
				EventCollector.startTiming("appodeal reward video");
				Appodeal.setRewardedVideoCallbacks(new RewardedVideoCallbacks() {
					@Override
					public void onRewardedVideoLoaded() {
						EventCollector.stopTiming("google reward video","google reward video","ok","");
					}
					@Override
					public void onRewardedVideoFailedToLoad() {
						EventCollector.stopTiming("google reward video","google reward video","fail","");
					}
					@Override
					public void onRewardedVideoShown() {
					}
					@Override
					public void onRewardedVideoFinished(int amount, String name) {
					}
					@Override
					public void onRewardedVideoClosed(final boolean finished) {
						returnTo.returnToWork(finished);
					}
				});
			}
		});
	}

	public static void showCinemaRewardVideo(InterstitialPoint ret) {
		returnTo = ret;
		Game.instance().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(isReady()) {
					Appodeal.show(PixelDungeon.instance(), Appodeal.REWARDED_VIDEO);
				} else {
					returnTo.returnToWork(false);
				}
			}
		});
	}

	public static boolean isReady() {
		return isAllowed() && Appodeal.isLoaded(Appodeal.REWARDED_VIDEO);
	}
}
