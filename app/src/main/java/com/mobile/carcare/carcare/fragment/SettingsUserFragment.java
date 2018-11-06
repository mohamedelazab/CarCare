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
import android.widget.ImageView;
import android.widget.TextView;
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
import com.mobile.carcare.carcare.dialog.ChangeEmailDialog;
import com.mobile.carcare.carcare.interfaces.BitmapResultListener;
import com.mobile.carcare.carcare.model.User;
import com.mobile.carcare.carcare.utils.Font;
import com.mobile.carcare.carcare.utils.Helper;
import com.mobile.carcare.carcare.utils.ResizeImage;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.mobile.carcare.carcare.utils.ResizeImage.getPath;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsUserFragment extends Fragment {

    private static final int AVATAR_USER_REQUEST_CODE = 4;

    @BindView(R.id.tv_username)
    TextView tvUsername;
    @BindView(R.id.tv_email)
    TextView tvEmail;
    @BindView(R.id.image_avatar_user)
    ImageView imgAvatarUser;
    @BindView(R.id.btn_change_user_email)
    Button btnChangeEmail;
    @BindView(R.id.btn_change_user_avatar)
    Button btnChangeAvatar;

    Uri avatarUserUri;
    String avatarDownloadUrl;
    byte[] avatarThumb;
    DatabaseReference userDatabaseReference;

    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        storage =FirebaseStorage.getInstance();
        storageReference =storage.getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =inflater.inflate(R.layout.fragment_settings_user, container, false);
        ButterKnife.bind(this, rootView);
        loadCurrentUser();
        btnChangeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Avatar image"), AVATAR_USER_REQUEST_CODE);
            }
        });

        btnChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeEmailDialog dialog =ChangeEmailDialog.getInstance(getContext());
                dialog.show();
            }
        });
        Font.apply(getContext(), rootView);
        return rootView;
    }

    private void loadCurrentUser() {
        userDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user =dataSnapshot.getValue(User.class);
                    tvUsername.setText(user.getName());
                    tvEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    if (!user.getAvatar().equals("default"))
                        Picasso.get().load(user.getAvatar()).into(imgAvatarUser);
                //}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AVATAR_USER_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                avatarUserUri = data.getData();
                String picturePath = getPath( getActivity( ).getApplicationContext( ),avatarUserUri);
                new ResizeImage(picturePath, new BitmapResultListener() {
                    @Override
                    public void onBitMapResult(Bitmap bitmap) {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), avatarUserUri);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
                            avatarThumb = baos.toByteArray();
                            saveChanges();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Picasso.get().load(avatarUserUri).noPlaceholder().fit()
                                .placeholder(R.drawable.placeholder).into((imgAvatarUser));
                    }
                }).execute();

            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getActivity(), getResources().getString(R.string.cancelled), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveChanges() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        if(avatarUserUri != null)
        {
            progressDialog.setTitle(getResources().getString(R.string.uploading));
            progressDialog.show();

            final StorageReference ref = storageReference.child("images/users/"+ UUID.randomUUID().toString()+"."+Helper.getFileExtension(getContext(),avatarUserUri));

            ref.putBytes(avatarThumb).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            avatarDownloadUrl =uri.toString();
                            userDatabaseReference.child("avatar").setValue(avatarDownloadUrl);
                            progressDialog.dismiss();
                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), getResources().getString(R.string.failed)+" "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
    }
}
