package com.mobile.carcare.carcare.fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mobile.carcare.carcare.R;
import com.mobile.carcare.carcare.adapter.CityAdapter;
import com.mobile.carcare.carcare.adapter.CountryAdapter;
import com.mobile.carcare.carcare.adapter.ProvinceAdapter;
import com.mobile.carcare.carcare.interfaces.BitmapResultListener;
import com.mobile.carcare.carcare.model.Agency;
import com.mobile.carcare.carcare.model.City;
import com.mobile.carcare.carcare.model.Country;
import com.mobile.carcare.carcare.model.Province;
import com.mobile.carcare.carcare.utils.Font;
import com.mobile.carcare.carcare.utils.Helper;
import com.mobile.carcare.carcare.utils.ResizeImage;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.mobile.carcare.carcare.utils.ResizeImage.getPath;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsAgencyFragment extends Fragment {

    private static final int HEADER_REQUEST_CODE = 2;
    private static final int AVATAR_REQUEST_CODE = 5;

    @BindView(R.id.image_header_edit)
    ImageView imgHeader;
    @BindView(R.id.image_avatar_edit)
    ImageView imgAvatar;
    @BindView(R.id.et_agency_name_edit)
    EditText etName;
    @BindView(R.id.et_agency_address_edit)
    EditText etAddress;
    @BindView(R.id.et_agency_description_edit)
    EditText etDescription;
    @BindView(R.id.et_agency_phone_edit)
    EditText etPhoneNumber;

    @BindView(R.id.spinner_country)
    Spinner spinnerCountry;
    @BindView(R.id.spinner_province)
    Spinner spinnerProvince;
    @BindView(R.id.spinner_city)
    Spinner spinnerCity;

    @BindView(R.id.btn_confirm_update)
    Button btnConfirmUpdates;

    private Country selectedCountry;
    private Province selectedProvince;
    private City selectedCity;

    private CountryAdapter countryAdapter;
    private ProvinceAdapter provinceAdapter;
    private CityAdapter cityAdapter;
    private List<Country> countries;
    private List<Province> allProvinces;
    private List<City> allCities;

    List<Province> countryProvinces;
    List<City> provinceCities;

    private Uri avatarURI, headerURI;
    private byte[] thumbAvatar, thumbHeader;
    FirebaseStorage storage;
    StorageReference storageReference;
    String headerDownloadUrl, avatarDownloadUrl;
    String name, address, description, phoneNumber;
    DatabaseReference currentAgencyReference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        countries = new ArrayList<>();
        allProvinces = new ArrayList<>();
        allCities = new ArrayList<>();
        currentAgencyReference = FirebaseDatabase.getInstance().getReference()
                .child("agencies").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        storage =FirebaseStorage.getInstance();
        storageReference =storage.getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_settings_agency, container, false);
        ButterKnife.bind(this, rootView);

        countryAdapter = new CountryAdapter(getContext(), countries);
        spinnerCountry.setAdapter(countryAdapter);

        loadCities();
        loadProvinces();
        loadCountries();
        loadCurrentUserData();

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

        btnConfirmUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = etName.getText().toString().trim();
                address = etAddress.getText().toString().trim();
                description = etDescription.getText().toString().trim();
                phoneNumber =etPhoneNumber.getText().toString().trim();
                if (name.equals("")) {
                    etName.setError(getResources().getString(R.string.required));
                } else if (address.equals("")) {
                    etName.setError(getResources().getString(R.string.required));
                } else if (description.equals("")) {
                    etName.setError(getResources().getString(R.string.required));
                }
                else if (phoneNumber.equals("")){
                    etPhoneNumber.setError(getResources().getString(R.string.required));
                }
                else {
                    confirmUpdates();
                }
            }
        });

        imgHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.select_header_image)), HEADER_REQUEST_CODE);
            }
        });

        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.select_avatar_image)), AVATAR_REQUEST_CODE);
            }
        });
        Font.apply(getContext(), rootView);
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == HEADER_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                headerURI = data.getData();
                String picturePath = getPath( getActivity( ).getApplicationContext( ),headerURI);
                //TODO:
                new ResizeImage(picturePath, new BitmapResultListener() {
                    @Override
                    public void onBitMapResult(Bitmap bitmap) {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), headerURI);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
                            thumbHeader = baos.toByteArray();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Picasso.get().load(headerURI).noPlaceholder().fit()
                                .into((imgHeader));
                    }
                }).execute();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getActivity(), getResources().getString(R.string.cancelled), Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == AVATAR_REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK) {

                avatarURI = data.getData();
                String picturePath = getPath( getActivity( ).getApplicationContext( ),avatarURI);
                new ResizeImage(picturePath, new BitmapResultListener() {
                    @Override
                    public void onBitMapResult(Bitmap bitmap) {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), avatarURI);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
                            thumbAvatar = baos.toByteArray();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Picasso.get().load(avatarURI).noPlaceholder().fit()
                                .into((imgAvatar));
                    }
                }).execute();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getActivity(), getResources().getString(R.string.cancelled), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadCurrentUserData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("agencies");
        reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Agency agency = dataSnapshot.getValue(Agency.class);
                    etName.setText(agency.getName());
                    etAddress.setText(agency.getAddress());
                    etDescription.setText(agency.getDescription());
                    etPhoneNumber.setText(agency.getPhoneNumber());
                    if (!agency.getHeader().equals("default"))
                        Picasso.get().load(agency.getHeader()).placeholder(R.drawable.placeholder).into(imgHeader);
                    if (!agency.getAvatar().equals("default"))
                        Picasso.get().load(agency.getAvatar()).placeholder(R.drawable.placeholder).into(imgAvatar);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void confirmUpdates() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
            if(headerURI != null)
            {
                progressDialog.setTitle(getResources().getString(R.string.uploading));
                progressDialog.show();

                final StorageReference ref = storageReference.child("images/agencies/"+ UUID.randomUUID().toString()+"."+Helper.getFileExtension(getContext(),headerURI));

                ref.putBytes(thumbHeader).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                progressDialog.dismiss();
                                headerDownloadUrl =uri.toString();
                                currentAgencyReference.child("header").setValue(headerDownloadUrl);
                            }
                        });
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(),getResources().getString(R.string.failed)+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                        .getTotalByteCount());
                                progressDialog.setMessage(getResources().getString(R.string.uploaded)+(int)progress+"%");
                            }
                        });
            }

        if(avatarURI != null)
        {

            final StorageReference ref = storageReference.child("images/agencies/"+ UUID.randomUUID().toString()+"."+Helper.getFileExtension(getContext(),avatarURI));

            ref.putBytes(thumbAvatar).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            avatarDownloadUrl =uri.toString();
                            currentAgencyReference.child("avatar").setValue(avatarDownloadUrl);
                            progressDialog.dismiss();
                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), getResources().getString(R.string.failed) +e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage(getResources().getString(R.string.uploaded)+(int)progress+"%");
                        }
                    });
        }

        saveToDatabase();

    }

    private void saveToDatabase() {
        currentAgencyReference.child("name").setValue(name);
        currentAgencyReference.child("address").setValue(address);
        currentAgencyReference.child("description").setValue(description);
        currentAgencyReference.child("name").setValue(name);
        currentAgencyReference.child("name").setValue(name);
        currentAgencyReference.child("countryId").setValue(selectedCountry.getCountryId());

        currentAgencyReference.child("countryName").setValue(selectedCountry.getCountryName());
        currentAgencyReference.child("provinceId").setValue(selectedProvince.getProvinceId());
        currentAgencyReference.child("provinceName").setValue(selectedProvince.getProvinceName());
        currentAgencyReference.child("cityId").setValue(selectedCity.getCityId());
        currentAgencyReference.child("cityName").setValue(selectedCity.getCityName());
        currentAgencyReference.child("phoneNumber").setValue(phoneNumber);
        Toast.makeText(getContext(), R.string.updated_successfully, Toast.LENGTH_SHORT).show();
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
}