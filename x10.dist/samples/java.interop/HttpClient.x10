/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;


/**
 * An example to show how to use Apache HttpComponents HttpClient 4.2.1 in X10.
 * Compile as "x10c -cp httpclient-4.2.1.jar:httpcore-4.2.1.jar HttpClient.x10"
 * Run as as "x10 -cp .:httpclient-4.2.1.jar:httpcore-4.2.1.jar:commons-logging-1.1.1.jar HttpClient"
 */
public class HttpClient {
    static val url = "http://targethost/";

    public static def main(Rail[String]):void {
        finish for (p in Place.places()) {
            at (p) async {
                val ncores = java.lang.Runtime.getRuntime().availableProcessors();
                Console.OUT.println("Place " + p.id + " has " + ncores + " cores.");
                finish for (var i:Int = 0n; i < ncores; ++i) {
                    val coreid = i;
                    async {
                        Console.OUT.println("Place " + p.id + " core " + coreid + " start.");
                        val httpclient = new DefaultHttpClient();
                        val httpGet = new HttpGet(url);
                        while (true)
                        {
                            val response1 = httpclient.execute(httpGet);
                            /*
                             * The underlying HTTP connection is still held by the response object 
                             * to allow the response content to be streamed directly from the network socket. 
                             * In order to ensure correct deallocation of system resources 
                             * the user MUST either fully consume the response content  or abort request 
                             * execution by calling HttpGet#releaseConnection().
                             */
                            try {
                                Console.OUT.println(response1.getStatusLine());
                                val entity1 = response1.getEntity();
                                /* do something useful with the response body and ensure it is fully consumed */
                                EntityUtils.consume(entity1);
                            } finally {
                                httpGet.releaseConnection();
                            }
                        }
                    }
                }
            }
        }
    }
}
