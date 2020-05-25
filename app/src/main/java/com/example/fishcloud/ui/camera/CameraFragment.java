package com.example.fishcloud.ui.camera;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.fishcloud.R;
import com.example.fishcloud.ui.login.LoginViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;


public class CameraFragment extends Fragment {
    private LoginViewModel loginViewModel;
    private int backButtonCount;
    private final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView fishDisplay;
    private LocationManager locationManager;
    private int REQUEST_LOCATION = 1;
    private final String EMPTY_FIELD_ERROR = "Field cannot be empty";
    private boolean correct;
    private File uploadFile;

    public CameraFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        backButtonCount = 0;

        Button capture = view.findViewById(R.id.button_capture);
        final EditText length = view.findViewById(R.id.txt_length);
        final EditText width  = view.findViewById(R.id.txt_width);
        final EditText weight = view.findViewById(R.id.txt_weight);
        final Spinner fishNameSpinner = view.findViewById(R.id.spinner);

        try {

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                    android.R.layout.simple_list_item_1, getFishNames());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            fishNameSpinner.setAdapter(adapter);
        }catch (Exception e){
            e.printStackTrace();
        }

        fishDisplay = view.findViewById(R.id.image_display);
        fishDisplay.setVisibility(View.GONE);

        final Button submit = view.findViewById(R.id.button_submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (length.getText().toString().trim().length() == 0){
                    length.setError(EMPTY_FIELD_ERROR);
                    length.requestFocus();
                    return;
                }

                if( width.getText().toString().trim().length() == 0){
                    width.setError(EMPTY_FIELD_ERROR);
                    width.requestFocus();
                    return;
                }

                if (weight.getText().toString().trim().length() == 0){
                    weight.setError(EMPTY_FIELD_ERROR);
                    weight.requestFocus();
                  return;
                }

                if(uploadFile == null) {
                    return;
                }

                correct = true;

                if(!correct)
                    return;

                locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    OnGPS();
                } else {
                    String[] location = getLocation();
                    Toast.makeText(getContext(), "Latitude : " + location[0] + " Longitude: " + location[1], Toast.LENGTH_LONG).show();
                    if (location[0] != null && location[1] != null) {
                        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                        //List<Address> addresses =geocoder.getFromLocation(latitude, longitude, 1);

                        try {
                            List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(location[0]), Double.parseDouble(location[1]), 1);
                            String address = addresses.get(0).getSubLocality();
                            String cityName = addresses.get(0).getLocality();
                            String stateName = addresses.get(0).getAdminArea();

                            uploadFish(fishNameSpinner.getSelectedItem().toString(),Float.parseFloat(weight.toString()),
                                    Integer.parseInt(length.toString()),Integer.parseInt(width.toString()),"",
                                    new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()),uploadFile );

                            Toast.makeText(getContext(), "Address : " + address + " City: " +cityName, Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        });

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    try {
                        uploadFile = File.createTempFile(
                                loginViewModel.displayName + " " + timeStamp,
                                ".jpg"
                        );
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                    }
                }

            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final View root = view;


        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Welcome, " + loginViewModel.displayName);
        final NavController navController = Navigation.findNavController(view);
        loginViewModel.authenticationState.observe(getViewLifecycleOwner(),
                new Observer<LoginViewModel.AuthenticationState>() {
                    @Override
                    public void onChanged(LoginViewModel.AuthenticationState authenticationState) {
                        switch (authenticationState) {
                            case AUTHENTICATED:

                                break;
                            case UNAUTHENTICATED:
                                navController.navigate(R.id.login_nav);
                                break;
                        }
                    }
                });


        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        backButtonCount++;
                        if (backButtonCount > 1) {
                            requireActivity().finish();
                        } else {
                            Snackbar.make(root,
                                    "Press again to exit",
                                    Snackbar.LENGTH_SHORT
                            ).show();
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            fishDisplay.setImageBitmap(imageBitmap);
            fishDisplay.setVisibility(View.VISIBLE);
        }
    }


    //
    private void uploadFish(String fishName, float weight, int length, int width,
                            String email, String date, File image) {

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("fish_name", fishName)
                .addFormDataPart("kg", String.valueOf(weight))
                .addFormDataPart("length", String.valueOf(length))
                .addFormDataPart("width", String.valueOf(width))
                .addFormDataPart("location_name", "Ivosjon")
                .addFormDataPart("latitude", "56.111159")
                .addFormDataPart("longitude", "14.440419")
                .addFormDataPart("email", email)
                .addFormDataPart("timestamp", date)
                .addFormDataPart("fishImage", image.getName(),
                        RequestBody.create(MediaType.parse("application/octet-stream"),
                                image))
                .build();

        Request request = new Request.Builder()
                .url("https://fishcloud.azurewebsites.net/fish/upload-fish")
                .method("POST", body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.w("UPLOAD FAILURE", e);
                    }
                });
            }


            @Override
            public void onResponse(Call call, final Response response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            System.out.println(response.body().string());
                        } catch (IOException ioe) {
                            System.out.println("Error during get body");
                        }
                    }
                });
            }
        });
    }

    private String[] getLocationNames() {

        String[] retVal = null;

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("https://fishcloud.azurewebsites.net/locations/get-name")
                .method("GET", null)
                .build();
        try {
            Response response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return retVal;
    }

    private List<String> getFishNames() {

        List<String> retVal = new ArrayList<>();

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("https://fishcloud.azurewebsites.net/locations/get-name")
                .method("GET", null)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.w("FAILED RETRIEVING", e);
                    }
                });
            }


            @Override
            public void onResponse(Call call, final Response response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            System.out.println(response.body().string());
                        } catch (IOException ioe) {
                            System.out.println("Error during get body");
                        }
                    }
                });
            }
        });
        return retVal;
    }

    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private String[] getLocation() {
        String[] val = new String[2];

        if (ActivityCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                double lat = locationGPS.getLatitude();
                double longi = locationGPS.getLongitude();
                val[0] = String.valueOf(lat);
                val[1] = String.valueOf(longi);

            } else {
                Toast.makeText(getContext(), "Unable to find location.", Toast.LENGTH_SHORT).show();
            }
        }
        return val;
    }


}
