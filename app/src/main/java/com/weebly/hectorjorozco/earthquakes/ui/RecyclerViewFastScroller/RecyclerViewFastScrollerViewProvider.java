package com.weebly.hectorjorozco.earthquakes.ui.RecyclerViewFastScroller;

import android.graphics.drawable.ShapeDrawable;

import android.graphics.drawable.shapes.RectShape;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.futuremind.recyclerviewfastscroll.viewprovider.ScrollerViewProvider;
import com.futuremind.recyclerviewfastscroll.Utils;
import com.futuremind.recyclerviewfastscroll.viewprovider.ViewBehavior;
import com.weebly.hectorjorozco.earthquakes.R;


public class RecyclerViewFastScrollerViewProvider extends ScrollerViewProvider {

    @Override
    public View provideHandleView(ViewGroup container) {
        View handle = new View(getContext());
        int width = getContext().getResources().getDimensionPixelSize(R.dimen.recycler_view_fast_scroll_custom_handle_width);
        int height = getContext().getResources().getDimensionPixelSize(R.dimen.recycler_view_fast_scroll_custom_handle_height);
        handle.setLayoutParams(new ViewGroup.LayoutParams(width, height));
        Utils.setBackground(handle, getRectangle(width, height, ContextCompat.getColor(getContext(), android.R.color.darker_gray)));
        handle.setVisibility(View.VISIBLE);
        return handle;
    }

    @Override
    public View provideBubbleView(ViewGroup container) {
        return new TextView(getContext());
    }

    @Override
    public TextView provideBubbleTextView() {
        return null;
    }

    @Override
    public int getBubbleOffset() {
        return 0;
    }

    @Override
    protected ViewBehavior provideHandleBehavior() {
        return null;
    }

    @Override
    protected ViewBehavior provideBubbleBehavior() {
        return null;
    }

    private static ShapeDrawable getRectangle(int width, int height, int color) {
        ShapeDrawable rectangle = new ShapeDrawable(new RectShape());
        rectangle.setIntrinsicWidth(width);
        rectangle.setIntrinsicHeight(height);
        rectangle.getPaint().setColor(color);
        return rectangle;
    }

}
