package com.blaze.api;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;

/**
 * Created by dzmitrykashlach on 9/12/14.
 */
public class BzmHttpClient {
    DefaultHttpClient httpClient;
    String serverName;
    String userName;
    String password;
    int serverPort;


  public BzmHttpClient(String serverName,String userName,String password,int serverPort){
      this.serverName=serverName;
      this.userName=userName;
      this.password=password;
      this.serverPort=serverPort;
      this.httpClient=new DefaultHttpClient();
  }


    public void configureProxy(){
        // Configure the proxy if necessary
        if (this.serverName != null && !this.serverName.isEmpty() && this.serverPort > 0) {
            if (this.userName != null && !this.userName.isEmpty()){
                Credentials cred = new UsernamePasswordCredentials(this.userName, password);
                httpClient.getCredentialsProvider().setCredentials(new AuthScope(serverName, serverPort), cred);
            }
            HttpHost proxyHost = new HttpHost(serverName, serverPort);
            httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxyHost);
        }
    }

    public HttpResponse getResponse(String url, JSONObject data) throws IOException {

//        logger.message("Requesting : " + url);
        if(data!=null){
//            logger.message("Request data : " + data.toString());
        }
        HttpPost postRequest = new HttpPost(url);
        postRequest.setHeader("Accept", "application/json");
        postRequest.setHeader("Content-type", "application/json; charset=UTF-8");

        if (data != null) {
            postRequest.setEntity(new StringEntity(data.toString()));
        }

        HttpResponse response = null;
        try {
            response = this.httpClient.execute(postRequest);

            int statusCode = response.getStatusLine().getStatusCode();
            String error = response.getStatusLine().getReasonPhrase();
            if ((statusCode >= 300) || (statusCode < 200)) {
                throw new RuntimeException(String.format("Failed : %d %s", statusCode, error));
            }
        }
        catch (UnknownHostException uhe) {
            System.err.format("Unknown host '" + this.serverName + "', check proxy settings! \n");
        }
        catch (Exception e) {
            System.err.format("Wrong response: %s\n", e);
        }

        return response;
    }


    public HttpResponse getResponseForFileUpload(String url, File file) throws IOException {

//        logger.message("Requesting : " + url);
        HttpPost postRequest = new HttpPost(url);
        postRequest.setHeader("Accept", "application/json");
        postRequest.setHeader("Content-type", "application/json; charset=UTF-8");

        if (file != null) {
            postRequest.setEntity(new FileEntity(file, "text/plain; charset=\"UTF-8\""));
        }

        HttpResponse response = null;
        try {
            response = this.httpClient.execute(postRequest);

            int statusCode = response.getStatusLine().getStatusCode();
            String error = response.getStatusLine().getReasonPhrase();
            if ((statusCode >= 300) || (statusCode < 200)) {
                throw new RuntimeException(String.format("Failed : %d %s", statusCode, error));
            }
        } catch (Exception e) {
            System.err.format("Wrong response: %s\n", e);
        }

        return response;
    }

    public JSONObject getJson(String url, JSONObject data) {
        JSONObject jo = null;
        try {
            HttpResponse response = this.getResponse(url, data);
            if (response != null) {
                String output = EntityUtils.toString(response.getEntity());
//                logger.message(output);
                jo = new JSONObject(output);
            }
        } catch (IOException e) {
            e.printStackTrace();
//            logger.message("error decoding Json " + e);
        } catch (JSONException e) {
            e.printStackTrace();
//            logger.message("error decoding Json " + e);
        }
        return jo;
    }


    @SuppressWarnings("deprecation")

    public JSONObject getJsonForFileUpload(String url, File file) {
        JSONObject jo = null;
        try {
            HttpResponse response = this.getResponseForFileUpload(url, file);
            if (response != null) {
                String output = EntityUtils.toString(response.getEntity());
//                logger.message(output);
                jo = new JSONObject(output);
            }
        } catch (IOException e) {
            e.printStackTrace();
//            logger.message("error decoding Json " + e);
        } catch (JSONException e) {
            e.printStackTrace();
//            logger.message("error decoding Json " + e);
        }
        return jo;
    }


}
