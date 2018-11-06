package com.mobile.carcare.carcare.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mobile.carcare.carcare.R;
import com.mobile.carcare.carcare.activity.HomeActivity;
import com.mobile.carcare.carcare.utils.Font;
import com.mobile.carcare.carcare.utils.MyPreferences;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.mobile.carcare.carcare.utils.Helper.enableTouch;
import static com.mobile.carcare.carcare.utils.Validate.isNotEmpty;
import static com.mobile.carcare.carcare.utils.Validate.isValidEmail;
import static com.mobile.carcare.carcare.utils.Validate.passwordMatch;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpUserFragment extends Fragment {

    @BindView(R.id.et_name_usr_sign)
    EditText etName;

    @BindView(R.id.et_email_usr_sign)
    EditText etEmail;

    @BindView(R.id.et_pass_usr_sign)
    EditText etPass;

    @BindView(R.id.et_pass_confirm_usr_sign)
    EditText etPassConfirm;

    @BindView(R.id.btn_sign_usr)
    Button btnSignUp;

    DatabaseReference databaseReference;

    @BindView(R.id.progress_bar_user)
    ProgressBar mProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =inflater.inflate(R.layout.fragment_sign_up_user, container, false);
        ButterKnife.bind(this, rootView);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name =etName.getText().toString().trim();
                String email =etEmail.getText().toString().trim();
                String pass =etPass.getText().toString();
                String passConfirm =etPassConfirm.getText().toString();

                if (isValidEmail(email) && isNotEmpty(name) && isNotEmpty(pass) && isNotEmpty(passConfirm) && passwordMatch(pass,passConfirm)){
                    SignUpUser(name, email, pass);
                }
                else {
                    if (!isNotEmpty(name)){
                        etName.setError(getContext().getResources().getString(R.string.required));
                    }
                    if (!isValidEmail(email)){
                        etEmail.setError(getContext().getResources().getString(R.string.not_email));
                    }
                    if (!isNotEmpty(pass)){
                        etPass.setError(getContext().getResources().getString(R.string.required));
                    }
                    if (!passwordMatch(pass,passConfirm)){
                        etPassConfirm.setError(getContext().getResources().getString(R.string.not_match));
                    }
                }
            }
        });
        Font.apply(getContext(), rootView);
        return rootView;
    }

    private void SignUpUser(final String name, String email, String pass) {

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    String uid =FirebaseAuth.getInstance().getCurrentUser().getUid();
                    new MyPreferences(getContext()).setUserType(1);
                    databaseReference = FirebaseDatabase.getInstance().getReference()
                            .child("users").child(uid);
                    HashMap<String, String> userData =new HashMap<>();
                    userData.put("name",name);
                    userData.put("avatar","default");
                    userData.put("id",uid);
                    databaseReference.setValue(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                mProgressBar.setVisibility(View.GONE);
                                enableTouch(getActivity());
                                Intent newIntent =new Intent(getContext(), HomeActivity.class);
                                newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(newIntent);
                                getActivity().finish();
                            }
                        }
                    });
                }
                else {
                    try {
                        throw task.getException();
                    } catch(FirebaseAuthWeakPasswordException e) {
                        etPass.setError(getString(R.string.error_weak_password));
                    } catch(FirebaseAuthInvalidCredentialsException e) {
                        etEmail.setError(getString(R.string.error_invalid_email));
                    } catch(FirebaseAuthUserCollisionException e) {
                        etEmail.setError(getString(R.string.error_user_exists));
                        etEmail.requestFocus();
                    } catch(Exception e) {
                        Log.e("TAGGGG", e.getMessage());
                        Toast.makeText(getContext(), R.string.error_occured, Toast.LENGTH_SHORT).show();
                    }
                    mProgressBar.setVisibility(View.GONE);
                    enableTouch(getActivity());
                }
            }
        }).addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.GONE);
                enableTouch(getActivity());
            }
        });
    }
}