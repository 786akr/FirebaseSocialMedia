package com.example.firebasesocialmedia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class SociallMediaApp extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private FirebaseAuth mAuth;
    private EditText edtdes;
    private Button btnshareImage;
    private ImageView placeHolder;
    private ListView ListView;
    private Bitmap bitmap;
    private String imageId;
    private ArrayAdapter arrayAdapter;
    private ArrayList arrayList;
    private ArrayList<String> uid;
    private String ImageDownloadLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sociall_media_app);
        mAuth = FirebaseAuth.getInstance();
        edtdes = findViewById(R.id.edtdes);
        btnshareImage = findViewById(R.id.btnshareImage);
        placeHolder = findViewById(R.id.placeHolder);
        ListView = findViewById(R.id.listView);
        ListView.setOnItemClickListener(this);
        arrayList = new ArrayList();
        uid = new ArrayList();
        arrayAdapter= new ArrayAdapter(SociallMediaApp.this,android.R.layout.simple_list_item_1,arrayList);
        ListView.setAdapter(arrayAdapter);
        placeHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });
        btnshareImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        UploadImage();

            }
        });
    }

    private void SelectImage() {
        if (Build.VERSION.SDK_INT < 23) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1000);
        } else {
            if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(SociallMediaApp.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SociallMediaApp.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2000);
            } else {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1000);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2000 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            SelectImage();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == RESULT_OK && data != null) {
            Uri choosenImage = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), choosenImage);
                placeHolder.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.Logout) {
            Logout();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Logout();
    }

    public void Logout() {
        mAuth.signOut();
        finish();
    }

 private void UploadImage(){
    // Get the data from an ImageView as bytes
     if(bitmap!=null) {
         placeHolder.setDrawingCacheEnabled(true);
         placeHolder.buildDrawingCache();
         bitmap = ((BitmapDrawable) placeHolder.getDrawable()).getBitmap();
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
         byte[] data = baos.toByteArray();
         imageId = UUID.randomUUID() + ".PNG";
         UploadTask uploadTask = FirebaseStorage.getInstance().getReference().child("my_image").child(imageId).putBytes(data);
         uploadTask.addOnFailureListener(new OnFailureListener() {
             @Override
             public void onFailure(@NonNull Exception exception) {
                 Toast.makeText(SociallMediaApp.this, exception.toString() + "", Toast.LENGTH_SHORT).show();
             }
         }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
             @Override
             public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                 // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                 // ...
                 Toast.makeText(SociallMediaApp.this, "Upload Process was Successful", Toast.LENGTH_SHORT).show();
                 edtdes.setVisibility(View.VISIBLE);
                 FirebaseDatabase.getInstance().getReference().child("my_Users").addChildEventListener(new ChildEventListener() {
                     @Override
                     public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                          uid.add(dataSnapshot.getKey());
                         String Username= (String) dataSnapshot.child("userame").getValue();
                         arrayList.add(Username);
                         arrayAdapter.notifyDataSetChanged(); }
                     @Override
                     public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                     }
                     @Override
                     public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                     }
                     @Override
                     public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                     }
                     @Override
                     public void onCancelled(@NonNull DatabaseError databaseError) {
                     }
                 });
                 taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                     @Override
                     public void onComplete(@NonNull Task<Uri> task) {
                         ImageDownloadLink = task.getResult().toString();
                     }
                 });
             }
         });
     }else{
         Toast.makeText(SociallMediaApp.this, "Please Select an Image", Toast.LENGTH_SHORT).show();
     }
     }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap<String,String>  dataMap=new HashMap<>();
        dataMap.put("fromwhom",FirebaseAuth.getInstance().getCurrentUser().getDisplayName() );
        dataMap.put("imageIdentifier",imageId);
        dataMap.put("ImageLink",ImageDownloadLink);
        dataMap.put("des",edtdes.getText().toString());
       FirebaseDatabase.getInstance().getReference().child("my_Users").child(uid.get(position)).child("receive post").push().setValue(dataMap);
    }
}