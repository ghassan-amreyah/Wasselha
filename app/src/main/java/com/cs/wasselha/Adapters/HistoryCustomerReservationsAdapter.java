package com.cs.wasselha.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cs.wasselha.Claims.Claims;
import com.cs.wasselha.Customer.DeliveryStatusActivity;
import com.cs.wasselha.interfaces.implementation.LocationDA;
import com.cs.wasselha.interfaces.implementation.ServiceDA;
import com.cs.wasselha.interfaces.implementation.TransporterDA;
import com.cs.wasselha.model.DeliveryServiceDetails;
import com.cs.wasselha.R;
import com.cs.wasselha.model.Location;
import com.cs.wasselha.model.Service;
import com.cs.wasselha.model.Transporter;

import java.io.IOException;
import java.util.ArrayList;

public class HistoryCustomerReservationsAdapter  extends ArrayAdapter<DeliveryServiceDetails> {

    private Context context;
    private int cResource;

    public HistoryCustomerReservationsAdapter(@NonNull Context context, int resource, @NonNull ArrayList<DeliveryServiceDetails> objects)
    {
        super(context, resource, objects);
        this.context = context;
        this.cResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        LayoutInflater layoutInFlater = LayoutInflater.from(context);
        convertView = layoutInFlater.inflate(cResource, parent, false);

        ImageView imageView = convertView.findViewById(R.id.imageInHistoryReservationsListView);
        TextView referenceNumber = convertView.findViewById(R.id.referenceNumberInReservationCustomerListView);
        TextView transporterName = convertView.findViewById(R.id.transporterNameInReservationCustomerListView);
        TextView sourceInReservationCustomerListView = convertView.findViewById(R.id.sourceInReservationCustomerListView);
        TextView destinationInReservationCustomerListView = convertView.findViewById(R.id.destinationInReservationCustomerListView);
        TextView statusInReservationCustomerListView = convertView.findViewById(R.id.statusInReservationCustomerListView);
        TextView dateInReservationCustomerListView = convertView.findViewById(R.id.dateInReservationCustomerListView);



        imageView.setImageResource(R.drawable.ic_past_reservation);

        int refNoDelDet = getItem(position).getId();
        referenceNumber.setText(String.valueOf(refNoDelDet));

        int serviceId = getItem(position).getService();
        int transId ;
        Transporter transporter ;
        Service service;
        Location srcLocation;
        Location destLocation;
        LocationDA locationDA = new LocationDA();
        try {
            service = new ServiceDA().getService(serviceId);
            transId = service.getTransporter();
            transporter = new TransporterDA().getTransporter(transId);
            srcLocation =  locationDA.getLocation(service.getSource_place());
            destLocation =  locationDA.getLocation(service.getDestination_place());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        transporterName.setText(transporter.getFirst_name());

       // if (getItem(position).getSource_collection_point() != 0)

        //    pass;

        sourceInReservationCustomerListView.setText(srcLocation.getTitle());
        destinationInReservationCustomerListView.setText(destLocation.getTitle());
        String statusAcceptedOrNot = "";
        if (getItem(position).isAccepted()) {
            statusAcceptedOrNot = "Accepted";
        }
        else {
            if (getItem(position).isResponsed())
                statusAcceptedOrNot = "Denied";
            else
                statusAcceptedOrNot = "Under Review";
        }
        statusInReservationCustomerListView.setText(statusAcceptedOrNot);
        dateInReservationCustomerListView.setText(getItem(position).getCollection_time());

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DeliveryStatusActivity.class);
                intent.putExtra("delSerDetId",String.valueOf(refNoDelDet));

                getContext().startActivity(intent);
            }
        });


        return convertView;
    }
}
