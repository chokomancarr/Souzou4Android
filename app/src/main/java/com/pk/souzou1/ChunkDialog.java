package com.pk.souzou1;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class ChunkDialog extends DialogFragment {
    public MainActivity main;
    public int pos;
    public int id;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String[] ss = {"センサー/入力を待つ", "動きを指定する"};
        builder.setTitle("ブロック種類")
                .setIcon(R.drawable.lightning)
                .setItems(ss, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        main.CreateChunk(pos, which == 0);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}