/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

#ifndef X10_LANG_ANY_H
#define X10_LANG_ANY_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>
#include <x10/lang/Reference.h>

namespace x10 {
    namespace lang {
        class String;
        
        class Any {
        public:
            RTT_H_DECLS_INTERFACE

            template <class Iface> struct itable {
                itable(x10_boolean (Iface::*equals)(::x10::lang::Any*),
                       x10_int (Iface::*hashCode)(),
                       ::x10::lang::String* (Iface::*toString)(),
                       ::x10::lang::String* (Iface::*typeName)()) : equals(equals), hashCode(hashCode),
                                                              toString(toString), typeName(typeName) {}

                x10_boolean (Iface::*equals)(::x10::lang::Any*);
                x10_int (Iface::*hashCode)();
                ::x10::lang::String* (Iface::*toString)();
                ::x10::lang::String* (Iface::*typeName)();
            };
        };
    }
}
#endif
// vim:tabstop=4:shiftwidth=4:expandtab
