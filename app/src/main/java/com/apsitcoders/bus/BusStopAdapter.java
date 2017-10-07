package com.apsitcoders.bus;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.apsitcoders.bus.model.BusStop;

import java.util.List;

/**
 * Created by adityathanekar on 07/10/17.
 */

public class BusStopAdapter extends ArrayAdapter<BusStop> {

    private Context context;

    public BusStopAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        TextView textView = rowView.findViewById(android.R.id.text1);
        BusStop busStop = getItem(position);
        textView.setText(busStop.getName());
        Log.d("Aditya", busStop.getName());
        return rowView;
    }
}
