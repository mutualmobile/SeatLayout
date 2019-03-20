package com.mutualmobile.android.library.seatlayout.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.RectF;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewManager;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import com.mutualmobile.android.library.R;
import com.mutualmobile.android.library.databinding.TooltipBinding;
import com.mutualmobile.android.library.seatlayout.SeatData;

import java.util.ArrayList;
import java.util.Collection;

public class ToolTipView extends LinearLayout
        implements ViewTreeObserver.OnPreDrawListener, View.OnClickListener {

    public static final String TRANSLATION_Y_COMPAT = "translationY";
    public static final String TRANSLATION_X_COMPAT = "translationX";
    public static final String SCALE_X_COMPAT = "scaleX";
    public static final String SCALE_Y_COMPAT = "scaleY";
    public static final String ALPHA_COMPAT = "alpha";
    private final Context mContext;
    private Collection<Animator> animators = new ArrayList<>(5);
    private OnToolTipViewClickedListener mListener;
    private RectF bounds;
    private int tooltipHeight;
    private int containerWidth;
    private int containerHeight;
    private int pointerWidth;
    private int pointerHeight;
    private int tooltipWidth;
    private float scaleFactor;
    private float top;
    private float left;
    private float startX;
    private float startY;
    private TooltipBinding binding;
    private float pivotX;
    private float pivotY;

    public ToolTipView(final Context context) {
        super(context);
        mContext = context;
        init();
    }

    private void init() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.tooltip, this, true);
        setOnClickListener(this);
        getViewTreeObserver().addOnPreDrawListener(this);
    }

    @Override
    public void onClick(final View view) {
        remove();
        if (mListener != null) {
            mListener.onToolTipViewClicked(this);
        }
    }

    public void setToolTip(float scaleFactor, SeatData pressedSeat, float left, float top) {
        this.bounds = pressedSeat.getContainerbound();
        this.scaleFactor = scaleFactor;
        this.left = left;
        this.top = top;
        binding.tooltipRow.setText("ROW " + pressedSeat.getRowNumber());
        binding.tooltipSeat.setText("SEAT " + pressedSeat.getSeatNumber());
        if (pressedSeat.getPrice() > 0) {
            binding.tooltipSeatPrice.setVisibility(View.VISIBLE);
            float price = pressedSeat.getPrice() / 100f;
            binding.tooltipSeatPrice.setText(String.format("$%.2f", price));
        } else {
            binding.tooltipSeatPrice.setVisibility(View.GONE);
        }

        String seatDescription = pressedSeat.getSeatDescription().trim();
        boolean isValidDescriptionProvided = seatDescription != null && !seatDescription.isEmpty();
        binding.tooltipSeatType.setVisibility(isValidDescriptionProvided ? View.VISIBLE : View.GONE);
        binding.tooltipSeatType.setText(isValidDescriptionProvided ? seatDescription : "");
    }

    @Override
    public boolean onPreDraw() {
        getViewTreeObserver().removeOnPreDrawListener(this);
        tooltipWidth = binding.tooltipContainer.getWidth();
        tooltipHeight = binding.tooltipContainer.getHeight();
        containerWidth = ((View) getParent()).getWidth();
        containerHeight = ((View) getParent()).getHeight();
        pointerWidth = binding.tooltipPointerDown.getWidth();
        pointerHeight = binding.tooltipPointerDown.getHeight();
        pivotX = getPivotX();
        pivotY = getPivotY();
        applyToolTipPosition();
        return true;
    }

    private void applyToolTipPosition() {
        animators.clear();
        float circleCenterX = bounds.centerX() * scaleFactor + left;
        float circleTopY = bounds.top * scaleFactor + top;
        float endX = circleCenterX - tooltipWidth / 2;
        float endY = circleTopY - tooltipHeight - pointerHeight / 2;
        float pointerX = tooltipWidth / 2 - pointerWidth / 2;

        final float leftLimit = 0;
        final float rightLimit = containerWidth - tooltipWidth;
        if (endX >= leftLimit && endX <= rightLimit) {
            this.setPivotX(pivotX);
            this.setPivotY(pivotY);
        } else if (endX < leftLimit) {
            endX = bounds.left * scaleFactor + left;
            pointerX = circleCenterX - endX - pointerWidth / 2;
            this.setPivotX(pivotX / 2);
            this.setPivotY(0);
        } else if (endX > rightLimit) {
            endX = bounds.right * scaleFactor + left - tooltipWidth;
            pointerX = circleCenterX - endX - pointerWidth / 2;
            this.setPivotX(tooltipWidth);
            this.setPivotY(0);
        }
        setX(endX);
        setY(endY);
        setPointerCenterX(pointerX);
        //setPointerCenterY(pointerX);
        startX = endX;
        startY = circleTopY;
        animators.add(ObjectAnimator.ofFloat(this, TRANSLATION_Y_COMPAT, startY, endY));
        animators.add(ObjectAnimator.ofFloat(this, TRANSLATION_X_COMPAT, startX, endX));
        animators.add(ObjectAnimator.ofFloat(this, SCALE_X_COMPAT, 0, 1));
        animators.add(ObjectAnimator.ofFloat(this, SCALE_Y_COMPAT, 0, 1));
        animators.add(ObjectAnimator.ofFloat(this, ALPHA_COMPAT, 0, 1));
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new OvershootInterpolator());
        animatorSet.playTogether(animators);
        animatorSet.setDuration(250);
        animatorSet.start();
    }

    public void setPointerCenterX(final float pointerCenterX) {
        binding.tooltipPointerUp.setX(pointerCenterX);
        binding.tooltipPointerDown.setX(pointerCenterX);
    }

    public void setPointerCenterY(float pointerCenterY) {
        binding.tooltipPointerLeft.setY(pointerCenterY - getY());
        binding.tooltipPointerRight.setY(pointerCenterY - getY());
    }

    public void remove() {
        Collection<Animator> animators = new ArrayList<>(5);
        animators.add(ObjectAnimator.ofFloat(this, TRANSLATION_Y_COMPAT, (int) getY(), startY));
        animators.add(ObjectAnimator.ofFloat(this, TRANSLATION_X_COMPAT, (int) getX(), startX));
        animators.add(ObjectAnimator.ofFloat(this, SCALE_X_COMPAT, 1, 0));
        animators.add(ObjectAnimator.ofFloat(this, SCALE_Y_COMPAT, 1, 0));

        animators.add(ObjectAnimator.ofFloat(this, ALPHA_COMPAT, 1, 0));

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animators);
        animatorSet.addListener(new DisappearanceAnimatorListener());
        animatorSet.start();
    }

    public interface OnToolTipViewClickedListener {
        void onToolTipViewClicked(ToolTipView toolTipView);
    }

    private class DisappearanceAnimatorListener extends AnimatorListenerAdapter {
        @Override
        public void onAnimationStart(final Animator animation) {
        }

        @Override
        public void onAnimationEnd(final Animator animation) {
            if (getParent() != null) {
                ((ViewManager) getParent()).removeView(ToolTipView.this);
            }
        }

        @Override
        public void onAnimationCancel(final Animator animation) {
        }

        @Override
        public void onAnimationRepeat(final Animator animation) {
        }
    }
}
