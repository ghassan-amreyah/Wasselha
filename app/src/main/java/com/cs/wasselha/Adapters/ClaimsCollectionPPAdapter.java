package com.cs.wasselha.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cs.wasselha.Claims.Claims;
import com.cs.wasselha.R;
import com.cs.wasselha.model.Claim;

import java.util.ArrayList;

public class ClaimsCollectionPPAdapter extends ArrayAdapter<Claims> {

    private Context context;
    private int cResource;

    public ClaimsCollectionPPAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Claims> objects
                                    )
    {
        super(context, resource, objects);
        this.context = context;
        this.cResource = resource;
        //this.claimsDACollectionPPData = claimsDACollectionPPData;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        LayoutInflater layoutInFlater = LayoutInflater.from(context);
        convertView = layoutInFlater.inflate(cResource, parent, false);

        ImageView claimImg = convertView.findViewById(R.id.imageInClaimsListView1);
        TextView review = convertView.findViewById(R.id.reviewInClaimListView1);
        TextView massage = convertView.findViewById(R.id.messageInClaimListView1);
        TextView date = convertView.findViewById(R.id.dateInClaimListView1);

        TextView from = convertView.findViewById(R.id.fromInClaimListView1);

        Log.d("inAdapterC","inAdapterC");
        claimImg.setImageResource(R.drawable.ic_review_list_view);
        review.setText(getItem(position).getReview());
        massage.setText(getItem(position).getMessage());
        date.setText(getItem(position).getDate());
        from.setText(getItem(position).getSentFrom());
        return convertView;
    }
}
