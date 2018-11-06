package com.mobile.carcare.carcare.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mobile.carcare.carcare.R;
import com.mobile.carcare.carcare.adapter.ServiceAdapter;
import com.mobile.carcare.carcare.interfaces.ServiceListener;
import com.mobile.carcare.carcare.model.Service;
import com.mobile.carcare.carcare.utils.Font;
import com.mobile.carcare.carcare.utils.MyPreferences;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileServicesFragment extends Fragment implements ServiceListener {

    @BindView(R.id.rv_services)
    RecyclerView rvServices;
    @BindView(R.id.tv_no_services)
    TextView tvNoServices;
    List<Service> services;
    GridLayoutManager layoutManager;
    ServiceAdapter serviceAdapter;

    String agencyKey;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        services = new ArrayList<>();
        assert getArguments() != null;
        agencyKey = getArguments().getString("Agency_Key");

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile_services, container, false);
        ButterKnife.bind(this, rootView);
        layoutManager = new GridLayoutManager(getContext(), 1);
        serviceAdapter = new ServiceAdapter(getContext(), services, this);
        rvServices.setLayoutManager(layoutManager);
        rvServices.setAdapter(serviceAdapter);
        loadServices();
        Font.apply(getContext(), rootView);
        return rootView;
    }

    private void loadServices() {
        DatabaseReference agenciesRef = FirebaseDatabase.getInstance().getReference().child("agencies").child(agencyKey).child("services");
        agenciesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                services.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    services.add(snapshot.getValue(Service.class));
                    serviceAdapter.notifyDataSetChanged();
                }
                Log.e("Services_SIZE", services.size() + "");
                if (services.size() ==0){
                    tvNoServices.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("SERVICES_Error", databaseError.getMessage() + "");
            }
        });
    }

    @Override
    public void onServiceClick(final Service service) {
        if (new MyPreferences(getContext()).getUserType() == 2) {
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user !=null) {
                if (agencyKey.equals(user.getUid())) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(getResources().getString(R.string.delete));
                    builder.setIcon(getResources().getDrawable(R.drawable.ic_delete_red_24dp));
                    builder.setMessage(getResources().getString(R.string.delete) + service.getServiceTitle() + " ?");
                    builder.setPositiveButton(getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getContext(), getResources().getString(R.string.delete), Toast.LENGTH_SHORT).show();
                            deleteService(service,user);
                        }
                    });

                    builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    builder.show();
                }
            }
        }
    }

    private void deleteService(final Service service, FirebaseUser user) {
        if (user != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            Query servicesQuery = ref.child("agencies").child(user.getUid()).child("services").orderByChild("id").equalTo(service.getId());

            servicesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                        appleSnapshot.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("TAG", "onCancelled", databaseError.toException());
                }
            });
        }
    }
}