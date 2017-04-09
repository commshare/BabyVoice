package com.lihb.babyvoice.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.lihb.babyvoice.R;
import com.lihb.babyvoice.adapter.ViewPageAdapter;
import com.lihb.babyvoice.prsenter.ImageBrowsePresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jelly on 2016/9/3.
 */
public class ImageBrowseActivity extends Activity implements ViewPager.OnPageChangeListener, ImageBrowseView {

    private ViewPager vp;
    private TextView hint;
    //    private TextView save;
    private ViewPageAdapter adapter;
    private ImageBrowsePresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setContentView(R.layout.activity_image_browse);
        vp = (ViewPager) this.findViewById(R.id.viewPager);
        hint = (TextView) this.findViewById(R.id.hint);
//        save = (TextView) this.findViewById(R.id.save);
//        save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                presenter.saveImage();
//            }
//        });
        initPresenter();
        presenter.loadImage();
    }

    public void initPresenter() {
        presenter = new ImageBrowsePresenter(this);
    }

    @Override
    public Intent getDataIntent() {
        return getIntent();
    }

    @Override
    public Context getMyContext() {
        return this;
    }

    @Override
    public void setImageBrowse(List<String> images, int position) {
        if (adapter == null && images != null && images.size() != 0) {
            adapter = new ViewPageAdapter(this, images);
            vp.setAdapter(adapter);
            vp.setCurrentItem(position);
            vp.addOnPageChangeListener(this);
            hint.setText(position + 1 + "/" + images.size());
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        presenter.setPosition(position);
        hint.setText(position + 1 + "/" + presenter.getImages().size());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public static void startActivity(Context context, ArrayList<String> images, int position) {
        Intent intent = new Intent(context, ImageBrowseActivity.class);
        intent.putStringArrayListExtra("images", images);
        intent.putExtra("position", position);
        context.startActivity(intent);
    }


}
