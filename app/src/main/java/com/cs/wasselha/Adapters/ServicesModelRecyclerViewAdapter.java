package com.cs.wasselha.Adapters;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cs.wasselha.R;
import com.cs.wasselha.Customer.ServiceDetailsActivity;
import com.cs.wasselha.Models.ServicesModel;
import com.cs.wasselha.interfaces.implementation.ServiceDA;
import com.cs.wasselha.model.Service;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

public class ServicesModelRecyclerViewAdapter extends RecyclerView.Adapter<ServicesModelRecyclerViewAdapter.MyViewHolder>
{
    Context context;
    ArrayList<ServicesModel> servicesModelList;
    ArrayList<Service> servicesDA;
    private static String apiURL="http://176.119.254.198:8000/wasselha";


    public ServicesModelRecyclerViewAdapter(Context context, ArrayList<ServicesModel> servicesModelList
            ,ArrayList<Service> servicesDA)
    {
        this.context = context;
        this.servicesModelList = servicesModelList;
        this.servicesDA = servicesDA;
    }

    @NonNull
    @Override
    public ServicesModelRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.services_recycler_view_in_customer_main_page, parent,false);
        return new ServicesModelRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServicesModelRecyclerViewAdapter.MyViewHolder holder, int position) {


        holder.transporterName.setText(servicesModelList.get(position).getTransporterName());
        holder.time.setText(servicesModelList.get(position).getTime());
        holder.sourceCity.setText(servicesModelList.get(position).getSourceCity());
        holder.destinationCity.setText(servicesModelList.get(position).getDestinationCity());
        Log.d("SMRVAReview",servicesModelList.get(position).getReview()+"");
        holder.transporterReviewInCustomerRecyclerView12.setText(String.valueOf(servicesModelList.get(position).getReview()));
       /* try {
            HomeCustomerFragment.setImage(new VehiclesDA().getVehicleImageOfTransporter( servicesModelList.get(position).getTransporter()),holder.cardImageView);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Glide.with(context)
                .load(servicesModelList.get(position).getImageUrl())
                .into( holder.cardImageView);
                */

       // holder.cardImageView.setImageResource(servicesModelList.get(position).getImage());

        Log.d("SMAdabter","Hello");
        Button detailsBtn = holder.itemView.findViewById(R.id.detailsBtnInCustomerCardRecyclerView);
        int serviceId =  servicesModelList.get(position).getServiceId();
        Service service ;
        try {
            service = new ServiceDA().getService(serviceId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int transporterId =  servicesModelList.get(position).getTransporterId();

        String transName = servicesModelList.get(position).getTransporterName();
        String vehicleType = servicesModelList.get(position).getVehicleType();
        String imageUrl = apiURL +servicesModelList.get(position).getImageUrl();
        int reviewTrans = servicesModelList.get(position).getReview();
        String srcCity= servicesModelList.get(position).getSourceCity();
        String destCity= servicesModelList.get(position).getDestinationCity();
        setImage(apiURL +servicesModelList.get(position).getImageUrl(), context, holder.cardImageView );
        Log.d("SMAdabter2","review=" + reviewTrans);

        detailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ServiceDetailsActivity.class);

                // Pass any extra data if needed
//                intent.putExtra("key", value);

                intent.putExtra("serviceDet",new Gson().toJson(service));
                intent.putExtra("transporterName",transName);
                intent.putExtra("vehicleType",vehicleType);
                intent.putExtra("imageUrl",imageUrl);
                intent.putExtra("srcCity",srcCity);
                intent.putExtra("destCity",destCity);
                intent.putExtra("reviewT",String.valueOf(reviewTrans));
                intent.putExtra("transporterId",String.valueOf(transporterId));

                // Start the activity
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return servicesModelList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView cardImageView;
        TextView transporterName, time, sourceCity, destinationCity, transporterReviewInCustomerRecyclerView12;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cardImageView = itemView.findViewById(R.id.cardImageView);
            transporterName = itemView.findViewById(R.id.transporterNameInCustomerRecyclerView);
            time = itemView.findViewById(R.id.timeInCustomerRecyclerView);
            sourceCity = itemView.findViewById(R.id.sourceCity);
            destinationCity = itemView.findViewById(R.id.destinationCity);
            transporterReviewInCustomerRecyclerView12
                    = itemView.findViewById(R.id.transporterReviewInCustomerRecyclerView12);

        }
    }

    void setImage(String imageUrl, Context context, ImageView image){
        Glide.with(context)
                .load(imageUrl)
                .into(image);
    }
}
