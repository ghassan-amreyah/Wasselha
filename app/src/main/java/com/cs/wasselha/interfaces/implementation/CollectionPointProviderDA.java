package com.cs.wasselha.interfaces.implementation;
import com.cs.wasselha.model.CollectionPointProvider;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.*;

public class CollectionPointProviderDA {
    OkHttpClient client = new OkHttpClient();

    public CollectionPointProvider getCollectionPointProviderById(int id) throws IOException {
        String url = "http://176.119.254.198:8000/wasselha/collection-point-providers/" + id + "/";
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            Gson gson = new Gson();
            return gson.fromJson(response.body().charStream(), CollectionPointProvider.class);
        }
    }

    public CollectionPointProvider updateCollectionPointProvider(CollectionPointProvider collectionPointProvider) throws IOException {
        String url = "http://176.119.254.198:8000/wasselha/collection-point-providers/" + collectionPointProvider.getId() + "/";
        Gson gson = new Gson();
        String json = gson.toJson(collectionPointProvider);

        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            return gson.fromJson(response.body().charStream(), CollectionPointProvider.class);
        }
    }
}
