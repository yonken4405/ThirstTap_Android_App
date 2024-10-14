package com.example.thirsttap.Login;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class CustomBottomSheetBehavior<V extends View> extends BottomSheetBehavior<V> {

    public CustomBottomSheetBehavior() {
        super();
    }

    public CustomBottomSheetBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
        // Prevent BottomSheet from dismissing on scroll
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            return true; // consume the event
        }
        return super.onTouchEvent(parent, child, event);
    }
}
