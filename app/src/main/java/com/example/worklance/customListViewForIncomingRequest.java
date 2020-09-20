package com.example.worklance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class customListViewForIncomingRequest extends ArrayAdapter<ServiceRequest> {

    private Context context;
    private TextView itemListText;
    private Button itemButton;
    private List<ServiceRequest> listValues;
    LayoutInflater inflater;


    public customListViewForIncomingRequest(Context context, int resource, List<ServiceRequest> listValues) {
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
            convertView = inflater.inflate(R.layout.incoming_request_list_view,parent,false);
        }

        //find view by id
        TextView subject = convertView.findViewById(R.id.incomingRequestSubject);
        TextView description = convertView.findViewById(R.id.incomingRequestDescription);
        TextView dateTime = convertView.findViewById(R.id.incomingRequestDateTime);
        TextView notificationStatus = convertView.findViewById(R.id.incomingRequestNotificationStatus);


        //set values in the list items
        subject.setText(listValues.get(position).getSubject());
        description.setText(listValues.get(position).getDescription());
        dateTime.setText(listValues.get(position).getDateTime());
        if (listValues.get(position).getNotificationStatus().equals("Requested")){
            notificationStatus.setText("TK: "+listValues.get(position).getRequestPrice());
        }
        else{
            notificationStatus.setText(listValues.get(position).getNotificationStatus());
        }

        return convertView;
    }
}
