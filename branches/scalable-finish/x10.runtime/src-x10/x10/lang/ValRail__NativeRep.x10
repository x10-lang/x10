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

import x10.compiler.Native;
import x10.compiler.NativeRep;
import x10.util.Pair;

// FIXME: should be a static class in ValRail
    // must be public as above native annotations will refer to us from user code
    public class ValRail__NativeRep {

        @Native("c++", "true")
        private static def isCPP () = false as Boolean;

        private static def useNativeFor (x:Place) = isCPP() && x!=here;

        // VERSIONS WITH REMOTE RAIL

        @Native("c++", "(#4)->copyTo(#5,#6,#7,#8)")
        @Native("java", "java.lang.System.out.println(\"Should never occur, see ValRail.x10\")")
        private static native def copyTo_[T] (src: ValRail[T], src_off:Int,
                                              dst: Rail[T], dst_off:Int,
                                              len:Int) : Void;

        public static def copyTo[T] (src: ValRail[T], src_off:Int,
                                     dst: Rail[T], dst_off:Int,
                                     len:Int) : Void {
            //NOT IMPLEMENTED! if (useNativeFor(dst.home)) { copyTo_(src,src_off,dst,dst_off,len); return; }
            // could be further optimised to send only the part of the valrail needed
            at (dst) {
                //TODO: implement optimisation in backend so we can use: for ((i):Point(1) in 0..len-1) {
                for (var i:Int=0 ; i<len ; ++i) {
                    dst(dst_off+i) = src(src_off+i);
                }
            }
        }

        // VERSIONS WITH 'finder' FOR REMOTE RAIL

        @Native("c++", "(#4)->copyTo(#5,#6,#7,#8)")
        @Native("java", "java.lang.System.out.println(\"Should never occur, see ValRail.x10\")")
        private static native def copyTo_[T] (src: ValRail[T], src_off:Int,
                                              dst_place:Place, dst_finder:()=>Pair[Rail[T]!,Int],
                                              len:Int) : Void;

        public static def copyTo[T] (src: ValRail[T], src_off:Int,
                                     dst_place:Place, dst_finder:()=>Pair[Rail[T]!,Int],
                                     len:Int) : Void {
            //NOT IMPLEMENTED! if (useNativeFor(dst_place)) { copyTo_(src,src_off,dst_place,dst_finder,len); return; }
            // could be further optimised to send only the part of the valrail needed
            at (dst_place) {
                val pair = dst_finder();
                val dst = pair.first;
                val dst_off = pair.second;
                //TODO: implement optimisation in backend so we can use: for ((i):Point(1) in 0..len-1) {
                for (var i:Int=0 ; i<len ; ++i) {
                    dst(dst_off+i) = src(src_off+i);
                }
            }
        }


        // HACKY VERSIONS WITH 'notifier' FOR PERFORMANCE WHILE FINISH IS SLOW

        /* not implemented
        public static def copyTo[T] (src: ValRail[T], src_off:Int,
                                     dst_place:Place, dst_finder:()=>Pair[Rail[T],Int],
                                     len:Int, notifier:()=>Void) : Void {
        }
        */


    }
