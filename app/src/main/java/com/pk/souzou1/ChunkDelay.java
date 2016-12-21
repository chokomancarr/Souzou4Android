package com.pk.souzou1;

import java.util.ArrayList;

public class ChunkDelay extends ChunkBase {
    public int delay = 0;

    public ChunkDelay () {
        events = new ArrayList<>();
        type = 10;
    }

    @Override
    public String Serialize () {
        return type + "&" + delay;
    }
}
