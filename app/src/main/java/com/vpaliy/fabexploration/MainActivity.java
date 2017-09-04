package com.vpaliy.fabexploration;

import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.vpaliy.fabexploration.dots.DotsFragment;
import com.vpaliy.fabexploration.player.PlayerFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.drawer)
    protected DrawerLayout drawer;

    @BindView(R.id.frame)
    protected FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ButterKnife.bind(this);
        setUpDrawer();
    }

    private void setUpDrawer(){
        drawer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        NavigationView navigationView=ButterKnife.findById(this,R.id.navigation);
        navigationView.setNavigationItemSelectedListener(item ->{
            drawer.closeDrawers();
            switch (item.getItemId()){
                case R.id.player:
                    loadFragment(new PlayerFragment());
                    return true;
                case R.id.dots:
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
