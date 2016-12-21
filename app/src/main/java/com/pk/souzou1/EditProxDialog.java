package com.pk.souzou1;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;

public class EditProxDialog extends DialogFragment {
    public MainActivity main;
    public TriggerProx c;

    public boolean loop = true;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.prox_editor, null);
        builder.setView(v);

        c.StartRun();

        SeekBar seekBar = (SeekBar)v.findViewById(R.id.prox_ed_slider);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                c.SetThres (progress*0.01f);
                c.button.setText("画面から" + (c.invert? ">" : "<") + progress + "mmにものがあったら");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ((Switch)v.findViewById(R.id.prox_ed_invert)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                c.invert = isChecked;
            }
        });

        v.findViewById(R.id.prox_ed_okbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        v.findViewById(R.id.prox_ed_optionbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditEventDialog diaa = new EditEventDialog();
                diaa.main = main;
                diaa.id = c.id;
                dismiss();
                diaa.show(main.getSupportFragmentManager(), "EditEventDialog");
            }
        });

        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (loop) {
                    Handler hh = new Handler(main.getMainLooper());
                    hh.post(new Runnable() {
                        @Override
                        public void run() {
                            ProgressBar p = (ProgressBar) v.findViewById(R.id.prox_ed_preview);
                            p.setProgress(1 + (int) (c.f * 100f / c.max));

                            setTint(p, (c.Pass()) ? "#FF00FF00" : "#FFFFFF00");
                        }
                    });
                    h.postDelayed(this, 100);
                }
            }
        }, 100);

        return builder.create();
    }

    public static void LoadData (TriggerProx e, float thres, boolean inv) {
        e.threshold = thres;
        e.invert = inv;
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        c.StopRun();
        loop = false;
    }

    @TargetApi(21)
    private void setTint (ProgressBar p, String c) {
        if (Build.VERSION.SDK_INT >= 21)
            p.setProgressTintList(ColorStateList.valueOf(Color.parseColor(c)));
    }
}
