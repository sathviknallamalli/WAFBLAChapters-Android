package com.apps.wafbla;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class RatingSheet extends AppCompatActivity {

    WebView webView;

    Intent i;
    String origname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_sheet);
        setTitle("Rating Sheet");
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back);
        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        i = getIntent();

        String name = i.getExtras().getString("name");
        origname = i.getExtras().getString("name");

        if (name.equals("E-business")) {
            name = "E-Business";
        }
        name = name.replace(" (FBLA)", "");
        String withhyphens = name.replace(" ", "-");

        String url = "http://docs.google.com/gview?embedded=true&url=" + "http://www.fbla-pbl.org/media/" + withhyphens + "-FBLA-Rating-Sheet.pdf";

        if (i.getExtras().getString("type").equals("Interview")) {
            url = "http://docs.google.com/gview?embedded=true&url=" + "http://www.fbla-pbl.org/media/" + withhyphens + "-FBLA-Rating-Sheets.pdf";
        }

        if (i.getExtras().getString("name").equals("Sports & Entertainment Management")) {
            url = "http://docs.google.com/gview?embedded=true&url=" + "http://www.fbla-pbl.org/media/" + "Sports-Entertainment-Management" + "-FBLA-Rating-Sheet.pdf";
        }

        if (i.getExtras().getString("name").equals("Computer Game & Simulation Programming")) {
            url = "http://docs.google.com/gview?embedded=true&url=" + "http://www.fbla-pbl.org/media/" + "Computer-Game-and-Simulation-Programming" + "-FBLA-Rating-Sheet.pdf";
        }

        if (i.getExtras().getString("name").equals("Banking & Financial Systems")) {
            url = "http://docs.google.com/gview?embedded=true&url=" + "http://www.fbla-pbl.org/media/" + "Banking-Financial-Systems" + "-FBLA-Rating-Sheet.pdf";
        }
        if (i.getExtras().getString("name").equals("Coding & Programming")) {
            url = "http://docs.google.com/gview?embedded=true&url=" + "http://www.fbla-pbl.org/media/" + "Coding-and-Programming" + "-FBLA-Rating-Sheet.pdf";
        }


        webView = findViewById(R.id.ratingwebview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent newintent = new Intent(RatingSheet.this, Details.class);
        startActivity(newintent);
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            getFragmentManager().popBackStackImmediate();
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
            finish();

           /*
            i=getIntent();
           Intent newintent = new Intent(RatingSheet.this, Details.class);
            newintent.putExtra("name",origname);
            newintent.putExtra("type", i.getExtras().getString("type"));
            newintent.putExtra("category", i.getExtras().getString("category"));
            startActivity(newintent);
            finish();
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);*/
        }
        return super.onOptionsItemSelected(item);
    }
}
