package com.cs.wasselha.CollectionPointProvider;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.cs.wasselha.Adapters.CollectionPointsAdapter;
import com.cs.wasselha.R;
import com.cs.wasselha.interfaces.implementation.CollectionPointDA;
import com.cs.wasselha.model.CollectionPoint;

import java.io.IOException;
import java.util.ArrayList;

public class HomeCollectionPointProviderFragment extends Fragment {
    private static final String ID_KEY = "id";
    private static final String LOGIN_TYPE_KEY = "loginType";
    private static final String PREFERENCES_NAME = "MyPreferences";
    ListView listView;
    Button btn;
    private ProgressDialog progressDialog;
    private ArrayList<CollectionPoints> collectionPointsData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        try
        {
            View view = inflater.inflate(R.layout.fragment_home_collection_point_provider, container, false);
            listView = view.findViewById(R.id.listViewInMainPageCollectionPointProvider);
            SharedPreferences preferences = getActivity().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
            String id = preferences.getString(ID_KEY, null);
            int collectionPointProviderID = Integer.parseInt(id.trim());
            populateData(collectionPointProviderID);
            collectionPointsData=new ArrayList<>();
            CollectionPointDA cA=new CollectionPointDA();

            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            try {
                ArrayList<CollectionPoint> collectionPoints=cA.getCollectionPsByCPPid(collectionPointProviderID);
                for (int i=0;i<collectionPoints.size();i++) {
                    collectionPointsData.add(new CollectionPoints(collectionPoints.get(i).getId(),R.drawable.collection_point_img, collectionPoints.get(i).getName(), btn));

                }
                CollectionPointsAdapter collectionPointsAdapter = new CollectionPointsAdapter(requireContext(), R.layout.home_page_collection_point_provider_list_view, collectionPointsData);
                listView.setAdapter(collectionPointsAdapter);
            } catch (IOException e) {
                e.printStackTrace();
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                }

            }, 1000);

            return view;
        }
        catch (Exception e)
        {
            Log.e("error:",e.toString());
            View view = inflater.inflate(R.layout.fragment_home_collection_point_provider, container, false);
            return view;
        }
    }

    private void populateServicesData()
    {
        collectionPointsData = new ArrayList<>();


        collectionPointsData.add(new CollectionPoints(R.drawable.collection_point_img, "Ramallah", btn));
        collectionPointsData.add(new CollectionPoints(R.drawable.collection_point_img, "Nablus", btn));
        collectionPointsData.add(new CollectionPoints(R.drawable.collection_point_img, "Birzeit", btn));

    }
    private void populateData(int collectionPointProviderID){


    }
}