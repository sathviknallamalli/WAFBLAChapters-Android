package com.apps.wafbla;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import androidx.annotation.NonNull;

/**
 * Created by sathv on 6/1/2018.
 */

public class StartMeeting extends Fragment {

    public StartMeeting() {

    }

    View view;

    String chapterid, role;

    Button start, end, show;
    EditText meetingtitle;

    TextView intro;

    String fname,lname;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create a view of the appropriate xml file and display it
        view = inflater.inflate(R.layout.startmeeting, container, false);
        //set the title of the screen
        SharedPreferences spchap = view.getContext().getSharedPreferences("chapterinfo", Context.MODE_PRIVATE);
        chapterid = spchap.getString("chapterID", "tempid");

        setHasOptionsMenu(true);
        SharedPreferences sp = view.getContext().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        role = sp.getString(getString(R.string.role), "role");
        fname = sp.getString(getString(R.string.fname), "fname");
        lname = sp.getString(getString(R.string.lname), "lname");

        getActivity().setTitle("Manage Meetings");

        meetingtitle = view.findViewById(R.id.meetingtitle);

        start = view.findViewById(R.id.startmeeting);
        show = view.findViewById(R.id.showid);
        end = view.findViewById(R.id.endmeeting);

        intro = view.findViewById(R.id.intro);


        final DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("Chapters").child(chapterid)
                .child("Meetings");
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount() != 1){
                    if (dataSnapshot.child("isActive").getValue().toString().equals("true")) {
                       intro.setText(intro.getText().toString() + "\n\nYou have a meeting currently live and active");
                        Toast.makeText(view.getContext(), "You have a meeting currently live and active", Toast.LENGTH_SHORT).show();
                        show.setText("Meeting ID: " + dataSnapshot.child("ID").getValue().toString());

                        meetingtitle.setText(dataSnapshot.child("Title").getValue().toString());
                        meetingtitle.setEnabled(false);
                        end.setEnabled(true);
                        start.setEnabled(false);


                        intro.setVisibility(View.VISIBLE);
                        show.setVisibility(View.VISIBLE);
                        end.setVisibility(View.VISIBLE);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy  hh:mm:ss");
                final String format = simpleDateFormat.format(new Date());

                dr.child("isActive").setValue(false);
                intro.setVisibility(View.INVISIBLE);
                show.setVisibility(View.INVISIBLE);
                end.setVisibility(View.INVISIBLE);
                Toast.makeText(view.getContext(), "Meeting ended", Toast.LENGTH_SHORT).show();
                dr.child("EndTime").setValue(format);

                //final String id, final String st, final String et, final String count, final String mt
                dr.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                        final DatabaseReference pmref = FirebaseDatabase.getInstance().getReference().child("Chapters").child(chapterid)
                                .child("Meetings").child("PastMeetings").push();
                        pmref.child("Title").setValue(dataSnapshot.child("Title").getValue().toString());
                        pmref.child("Attendance").setValue(dataSnapshot.child("Attendance").getValue());
                        pmref.child("StartTime").setValue(dataSnapshot.child("StartTime").getValue().toString());



                        dr.child("Attendance").removeValue();
                        dr.child("ID").removeValue();
                        dr.child("StartTime").removeValue();
                        dr.child("Title").removeValue();
                        dr.child("isActive").removeValue();
                        dr.child("EndTime").removeValue();
                        dr.child("Attendees").removeValue();



                        meetingtitle.setText("");
                        meetingtitle.setEnabled(true);
                        start.setEnabled(true);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!start.isEnabled()) {
                    Toast.makeText(view.getContext(), "You must end current meeting to start a new one", Toast.LENGTH_SHORT).show();
                } else {
                    if (meetingtitle.getText().toString().isEmpty()) {
                        Toast.makeText(view.getContext(), "Enter meeting title", Toast.LENGTH_SHORT).show();
                    } else {

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy  hh:mm:ss");
                        String format = simpleDateFormat.format(new Date());

                        DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("Chapters").child(chapterid);
                        dr.child("Meetings").child("Title").setValue(meetingtitle.getText().toString());
                        dr.child("Meetings").child("isActive").setValue(true);
                        String id = getSaltString();
                        dr.child("Meetings").child("ID").setValue(id);
                        dr.child("Meetings").child("Attendance").setValue(0);
                        dr.child("Meetings").child("StartTime").setValue(format);

                        intro.setVisibility(View.VISIBLE);
                        show.setVisibility(View.VISIBLE);
                        show.setText("Meeting ID: " + id);
                        end.setVisibility(View.VISIBLE);
                        end.setEnabled(true);
                        start.setEnabled(false);
                        meetingtitle.setEnabled(false);

                    }
                }

            }
        });


        return view;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.pastMeetingIcon) {

            /* DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("Users");*/


            SharedPreferences spchap = view.getContext().getSharedPreferences("chapterinfo", Context.MODE_PRIVATE);
            final String chapid = spchap.getString("chapterID", "tempid");

            SharedPreferences sp = view.getContext().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
            final String role = sp.getString(getString(R.string.role), "role");
            DatabaseReference dr;

            if(role.equals("Adviser")){
                dr = FirebaseDatabase.getInstance().getReference().child("Chapters").child(chapid)
                        .child("Advisers");
            }else{
                dr = FirebaseDatabase.getInstance().getReference().child("Chapters").child(chapid)
                        .child("Users");
            }


            Intent i = new Intent(view.getContext(), PastMeetings.class);
            startActivity(i);
            //    finish();
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.meeting, menu);


        for (int i = 0; i < menu.size(); i++) {
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getView() == null) {
            return;
        }

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

                    System.exit(0);
                    return true;
                }
                return false;
            }
        });
    }

    protected String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 6) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }



}