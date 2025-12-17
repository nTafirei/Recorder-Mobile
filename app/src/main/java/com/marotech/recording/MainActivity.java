package com.marotech.recording;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.marotech.recording.fragments.HomeFragment;
import com.marotech.recording.fragments.LoginFragment;
import com.marotech.recording.fragments.LogoutFragment;
import com.marotech.recording.fragments.RecordingsFragment;
import com.marotech.recording.fragments.RegisterFragment;

public class MainActivity extends AbstractActivity {

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        loginFragment = new LoginFragment(this.getApplicationContext(), null);
        registerFragment = new RegisterFragment(this.getApplicationContext());
        syncFragment = new LogoutFragment(this.getApplicationContext());
        recordingsFragment = new RecordingsFragment(this.getApplicationContext());
        BottomNavigationView bottomNavigationView
                = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView
                .setOnNavigationItemSelectedListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("        " + getResources().getString(R.string.app_name));
        actionBar.setSubtitle("          " + getResources().getString(R.string.app_sub_title));
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        if (savedInstanceState == null) { // Activity created first time, not on rotation
            homeFragment = new HomeFragment(this.getApplicationContext());
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragment_container, homeFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, homeFragment)
                .commit();
        return true;
    }

    private final String TAG = "paystream_MainActivity";
}
