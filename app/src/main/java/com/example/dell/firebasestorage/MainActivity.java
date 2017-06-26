package com.example.dell.firebasestorage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends Activity{
    private StorageReference mstorage;
    private DatabaseReference mdatabase;
    private ImageView Imagev;
    private EditText imagename;
    private Button upload,browse;
    private Uri imgUri;


    public static final String FB_STORAGE_PATH="image/";
    public static final String FB_DATABASE_PATH="image";
    public static final int REQUEST_CODE=1234;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Imagev= (ImageView) findViewById(R.id.Imagev);
        imagename= (EditText) findViewById(R.id.imagename);
        upload= (Button) findViewById(R.id.Uploadbutton);
        browse= (Button) findViewById(R.id.browsebutton);
        mstorage= FirebaseStorage.getInstance().getReference();
        mdatabase= FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH);



    }
    public void btnBrowse_click(View v){

        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Image"),REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imgUri = data.getData();
               try{
                   Bitmap bm= MediaStore.Images.Media.getBitmap(getContentResolver(),imgUri);
                   Imagev.setImageBitmap(bm);
               } catch (FileNotFoundException e) {
                   e.printStackTrace();
               } catch (IOException e) {
                   e.printStackTrace();
               }
        }
    }
    public String getImageExt(Uri uri){
        ContentResolver contentresolver=getContentResolver();
        MimeTypeMap mimetypemap=MimeTypeMap.getSingleton();
        return mimetypemap.getExtensionFromMimeType(contentresolver.getType(uri));


    }
    @SuppressWarnings("VisibleForTests")
    public void btnUpload_click(View v){
        if(imgUri!=null){
            final ProgressDialog progressdialog=new ProgressDialog(this);
            progressdialog.setTitle("Uploading Image");
            progressdialog.show();

            //get the storage reference
            StorageReference ref=mstorage.child(FB_STORAGE_PATH + System.currentTimeMillis() + "," +  getImageExt(imgUri));


            //Add file to reference
            ref.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {



                    //dismiss progress dialog when succes
                    progressdialog.dismiss();

                    //display success toast
                    Toast.makeText(getApplicationContext(),"Image Uploaded" ,Toast.LENGTH_LONG).show();
                    ImageUpload imageupload=new ImageUpload(imagename.getText().toString(), taskSnapshot.getDownloadUrl().toString());
                    //save image info into firebase database
                   String uploadid=mdatabase.push().getKey();
                    mdatabase.child(uploadid).setValue(imageupload);


                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {


                            progressdialog.dismiss();

                            //display err toast
                            Toast.makeText(getApplicationContext(),e.getMessage() ,Toast.LENGTH_LONG).show();


                        }
                    })

                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            double progress=(100.0 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                            progressdialog.setMessage("Uploaded "+ (int)progress +"%");
                        }
                    });



        }
        else{
            Toast.makeText(getApplicationContext(), "Please Select image" ,Toast.LENGTH_LONG).show();

        }
    }

    public void btnShowList_click(View v){
        Intent i =new Intent(MainActivity.this,ImagelistActivity.class);
        startActivity(i);

    }
}