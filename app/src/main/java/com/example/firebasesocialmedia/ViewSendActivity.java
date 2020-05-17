package com.example.firebasesocialmedia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ViewSendActivity extends AppCompatActivity  implements AdapterView.OnItemClickListener , AdapterView.OnItemLongClickListener {
  private ListView              listView;
  private ArrayAdapter         arrayAdapter;
  private ArrayList<String>    username;
  private FirebaseAuth auth;
  private ImageView sendImage;
  private TextView txtdes;
    private ArrayList<DataSnapshot> dataSnapshotarraylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_view_send);
     listView=findViewById(R.id.listView);
     listView.setOnItemLongClickListener(this);
     sendImage=findViewById(R.id.sendImage);
     txtdes=findViewById(R.id.txtdescription);
     listView.setOnItemClickListener (this);
     username=new ArrayList<>();
     dataSnapshotarraylist=new ArrayList();
     arrayAdapter=new ArrayAdapter(ViewSendActivity.this,android.R.layout.simple_list_item_1,username);
     listView.setAdapter(arrayAdapter);
        FirebaseDatabase.getInstance().getReference().child("my_Users").child(auth.getCurrentUser().getUid()).child("receive post").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                dataSnapshotarraylist.add(dataSnapshot);
                String usernameSendPost=(String)dataSnapshot.child("fromwhom").getValue();
                username.add(usernameSendPost);
               arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
 int i=0;
         for(DataSnapshot snap : dataSnapshotarraylist){
if(snap.getKey().equals(dataSnapshot.getKey())){
    dataSnapshotarraylist.remove(i);
    username.remove(i);
}
i++;
 }

arrayAdapter.notifyDataSetChanged();
         sendImage.setImageResource(R.drawable.place);
         txtdes.setText("");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
 DataSnapshot dataSnapshot= (DataSnapshot) dataSnapshotarraylist.get(position);
 String downloadLink= (String) dataSnapshot.child("ImageLink").getValue();
        Picasso.get().load(downloadLink).into(sendImage);
        txtdes.setText(dataSnapshot.child("des").getValue()+"");
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        new AlertDialog.Builder(this)
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseStorage.getInstance().getReference()
                                .child("my_image")
                                .child((String)dataSnapshotarraylist.
                                        get(position).
                                child("imageIdentifier").getValue()).delete();
                        FirebaseDatabase.getInstance()
                                .getReference().child("my_Users")
                                .child(auth.getCurrentUser().getUid())
                                .child("receive post")
                                .child( dataSnapshotarraylist.get(position).getKey()).removeValue();

                    }
                })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {


                        }
                    })
                // A null listener allows the button to dismiss the dialog and take no further action.

                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        return false;
    }
}
