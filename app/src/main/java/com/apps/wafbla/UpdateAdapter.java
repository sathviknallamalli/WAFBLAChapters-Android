package com.apps.wafbla;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;

/**
 * Created by sathv on 6/4/2018.
 */

public class UpdateAdapter extends ArrayAdapter<UpdateItem> {
    Context context;
    ArrayList<UpdateItem> updateList = null;

    //checked books adapter contructor
    public UpdateAdapter(Context context, int resource, ArrayList<UpdateItem> updateList) {
        super(context, resource, updateList);
        this.context = context;
        this.updateList = updateList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UpdateItem singleUpdate = updateList.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.update_item, parent, false);
        }

        //retrieve the fields
        TextView updateTitle = (TextView) convertView.findViewById(R.id.updateTitle);
        TextView updateText = (TextView) convertView.findViewById(R.id.updateText);

        //set the appropriate fields with the appropriate info
        updateTitle.setText(singleUpdate.title);
        updateText.setText(singleUpdate.message);

        return convertView;
    }
    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}
