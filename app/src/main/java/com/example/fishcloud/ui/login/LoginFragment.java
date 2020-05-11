package com.example.fishcloud.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.fishcloud.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class LoginFragment extends Fragment {
    private LoginViewModel loginViewModel;
    private int backButtonCount;

    private final int RC_SIGN_IN = 1;


    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);


        return root;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        backButtonCount = 0;
        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);


        view.findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        final NavController navController = Navigation.findNavController(view);





        final View root = view;
        loginViewModel.authenticationState.observe(getViewLifecycleOwner(),
                new Observer<LoginViewModel.AuthenticationState>() {
                    @Override
                    public void onChanged(LoginViewModel.AuthenticationState authenticationState) {
                        switch (authenticationState) {
                            case AUTHENTICATED:
                                navController.navigate(R.id.action_global_camera_nav);
                                break;
                            case UNAUTHENTICATED:
                                Snackbar.make(root,
                                        "Sign in first",
                                        Snackbar.LENGTH_SHORT
                                ).show();
                                break;
                        }
                    }
                });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
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

    private void signIn() {
        Intent signInIntent = loginViewModel.googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        GoogleSignInAccount account = null;
        try {
            account = completedTask.getResult(ApiException.class);

            System.out.println(account.getEmail());
            System.out.println(account.getDisplayName());
            //  getWebservice(account.getEmail(), account.getDisplayName());

        } catch (ApiException e) {
            Log.w("login fail", "signInResult:failed code=" + e.getStatusCode());
        }

        loginViewModel.authenticate(account);
    }



    private void getWebservice(String email, String displayName) {

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n\t\"name\": \"" + displayName + "\",\n\t\"email\": \"" + email + "\"\n}");
        Request request = new Request.Builder()
                .url("https://fishcloud.azurewebsites.net/users/register-user")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.w("LOG IN", e);
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

}
