package com.example.admin.ftptest.MyView;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.admin.ftptest.R;

/**
 * Created by admin on 2018/5/16.
 */

public class SortWayPopup extends PopupWindow {
    private View mMenuView;
    private TextView descByTime,descByFileName,ascByFileSize,descByFileSize;

    public SortWayPopup(final Activity context, View.OnClickListener itemsOnClick) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.header_sortway_choise_layout, null);
         descByTime = mMenuView.findViewById(R.id.desc_by_time);
        descByFileName = mMenuView.findViewById(R.id.desc_by_fileName);
        ascByFileSize = mMenuView.findViewById(R.id.asc_by_fileSize);
        descByFileSize = mMenuView.findViewById(R.id.desc_by_fileSize);

        descByTime.setOnClickListener(itemsOnClick);
        descByFileName.setOnClickListener(itemsOnClick);
        ascByFileSize.setOnClickListener(itemsOnClick);
        descByFileSize.setOnClickListener(itemsOnClick);
        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(260);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        //this.setAnimationStyle(R.style.AnimBottom);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xFFE6E6E6);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = mMenuView.findViewById(R.id.pop_layout).getTop();
                int y=(int) event.getY();
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(y<height){
                        dismiss();
                    }
                }
                return true;
            }
        });
    }

}
