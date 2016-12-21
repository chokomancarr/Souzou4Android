package com.pk.souzou1;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class EditEventDialog extends DialogFragment{
    public MainActivity main;
    public int id;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String[] sss = {"上に移動", "下に移動", "削除"};
        main.d("event " + id + " edit");
        builder.setTitle("ブロック設定")
                .setIcon(R.drawable.spanner)
                .setItems(sss, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 2:
                                DeleteDialog dia2 = new DeleteDialog();
                                dia2.main = main;
                                dia2.id = id;
                                dia2.show(getFragmentManager(), "DeleteDialog");
                                break;
                            default:
                                main.Move(id, which == 0);
                        }
                    }
                })
                .setNegativeButton("取り消し", null);
        // Create the AlertDialog object and return it
        return builder.create();
    }
}