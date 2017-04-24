package com.lihb.babyvoice.utils;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.lihb.babyvoice.R;

import java.lang.reflect.Field;

/**
 * Created by lunqingwen on 2015/9/28.
 */
public class DatePickerEx extends DatePicker {

    public DatePickerEx(Context context) {
        super(context);
    }

    public DatePickerEx(Context context, AttributeSet attrs) {
        super(context, attrs);
        doCustomize(context, attrs);
    }

    public DatePickerEx(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        doCustomize(context, attrs);
    }

    @TargetApi(21)
    public DatePickerEx(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        doCustomize(context, attrs);
    }

    private void doCustomize(Context context, AttributeSet attrs) {
        NumberPicker yearPicker = (NumberPicker) findViewById(
                Resources.getSystem().getIdentifier("year", "id", "android"));

        NumberPicker monthPicker = (NumberPicker) findViewById(
                Resources.getSystem().getIdentifier("month", "id", "android"));

        NumberPicker dayPicker = (NumberPicker) findViewById(
                Resources.getSystem().getIdentifier("day", "id", "android"));

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.DatePickerEx);
        int count = a.getIndexCount();
        for (int i = 0; i < count; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.DatePickerEx_android_textColor) {
                int color = a.getColor(attr, 0);
                setTextColor(yearPicker, color);
                setTextColor(monthPicker, color);
                setTextColor(dayPicker, color);
            } else if (attr == R.styleable.DatePickerEx_dividerColor) {
                int color = a.getColor(attr, 0);
                setDividerColor(yearPicker, color);
                setDividerColor(monthPicker, color);
                setDividerColor(dayPicker, color);
            } else if (attr == R.styleable.DatePickerEx_dividerHeight) {
                int height = a.getDimensionPixelSize(attr, -1);
                if (height > 0) {
                    setDividerHeight(yearPicker, height);
                    setDividerHeight(monthPicker, height);
                    setDividerHeight(dayPicker, height);
                }
            }
        }
        a.recycle();
    }

    private void setTextColor(NumberPicker picker, int color) {
        int count = picker.getChildCount();
        for (int i = 0; i < count; i++) {
            View v = picker.getChildAt(i);
            if (v instanceof EditText) {
                try {
                    Field field = picker.getClass().getDeclaredField("mSelectorWheelPaint");
                    field.setAccessible(true);
                    ((Paint) field.get(picker)).setColor(color);
                    ((EditText) v).setTextColor(color);
                    v.invalidate();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setDividerColor(NumberPicker picker, int color) {
        try {
            Field field = picker.getClass().getDeclaredField("mSelectionDivider");
            field.setAccessible(true);
            ColorDrawable drawable = new ColorDrawable(color);
            field.set(picker, drawable);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void setDividerHeight(NumberPicker picker, int height) {
        try {
            Field field = picker.getClass().getDeclaredField("mSelectionDividerHeight");
            field.setAccessible(true);
            field.set(picker, height);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

