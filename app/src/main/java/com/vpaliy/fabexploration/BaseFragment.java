package com.vpaliy.fabexploration;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {

    private Unbinder unbinder;

    @Override
    public void onStop() {
        super.onStop();
        if(unbinder!=null){
            unbinder.unbind();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root=inflater.inflate(mainRes(),container,false);
        unbinder= ButterKnife.bind(this,root);
        return root;
    }

    @LayoutRes
    protected abstract int mainRes();
}
