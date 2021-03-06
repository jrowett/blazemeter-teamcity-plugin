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

import com.google.common.collect.LinkedHashMultimap;
import com.blaze.runner.TestStatus;
import okhttp3.MediaType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;


public interface Api {

    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    MediaType TEXT = MediaType.parse("text/plain; charset=ISO-8859-1");
    String ACCEPT="Accept";
    String CONTENT_TYPE="Content-type";
    String APP_JSON="application/json";
    String APP_JSON_UTF_8="application/json; charset=UTF-8";

    String APP_KEY = "jnk100x987c06f4e10c4";

    TestStatus masterStatus(String id);

    int getTestMasterStatusCode(String id);

    HashMap<String,String> startTest(String testId, boolean collection) throws JSONException,IOException;

    int getTestCount() throws JSONException, IOException, ServletException;

    JSONObject stopTest(String testId) throws IOException, JSONException;

    void terminateTest(String testId) throws IOException;

    JSONObject testReport(String reportId);

    LinkedHashMultimap<String, String> testsMultiMap() throws IOException, MessagingException;

    JSONObject getUser() throws IOException,JSONException;

    JSONObject getCIStatus(String sessionId) throws JSONException, IOException;

    boolean active(String testId);

    String retrieveJUNITXML(String sessionId) throws IOException;

    JSONObject retrieveJtlZip(String sessionId) throws IOException, JSONException;

    List<String> getListOfSessionIds(String masterId) throws IOException,JSONException;

    JSONObject generatePublicToken(String sessionId)throws IOException,JSONException;

    String getServerUrl();

    void setServerUrl(String serverUrl);

    boolean ping() throws Exception;

    boolean notes(String note,String masterId)throws Exception;

    boolean properties(JSONArray properties, String sessionId) throws Exception;

    public JSONObject testConfig(String testId) throws IOException, JSONException;

    public String getApiKey();
    public void setApiKey(String apiKey);
}

