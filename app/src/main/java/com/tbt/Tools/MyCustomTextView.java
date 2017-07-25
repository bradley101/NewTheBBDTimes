package com.tbt.Tools;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by bradley on 07-03-2017.
 */

public class MyCustomTextView extends android.support.v7.widget.AppCompatTextView {
    public MyCustomTextView(Context context) {
        super(context);
        init();
    }

    public MyCustomTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyCustomTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "RobotoSlab.ttf");
        setTypeface(tf);
    }
}
