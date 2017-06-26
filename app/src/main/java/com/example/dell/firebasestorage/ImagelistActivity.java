package com.example.dell.firebasestorage;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ImagelistActivity extends AppCompatActivity {
    private DatabaseReference mdatabase;
    private List<ImageUpload> imglist;
    private ListView lv;
    private ImagelistAdapter adapter;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagelist);
        imglist= new ArrayList<>();
        lv= (ListView) findViewById(R.id.listview);
        //show progess dialog while list image loading
        dialog=new ProgressDialog(this);
        dialog.setMessage("Please wait loading list image");
        dialog.show();

        mdatabase= FirebaseDatabase.getInstance().getReference(MainActivity.FB_DATABASE_PATH);
        mdatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dialog.dismiss();
                //fetch image data from firebase database
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    //ImageUpload class required default constructor
                    ImageUpload img=snapshot.getValue(ImageUpload.class);
                    imglist.add(img);
                }

                //init adapter
                adapter=new ImagelistAdapter(ImagelistActivity.this,R.layout.image_item,imglist);
                //set adapter for listview
                lv.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            dialog.dismiss();
            }
        });
    }
}
