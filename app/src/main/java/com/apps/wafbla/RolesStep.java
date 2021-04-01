package com.apps.wafbla;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class RolesStep extends AppCompatActivity {

    TextView membercode;
    TextView advcode;

    Button moveon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roles);


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        LinearLayout regll = findViewById(R.id.rolelayout);
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(regll.getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }


       membercode = findViewById(R.id.membercode);
        advcode = findViewById(R.id.advcode);

        Intent intent = getIntent();
        String chapid = intent.getExtras().getString("chapterid");

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Chapters").child(chapid).child("JoinCodes");
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    membercode.setText("Member code: " + snapshot.child("MemberCode").getValue().toString());
                    advcode.setText("Adviser code: " + snapshot.child("AdviserCode").getValue().toString());
                }else{
                    generateCode();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        moveon = findViewById(R.id.moveon);

        moveon.setVisibility(View.INVISIBLE);
        membercode.setVisibility(View.INVISIBLE);
        advcode.setVisibility(View.INVISIBLE);


        moveon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = getIntent();
                String chapid = intent.getExtras().getString("chapterid");

                SharedPreferences spchap = getSharedPreferences("chapterinfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorchap = spchap.edit();

                editorchap.putString("chapterID",chapid);
                editorchap.apply();

                Intent i = new Intent(getApplicationContext(), LastStep.class);
                i.putExtra("chapterid", chapid);
                startActivity(i);
                overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
                finish();
            }
        });
    }

    public void generateCode(){
        Intent intent = getIntent();
        String chapid = intent.getExtras().getString("chapterid");
        String email = intent.getExtras().getString("adviseremail");

        String mc = getSaltString();
        String ac = getSaltString();


        membercode.setText("Member code: " + mc);
        advcode.setText("Adviser code: " + ac);


        DatabaseReference codes = FirebaseDatabase.getInstance().getReference().child("Chapters").child(chapid);
        codes.child("JoinCodes").child("MemberCode").setValue(mc);
        codes.child("JoinCodes").child("Adviser Code").setValue(ac);

        moveon.setVisibility(View.VISIBLE);
        membercode.setVisibility(View.VISIBLE);
        advcode.setVisibility(View.VISIBLE);

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
