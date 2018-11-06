package com.mobile.carcare.carcare.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.mobile.carcare.carcare.R;
import com.mobile.carcare.carcare.fragment.SignUpAgencyFragment;
import com.mobile.carcare.carcare.fragment.SignUpUserFragment;
import com.mobile.carcare.carcare.utils.Font;

import static com.mobile.carcare.carcare.utils.Constants.SIGN_UP_TYPE;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Font.apply(this, findViewById(android.R.id.content));
        //ButterKnife.bind(this);
        int signUpChoice =getIntent().getExtras().getInt(SIGN_UP_TYPE);
        Fragment fragment;
        String tag;
        if (signUpChoice ==1){
            fragment =new SignUpUserFragment();
            tag ="SignUp_user";
        }
        else {
            fragment =new SignUpAgencyFragment();
            tag ="SignUp_agency";
        }
        openFragment(fragment,tag);

    }

    public void openFragment(Fragment fragment, String tag){

        SignUpActivity.this.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_sign_up,fragment,tag)
                //.addToBackStack(tag)
                .commit();
    }
}
