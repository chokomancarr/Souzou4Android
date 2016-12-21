package com.pk.souzou1;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class DeleteDialog extends DialogFragment {
    //public String name;
    public MainActivity main;
    public int id;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        main.d("delete? " + id);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("削除しますか?")
                .setIcon(R.drawable.alert)
                .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id2) {
                        main.Del(id);
                    }
                })
                .setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id2) {
                        //nevermind
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
