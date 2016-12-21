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

public class EditMicDialog extends DialogFragment {
    public MainActivity main;
    public TriggerMic c;

    public boolean loop = true;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.mic_editor, null);
        builder.setView(v);

        c.StartRun();

        SeekBar seekBar = (SeekBar)v.findViewById(R.id.mic_ed_slider);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                c.threshold = progress*100;
                c.button.setText(progress + "以上の音があったら");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        v.findViewById(R.id.mic_ed_okbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        v.findViewById(R.id.mic_ed_optionbutton).setOnClickListener(new View.OnClickListener() {
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
                            ProgressBar p = (ProgressBar) v.findViewById(R.id.mic_ed_preview);
                            p.setProgress((int) (c.f * 0.01));

                            setTint(p, (c.f > c.threshold) ? "#FF00FF00" : "#FFFFFF00");
                        }
                    });
                    h.postDelayed(this, 100);
                }
            }
        }, 100);

        return builder.create();
    }

    public static void LoadData (TriggerMic e, float thres, boolean inv) {
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
