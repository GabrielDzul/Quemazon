package com.kokomusoft.quemazon;


import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kokomusoft.quemazon.model.Report;
import com.rilixtech.materialfancybutton.MaterialFancyButton;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddReportFragment extends Fragment {
    RelativeLayout reportStepOneContainer;
    RelativeLayout reportStepTwoContainer;
    RelativeLayout reportStepThreeContainer;
    RelativeLayout reportStepFourContainer;
    private ProgressBar progressBar;
    //FloatingTextButton currentLocationBtn;
    MaterialFancyButton currentLocationBtn;
    private MaterialFancyButton lowIntensityBtn;
    private MaterialFancyButton mediumIntensityBtn;
    private MaterialFancyButton highIntensityBtn;
    private MaterialFancyButton openCameraBtn;
    private MaterialFancyButton toStepFourBtn;
    private MaterialFancyButton closeReportBtn;
    private ImageView cameraImage;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 112;
    private static final int CAMERA_REQUEST_CODE = 215;
    private static final String INTENSITY_LOW = "Leve";
    private static final String INTENSITY_MEDIUM = "Moderado";
    private static final String INTENSITY_HIGH = "Fuera de control";

    private boolean locationPermitionGranted = false;
    private boolean gpsEnabled = false;
    private boolean networkAvailable = false;
    Date currentTime = Calendar.getInstance().getTime();
    private FusedLocationProviderClient fusedLocationProviderClient;
    private StorageReference storageReference;
    //private FirebaseStorage storage = FirebaseStorage.getInstance();
    private LatLng userLatLng;
    private Report report;



    public AddReportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_report, container, false);
        // Inflate the layout for this fragment
        //getActivity().getActionBar().setTitle("Nuevo Reporte");
        getLocationPermission();

        if (locationPermitionGranted){
            checkGPSEnabled();
        }
        //FirebaseApp.initializeApp(getActivity());
        storageReference = FirebaseStorage.getInstance().getReference();
        report = new Report();
        reportStepOneContainer = rootView.findViewById(R.id.reportStepOneContainer);
        reportStepTwoContainer = rootView.findViewById(R.id.reportStepTwoContainer);
        reportStepThreeContainer = rootView.findViewById(R.id.reportStepThreeContainer);
        reportStepFourContainer = rootView.findViewById(R.id.reportStepFourContainer);
        reportStepTwoContainer.setVisibility(View.GONE);
        reportStepThreeContainer.setVisibility(View.GONE);
        reportStepFourContainer.setVisibility(View.GONE);
        lowIntensityBtn = rootView.findViewById(R.id.lowIntensityBtn);
        mediumIntensityBtn = rootView.findViewById(R.id.mediumIntensityBtn);
        highIntensityBtn = rootView.findViewById(R.id.highIntensityBtn);
        progressBar = rootView.findViewById(R.id.progressbar);
        openCameraBtn = rootView.findViewById(R.id.openCameraBtn);
        toStepFourBtn = rootView.findViewById(R.id.toStepFourBtn);
        closeReportBtn  = rootView.findViewById(R.id.closeReportBtn);
        cameraImage = rootView.findViewById(R.id.cameraImage);
        progressBar.setVisibility(View.INVISIBLE);

        currentLocationBtn = rootView.findViewById(R.id.currentLocationBtn);
        currentLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateLayout(reportStepOneContainer, reportStepTwoContainer);
            }

        });

        lowIntensityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                report.setIntensity(INTENSITY_LOW);
                animateLayout(reportStepTwoContainer, reportStepThreeContainer);
                //Log.d("Report completed", report.toString());
            }
        });

        mediumIntensityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                report.setIntensity(INTENSITY_MEDIUM);
                animateLayout(reportStepTwoContainer, reportStepThreeContainer);
                //Log.d("Report completed", report.toString());
            }
        });

        highIntensityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                report.setIntensity(INTENSITY_HIGH);
                animateLayout(reportStepTwoContainer, reportStepThreeContainer);

            }
        });

        openCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            }
        });

        toStepFourBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Report completed", report.toString());
                //TODO: Add method to send the report to the api
                animateLayout(reportStepThreeContainer, reportStepFourContainer);
            }
        });

        closeReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });


        return rootView;


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == getActivity().RESULT_OK){
            progressBar.setVisibility(View.VISIBLE);
            //Uri uri = data.getData();
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] dataBAOS = baos.toByteArray();
            //Log.d("Data content", data.getData().toString());
            StorageReference filePath = storageReference.child("FireImages").child("Image" + currentTime);
            //Bitmap bitmap = null;


            filePath.putBytes(dataBAOS).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressBar.setVisibility(View.INVISIBLE);
                    //openCameraBtn.setVisibility(View.GONE);
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    Picasso.get().load(downloadUri).fit().into(cameraImage);
                    report.setImageUrl(downloadUri.toString());
                    Log.d("Report completed", report.toString());
                }
            });
        }
    }

    private void animateLayout(final RelativeLayout layoutToReplace, RelativeLayout layoutToShow) {
        //reportStepOneContainer.setVisibility(View.GONE);
        getDeviceLocation();
        layoutToShow.setVisibility(View.VISIBLE);
        layoutToShow.setAlpha(0.0f);
        layoutToShow.animate()
                .translationY(layoutToShow.getHeight())
                .setDuration(1000)
                .alpha(1.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        layoutToReplace.setVisibility(View.GONE);
                    }
                });
    }

    private void getDeviceLocation(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        try{
            if (locationPermitionGranted){
                Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()){
                            Location currentLocation = (Location) task.getResult();
                            userLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            report.setLatLng(userLatLng);
                            Log.d("OnCompleteTask", "Found Location" + userLatLng);
                        }else{
                            Log.d("OnCompleteTask", "Current Location is null");
                            Toast.makeText(getActivity(), "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        }catch (SecurityException e){
            Log.e("getDeviceLocation", e.getMessage());
        }
    }

    private void getLocationPermission(){
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        if (ContextCompat.checkSelfPermission(this.getActivity().getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationPermitionGranted = true;
            }else{
                ActivityCompat.requestPermissions(getActivity(), permissions,LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(getActivity(), permissions,LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermitionGranted = false;

        switch (requestCode){
            case    LOCATION_PERMISSION_REQUEST_CODE:{
                if (grantResults.length > 0){
                    for (int i = 0; i < grantResults.length; i++){
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            locationPermitionGranted = false;
                            return;
                        }
                    }
                    locationPermitionGranted = true;
                    //Init map
                }
            }
        }
    }

    private void checkGPSEnabled(){
        LocationManager lm = (LocationManager)getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch (Exception ex){}
        try{
            networkAvailable = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch (Exception ex){}
        if(!gpsEnabled && !networkAvailable){
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setMessage(getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                }
            });
            dialog.setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
        }

    }

}
