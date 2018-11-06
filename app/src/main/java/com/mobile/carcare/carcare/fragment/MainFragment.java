package com.mobile.carcare.carcare.fragment;


import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
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
import android.widget.ProgressBar;
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
import com.mobile.carcare.carcare.widget.AgencyWidget;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements AgencyListener {

    Toolbar toolbar;
    ImageView imgViewTypeToolbar;
    TextView tvToolbar;

    RecyclerView rvAgencies;
    List<Agency> agencies;
    List<String> agencyKeys;
    GridLayoutManager layoutManager;
    AgenciesAdapter adapter;
    ProgressBar progressBar;
    boolean isViewGrid =true;
    int scrollPosition =0;
    DatabaseReference agenciesRef;
    Parcelable mLayoutManagerSavedState;

    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static Bundle mBundleRecyclerViewState;
    private Parcelable mListState = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =inflater.inflate(R.layout.fragment_main, container, false);
        toolbar =rootView.findViewById(R.id.toolbar_me2);
        imgViewTypeToolbar =rootView.findViewById(R.id.img_view_type_toolbar);
        tvToolbar =rootView.findViewById(R.id.tv_toolbar);
        tvToolbar.setText(R.string.agencies);
        progressBar =rootView.findViewById(R.id.progress_bar_main);
        rvAgencies =rootView.findViewById(R.id.rv_agency);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        agencies =new ArrayList<>();
        agencyKeys =new ArrayList<>();
        layoutManager =new GridLayoutManager(getContext(),1);
        adapter =new AgenciesAdapter(getContext(), agencies, this);
        rvAgencies.setHasFixedSize(true);
        rvAgencies.setLayoutManager(layoutManager);
        rvAgencies.setAdapter(adapter);

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
//        if (savedInstanceState !=null){
//            scrollPosition =savedInstanceState.getInt("scroll_position");
////            Log.e("GGGGGG", "SAVED_POSITION: "+scrollPosition);
//        }

        loadFavorites();

        Font.apply(getContext(), rootView);
        return rootView;
    }

//    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        int scrollPosition =layoutManager.findFirstCompletelyVisibleItemPosition();
//        if(scrollPosition ==-1){
//            scrollPosition =layoutManager.findLastVisibleItemPosition();
//        }
//        outState.putInt("scroll_position", scrollPosition);
//    }
@Override
public void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelable("scroll_position", layoutManager.onSaveInstanceState());
    outState.putInt("scroll_position2", layoutManager.findLastVisibleItemPosition());
}

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            mLayoutManagerSavedState = savedInstanceState.getParcelable("scroll_position");
            scrollPosition =savedInstanceState.getInt("scroll_position2");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mBundleRecyclerViewState = new Bundle();
        mListState = rvAgencies.getLayoutManager().onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, mListState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (mBundleRecyclerViewState != null) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    mListState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
                    rvAgencies.getLayoutManager().onRestoreInstanceState(mListState);

                }
            }, 50);
        }

        rvAgencies.setLayoutManager(layoutManager);
    }

    private void loadFavorites() {
        progressBar.setVisibility(View.VISIBLE);
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
            agenciesRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
                    }
                    loadAgencies();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("AGENCIES_Error", databaseError.getMessage() + "");
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
        else {
            loadAgencies();
        }
    }

    private void loadAgencies() {

        agenciesRef =FirebaseDatabase.getInstance().getReference().child("agencies");
        agenciesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //agencies.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    Agency agency =snapshot.getValue(Agency.class);
                    agencies.add(agency);
                    Log.e("Agency", agency.getAvatar());
                    for (String key :agencyKeys)
                    if (agency.getKey().equals(key)){
                        agency.setInFavorites(true);
                    }
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                Log.e("AGENCIES_SIZE", agencies.size()+"");
                //rvAgencies.smoothScrollToPosition(scrollPosition);
                //Log.e("GGGGGGGGGGGGGGGG", scrollPosition + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Log.e("AGENCIES_Error", databaseError.getMessage()+"");
                progressBar.setVisibility(View.GONE);
            }

        });
    }

    @Override
    public void onAgencyClick(Agency agency, int position) {
        Intent profileIntent =new Intent(getContext(), AgencyProfileActivity.class);
        profileIntent.putExtra(Constants.Agency_Profile_Intent,agency);
        profileIntent.putExtra(Constants.Agency_Profile_Intent_POSITION,position);
        startActivity(profileIntent);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getContext());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(getContext(), AgencyWidget.class));
        AgencyWidget.updateFromActivity(getContext(), appWidgetManager, appWidgetIds, position, (ArrayList<Agency>) agencies);
    }
}