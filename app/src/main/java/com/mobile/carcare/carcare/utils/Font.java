package com.mobile.carcare.carcare.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class Font {
    public static final String FONT_NAME = "Cairo-Regular.ttf";

    public static void apply(Context context, View view){
        if (view instanceof ViewGroup){
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i=0; i<viewGroup.getChildCount(); i++){
                View childView =viewGroup.getChildAt(i);
               apply(context,childView);
            }
        }
        else if (view instanceof TextView) {
            ((TextView) view).setTypeface(Typeface.createFromAsset(context.getAssets(),FONT_NAME ));
        }else if (view instanceof EditText) {
            ((EditText) view).setTypeface(Typeface.createFromAsset(context.getAssets(), FONT_NAME));
        }else if (view instanceof Button) {
            ((Button) view).setTypeface(Typeface.createFromAsset(context.getAssets(), FONT_NAME));
        }else if (view instanceof RadioButton) {
            ((RadioButton) view).setTypeface(Typeface.createFromAsset(context.getAssets(), FONT_NAME));
        }else if (view instanceof CheckBox) {
            ((CheckBox) view).setTypeface(Typeface.createFromAsset(context.getAssets(), FONT_NAME));
        }
    }
}
