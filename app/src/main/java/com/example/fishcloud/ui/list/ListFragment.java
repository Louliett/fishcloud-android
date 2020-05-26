package com.example.fishcloud.ui.list;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fishcloud.R;
import com.example.fishcloud.adapters.FishListAdapter;
import com.example.fishcloud.data.model.Fish;
import com.google.android.gms.auth.api.signin.GoogleSignIn;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FishListAdapter mAdapter;
    private List<Fish> catches;

    public ListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list, container, false);
        // Inflate the layout for this fragment
        catches = new ArrayList<>();
//        catches.add(new Fish("asdf",1,2,3,"sdf","123123"));
        recyclerView = (RecyclerView) root.findViewById(R.id.catches_list);

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        getUserCatches(GoogleSignIn.getLastSignedInAccount(getContext()).getEmail());
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Catches");

    }

    private void getUserCatches(String email) {

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n\t\"email\": \"" + email + "\"\n}");
        Request request = new Request.Builder()
                .url("https://fishcloud.azurewebsites.net/fish/caught-by-user")
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
                            catches = parseCatchesResponse(response.body().string());
                            mAdapter = new FishListAdapter(getContext(), catches);

                            recyclerView.setAdapter(mAdapter);


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private List<Fish> parseCatchesResponse(String jsonString) {
        List<Fish> catches = null;

        System.out.println(jsonString);
        JSONArray obj = null;
        try {
            catches = new ArrayList<>();
            obj = new JSONArray(jsonString);

            for (int i = 0; i < obj.length(); i++) {
                String name = obj.getJSONObject(i).getString("fish_name");
                String weight = obj.getJSONObject(i).getString("kg");
                String length = obj.getJSONObject(i).getString("length");
                String width = obj.getJSONObject(i).getString("width");
                String timestamp = obj.getJSONObject(i).getString("timestamp");
                String url = obj.getJSONObject(i).getString("url");
                String location = obj.getJSONObject(i).getString("location_name");
                String newURL = "https://fishcloud.azurewebsites.net" + url.substring(1);


                catches.add(new Fish(name, Integer.parseInt(length),
                        Integer.parseInt(width), Float.parseFloat(weight),
                        location, timestamp, newURL));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return catches;
    }

}
