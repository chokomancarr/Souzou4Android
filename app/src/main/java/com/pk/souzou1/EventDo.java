package com.pk.souzou1;

public class EventDo extends EventBase {
    public boolean use2Vals = false;
    public byte val, val2;
    public boolean inv, inv2;
    public boolean isWait;

    public EventDo () {
        id = 1;
    }

    public void clone (EventBase e) {
        EventDo t = (EventDo)e;
        use2Vals = t.use2Vals;
        val = t.val;
        val2 = t.val2;
        inv = t.inv;
        inv2 = t.inv2;
        isWait = t.isWait;
    }

    public String Serialize () {
        if (use2Vals) {
            return type + "&" + val + "&" + val2 + "&" + (inv ? "1" : "0") + "&" + (inv2 ? "1" : "0") + "&" + (isWait ? "1" : "0");
        }
        else {
            return type + "&" + val + "&" + (inv ? "1" : "0") + "&" + (isWait ? "1" : "0");
        }
    }

    public byte[] GetOutput () { //10ty_peiI 0val_valv
        byte[] b = new byte[use2Vals? 4 : 2];
        //b[0] = (byte)(128 | ((id & 7) << 4) | (a & (1 | 2 | 4 | 8)));
        b[0] = (byte)(128 | (type & 60) | (inv? 2 : 0) | (isWait? 1 : 0));
        b[1] = (byte)(val & 127);
        if (use2Vals) {
            b[2] =(byte)(128 | ((type-1) & 60) | (inv2? 2 : 0) | (isWait? 1 : 0));
            b[3] = (byte)(val2 & 127);
        }
        return b;
    }
}
