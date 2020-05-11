package com.example.fishcloud.ui.login;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.fishcloud.data.login.LoginRepository;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

public class LoginViewModel extends ViewModel {
    public enum AuthenticationState {
        UNAUTHENTICATED,        // Initial state, the user needs to authenticate
        AUTHENTICATED,          // The user has authenticated successfully
        INVALID_AUTHENTICATION  // Authentication failed
    }

    final public MutableLiveData<AuthenticationState> authenticationState =
            new MutableLiveData<>();
    public String displayName;
    public GoogleSignInClient googleSignInClient;


    public LoginViewModel() {
        authenticationState.setValue(AuthenticationState.UNAUTHENTICATED);
        displayName = "";

    }

    public void setGoogleSignInClient(GoogleSignInClient googleSignInClient) {
        this.googleSignInClient = googleSignInClient;
    }

    public void authenticate(GoogleSignInAccount account) {
        if (account != null) {
            authenticationState.setValue(AuthenticationState.AUTHENTICATED);
            displayName = account.getDisplayName();
        } else {
            authenticationState.setValue(AuthenticationState.UNAUTHENTICATED);
        }
    }

    public void revokeAuth() {
        authenticationState.setValue(AuthenticationState.UNAUTHENTICATED);
    }

}
