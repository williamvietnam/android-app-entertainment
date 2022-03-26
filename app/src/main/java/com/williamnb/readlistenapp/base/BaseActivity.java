package com.williamnb.readlistenapp.base;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

public abstract class BaseActivity<VB extends ViewBinding, VM extends BaseViewModel>
        extends AppCompatActivity implements BaseActivityView {

    protected VB viewBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(getClass().getName(), "onCreate()...");

        onPostOnCreate();

        viewBinding = getActivityBinding();
        setContentView(viewBinding.getRoot());

        initializeView();
        initializeComponent();
        initializeEvents();
        initializeData();
    }

    @Override
    protected void onDestroy() {
        Log.d(getClass().getName(), "onDestroy()...");
        super.onDestroy();
    }

    protected void onPostOnCreate() {
        // DO_NO_THING
    }

    protected abstract VB getActivityBinding();
}
