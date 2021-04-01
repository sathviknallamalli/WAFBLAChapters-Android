package com.apps.wafbla;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by sathv on 6/1/2018.
 */

public class Update extends Fragment {

    public Update() {

    }

    FirebaseAuth mAuth;
    View view;
    ArrayList<UpdateItem> updates;
    UpdateAdapter adapter;

TextView updateTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create a view of the appropriate xml file and display it
        view = inflater.inflate(R.layout.update, container, false);
        //set the title of the screen
        setHasOptionsMenu(true);

        mAuth = FirebaseAuth.getInstance();
        getActivity().setTitle("Updates");

        updates = new ArrayList<UpdateItem>();
        final ListView updateList = (ListView) view.findViewById(R.id.updateList);

    updateTitle = view.findViewById(R.id.updatetitle);

        DatabaseReference eventref = FirebaseDatabase.getInstance().getReference().child("Updates");

        eventref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){

                    updates.clear();
                    for (DataSnapshot childSnapshot: snapshot.getChildren()) {
                        updates.add(new UpdateItem(childSnapshot.getKey().toString(), childSnapshot.getValue().toString()));
                    }

                    //set adapter
                    adapter = new UpdateAdapter(view.getContext(), R.layout.update_item, updates);
                    updateList.setAdapter(adapter);
                }else{
                    updateTitle.setText("No updates!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;

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


}