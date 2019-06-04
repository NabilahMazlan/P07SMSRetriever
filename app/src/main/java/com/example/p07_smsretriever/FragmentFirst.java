package com.example.p07_smsretriever;


import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.Toast;
import android.content.Intent;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentFirst extends Fragment {
    TextView tvView;
    Button btnReceive, btnEmail;
    EditText etEnter;
    private String smsBody;

    public FragmentFirst() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        tvView = view.findViewById(R.id.tvFrag1);
        btnReceive = view.findViewById(R.id.buttonReceive);
        btnEmail = view.findViewById(R.id.buttonEmail);
        etEnter = view.findViewById(R.id.etNum);
        String sms = "";


        btnReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String data = etEnter.getText().toString() ;
                Uri uri = Uri.parse("content://sms");
                String[] reqCols = new String[]{"date", "address", "body", "type"};
                String filter = "address LIKE ?";
                String[] filterArgs = {"%"+data+"%"};
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
                        }
                        smsBody += type + ": " + address + "\n at" + date + "\n\"" + body +"\"\n\n";

                    }while (cursor.moveToNext());
                }
                tvView.setText(smsBody);
                etEnter.setText("");
            }
        });

        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"17010304@myrp.edu.sg"});
                i.putExtra(Intent.EXTRA_SUBJECT, "List Of Messages");
                i.putExtra(Intent.EXTRA_TEXT, smsBody);
                i.setType("message/rfc822");
                startActivity(Intent.createChooser(i, "Choose an email client: "));

            }
        });
        if(savedInstanceState != null){
            tvView.setText(savedInstanceState.getString("number"));
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("number", tvView.getText().toString());
    }
}
