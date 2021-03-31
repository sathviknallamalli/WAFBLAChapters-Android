package com.apps.wafbla;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;


/**
 * Created by sathv on 6/1/2018.
 */

public class Notifications extends Fragment {

    public Notifications() {

    }


    ArrayList<Notif> notifs;

    NotifAdapter adapter;

    TextView howtotv;
    ListView listofnotifs;
    View view;


    private NotifAdapter filteredvaluesadapter;

    ArrayList<String> notiftitle = new ArrayList<String>();
    ArrayList<String> notifmessage = new ArrayList<String>();
    ArrayList<String> notiftimestamp = new ArrayList<String>();


    FirebaseAuth mauth;
    String role,chapid;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create a view of the appropriate xml file and display it
        view = inflater.inflate(R.layout.notifications, container, false);
        //set the title of the screen
        getActivity().setTitle("Notifications");
        setHasOptionsMenu(true);

        mauth = FirebaseAuth.getInstance();

        howtotv = view.findViewById(R.id.howottv);
        howtotv.setText("");
        howtotv.setVisibility(View.GONE);

        listofnotifs = (ListView) view.findViewById(R.id.listofnotifs);
        notifs = new ArrayList<Notif>();

        SharedPreferences sp = view.getContext().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        role = sp.getString(getString(R.string.role), "role");

        SharedPreferences spchap = view.getContext().getSharedPreferences("chapterinfo", Context.MODE_PRIVATE);
        chapid = spchap.getString("chapterID", "tempid");

        String child = "";
        if(role.equals("Adviser")){
            child="Advisers";

        }else if(role.equals("Member")){
            child="Users";
        }



        DatabaseReference eventref = FirebaseDatabase.getInstance().getReference().child("Chapters")
                .child(chapid).child(child).child(mauth.getCurrentUser().getUid()).child("Notifications");

        eventref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    //if an adviser and has notification, leave them instructions
                    if(role.equals("Adviser")){
                        howtotv.setText("To send a message to the whole chapter, click the send icon in the top bar!");
                        howtotv.setVisibility(View.VISIBLE);
                    }
                    //if member with notifications, show nothing
                    else{
                        howtotv.setText("");
                        howtotv.setVisibility(View.GONE);
                    }
                    notiftimestamp.clear();
                    notiftitle.clear();
                    notifmessage.clear();

                    for (DataSnapshot childSnapshot: snapshot.getChildren()) {
                        notiftitle.add(childSnapshot.child("Title").getValue().toString());
                        notifmessage.add(childSnapshot.child("Message").getValue().toString());
                        notiftimestamp.add(childSnapshot.child("Timestamp").getValue().toString());
                    }

                    for (int i = 0; i < notiftitle.size(); i++) {

                        //add checked book to arraylist
                        notifs.add(new Notif(notiftitle.get(i), notifmessage.get(i), notiftimestamp.get(i)));

                    }
                    //set adapter
                    adapter = new NotifAdapter(view.getContext(), R.layout.event_item, notifs);
                    listofnotifs.setAdapter(adapter);
                }else{
                    //no notifications in the child - if a member, say nothing to view
                    if(role.equals("Member")){
                        howtotv.setText("No notifications to view for you!");
                        howtotv.setVisibility(View.VISIBLE);
                    }
                    //if an adviser, leave them instructions to send message
                    else{
                        howtotv.setText("To send a message to the whole chapter, click the send icon in the top bar!");
                        howtotv.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        listofnotifs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View listview, int position, long val) {

                int i = position;

                //based on what book is

                Intent appInfo = new Intent(view.getContext(), SpecificNotification.class);
                appInfo.putExtra("title", notiftitle.get(i));
                appInfo.putExtra("timestamp", notiftimestamp.get(i));
                appInfo.putExtra("message", notifmessage.get(i));
                startActivity(appInfo);

                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.addToBackStack(Notifications.class.getName()).commit();
                fm.executePendingTransactions();

            }
        });

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == R.id.item_send) {
            Intent newintent = new Intent(view.getContext(), ANote.class);
            newintent.putExtra("notename", "sendNotif");
            startActivity(newintent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.notifmenu, menu);

        if(!role.equals("Member")){
            //if user is an offer or adviser -> check

            for (int i = 0; i < menu.size(); i++) {
                Drawable drawable = menu.getItem(i).getIcon();
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);

            }

        }else{
            //only a member, so hide the send button
            for (int i = 0; i < menu.size(); i++) {
                Drawable drawable = menu.getItem(i).getIcon();
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                if(menu.getItem(i).getTitle().toString().equals("Send")){
                    menu.getItem(i).setVisible(false);
                }
            }
        }

        resetSearch();
        //inflater.inflate(R.menu.activities, menu);
        MenuItem searchItem = menu.findItem(R.id.searchevents);
        SearchView searchView = (SearchView) searchItem.getActionView();

        //the listener
        SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                //resetSearch();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText == null || newText.trim().isEmpty()) {
                    //if the search bar is empty, load the original listview using the resetsearch method that is defined below
                    resetSearch();
                    return false;
                } else {

                    //filtered values of book based on the search
                    final ArrayList<Notif> filteredValues = new ArrayList<Notif>(notifs);

                    for (int i = 0; i < notifs.size(); i++) {

                        //if the title of each book does not contaain the string from the search bar, then delete it from the listview
                        //and remove from the filtered values arraylist
                        if (!(notifs.get(i).getTitle().toLowerCase()).contains(newText.toLowerCase())) {

                            //remove each field
                            filteredValues.remove(notifs.get(i));
                            filteredvaluesadapter = new NotifAdapter(getActivity().getApplicationContext(), R.layout.notif_item, filteredValues);
                        }
                    }

                    listofnotifs.setAdapter(filteredvaluesadapter);

                    return false;
                }
            }
        };

        //set the appropriate listener and hint for searchbar
        searchView.setOnQueryTextListener(listener);
        searchView.setQueryHint("Search notification by title");
    }

    //reset search method used when the search bar is empty and the originnal list view is set with orig arrays
    public void resetSearch() {
        //books two was originally set with orig arrays
        adapter = new NotifAdapter(getActivity().getApplicationContext(), R.layout.notif_item, notifs);
        listofnotifs.setAdapter(adapter);


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


