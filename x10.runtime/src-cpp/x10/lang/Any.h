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

            template <class I> struct itable {
                itable(x10_boolean (I::*equals)(x10::lang::Any*),
                	   x10_boolean (I::*equals__tm__)(x10tm::TMThread *SelfTM, x10::lang::Any*),
                       x10_int (I::*hashCode)(),
                       x10_int (I::*hashCode__tm__)(x10tm::TMThread *SelfTM),
                       x10::lang::String* (I::*toString)(),
                       x10::lang::String* (I::*toString__tm__)(x10tm::TMThread *SelfTM),
                       x10::lang::String* (I::*typeName)(),
                       x10::lang::String* (I::*typeName__tm__)(x10tm::TMThread *SelfTM)) : equals(equals), equals__tm__(equals__tm__), hashCode(hashCode), hashCode__tm__(hashCode__tm__),
                                                              toString(toString), toString__tm__(toString__tm__), typeName(typeName), typeName__tm__(typeName__tm__) {}

                x10_boolean (I::*equals)(x10::lang::Any*);
                x10_boolean (I::*equals__tm__)(x10tm::TMThread *SelfTM, x10::lang::Any*);
                x10_int (I::*hashCode)();
                x10_int (I::*hashCode__tm__)(x10tm::TMThread *SelfTM);
                x10::lang::String* (I::*toString)();
                x10::lang::String* (I::*toString__tm__)(x10tm::TMThread *SelfTM);
                x10::lang::String* (I::*typeName)();
                x10::lang::String* (I::*typeName__tm__)(x10tm::TMThread *SelfTM);
            };
        };
    }
}
#endif
// vim:tabstop=4:shiftwidth=4:expandtab
