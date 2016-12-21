package com.pk.souzou1;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.widget.Button;

import java.util.List;

public abstract class ChunkBase {
    public int id;
    public int type;
    public boolean runAtOnce;
    public String label;
    public List<EventBase> events;
    public MainActivity main;
    public Button button;
    public boolean isLoop = false;

    public void RunType (boolean isSingle) {
        runAtOnce = !isSingle;
        //Button b = (Button)main.findViewById(id);
        Drawable d = ContextCompat.getDrawable(main.getApplicationContext(), isSingle? R.drawable.series : R.drawable.parallel);
        button.setCompoundDrawablesWithIntrinsicBounds( d, null, null, null );
    }

    public void Str (String s) {
        //Button b = (Button)main.findViewById(id);
        button.setText(s);
    }

    public void AddEvent (int p, EventBase e) {
        events.add(p, e);
    }

    public String Serialize() {
        return type + "&" + (runAtOnce? "1" : "0");
    }
}
