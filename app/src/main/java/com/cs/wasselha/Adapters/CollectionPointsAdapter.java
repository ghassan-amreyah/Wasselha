package com.cs.wasselha.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cs.wasselha.CollectionPointProvider.CollectionPointInformationActivity;
import com.cs.wasselha.CollectionPointProvider.CollectionPoints;
import com.cs.wasselha.R;
import com.cs.wasselha.Transporter.TransporterReservationDetailsActivity;

import java.util.ArrayList;

public class CollectionPointsAdapter extends ArrayAdapter<CollectionPoints> {

    private Context context;
    private int cResource;

    public CollectionPointsAdapter(@NonNull Context context, int resource, @NonNull ArrayList<CollectionPoints> objects)
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

        ImageView imageView = convertView.findViewById(R.id.imageInListViewMainPageCollectionPoints);
        TextView nameCollectionPoint = convertView.findViewById(R.id.nameCollectionPointInListViewMainPageCollectionPoints);
        Button manageButton = convertView.findViewById(R.id.manageBtnInListViewMainPageCollectionPointProvider);


        imageView.setImageResource(getItem(position).getImage());
        nameCollectionPoint.setText(getItem(position).getCollectionPointName());
        manageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CollectionPointInformationActivity.class);
                intent.putExtra("id",getItem(position).getId());
                context.startActivity(intent);
            }
        });

        return convertView;
    }
}
