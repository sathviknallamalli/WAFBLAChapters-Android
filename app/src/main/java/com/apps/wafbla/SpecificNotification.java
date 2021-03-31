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
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;
import me.fahmisdk6.avatarview.AvatarView;

public class SpecificNotification extends AppCompatActivity {

    TextView ntitle, nmessage, ntime;
    String chapterid, role;
    DatabaseReference dr;
    FirebaseAuth mAuth;
    FirebaseUser user;

    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specificnotification);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        setTitle("Notification");
        final Drawable upArrow = getResources().getDrawable(R.drawable.exit);
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences spchap = getSharedPreferences("chapterinfo", Context.MODE_PRIVATE);
        chapterid = spchap.getString("chapterID", "tempid");

        SharedPreferences sp = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        role = sp.getString(getString(R.string.role), "role");

        ntime = findViewById(R.id.ntimestamp);
        ntitle = findViewById(R.id.ntitle);
        nmessage = findViewById(R.id.nmessage);

        String childid="";
        if(role.equals("Adviser")){
            childid = "Advisers";
        }else if(role.equals("Member")||role.equals("Officer")){
            childid = "Users";
        }

        i = getIntent();
        ntitle.setText(i.getExtras().getString("title"));
        ntime.setText(i.getExtras().getString("timestamp"));
        nmessage.setText(i.getExtras().getString("message"));


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == android.R.id.home) {
            getFragmentManager().popBackStackImmediate();
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
            finish();
        }


        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        getFragmentManager().popBackStackImmediate();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        finish();
    }

}
