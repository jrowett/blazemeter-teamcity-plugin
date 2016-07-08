/**
 Copyright 2016 BlazeMeter Inc.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 http://www.apache.org/licenses/LICENSE-2.0
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.blaze.api;

import com.blaze.runner.Constants;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.IOException;

public class BzmHttpWrapper {
    private Logger logger = (Logger) LoggerFactory.getLogger("com.blazemeter");
    private boolean useProxy=false;
    private String proxyHost=null;
    private int proxyPort=0;
    private String proxyUser=null;
    private String proxyPass=null;

    public enum Method {GET, POST, PUT}

    private transient CloseableHttpClient httpClient = null;
    private HttpHost proxy=null;


    public BzmHttpWrapper() {
        this.httpClient = HttpClients.createDefault();
        useProxy=Boolean.parseBoolean(System.getProperty(Constants.USE_PROXY));
        proxyHost=System.getProperty(Constants.PROXY_HOST);

        try{
            this.proxyPort=Integer.parseInt(System.getProperty(Constants.PROXY_PORT));
        }catch (NumberFormatException nfe){
            logger.warn("Failed to read http.proxyPort: ",nfe);
        }

        if(useProxy&&!StringUtils.isBlank(this.proxyHost)){
            logger.info("Using proxy="+String.valueOf(this.useProxy));
            this.proxy=new HttpHost(proxyHost,proxyPort);
            logger.info("Using proxy.host="+this.proxyHost);
            logger.info("Using proxy.port="+this.proxyPort);

            this.proxyUser=System.getProperty(Constants.PROXY_USER);
            logger.info("Using proxy.user="+this.proxyUser);
            this.proxyPass=System.getProperty(Constants.PROXY_PASS);
            logger.info("Using proxy.pass="+this.proxyPass);

            if(!StringUtils.isEmpty(this.proxyUser)&&!StringUtils.isEmpty(this.proxyPass)){
                Credentials cr=new UsernamePasswordCredentials(proxyUser, proxyPass);
                AuthScope aus=new AuthScope(proxyHost, proxyPort);
                CredentialsProvider credsProvider = new BasicCredentialsProvider();
                credsProvider.setCredentials(aus,cr);
                this.httpClient = HttpClients.custom().setProxy(proxy).setDefaultCredentialsProvider(credsProvider).build();
            }else {
                this.httpClient = HttpClients.custom().setProxy(proxy).build();
            }
        }else {
            logger.info("Using proxy="+String.valueOf(this.useProxy));
        }
    }

    public HttpResponse httpResponse(String url, JSONObject data, Method method) throws IOException {
        if (StringUtils.isBlank(url)) return null;
        if (logger.isDebugEnabled())
            logger.debug("Requesting : " + url.substring(0,url.indexOf("?")+14));
        HttpResponse response = null;
        HttpRequestBase request = null;

        try {
            if (method == Method.GET) {
                request = new HttpGet(url);
            } else if (method == Method.POST) {
                request = new HttpPost(url);
                if (data != null) {
                    ((HttpPost) request).setEntity(new StringEntity(data.toString()));
                }
            } else if (method == Method.PUT) {
                request = new HttpPut(url);
                if (data != null) {
                    ((HttpPut) request).setEntity(new StringEntity(data.toString()));
                }
            }
            else {
                throw new RuntimeException("Unsupported method: " + method.toString());
            }
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json; charset=UTF-8");
            response = this.httpClient.execute(request);


            if (response == null || response.getStatusLine() == null) {
                if(logger.isDebugEnabled())
                    logger.debug("Erroneous response (Probably null) for url: \n", url);
                response = null;
            }
        } catch (Throwable thr) {
            if(logger.isDebugEnabled())
                logger.debug("Problems with creating and sending request: \n", thr);
        }
        return response;
    }


    public <T> T response(String url, JSONObject data, Method method, Class<T> returnType){
        T returnObj=null;
        JSONObject jo = null;
        String output = null;
        HttpResponse response = null;
        try {
            response = httpResponse(url, data, method);
            if (response != null) {
                output = EntityUtils.toString(response.getEntity());
                if (logger.isDebugEnabled())
                    logger.debug("Received object from server: " + output);
                if (output.isEmpty()) {
                    throw new IOException();
                }
                jo = new JSONObject(output);
            }
        } catch (IOException ioe) {
            if (logger.isDebugEnabled())
                logger.debug("Received empty response from server: ",ioe);
            return null;
        } catch (JSONException e) {
            if (logger.isDebugEnabled())
                logger.debug("ERROR decoding Json: ", e);
            returnType= (Class<T>) String.class;
            return returnType.cast(output);
        }

        try{
            returnObj=returnType.cast(jo);

        }catch (ClassCastException cce){
            if (logger.isDebugEnabled())
                logger.debug("Failed to parse response from server: ", cce);
            throw new RuntimeException(jo.toString());

        }
        return returnObj;
    }

}
