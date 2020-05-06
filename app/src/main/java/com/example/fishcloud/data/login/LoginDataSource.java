package com.example.fishcloud.data.login;

import com.example.fishcloud.data.model.LoggedInUser;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {

        try {

            // TODO: handle loggedInUser authentication


//            OkHttpClient client = new OkHttpClient().newBuilder().build();
//
//            MediaType mediaType = MediaType.parse("application/json");
//
//            RequestBody body = RequestBody.create(mediaType, "{\n\t\"name\": \"lara\",\n\t\"email\": \"lara@gmail.com\"\n}");
//            Request request = new Request.Builder()
//                    .url("https://fishcloud.azurewebsites.net/users/create-user")
//                    .method("POST", body)
//                    .addHeader("Content-Type", "application/json")
//                    .addHeader("Cookie", "ARRAffinity=0de3dfe0f956df48d150a3cc8506c1efae6a5e5a5e673891639f0556803020f3")
//                    .build();
//            Response response = client.newCall(request).execute();

            LoggedInUser loggedInUser =
                    new LoggedInUser(
                            java.util.UUID.randomUUID().toString(),
                            "Jane Doe");
            return new Result.Success<>(loggedInUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}
