package com.apps.wafbla;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sathv on 6/4/2018.
 */

public class PastMeetingAdapter extends ArrayAdapter<PastMeetingItem> {
    Context context;
    ArrayList<PastMeetingItem> meetingList = null;

    //checked books adapter contructor
    public PastMeetingAdapter(Context context, int resource, ArrayList<PastMeetingItem> meetingList) {
        super(context, resource, meetingList);
        this.context = context;
        this.meetingList = meetingList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PastMeetingItem singleMeeting = meetingList.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.pastmeetingitem, parent, false);
        }

        //retrieve the fields
        TextView name = (TextView) convertView.findViewById(R.id.meetingName);
        TextView timestamp = (TextView) convertView.findViewById(R.id.meetingTimestamp);
        TextView count = (TextView) convertView.findViewById(R.id.meetingCount);


        //set the appropriate fields with the appropriate info
        name.setText("Meeting name: " + singleMeeting.title);
        timestamp.setText("Start time: " + singleMeeting.timstamp);
        count.setText("Attendance: "+ singleMeeting.attendance);

        return convertView;
    }
    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}
