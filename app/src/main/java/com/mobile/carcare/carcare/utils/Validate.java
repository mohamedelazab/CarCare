package com.mobile.carcare.carcare.utils;

import android.text.TextUtils;
import android.util.Patterns;

public class Validate {

    public static boolean isValidEmail(String email){
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    public static boolean isNotEmpty(String input){
        return (!TextUtils.isEmpty(input));
    }

    public static boolean passwordMatch(String pass, String passConfirm){
        return (pass.equals(passConfirm) && !pass.equals("") && !passConfirm.equals(""));
    }

    public static int isUsernameValid(String input){
        if (input.trim().contains(" ")){
            return 1;
        }
        else if (input.trim().isEmpty()){
            return 2;
        }
        else {
            return 0;
        }
    }
}