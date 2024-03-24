package com.cs.wasselha.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cs.wasselha.R;
import com.cs.wasselha.interfaces.implementation.LocationDA;
import com.cs.wasselha.interfaces.implementation.ServiceDA;
import com.cs.wasselha.interfaces.implementation.TransporterDA;
import com.cs.wasselha.model.DeliveryServiceDetails;
import com.cs.wasselha.model.DeliveryStatus;
import com.cs.wasselha.model.Location;
import com.cs.wasselha.model.Service;
import com.cs.wasselha.model.Transporter;

import java.io.IOException;
import java.util.ArrayList;

public class DeliveryStatusAdapter extends ArrayAdapter<DeliveryStatus> {

    private Context context;
    private int cResource;

    public DeliveryStatusAdapter(@NonNull Context context, int resource, @NonNull ArrayList<DeliveryStatus> objects)
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

        ImageView imageView = convertView.findViewById(R.id.imageInDeliveryStatusListView);
        TextView actionTimeView = convertView.findViewById(R.id.actionTimeInCustomerDeliveryStatusListView);
        TextView collectionFromInCustomerDeliveryStatusListView =
                convertView.findViewById(R.id.collectionFromInCustomerDeliveryStatusListView);
        TextView handoverTOInCustomerDeliveryStatusListView = convertView.findViewById(R.id.handoverTOInCustomerDeliveryStatusListView);



        imageView.setImageResource(R.drawable.ic_past_reservation);

        actionTimeView.setText(getItem(position).getActionTime().toString());
        collectionFromInCustomerDeliveryStatusListView.setText(getItem(position).getCollectionFrom());
        handoverTOInCustomerDeliveryStatusListView.setText(getItem(position).getHandoverTo());




        // if (getItem(position).getSource_collection_point() != 0)

        //    pass;






        return convertView;
    }
}
