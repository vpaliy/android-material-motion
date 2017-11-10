package com.vpaliy.fabexploration;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Switch;
import android.widget.TextView;
import com.vpaliy.fabexploration.dots.DotsFragment;
import com.vpaliy.fabexploration.player.PlayerFragment;
import butterknife.ButterKnife;
import butterknife.BindView;

public class MainActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener{

    private static final String WARNING_KEY="enabled:warning";

    @BindView(R.id.drawer)
    protected DrawerLayout drawer;

    @BindView(R.id.navigation)
    protected NavigationView navigation;

    @BindView(R.id.message)
    protected TextView message;

    private SharedPreferences preferences;
    private NotifierTask notifierTask;
    private boolean enabledWarning=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ButterKnife.bind(this);
        setUpDrawer();
        ViewCompat.setElevation(message,getResources().
                getDimensionPixelOffset(R.dimen.message_elevation));
        loadFragment(new PlayerFragment());
        preferences= PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(this);
        onSharedPreferenceChanged(preferences,WARNING_KEY);

    }

    private void setUpDrawer(){
        drawer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        navigation.setNavigationItemSelectedListener(item ->{
            drawer.closeDrawers();
            switch (item.getItemId()) {
                case R.id.player:
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    loadFragment(new PlayerFragment());
                    return true;
                case R.id.dots:
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    loadFragment(new DotsFragment());
                    return true;
                case R.id.enable_message:
                    Switch view=(Switch)MenuItemCompat.getActionView(item);
                    preferences.edit()
                            .putBoolean(WARNING_KEY,view.isEnabled())
                            .apply();
                    return true;
            }
            return false;
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(WARNING_KEY)) {
            enabledWarning = sharedPreferences.getBoolean(key,enabledWarning);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(preferences!=null){
            preferences.unregisterOnSharedPreferenceChangeListener(this);
            preferences=null;
        }
    }

    private void loadFragment(Fragment fragment){
        showWarning();
        if(fragment!=null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame,fragment)
                    .commit();
        }
    }

    private void showWarning(){
        if(enabledWarning) {
            if(notifierTask!=null) {
                notifierTask.cancel();
            }
            message.setText(R.string.drawer_warning);
            message.setScaleX(0f);
            message.setScaleY(0f);
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
                            notifierTask=new NotifierTask.Starter(message, 3)
                                    .setCallback(() -> message.post(() ->
                                            message.animate()
                                                    .scaleY(0)
                                                    .scaleX(0)
                                                    .setStartDelay(0)
                                                    .setDuration(200)
                                                    .setListener(null)
                                                    .setInterpolator(new LinearInterpolator())
                                                    .start()))
                                    .setPeriod(1000)
                                    .setDelay(1000)
                                    .start();
                        }
                    })
                    .start();
        }
    }
}
