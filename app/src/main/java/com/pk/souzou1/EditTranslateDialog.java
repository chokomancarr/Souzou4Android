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
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Locale;

public class EditTranslateDialog extends DialogFragment {
    public MainActivity main;
    public EventDo c;

    boolean isSp = false;
    int valX = 0;
    int valY = 0;
    boolean invertX = false;
    boolean invertY = false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.do_editor2, null);
        builder.setView(v);

        isSp = !c.isWait;
        invertX = c.inv;
        valX = c.val;
        invertY = c.inv2;
        valY = c.val2;

        v.findViewById(R.id.do2_ed_optionbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditEventDialog diaa = new EditEventDialog();
                diaa.main = main;
                diaa.id = c.id;
                dismiss();
                diaa.show(main.getSupportFragmentManager(), "EditEventDialog");
            }
        });
        v.findViewById(R.id.do2_ed_okbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        Switch sw = (Switch)v.findViewById(R.id.do2_ed_useSp);
        sw.setChecked(isSp);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isSp = isChecked;
                TextView tv = (TextView)v.findViewById(R.id.do2_ed_descx);
                TextView tv2 = (TextView)v.findViewById(R.id.do2_ed_descy);
                if (!isSp) {
                    tv.setText("移動距離");
                    tv2.setText("移動距離");
                }
                else {
                    tv.setText("移動速度");
                    tv2.setText("移動速度");
                }
            }
        });
        Switch chx = (Switch)v.findViewById(R.id.do2_ed_invertx);
        chx.setChecked(invertX);
        chx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                invertX = isChecked;
            }
        });
        Switch chy = (Switch)v.findViewById(R.id.do2_ed_inverty);
        chy.setChecked(invertY);
        chy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                invertY = isChecked;
            }
        });
        final TextView valVX = (TextView)v.findViewById(R.id.do2_ed_valuex);
        valVX.setText(String.format(Locale.JAPAN, "%.1f", isSp? valX*0.1074f : valX*1074f));
        SeekBar sbx = (SeekBar)v.findViewById(R.id.do2_ed_sliderx);
        sbx.setProgress(valX);
        sbx.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                valX = progress;
                valVX.setText(String.format(Locale.JAPAN, "%.1f", isSp? valX*0.1074f : valX*1074f));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        final TextView valVY = (TextView)v.findViewById(R.id.do2_ed_valuey);
        valVY.setText(String.format(Locale.JAPAN, "%.1f", isSp? valY*0.1074f : valY*1074f));
        SeekBar sby = (SeekBar)v.findViewById(R.id.do2_ed_slidery);
        sby.setProgress(valY);
        sby.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                valY = progress;
                valVY.setText(String.format(Locale.JAPAN, "%.1f", isSp? valY*0.1074f : valY*1074f));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        v.findViewById(R.id.do2_ed_okbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.val = (byte)valX; //val max is 100
                c.inv = invertX;
                c.val2 = (byte)valY; //val max is 100
                c.inv2 = invertY;
                c.isWait = !isSp;
                //c.type = isX+1;
                if (isSp)
                    c.Str("移動（" + String.format(Locale.JAPAN, "%.1f", (invertX? -valX*0.1074f : valX*0.1074f)) + ", " + String.format(Locale.JAPAN, "%.1f", (invertY? -valY*0.1074f : valY*0.1074f)) + "）cm毎秒");
                else
                    c.Str("移動（" + (invertX? -valX : valX) + ", " + (invertY? -valY : valY) + "）cm");
                dismiss();
            }
        });


        return builder.create();
    }

    public static void LoadData (EventDo c, int valX, int valY, boolean invertX, boolean invertY, boolean isWait) {
        c.val = (byte)valX; //val max is 100
        c.inv = invertX;
        c.val2 = (byte)valY; //val max is 100
        c.inv2 = invertY;
        c.isWait = isWait;
        c.use2Vals = true;
        if (!isWait)
            c.Str("移動（" + String.format(Locale.JAPAN, "%.1f", (invertX? -valX*0.1074f : valX*0.1074f)) + ", " + String.format(Locale.JAPAN, "%.1f", (invertY? -valY*0.1074f : valY*0.1074f)) + "）cm毎秒");
        else
            c.Str("移動（" + (invertX? -valX : valX) + ", " + (invertY? -valY : valY) + "）cm");
    }
}
