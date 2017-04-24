package com.lihb.babyvoice.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.DatePicker;

import com.lihb.babyvoice.R;


/**
 * Created by lunqingwen on 2015/9/29.
 */
public class SimpleDatePickerDialog {
    private CommonDialog dialog;
    private DatePicker datePicker;
    private int oldYear;
    private int oldMonth;
    private int oldDay;

    private DatePicker.OnDateChangedListener dateChangedListener = new DatePicker.OnDateChangedListener() {
        @Override
        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            if (oldDay != dayOfMonth) {
                datePicker.init(oldYear, oldMonth, dayOfMonth, dateChangedListener);
                oldDay = dayOfMonth;
            } else if (oldMonth != monthOfYear) {
                datePicker.init(oldYear, monthOfYear, dayOfMonth, dateChangedListener);
                oldMonth = monthOfYear;
            } else {
                oldYear = year;
                oldMonth = monthOfYear;
                oldDay = dayOfMonth;
            }
        }
    };

    private SimpleDatePickerDialog(Builder builder) {
        dialog = CommonDialog.createDialog(builder.context);
        dialog.setWrapContentWidth();
        datePicker = (DatePicker) View.inflate(builder.context, R.layout.select_birthday_view, null);
        if (builder.year > 0 && builder.month >= 0 && builder.day > 0) {
            datePicker.init(builder.year, builder.month, builder.day, dateChangedListener);
        } else {
            datePicker.init(datePicker.getYear(), datePicker.getMonth(),
                    datePicker.getDayOfMonth(), dateChangedListener);
        }
        oldYear = datePicker.getYear();
        oldMonth = datePicker.getMonth();
        oldDay = datePicker.getDayOfMonth();

        dialog.setTitleText(builder.titleId)
                .setContentView(datePicker)
                .setLeftButtonText(R.string.cancel)
                .setLeftButtonAction(builder.cancelListener)
                .setRightButtonText(R.string.confirm)
                .setRightButtonAction(builder.confirmListener)
                .setCancelable(true);
    }

    public void show() {
        dialog.show();
    }

    public int getYear() {
        return datePicker.getYear();
    }

    public int getMonth() {
        return datePicker.getMonth();
    }

    public int getDay() {
        return datePicker.getDayOfMonth();
    }

    public String getYYYYMMDD() {
        return String.format("%04d%02d%02d", datePicker.getYear(),
                datePicker.getMonth() + 1, datePicker.getDayOfMonth());
    }

    public static class Builder {
        Context context;
        int titleId;
        private int year;
        private int month;
        private int day;
        private CommonDialog.OnActionListener cancelListener;
        private CommonDialog.OnActionListener confirmListener;

        public Builder setContext(@NonNull Context context) {
            this.context = context;
            return this;
        }

        public Builder setTitleId(int titleId) {
            this.titleId = titleId;
            return this;
        }

        public Builder setYear(int year) {
            this.year = year;
            return this;
        }

        public Builder setMonth(int month) {
            this.month = month;
            return this;
        }

        public Builder setDay(int day) {
            this.day = day;
            return this;
        }

        public Builder setYYYYMMDD(String yyyymmdd) {
            if (yyyymmdd != null && yyyymmdd.length() == 8) {
                year = Integer.valueOf(yyyymmdd.substring(0, 4));
                month = Integer.valueOf(yyyymmdd.substring(4, 6)) - 1;
                day = Integer.valueOf(yyyymmdd.substring(6, 8));
            }
            return this;
        }

        public Builder setCancelListener(CommonDialog.OnActionListener cancelListener) {
            this.cancelListener = cancelListener;
            return this;
        }

        public Builder setConfirmListener(CommonDialog.OnActionListener confirmListener) {
            this.confirmListener = confirmListener;
            return this;
        }

        public SimpleDatePickerDialog build() {
            SimpleDatePickerDialog dialog = new SimpleDatePickerDialog(this);
            return dialog;
        }
    }
}
