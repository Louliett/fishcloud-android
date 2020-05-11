package com.example.fishcloud.ui.camera;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.fishcloud.R;
import com.example.fishcloud.ui.login.LoginViewModel;
import com.google.android.material.snackbar.Snackbar;


public class CameraFragment extends Fragment {
    private LoginViewModel loginViewModel;
    private int backButtonCount;

    public CameraFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        backButtonCount = 0;


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

        final View root = view;

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

}
