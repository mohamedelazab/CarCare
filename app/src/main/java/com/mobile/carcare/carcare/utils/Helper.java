package com.mobile.carcare.carcare.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;

import java.util.Random;

public class Helper {

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    public static String getFileExtension(Context context, Uri uri){
        ContentResolver contentResolver =context.getContentResolver();
        MimeTypeMap mimeTypeMap =MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public static void enableTouch(Activity activity){
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public static void disableTouch(Activity activity){
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public static boolean isInputMatch(String word, String input){
        int count =0;
        for (int i =0; i<word.length(); i++){
            for (int j=0;j<input.length();j++){
                if (input.charAt(j) == word.charAt(i)){
                    count++;
                }
            }
        }
        return count >= 2;
    }
}