package com.mutualmobile.android.library.seatlayout.utils;

import android.content.Context;

public class DensityUtil {
/**
 * dip to px
 */  
public static float dip2px(Context context, float dpValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return  (dpValue * scale );
}

  /**
   * sip to px
   */
  public static float sip2px(Context context, float spValue) {
    final float scale = context.getResources().getDisplayMetrics().scaledDensity;
    return  (spValue * scale );
  }
}