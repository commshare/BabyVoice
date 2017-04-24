package com.lihb.babyvoice.utils;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.text.method.MovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lihb.babyvoice.R;
import com.lihb.babyvoice.command.DismissCommand;
import com.lihb.babyvoice.customview.base.BaseActivity;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * 通用对话框控件
 * 通过不同的参数可以支持显示提示框、二次确认框、进度框、输入框
 * 大部分接口返回自身引用，以支持链式调用
 * 最后别忘了show
 * <p/>
 * Created by caijw on 2015/6/26.
 */
public class CommonDialog {

    public enum Visible {
        IfNonEmpty, // 内容非空则显示
        Show,       // 显示
        Gone        // 不显示
    }

    public static final int BUTTON_LEFT = Dialog.BUTTON_NEGATIVE;
    public static final int BUTTON_RIGHT = Dialog.BUTTON_POSITIVE;
    public static final int BUTTON_MAIN = Dialog.BUTTON_NEUTRAL;

    public static final int BUTTON_LEFT_CLICKED = Dialog.BUTTON_NEGATIVE;
    public static final int BUTTON_RIGHT_CLICKED = Dialog.BUTTON_POSITIVE;
    public static final int BUTTON_MAIN_CLICKED = Dialog.BUTTON_NEUTRAL;
    public static final int EDIT_DONE = -5;

    protected Dialog dialog;

    private LinearLayout dialogContentArea;
    private LinearLayout centerContainer;

    private Visible titleVisible = Visible.IfNonEmpty;
    //private View titleTextLayout;
    private TextView titleTextView;

    private Visible textVisible = Visible.IfNonEmpty;
    private TextView textView;

    private Visible editTextVisible = Visible.IfNonEmpty;
    private EditText editText;

    private Visible iconVisible = Visible.IfNonEmpty;
    private ImageView iconView;

    private Visible progressVisible = Visible.IfNonEmpty;
    private boolean progressAssign = false;
    private ProgressBar progressBar;

    private Visible buttonsVisible = Visible.IfNonEmpty;
    private View buttonsLayout;
    private View buttonsSeparateLine;
    private Button leftButton;
    private Button rightButton;
    private Button mainButton;
    private View.OnClickListener dismissAction;
    private ValueAnimator progressAnimator;

    protected CommonDialog(Context context) {
        dialog = new Dialog(context, R.style.common_dialog);
        initView();
    }

    private void initView() {
        dialog.setContentView(R.layout.common_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCanceledOnTouchOutside(false);   // 禁止点击框外取消
        dialog.setCancelable(false);               // 禁止返回键取消
        dialogContentArea = (LinearLayout)dialog.findViewById(R.id.dialog_content_area);
        centerContainer = (LinearLayout) dialog.findViewById(R.id.dialog_center_container);

       // titleTextLayout = dialog.findViewById(R.id.dialog_title_layout);
        titleTextView = (TextView) dialog.findViewById(R.id.dialog_title_text_view);
        textView = (TextView) dialog.findViewById(R.id.dialog_main_text_view);
        editText = (EditText) dialog.findViewById(R.id.dialog_edit_text);
        iconView = (ImageView) dialog.findViewById(R.id.dialog_main_icon);
        progressBar = (ProgressBar) dialog.findViewById(R.id.dialog_progress_bar);
        buttonsSeparateLine = dialog.findViewById(R.id.dialog_v_separate_line);
        buttonsLayout = dialog.findViewById(R.id.dialog_buttons_layout);
        leftButton = (Button) dialog.findViewById(R.id.dialog_left_button);
        rightButton = (Button) dialog.findViewById(R.id.dialog_right_button);
        mainButton = (Button) dialog.findViewById(R.id.dialog_main_button);

        // 按钮默认响应为关闭对话框
        dismissAction = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        };
        leftButton.setOnClickListener(dismissAction);
        rightButton.setOnClickListener(dismissAction);
        mainButton.setOnClickListener(dismissAction);
    }

    public boolean isValid() {
        if (dialog == null || !dialog.isShowing())
            return false;
        final Activity a = dialog.getOwnerActivity();
        if (a != null && a instanceof BaseActivity) {
            BaseActivity _a = (BaseActivity)a;
            return !(_a.isUiDestroyed() || _a.isFinishing());
        }
        return true;
    }

    public CommonDialog setOwnerActivity(Activity activity){
        dialog.setOwnerActivity(activity);
        return this;
    }

    public static void destroyDialog(CommonDialog dlg){
        if (dlg != null) {
            dlg.dismiss();
        }
    }

    /**
     * 创建一个通用对话框
     * 别忘了调用show将它显示
     */
    public static CommonDialog createDialog(Context context) {
        return new CommonDialog(context);
    }

    public CommonDialog setupSubscription() {
        RxBus.getDefault()
                .register(DismissCommand.class, centerContainer)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<DismissCommand>() {
                    @Override
                    public void call(DismissCommand command) {
                        dismiss();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                    }
                });
        return this;
    }

    /**
     * 设置位置
     * @param gravity
     * @return
     */
    public CommonDialog setGravity(int gravity) {
        dialog.getWindow().setGravity(gravity);
        return this;
    }

    /**
     * 按屏幕宽度的百分百显示
     * @return
     */
    public CommonDialog setWidthPer(float per) {
        dialog.getWindow().getAttributes().width = (int)(dialog.getContext().getResources().getDisplayMetrics().widthPixels * per);
        return this;
    }

    public CommonDialog setSeparateLineVisibility(boolean visibility) {
        buttonsSeparateLine.setVisibility(visibility ? View.VISIBLE : View.GONE);
        return this;
    }


    /**
     * 根据内容确定宽度
     *
     * @return
     */
    public CommonDialog setWrapContentWidth() {
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        return this;
    }

    /**
     * 是否允许点击对话框外部来关闭对话框
     * 同setCanceledOnTouchOutside，只是带了自身引用的返回
     *
     * @param canceled
     * @return
     */
    public CommonDialog setCloseOnTouchOutside(boolean canceled) {
        dialog.setCanceledOnTouchOutside(canceled);
        return this;
    }

    /**
     * 设置是否禁止返回键
     *
     * @param flag
     * @return CommonDialog
     */
    public CommonDialog setCancelable(boolean flag) {
        dialog.setCancelable(flag);
        return this;
    }

    /**
     * 设置是否显示标题栏
     * 默认内容非空时显示
     */
    public CommonDialog setTitleVisible(Visible visible) {
        titleVisible = visible;
        return this;
    }

    /**
     * 设置标题文本
     */
    public CommonDialog setTitleText(String text) {
        titleTextView.setText(text);
        return this;
    }

    /**
     * 设置标题文本
     */
    public CommonDialog setTitleText(int resid) {
        titleTextView.setText(resid);
        return this;
    }

    /**
     * 返回标题栏文本控件
     *
     * @return
     */
    public TextView getTitleTextView() {
        return titleTextView;
    }

    /**
     * 设置主图标是否可见
     * 默认内容非空时显示
     */
    public CommonDialog setIconVisible(Visible visible) {
        iconVisible = visible;
        return this;
    }

    /**
     * 设置主图标内容
     */
    public CommonDialog setIconResource(Drawable drawable) {
        iconView.setImageDrawable(drawable);
        return this;
    }

    /**
     * 设置主图标内容
     */
    public CommonDialog setIconRes(int resid) {
        iconView.setImageResource(resid);
        return this;
    }

    /**
     * 设置内容区的margin
     * @param leftDp
     * @param topDp
     * @param rightDp
     * @param bottomDp
     * @return
     */
    public CommonDialog setContentAreaMargin(int leftDp, int topDp, int rightDp, int bottomDp) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(DimensionUtil.dipToPx(dialog.getContext(),leftDp),
                DimensionUtil.dipToPx(dialog.getContext(), topDp),
                DimensionUtil.dipToPx(dialog.getContext(), rightDp),
                DimensionUtil.dipToPx(dialog.getContext(), bottomDp));
        dialogContentArea.setLayoutParams(lp);
        return this;
    }

    /**
     * 设置内容区的高度height
     * @param height
     * @return
     */
    public CommonDialog setContentAreaHeight(int height) {
        dialogContentArea.getLayoutParams().height = DimensionUtil.dipToPx(dialog.getContext(), height);
        return this;
    }

    public CommonDialog setTextGravity(int gravity) {
        textView.setGravity(gravity);
        return this;
    }
    /**
     * 设置是否显示主文本
     * 默认内容非空时显示
     */
    public CommonDialog setTextVisible(Visible visible) {
        textVisible = visible;
        return this;
    }

    public CommonDialog setText(CharSequence text) {
        textView.setText(text);
        return this;
    }
    /**
     * 设置主文本内容，需要换行可使用“\n”
     */
    public CommonDialog setText(String text) {
        textView.setText(text);
        return this;
    }

    /**
     * 设置主文本内容的MovementMethod
     */
    public CommonDialog setTextMovementMethod(MovementMethod movement){
        textView.setMovementMethod(movement);
        return this;
    }

    /**
     * 设置文本size， 单位为SP
     * @param size
     * @return
     */
    public CommonDialog setTextSize(float size) {
        textView.setTextSize(size);
        return this;
    }

    /**
     * 设置主文本内容，需要换行可使用“\n”
     */
    public CommonDialog setText(int resid) {
        textView.setText(resid);
        return this;
    }

    /**
     * 设置texview的margin
     * @param leftDp
     * @param topDp
     * @param rightDp
     * @param bottomDp
     * @return
     */
    public CommonDialog setTextMargin(int leftDp, int topDp, int rightDp, int bottomDp) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(DimensionUtil.dipToPx(dialog.getContext(), leftDp),
                DimensionUtil.dipToPx(dialog.getContext(), topDp),
                DimensionUtil.dipToPx(dialog.getContext(), rightDp),
                DimensionUtil.dipToPx(dialog.getContext(), bottomDp));
        textView.setLayoutParams(lp);
        return this;
    }

    /**
     * 设置textView的padding，上下左右都为同一个值
     * @param intDp
     * @return
     */
    public CommonDialog setTextPadding(int intDp) {
        int intPx = DimensionUtil.dipToPx(dialog.getContext(), intDp);
        textView.setPadding(intPx, intPx, intPx, intPx);
        return this;
    }



    /**
     * 设置文本输入框是否可见
     * 默认Hint或Text非空时显示
     */
    public CommonDialog setEditTextVisible(Visible visible) {
        editTextVisible = visible;
        return this;
    }

    /**
     * 设置文本输入提示
     */
    public CommonDialog setEditHint(String text) {
        editText.setHint(text);
        return this;
    }

    public EditText getEditTextView() {
        return editText;
    }

    /**
     * 设置键盘输入事件
     * 默认或设置为null时，无键盘事件
     */
    public CommonDialog setEditDoneAction(final OnActionListener action) {
        editText.setImeOptions(action == null ? EditorInfo.IME_NULL : EditorInfo.IME_ACTION_DONE);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (action != null) {
                        action.onAction(EDIT_DONE);
                        return true;
                    }
                }
                return false;
            }
        });

        return this;
    }

    /**
     * 设置文本输入提示
     */
    public CommonDialog setEditHint(int resid) {
        editText.setHint(resid);
        return this;
    }

    /**
     * 设置默认输入文本
     */
    public CommonDialog setEditText(String text) {
        editText.setText(text);
        return this;
    }

    /**
     * 设置默认输入文本
     */
    public CommonDialog setEditText(int resid) {
        editText.setText(resid);
        return this;
    }

    /**
     * 设置当前已输入的文本内容
     *
     * @return
     */
    public String getEditText() {
        return editText.getText() == null ? "" : editText.getText().toString();
    }

    /**
     * 设置是否显示进度条
     * 默认不显示
     */
    public CommonDialog setProgressVisible(Visible visible) {
        progressVisible = visible;
        return this;
    }

    /**
     * 设置进度条进度值
     *
     * @param value 进度值，0~100
     */
    public CommonDialog setProgress(int value) {
        progressBar.setProgress(value);
        progressAssign = true;
        return this;
    }

    /**
     * 设置进度条进度值，以动画方式显示
     *
     * @param value    进度值，0~100
     * @param duration 动画持续时间
     * @return
     */
    public CommonDialog setAnimatedProgress(int value, int duration) {
        if (progressAnimator == null) {
            progressAnimator = new ValueAnimator();
            progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    progressBar.setProgress((Integer) animation.getAnimatedValue());
                }
            });
        }

        progressAnimator.cancel();
        progressAnimator.setIntValues(progressBar.getProgress(), value);
        progressAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        progressAnimator.setDuration(duration).start();

        return setProgress(progressBar.getProgress());
    }

    /**
     * 设置进度条进度值，以动画方式显示
     *
     * @param value 进度值，0~100
     * @return
     */
    public CommonDialog setAnimatedProgress(int value) {
        return setAnimatedProgress(value, 500);
    }

    /**
     * 获得当前进度值
     */
    public int getProgress() {
        return progressBar.getProgress();
    }

    /**
     * 设置按钮组的高度 单位dp
     * @param height
     * @return
     */
    public CommonDialog setButtonsLayoutHeight(int height) {
        buttonsLayout.getLayoutParams().height = DimensionUtil.dipToPx(dialog.getContext(), height);
        return this;
    }

    /**
     * 设置是否显示按钮
     * 默认非空时显示，左右按钮模式 与 主按钮 模式不能共存，主按钮优先显示
     */
    public CommonDialog setButtonsVisible(Visible visible) {
        buttonsVisible = visible;
        return this;
    }

    /**
     * 设置左按钮文本
     */
    public CommonDialog setLeftButtonText(String text) {
        leftButton.setText(text);
        return this;
    }

    /**
     * 设置左按钮文本
     */
    public CommonDialog setLeftButtonText(int resid) {
        leftButton.setText(resid);
        return this;
    }

    /**
     * 设置左按钮事件响应
     * 不设置的话默认处理是关闭对话框
     */
    public CommonDialog setLeftButtonAction(final OnActionListener action) {
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (action != null) {
                    action.onAction(BUTTON_LEFT_CLICKED);
                }
                dismiss();
            }
        });

        return this;
    }

    /**
     * 设置右按钮文本
     */
    public CommonDialog setRightButtonText(String text) {
        rightButton.setText(text);
        return this;
    }

    /**
     * 设置右按钮文本
     */
    public CommonDialog setRightButtonText(int resid) {
        rightButton.setText(resid);
        return this;
    }

    /**
     * 设置右按钮事件响应
     * 不设置的话默认处理是关闭对话框
     */
    public CommonDialog setRightButtonAction(final OnActionListener action) {
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (action != null) {
                    action.onAction(BUTTON_RIGHT_CLICKED);
                }
                dismiss();
            }
        });

        return this;
    }

    /**
     * 设置主按钮文本
     */
    public CommonDialog setMainButtonText(String text) {
        mainButton.setText(text);
        return this;
    }

    /**
     * 设置主按钮文本
     */
    public CommonDialog setMainButtonText(int resid) {
        mainButton.setText(resid);
        return this;
    }

    /**
     * 设置主按钮事件响应
     * 不设置的话默认处理是关闭对话框
     */
    public CommonDialog setMainButtonAction(final OnActionListener action) {
        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (action != null) {
                    action.onAction(BUTTON_MAIN_CLICKED);
                }
                dismiss();
            }
        });

        return this;
    }

    /**
     * 设置按钮内文字大小
     * @param size
     * @return
     */
    public CommonDialog setMainButtonTextSize(int size) {
        mainButton.setTextSize(size);
        return this;
    }


    /**
     * 获得自定义view
     *
     * @return 自定义内容
     */
    public View getContentView() {
        View view = null;
        if (centerContainer.getChildCount() > 0) {
            view = centerContainer.getChildAt(0);
        }
        return view;
    }

    /**
     * 设置自定义内容
     *
     * @param view
     */
    public CommonDialog setContentView(View view) {
        centerContainer.removeAllViews();
        centerContainer.addView(view);

        return this;
    }

    /**
     * 设置自定义内容
     *
     * @param view
     */
    public CommonDialog setContentView(View view, LinearLayout.LayoutParams layoutParams) {
        centerContainer.removeAllViews();
        centerContainer.addView(view, layoutParams);

        return this;
    }

    /**
     * 获得按钮
     *
     * @param whichButton BUTTON_LEFT or BUTTON_RIGHT or BUTTON_MAIN
     * @return button
     */
    public Button getButton(int whichButton) {
        if (whichButton == BUTTON_LEFT) {
            return leftButton;
        } else if (whichButton == BUTTON_RIGHT) {
            return rightButton;
        } else if (whichButton == BUTTON_MAIN) {
            return mainButton;
        }
        return null;
    }

    public void show() {
        if (titleVisible == Visible.Gone || TextUtils.isEmpty(titleTextView.getText())) {
            titleTextView.setVisibility(View.GONE);
        }

        boolean isUnique = true;
        if (iconVisible == Visible.Gone || iconView.getDrawable() == null) {
            iconView.setVisibility(View.GONE);
        } else {
            iconView.setVisibility(View.VISIBLE);
            isUnique = false;
        }

        if (textVisible == Visible.Gone || TextUtils.isEmpty(textView.getText())) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
            if (!isUnique) {
                cutTopMargin(textView);
            }
            isUnique = false;
        }

        if (editTextVisible == Visible.Gone ||
                (TextUtils.isEmpty(editText.getText()) && TextUtils.isEmpty(editText.getHint()))) {
            editText.setVisibility(View.GONE);
        } else {
            editText.setVisibility(View.VISIBLE);
            if (!isUnique) {
                cutTopMargin(editText);
            }
            isUnique = false;

            ImeUtil.showIME(dialog.getContext(),editText);
        }

        if (progressVisible == Visible.Gone || !progressAssign) {
            progressBar.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            if (!isUnique) {
                cutTopMargin(progressBar);
            }
            isUnique = false;
        }

        if (buttonsVisible == Visible.Gone
                || (TextUtils.isEmpty(mainButton.getText())
                && TextUtils.isEmpty(leftButton.getText())
                && TextUtils.isEmpty(rightButton.getText()))) {
            buttonsLayout.setVisibility(View.GONE);
        } else {
            buttonsLayout.setVisibility(View.VISIBLE);

            // 主按钮与左右按钮互斥
            if (TextUtils.isEmpty(mainButton.getText())) {
                mainButton.setVisibility(View.GONE);
                leftButton.setVisibility(View.VISIBLE);
                rightButton.setVisibility(View.VISIBLE);
                buttonsSeparateLine.setVisibility(View.VISIBLE);
            } else {
                mainButton.setVisibility(View.VISIBLE);
                leftButton.setVisibility(View.GONE);
                rightButton.setVisibility(View.GONE);
                buttonsSeparateLine.setVisibility(View.GONE);
            }
        }

        dialog.show();
    }

    public void dismiss() {
        ImeUtil.hideIME(dialog.getContext(),dialog.getCurrentFocus());
        dialog.dismiss();
    }

    private void cutTopMargin(View view) {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) view.getLayoutParams();
        lp.topMargin = 0;
        view.requestLayout();
    }

    public interface OnActionListener {
        void onAction(int which);
    }
}
