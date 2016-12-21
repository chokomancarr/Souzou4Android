package com.pk.souzou1;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public List<ChunkBase> chunks;
    public int totalCount;
    public int cursorPos;
    public int increment;

    public SerialStuff serialStuff;
    public static String debugString = "";

    public boolean hideFace;
    public boolean faceIsWhite = false;
    public boolean running = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final MainActivity m = this;
        serialStuff = new SerialStuff(this);
        setContentView(R.layout.activity_main);
        increment = 0;
        chunks = new ArrayList<>();
        SetCursor(0);
        final ScrollView sc = (ScrollView) findViewById(R.id.scrollView);
        sc.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                View v = findViewById(R.id.cursor);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, getdp(2));
                params.setMargins(0, getdp(54*cursorPos + 106) - sc.getScrollY(), 0, 0);
                v.setLayoutParams(params);
            }
        });

        final ImageView f = (ImageView)findViewById(R.id.face);
        f.setVisibility(View.INVISIBLE);
        f.setEnabled(false);

        ListView listView = (ListView)findViewById(R.id.left_drawer_list);
        listView.setAdapter(new DrawerAdapter(this, R.layout.drawer_list_item, getResources().getStringArray(R.array.drawer_items)));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                d("Menu " + position);
                DrawerLayout layout = (DrawerLayout)findViewById(R.id.drawer_layout);
                layout.closeDrawer(GravityCompat.START);
                if (position == 0 && chunks.size() > 0 && !running) {
                    running = true;
                    if (!hideFace) {
                        f.setImageResource(faceIsWhite ? R.drawable.face_white2 : R.drawable.face_black2);
                        f.setVisibility(View.VISIBLE);
                        f.setEnabled(true);
                        if (Build.VERSION.SDK_INT >= 21) //double check
                            SetStatusBarCol(faceIsWhite ? "#ffffff" : "#000000");
                        if (Build.VERSION.SDK_INT >= 23) //double check
                            SetNavBarCol(faceIsWhite ? "#ffffff" : "#000000");
                        Blink(f);
                    }
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Runner r = new Runner(m, serialStuff, 30); //33fps
                            r.Start();
                        }
                    }, 1000);
                }
                else if (position == 1 && !running) {
                    DeleteDialog del = new DeleteDialog();
                    del.main = m;
                    del.id = -1;
                    del.show(getSupportFragmentManager(), "DeleteAllDialog");
                }
                else if (position == 2 && chunks.size() > 0) {
                    SaveDialog sd = new SaveDialog();
                    sd.main = m;
                    sd.show(getSupportFragmentManager(), "SaveDialog");
                }
                else if (position == 3) {
                    LoadDialog sd = new LoadDialog();
                    sd.main = m;
                    sd.show(getSupportFragmentManager(), "LoadDialog");
                }
                else if (position == 4) {
                    //pc stuff
                }
                else if (position == 5) {
                    HelpDialog sd = new HelpDialog();
                    sd.main = m;
                    sd.show(getSupportFragmentManager(), "HelpDialog");
                }
            }
        });
        Toolbar button = (Toolbar)findViewById(R.id.my_toolbar);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d("drawer button");
                DrawerLayout layout = (DrawerLayout)findViewById(R.id.drawer_layout);
                layout.openDrawer(GravityCompat.START);
            }
        });

        f.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d("face touch");
                //View decorView = getWindow().getDecorView();
                //int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
                //decorView.setSystemUiVisibility(uiOptions);
                //ActionBar actionBar = getActionBar();
                //if (actionBar != null) actionBar.show();
                f.setVisibility(View.INVISIBLE);
                f.setEnabled(false);
                running = false;
                if (Build.VERSION.SDK_INT >= 21) //double check
                    SetStatusBarCol("#303F9F");
                if (Build.VERSION.SDK_INT >= 23) //double check
                    SetNavBarCol("#303F9F");
            }
        });

        final RelativeLayout rightLayout = (RelativeLayout)findViewById(R.id.right_drawer);
        rightLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        View view = findViewById(R.id.drawer_gear);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d("drawer options");
                DrawerLayout layout = (DrawerLayout)findViewById(R.id.drawer_layout);
                layout.closeDrawer(GravityCompat.START);
                TextView v1 = (TextView)findViewById(R.id.debug_info);
                TextView v2 = (TextView)findViewById(R.id.debug_log);
                EditText v3 = (EditText)findViewById(R.id.debug_field);
                v1.setVisibility(View.INVISIBLE);
                v2.setVisibility(View.INVISIBLE);
                Button b1 = (Button)findViewById(R.id.debug_send);
                TextView b2 = (TextView)findViewById(R.id.debug_clear);
                b1.setVisibility(View.INVISIBLE);
                b2.setVisibility(View.INVISIBLE);
                v3.setEnabled(false);
                //ViewGroup.LayoutParams params = rightLayout.getLayoutParams();
                //params.width = getdp(280);
                //rightLayout.setLayoutParams(params);
                layout.openDrawer(GravityCompat.END);
            }
        });

        Switch sw = (Switch)findViewById(R.id.wfaceSwitch);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                faceIsWhite = isChecked;
            }
        });

        //
        sw = (Switch)findViewById(R.id.hatenaSwitch);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ((TextView)findViewById(R.id.hatenaFace)).setText(isChecked? "(˘ω˘ )ﾄｼﾞﾃ" : "( ˘ω˘)");
                hideFace = isChecked;
                findViewById(R.id.debug).setVisibility(isChecked? View.VISIBLE : View.INVISIBLE);
            }
        });

        Button bb = (Button)findViewById(R.id.debug_clear);
        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                debugString ="";
                TextView v2 = (TextView)findViewById(R.id.debug_log);
                v2.setText("");
            }
        });

        final EditText v3 = (EditText)findViewById(R.id.debug_field);

        Button bbs = (Button)findViewById(R.id.debug_send);
        bbs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serialStuff.Write(v3.getText().toString().toCharArray(), 500);
                v3.setText("");
            }
        });

        DrawerLayout layout = (DrawerLayout)findViewById(R.id.drawer_layout);
        layout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                if (drawerView.getId() == R.id.right_drawer) {
                    EditText v3 = (EditText)findViewById(R.id.debug_field);
                    if (serialStuff.connOpen && v3.isEnabled()) {
                        SerialStuff.debugging = true;
                    }
                }
                running = false;
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                if (drawerView.getId() == R.id.right_drawer) {
                    TextView v1 = (TextView)findViewById(R.id.debug_info);
                    TextView v2 = (TextView)findViewById(R.id.debug_log);
                    v1.setVisibility(View.VISIBLE);
                    v2.setVisibility(View.VISIBLE);
                    Button b1 = (Button)findViewById(R.id.debug_send);
                    TextView b2 = (TextView)findViewById(R.id.debug_clear);
                    b1.setVisibility(View.VISIBLE);
                    b2.setVisibility(View.VISIBLE);
                    v3.setEnabled(true);
                    SerialStuff.debugging = false;
                    //ViewGroup.LayoutParams params = drawerView.getLayoutParams();
                    //params.width = getdp(320);
                    //drawerView.setLayoutParams(params);
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        final SerialStuff serr = serialStuff;
        findViewById(R.id.arduino_status_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serr.hasDriver = false;
                serr.confirmed = false;
            }
        });

        increment = 1;

        //
        findViewById(R.id.debug).setVisibility(View.INVISIBLE);

        HelpDialog sd = new HelpDialog();
        sd.main = m;
        sd.show(getSupportFragmentManager(), "HelpDialog");
    }

    /*
    @Override
    protected void onResume () {
        super.onResume();

        if (serialStuff.device != null) {
            UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
            if (manager.hasPermission(serialStuff.device)) {
                AppCompatButton button = (AppCompatButton)findViewById(R.id.arduino_status_button);
                button.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.arduino_ready));
            }
        }
    }
    */

    public void NavUp (View view) {
        SetCursor(cursorPos-1);
    }

    public void NavDown (View view) {
        SetCursor(cursorPos+1);
    }

    public void Add (View view) {
        AddDialog dialog = new AddDialog();
        cursorChunkPos cp = getCursorChunk(cursorPos);
        cp.l();
        dialog.main = this;
        dialog.incre = increment;
        dialog.chunkId = cp.c;
        dialog.posId = cp.i;
        dialog.canEvent = (cp.c != -1 && (chunks.get(cp.c).type == 1 || chunks.get(cp.c).type == 2));
        dialog.canChunk = (cp.c == -1) || (cp.i2 == 0) || (cp.i == cp.i2);
        dialog.show(getSupportFragmentManager(), "AddDialog");
    }

    public void ShowDebug (View view) {
        d("Menu debug");
        DrawerLayout layout = (DrawerLayout)findViewById(R.id.drawer_layout);
        layout.closeDrawer(GravityCompat.START);
        layout.openDrawer(GravityCompat.END);
    }

    void CreateChunk (int pos, boolean in) {
        Log.i("main", "chunk+");
        LinearLayout view = (LinearLayout)findViewById(R.id.body_view);
        LayoutInflater inflater = getLayoutInflater();
        LinearLayout l = (LinearLayout) inflater.inflate(R.layout.chunk_layout, null);
        l.setId(1000 + increment);
        Button b = (Button)l.getChildAt(0);
        b.setText(in? "待つ" : "行う");
        b.setId(100 + increment);
        //b.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.chunk));
        //b.setTextColor(Color.parseColor("#FFFFFF"));

        //Drawable d = ContextCompat.getDrawable(getApplicationContext(), R.drawable.series);
        //b.setCompoundDrawablesWithIntrinsicBounds( d, null, null, null );

        final ChunkBase w;
        if (in) {
            w = new ChunkWait();
            w.label = "待つ";
        }
        else {
            w = new ChunkDo();
            w.label = "行う";
        }
        w.main = this;
        w.id = 100 + increment;
        w.button = b;
        final MainActivity m = this;
        final int i = 100 + increment++;
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditChunkDialog dialog = new EditChunkDialog();
                dialog.c = w;
                dialog.main = m;
                dialog.id = i;
                d ("button " + i + " pressed (short)");
                dialog.show(getSupportFragmentManager(), "EditChunkDialog");
            }
        });
        chunks.add(pos, w);

        //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
        //        LinearLayout.LayoutParams.WRAP_CONTENT,
        //        getdp(50)
        //);

        //params.setMargins(getdp(10), 0, getdp(5), getdp(4));
        view.addView(l, pos+1);

        totalCount++;
        SetCursor(cursorPos+1);
    }

    void CreateChunkLoop (int pos) {
        Log.i("main", "loop+");
        LinearLayout view = (LinearLayout)findViewById(R.id.body_view);
        Button b = new Button(this);
        b.setText("ゼロから");
        b.setId(100 + increment);
        b.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.loop));
        b.setTextColor(Color.parseColor("#FFFFFF"));

        Drawable d = ContextCompat.getDrawable(getApplicationContext(), R.drawable.eject);
        b.setCompoundDrawablesWithIntrinsicBounds( d, null, null, null );

        final ChunkBase w = new ChunkLoop();
        w.main = this;
        w.id = 100 + increment;
        w.button = b;
        w.isLoop = true;
        final MainActivity m = this;
        final int i = 100 + increment++;
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditChunkDialog dialog = new EditChunkDialog();
                dialog.c = w;
                dialog.main = m;
                dialog.id = i;
                dialog.solo = true;
                d("button " + i + " pressed (short)");
                dialog.show(getSupportFragmentManager(), "EditChunkDialog");
            }
        });
        chunks.add(pos, w);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                getdp(50)
        );

        params.setMargins(getdp(10), 0, getdp(5), getdp(4));
        view.addView(b, pos+1, params);

        totalCount++;
        SetCursor(cursorPos+1);
    }

    void CreateChunkDelay (int pos) {
        Log.i("main", "delay+");
        LinearLayout view = (LinearLayout)findViewById(R.id.body_view);
        Button b = new Button(this);
        b.setText("待つ(0ミリ秒)");
        b.setId(100 + increment);
        b.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.delay));
        b.setTextColor(Color.parseColor("#FFFFFF"));

        Drawable d = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lightningw);
        b.setCompoundDrawablesWithIntrinsicBounds( d, null, null, null );

        final ChunkBase w = new ChunkDelay();
        w.main = this;
        w.id = 100 + increment;
        w.button = b;
        final MainActivity m = this;
        final int i = 100 + increment++;
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditWaitDialog dialog = new EditWaitDialog();
                dialog.c = w;
                dialog.main = m;
                dialog.id = i;
                d("button " + i + " pressed (short)");
                dialog.show(getSupportFragmentManager(), "EditWaitDialog");
            }
        });
        chunks.add(pos, w);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                getdp(50)
        );

        params.setMargins(getdp(10), 0, getdp(5), getdp(4));
        view.addView(b, pos+1, params);

        totalCount++;
        SetCursor(cursorPos+1);
    }

    public EventBase CreateEvent (int c, int pos, int type) {
        LinearLayout view = (LinearLayout)findViewById(chunks.get(c).id+900);
        Button b = new Button(this);
        final EventBase w;
        switch (type) {
            case 0:
                b.setText("回転（0°）");
                w = new EventDo();
                break;
            case 2:
                b.setText("移動（0, 0）cm毎秒");
                w = new EventDo();
                ((EventDo)w).use2Vals = true;
                break;

            case 10:
                b.setText("0以上の音があったら");
                w = new TriggerMic();
                break;
            case 11:
                b.setText("画面から0mmにものがあったら");
                w = new TriggerProx(this);
                break;
            default:
                d("unknown id");
                return null;
        }
        b.setId(500 + increment);
        b.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.event));
        b.setTextColor(Color.parseColor("#EEEEEE"));

        Drawable d = ContextCompat.getDrawable(getApplicationContext(), R.drawable.lightningw);
        b.setCompoundDrawablesWithIntrinsicBounds( d, null, null, null );

        w.main = this;
        w.id = 500 + increment;
        w.button = b;
        w.type = type;
        final MainActivity m = this;
        final int i = 500 + increment++;
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (w.type) {
                    case 0:
                        EditRotateDialog dialog2 = new EditRotateDialog();
                        dialog2.c = (EventDo) w;
                        dialog2.main = m;
                        d("button " + i + " pressed (short)");
                        dialog2.show(getSupportFragmentManager(), "EditRotDialog");
                        break;
                    case 1:
                    case 2:
                        EditTranslateDialog dialog = new EditTranslateDialog();
                        dialog.c = (EventDo) w;
                        dialog.main = m;
                        d("button " + i + " pressed (short)");
                        dialog.show(getSupportFragmentManager(), "EditTrDialog");
                        break;
                    case 10:
                        EditMicDialog micDialog = new EditMicDialog();
                        micDialog.main = m;
                        micDialog.c = (TriggerMic)w;
                        micDialog.show(getSupportFragmentManager(), "EditMicDialog");
                        break;
                    case 11:
                        EditProxDialog proxDialog = new EditProxDialog();
                        proxDialog.main = m;
                        proxDialog.c = (TriggerProx) w;
                        proxDialog.show(getSupportFragmentManager(), "EditProxDialog");
                        break;
                }
            }
        });
        chunks.get(c).AddEvent(pos, w);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                getdp(50)
        );

        params.setMargins(getdp(30), 0, getdp(5), getdp(4));
        view.addView(b, pos+1, params);

        totalCount++;
        SetCursor(cursorPos+1);
        return w;
    }

    public void Move(int a, boolean up) {
        if (a < 500) { //chunk
            ChunkBase c = null;
            for (ChunkBase cb : chunks) {
                if (cb.id == a) {
                    c = cb;
                    d2("chunk found");
                    break;
                }
            }
            if (c != null) {

            }
            else
                d2 ("cannot find chunk to move! (" + a + ")");
        }
        else if (a < 1000) { //event
            int i = 0;
            for (ChunkBase cb : chunks) {
                for (int q2 = cb.events.size()-1; q2 >=0; q2--) {
                    if (cb.events.get(q2).id == a) {
                        if (up) {
                            if (q2 > 0) {
                                EventBase ee = CreateEvent(i, q2-1, cb.events.get(q2).type);
                                ee.clone(cb.events.get(q2));
                                DeleteEvent(i, cb.events.get(q2+1));
                            }
                        }
                        return;
                    }
                }
                i++;
            }
            d2 ("cannot find event to move! (" + a + ")");
        }
    }

    public void DeleteChunk(ChunkBase c) {
        if (c.type == -1 || c.type == 10) {
            DeleteSolo(c);
            return;
        }
        LinearLayout view = (LinearLayout) findViewById(R.id.body_view);
        LinearLayout v = (LinearLayout)findViewById(c.id+900);
        if (v != null) {
            d2(v.getChildCount() + "");
            v.removeAllViews();
            view.removeView(v);
        }
        else
            d2("no such id");
        chunks.remove(c);
    }

    public void DeleteSolo (ChunkBase c) {
        LinearLayout view = (LinearLayout) findViewById(R.id.body_view);
        view.removeView(c.button);
        chunks.remove(c);
    }

    public void DeleteEvent(int i, EventBase c) {
        LinearLayout view = (LinearLayout) findViewById(chunks.get(i).id + 900);
        Button v = (Button)findViewById(c.id);
        if (v != null) {
            view.removeView(v);
        }
        else
            d2("no such id");
        chunks.get(i).events.remove(c);
    }

    public void Del (int a) {
        d("deleting " + a);
        if (a < 0) {
            for (int x = chunks.size()-1; x >= 0; x--) {
                DeleteChunk(chunks.get(x));
            }
            RefreshCount();
        }
        else if (a < 500) { //chunk
            ChunkBase c = null;
            for (ChunkBase cb : chunks) {
                if (cb.id == a) {
                    c = cb;
                    d2("chunk found");
                    break;
                }
            }
            if (c != null) {
                DeleteChunk(c);
                RefreshCount();
            }
            else
                d2 ("cannot find chunk to delete! (" + a + ")");
        }
        else if (a < 1000) { //event
            EventBase c;
            int i = 0;
            for (ChunkBase cb : chunks) {
                for (EventBase e : cb.events) {
                    if (e.id == a) {
                        c = e;
                        DeleteEvent(i, c);
                        RefreshCount();
                        return;
                    }
                }
                i++;
            }
            d2 ("cannot find event to delete! (" + a + ")");
        }
    }

    public void ReLoad () {
        LoadDialog sd = new LoadDialog();
        sd.main = this;
        sd.show(getSupportFragmentManager(), "LoadDialog");
    }

    public int getdp (int dp) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp*scale + 0.5f);
    }

    public void SetCursor (int pos) {
        pos = Math.min(pos, totalCount);
        pos = Math.max(pos, 0);
        View v = findViewById(R.id.cursor);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, getdp(2));
        params.setMargins(0, getdp(54*pos + 106), 0, 0);
        v.setLayoutParams(params);
        cursorPos = pos;
        //d("cursor " + cursorPos);
    }

    public cursorChunkPos getCursorChunk (int pos) {
        int s = chunks.size(); //2
        if (s == 0 || pos == 0)
            return new cursorChunkPos(-1, 0, 0);
        int a = 0;
        int i = 0;
        int i2 = 0;
        int s2 = chunks.get(0).events.size();
        while (a < pos-1) {
            if (s2 > i2) { //next event
                i2++;
                a++;
            }
            else if (s-1 > i) { //next chunk
                i++;
                a++;
                i2 = 0;
                s2 = chunks.get(i).events.size();
            }
            else return new cursorChunkPos(i, i2, s2);
        }
        return new cursorChunkPos(i, i2, s2);
    }

    class cursorChunkPos {
        public int c;
        public int i;
        public int i2;
        public cursorChunkPos (int a, int b, int b2) {
            c = a;
            i = b;
            i2 = b2;
        }

        public void l () {
            d(c + " " + i + "(" + i2 + ")");
        }
    }

    public void RefreshCount () {
        totalCount = 0;
        for (ChunkBase c : chunks) {
            totalCount += c.events.size() + 1;
        }
        SetCursor(cursorPos); //cursor may be out of bounds
    }


    public void Blink(final ImageView f) {
        if (running) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    f.setImageResource(faceIsWhite? R.drawable.face_white2 : R.drawable.face_black2);
                    Blink2(f);
                }
            }, Math.round(Math.random()*2000 + 500));
        }
    }

    public void Blink2 (final ImageView f) {
        if (running) {
            if (running) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        f.setImageResource(faceIsWhite? R.drawable.face_white1 : R.drawable.face_black1);
                        Blink(f);
                    }
                }, 150);
            }
        }
    }


    public void d (String s) {
        TextView t = (TextView)findViewById(R.id.debug);
        t.setText(s);
    }
    public void d2 (String s) {
        TextView t = (TextView)findViewById(R.id.debug);
        String ss = t.getText().toString();
        t.setText(t.getText() + "\r\n" + s);
        if (getCharCount(ss, '\n') > 20) {
            t.setText(ss.substring(ss.indexOf("\n") + 1));
        }
    }

    public int getCharCount (String s, char c) {
        int y = 0;
        for (char cc : s.toCharArray()) {
            if (cc == c)
                y++;
        }
        return y;
    }

    @TargetApi(21)
    void SetStatusBarCol (String s) {
        if (Build.VERSION.SDK_INT >= 21)
            getWindow().setStatusBarColor(Color.parseColor(s));
    }

    @TargetApi(23)
    void SetNavBarCol (String s) {
        if (Build.VERSION.SDK_INT >= 23)
            getWindow().setNavigationBarColor(Color.parseColor(s));
    }

}


