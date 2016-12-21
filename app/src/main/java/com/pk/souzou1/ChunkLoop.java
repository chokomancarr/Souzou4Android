package com.pk.souzou1;

import java.util.ArrayList;

/*
 *TEMPORARY: returns to first tile (because i am lazy)
 */

public class ChunkLoop extends ChunkBase {
    public boolean isStart;
    public int id;
    public long delay;

    public ChunkLoop () {
        events = new ArrayList<>();
        type = -1;
    }

    @Override
    public String Serialize () {
        return type + "";
    }
}
