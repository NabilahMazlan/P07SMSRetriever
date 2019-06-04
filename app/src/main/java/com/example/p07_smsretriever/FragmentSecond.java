package com.example.p07_smsretriever;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

public class FragmentSecond extends Fragment {

    TextView tvView;
    Button btnReceive;
    EditText etEnter;
    String[] filterArgs;
    String smsBody ="";

    public FragmentSecond() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_second, container, false);
        tvView = view.findViewById(R.id.tvFrag2);
        btnReceive = view.findViewById(R.id.buttonReceive);
        etEnter = view.findViewById(R.id.etWord);

        btnReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String data = etEnter.getText().toString() ;
                String[] splittedData = data.split(" ");

                Uri uri = Uri.parse("content://sms");
                String[] reqCols = new String[]{"date", "address", "body", "type"};
                for(String splitData : splittedData){
                    String filter = "body LIKE ?";

                    String[] filterArgs = {"%"+splitData+"%"};
                    ContentResolver cr = getContext().getContentResolver();
                    Cursor cursor = cr.query(uri, reqCols, filter, filterArgs, null);
                    if(cursor.moveToFirst()){
                        do{
                            long dateInMillis = cursor.getLong(0);
                            String date = (String) DateFormat.format("dd MMM yyyy h:mm:ss aa", dateInMillis);
                            String address = cursor.getString(1);
                            String body = cursor.getString(2);
                            String type = cursor.getString(3);
                            if(type.equalsIgnoreCase("1")){
                                type = "Inbox";
                            }else{
                                type = "Sent";
                                smsBody += type + ": " + address + "\n at" + date + "\n\"" + body +"\"\n\n";

                            }

                        }while (cursor.moveToNext());
                }

                }
                tvView.setText(smsBody);
                etEnter.setText("");
            }
        });

        if(savedInstanceState != null){
            tvView.setText(savedInstanceState.getString("words"));
        }
        return view;
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("words", tvView.getText().toString());
    }

}
