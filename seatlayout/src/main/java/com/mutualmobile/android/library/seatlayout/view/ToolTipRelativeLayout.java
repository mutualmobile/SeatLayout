package com.mutualmobile.android.library.seatlayout.view;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.mutualmobile.android.library.seatlayout.SeatData;

public class ToolTipRelativeLayout extends RelativeLayout {

  private CountDownTimer timer;
  private ToolTipView toolTipView;

  public ToolTipRelativeLayout(final Context context) {
    super(context);
    initTimer();
  }

  public ToolTipRelativeLayout(final Context context, final AttributeSet attrs) {
    super(context, attrs);
    initTimer();
  }

  public ToolTipRelativeLayout(final Context context, final AttributeSet attrs, final int defStyle) {
    super(context, attrs, defStyle);
    initTimer();
  }

  private void initTimer() {
    timer = new CountDownTimer(2000, 1000) {
      public void onTick(long millisUntilFinished) {
      }

      public void onFinish() {
        toolTipView.remove();
      }
    };
  }

  public ToolTipView showToolTipForView(float scaleFactor, SeatData pressedSeat, float left, float top) {
    removeViews();
    toolTipView = new ToolTipView(getContext());
    toolTipView.setToolTip(scaleFactor, pressedSeat, left, top);
    addView(toolTipView);
    timer.start();
    return toolTipView;
  }

  public void removeViews() {
    if (timer != null) {
      timer.cancel();
    }
    if (toolTipView != null) {
      toolTipView.remove();
      toolTipView = null;
    }
  }
}
