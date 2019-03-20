package com.mutualmobile.android.library.seatlayout.view;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import com.mutualmobile.android.library.seatlayout.SeatData;
import com.mutualmobile.android.library.seatlayout.utils.DensityUtil;


public class RippleView extends View {

    private static final float NORMAL_MAX_MORE_RADIUS_TIMES = 3f;
    private Paint ripplePaint = new Paint();

    private TimeInterpolator mInterpolator = new DecelerateInterpolator();

    private int startRadius;
    private int mChangeRadius;
    private int endRadius;
    private float centerX;
    private float centerY;
    private ValueAnimator rippleAnimator;

    public RippleView(Context context) {
        this(context, null);
    }

    public RippleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RippleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mChangeRadius = startRadius;

        ripplePaint.setAntiAlias(true);
        ripplePaint.setStyle(Paint.Style.STROKE);
        ripplePaint.setStrokeWidth(DensityUtil.dip2px(context, 1));

        rippleAnimator = new ValueAnimator();
        rippleAnimator.setInterpolator(mInterpolator);
        rippleAnimator.setRepeatCount(0);
        rippleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ripplePaint.setColor(Color.argb((int) ((1 - animation.getAnimatedFraction()) * 255), 111, 137, 159));
                mChangeRadius = (int) animation.getAnimatedValue();
                postInvalidate();
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (rippleAnimator != null) {
            rippleAnimator.cancel();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(centerX, centerY, mChangeRadius, ripplePaint);
    }

    public void startRipple(float scale, SeatData pressedSeat, float left, float top) {
        centerX = (pressedSeat.seatCenterPoint.x * scale) + left;
        centerY = (pressedSeat.seatCenterPoint.y * scale) + top;
        startRadius = (int) ((pressedSeat.containerbound.width() * scale) * (0.5f));
        endRadius = (int) ((startRadius * NORMAL_MAX_MORE_RADIUS_TIMES));
        if (pressedSeat.getSeatStatus() == SeatView.SeatStatus.CHOSEN
                || pressedSeat.getSeatStatus() == SeatView.SeatStatus.SOLD
                || pressedSeat.getSeatStatus() == SeatView.SeatStatus.BROKEN
                || (pressedSeat.getSeatStyle() == SeatView.SeatStyle.COMPANION && pressedSeat.getSeatStatus() == SeatView.SeatStatus.CHOSEN)
                || pressedSeat.getSeatStyle() == SeatView.SeatStyle.HANDICAP && pressedSeat.getSeatStatus() == SeatView.SeatStatus.CHOSEN) {
            if (rippleAnimator.isRunning()) {
                rippleAnimator.end();
            }
            rippleAnimator.setIntValues(startRadius, endRadius);
            rippleAnimator.start();
        } else {
            rippleAnimator.setIntValues(endRadius, startRadius);
            rippleAnimator.start();
        }
    }
}
