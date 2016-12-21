package com.pk.souzou1;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class EventDialog extends DialogFragment {
    public MainActivity main;
    public int pos;
    public int id;
    public boolean isInput;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String[] ss;
        if (isInput)
            ss = new String[]{"大/小音量", "画面距離"};
        else
            ss = new String[]{"移動", "回転"};
        builder.setTitle(isInput? "なんか待つ" : "なんかやる")
                .setIcon(R.drawable.lightning)
                .setItems(ss, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        main.CreateEvent(id, pos, isInput? (which+10) : 2-which*2);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}