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

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;

import java.io.IOException;

public class RetryInterceptor implements Interceptor {

    private Logger logger =null;
    public RetryInterceptor(Logger logger){
        this.logger = logger;
    }
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        int retry = 1;
        int maxRetries=3;
        while (!respSuccess(response) && retry < maxRetries+1) {
            try{
                Thread.sleep(1000*retry);
            }catch (InterruptedException e){
                throw new IOException("Retry bzmLog was interrupted on sleep at retry # "+retry);
            }
            response = chain.proceed(request);
            logger.info("Child request: code = "+response.code()+" -> "+retry+" retry");
            retry++;
        }
        return response;
    }

    private boolean respSuccess(Response response) {
        boolean respSuccess = response.isSuccessful() || response.code() <= 406;
        return respSuccess;
    }
}
