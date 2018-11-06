package com.mobile.carcare.carcare.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.mobile.carcare.carcare.R;
import com.mobile.carcare.carcare.fragment.FavoritesFragment;
import com.mobile.carcare.carcare.fragment.MainFragment;
import com.mobile.carcare.carcare.fragment.MoreFragment;
import com.mobile.carcare.carcare.fragment.SearchFragment;
import com.mobile.carcare.carcare.fragment.SettingsAgencyFragment;
import com.mobile.carcare.carcare.fragment.SettingsUserFragment;
import com.mobile.carcare.carcare.utils.Font;
import com.mobile.carcare.carcare.utils.MyPreferences;

import java.util.List;

import static com.mobile.carcare.carcare.utils.Constants.AGENCY_SETTINGS_FRAGMENT_TAG;
import static com.mobile.carcare.carcare.utils.Constants.FAVORITES_FRAGMENT_TAG;
import static com.mobile.carcare.carcare.utils.Constants.MAIN_FRAGMENT_TAG;
import static com.mobile.carcare.carcare.utils.Constants.MORE_FRAGMENT_TAG;
import static com.mobile.carcare.carcare.utils.Constants.SEARCH_FRAGMENT_TAG;
import static com.mobile.carcare.carcare.utils.Constants.USER_SETTINGS_FRAGMENT_TAG;

public class HomeActivity extends AppCompatActivity {


    Button btnMain, btnFavorites, btnSettings, btnMore;
    ImageView btnSearch;
    int inPixels;
    MyPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Font.apply(this, findViewById(android.R.id.content));
        preferences = new MyPreferences(HomeActivity.this);
        btnMain = findViewById(R.id.btnMain);
        btnFavorites = findViewById(R.id.btnFavorites);
        btnMore = findViewById(R.id.btnMore);
        btnSettings = findViewById(R.id.btnSettings);
        btnSearch = findViewById(R.id.btnSearch);
        inPixels = (int) HomeActivity.this.getResources().getDimension(R.dimen.bottom_nav_hover);

        btnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnMain.setWidth(inPixels);
                btnMain.setWidth(inPixels);
                btnSearch.setPadding(8, 8, 8, 8);
                btnMain.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_home_hover, 0, 0);
                btnFavorites.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_fav, 0, 0);
                btnSettings.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_settings, 0, 0);
                btnMore.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_more, 0, 0);

                MainFragment mainFragment = new MainFragment();
                openFragment(mainFragment, MAIN_FRAGMENT_TAG);
            }
        });

        btnFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFavorites.setWidth(inPixels);
                btnFavorites.setWidth(inPixels);
                btnSearch.setPadding(8, 8, 8, 8);
                btnFavorites.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_fav_hover, 0, 0);
                btnMain.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_home, 0, 0);
                btnSettings.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_settings, 0, 0);
                btnMore.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_more, 0, 0);

                if (isSignedIn()) {
                    openFragment(new FavoritesFragment(), FAVORITES_FRAGMENT_TAG);
                } else {
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                }
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSearch.setPadding(0, 0, 0, 0);
                btnMain.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_home, 0, 0);
                btnFavorites.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_fav, 0, 0);
                btnSettings.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_settings, 0, 0);
                btnMore.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_more, 0, 0);
                openFragment(new SearchFragment(), SEARCH_FRAGMENT_TAG);
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSettings.setWidth(inPixels);
                btnSettings.setWidth(inPixels);
                btnSearch.setPadding(8, 8, 8, 8);
                btnSettings.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_settings_hover, 0, 0);
                btnMain.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_home, 0, 0);
                btnFavorites.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_fav, 0, 0);
                btnMore.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_more, 0, 0);

                if (preferences.getUserType() == 1) {
                    openFragment(new SettingsUserFragment(), USER_SETTINGS_FRAGMENT_TAG);
                } else if (preferences.getUserType() == 2) {
                    openFragment(new SettingsAgencyFragment(), AGENCY_SETTINGS_FRAGMENT_TAG);
                } else {
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                }
            }
        });

        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnMore.setWidth(inPixels);
                btnMore.setWidth(inPixels);
                btnSearch.setPadding(8, 8, 8, 8);
                MoreFragment moreFragment = new MoreFragment();
                openFragment(moreFragment, MORE_FRAGMENT_TAG);
                btnMore.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_more_hover, 0, 0);
                btnMain.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_home, 0, 0);
                btnFavorites.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_fav, 0, 0);
                btnSettings.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_settings, 0, 0);

            }
        });
        if (savedInstanceState !=null){
            if (savedInstanceState.containsKey("current_tag")){
                String currentTag =savedInstanceState.getString("current_tag");
                if (currentTag!=null){
                    switch (currentTag){
                        case MAIN_FRAGMENT_TAG:
                            btnMain.performClick();
                            break;
                        case MORE_FRAGMENT_TAG:
                            btnMore.performClick();
                            break;
                        case AGENCY_SETTINGS_FRAGMENT_TAG:
                            btnSettings.performClick();
                            break;
                        case USER_SETTINGS_FRAGMENT_TAG:
                            btnSettings.performClick();
                            break;
                        case SEARCH_FRAGMENT_TAG:
                            btnSearch.performClick();
                            break;
                        case FAVORITES_FRAGMENT_TAG:
                            btnFavorites.performClick();
                            break;
                    }
                }
            }
        }
        else {
            btnMain.performClick();
        }

    }

    private void openFragment(Fragment fragment, String fragmentTag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fl_home, fragment, fragmentTag);
        transaction.addToBackStack(fragmentTag);
        transaction.commit();
    }

    private boolean isSignedIn() {
        int userType = preferences.getUserType();
        return userType != -1;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        String current_Frag_tag = getVisibleFragmentTag();
        outState.putString("current_tag",current_Frag_tag);
        super.onSaveInstanceState(outState);
    }

    public String getVisibleFragmentTag() {
        FragmentManager fragmentManager = HomeActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible())
                return fragment.getTag();
        }
        return null;
    }
}