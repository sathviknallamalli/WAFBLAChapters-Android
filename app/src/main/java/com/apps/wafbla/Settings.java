package com.apps.wafbla;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.firebase.client.ServerValue;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import me.fahmisdk6.avatarview.AvatarView;

public class Settings extends AppCompatActivity {

    EditText f, l, position, email;

    private static final int GALLEY_REQUEST = 1;
    ViewSwitcher vs;
    DatabaseReference dr;
    CircleImageView civ;
    FirebaseAuth mAuth;
    FirebaseUser user;
    AvatarView userinitials;

    Button logout;

    String role;

    String chapterid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        setTitle("Edit settings");
        final Drawable upArrow = getResources().getDrawable(R.drawable.exit);
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences spchap = getSharedPreferences("chapterinfo", Context.MODE_PRIVATE);
        chapterid = spchap.getString("chapterID", "tempid");

        SharedPreferences sp = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        role = sp.getString(getString(R.string.role), "role");

        String childid="";
        if(role.equals("Adviser")){
            childid = "Advisers";
        }else if(role.equals("Member")||role.equals("Officer")){
            childid = "Users";
        }

        dr = FirebaseDatabase.getInstance().getReference().
                child("Chapters").child(chapterid).child(childid).child(mAuth.getCurrentUser().getUid());
        f = findViewById(R.id.f);
        l = findViewById(R.id.l);
        position = findViewById(R.id.position);

        f.setText(sp.getString(getString(R.string.fname), "fname"));
        l.setText(sp.getString(getString(R.string.lname), "lname"));
        position.setText(sp.getString(getString(R.string.role), "role"));
        position.setEnabled(false);
        position.setKeyListener(null);

        vs = findViewById(R.id.vsetting);
        userinitials = findViewById(R.id.profpicset);
        civ = findViewById(R.id.circset);

            userinitials.bind(f.getText().toString() + " " + l.getText().toString(), null);


        logout = findViewById(R.id.lo);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Settings.this, LockScreen.class));
                finish();
            }
        });

        email = findViewById(R.id.em);
        email.setText(sp.getString(getString(R.string.email), "email"));
        email.setEnabled(false);
        email.setKeyListener(null);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);

        for (int i = 0; i < menu.size(); i++) {
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            Intent i = new Intent(Settings.this, FBLAHome.class);
            startActivity(i);
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        }

        if(item.getItemId() == R.id.check){
            final String userid = mAuth.getCurrentUser().getUid();


            dr.child("fname").setValue(f.getText().toString());
            dr.child("lname").setValue(l.getText().toString());

            Intent i = new Intent(Settings.this, FBLAHome.class);
            startActivity(i);
            //overridePendingTransition(R.anim.slide_in_out, R.anim.slide_in_up);

        }
        return super.onOptionsItemSelected(item);
    }


}
