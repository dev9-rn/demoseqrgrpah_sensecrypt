package com.sssl.seqrgraphdemo.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.textfield.TextInputEditText;

import com.sssl.seqrgraphdemo.R;


public class PinDialog {


    public void showPinDialog(Activity activity, PinListener pinListener) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);

        ViewGroup subView = (ViewGroup) activity.getLayoutInflater().// inflater view
                inflate(R.layout.password_field, null, false);

        alertDialog.setView(subView);

        alertDialog.setTitle(activity.getResources().getString(R.string.security_pin));
        alertDialog.setMessage(activity.getResources().getString(R.string.enter_pin));
        alertDialog.setIcon(R.drawable.ic_baseline_vpn_key_24);

        TextInputEditText input = subView.findViewById(R.id.et_passocde);


        alertDialog.setPositiveButton(activity.getResources().getString(R.string.proceed),
                (DialogInterface dialog, int which) -> {

                    if (input.length() > 0 && input.getText().toString().trim().length() >= 4) {
                        dialog.dismiss();
                        String pin = input.getText().toString().trim();

                        pinListener.onPinSet(true, pin);
                    } else {

                        pinListener.onPinSet(false, null);
                    }


                });

        alertDialog.setNegativeButton(activity.getResources().getString(R.string.dialog_cancel),
                (DialogInterface dialog, int which) -> dialog.cancel());

        alertDialog.show();
    }
}
