package com.mobile.carcare.carcare.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.mobile.carcare.carcare.R;
import com.mobile.carcare.carcare.utils.Font;

public class SignUpTypeDialog extends Dialog {

    @NonNull
    public static SignUpTypeDialog getInstance(Context context){
        return new SignUpTypeDialog(context);
    }

    public SignUpTypeDialog setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        return this;
    }

    private View.OnClickListener onClickListener;

    public SignUpTypeDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getWindow() != null) {
            this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        setContentView(R.layout.dialog_sign_up_type);
        Font.apply(getContext(), findViewById(android.R.id.content));
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        setCancelable(false);

        findViewById(R.id.btn_sign_user_dialog).setOnClickListener(onClickListener);

        findViewById(R.id.btn_sign_agency_dialog).setOnClickListener(onClickListener);

        findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUpTypeDialog.this.dismiss();
            }
        });
        //Fonts.apply(getContext(), findViewById(android.R.id.content));
    }
}