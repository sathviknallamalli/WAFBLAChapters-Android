package com.apps.wafbla;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

import androidx.appcompat.app.AppCompatActivity;

public class LastStep extends AppCompatActivity {

    ImageView chapterlogo;
    Uri mImageuri = null;
    private static final int GALLEY_REQUEST = 1;
    private ArrayList<Uri> ImageList = new ArrayList<Uri>();
    private ArrayList<String> taglist = new ArrayList<String>();

    private int clickImage;
    String chapid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_step);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        LinearLayout regll = findViewById(R.id.lastlayout);
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(regll.getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }

        Intent intent = getIntent();
        chapid = intent.getExtras().getString("chapterid");


        chapterlogo = findViewById(R.id.chapterlogo);


        Button complete = findViewById(R.id.complete);
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DatabaseReference chap = FirebaseDatabase.getInstance().getReference().child("Chapters")
                        .child(chapid);


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


                Intent intent = new Intent(getApplicationContext(), PreFinish.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
                finish();
            }

        });

    }

    private void SendLink(String url, String tag) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("link", url);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Chapters")
                .child(chapid).child("Images");
        databaseReference.child(tag).setValue(url);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        switch (clickImage) {

            case 1:
                galrequest(requestCode, resultCode, data, chapterlogo);
                break;
        }


    }

    public void imgclick(View v) {
        if (v.getId() == R.id.chapterlogo) {
            clickImage = 1;
        }

        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLEY_REQUEST);
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


}
