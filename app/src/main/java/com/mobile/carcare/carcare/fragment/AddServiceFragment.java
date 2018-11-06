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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mobile.carcare.carcare.R;
import com.mobile.carcare.carcare.utils.Font;
import com.mobile.carcare.carcare.utils.Helper;
import com.mobile.carcare.carcare.utils.MyPreferences;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddServiceFragment extends Fragment {

    private static final int PICK_IMAGE = 4;
    @BindView(R.id.text_update_hint)
    TextView tvUploadHint;

    @BindView(R.id.img_header_service_update)
    ImageView imageViewUpdate;

    @BindView(R.id.et_item_service_title_update)
    EditText etTitleUpdate;

    @BindView(R.id.et_item_service_description_update)
    EditText etDescriptionUpdate;

    @BindView(R.id.et_item_service_price_update)
    EditText etPriceUpdate;

    @BindView(R.id.btn_finish_update)
    Button btnFinishUpdate;

    Uri selectedImageURI;
    byte[] thumbByte;
    String agencyKey;
    String imageDownloadUrl;

    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage =FirebaseStorage.getInstance();
        storageReference =storage.getReference();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =inflater.inflate(R.layout.fragment_add, container, false);
        ButterKnife.bind(this, rootView);
        agencyKey =(getArguments()).getString("Agency_Key");

        btnFinishUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serviceTitle =etTitleUpdate.getText().toString().trim();
                String serviceDescription =etDescriptionUpdate.getText().toString().trim();
                String servicePrice =etPriceUpdate.getText().toString().trim();
                if (serviceTitle.isEmpty()){
                    etTitleUpdate.setError(getResources().getString(R.string.required));
                }
                else if(serviceDescription.isEmpty()){
                    etDescriptionUpdate.setError(getResources().getString(R.string.required));
                }
                else if(servicePrice.isEmpty()){
                    etPriceUpdate.setError(getResources().getString(R.string.required));
                }

                else {
                    uploadImageAndData(serviceTitle,serviceDescription,servicePrice);
                }
            }
        });

        imageViewUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });
        Font.apply(getContext(), rootView);
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {

                selectedImageURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), selectedImageURI);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
                    thumbByte = baos.toByteArray();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                Picasso.get().load(selectedImageURI).noPlaceholder().fit()
                        .into((imageViewUpdate));
                tvUploadHint.setVisibility(View.GONE);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getActivity(), getResources().getString(R.string.cancelled), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addService(final String serviceTitle, String serviceDescription, String servicePrice, String serviceImg) {
        String serviceId = "service_"+UUID.randomUUID().toString();
        new MyPreferences(getContext()).setUserType(2);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("agencies").child(agencyKey).child("services").child(serviceId);
        HashMap<String, String> serviceData = new HashMap<>();
        serviceData.put("id",serviceId);
        serviceData.put("serviceTitle", serviceTitle);
        serviceData.put("serviceDescription", serviceDescription);
        serviceData.put("servicePrice",servicePrice);
        serviceData.put("serviceImg", serviceImg);
        databaseReference.setValue(serviceData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    getActivity().getSupportFragmentManager().popBackStack();
                    Toast.makeText(getContext(), R.string.service_added_successfully, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadImageAndData(final String serviceTitle, final String serviceDescription, final String servicePrice) {

        if(selectedImageURI != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle(getString(R.string.uploading));
            progressDialog.show();

            final StorageReference ref = storageReference.child("images/services/"+ UUID.randomUUID().toString()+"."+Helper.getFileExtension(getContext(),selectedImageURI));

            ref.putBytes(thumbByte).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageDownloadUrl =uri.toString();
                            addService(serviceTitle,serviceDescription,servicePrice,imageDownloadUrl);
                            progressDialog.dismiss();
                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), getString(R.string.failed)+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage(getString(R.string.uploaded)+(int)progress+"%");
                        }
                    });
        }
    }

}