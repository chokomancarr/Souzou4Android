package com.pk.souzou1;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class AddDialog extends DialogFragment {
    public int chunkId;
    public int posId;
    public MainActivity main;
    public int incre;
    public boolean canEvent;
    public boolean canChunk;
    int sel = 0;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String[] ss;
        if (!canEvent) {
            ss = new String[] {"ブロック", "始めに戻る", "遅延"};
        }
        else if (!canChunk) {
            ss = new String[] {"動きの指示"};
        }
        else {
            ss = new String[] {"ブロック", "始めに戻る", "遅延", "素子を追加する"};
        }
        builder.setTitle("追加")
            .setIcon(R.drawable.add)
            .setItems(ss, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog2, int which) {
                    sel = which;
                    switch (sel) {
                        case 0:
                            if (canChunk) {
                                ChunkDialog dialog = new ChunkDialog();
                                dialog.main = main;
                                dialog.pos = chunkId + 1; //put below
                                dialog.id = incre + 100;
                                dialog.show(main.getSupportFragmentManager(), "ChunkDialog");
                            } else {
                                EventDialog dialog = new EventDialog();
                                dialog.main = main;
                                dialog.id = chunkId;
                                dialog.pos = posId;
                                dialog.isInput = main.chunks.get(chunkId).type == 2;
                                dialog.show(main.getSupportFragmentManager(), "EventDialog");
                            }
                            break;
                        case 1:
                            main.CreateChunkLoop(chunkId + 1);
                            break;
                        case 2:
                            main.CreateChunkDelay(chunkId + 1);
                            break;
                        case 3:
                            EventDialog dialog = new EventDialog();
                            dialog.main = main;
                            dialog.id = chunkId;
                            dialog.pos = posId;
                            dialog.isInput = main.chunks.get(chunkId).type == 2;
                            dialog.show(main.getSupportFragmentManager(), "EventDialog");
                            break;
                    }
                }
            });
        return builder.create();
    }
}
