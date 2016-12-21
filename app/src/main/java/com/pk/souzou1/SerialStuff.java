package com.pk.souzou1;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class SerialStuff {
    public MainActivity main;
    public UsbDevice device;
    public UsbDeviceConnection connection;
    public UsbEndpoint readEnd;
    public UsbEndpoint writeEnd;
    public boolean connOpen;
    public static boolean granted = false;
    public boolean confirmed = false;
    public static boolean hasDriver = false;
    public static boolean debugging = false;
    public boolean readBufferDo;
    List<Byte> readBuffer;
    private Calendar calender;
    //private boolean ardOuting = false;
    //private String ardOut = "";
    public byte[] getReadBuffer () {
        byte[] b = new byte[readBuffer.size()];
        for (int y = readBuffer.size()-1; y >= 0; y--) {
            /*
            if (ardOuting) {
                if (readBuffer.get(y) == (byte)3) {
                    ardOuting = false;
                    Log.i("Arduino says: ", ardOut);
                    main.d2("Ard: " + ardOut);
                    ardOut = "";
                }
                ardOut += readBuffer.get(y);
            }
            else
            */
            b[y] = readBuffer.get(y);
        }
        readBuffer.clear();
        return b;
    }

    public SerialStuff (MainActivity a) {
        main = a;
        readBuffer = new ArrayList<>();
        final Handler h = new Handler();
        final int delay = 1000; //milliseconds
        calender = Calendar.getInstance();
        timeNow = calender.getTimeInMillis();
        timeLast = timeNow;

        h.postDelayed(new Runnable(){
            public void run() {
                if (!granted && RefreshDriver()) { //has driver
                    //autoing = false;
                    if (!hasDriver) { //first occurrence
                        hasDriver = true;
                        GetPermission();
                    } else if (!confirmed){ //until get permission to reduce load
                        UsbManager manager = (UsbManager) main.getSystemService(Context.USB_SERVICE);
                        AppCompatButton button = (AppCompatButton)main.findViewById(R.id.arduino_status_button);
                        if (manager.hasPermission(device)) {
                            button.setBackground(ContextCompat.getDrawable(main.getApplicationContext(), R.drawable.arduino_ready));
                            OpenConn();
                            Write(new byte[]{(byte)255, (byte)255, (byte)255}, 20);
                            ReadContinuous(100, 200, 200, main.getApplicationContext());
                            confirmed = true;
                        }
                        else {
                            button.setBackground(ContextCompat.getDrawable(main.getApplicationContext(), R.drawable.arduino_alert));
                        }
                    }

                } else {
                    if (hasDriver) {
                        //clean
                        hasDriver = false;
                        confirmed = false;
                        debugging = false;
                        granted = false;
                        AppCompatButton button = (AppCompatButton) main.findViewById(R.id.arduino_status_button);
                        button.setBackground(ContextCompat.getDrawable(main.getApplicationContext(), R.drawable.arduino_none));
                        CloseConn();
                    }
                }
                h.postDelayed(this, delay);
            }
        }, delay);
    }

    boolean isDebugMessage = false;
    String debugMessageBuffer = "";
    String debugMessageBufferBytes = "";
    long timeNow = 0;
    long timeLast = 0;
    public void OnRead (byte[] buffer, int length) {
        if (debugging) {
                //char[] bb = b2c (buffer, length);
                MainActivity.debugString += "{" + b22(buffer, length) + "}";
            TextView v2 = (TextView) main.findViewById(R.id.debug_log);
            v2.setText(MainActivity.debugString);
        } else {
            main.d2("get {" + b22(buffer, length) + "}");
        }
        int a = readBuffer.size();
        for (byte b : buffer) {
            if (isDebugMessage) {
                timeNow = calender.getTimeInMillis();
                if (timeNow - timeLast > 1000) {//timeout for message to prevent error in transfer
                    isDebugMessage = false;
                    if (b == (byte)192) isDebugMessage = true;
                    else {
                        readBuffer.add(a++, b);
                    }
                }
                if (b == (byte)3) {
                    //Log.i("serial", "device says: \"" + debugMessageBuffer + "\"");
                    Log.i("serial", "device says: \"" + debugMessageBufferBytes + "\" (bytes)");
                    debugMessageBuffer = "";
                    debugMessageBufferBytes = "";
                    isDebugMessage = false;
                }
                else if (b != (byte)0){
                    debugMessageBuffer += (char) b;
                    debugMessageBufferBytes += b22(new byte[]{b}, 1) + " ";
                }
            }
            else if (b == (byte)192) isDebugMessage = true;
            else {
                readBuffer.add(a++, b);
            }
        }
    }

    public boolean RefreshDriver () {
        try {
            UsbManager manager = (UsbManager) main.getSystemService(Context.USB_SERVICE);
            HashMap<String, UsbDevice> map = manager.getDeviceList();
            Iterator<UsbDevice> iterator = map.values().iterator();
            if (iterator.hasNext()) {
                while (iterator.hasNext()) {
                    device = iterator.next();
                    int vid = device.getVendorId();
                    int pid = device.getProductId();
                    if ((pid == 67 || pid == 1) && vid == 9025) {
                        return true;
                    }
                }
            }
        }
        catch (Exception e) {
            lg(e.getMessage());
            return false;
        }
        return false;
    }

    public void GetPermission () {
        UsbManager manager = (UsbManager) main.getSystemService(Context.USB_SERVICE);
        PendingIntent intent = PendingIntent.getBroadcast(main, 0, new Intent("com.pk.souzou1.USB_PERMISSION"), 0);
        manager.requestPermission(device, intent);
    }

    public boolean OpenConn () {
        return OpenConn(9600);
    }
    public boolean OpenConn (int baudrate) {
        if (!connOpen) {
            try {
                UsbManager manager = (UsbManager) main.getSystemService(Context.USB_SERVICE);
                connection = manager.openDevice(device);
                if ((connection != null) && (device.getInterfaceCount() != 0)) {
                    UsbInterface mControlInterface = device.getInterface(0);
                    if(connection.claimInterface(mControlInterface, true)) {
                        UsbInterface mDataInterface = device.getInterface(1);
                        if(connection.claimInterface(mDataInterface, true)) {
                            int q = mDataInterface.getEndpointCount();
                            if (q > 1) {
                                for (int aa = 0; aa < q; aa++) {
                                    if (mDataInterface.getEndpoint(aa).getDirection() == UsbConstants.USB_DIR_IN)
                                        readEnd = mDataInterface.getEndpoint(aa);
                                    else
                                        writeEnd = mDataInterface.getEndpoint(aa);
                                }
                            }
                            //set baudrate
                            byte[] msg = new byte[]{(byte)(baudrate & 255), (byte)(baudrate >> 8 & 255), (byte)(baudrate >> 16 & 255), (byte)(baudrate >> 24 & 255), (byte)0, (byte)0, (byte)8};
                            connection.controlTransfer(33, 32, 0, 0, msg, msg.length, 5000);
                            connOpen = true;
                            return true;
                        }
                    }
                }
            }
            catch (Exception e) {
                lg(e.getMessage());
            }
        }
        return false;
    }

    public void CloseConn () {
        if (connOpen) {
            connection.close();
            connOpen = false;
        }
    }

    /*
    public int Read (final byte[] b, final int size, final int timeout) {
        if (connOpen) {
            return connection.bulkTransfer(readEnd, b, size, timeout);
            //new Thread(new Runnable(){
            //    public void run(){
            //    }
            //}).start();
        }
        else return -10;
    */
    public void ReadContinuous (final int size, final int timeout, final int interval, final Context context) {
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (connOpen) {
                    DoReadContinuous(size, timeout, context);
                    h.postDelayed(this, interval);
                }
            }
        }, interval);
    }

    public void DoReadContinuous (final int size, final int timeout, final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final byte[] b = new byte[size];
                final int a = connection.bulkTransfer(readEnd, b, size, timeout);
                if (a > 0) {
                    Log.i("bulk", "data in " + a);
                    new Handler(context.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            OnRead(b, a);
                        }
                    });
                }
            }
        }).start();
    }

    public int Write (char[] c, int timeout) {
        return Write(c2b(c), timeout);
    }

    public int Write (byte[] b, int timeout) {
        if (connOpen) {
            //connection.controlTransfer(UsbConstants.USB_TYPE_VENDOR | UsbConstants.USB_DIR_OUT, )
            synchronized (this) {
                int c = connection.bulkTransfer(writeEnd, b, b.length, timeout);
                main.d2("wrote " + c + " bytes");
                return c;
            }
        }
        else {
            main.d2("no connection!");
            return -1;
        }
    }

    void lg (String s) {
        Log.d("Driver", s);
    }

    char[] b2c (byte[] b, int c) {
        char[] a = new char[c];
        for (int x = 0; x < c; x++) {
            a[x] = (char)b[x];
        }
        return a;
    }

    byte[] c2b (char[] b) {
        byte[] a = new byte[b.length];
        for (int x = b.length - 1; x >= 0; x--) {
            a[x] = (byte)b[x];
        }
        return a;
    }

    String b2s (byte[] b, int c) {
        String s = "";
        for (int x = 0; x < c; x++) {
            s += (char)b[x];
        }
        return s;
    }

    String b22 (byte[] b, int c) {
        String s = "";
        for (int x = 0; x < c; x++) {
            for (int a = 7; a >= 0; a--) {
                s += ((b[x] & (1 << a)) == 0 ? "0" : "1");
            }
            s += " ";
        }
        return s;
    }
}
