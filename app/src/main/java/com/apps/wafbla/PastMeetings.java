package com.apps.wafbla;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import me.fahmisdk6.avatarview.AvatarView;

public class PastMeetings extends AppCompatActivity {


    DatabaseReference dr;
    FirebaseAuth mAuth;
    FirebaseUser user;

    ListView past;
    TextView none;
    String chapterid;

    ArrayList<PastMeetingItem> meetings;
    PastMeetingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pastmeetings);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        setTitle("Past Meetings");
        final Drawable upArrow = getResources().getDrawable(R.drawable.exit);
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        past = findViewById(R.id.pastListView);
        none = findViewById(R.id.noMeeting);
        meetings = new ArrayList<PastMeetingItem>();

        SharedPreferences spchap = getSharedPreferences("chapterinfo", Context.MODE_PRIVATE);
        chapterid = spchap.getString("chapterID", "tempid");


        dr = FirebaseDatabase.getInstance().getReference().
                child("Chapters").child(chapterid).child("Meetings").child("PastMeetings");
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    meetings.clear();
                    for (DataSnapshot childSnapshot: snapshot.getChildren()) {
                        meetings.add(new PastMeetingItem(childSnapshot.child("Title").getValue().toString(),
                                childSnapshot.child("StartTime").getValue().toString(), childSnapshot.child("Attendance").getValue().toString()));

                    }
                    //set adapter
                    adapter = new PastMeetingAdapter(PastMeetings.this, R.layout.pastmeetingitem, meetings);
                    past.setAdapter(adapter);
                }else{
                    none.setVisibility(View.VISIBLE);
                    none.setText("No past meetings for you to view");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            getFragmentManager().popBackStackImmediate();
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


}
