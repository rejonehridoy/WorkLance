package com.example.worklance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import androidx.annotation.NonNull;

public class CustomAdapter extends ArrayAdapter<RequestInfo> {

    private Context context;
    private TextView itemListText;
    private Button itemButton;
    private List<RequestInfo> listValues;
    private String UserType;
    LayoutInflater inflater;
    DatabaseReference myRef;
    String phn;


    public CustomAdapter(Context context, int resource, List<RequestInfo> listValues,String userType) {
        super(context, resource,listValues);
        this.context = context;
        this.listValues = listValues;
        UserType = userType;
    }

    /**
     * getView method is called for each item of ListView
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //String currentValue = listValues.get(position);

        if (convertView == null)
        {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_list_view,parent,false);
        }

        //find view by id
        TextView subject = convertView.findViewById(R.id.CLVproblemName);
        TextView description = convertView.findViewById(R.id.CLVproblemDescription);
        TextView serviceman = convertView.findViewById(R.id.CLVservicemanName);
        //TextView contact = convertView.findViewById(R.id.CLVcontactNo);
        TextView date = convertView.findViewById(R.id.CLVdate);
        TextView price = convertView.findViewById(R.id.CLVprice);
        TextView workerType = convertView.findViewById(R.id.CLVworkerType);
        TextView status = convertView.findViewById(R.id.CLVstatus);
        TextView titleUserOrServiceMan = convertView.findViewById(R.id.UserOrServiceManText);

        subject.setText(listValues.get(position).Subject);
        description.setText(listValues.get(position).Description);
        if(UserType.equals("User")){
            if (listValues.get(position).AllocatedServiceMan.isEmpty()){
                serviceman.setText("N/A");
                price.setText("TK : N/A");
                //contact.setText("N/A");
            }
            else{
                serviceman.setText(listValues.get(position).AllocatedServiceMan);
                price.setText("TK :"+listValues.get(position).Price);

                //Toast.makeText(getContext(), "Inside : "+get_contact_of_serviceman(listValues.get(position).AllocatedServiceMan),Toast.LENGTH_LONG).show();
                //contact.setText(get_contact_of_serviceman(listValues.get(position).AllocatedServiceMan));
            }
        }
        else{
            if (listValues.get(position).AllocatedServiceMan.isEmpty()){
                titleUserOrServiceMan.setText("Client Name: ");
                serviceman.setText("N/A");
                price.setText("TK : N/A");
                //contact.setText("N/A");
            }
            else{
                titleUserOrServiceMan.setText("Client Name: ");
                serviceman.setText(listValues.get(position).UserName);
                price.setText("TK :"+listValues.get(position).Price);

                //Toast.makeText(getContext(), "Inside : "+get_contact_of_serviceman(listValues.get(position).AllocatedServiceMan),Toast.LENGTH_LONG).show();
                //contact.setText(get_contact_of_serviceman(listValues.get(position).AllocatedServiceMan));
            }
        }


        date.setText(listValues.get(position).RequestedTime);
        workerType.setText(listValues.get(position).WorkerType);
        status.setText(listValues.get(position).Status);

        return convertView;
    }


}
