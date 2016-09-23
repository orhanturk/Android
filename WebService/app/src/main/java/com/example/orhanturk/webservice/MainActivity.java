package com.example.orhanturk.webservice;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    static InputStream veri;
    static  String veri_string;
    static boolean kontrol = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Permission StrictMode
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        GetPersons();
    }
    public void GetPersons()
    {
        ArrayList<String> persons = FromJSONtoArrayList();
        ListView listView1 = (ListView)findViewById(R.id.ListCustomers);
        listView1.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, persons));
    }

    public ArrayList<String> FromJSONtoArrayList() {
        ArrayList<String> listItems = new ArrayList<>();

        String url= "http://192.168.2.131:7777/GetCustomer.ashx";
        List<NameValuePair> param = new ArrayList<>();
        String ResultServer ="";
        param.add(new BasicNameValuePair("key", "c6e025367cf9b699f8d60a9bb8b142a4"));
        param.add(new BasicNameValuePair("CustomerId", ""));
        ResultServer = httpPost(url,"GET", param, 20000);

        String strStatusID = "0", strUserName = "";
        kontrol = true;
        try {
            JSONArray internships = new JSONArray(ResultServer);

            //Loop the Array
            for(int i=0;i < internships.length();i++) {
                JSONObject e = internships.getJSONObject(i);

                strStatusID = e.getString("CustomerId");
                strUserName = e.getString("Name");

                listItems.add(" ID : " + strStatusID + " / " + strUserName);
            }
        } catch(JSONException e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            Log.e("Hata",e.toString());
        }

        return listItems;
    }
    public static  String httpPost(String url, String method,List<NameValuePair> params,int time) {

        //url: post yapılacak adres
        //method: post mu get mi
        //params:post edilecek veriler değişkenler
        //time: sunucudan cevap gelmezse kaç sn sonra uygulama donmadan postun iptal edileceği
        try {

            if (method == "POST") {

                HttpParams httpParameters = new BasicHttpParams();
                int timeout1 = time;
                int timeout2 = time;
                HttpConnectionParams.setConnectionTimeout(httpParameters, timeout1);
                HttpConnectionParams.setSoTimeout(httpParameters, timeout2);
                DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                if (httpResponse != null) {
                    HttpEntity httpEntity = httpResponse.getEntity();
                    veri = httpEntity.getContent();

                }

            } else if (method == "GET") {

                DefaultHttpClient httpClient = new DefaultHttpClient();
                String paramString = URLEncodedUtils.format(params, "utf-8");
                url += "?" + paramString;
                HttpGet httpGet = new HttpGet(url);

                HttpResponse httpResponse = httpClient.execute(httpGet);
                if (httpResponse != null) {
                    HttpEntity httpEntity = httpResponse.getEntity();
                    veri = httpEntity.getContent();
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(veri, "utf-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            veri.close();
            veri_string = sb.toString();
        }
        catch (Exception e) {
            Log.e("Hata ", e.toString());
        }
        return veri_string; // Aldığımız cevabın string halini geri dönüyoruz;
    }
}