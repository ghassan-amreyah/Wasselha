package com.cs.wasselha.Customer;

import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.cs.wasselha.Adapters.HistoryCustomerReservationsAdapter;
import com.cs.wasselha.Adapters.NotificationsCustomerAdapter;
import com.cs.wasselha.R;
import com.cs.wasselha.interfaces.implementation.DeliveryServiceDetailsDA;
import com.cs.wasselha.model.DeliveryServiceDetails;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReservationsCustomerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReservationsCustomerFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    ListView reservationsListView;
    private ArrayList<DeliveryServiceDetails> delivaryDetailsReservationsForCustomerData;


    private static final String ID_KEY = "id";
    private static final String LOGIN_TYPE_KEY = "loginType";
    private static final String PREFERENCES_NAME = "MyPreferences";
    private ProgressDialog progressDialog;


    private int customerId;
    private String userType;


    public ReservationsCustomerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReservationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReservationsCustomerFragment newInstance(String param1, String param2) {
        ReservationsCustomerFragment fragment = new ReservationsCustomerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reservations_customer, container, false);

        reservationsListView = view.findViewById(R.id.reservationCustomerHistoryListView);

        getFromSharedPref();
        try
        {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setCancelable(false);
            progressDialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run()
                {
                    progressDialog.dismiss();
                }

            }, 500);

            populateNotificationsData();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        return view;

    }

    void getFromSharedPref() {
        SharedPreferences preferences = getActivity().getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        userType = preferences.getString(LOGIN_TYPE_KEY, null);
        customerId = Integer.parseInt(preferences.getString(ID_KEY, ""));


    }

    private void populateNotificationsData() throws IOException {


        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {

                try {
                    delivaryDetailsReservationsForCustomerData = new DeliveryServiceDetailsDA().getDSDsForACustomer(customerId);
                    delivaryDetailsReservationsForCustomerData.sort(new Comparator<DeliveryServiceDetails>() {
                        @Override
                        public int compare(DeliveryServiceDetails o1, DeliveryServiceDetails o2) {
                            return  o2.getId()-o1.getId();
                        }
                    });

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                HistoryCustomerReservationsAdapter historyCustomerReservationsAdapter =
                        new HistoryCustomerReservationsAdapter(requireContext(), R.layout.reservation_customer_list_view, delivaryDetailsReservationsForCustomerData);
                reservationsListView.setAdapter(historyCustomerReservationsAdapter);
            }
        });

    }

}