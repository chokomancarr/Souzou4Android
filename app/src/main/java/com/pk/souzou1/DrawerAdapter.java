package com.pk.souzou1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Arrays;

public class DrawerAdapter extends ArrayAdapter<String> {

    public DrawerAdapter(Context context, int resource, String[] items) {
        super(context, resource, Arrays.asList(items));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.drawer_list_item, null);
        }

        String p = getItem(position);

        if (p != null) {
            TextView textView = (TextView) v.findViewById(R.id.drawer_item_text);
            if (textView != null)
                textView.setText(p);
        }

        return v;
    }

}