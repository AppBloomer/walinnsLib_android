package com.walinns.walinnsapi;

import android.content.Context;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by walinnsinnovation on 30/12/17.
 */

public class APIClient {
    static WAPref sharedPref;
    private static final WALog logger = WALog.getLogger();
    public Context mContext;
    public String mUrl;
    public JSONObject mjsonObject;
    static String URL="http://ec2-18-218-53-112.us-east-2.compute.amazonaws.com:8080/";
    protected WALifeCycle mWalinnsactivitylifecycle;

    public APIClient(Context context){
        sharedPref=new WAPref(context);
    }

    public APIClient(Context context,String url,JSONObject jsonObject){
        this.mContext=context;
        this.mUrl=url;
        this.mjsonObject=jsonObject;
        Connection();

    }
    //  private static Retrofit retrofit = null;

    //    static Retrofit getClient() {
//
//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
//        httpClient.addInterceptor(new Interceptor() {
//            @Override
//            public Response intercept(Interceptor.Chain chain) throws IOException {
//                Request original = chain.request();
//                //@Headers("authorization:qwertyuiop123")
//                // Request customization: add request headers
//                logger.e("WalinnsTrackerClient", "Token:"+sharedPref.getValue(SharedPref.project_token));
//                if(!sharedPref.getValue(SharedPref.project_token).isEmpty()&&sharedPref.getValue(SharedPref.project_token)!=null) {
//                    Request.Builder requestBuilder = original.newBuilder()
//                            .header("authorization", sharedPref.getValue(SharedPref.project_token)); // <-- this is the important line
//                    Request request = requestBuilder.build();
//                    return chain.proceed(request);
//                }else {
//                    logger.e("WalinnsTrackerClient","Please put valid project token");
//                    return null;
//                }
//
//            }
//        });
//        OkHttpClient client = httpClient.build();
//
//
//        retrofit = new Retrofit.Builder()
//                .baseUrl(url)
//                .addConverterFactory(ScalarsConverterFactory.create())
//                .addConverterFactory(GsonConverterFactory.create())
//                .client(client)
//                .build();
//
//
//
//        return retrofit;
//    }
    private void Connection(){
        try{
            logger.e("WalinnsTrackerClient","Request_data"+ mjsonObject.toString());
            java.net.URL url = new URL(URL+mUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.addRequestProperty("CONTENT_TYPE", "application/json");
            conn.addRequestProperty("Authorization", sharedPref.getValue(WAPref.project_token));
            conn.setDoInput(true);
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(mjsonObject));

            writer.flush();
            writer.close();
            os.close();

            logger.e("Http_connection_request_data",conn.getRequestMethod()+"..."+conn.toString()+"..."+sharedPref.getValue(WAPref.project_token));

            int responseCode=conn.getResponseCode();


            if (responseCode == HttpsURLConnection.HTTP_OK) {
                if(mUrl.equals("devices")){
                    logger.e("WalinnsTrackerClient","life_cycle_method_detected"+mUrl);

                    WalinnsAPIClient walinnsTrackerClient=new WalinnsAPIClient(mContext);
                    walinnsTrackerClient.lifeCycle();
                }
                logger.e("WalinnsTrackerHttpConnection","Sucess");

            }else {
                logger.e("WalinnsTrackerHttpConnection","Fail");

            }
        }
        catch(Exception e){
            logger.e("WalinnsTrackerHttpConnection","Exce"+e);

        }
    }
    private String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }

}
