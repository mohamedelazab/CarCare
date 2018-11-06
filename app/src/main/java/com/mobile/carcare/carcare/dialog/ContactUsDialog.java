package com.mobile.carcare.carcare.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mobile.carcare.carcare.R;
import com.mobile.carcare.carcare.utils.Font;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactUsDialog extends Dialog {

    @BindView(R.id.et_contact)
    EditText etContactUs;

    @BindView(R.id.btn_send)
    Button btnSend;

    @BindView(R.id.btn_cancel)
    Button btnCancel;

    public ContactUsDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getWindow() != null) {
            this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        setContentView(R.layout.dialog_contact_us);
        Font.apply(getContext(), findViewById(android.R.id.content));
        ButterKnife.bind(this);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        setCancelable(false);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etContactUs.getText().toString().trim().length()<10){
                    etContactUs.setError(getContext().getResources().getString(R.string.minLenth));
                }
                else {
                    DatabaseReference databaseReference =FirebaseDatabase.getInstance().getReference("contact_us");
                    Map<String, String> data =new HashMap<>();
                    data.put("notes", etContactUs.getText().toString().trim());
                    data.put("user_email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    databaseReference.setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getContext(), R.string.thanks, Toast.LENGTH_SHORT).show();
                                ContactUsDialog.this.dismiss();
                            }
                        }
                    });
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactUsDialog.this.dismiss();
            }
        });
    }
}