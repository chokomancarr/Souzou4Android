package com.pk.souzou1;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class EditChunkDialog extends DialogFragment{
    public ChunkBase c;
    public MainActivity main;
    public int id;
    public boolean solo = false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (!solo) {
            String[] sss = {"実行順序を変更", "上に移動", "下に移動", "削除"};
            builder.setTitle("ブロック設定")
                    .setIcon(R.drawable.spanner)
                    .setItems(sss, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    SerParDialog dia = new SerParDialog();
                                    dia.main = main;
                                    dia.id = id;
                                    dia.show(getFragmentManager(), "SerParDialog");
                                    break;
                                case 3:
                                    DeleteDialog dia2 = new DeleteDialog();
                                    dia2.main = main;
                                    dia2.id = id;
                                    dia2.show(getFragmentManager(), "DeleteDialog");
                                    break;
                            }
                        }
                    });
        }
        else {
            String[] sss = {"上に移動", "下に移動", "削除"};
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
                            }
                        }
                    });
        }
        // Create the AlertDialog object and return it
        return builder.create();
    }
}