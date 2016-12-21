package com.pk.souzou1;

import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

public class Runner {
    public MainActivity main;
    SerialStuff serialStuff;

    ChunkBase c;
    int activeChunk;
    int chunkMax;
    int activeIndex;
    int indexMax;
    int delta;
    boolean waiting;
    List<Byte> waitingList = new ArrayList<>();
    int idIncre;
    long startTime;

    public Runner (MainActivity a, SerialStuff s, int d) {
        main = a;
        serialStuff = s;
        delta = d;
        idIncre = 1;
        startTime = System.currentTimeMillis();
    }

    public void Start () {
        activeChunk = 0;
        chunkMax = main.chunks.size()-1;
        activeIndex = 0;
        c = main.chunks.get(0);
        indexMax = c.events.size()-1;
        main.d("start");
        Continue();
    }

    void Continue () {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (main.running) {

                    if (waiting) {
                        main.d2("[t=" + (System.currentTimeMillis() - startTime) + " " + activeChunk + ":" + activeIndex + "] wait " + waitingList.size());
                        byte[] b = serialStuff.getReadBuffer();
                        for (int q = 0, a = b.length; q < a; q++) {
                            if (waitingList.contains(b[q])) {
                                waitingList.remove(b[q]);
                                main.d2("got ack " + (int) b[q]);
                            }
                        }
                        if (waitingList.size() == 0) {
                            waiting = false;
                            serialStuff.readBufferDo = false;
                        }
                        handler.postDelayed(this, delta);
                    } else {
                        main.d2("[t=" + (System.currentTimeMillis() - startTime) + " " + activeChunk + ":" + activeIndex +  " (" + c.type + ")]");
                        //main.d2("type " + c.type);
                        if (c.type == -1) { //start again
                            activeChunk = 0;
                            c = main.chunks.get(activeChunk);
                            activeIndex = 0;
                            indexMax = c.events.size() - 1;
                            handler.postDelayed(this, delta);
                        } else if (c.type == 1) { //action
                            //evaluate current element
                            if (c.runAtOnce) {
                                main.d2("parallel not implemented!");
                            } else {
                                if (activeIndex <= indexMax) {
                                    EventDo e = (EventDo) c.events.get(activeIndex);
                                    byte[] bb = e.GetOutput();
                                    serialStuff.Write(bb, 500);
                                    main.d2("do " + tobinary(bb[0]) + " " + tobinary(bb[1]));
                                    if (e.isWait) {
                                        waitingList.add((byte) (128 + e.type + 1));
                                        waiting = true;
                                        serialStuff.readBufferDo = true;
                                    }
                                    if (activeIndex == indexMax) {
                                        activeChunk++;
                                        if (activeChunk > chunkMax) { //finished
                                            main.d2("finish");
                                            main.running = false;
                                            return;
                                        } else {
                                            c = main.chunks.get(activeChunk);
                                            activeIndex = 0;
                                            indexMax = c.events.size() - 1;
                                        }
                                    } else {
                                        activeIndex++;
                                    }
                                    handler.postDelayed(this, delta);
                                }
                                else {
                                    main.d2("finish");
                                    main.running = false;
                                    return;
                                }
                            }
                        }
                        else if (c.type == 2) { //wait
                            if (c.runAtOnce) {
                                main.d2("parallel not implemented!");
                            } else {
                                if (activeIndex <= indexMax) {
                                    EventBase e = c.events.get(activeIndex);
                                    if (!e.RunState) //may have to change to previous delta to reduce time lag
                                        e.SetRun(true);
                                    if (e.Pass()) {
                                        e.SetRun(false);
                                        if (activeIndex == indexMax) {
                                            activeChunk++;
                                            if (activeChunk > chunkMax) { //finished
                                                main.d2("finish");
                                                main.running = false;
                                                return;
                                            } else {
                                                c = main.chunks.get(activeChunk);
                                                activeIndex = 0;
                                                indexMax = c.events.size() - 1;
                                            }
                                        } else {
                                            activeIndex++;
                                        }
                                    }
                                    handler.postDelayed(this, delta);
                                }
                                else {
                                    main.d2("finish");
                                    main.running = false;
                                }
                            }
                        }
                        else if (c.type == 10) { //delay
                            activeChunk++;
                            if (activeChunk > chunkMax) { //finished
                                main.d2("finish");
                                main.running = false;
                            } else {
                                handler.postDelayed(this, ((ChunkDelay) c).delay);
                                main.d2("waiting " + ((ChunkDelay) c).delay);
                                c = main.chunks.get(activeChunk); //0
                                activeIndex = 0;
                                indexMax = c.events.size() - 1;
                            }
                        } else { //unidentified chunk, skip
                            main.d2("strange type " + c.type);
                            activeChunk++;
                            if (activeChunk > chunkMax) { //finished
                                main.d2("finish");
                                main.running = false;
                            } else {
                                c = main.chunks.get(activeChunk);
                                activeIndex = 0;
                                indexMax = c.events.size() - 1;
                            }
                        }
                    }
                }
                else {
                    main.d2("abort");
                }
            }
        }, delta);

    }

    String tobinary (byte b) {
        String s = "";
        for (int a = 7; a >= 0; a--) {
            s += ((b & (1 << a))==0? "0" : "1");
        }
        return s;
    }
}
