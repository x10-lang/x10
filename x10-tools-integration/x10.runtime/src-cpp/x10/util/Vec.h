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

#ifndef X10_UTIL_VEC_H
#define X10_UTIL_VEC_H

#include <x10aux/config.h>
#include <x10aux/ref.h>
#include <x10aux/RTT.h>
#include <x10aux/serialization.h>

namespace x10 {
    namespace util {

        template <class T, x10_int SZ> struct NativeVec{
                inline x10_int size() const { return SZ; }
                T arr[SZ];
                NativeVec(void) { };
                NativeVec(size_t sz) { (void) sz; };
                const T &get (int i) const { return arr[i]; }
                const T &set (const T &v, int i) { arr[i] = v; return v; }
                NativeVec(const NativeVec<T,SZ> &src)
                {
                        ::memcpy(arr, src.arr, SZ * sizeof(T));
                }
                NativeVec(const NativeVec<T,-1> &src)
                {
                        assert(src.size() == SZ);
                        ::memcpy(arr, src.arr, SZ * sizeof(T));
                }
        };

        template <class T> struct NativeVec<T,-1> {
                T *arr;
                size_t sz;
                inline x10_int size() const { return sz; }
                NativeVec(void) :arr(NULL), sz(0) { };
                NativeVec(size_t sz_) :arr(x10aux::alloc<T>(sz*sizeof(T))), sz(sz_) { };
                ~NativeVec(void) { }
                const T &get (int i) const { return arr[i]; }
                const T &set (const T &v, int i) { arr[i] = v; return v; }
                NativeVec<T,-1> &operator= (const NativeVec<T,-1> &src)
                {
                        sz = src.size();
                        arr = x10aux::realloc(arr, sz*sizeof(T));
                        ::memcpy(arr, src.arr, sz * sizeof(T));
                        return *this;
                }
                NativeVec (const NativeVec<T,-1> &src)
                    : arr(x10aux::alloc<T>(src.sz*sizeof(T))), sz(src.sz)
                {
                        ::memcpy(arr, src.arr, sz * sizeof(T));
                }
                template<int SZ> NativeVec (const NativeVec<T,SZ> &src)
                    : arr(x10aux::alloc<T>(SZ*sizeof(T))), sz(SZ)
                {
                        ::memcpy(arr, src.arr, sz * sizeof(T));
                }
        };
    }
}

#endif
// vim: ts=8:sw=8:et
