package com.medinin.medininapp.utils;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

public class ClearFocusOnKBClose {

    public ClearFocusOnKBClose(final View rootView) {
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        Rect r = new Rect();
                        rootView.getWindowVisibleDisplayFrame(r);
                        int screenHeight = rootView.getRootView().getHeight();

                        // r.bottom is the position above soft keypad or device button.
                        // if keypad is shown, the r.bottom is smaller than that before.
                        int keypadHeight = screenHeight - r.bottom;

                        if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                            // keyboard is opened
                        } else {
                            // keyboard is closed
                            rootView.requestFocus();
                        }
                    }
                }
        );
    }
}
