package com.malviya.demohttpclient;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.ExceptionUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;

import syncNetwork.EasySSLSocketFactory;
import syncNetwork.TLSSocketFactory;

/**
 * Created by 23508 on 3/8/2017.
 */

public class NetworkAsyncConnection extends AsyncTask<String, Void ,String> {


    private DefaultHttpClient httpclient;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String[] params) {
        //String resp = getNetwork(params[0]);
        String resp = "";
        try {
            resp = getRespByHttpURLConnection(params[0]);
        } catch (Exception e) {
            Log.e("Exception",e.getMessage());
        }
        return resp;
    }


    @Override
    protected void onPostExecute(String resp) {
        super.onPostExecute(resp);
        Log.i("onPostExecute",resp);
    }




    public String getNetwork(String url) {
        try {
            SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            schemeRegistry.register(new Scheme("https",new EasySSLSocketFactory(), 443));
            HttpParams params = new BasicHttpParams();
            params.setParameter("http.conn-manager.max-total", Integer.valueOf(30));
            params.setParameter("http.conn-manager.max-per-route", new ConnPerRouteBean(30));
            params.setParameter("http.protocol.expect-continue", Boolean.valueOf(false));
            params.setParameter("http.protocol.cookie-policy", "rfc2965");
            HttpProtocolParams.setContentCharset(params, "UTF-8");
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setUserAgent(params, "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            HttpConnectionParams.setConnectionTimeout(params, 5000);
            HttpConnectionParams.setSoTimeout(params, 5000);
           // this.httpclient = new DefaultHttpClient(new SingleClientConnManager(params, schemeRegistry), params);
            httpclient = new DefaultHttpClient();
            return EntityUtils.toString(this.httpclient.execute(new HttpGet(url)).getEntity());
        } catch (Exception e) {
            Log.e("Exception ",e.getMessage());
            return null;
        }
    }



    public String getRespByHttpURLConnection(String pURL) throws NoSuchAlgorithmException, KeyManagementException, IOException {
        URL url = new URL(pURL);
        //resolved by javax.net.ssl.SSLException: Connection closed by peer
        TLSSocketFactory tlsSocketFactory = new TLSSocketFactory();
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setSSLSocketFactory(tlsSocketFactory);
        conn.setRequestMethod("GET");
        InputStream inputStream = conn.getInputStream();
        StringBuffer buffer = new StringBuffer();
        if (inputStream == null) {
            return null;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line + "\n");
        }

        if (buffer.length() == 0) {
            return null;
        }

        return buffer.toString();
    }

}
