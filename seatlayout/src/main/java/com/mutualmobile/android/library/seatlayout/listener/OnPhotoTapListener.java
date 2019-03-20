package com.mutualmobile.android.library.seatlayout.listener;

import android.widget.ImageView;

/**
 * A callback to be invoked when the Photo is tapped with a single
 * tap.
 */
public interface OnPhotoTapListener {

    /**
     * A callback to receive where the user taps on a photo. You will only receive a callback if
     * the user taps on the actual photo, tapping on 'whitespace' will be ignored.
     *
     * @param view ImageView the user tapped.
     * @param pointerX    where the user tapped from the of the Drawable, as percentage of the
     *             Drawable width.
     * @param pointerY    where the user tapped from the top of the Drawable, as percentage of the
     *             Drawable height.
     */
    void onPhotoTap(ImageView view, float pointerX, float pointerY, float tooltipX, float tooltipY);
}
