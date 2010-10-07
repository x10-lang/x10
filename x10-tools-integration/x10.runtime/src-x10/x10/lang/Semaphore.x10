/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.lang;

import x10.compiler.Pinned;
import x10.compiler.Global;
import x10.util.Stack;

@Pinned public class Semaphore {
        private val lock = new Lock();

        private val threads = new Stack[Runtime.Thread]();

        private var permits:Int;

        def this(n:Int) {
            permits = n;
        }

        private static def min(i:Int, j:Int):Int = i<j ? i : j;

        def release(n:Int):void {
            lock.lock();
            permits += n;
            val m = min(permits, min(n, threads.size()));
            for (var i:Int = 0; i<m; i++) {
                threads.pop().unpark();
            }
            lock.unlock();
        }

        def release():void {
            release(1);
        }

        def reduce(n:Int):void {
            lock.lock();
            permits -= n;
            lock.unlock();
        }

        def acquire():void {
            lock.lock();
            val thread = Runtime.Thread.currentThread();
            while (permits <= 0) {
                threads.push(thread);
                while (threads.contains(thread)) {
                    lock.unlock();
                    Runtime.Thread.park();
                    lock.lock();
                }
            }
            --permits;
            lock.unlock();
        }

        def available():Int = permits;
    }

