package com.example.fishcloud.ui.login;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.fishcloud.data.login.LoginRepository;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class LoginViewModel extends ViewModel {
    public enum AuthenticationState {
        UNAUTHENTICATED,        // Initial state, the user needs to authenticate
        AUTHENTICATED,          // The user has authenticated successfully
        INVALID_AUTHENTICATION  // Authentication failed
    }

    final public MutableLiveData<AuthenticationState> authenticationState =
            new MutableLiveData<>();
    public String displayName;


    public LoginViewModel() {

        authenticationState.setValue(AuthenticationState.UNAUTHENTICATED);
        displayName = "";
    }


    public void authenticate(GoogleSignInAccount account) {
        if (account != null) {
            authenticationState.setValue(AuthenticationState.AUTHENTICATED);
            displayName = account.getDisplayName();
        } else {
            authenticationState.setValue(AuthenticationState.INVALID_AUTHENTICATION);
        }
    }

    public void revokeAuth() {
        authenticationState.setValue(AuthenticationState.UNAUTHENTICATED);
    }

}
