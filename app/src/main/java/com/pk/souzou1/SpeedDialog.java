package com.pk.souzou1;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

public class SpeedDialog extends DialogFragment {
    public int chunkId;
    public int eventId;
    public MainActivity main;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater i = main.getLayoutInflater();
        View view = i.inflate(R.layout.number_picker, null);
        NumberPicker picker = (NumberPicker)view.findViewById(R.id.numberPicker);
        picker.setMaxValue(10);
        picker.setMinValue(0);
        picker.setValue(128);
        picker.setWrapSelectorWheel(false);
        builder.setTitle("速度調整")
                .setIcon(R.drawable.stopwatch)
                .setView(view)
                .setPositiveButton("適用", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id2) {
                        for (ChunkBase cb : main.chunks) {
                            if (cb.id == chunkId) {
                                //derp
                                break;
                            }
                        }
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
