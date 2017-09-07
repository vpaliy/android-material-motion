package com.vpaliy.fabexploration;

import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import com.vpaliy.fabexploration.dots.DotsFragment;
import com.vpaliy.fabexploration.player.PlayerFragment;
import butterknife.ButterKnife;
import butterknife.BindView;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.drawer)
    protected DrawerLayout drawer;

    @BindView(R.id.frame)
    protected FrameLayout frameLayout;

    @BindView(R.id.navigation)
    protected NavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ButterKnife.bind(this);
        setUpDrawer();
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
        if(fragment!=null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame,fragment)
                    .commit();
        }
    }
}
