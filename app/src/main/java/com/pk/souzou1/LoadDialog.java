package com.pk.souzou1;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.io.FileInputStream;
import java.util.ArrayList;

public class LoadDialog extends DialogFragment {
    public MainActivity main;

    private void Read (String s) {
        Log.i("load", "reading: " + s);
        try {
            String[] s1 = s.split("#");
            if (s1.length==0) {
                Log.i("load", "# length wrong");
                return;
            }
            String[] s2 = s1[0].split("/");
            int pos = 0;
            for (String ss : s2) {
                if (ss.isEmpty())
                    continue;
                String[] s3 = ss.split("&");
                int type =  Integer.parseInt(s3[0]);
                switch (type) {
                    case -1:
                        main.CreateChunkLoop(pos++);
                        break;
                    case 1:
                        main.CreateChunk(pos++, false);
                        main.chunks.get(pos-1).RunType(s3[1].equals("0"));
                        break;
                    case 2:
                        main.CreateChunk(pos++, true);
                        main.chunks.get(pos-1).RunType(s3[1].equals("0"));
                        break;
                    case 10:
                        main.CreateChunkDelay(pos++);
                        ((ChunkDelay)main.chunks.get(pos-1)).delay = Integer.parseInt(s3[1]);
                        break;
                    default:
                        Log.i("load", "unknown type: " + type);
                        break;
                }
            }
            s2 = s1[1].split("/");
            int[] pos2 = new int[pos];
            for (String ss : s2) {
                if (ss.isEmpty())
                    continue;
                String[] s3 = ss.split("&");
                int parent = Integer.parseInt(s3[0]);
                int type =  Integer.parseInt(s3[1]);

                Log.i("load", "event+ " + type);
                EventBase e = main.CreateEvent(parent, pos2[parent]++, type);
                switch (type) {
                    case 0:
                        EditRotateDialog.LoadData((EventDo)e, Integer.parseInt(s3[2]), s3[3].equals("1"), s3[4].equals("1"));
                        break;
                    case 2:
                        EditTranslateDialog.LoadData((EventDo)e, Integer.parseInt(s3[2]), Integer.parseInt(s3[3]), s3[4].equals("1"), s3[5].equals("1"), s3[6].equals("1"));
                        break;
                    case 10:
                        EditMicDialog.LoadData((TriggerMic)e, Float.parseFloat(s3[2]), s3[3].equals("1"));
                        break;
                    case 11:
                        EditProxDialog.LoadData((TriggerProx)e, Float.parseFloat(s3[2]), s3[3].equals("1"));
                        break;
                    default:
                        Log.i("load", "unknown type: " + type);
                        break;
                }
            }
        }catch (Exception e) {
            Log.i("load", e.getMessage());
            main.Del(-1);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater i = main.getLayoutInflater();
        View view = i.inflate(R.layout.loader, null);
        builder.setView(view);

        String[] ss = main.fileList();
        ArrayList<String> files2 = new ArrayList<>();
        for (String t : ss) {
            if (t.startsWith("sozodat_")) {
                files2.add(t);
            }
        }

        if (files2.size() > 0) {
            LinearLayout layout = (LinearLayout)view.findViewById(R.id.ld_body);
            Object[] oo = files2.toArray();
            for (int y = oo.length-1; y >= 0; y--) {
                final int yy = y;
                final String nm = ((String)oo[y]).substring(8);
                Log.i("load", "file " + nm);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        getdp(50)
                );

                View v = i.inflate(R.layout.load_item, null);
                ((TextView)v.findViewById(R.id.load_item_text)).setText(nm);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("ldi", String.format("%1$d", yy));
                        try {
                            FileInputStream stream = main.openFileInput("sozodat_" + nm);
                            byte[] bytes = new byte[stream.available()];
                            if (stream.read(bytes) > 0) {
                                Read(new String(bytes));
                                dismiss();
                            }
                        } catch (Exception e) {
                            Log.i("load", "file not found");
                        }
                    }
                });
                v.findViewById(R.id.load_item_delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        main.deleteFile("sozodat_" + nm);
                        dismiss();
                        main.ReLoad();
                    }
                });

                params.setMargins(getdp(10), 0, getdp(5), getdp(4));
                layout.addView(v, 0, params);
            }
        }
        else {
            Log.i("load", "no files to read");
        }

        return builder.create();
    }

    public int getdp (int dp) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp*scale + 0.5f);
    }
}