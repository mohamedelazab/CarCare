package com.mobile.carcare.carcare.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mobile.carcare.carcare.R;
import com.mobile.carcare.carcare.adapter.MyPagerAdapter;
import com.mobile.carcare.carcare.fragment.AddServiceFragment;
import com.mobile.carcare.carcare.fragment.ProfileMapFragment;
import com.mobile.carcare.carcare.fragment.ProfileServicesFragment;
import com.mobile.carcare.carcare.model.Agency;
import com.mobile.carcare.carcare.utils.Constants;
import com.mobile.carcare.carcare.utils.Font;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AgencyProfileActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CALL_PHONE = 85;
    @BindView(R.id.tv_agency_name_profile)
    TextView tvAgencyName;
    @BindView(R.id.tv_agency_description_profile)
    TextView tvAgencyDescription;
    @BindView(R.id.tv_agency_address_profile)
    TextView tvAgencyAddress;
    @BindView(R.id.tv_agency_country_profile)
    TextView tvAgencyCountry;
    @BindView(R.id.tv_agency_province_profile)
    TextView tvAgencyProvince;
    @BindView(R.id.tv_agency_city_profile)
    TextView tvAgencyCity;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.fab_edit)
    FloatingActionButton fabEdit;
    @BindView(R.id.fab_call)
    FloatingActionButton fabCall;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.image_header_profile)
    ImageView imageHeaderProfile;
    @BindView(R.id.image_avatar_profile)
    ImageView imageAvatarProfile;
    Agency agency;
    boolean isFABOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agency_profile);
        Font.apply(this, findViewById(android.R.id.content));
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        collapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);
        agency = new Agency();
        agency = getIntent().getExtras().getParcelable(Constants.Agency_Profile_Intent);
        tvAgencyName.setText(agency.getName());
        tvAgencyDescription.setText(agency.getDescription());
        tvAgencyAddress.setText(agency.getAddress());
        tvAgencyCountry.setText(agency.getCountryName());
        tvAgencyProvince.setText(agency.getProvinceName());
        tvAgencyCity.setText(agency.getCityName());
        Picasso.get().load(agency.getHeader()).placeholder(R.drawable.bg2).into(imageHeaderProfile);
        if(!agency.getAvatar().equals("default"))
        Picasso.get().load(agency.getAvatar()).placeholder(R.drawable.placeholder).into(imageAvatarProfile);
        Log.e("Agency_UID", agency.getKey()+"");

        setupViewPager(viewPager);

        tabLayout.setupWithViewPager(viewPager);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFABOpen) {
                    closeFABMenu();
                } else {
                    showFABMenu();
                }
            }
        });

        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
                AddServiceFragment serviceFragment =new AddServiceFragment();
                Bundle bundle =new Bundle();
                bundle.putString("Agency_Key",agency.getKey());
                serviceFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.layout_profile,serviceFragment,"Service_Frag").addToBackStack("Service_Frag").commit();
            }
        });

        fabCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(AgencyProfileActivity.this,
                        Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(AgencyProfileActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            PERMISSIONS_REQUEST_CALL_PHONE);

                    // MY_PERMISSIONS_REQUEST_CALL_PHONE is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
                else {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + agency.getPhoneNumber()));
                    startActivity(callIntent);
                }
            }
        });

        if (!isProfileOfMine()){
            fabEdit.hide();
        }
    }

    private boolean isProfileOfMine() {
        FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();
        if (user !=null){
            return user.getUid().equals(agency.getKey());
        }
        return false;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the phone call
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + agency.getPhoneNumber()));
                    startActivity(callIntent);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

        private void setupViewPager(ViewPager viewPager) {
        List<Fragment> fragments =new ArrayList<>();
        List<String> titles =new ArrayList<>();
        Bundle bundle =new Bundle();
        bundle.putString("lat",agency.getLat());
        bundle.putString("lng",agency.getLng());
        bundle.putString("agencyName",agency.getName());
        ProfileMapFragment profileMapFragment =new ProfileMapFragment();
        profileMapFragment.setArguments(bundle);

        ProfileServicesFragment profileServicesFragment=new ProfileServicesFragment();
        Bundle servicesBundle =new Bundle();
        servicesBundle.putString("Agency_Key",agency.getKey());
        profileServicesFragment.setArguments(servicesBundle);
        fragments.add(profileServicesFragment);
        fragments.add(profileMapFragment);
        titles.add("Services");
        titles.add("Location");
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager(), fragments, titles);
        viewPager.setAdapter(adapter);
    }

    private void showFABMenu(){
        isFABOpen=true;
        fabCall.animate().translationY(-getResources().getDimension(R.dimen.standard_75));
        fabEdit.animate().translationY(-getResources().getDimension(R.dimen.standard_150));
    }

    private void closeFABMenu(){
        isFABOpen=false;
        fabEdit.animate().translationY(0);
        fabCall.animate().translationY(0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
