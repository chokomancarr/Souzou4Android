package com.pk.souzou1;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Locale;

public class EditRotateDialog extends DialogFragment {
    public MainActivity main;
    public EventDo c;

    boolean isSp = false;
    int val = 0;
    boolean invert = false;
    float counter = 0;

    boolean loop = true;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.do_editor, null);
        builder.setView(v);

        isSp = !c.isWait;
        invert = c.inv;
        val = c.val;

        final ImageView robot = (ImageView)v.findViewById(R.id.do_ed_robot);
        v.findViewById(R.id.do_ed_optionbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditEventDialog diaa = new EditEventDialog();
                diaa.main = main;
                diaa.id = c.id;
                dismiss();
                diaa.show(main.getSupportFragmentManager(), "EditEventDialog");
            }
        });
        v.findViewById(R.id.do_ed_okbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.val = (byte)val; //val max is 100
                c.inv = invert;
                c.isWait = !isSp;
                c.type = 0;
                if (isSp)
                    c.Str("回転(" + (invert? "-" : "") + Math.round(val*0.559f) + "°毎秒)");
                else
                c.Str("回転(" + (invert? "-" : "") + Math.round(val*1.8f) + "°)");
                dismiss();
            }
        });
        final TextView valV = (TextView)v.findViewById(R.id.do_ed_value);
        Switch useSp = ((Switch)v.findViewById(R.id.do_ed_useSp));
        useSp.setChecked(isSp);
        useSp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isSp = isChecked;
                TextView tv = (TextView)v.findViewById(R.id.do_ed_desc);
                if (!isSp) {
                    robot.setRotation(val*1.8f*(invert? -1 : 1));
                    counter = val*1.8f*(invert? -1 : 1);
                    tv.setText("回転角度");
                    valV.setText(String.format(Locale.JAPAN, "%.1f", val*1.8f));
                }
                else {
                    valV.setText(String.format(Locale.JAPAN, "%.1f", val*0.559f));
                    tv.setText("回転速度");
                }
            }
        });
        Switch invertSw = ((Switch)v.findViewById(R.id.do_ed_invert));
        invertSw.setChecked(invert);
        invertSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                invert = isChecked;
                if (!isSp) {
                    robot.setRotation(val*1.8f*(invert? -1 : 1));
                }
            }
        });
        SeekBar slider = ((SeekBar)v.findViewById(R.id.do_ed_slider));
        slider.setProgress(val);
        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                val = progress;
                if (!isSp) {
                    robot.setRotation(val*1.8f*(invert? -1 : 1));
                    valV.setText(String.format(Locale.JAPAN, "%.1f", val*1.8f));
                }
                else valV.setText(String.format(Locale.JAPAN, "%.1f", val*0.559f));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                loop = false;
            }
        });


        final Handler h = new Handler();
        final int delay = 30; //milliseconds

        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (loop) {
                    if (isSp) {
                        counter += (invert? -1 : 1) * val * 0.01 * 0.0559f * delay;
                        if (counter > 360f)
                            counter -= 360f;
                        else if (counter < 0)
                            counter += 360f;
                        robot.setRotation(counter);
                    }
                    h.postDelayed(this, delay);
                }
            }
        }, delay);


        return builder.create();
    }

    public static void LoadData (EventDo c, int val, boolean invert, boolean isWait) {
        c.val = (byte)val; //val max is 100
        c.inv = invert;
        c.isWait = !isWait;
        if (!isWait)
            c.Str("回転(" + (invert? "-" : "") + Math.round(val*0.559f) + "°毎秒)");
        else
            c.Str("回転(" + (invert? "-" : "") + Math.round(val*1.8f) + "°)");
    }
}
