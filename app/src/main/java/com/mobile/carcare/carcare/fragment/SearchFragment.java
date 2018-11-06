package com.mobile.carcare.carcare.fragment;


import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobile.carcare.carcare.R;
import com.mobile.carcare.carcare.adapter.CityAdapter;
import com.mobile.carcare.carcare.adapter.CountryAdapter;
import com.mobile.carcare.carcare.adapter.ProvinceAdapter;
import com.mobile.carcare.carcare.model.Agency;
import com.mobile.carcare.carcare.model.City;
import com.mobile.carcare.carcare.model.Country;
import com.mobile.carcare.carcare.model.Province;
import com.mobile.carcare.carcare.utils.Font;
import com.mobile.carcare.carcare.utils.Helper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    @BindView(R.id.et_agency_name_search)
    EditText etAgencySearch;

    @BindView(R.id.spinner_country)
    Spinner spinnerCountry;
    @BindView(R.id.spinner_province)
    Spinner spinnerProvince;
    @BindView(R.id.spinner_city)
    Spinner spinnerCity;

    Country selectedCountry;
    Province selectedProvince;
    City selectedCity;

    CountryAdapter countryAdapter;
    ProvinceAdapter provinceAdapter;
    CityAdapter cityAdapter;
    List<Country> allCountries;
    List<Province> allProvinces;
    List<City> allCities;

    List<Province> countryProvinces;
    List<City> provinceCities;

    @BindView(R.id.btn_do_search)
    Button btnDoSearch;

    List<Agency> searchResultAgencies;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allCountries = new ArrayList<>();
        allProvinces = new ArrayList<>();
        allCities = new ArrayList<>();
        searchResultAgencies =new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView  =inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, rootView);

        countryAdapter = new CountryAdapter(getContext(), allCountries);
        spinnerCountry.setAdapter(countryAdapter);
        spinnerCountry.setSelection(0, false);

        loadCities();
        loadProvinces();
        loadCountries();

        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position !=0) {
                    selectedCountry = allCountries.get(position);
                    Log.e("Country", selectedCountry.getCountryId());
                    spinnerProvince.setAdapter(null);
                    spinnerCity.setAdapter(null);
                    countryProvinces = new ArrayList<>();
                    countryProvinces.add(new Province("", getContext().getResources().getString(R.string.select_province), ""));
                    for (Province province : allProvinces) {
                        Log.e("province_Country", province.getCountryId() + "");
                        if (province.getCountryId().equals(selectedCountry.getCountryId())) {
                            countryProvinces.add(province);
                        }
                    }
                    Log.e("countryProvinces", countryProvinces.size() + "");
                    provinceAdapter = new ProvinceAdapter(getContext(), countryProvinces);
                    spinnerProvince.setAdapter(provinceAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position !=0) {
                    selectedProvince = countryProvinces.get(position);
                    provinceCities = new ArrayList<>();
                    provinceCities.add(new City("", getContext().getResources().getString(R.string.select_city), ""));
                    for (City city : allCities) {
                        if (city.getProvinceId().equals(selectedProvince.getProvinceId())) {
                            provinceCities.add(city);
                        }
                    }
                    cityAdapter = new CityAdapter(getContext(), provinceCities);
                    spinnerCity.setAdapter(cityAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position !=0)
                selectedCity = provinceCities.get(position);
                //Toast.makeText(getContext(), ""+selectedCity.getCityName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnDoSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String searchInput =etAgencySearch.getText().toString().trim();
                if (!searchInput.equals("")) {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("agencies");
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                                Agency agency =snapshot.getValue(Agency.class);
                                if (agencyCheckValid(agency,searchInput)){
                                    searchResultAgencies.add(agency);
                                }
                            }
                            if (searchResultAgencies.size()>0){
                                Bundle bundle =new Bundle();
                                bundle.putParcelableArrayList("SEARCH_RESULT", (ArrayList<? extends Parcelable>) searchResultAgencies);
                                SearchResultFragment searchResultFragment =new SearchResultFragment();
                                searchResultFragment.setArguments(bundle);
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.fl_home,searchResultFragment,"search_result").commit();
                            }
                            else {
                                Toast.makeText(getContext(), R.string.no_agencies, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else {
                    etAgencySearch.setError(getResources().getString(R.string.required));
                }
            }
        });
        Font.apply(getContext(), rootView);
        return rootView;
    }

    private boolean agencyCheckValid(Agency agency, String searchInput) {
        if (!Helper.isInputMatch(agency.getName(), searchInput)){
            return false;
        }
        else if (selectedCountry !=null){
            return agency.getCountryId().equals(selectedCountry.getCountryId());
        }
        else if (selectedProvince !=null){
            return agency.getProvinceId().equals(selectedProvince.getProvinceId());
        }
        else if (selectedCity !=null){
            return agency.getCityId().equals(selectedCity.getCityId());
        }
        return true;
    }


    private void loadCountries() {

        DatabaseReference agenciesRef = FirebaseDatabase.getInstance().getReference().child("countries");
        agenciesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allCountries.clear();
                allCountries.add(new Country("",getContext().getResources().getString(R.string.select_country)));
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    allCountries.add(snapshot.getValue(Country.class));
                    snapshot.getValue(Province.class);
                }
                countryAdapter.notifyDataSetChanged();
                Log.e("Countries_SIZE", allCountries.size() + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Countries_Error", databaseError.getMessage() + "");
            }
        });
    }

    private void loadProvinces() {
        DatabaseReference agenciesRef = FirebaseDatabase.getInstance().getReference().child("provinces");
        agenciesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allProvinces.clear();
                allProvinces.add(new Province("","Select Province",""));
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    allProvinces.add(snapshot.getValue(Province.class));
                    snapshot.getValue(Province.class);
                }
                Log.e("provinces_SIZE", allProvinces.size() + "");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Provinces_Error", databaseError.getMessage() + "");
            }
        });
    }

    private void loadCities() {

        DatabaseReference agenciesRef = FirebaseDatabase.getInstance().getReference().child("cities");
        agenciesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allCities.clear();
                allCities.add(new City("",getContext().getResources().getString(R.string.select_city),""));
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    allCities.add(snapshot.getValue(City.class));
                    snapshot.getValue(Province.class);
                }

                Log.e("Cities_SIZE", allCities.size() + "");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Cities_Error", databaseError.getMessage() + "");
            }
        });
    }
}