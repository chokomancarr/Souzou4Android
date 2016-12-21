package com.pk.souzou1;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class SerParDialog extends DialogFragment {
    boolean isSingle = true;
    public MainActivity main;
    public int id;

    @Override
public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    String[] sss = {"上から", "同時に"};
    builder.setTitle("実行順序")
            .setIcon(R.drawable.clock)
            .setSingleChoiceItems(sss, 0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    isSingle = which == 0;
                }
            })
            .setPositiveButton("適用", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id2) {
                    for (ChunkBase cb : main.chunks) {
                        if (cb.id == id) {
                            cb.RunType(isSingle);
                            break;
                        }
                    }
                }
            });
    // Create the AlertDialog object and return it
    return builder.create();
}
}
