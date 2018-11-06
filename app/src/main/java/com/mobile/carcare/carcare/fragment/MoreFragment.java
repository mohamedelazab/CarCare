package com.mobile.carcare.carcare.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mobile.carcare.carcare.R;
import com.mobile.carcare.carcare.activity.HomeActivity;
import com.mobile.carcare.carcare.activity.LoginActivity;
import com.mobile.carcare.carcare.dialog.AboutDialog;
import com.mobile.carcare.carcare.dialog.ContactUsDialog;
import com.mobile.carcare.carcare.utils.Font;
import com.mobile.carcare.carcare.utils.MyPreferences;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoreFragment extends Fragment {

    Toolbar toolbar;
    @BindView(R.id.btn_rate_us)
    Button btnRate;
    @BindView(R.id.btn_about_us)
    Button btnAbout;
    @BindView(R.id.btn_contact_us)
    Button btnContact;
    @BindView(R.id.btn_Sign_in_out)
    Button btnSignInOut;
    boolean isUserSignedIn =true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =inflater.inflate(R.layout.fragment_more, container, false);
        ButterKnife.bind(this, rootView);
        toolbar =rootView.findViewById(R.id.toolbar_me2);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((TextView)rootView.findViewById(R.id.tv_toolbar)).setText(R.string.more);
        rootView.findViewById(R.id.img_view_type_toolbar).setVisibility(View.GONE);

        if (FirebaseAuth.getInstance().getCurrentUser() ==null){
            signOutMode();
        }

        btnSignInOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUserSignedIn) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(R.string.confirm_sign_out);
                    builder.setCancelable(false);
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseAuth.getInstance().signOut();
                            new MyPreferences(getActivity()).setUserType(-1);
                            signOutMode();
                            Toast.makeText(getContext(), R.string.done, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getContext(),HomeActivity.class));
                            getActivity().finish();

                        }
                    });

                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
                else {
                    startActivity(new Intent(getContext(),LoginActivity.class));
                }
            }
        });

        btnRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AboutDialog(getContext()).show();
            }
        });

        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();
                if (user !=null) {
                    ContactUsDialog dialog = new ContactUsDialog(getContext());
                    dialog.show();
                }
                else {
                    startActivity(new Intent(getContext(), LoginActivity.class));
                }
            }
        });
        Font.apply(getContext(), rootView);
        return rootView;
    }

    private void signOutMode() {
        isUserSignedIn =false;
        btnSignInOut.setText(R.string.sign_in);
        btnSignInOut.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.ic_sign_in);
    }
}