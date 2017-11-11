package com.vpaliy.fabexploration;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import com.bumptech.glide.Glide;
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
        setupWarning();
        setUpDrawer();
        ViewCompat.setElevation(message,getResources().
                getDimensionPixelOffset(R.dimen.message_elevation));
        loadFragment(new PlayerFragment());

    }

    private void setUpDrawer(){
        drawer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        final ImageView header=(ImageView)navigation.getHeaderView(0);
        Glide.with(this)
                .asDrawable()
                .load(R.drawable.header)
                .into(header);
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
                case R.id.git:
                    final Intent showGitHub=new Intent(Intent.ACTION_VIEW,
                            Uri.parse(getString(R.string.GitHub)));
                    startActivity(showGitHub);
                    return true;
            }
            return false;
        });
    }

    private void setupWarning(){
        preferences= PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(this);
        onSharedPreferenceChanged(preferences,WARNING_KEY);
        final MenuItem item=navigation.getMenu().findItem(R.id.enable_message);
        Switch view=(Switch)MenuItemCompat.getActionView(item);
        view.setChecked(preferences.getBoolean(WARNING_KEY,false));
        view.setOnCheckedChangeListener((button,isChecked)->{
            if(enabledWarning!=isChecked) {
                preferences.edit()
                        .putBoolean(WARNING_KEY, isChecked)
                        .apply();
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(WARNING_KEY)) {
            final boolean isChanged=sharedPreferences.getBoolean(key,enabledWarning);
            if(isChanged!=enabledWarning) {
                enabledWarning=isChanged;
                showWarning();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(preferences!=null){
            preferences.unregisterOnSharedPreferenceChangeListener(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(preferences!=null){
            preferences.registerOnSharedPreferenceChangeListener(this);
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
        if(notifierTask!=null) {
            notifierTask.cancel();
        }
        message.setText(R.string.drawer_warning);
        message.setScaleX(0f);
        message.setScaleY(0f);
        if(enabledWarning) {
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
                    }).start();
        }
    }
}
