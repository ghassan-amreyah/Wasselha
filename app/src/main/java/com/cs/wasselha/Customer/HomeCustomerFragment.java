package com.cs.wasselha.Customer;

import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cs.wasselha.R;
import com.cs.wasselha.Models.ServicesModel;
import com.cs.wasselha.Adapters.ServicesModelRecyclerViewAdapter;
import com.cs.wasselha.interfaces.implementation.CustomerDA;
import com.cs.wasselha.interfaces.implementation.DeliveryServiceDetailsDA;
import com.cs.wasselha.interfaces.implementation.LocationDA;
import com.cs.wasselha.interfaces.implementation.ServiceDA;
import com.cs.wasselha.interfaces.implementation.TransporterDA;
import com.cs.wasselha.interfaces.implementation.VehiclesDA;
import com.cs.wasselha.model.Customer;
import com.cs.wasselha.model.DeliveryServiceDetails;
import com.cs.wasselha.model.Location;
import com.cs.wasselha.model.Service;
import com.cs.wasselha.model.Transporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class HomeCustomerFragment extends Fragment {

    RecyclerView servicesAvailableRecyclerView;
    ArrayList<Service> servicesModelDAList = new ArrayList<>();
    ArrayList<ServicesModel> servicesModelList = new ArrayList<>();

    int imageCard = R.drawable.car1;
    boolean fromFilter = false;
    static Context context ;
    Intent intentFromFilter;
    private static final String ID_KEY = "id";
    private static final String LOGIN_TYPE_KEY = "loginType";
    private static final String PREFERENCES_NAME = "MyPreferences";
    private static String apiURL="http://176.119.254.198:8000/wasselha";
    private int customerId;
    private String userType;
    Customer customerObj;
    Location locationOfCustomer;

    private ProgressDialog progressDialog;
    public HomeCustomerFragment(Context context2 ) {
        context = context2;
        //this.servicesModelList = servicesModelList;
    }

    public HomeCustomerFragment(Context context2 ,boolean fromFilter, Intent intentFromFilter) {
        context = context2;
        this.fromFilter = fromFilter;

        if (fromFilter){
            this.intentFromFilter = intentFromFilter;
            fromFilterMethod();
        }

        //this.servicesModelList = servicesModelList;
    }

    double longSrcFilter;
    double longDestFilter;
    double latDestFilter;
    double latSrcFilter;
    int maxPriceFilter = 10000000;
    String prefSpin = "Location";;
    String spinnerPackType = "default";
    private void fromFilterMethod() {
        longSrcFilter =  Double.parseDouble(intentFromFilter.getStringExtra("srcLong"));
        longDestFilter =  Double.parseDouble(intentFromFilter.getStringExtra("destLong"));
        latDestFilter =  Double.parseDouble(intentFromFilter.getStringExtra("destLat"));
        latSrcFilter =  Double.parseDouble(intentFromFilter.getStringExtra("srcLat"));
        maxPriceFilter =  Integer.parseInt(intentFromFilter.getStringExtra("priceMaxValue"));
        prefSpin = intentFromFilter.getStringExtra("prefSpinner");
        spinnerPackType = intentFromFilter.getStringExtra("spinnerPackType");

        if (prefSpin == null)
            prefSpin = "Location";
    }

    public HomeCustomerFragment( ) {

        //this.servicesModelList = servicesModelList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_home_customer, container, false);
        servicesAvailableRecyclerView = view.findViewById(R.id.servicesAvailableRecyclerView);
        setComparator();

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

            }, 1000);
            getFromSharedPref();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }


        Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        /*if(servicesModelDAList.size()==0){*/

                        servicesModelDAList = getServiceFromDA();
                           // Log.d("list222",servicesModelDAList.toString());
                        servicesModelSetup();
                       /* }else {
                            trySort();
                            //Collections.sort(servicesModelList, comparatorLocation);
                        }*/

                        ServicesModelRecyclerViewAdapter serviceAdapter = new ServicesModelRecyclerViewAdapter(getContext(), servicesModelList,servicesModelDAList);
                        servicesAvailableRecyclerView.setAdapter(serviceAdapter);
                        servicesAvailableRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

        return view;
    }

    private void trySort() {

        if (prefSpin.equalsIgnoreCase("Location") ||prefSpin.equalsIgnoreCase("الموقع")){

            Collections.sort(servicesModelList, comparatorLocation);
        }else if(prefSpin.equalsIgnoreCase("Price") ||prefSpin.equalsIgnoreCase("السعر")){


            servicesModelList.sort(new Comparator<ServicesModel>() {
                @Override
                public int compare(ServicesModel o1, ServicesModel o2) {
                    return (int) (o1.getPrice() - o2.getPrice());
                }
            });
        }else if(prefSpin.equalsIgnoreCase("Time") ||prefSpin.equalsIgnoreCase("الوقت")){

            servicesModelList.sort(new Comparator<ServicesModel>() {
                @Override
                public int compare(ServicesModel o1, ServicesModel o2) {
                    return  new Date(o1.getTime()).compareTo(new Date(o2.getTime())) ;
                }
            });
        }else if(prefSpin.equalsIgnoreCase("Review") ||prefSpin.equalsIgnoreCase("التقييم")){

            servicesModelList.sort(new Comparator<ServicesModel>() {
                @Override
                public int compare(ServicesModel o1, ServicesModel o2) {
                    return  (o1.getReview() - o2.getReview()) ;
                }
            });
        }
    }
    private boolean isValidPackageSpinnerType(String vehicleType) {

        if (spinnerPackType.equalsIgnoreCase("default"))
            return true;
        if (spinnerPackType.equalsIgnoreCase("Small (Car)") ||spinnerPackType.equalsIgnoreCase("صغير الحجم (سيارة)")){

            if (vehicleType.equalsIgnoreCase("car"))
                return true;
        }else if (spinnerPackType.equalsIgnoreCase("Normal (Van)") ||spinnerPackType.equalsIgnoreCase("متوسط الحجم (شاحنة صغيرة)")){

            if (vehicleType.equalsIgnoreCase("van"))
                return true;
        }else if (spinnerPackType.equalsIgnoreCase("Big (Truck)") ||spinnerPackType.equalsIgnoreCase("كبير الحجم (شاحنة)")){

            if (vehicleType.equalsIgnoreCase("truck"))
                return true;
        }
        return false;
    }

    private ArrayList<Service> getServiceFromDA() throws IOException {

        ServiceDA serviceDA = new ServiceDA();
        return serviceDA.getServices();
    }

    private boolean alreadyReservedByThisCustomer(int serviceId){
        for (int j = 0 ; j < delivaryDetailsReservationsForCustomerData.size() ; j++){

            if(delivaryDetailsReservationsForCustomerData.get(j).getService() == serviceId)
                return true;
        }
        return false;
    }

    ArrayList<DeliveryServiceDetails> delivaryDetailsReservationsForCustomerData;
    private void servicesModelSetup() throws IOException {



        delivaryDetailsReservationsForCustomerData = new DeliveryServiceDetailsDA().getDSDsForACustomer(customerId);



        if(prefSpin.equalsIgnoreCase("Location") ||prefSpin.equalsIgnoreCase("الموقع"))
            defaultGetDataAndLocation();
        else
            getDataForAll();

        trySort();



    }

    private void getDataForAll() throws IOException {
        for(int i = 0 ; i < servicesModelDAList.size() ; i++)
        {

            VehiclesDA vehiclesDA = new VehiclesDA();
            String vehicleType =  vehiclesDA.getVehicleTypeOfTransporter(servicesModelDAList.get(i).getTransporter());
            if (!isValidPackageSpinnerType(vehicleType) )
                continue;
            if (alreadyReservedByThisCustomer(servicesModelDAList.get(i).getId()))
                continue;
            // this is for the filter
            if (servicesModelDAList.get(i).getPrice() > maxPriceFilter)
                continue;


            Transporter transporter =  new TransporterDA().getTransporter(servicesModelDAList.get(i).getTransporter());
            LocationDA locationDA = new LocationDA();

            Location srcLocation = locationDA.getLocation(servicesModelDAList.get(i).getSource_place());
            Location destLocation = locationDA.getLocation(servicesModelDAList.get(i).getDestination_place());

            if ((int) ( calcDistanceFromLongLat(srcLocation.getLatitude(),srcLocation.getLongitude(),latSrcFilter,longSrcFilter)+
                    calcDistanceFromLongLat(destLocation.getLatitude(),destLocation.getLongitude(),latDestFilter,longDestFilter)  ) >30 )
                continue;
            servicesModelList.add(new ServicesModel(


                    servicesModelDAList.get(i).getId(),
                    transporter.getFirst_name(),
                    servicesModelDAList.get(i).getTransporter(),
                    servicesModelDAList.get(i).getService_date().toString(),

                    srcLocation.getTitle(),

                    destLocation.getTitle(),

                    vehiclesDA.getVehicleImageURLOfTransporter(servicesModelDAList.get(i).getTransporter()),

                    vehicleType,
                    transporter.getReview(),
                    srcLocation,
                    destLocation,
                    servicesModelDAList.get(i).getPrice()));
        }
    }

    private void defaultGetDataAndLocation() throws IOException {
        for(int i = 0 ; i < servicesModelDAList.size() ; i++)
        {

            VehiclesDA vehiclesDA = new VehiclesDA();
            String vehicleType =  vehiclesDA.getVehicleTypeOfTransporter(servicesModelDAList.get(i).getTransporter());
            if (!isValidPackageSpinnerType(vehicleType) )
                continue;
            if (alreadyReservedByThisCustomer(servicesModelDAList.get(i).getId()))
                continue;
            // this is for the filter
            if (servicesModelDAList.get(i).getPrice() > maxPriceFilter)
                continue;


            Transporter transporter =  new TransporterDA().getTransporter(servicesModelDAList.get(i).getTransporter());
            LocationDA locationDA = new LocationDA();

            Location srcLocation = locationDA.getLocation(servicesModelDAList.get(i).getSource_place());
            Location destLocation = locationDA.getLocation(servicesModelDAList.get(i).getDestination_place());



            servicesModelList.add(new ServicesModel(


                    servicesModelDAList.get(i).getId(),
                    transporter.getFirst_name(),
                    servicesModelDAList.get(i).getTransporter(),
                    servicesModelDAList.get(i).getService_date().toString(),

                    srcLocation.getTitle(),

                    destLocation.getTitle(),

                    vehiclesDA.getVehicleImageURLOfTransporter(servicesModelDAList.get(i).getTransporter()),

                    vehicleType,
                    transporter.getReview(),
                    srcLocation,
                    destLocation,
                    servicesModelDAList.get(i).getPrice()));
        }
    }

    Comparator<ServicesModel> comparatorLocation;
    private void setComparator(){

        comparatorLocation = new Comparator<ServicesModel>() {


            // Method
            @Override
            public int compare(ServicesModel s1, ServicesModel s2) {



                double latSrc1 = s1.getSrcLocation().getLatitude();
                double longSrc1 =  s1.getSrcLocation().getLongitude();

                double latSrc2 = s2.getSrcLocation().getLatitude();
                double longSrc2 =  s2.getSrcLocation().getLongitude();

                double latDest1 = s1.getDestLocation().getLatitude();
                double longDest1 =  s1.getDestLocation().getLongitude();

                double latDest2 = s2.getDestLocation().getLatitude();
                double longDest2 =  s2.getDestLocation().getLongitude();


                double longSrcFilterIn;
                double longDestFilterIn;
                double latDestFilterIn;
                double latSrcFilterIn;

                if (intentFromFilter != null){
                    longSrcFilterIn= longSrcFilter ;
                    longDestFilterIn= longDestFilter ;
                    latDestFilterIn=   latDestFilter ;
                    latSrcFilterIn= latSrcFilter ;
                }else {

                    if (userType !=null){


                            
                            longSrcFilterIn = locationOfCustomer.getLongitude();
                            latSrcFilterIn = locationOfCustomer.getLatitude();
                            longDestFilterIn = 0; // defalut value , it is not true, for false for all , means ok!
                            latDestFilterIn = 0;
                           


                    }else {
                        return 0;
                    }

                }


             return  (int) ( (calcDistanceFromLongLat(latSrc1, longSrc1,latSrcFilterIn,longSrcFilterIn) + (calcDistanceFromLongLat(latDest1, longDest1,latDestFilterIn,longDestFilterIn)))-
                     (calcDistanceFromLongLat(latSrc2, longSrc2,latSrcFilterIn,longSrcFilterIn) +(calcDistanceFromLongLat(latDest2, longDest2,latDestFilterIn,longDestFilterIn))) )
                ;


            }
        };
    }

    private double calcDistanceFromLongLat(double lat1,double lon1,double lat2,double lon2) {
        int R = 6371; // Radius of the earth in km
        double dLat = deg2rad(lat2-lat1);  // deg2rad below
        double dLon = deg2rad(lon2-lon1);
        double a =
                Math.sin(dLat/2) * Math.sin(dLat/2) +
                        Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                                Math.sin(dLon/2) * Math.sin(dLon/2)
                ;
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c; // Distance in km
        return d;
    }



    double deg2rad(double deg) {
        return deg * (Math.PI/180);
    }

    /*private String getImageUrl(Service service) {


        getVehicleImageURLAndSetImage(context,service.getTransporter().getId());

    }*/


    void getFromSharedPref() throws IOException {
        SharedPreferences preferences = getActivity().getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        userType = preferences.getString(LOGIN_TYPE_KEY, null);
        customerId =Integer.parseInt( preferences.getString(ID_KEY,""));

         customerObj = new CustomerDA().getCustomer(customerId);

         locationOfCustomer=  new LocationDA().getLocation(customerObj.getLocation());

    }



}