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

#ifndef X10_LANG_CLOSURE_H
#define X10_LANG_CLOSURE_H

#include <x10aux/config.h>

#include <x10/lang/Reference.h>
#include <x10aux/basic_functions.h>

#define X10_LANG_ANY_H_NODEPS
#include <x10/lang/Any.h>
#undef X10_LANG_ANY_H_NODEPS

namespace x10 { namespace lang { class String; } }

namespace x10 {

    namespace lang {

        /**
         * This is a class that exists only at the C++ implementation level,
         * not at the X10 language level.  Therefore it does not have an
         * associated RTT.
         * 
         * The purpose of this class is to provide a common C++ level superclass
         * for all X10 closures.  This provides a class in which to locate object model
         * and other serialization/deserialization functions that are common for all
         * concrete Closure instances.  This is an abstract class.
         */
        class Closure : public Reference {
        public:
            Closure() {
            }

            virtual x10_int hashCode();
            
            virtual String* toString() { return ::x10aux::identity_to_string(this); }
            
            virtual const char* toNativeString();

            virtual ::x10::lang::String* typeName();
        };
    }
}


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
