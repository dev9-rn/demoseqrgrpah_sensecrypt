package com.sssl.seqrgraphdemo.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

public class UiText {

    private static boolean isStringResource = true;

    private int id;
    private Object[] formatOrgs;

    private String dynamicString = "";


    private UiText(int id, boolean isStringResource, String dynamicString, Object... formatArgs) {
        this.id = id;
        this.isStringResource = isStringResource;
        this.dynamicString = dynamicString;
        this.formatOrgs = formatArgs;
    }


    public static UiText nonTranslatableString(String data) {


        return new UiText(0, false, data, null);

    }

    public static UiText emptyString() {


        return new UiText(0, false, "", null);

    }

    public static UiText stringResource(@StringRes int resId, Object... formatArgs) {

        return new UiText(resId, true, null, formatArgs);
    }


    public String asString(Context context) {

        if (isStringResource) {

            return context.getResources().getString(id, formatOrgs);
        } else {
            return dynamicString;
        }

    }


    @NonNull
    @Override
    public String toString() {


        if (isStringResource) {
            return "resource id : " + id;
        } else {
            return "dynamic string: " + dynamicString;
        }
    }
}
