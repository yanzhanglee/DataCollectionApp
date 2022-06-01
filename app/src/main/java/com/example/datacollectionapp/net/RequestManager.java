package com.example.datacollectionapp.net;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RequestManager {
    private static final String baseUrl = "http://43.128.63.245:8083/portal/";

    public static UploadService getNormalService(){
        UploadService service;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        service = retrofit.create(UploadService.class);
        return service;
    }

    public static UploadService getHeaderService(){
        UploadService service = null;
        try{ Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(getHeaderClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
            service = retrofit.create(UploadService.class);
        }catch (Exception e){
            System.out.print(e.toString());
        }
        return service;
    }

    private static OkHttpClient getHeaderClient() {
        OkHttpClient client;
        client = new OkHttpClient.Builder().addInterceptor(new HeaderInterceptor()).build();
        return client;
    }

    static class HeaderInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Request request1 = request.newBuilder()
//                    .addHeader("device_id", "597823023")
//                    .addHeader("name", "macName")
//                    .addHeader("interceptor", "拦截试试啊")
                    .build();
            Response response = chain.proceed(request1);
            return response;
        }
    }
    static  OkHttpClient createOkHttpClient() {
        final OkHttpClient.Builder httpClient =
                new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                final Request original = chain.request();
                final HttpUrl originalHttpUrl = original.url();
                final HttpUrl url = originalHttpUrl.newBuilder()
                        .build();
                // Request customization: add request headers
                final Request.Builder requestBuilder = original.newBuilder()
                        .url(url);
                final Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        return httpClient.build();
    }


}
