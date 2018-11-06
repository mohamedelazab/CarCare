package com.mobile.carcare.carcare.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mobile.carcare.carcare.R;
import com.mobile.carcare.carcare.utils.Font;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileMapFragment extends Fragment implements OnMapReadyCallback {

    //@BindView(R.id.map_view_profile)
    MapView myMapView;

    GoogleMap mGoogleMap;
    String agencyName;
    double lat, lng;

    Marker currentLocationMarker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile_map, container, false);
        //ButterKnife.bind(this, rootView);
        myMapView = rootView.findViewById(R.id.map_view_profile);
        Bundle bundle = getArguments();
        assert bundle != null;
        lat = Double.parseDouble(bundle.getString("lat"));
        lng = Double.parseDouble(bundle.getString("lng"));
        agencyName = bundle.getString("agencyName");
        Log.e("DataArgs",lat+", "+lng+", "+agencyName);
        Font.apply(getContext(), rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MapsInitializer.initialize(getContext());
        if (myMapView != null) {
            myMapView.onCreate(null);
            myMapView.onResume();
            myMapView.getMapAsync(this);
            myMapView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LatLng agencyPoint = new LatLng(lat, lng);
                    drawMarker(agencyPoint);
                }
            });
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap =googleMap;

        LatLng objLatLng=new LatLng(lat,lng);
       // drawMarker(objLatLng);
        Log.e("DataArgs",lat+", "+lng+", "+agencyName);

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(objLatLng, 20));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(objLatLng);
        markerOptions.title(agencyName);

        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        currentLocationMarker = mGoogleMap.addMarker(markerOptions);
    }

    private void drawMarker(LatLng point) {

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(point));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomBy(16.0f));

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(point);
        markerOptions.title(agencyName);

        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        currentLocationMarker = mGoogleMap.addMarker(markerOptions);
    }
}
