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
import x10.compiler.ClockedVar;

// FIXME: should be static class in Rail

    // must be public as above native annotations will refer to us from user code
    public class Rail__NativeRep {

        @Native("c++", "true")
        private static def isCPP () = false as Boolean;

        // TODO: check if T is pointer-free
        private static def useNativeFor (x:Place) = isCPP() && x!=here;


        // VERSIONS WITH REMOTE RAIL

        @Native("c++", "(#4)->copyTo(#5,#6,#7,#8)")
        @Native("java", "java.lang.System.out.println(\"Should never occur, see Rail.x10\")")
        private static native def copyTo_[T] (src: Rail[T]!, src_off:Int,
                                              dst: Rail[T], dst_off:Int,
                                              len:Int) : Void;
        @Native("c++", "(#4)->copyFrom(#5,#6,#7,#8)")
        @Native("java", "java.lang.System.out.println(\"Should never occur, see Rail.x10\")")
        private static native def copyFrom_[T] (dst: Rail[T]!, dst_off:Int,
                                                src: Rail[T], src_off:Int,
                                                len:Int) : Void;
        @Native("c++", "(#4)->copyFrom(#5,#6,#7,#8)")
        @Native("java", "java.lang.System.out.println(\"Should never occur, see Rail.x10\")")
        private static native def copyFrom_[T] (dst: Rail[T]!, dst_off:Int,
                                                src: ValRail[T], src_off:Int,
                                                len:Int) : Void;

        public static def copyTo[T] (src: Rail[T]!, src_off:Int,
                                     dst: Rail[T], dst_off:Int,
                                     len:Int) : Void {
            if (useNativeFor(dst.home)) { copyTo_(src,src_off,dst,dst_off,len); return; }
            // could be further optimised to send only the part of the valrail needed
            val to_serialize = src as ValRail[T];
            at (dst) {
                //TODO: implement optimisation in backend so we can use: for ((i):Point(1) in 0..len-1) {
                for (var i:Int=0 ; i<len ; ++i) {
                    dst(dst_off+i) = to_serialize(src_off+i);
                }
            }
        }

        public static def copyFrom[T] (dst: Rail[T]!, dst_off:Int,
                                       src: Rail[T], src_off:Int,
                                       len:Int) : Void {
            if (useNativeFor(src.home)) { copyFrom_(dst,dst_off,src,src_off,len); return; }
            // semantics allows an async per rail element inside a single finish
            // this version is optimised to use a single async for the whole rail
            // it could be further optimised to send only the part of the rail needed
            at (src) {
                val to_serialize = src as ValRail[T];
                at (dst) {
                    //TODO: implement optimisation in backend so we can use: for ((i):Point(1) in 0..len-1) {
                    for (var i:Int=0 ; i<len ; ++i) {
                        dst(dst_off+i) = to_serialize(src_off+i);
                    }
                }
            }
        }

        public static def copyFrom[T] (dst: Rail[T]!, dst_off:Int,
                                       src: ValRail[T], src_off:Int,
                                       len:Int) : Void {
            if (useNativeFor(src.home)) { copyFrom_(dst,dst_off,src,src_off,len); return; }
            // source is always local
            // semantics allows an async per rail element inside a single finish
            // this version is optimised to not use any asynchrony
            //TODO: implement optimisation in backend so we can use: for ((i):Point(1) in 0..len-1) {
            for (var i:Int=0 ; i<len ; ++i) {
                dst(dst_off+i) = src(src_off+i);
            }
        }


        // VERSIONS WITH 'finder' FOR REMOTE RAIL

        @Native("c++", "(#4)->copyTo(#5,#6,#7,#8)")
        @Native("java", "java.lang.System.out.println(\"Should never occur, see Rail.x10\")")
        public static  native def copyTo_[T] (src: Rail[T]!, src_off:Int,
                                              dst_place:Place, dst_finder:()=>Pair[Rail[T]!,Int],
                                              len:Int) : Void;

        @Native("c++", "(#4)->copyTo(#5,#6,#7,#8)")
        @Native("java", "java.lang.System.out.println(\"Should never occur, see Rail.x10\")")
        public static  native def copyFrom_[T] (src: Rail[T]!, src_off:Int,
                                                dst_place:Place, dst_finder:()=>Pair[Rail[T]!,Int],
                                                len:Int) : Void;

        @Native("c++", "(#4)->copyTo(#5,#6,#7,#8)")
        @Native("java", "java.lang.System.out.println(\"Should never occur, see Rail.x10\")")
        public static  native def copyFrom1_[T] (dst: Rail[T]!, dst_off:Int,
                                                 src_place:Place, src_finder:()=>Pair[ValRail[T]!,Int],
                                                 len:Int) : Void;


        public static def copyTo[T] (src: Rail[T]!, src_off:Int,
                                     dst_place:Place, dst_finder:()=>Pair[Rail[T]!,Int],
                                     len:Int) : Void {
            if (useNativeFor(dst_place)) { copyTo_(src,src_off,dst_place,dst_finder,len); return; }
            // semantics allows an async per rail element inside a single finish
            // this version is optimised to use a single async for the whole rail
            // it could be further optimised to send only the part of the rail needed
            val to_serialize = src as ValRail[T];
            at (dst_place) {
                val pair = dst_finder();
                val dst = pair.first;
                val dst_off = pair.second;
                //TODO: implement optimisation in backend so we can use: for ((i):Point(1) in 0..len-1) {
                for (var i:Int=0 ; i<len ; ++i) {
                    dst(dst_off+i) = to_serialize(src_off+i);
                }
            }
        }

        public static def copyFrom[T] (dst: Rail[T]!, dst_off:Int,
                                       src_place:Place, src_finder:()=>Pair[Rail[T]!,Int],
                                       len:Int) : Void {
            if (useNativeFor(src_place)) { copyFrom_(dst,dst_off,src_place,src_finder,len); return; }
            // semantics allows an async per rail element inside a single finish
            // this version is optimised to use a single async for the whole rail
            // it could be further optimised to send only the part of the rail needed
            at (src_place) {
                val pair = src_finder();
                val src = pair.first;
                val src_off = pair.second;
                val to_serialize = src as ValRail[T];
                at (dst) {
                    //TODO: implement optimisation in backend so we can use: for ((i):Point(1) in 0..len-1) {
                    for (var i:Int=0 ; i<len ; ++i) {
                        dst(dst_off+i) = to_serialize(src_off+i);
                    }
                }
            }
        }

        // upon resolution of XTENLANG-533, drop the 1 from the name and update Rail.x10 to match
        public static def copyFrom1[T] (dst: Rail[T]!, dst_off:Int,
                                       src_place:Place, src_finder:()=>Pair[ValRail[T]!,Int],
                                       len:Int) : Void {
            if (useNativeFor(src_place)) { copyFrom1_(dst,dst_off,src_place,src_finder,len); return; }
            // not necessarily local, so go and fetch the ValRail...
            // semantics allows an async per rail element inside a single finish
            // this version is optimised to use a single async for the whole rail
            // it could be further optimised to send only the part of the rail needed
            at (src_place) {
                val pair = src_finder();
                val src = pair.first;
                val src_off = pair.second;
                at (dst) {
                    //TODO: implement optimisation in backend so we can use: for ((i):Point(1) in 0..len-1) {
                    for (var i:Int=0 ; i<len ; ++i) {
                        dst(dst_off+i) = src(src_off+i);
                    }
                }
            }
        }




        // HACKY VERSIONS WITH 'notifier' FOR PERFORMANCE WHILE FINISH IS SLOW

        @Native("c++", "(#4)->copyTo(#5,#6,#7,#8,#9)")
        @Native("java", "java.lang.System.out.println(\"Should never occur, see Rail.x10\")")
        public static  native def copyTo_[T] (src: Rail[T]!, src_off:Int,
                                              dst: Rail[T], dst_off:Int,
                                              len:Int, notifier:()=>Void) : Void;

        public static def copyTo[T] (src: Rail[T]!, src_off:Int,
                                     dst: Rail[T], dst_off:Int,
                                     len:Int, notifier:()=>Void) : Void {
            if (useNativeFor(dst.home)) { copyTo_(src,src_off,dst,dst_off,len,notifier); return; }
            if (dst.home==here) {
                val dst2 = dst as Rail[T]!;
                for (var i:Int=0 ; i<len ; ++i) {
                    dst2(dst_off+i) = src(src_off+i);
                }
                notifier();
                return;
            }
            // semantics allows an async per rail element inside a single finish
            // this version is optimised to use a single async for the whole rail
            // it could be further optimised to send only the part of the rail needed
            val to_serialize = src as ValRail[T];
            Runtime.runAtNative(dst.home.id, ()=>{
                val dst2 = dst as Rail[T]!; // type system does not understand runAtNative (understandably)
                //TODO: implement optimisation in backend so we can use: for ((i):Point(1) in 0..len-1) {
                for (var i:Int=0 ; i<len ; ++i) {
                    dst2(dst_off+i) = to_serialize(src_off+i);
                }
                notifier();
            });
        }

        @Native("c++", "(#4)->copyTo(#5,#6,#7,#8,#9)")
        @Native("java", "java.lang.System.out.println(\"Should never occur, see Rail.x10\")")
        public static  native def copyTo_[T] (src: Rail[T]!, src_off:Int,
                                              dst_place:Place, dst_finder:()=>Pair[Rail[T]!,Int],
                                              len:Int, notifier:()=>Void) : Void;

        public static def copyTo[T] (src: Rail[T]!, src_off:Int,
                                     dst_place:Place, dst_finder:()=>Pair[Rail[T]!,Int],
                                     len:Int, notifier:()=>Void) : Void {
            if (useNativeFor(dst_place)) { copyTo_(src,src_off,dst_place,dst_finder,len,notifier); return; }
            if (dst_place==here) {
                val pair = dst_finder();
                val dst = pair.first; 
                val dst_off = pair.second; 
                for (var i:Int=0 ; i<len ; ++i) {
                    dst(dst_off+i) = src(src_off+i);
                }
                notifier();
                return;
            }
            // semantics allows an async per rail element inside a single finish
            // this version is optimised to use a single async for the whole rail
            // it could be further optimised to send only the part of the rail needed
            val to_serialize = src as ValRail[T];
            Runtime.runAtNative(dst_place.id, ()=>{
                val pair = dst_finder();
                val dst = pair.first;
                val dst_off = pair.second;
                //TODO: implement optimisation in backend so we can use: for ((i):Point(1) in 0..len-1) {
                for (var i:Int=0 ; i<len ; ++i) {
                    dst(dst_off+i) = to_serialize(src_off+i);
                }
                notifier();
            });
        }

        /* not implemented
        public static def copyFrom[T] (dst: Rail[T]!, dst_off:Int,
                                       src_place:Place, src_finder:()=>Pair[Rail[T],Int],
                                       len:Int, notifier:()=>Void) : Void {
        }

        public static def copyFrom[T] (dst: Rail[T]!, dst_off:Int,
                                       src_place:Place, src_finder:()=>Pair[ValRail[T],Int],
                                       len:Int, notifier:()=>Void) : Void {
        }
        */



        // versions for PlaceLocalHandle.x10
        public static def copyTo[T](src:Rail[T]!, src_off:Int,
                                    dst_place:Place, dst_handle:PlaceLocalHandle[Rail[T]]!, dst_off:Int,
                                    len:Int) {
            val finder = ()=> Pair[Rail[T],Int](dst_handle(), dst_off);
            src.copyTo(src_off, dst_place, finder, len);
            Runtime.dealloc(finder);
        }

        public static def copyTo[T](src:Rail[T]!, src_off:Int,
                                    dst:Place, dst_handle:PlaceLocalHandle[Rail[T]]!, dst_off:Int,
                                    len:Int, notifier:()=>Void) {
            val finder = ()=> Pair[Rail[T],Int](dst_handle(), dst_off);
            src.copyTo(src_off, dst, finder, len, notifier);
            Runtime.dealloc(finder);
            Runtime.dealloc(notifier);
        }


        @Native("c++", "x10::lang::Rail<#1 >::makeCUDA(#4,#5)")
        private static def cudaMakeRail[T](dst:Place, length:Int) : Rail[T]{self.length==length} = null;

        public static safe def makeRemoteRail[T](p:Place, length:Int, init: (Int) => T)
            : Rail[T]{self.length==length}
        {
            val tmp = Rail.make(length, init);
            return makeRemoteRail[T](p,length,tmp);
        }

        public static safe def makeRemoteRail[T](p:Place, length:Int, init: Rail[T]!)
            : Rail[T]{self.length==length}
        {
            // cast needed due to lack of support for ?: in the type inference algorithm
            val r = p.isCUDA() ? cudaMakeRail[T](p,length) : at (p) Rail.make[T](length) as Rail[T](length);
            finish init.copyTo(0, r, 0, length);
            return r;
        }

    	public static safe def makeClockedRail[T](length: Int): Rail[ClockedVar[T]]!{self.length==length} 
    	{
     	   return Rail.make[ClockedVar[T]](length, (int) => new ClockedVar [T] ());
    	}


    }
