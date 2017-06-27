package com.example.mitya.loftmoneytracker;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;


public class FragmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragments);
        final TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        final ViewPager pages = (ViewPager) findViewById(R.id.pages);

        pages.setAdapter(new MainPagerAdapter());
        tabs.setupWithViewPager(pages);
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
            if (position == 2) args.putString("type", Item.TYPE_INCOME);
            else args.putString("type", types[position]);



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
