package com.mobile.carcare.carcare.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobile.carcare.carcare.R;
import com.mobile.carcare.carcare.dialog.SignUpTypeDialog;
import com.mobile.carcare.carcare.utils.Font;
import com.mobile.carcare.carcare.utils.MyPreferences;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.mobile.carcare.carcare.utils.Constants.SIGN_UP_TYPE;
import static com.mobile.carcare.carcare.utils.Helper.disableTouch;
import static com.mobile.carcare.carcare.utils.Helper.enableTouch;
import static com.mobile.carcare.carcare.utils.Validate.isNotEmpty;
import static com.mobile.carcare.carcare.utils.Validate.isValidEmail;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.btn_login)
    Button btnLogin;

    @BindView(R.id.et_email)
    EditText etEmail;

    @BindView(R.id.et_Pass)
    EditText etPassword;

    @BindView(R.id.tv_create_account)
    TextView tvCreateAccount;

    @BindView(R.id.progress_bar_login)
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Font.apply(this, findViewById(android.R.id.content));
        ButterKnife.bind(this);

        tvCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUpTypeDialog signUpTypeDialog = SignUpTypeDialog.getInstance(LoginActivity.this);
                signUpTypeDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intentSignUp = new Intent(LoginActivity.this, SignUpActivity.class);
                        switch (v.getId()) {
                            case R.id.btn_sign_user_dialog:
                                intentSignUp.putExtra(SIGN_UP_TYPE, 1);
                                break;

                            case R.id.btn_sign_agency_dialog:
                                intentSignUp.putExtra(SIGN_UP_TYPE, 2);
                                break;
                        }
                        startActivity(intentSignUp);
                    }
                });
                signUpTypeDialog.show();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String pass = etPassword.getText().toString().trim();
                if (isValidEmail(email) && isNotEmpty(pass)) {
                    SignIn(email, pass);
                } else {
                    if (!isValidEmail(email)) {
                        etEmail.setError("Not Email.!");
                    }
                    if (!isNotEmpty(pass)) {
                        etEmail.setError("Required");
                    }
                }
            }
        });
    }

    private void SignIn(String email, String pass) {
        mProgressBar.setVisibility(View.VISIBLE);
        disableTouch(LoginActivity.this);
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            detectUserType(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        } else {
                            // If sign in fails, display a message to the user.
                            String error;
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                error = getString(R.string.error_weak_password);
                                Toast.makeText(LoginActivity.this, "" + error, Toast.LENGTH_SHORT).show();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
//                                error =getString(R.string.error_invalid_email);
                                error = e.getMessage();
                                Toast.makeText(LoginActivity.this, "" + error, Toast.LENGTH_SHORT).show();
                            } catch (FirebaseAuthUserCollisionException e) {
                                error = getString(R.string.error_user_exists);
                                Toast.makeText(LoginActivity.this, "" + error, Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Log.e("TAGGGG", e.getMessage());
                                Toast.makeText(LoginActivity.this, task.getException() + "",
                                        Toast.LENGTH_LONG).show();
                            }
                            mProgressBar.setVisibility(View.GONE);
                            enableTouch(LoginActivity.this);
                        }
                    }
                });
    }

    private void detectUserType(String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    new MyPreferences(LoginActivity.this).setUserType(1);
                }
                else {
                    new MyPreferences(LoginActivity.this).setUserType(2);
                }
                Intent newIntent = new Intent(LoginActivity.this, HomeActivity.class);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(newIntent);
                finish();
                mProgressBar.setVisibility(View.GONE);
                enableTouch(LoginActivity.this);
                Log.e("USER_Login", FirebaseAuth.getInstance().getCurrentUser().getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mProgressBar.setVisibility(View.GONE);
                enableTouch(LoginActivity.this);
            }
        });
    }
}