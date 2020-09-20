package com.example.worklance;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import java.util.List;
import java.util.zip.Inflater;


public class CustomAdapterforButtonListView extends ArrayAdapter<ServiceRequest> {

    private Context context;
    private TextView itemListText;
    private Button itemButton;
    private List<ServiceRequest> listValues;
    LayoutInflater inflater;
    String Rating="",name="";




    public CustomAdapterforButtonListView(Context context, int resource, List<ServiceRequest> listValues) {
        super(context, resource,listValues);
        this.context = context;
        this.listValues = listValues;
    }

    /**
     * getView method is called for each item of ListView
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
        {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row,parent,false);
        }



        //find view by id
        TextView subject = convertView.findViewById(R.id.SS_subject);
        TextView price = convertView.findViewById(R.id.SS_price);
        final TextView servicemanName = convertView.findViewById(R.id.SS_servicemanName);
        final TextView rating = convertView.findViewById(R.id.SS_rating);
        Button acceptBtn = convertView.findViewById(R.id.SS_acceptBtn);

        //set values in the text viwes
        subject.setText(listValues.get(position).getSubject());
        price.setText(listValues.get(position).getRequestPrice()+ " TK");

        // ami ekn eikhne firestore theke data fetch krbo username and rating.....amr onk tel...dekhe jaw ami ki kri...
        FirebaseFirestore.getInstance().collection("Users").document(listValues.get(position).getSid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    name = documentSnapshot.getString("fullName");
                    Rating = documentSnapshot.getString("rating");
                    servicemanName.setText(name);
                    rating.setText(Rating);
                }

            }
        });     //amio ektu ektu lazy ,tai add on failure listener add korlm na... :)


        final String rid = listValues.get(position).getRid();
        final String sid = listValues.get(position).getSid();
        final String p = listValues.get(position).getRequestPrice();
        //To lazy to implement interface (this is tashfik not me -_- )
        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update_request_info(rid,sid,p);
                Intent listViewButtonintent=new Intent(getContext(),workInProgress.class);
                listViewButtonintent.putExtra("Rid",rid);
                listViewButtonintent.putExtra("Sid",sid);
                listViewButtonintent.putExtra("Price",p);
                getContext().startActivity(listViewButtonintent);


            }
        });

        return convertView;
    }
    private void update_request_info(final String rid, final String sid, final String price)
    {
        // get serviceman info

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(sid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    String serviceManName = documentSnapshot.getString("userName");
                    String fullName = documentSnapshot.getString("fullName");
                    String phone = documentSnapshot.getString("phone");
                    String rating = documentSnapshot.getString("rating");

                    //update using FireStore
                    FirebaseFirestore dbRef = FirebaseFirestore.getInstance();
                    DocumentReference doc = dbRef.collection("Requests").document(rid);
                    doc.update("allocatedServiceMan",serviceManName);
                    doc.update("price",price);
                    doc.update("status","In Progress");

                    //update using Firebase real time database
                    DatabaseReference dbFirebase = FirebaseDatabase.getInstance().getReference("Requests").child(rid);
                    dbFirebase.child("allocatedServiceMan").setValue(serviceManName);
                    dbFirebase.child("price").setValue(price);
                    dbFirebase.child("status").setValue("In Progress");

                    //update data in userNotification table through firestore
                    FirebaseFirestore.getInstance().collection("UserNotification").document(rid).update("sid",sid);
                    FirebaseFirestore.getInstance().collection("UserNotification").document(rid).update("servicemanName",serviceManName);

                    //update data in userNotification table through firebase realtime database
                    FirebaseDatabase.getInstance().getReference("UserNotification").child(rid).child("sid").setValue(sid);
                    FirebaseDatabase.getInstance().getReference("UserNotification").child(rid).child("servicemanName").setValue(serviceManName);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

}
