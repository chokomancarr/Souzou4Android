package com.pk.souzou1;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class EditWaitDialog extends DialogFragment {
    public MainActivity main;
    public ChunkBase c;
    public int id;

    int val = 0;
    float mul = 1;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.wait_editor, null);
        builder.setView(v);
        final TextView tv = (TextView)v.findViewById(R.id.wt_ed_value);
        v.findViewById(R.id.wt_ed_okbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int r = Math.round(val * mul);
                ((ChunkDelay)c).delay = r;
                main.d("" + ((ChunkDelay)c).delay);
                c.Str("待つ(" + ((ChunkDelay)c).delay + "ミリ秒)");
                dismiss();
            }
        });
        v.findViewById(R.id.wt_ed_optionbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditChunkDialog diaa = new EditChunkDialog();
                diaa.main = main;
                diaa.id = c.id;
                diaa.solo = true;
                dismiss();
                diaa.show(main.getSupportFragmentManager(), "EditChunkDialog");
            }
        });
        ((SeekBar)v.findViewById(R.id.wt_ed_slider)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                val = progress*10;
                tv.setText(String.valueOf((val)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        RadioButton rb1 = (RadioButton)v.findViewById(R.id.wt_ed_t01);
        rb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mul = 0.1f;
                    main.d("" + mul);
                    //int r = Math.round(val * mul);
                    //((ChunkDelay)c).delay = r;
                }
            }
        });
        rb1.setChecked(true);
        RadioButton rb2 = (RadioButton)v.findViewById(R.id.wt_ed_t1);
        rb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mul = 1f;
                    main.d("" + mul);
                    //int r = Math.round(val * mul);
                    //((ChunkDelay)c).delay = r;
                }
            }
        });
        RadioButton rb3 = (RadioButton)v.findViewById(R.id.wt_ed_t10);
        rb3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mul = 10f;
                    main.d("" + mul);
                    //int r = Math.round(val * mul);
                    //((ChunkDelay)c).delay = r;
                }
            }
        });

        return builder.create();
    }
}
