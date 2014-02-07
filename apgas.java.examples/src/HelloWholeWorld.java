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

import com.ibm.apgas.Pool;
import com.ibm.apgas.Task;

public class HelloWholeWorld {

    public static void main(String[] args) {
        Pool p = new Pool(new Task() {
            public void body() {
                sayHello();
              }
        });
        p.start();
    }

    static void sayHello() {
        for (int i=0; i<Pool.numPlaces(); i++) {
            Pool.runAsync(i, new Task() {
                public void body() {
                    System.out.println("Hello from place "+Pool.here());
                }
            });
        }
    }
}
