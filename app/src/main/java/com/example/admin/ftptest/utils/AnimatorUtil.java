package com.example.admin.ftptest.utils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;

public class AnimatorUtil {
    public static void startAnimator(final View view, final float from,final float to){
        view.setVisibility(View.VISIBLE);
        view.post(new Runnable(){
            @Override
            public void run() {
                ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY",from,to);
                animator.setDuration(300);
                animator.start();
            }
        });
    }
    public static void dismissAnimator(final View view,float from,float to){
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(view, "translationY", from,to);
        animator1.setDuration(300);
        Animator.AnimatorListener dismissListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        };
        animator1.addListener(dismissListener);
        animator1.start();
    }
}
