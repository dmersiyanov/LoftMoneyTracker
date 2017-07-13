package com.example.mitya.loftmoneytracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


public class MainActivity extends AppCompatActivity {
    private TabLayout tabs;
    private ViewPager pages;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabs = (TabLayout) findViewById(R.id.tabs);
        pages = (ViewPager) findViewById(R.id.pages);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!((LsApp) getApplication()).isLoggedIn())
            startActivity(new Intent(this, AuthActivity.class));
        else {
            setSupportActionBar(toolbar);
            pages.setAdapter(new MainPagerAdapter());
            tabs.setupWithViewPager(pages);
        }
    }

    private class MainPagerAdapter extends FragmentPagerAdapter {
        private final String[] titles;
        private final String[] types = {Item.TYPE_EXPENSE, Item.TYPE_INCOME};

        MainPagerAdapter() {
            super(getSupportFragmentManager());
            titles = getResources().getStringArray(R.array.main_pager_titles);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle args = new Bundle();
            if (position == getCount() - 1)
                return new BalanceFragment();
            args.putString("type", types[position]);

            final ItemsFragment itemsFragment = new ItemsFragment();
            itemsFragment.setArguments(args);
            return itemsFragment;
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
