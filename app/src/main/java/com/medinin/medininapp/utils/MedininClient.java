package com.medinin.medininapp.utils;

import android.provider.Settings;

import com.medinin.medininapp.Myapp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Dispatcher;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Bharath on 06/23/2019.
 * It is a singleton class, We will have only one instance across the app.
 */

public class MedininClient {
    public static OkHttpClient okHttpClient;

    /**
     * Private constructor to avoid duplicate instance.
     */
    private MedininClient() {
    }

    public static OkHttpClient getClient() {
        if (okHttpClient == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .header("Content-Type", "application/json")
                            //  .header("User-Agent", "iPad|"+getDeviceId())
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(request);
                }
            });
            Dispatcher dispatcher = new Dispatcher();
            dispatcher.setMaxRequests(5);
            httpClient.connectTimeout(10, TimeUnit.MINUTES)
                    .readTimeout(10, TimeUnit.MINUTES)
                    .dispatcher(dispatcher)
                    .socketFactory(new CustomSocketFactory(new OkHttpClient().socketFactory()));


            okHttpClient = httpClient.build();
        }
        return okHttpClient;
    }

    public static String getDeviceId() {
        String deviceId = Settings.Secure.getString(Myapp.getInstance().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return deviceId;
    }




    public static class CustomSocketFactory extends SocketFactory {
        public static final int SEND_WINDOW_SIZE_BYTES = (int) Math.pow(2, 16);
        private SocketFactory mDefaultSocketFactory;

        public CustomSocketFactory(SocketFactory defaultSocketFactory) {
            if (defaultSocketFactory == null) {
                mDefaultSocketFactory = SocketFactory.getDefault();
            } else {
                mDefaultSocketFactory = defaultSocketFactory;
            }
        }

        @Override
        public Socket createSocket(String host, int port) throws IOException {
            return setSocketOptions(mDefaultSocketFactory.createSocket(host, port));
        }

        @Override
        public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException {
            return setSocketOptions(mDefaultSocketFactory.createSocket(host, port, localHost, localPort));
        }

        @Override
        public Socket createSocket(InetAddress host, int port) throws IOException {
            return setSocketOptions(mDefaultSocketFactory.createSocket(host, port));
        }

        @Override
        public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
            return setSocketOptions(mDefaultSocketFactory.createSocket(address, port, localAddress, localPort));
        }

        @Override
        public Socket createSocket() throws IOException {
            return setSocketOptions(mDefaultSocketFactory.createSocket());
        }

        private Socket setSocketOptions(Socket socket) throws SocketException {
            socket.setSendBufferSize(SEND_WINDOW_SIZE_BYTES);

            return socket;
        }
    }
}
