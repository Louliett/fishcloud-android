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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
    private List<String> fishName;
    private Spinner fishNameSpinner;
    private ArrayAdapter<String> adapter;
    private EditText length;
    private EditText width;
    private EditText weight;

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
        length = view.findViewById(R.id.txt_length);
        width = view.findViewById(R.id.txt_width);
        weight = view.findViewById(R.id.txt_weight);


        fishDisplay = view.findViewById(R.id.image_display);
        fishDisplay.setVisibility(View.GONE);

        final Button submit = view.findViewById(R.id.button_submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (length.getText().toString().trim().length() == 0) {
                    length.setError(EMPTY_FIELD_ERROR);
                    length.requestFocus();
                    return;
                }

                if (width.getText().toString().trim().length() == 0) {
                    width.setError(EMPTY_FIELD_ERROR);
                    width.requestFocus();
                    return;
                }

                if (weight.getText().toString().trim().length() == 0) {
                    weight.setError(EMPTY_FIELD_ERROR);
                    weight.requestFocus();
                    return;
                }

                if (uploadFile == null) {
                    Toast.makeText(getContext(), "Take photo first", Toast.LENGTH_LONG).show();
                    return;
                }

                correct = true;

                if (!correct)
                    return;

                locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    OnGPS();
                } else {
                    String[] location = getLocation();
                    if (location[0] != null && location[1] != null) {
                        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());


                        String address = "Unidentified";
                        try {
                            List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(location[0]), Double.parseDouble(location[1]), 1);
                            while (addresses.size() == 0) {
                                addresses = geocoder.getFromLocation(Double.parseDouble(location[0]), Double.parseDouble(location[1]), 1);
                            }
                            if (addresses.size() > 0) {
                                address = addresses.get(0).getAdminArea();

                            }
                        } catch (Exception e) {
                            System.out.print(e.getMessage());
                        }
                        System.out.println("Location name: " + address);


                        System.out.println("Selected name:" + fishNameSpinner.getSelectedItem().toString());
                        System.out.println("Weight:" + weight.getText().toString());
                        System.out.println("Length:" + length.getText().toString());
                        System.out.println("Width:" + width.getText().toString());
                        System.out.println("Email " + GoogleSignIn.getLastSignedInAccount(getContext()).getEmail());
                        System.out.println("Time " + new Date().getTime());
                        System.out.println("File name " + uploadFile.getName());

                        uploadFish(fishNameSpinner.getSelectedItem().toString(), Float.parseFloat(weight.getText().toString()), Integer.parseInt(length.getText().toString()),
                                Integer.parseInt(width.getText().toString()), GoogleSignIn.getLastSignedInAccount(getContext()).getEmail(),
                                String.valueOf(new Date().getTime()), uploadFile, location[0], location[1], "Ivosjon");

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
                                loginViewModel.displayName + "_" + timeStamp,
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

        fishNameSpinner = view.findViewById(R.id.spinner);


        getFishNames();


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

            createPhoto(imageBitmap);

            fishDisplay.setVisibility(View.VISIBLE);
        }
    }

    private void createPhoto(Bitmap bitmap) {

        OutputStream os;
        try {
            os = new FileOutputStream(uploadFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
        }
    }


    //
    private void uploadFish(String fishName, float weight, int length, int width,
                            String email, String date, final File image, String lat, String longi, String locName) {

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        // MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("fish_name", fishName)
                .addFormDataPart("kg", String.valueOf(weight))
                .addFormDataPart("length", String.valueOf(length))
                .addFormDataPart("width", String.valueOf(width))
                .addFormDataPart("location_name", locName)
                .addFormDataPart("latitude", lat)
                .addFormDataPart("longitude", longi)
                .addFormDataPart("email", email)
                .addFormDataPart("timestamp", date)
                .addFormDataPart("fishImage", image.getName(),
                        RequestBody.create(MediaType.parse("image/jpeg"),
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
                            Toast.makeText(getContext(), "Upload successful!", Toast.LENGTH_SHORT).show();
                            fishDisplay.setVisibility(View.GONE);
                            uploadFile = null;

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

    private void getFishNames() {

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("https://fishcloud.azurewebsites.net/fish/get-name")
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

                            fishName = parseFishNameResponse(response.body().string());
                            adapter = new ArrayAdapter<String>(getContext(),
                                    android.R.layout.simple_list_item_1, fishName);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            fishNameSpinner.setAdapter(adapter);
                        } catch (IOException ioe) {
                            System.out.println("Error during get body");
                        }
                    }
                });
            }
        });
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

    private List<String> parseFishNameResponse(String jsonString) {
        List<String> fishNames = null;

        System.out.println(jsonString);
        JSONArray obj = null;
        try {
            fishNames = new ArrayList<>();
            obj = new JSONArray(jsonString);

            for (int i = 0; i < obj.length(); i++) {
                String name = obj.getJSONObject(i).getString("name");
                fishNames.add(name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return fishNames;
    }


}
