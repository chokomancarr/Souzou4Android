package com.pk.souzou1;

import android.widget.Button;

public abstract class EventBase {
    public int type;
    public String label;
    public MainActivity main;
    public int id;
    public Button button;
    public boolean RunState;

    public void Str (String s) {
        button.setText(s);
    }

    public abstract byte[] GetOutput ();

    public boolean Pass () {
        return false;
    }

    public void SetRun (boolean b) {
        RunState = b;
    }

    public abstract String Serialize ();

    public abstract void clone(EventBase e);
}
