package olyapps.sathv.fbla2020;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by sathv on 6/1/2018.
 */

public class HowItWorks extends Fragment {

    public HowItWorks() {

    }

    WebView webView, compwebview;
View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create a view of the appropriate xml file and display it
        view = inflater.inflate(R.layout.howitworks, container, false);
        //set the title of the screen
        getActivity().setTitle("How it works");
        setHasOptionsMenu(true);

        String flow = "http://docs.google.com/gview?embedded=true&url=http://www.fbla-pbl.org/media/FBLA-Choose-Your-Competitive-Events-Flow-Chart.pdf";

        webView = view.findViewById(R.id.choose);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(flow);

        String compevent = "http://docs.google.com/gview?embedded=true&url=http://www.fbla-pbl.org/media/FBLA-Events-At-A-Glance.pdf";

        compwebview = view.findViewById(R.id.compevent);
        compwebview.getSettings().setJavaScriptEnabled(true);
        compwebview.setWebViewClient(new WebViewClient());
        compwebview.loadUrl(compevent);


        return view;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.logout){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getView().getContext(), LockScreen.class));
        } if (item.getItemId() == R.id.item_userprofile) {
            Intent newintent = new Intent(view.getContext(), Profile.class);
            startActivity(newintent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fblahome, menu);

        for(int i = 0; i < menu.size(); i++){
            Drawable drawable = menu.getItem(i).getIcon();
            if(drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(getView() == null){
            return;
        }

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){

                    System.exit(0);
                    return true;
                }
                return false;
            }
        });
    }

}