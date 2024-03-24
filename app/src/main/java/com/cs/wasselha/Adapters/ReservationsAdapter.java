package com.cs.wasselha.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cs.wasselha.Customer.ReservationDetailsActivity;
import com.cs.wasselha.R;
import com.cs.wasselha.Transporter.HomeTransporterFragment;
import com.cs.wasselha.Transporter.Reservations;
import com.cs.wasselha.Transporter.TransporterReservationActivity;
import com.cs.wasselha.Transporter.TransporterReservationDetailsActivity;

import java.util.ArrayList;

public class ReservationsAdapter  extends ArrayAdapter<Reservations> {

    private Context context;
    private int cResource;
    ArrayList<Reservations> reservationsData ;

    public ReservationsAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Reservations> objects)
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
        RelativeLayout reservationlayout = convertView.findViewById(R.id.reservationlayout);
        TextView customerName = convertView.findViewById(R.id.customerNameInTransporterReservationListview);
        TextView packageType = convertView.findViewById(R.id.PackageTypeInTransporterReservationListview);
        TextView sourceCity = convertView.findViewById(R.id.sourceCityInTransporterReservationListView);
        TextView destinationCity = convertView.findViewById(R.id.destinationCityInTransporterReservationListView);
        TextView time = convertView.findViewById(R.id.timeOfReservationsInTransporterReservationListView);

        customerName.setText(getItem(position).getCustomerName());
        packageType.setText(getItem(position).getPackageType());
        sourceCity.setText(getItem(position).getSourceCity());
        destinationCity.setText(getItem(position).getDestinationCity());
        time.setText(getItem(position).getTime());
        reservationlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getContext(), TransporterReservationDetailsActivity.class);
                intent.putExtra("deliveryservicedetails",getItem(position).getId());
                intent.putExtra("customerid",getItem(position).getCustomerId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        return convertView;
    }
}
