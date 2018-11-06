package com.mobile.carcare.carcare.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobile.carcare.carcare.R;
import com.mobile.carcare.carcare.activity.AgencyProfileActivity;
import com.mobile.carcare.carcare.adapter.AgenciesAdapter;
import com.mobile.carcare.carcare.interfaces.AgencyListener;
import com.mobile.carcare.carcare.model.Agency;
import com.mobile.carcare.carcare.utils.Constants;
import com.mobile.carcare.carcare.utils.Font;
import com.mobile.carcare.carcare.utils.MyPreferences;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchResultFragment extends Fragment implements AgencyListener {

    Toolbar toolbar;
    ImageView imgViewTypeToolbar;
    TextView tvToolbar;
    @BindView(R.id.rv_agency)
    RecyclerView rvAgencies;
    List<Agency> agencies;
    List<String> agencyKeys;
    GridLayoutManager layoutManager;
    AgenciesAdapter adapter;
    boolean isViewGrid =true;
    int scrollPosition=0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        agencies =new ArrayList<>();
        agencyKeys =new ArrayList<>();
        agencies.clear();
        agencies =getArguments().getParcelableArrayList("SEARCH_RESULT");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =inflater.inflate(R.layout.fragment_search_result, container, false);
        ButterKnife.bind(this, rootView);
        toolbar =rootView.findViewById(R.id.toolbar_me2);
        imgViewTypeToolbar =rootView.findViewById(R.id.img_view_type_toolbar);
        tvToolbar =rootView.findViewById(R.id.tv_toolbar);
        tvToolbar.setText(R.string.search_result);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        layoutManager =new GridLayoutManager(getContext(),1);
        adapter =new AgenciesAdapter(getContext(), agencies, this);
        rvAgencies.setLayoutManager(layoutManager);
        rvAgencies.setAdapter(adapter);
        loadFavorites();

        imgViewTypeToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isViewGrid){
                    imgViewTypeToolbar.setImageResource(R.drawable.ic_linear);
                    layoutManager =new GridLayoutManager(getContext(),2);
                    rvAgencies.setLayoutManager(layoutManager);
                    isViewGrid =false;
                }
                else {
                    imgViewTypeToolbar.setImageResource(R.drawable.ic_grid);
                    layoutManager =new GridLayoutManager(getContext(),1);
                    rvAgencies.setLayoutManager(layoutManager);
                    isViewGrid =true;
                }
            }
        });
        Font.apply(getContext(), rootView);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        scrollPosition =layoutManager.findFirstCompletelyVisibleItemPosition();
        if(scrollPosition ==-1){
            scrollPosition =layoutManager.findLastVisibleItemPosition();
        }
        outState.putInt("scroll_position", scrollPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState !=null){
            if(savedInstanceState.containsKey("scroll_position")){
                scrollPosition =savedInstanceState.getInt("scroll_position");
            }
        }
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onAgencyClick(Agency agency, int position) {
        Intent profileIntent =new Intent(getContext(), AgencyProfileActivity.class);
        profileIntent.putExtra(Constants.Agency_Profile_Intent,agency);
        startActivity(profileIntent);
    }

    private void loadFavorites() {
        String childType = "";
        MyPreferences preferences = new MyPreferences(getContext());
        Log.e("Pref", preferences.getUserType()+"");
        if (preferences.getUserType() == 1) {
            childType = "users";
        } else if(preferences.getUserType() == 2) {
            childType = "agencies";
        }
        if (!childType.equals("")) {
            DatabaseReference agenciesRef = FirebaseDatabase.getInstance().getReference()
                    .child(childType).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("favorites");
            agenciesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    agencyKeys.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Log.e("Favorites", snapshot.getKey()+" , "+snapshot.getValue());
                        agencyKeys.add(snapshot.child("agency_key").getValue(String.class));
                    }
                    Log.e("Favorites", agencyKeys.size()+"");
                    if (agencyKeys.size()>0){
                        Log.e("Favorites", agencyKeys.get(0)+"");
                        viewFavorites();
                    }
                }

                private void viewFavorites() {
                    for (int i = 0; i < agencyKeys.size(); i++) {
                        for (int j = 0; j < agencies.size(); j++) {
                            if (agencies.get(j).getKey().equals(agencyKeys.get(i))){
                                agencies.get(j).setInFavorites(true);
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                    rvAgencies.scrollToPosition(scrollPosition);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("AGENCIES_Error", databaseError.getMessage() + "");
                }
            });
        }

    }
}
