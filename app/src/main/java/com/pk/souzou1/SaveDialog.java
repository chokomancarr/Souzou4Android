package com.pk.souzou1;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/* format seps &/#
 *
 * (chunks) do1 wait2 delay10 ret-1  runAtOnce 0 1
 * type&runAtOnce
 *
 * (events)
 * chunkid&type&params
 */

public class SaveDialog extends DialogFragment {
    public MainActivity main;
    public boolean showWarning;
    private String nm = "";
    private String allowedNm = "1234567890+-_*?!~@[]{}()&'\"abcdefghijklmnopqrstuvwxyz";

    private String GetData () {
        String dat = "";
        String dat2 = "";
        int pos = 0;
        for (ChunkBase c : main.chunks) {
            dat += c.Serialize() + "/";
            for (EventBase e : c.events) {
                dat2 += pos + "&" + e.Serialize() + "/";
            }
            pos++;
        }
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        dat += "#" + dat2;// + "#" + sdf.format(new Date());

        return dat;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater i = main.getLayoutInflater();
        View view = i.inflate(R.layout.saver, null);
        builder.setView(view);

        final View wrn = view.findViewById(R.id.sv_ed_exist);
        wrn.setVisibility(showWarning? View.VISIBLE : View.INVISIBLE);
        final Button ok = (Button)view.findViewById(R.id.sv_ed_okbutton);
        final Button ow = (Button)view.findViewById(R.id.sv_ed_owbutton);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String[] ss = main.fileList();
                    for (String sss : ss)
                        Log.i("1111", sss);
                    if (!hasS(ss, "sozodat_" + nm)) {
                        FileOutputStream outputStream = main.openFileOutput("sozodat_" + nm, Context.MODE_PRIVATE);
                        String data = GetData();
                        outputStream.write(data.getBytes());
                        outputStream.close();
                        dismiss();
                    }
                    else {
                        ow.setVisibility(View.VISIBLE);
                        wrn.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    Log.i("save", "prob: " + e.getMessage());
                }
            }
        });
        ow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    FileOutputStream outputStream = main.openFileOutput("sozodat_" + nm, Context.MODE_PRIVATE);
                    String data = GetData();
                    outputStream.write(data.getBytes());
                    outputStream.close();
                    dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        ((EditText)view.findViewById(R.id.sv_ed_name)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nm = s.toString();
                if (!nm.isEmpty()) {
                    ok.setClickable(true);
                    ow.setVisibility(View.INVISIBLE);
                    wrn.setVisibility(View.INVISIBLE);
                    for (char c : nm.toCharArray()) {
                        if (!hasC(allowedNm.toCharArray() ,c)) {
                            ok.setClickable(false);
                            return;
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return builder.create();
    }

    private boolean hasC (char[] list, char c) {
        for (char cc : list) {
            if (cc == c)
                return true;
        }
        return false;
    }

    private boolean hasS (String[] list, String c) {
        for (String cc : list) {
            if (cc.equals(c))
                return true;
        }
        return false;
    }
}

