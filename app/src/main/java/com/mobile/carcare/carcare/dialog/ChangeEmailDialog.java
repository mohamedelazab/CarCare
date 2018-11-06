package com.mobile.carcare.carcare.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mobile.carcare.carcare.R;
import com.mobile.carcare.carcare.utils.Font;
import com.mobile.carcare.carcare.utils.Validate;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChangeEmailDialog extends Dialog {

    @BindView(R.id.et_new_email_dialog)
    EditText etNewEmail;
    @BindView(R.id.et_pass_dialog)
    EditText etPass;
    @BindView(R.id.btn_continue_change)
    Button btnContinue;
    @BindView(R.id.btn_close)
    ImageButton btnClose;

    @BindView(R.id.progress_bar_dialog)
    ProgressBar progressBar;
    @NonNull
    public static ChangeEmailDialog getInstance(Context context){
        return new ChangeEmailDialog(context);
    }

    private ChangeEmailDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getWindow() != null) {
            this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        setContentView(R.layout.dialog_change_email);
        Font.apply(getContext(), findViewById(android.R.id.content));
        ButterKnife.bind(this);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        setCancelable(false);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Validate.isNotEmpty(etPass.getText().toString().trim()) && Validate.isValidEmail(etNewEmail.getText().toString().trim())){
                    progressBar.setVisibility(View.VISIBLE);
                    changeEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail(), etPass.getText().toString(), etNewEmail.getText().toString().trim());
                }
                else {
                    etPass.setError(getContext().getResources().getString(R.string.required));
                    etNewEmail.setError(getContext().getResources().getString(R.string.required));
                }
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeEmailDialog.this.dismiss();
            }
        });
    }

    private void changeEmail(final String currentEmail, String password, final String newEmail) {
        FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential =EmailAuthProvider.getCredential(currentEmail,password);
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("TAG_USER_RE_AUTH", "User re-authenticated.");
                FirebaseUser user1 =FirebaseAuth.getInstance().getCurrentUser();
                user1.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.e("TAG_Email_update","Email updated");
                            progressBar.setVisibility(View.GONE);
                            btnClose.performClick();
                            Toast.makeText(getContext(), R.string.email_updated, Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });

    }
}
