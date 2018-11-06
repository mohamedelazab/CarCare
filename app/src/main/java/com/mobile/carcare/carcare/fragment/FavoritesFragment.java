package com.mobile.carcare.carcare.fragment;


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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoritesFragment extends Fragment implements AgencyListener {

//    @BindView(R.id.toolbar_me2)
    Toolbar mToolbar;
   // @BindView(R.id.tv_toolbar)
    TextView tvToolbar;
    //@BindView(R.id.img_view_type_toolbar)
    ImageView imgViewToolbar;
    @BindView(R.id.rv_favorites)
    RecyclerView rvFavorites;
    @BindView(R.id.progress_bar_fav)
    ProgressBar progressBar;
    @BindView(R.id.tv_no_favorites)
    TextView tvNoFavorites;
    AgenciesAdapter adapter;
    GridLayoutManager layoutManager;
    List<Agency> favAgencies;
    List<String> agencyKeys;
    boolean isViewGrid =true;
    int scrollPosition =0;

    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static Bundle mBundleRecyclerViewState;
    private Parcelable mListState = null;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        favAgencies =new ArrayList<>();
        agencyKeys =new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =inflater.inflate(R.layout.fragment_favorites, container, false);
        ButterKnife.bind(this, rootView);
        mToolbar =rootView.findViewById(R.id.toolbar_me2);
        tvToolbar =rootView.findViewById(R.id.tv_toolbar);
        imgViewToolbar =rootView.findViewById(R.id.img_view_type_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        tvToolbar.setText(R.string.favorites);

        imgViewToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isViewGrid){
                    imgViewToolbar.setImageResource(R.drawable.ic_linear);
                    layoutManager =new GridLayoutManager(getContext(),2);
                    rvFavorites.setLayoutManager(layoutManager);
                    isViewGrid =false;
                }
                else {
                    imgViewToolbar.setImageResource(R.drawable.ic_grid);
                    layoutManager =new GridLayoutManager(getContext(),1);
                    rvFavorites.setLayoutManager(layoutManager);
                    isViewGrid =true;
                }
            }
        });
        adapter =new AgenciesAdapter(getContext(), favAgencies, this);
        layoutManager =new GridLayoutManager(getContext(),1);
        rvFavorites.setLayoutManager(layoutManager);
        rvFavorites.setAdapter(adapter);
        loadFavorites();
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
                    }
                    adapter.notifyDataSetChanged();
                    rvFavorites.scrollToPosition(scrollPosition);
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (mBundleRecyclerViewState != null) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    mListState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
                    rvFavorites.getLayoutManager().onRestoreInstanceState(mListState);

                }
            }, 50);
        }

        rvFavorites.setLayoutManager(layoutManager);
    }

    private void loadAgencies() {
        DatabaseReference agenciesRef =FirebaseDatabase.getInstance().getReference().child("agencies");
        agenciesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                favAgencies.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    Agency agency =snapshot.getValue(Agency.class);

                    for (String key :agencyKeys)
                        if (agency.getKey().equals(key)){
                            agency.setInFavorites(true);
                            favAgencies.add(agency);
                        }
                }
                Log.e("FAV_AGENCIES_SIZE", favAgencies.size()+"");
                progressBar.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
                rvFavorites.scrollToPosition(scrollPosition);
                if (favAgencies.size() ==0)
                tvNoFavorites.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("AGENCIES_Error", databaseError.getMessage()+"");
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}