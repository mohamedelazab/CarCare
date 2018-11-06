package com.mobile.carcare.carcare.fragment;


import android.content.Intent;
import android.os.Bundle;
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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobile.carcare.carcare.R;
import com.mobile.carcare.carcare.activity.HomeActivity;
import com.mobile.carcare.carcare.adapter.CityAdapter;
import com.mobile.carcare.carcare.adapter.CountryAdapter;
import com.mobile.carcare.carcare.adapter.ProvinceAdapter;
import com.mobile.carcare.carcare.dialog.MapFragmentDialog;
import com.mobile.carcare.carcare.model.City;
import com.mobile.carcare.carcare.model.Country;
import com.mobile.carcare.carcare.model.Province;
import com.mobile.carcare.carcare.utils.Constants;
import com.mobile.carcare.carcare.utils.Font;
import com.mobile.carcare.carcare.utils.MyPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static com.mobile.carcare.carcare.utils.Validate.isNotEmpty;
import static com.mobile.carcare.carcare.utils.Validate.isValidEmail;
import static com.mobile.carcare.carcare.utils.Validate.passwordMatch;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpAgencyFragment extends Fragment {

    @BindView(R.id.et_name_agency_sign)
    EditText etAgencyName;
    @BindView(R.id.et_address_agency_sign)
    EditText etAgencyAddress;
    @BindView(R.id.et_location_agency_sign)
    EditText etAgencyLocation;
    @BindView(R.id.et_email_agency_sign)
    EditText etAgencyEmail;
    @BindView(R.id.et_pass_agency_sign)
    EditText etAgencyPass;
    @BindView(R.id.et_pass_confirm_agency_sign)
    EditText etAgencyPassConfirm;
    @BindView(R.id.spinner_country)
    Spinner spinnerCountry;
    @BindView(R.id.spinner_province)
    Spinner spinnerProvince;
    @BindView(R.id.spinner_city)
    Spinner spinnerCity;
    @BindView(R.id.btn_sign_agency)
    Button btnSignUpAgency;

    @BindView(R.id.progress_bar_agency)
    ProgressBar mProgressBar;

    DatabaseReference databaseReference;
    String lat = "", lng = "";
    Country selectedCountry;
    Province selectedProvince;
    City selectedCity;

    CountryAdapter countryAdapter;
    ProvinceAdapter provinceAdapter;
    CityAdapter cityAdapter;
    List<Country> countries;
    List<Province> allProvinces;
    List<City> allCities;

    List<Province> countryProvinces;
    List<City> provinceCities;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        countries = new ArrayList<>();
        allProvinces = new ArrayList<>();
        allCities = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_sign_up_agency, container, false);
        ButterKnife.bind(this, rootView);
        //load spinners data and make the adapter and custom item..

        countryAdapter = new CountryAdapter(getContext(), countries);
        spinnerCountry.setAdapter(countryAdapter);

        etAgencyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMapFragment(new MapFragmentDialog());
            }
        });

        etAgencyLocation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    openMapFragment(new MapFragmentDialog());
                }
            }
        });

        loadCities();
        loadProvinces();
        loadCountries();

        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCountry = countries.get(position);
                Log.e("Country", selectedCountry.getCountryId());
                spinnerProvince.setAdapter(null);
                spinnerCity.setAdapter(null);
                countryProvinces = new ArrayList<>();
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

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedProvince = countryProvinces.get(position);
                //Toast.makeText(getContext(), "Selected.!", Toast.LENGTH_SHORT).show();
                provinceCities = new ArrayList<>();
                for (City city : allCities) {
                    if (city.getProvinceId().equals(selectedProvince.getProvinceId())) {
                        provinceCities.add(city);
                    }
                }
                cityAdapter = new CityAdapter(getContext(), provinceCities);
                spinnerCity.setAdapter(cityAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCity = provinceCities.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSignUpAgency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etAgencyName.getText().toString().trim();
                String address = etAgencyAddress.getText().toString().trim();
                String email = etAgencyEmail.getText().toString().trim();
                String pass = etAgencyPass.getText().toString().trim();
                String passConfirm = etAgencyPassConfirm.getText().toString().trim();
                String location = etAgencyLocation.getText().toString().trim();

                if (isDataInputsValid(name, address, email, pass, passConfirm, location, selectedCountry, selectedProvince, selectedCity)) {
                    SignUpAgency(name, email, pass, address);
                }
            }
        });
        Font.apply(getContext(), rootView);
        return rootView;
    }

    private void SignUpAgency(final String name, String email, String pass, final String address) {
        mProgressBar.setVisibility(View.VISIBLE);

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            new MyPreferences(getContext()).setUserType(2);
                            databaseReference = FirebaseDatabase.getInstance().getReference()
                                    .child("agencies").child(uid);
                            HashMap<String, String> agencyData = new HashMap<>();
                            agencyData.put("name", name);
                            agencyData.put("avatar", "default");
                            agencyData.put("header", "default");
                            agencyData.put("lat", lat);
                            agencyData.put("lng", lng);
                            agencyData.put("address", address);
                            agencyData.put("countryId", selectedCountry.getCountryId());
                            agencyData.put("countryName", selectedCountry.getCountryName());
                            agencyData.put("provinceId", selectedProvince.getProvinceId());
                            agencyData.put("provinceName", selectedProvince.getProvinceName());
                            agencyData.put("cityId", selectedCity.getCityId());
                            agencyData.put("cityName", selectedCity.getCityName());
                            agencyData.put("phoneNumber", "0");
                            agencyData.put("description", "Description");
                            agencyData.put("key",uid);

                            databaseReference.setValue(agencyData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mProgressBar.setVisibility(View.GONE);
                                        Intent newIntent = new Intent(getContext(), HomeActivity.class);
                                        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(newIntent);
                                        getActivity().finish();
                                    }
                                }
                            });
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                etAgencyPass.setError(getString(R.string.error_weak_password));
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                etAgencyEmail.setError(getString(R.string.error_invalid_email));
                            } catch (FirebaseAuthUserCollisionException e) {
                                etAgencyEmail.setError(getString(R.string.error_user_exists));
                                etAgencyEmail.requestFocus();
                            } catch (Exception e) {
                                Log.e("TAGGGG", e.getMessage());
                                Toast.makeText(getContext(), R.string.an_error_occured, Toast.LENGTH_SHORT).show();
                            }
                            mProgressBar.setVisibility(View.GONE);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private boolean isDataInputsValid(String name, String address, String email, String pass, String passConfirm, String location, Country country, Province province, City city) {
        if (isNotEmpty(name) && isNotEmpty(address) && isValidEmail(email)
                && isNotEmpty(pass) && isNotEmpty(passConfirm)
                && passwordMatch(pass, passConfirm) && country !=null && province !=null && city !=null) {
            return true;
        } else {

            if (!isNotEmpty(pass)) {
                etAgencyPass.setError(getContext().getResources().getString(R.string.required));
            } else {
                if (!passwordMatch(pass, passConfirm)) {
                    etAgencyPassConfirm.setError(getContext().getResources().getString(R.string.not_match));
                }
            }

            if (!isNotEmpty(name)) {
                etAgencyName.setError(getContext().getResources().getString(R.string.required));
            }
            if (!isNotEmpty(address)) {
                etAgencyAddress.setError(getContext().getResources().getString(R.string.required));
            }
            if (!isValidEmail(email)) {
                etAgencyEmail.setError(getContext().getResources().getString(R.string.not_email));
            }
            if (!isNotEmpty(name)) {
                etAgencyName.setError(getContext().getResources().getString(R.string.required));
            }
            if (!isNotEmpty(location)) {
                etAgencyLocation.setError(getContext().getResources().getString(R.string.required));
            }
            if (country == null) {
                Toast.makeText(getContext(), R.string.select_country, Toast.LENGTH_SHORT).show();
            }
            if (province == null) {
                Toast.makeText(getContext(), R.string.select_province, Toast.LENGTH_SHORT).show();
            }
            if (city == null) {
                Toast.makeText(getContext(), R.string.select_city, Toast.LENGTH_SHORT).show();
            }

            return false;
        }
    }

    private void loadCountries() {

        DatabaseReference agenciesRef = FirebaseDatabase.getInstance().getReference().child("countries");
        agenciesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                countries.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    countries.add(snapshot.getValue(Country.class));
                    snapshot.getValue(Province.class);
                }
                countryAdapter.notifyDataSetChanged();
                Log.e("Countries_SIZE", countries.size() + "");
                if (countries.size() > 0) {
                    Log.e("Country", countries.get(0).getCountryName() +
                            "id: " + countries.get(0).getCountryName());
                }
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
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    allProvinces.add(snapshot.getValue(Province.class));
                    snapshot.getValue(Province.class);
                }
                Log.e("provinces_SIZE", allProvinces.size() + "");
                if (allProvinces.size() > 0) {
                    Log.e("Province", allProvinces.get(0).getProvinceName());
                }
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
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    allCities.add(snapshot.getValue(City.class));
                    snapshot.getValue(Province.class);
                }

                Log.e("Cities_SIZE", allCities.size() + "");
                if (allCities.size() > 0) {
                    Log.e("City", allCities.get(0).getCityName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Cities_Error", databaseError.getMessage() + "");
            }
        });
    }

    private void openMapFragment(MapFragmentDialog mapFragment) {
        mapFragment.setTargetFragment(SignUpAgencyFragment.this, Constants.FRAGMENT_CODE);
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fl_sign_up, mapFragment, "Fragment_map_dialog")
                    .addToBackStack(mapFragment.getClass().getName())
                    .commit();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.FRAGMENT_CODE) {
                lat = data.getStringExtra("Latitude");
                lng = data.getStringExtra("longitude");
                if (lat.equals("default") || lng.equals("default")) {

                }
                else {
                    etAgencyLocation.setText(getContext().getResources().getString(R.string.location_selected));
                    etAgencyLocation.setError(null);
                    etAgencyLocation.clearFocus();
//                Toast.makeText(getContext(), "Latitude: "+lat+"longitude: "+lng, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
