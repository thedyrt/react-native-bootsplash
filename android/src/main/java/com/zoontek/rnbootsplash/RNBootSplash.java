package com.zoontek.rnbootsplash;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.UiThreadUtil;

public class RNBootSplash {

  private static boolean mInitialized = false;
  private static int mDrawableResId = -1;
  private static int mLayoutResId = -1;
  private static boolean mIsVisible = false;

  public static void initLayout(int layoutResId, Activity activity) {
    mLayoutResId = layoutResId;
    mInitialized = true;
    RNBootSplash.show(activity, 0.0f);
  }

  public static void init(final int drawableResId, @NonNull final Activity activity) {
    if (!mInitialized) {
      mDrawableResId = drawableResId;
      mInitialized = true;
      RNBootSplash.show(activity, 0.0f);
    }
  }

  static void show(@NonNull final Activity activity, final Float duration) {
    UiThreadUtil.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (!mInitialized || mIsVisible) {
          return;
        }

        mIsVisible = true;

        Context context = activity.getApplicationContext();
        int roundedDuration = duration.intValue();

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        ViewGroup layout = null;
        if (mDrawableResId != -1) {
          layout = new LinearLayout(context);
          View view = new View(context);
          view.setBackgroundResource(mDrawableResId);
          layout.setLayoutTransition(null);
          ((LinearLayout)layout).setOrientation(LinearLayout.VERTICAL);
          layout.addView(view, params);
        } else {
          layout = (ViewGroup) LayoutInflater.from(activity).inflate(mLayoutResId, null, false);
        }

        layout.setId(R.id.bootsplash_layout_id);

        if (roundedDuration <= 0) {
          activity.addContentView(layout, params);
        } else {
          layout.setAlpha(0.0f);
          activity.addContentView(layout, params);

          layout
              .animate()
              .setDuration(roundedDuration)
              .alpha(1.0f)
              .setInterpolator(new DecelerateInterpolator())
              .start();
        }
      }
    });
  }

  static void hide(@NonNull final Activity activity, final Float duration) {
    UiThreadUtil.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (!mInitialized || !mIsVisible) {
          return;
        }

        mIsVisible = false;

        final ViewGroup layout = activity.findViewById(R.id.bootsplash_layout_id);

        if (layout == null) {
          return;
        }

        int roundedDuration = duration.intValue();
        final ViewGroup parent = (ViewGroup) layout.getParent();

        if (roundedDuration <= 0) {
          parent.removeView(layout);
        } else {
          layout
              .animate()
              .setDuration(roundedDuration)
              .alpha(0.0f)
              .setInterpolator(new AccelerateInterpolator())
              .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                  super.onAnimationEnd(animation);
                  if (parent != null) {
                    parent.removeView(layout);
                  }
                }
              }).start();
        }
      }
    });
  }
}
