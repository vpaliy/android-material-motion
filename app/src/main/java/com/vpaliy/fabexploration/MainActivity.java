package com.vpaliy.fabexploration;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;
import com.vpaliy.fabexploration.dots.DotsFragment;
import com.vpaliy.fabexploration.player.PlayerFragment;
import butterknife.ButterKnife;
import butterknife.BindView;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.drawer)
    protected DrawerLayout drawer;

    @BindView(R.id.navigation)
    protected NavigationView navigation;

    @BindView(R.id.message)
    protected TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ButterKnife.bind(this);
        setUpDrawer();
        ViewCompat.setElevation(message,getResources().
                getDimensionPixelOffset(R.dimen.message_elevation));
        loadFragment(new PlayerFragment());
    }

    private void setUpDrawer(){
        drawer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        navigation.setNavigationItemSelectedListener(item ->{
            drawer.closeDrawers();
            switch (item.getItemId()){
                case R.id.player:
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    loadFragment(new PlayerFragment());
                    return true;
                case R.id.dots:
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    loadFragment(new DotsFragment());
                    return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment){
        message.setScaleX(0f);message.setScaleY(0f);
        message.animate()
                .scaleX(1)
                .scaleY(1)
                .setStartDelay(500)
                .setDuration(300)
                .setInterpolator(new OvershootInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        final ObjectAnimator dragAnimator=ObjectAnimator.ofFloat(message,View.TRANSLATION_X,-50,50);
                        dragAnimator.setRepeatCount(2);
                        dragAnimator.setDuration(100);
                        dragAnimator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                message.setTranslationX(0);
                                new NotifierTask.Starter(message,5)
                                        .setCallback(()-> message.postDelayed(() ->
                                                message.animate()
                                                        .scaleY(0)
                                                        .scaleX(0)
                                                        .setStartDelay(0)
                                                        .setDuration(200)
                                                        .setListener(null)
                                                        .setInterpolator(new LinearInterpolator())
                                                        .start(), 2000))
                                        .setPeriod(1000)
                                        .start();
                            }
                        });
                        dragAnimator.start();
                    }
                })
                .start();
        if(fragment!=null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame,fragment)
                    .commit();
        }
    }
}
