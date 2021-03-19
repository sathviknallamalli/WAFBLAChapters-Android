package com.apps.wafbla;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by sathv on 6/1/2018.
 */

public class ChapterSettings extends AppCompatActivity implements View.OnClickListener {

    String chapterid;
    private static final int GALLEY_REQUEST = 1;


    //basic stuff
    ArrayAdapter<CharSequence> adapter2;
    EditText chapid, name, zip;
    Spinner state;
    ImageView logo;
    ImageButton camclick;

    //rolekey stuff
    EditText mk, ak;

    //chapter officers and images
    ImageView chapimg;
    private int clickImage;
    Uri mImageuri = null;
    private ArrayList<Uri> ImageList = new ArrayList<Uri>();
    private ArrayList<String> taglist = new ArrayList<String>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create a view of the appropriate xml file and display it
        setContentView(R.layout.chaptersettings);
        //set the title of the screen
        setTitle("Chapter Settings");

        SharedPreferences spchap = getSharedPreferences("chapterinfo", Context.MODE_PRIVATE);
        chapterid = spchap.getString("chapterID", "tempid");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back);
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        chapid = findViewById(R.id.chapidsett);
        name = findViewById(R.id.chapnamesett);
        zip = findViewById(R.id.chapzipsett);
        logo = findViewById(R.id.chaplogosett);
        camclick = findViewById(R.id.camclicksett);
        camclick.setOnClickListener(this);


        mk = findViewById(R.id.memberset);
        ak = findViewById(R.id.adviserset);

        chapimg = findViewById(R.id.chaplogosett);



        state = findViewById(R.id.chapstatesett);

        adapter2 = ArrayAdapter.createFromResource(this, R.array.states, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        state.setAdapter(adapter2);
        state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                //parent.getItemAtPosition(position)
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });


        DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("Chapters").child(chapterid);
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name.setText(dataSnapshot.child("Setup").child("ChapterName").getValue().toString());
                zip.setText(dataSnapshot.child("Setup").child("Zip").getValue().toString());
                chapid.setText(dataSnapshot.child("Setup").child("ID").getValue().toString());
                state.setSelection(find(dataSnapshot.child("Setup").child("State").getValue().toString()));
                Glide.with(ChapterSettings.this).load(dataSnapshot.child("Images").child("ChapterLogo").getValue().toString()).into(logo);



                mk.setText(dataSnapshot.child("JoinCodes").child("MemberCode").getValue().toString());
                ak.setText(dataSnapshot.child("JoinCodes").child("AdviserCode").getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            Intent i = new Intent(ChapterSettings.this, FBLAHome.class);
            startActivity(i);
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        }

        if (item.getItemId() == R.id.check) {
            //save , , , , images, ,


            if (!state.getSelectedItem().toString().equals("Select your state chapter")) {
                DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("Chapters").child(chapterid);

                dr.child("Setup").child("ChapterName").setValue(name.getText().toString());
                dr.child("Setup").child("State").setValue(state.getSelectedItem().toString());
                dr.child("Setup").child("Zip").setValue(zip.getText().toString());

                final StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child(chapid + "ImageFolder");

                for (int uploads = 0; uploads < ImageList.size(); uploads++) {
                    Uri Image = ImageList.get(uploads);
                    final String tag = taglist.get(uploads);
                    final StorageReference imagename = ImageFolder.child("image/" + Image.getLastPathSegment());

                    final int finalUploads = uploads;
                    imagename.putFile(Image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imagename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String url = String.valueOf(uri);
                                    SendLink(url, tag);

                                    if (finalUploads == (ImageList.size() - 1)) {
                                        ImageList.clear();
                                        taglist.clear();
                                    }
                                }
                            });

                        }
                    });
                }
                Toast.makeText(ChapterSettings.this, "Updated!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ChapterSettings.this, "Choose valid state", Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);

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

    // Function to find the index of an element in a primitive array in Java
    public static int find(String target) {
        String[] states = {"Select your state chapter",
                "Alabama",
                "Alaska",
                "Arizona",
                "Arkansas",
                "California",
                "Colorado",
                "Connecticut",
                "Delaware",
                "District of Columbia",
                "Florida",
                "Georgia",
                "Guam",
                "Hawaii",
                "Idaho",
                "Illinois",
                "Indiana",
                "Iowa",
                "Kansas",
                "Kentucky",
                "Louisiana",
                "Maine",
                "Maryland",
                "Massachusetts",
                "Michigan",
                "Minnesota",
                "Mississippi",
                "Missouri",
                "Montana",
                "Nebraska",
                "Nevada",
                "New Hampshire",
                "New Jersey",
                "New Mexico",
                "New York",
                "North Carolina",
                "North Dakota",
                "Northern Marianas Islands",
                "Ohio",
                "Oklahoma",
                "Oregon",
                "Pennsylvania",
                "Puerto Rico",
                "Rhode Island",
                "South Carolina",
                "South Dakota",
                "Tennessee",
                "Texas",
                "Utah",
                "Vermont",
                "Virginia",
                "Virgin Islands",
                "Washington",
                "West Virginia",
                "Wisconsin",
                "Wyoming"};
        for (int i = 0; i < states.length; i++)
            if (states[i].equals(target)) {
                return i;
            }
        return -1;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        switch (clickImage) {

            case 1:
                galrequest(requestCode, resultCode, data, chapimg);
                break;

        }


    }

    public void galrequest(int requestCode, int resultCode, Intent data, ImageView iv) {
        if (requestCode == GALLEY_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri).setFixAspectRatio(true).setAspectRatio(1, 1).
                    setGuidelines(CropImageView.Guidelines.ON).start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageuri = result.getUri();
                iv.setImageURI(mImageuri);
                ImageList.add(mImageuri);

                taglist.add(iv.getTag().toString());
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    private void SendLink(String url, String tag) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("link", url);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Chapters")
                .child(chapterid).child("Images");
        databaseReference.child(tag).setValue(url);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.camclicksett) {
            clickImage = 1;
        }

        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLEY_REQUEST);
    }
}