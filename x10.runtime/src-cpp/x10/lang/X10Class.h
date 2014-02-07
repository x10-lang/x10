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

#ifndef X10_LANG_X10CLASS_H
#define X10_LANG_X10CLASS_H

#include <x10/lang/Reference.h>
#include <x10aux/basic_functions.h>

namespace x10 {
    namespace lang {
        
        /**
         * This is a class that exists only at the C++ implementation level,
         * not at the X10 language level.
         * 
         * The purpose of this class is to provide a common C++ level superclass
         * for all X10 classes to enable C++ level types to distinguish between
         * types that are X10 classes vs. types that are not (other subclasses of Reference).
         * 
         * It is not formally part of the RTT system (_type is abstract),
         * but we do declare a private synthetic RTT for it to properly initialize
         * the sentinel RTT in empty_itables for debugging.
         */
        class X10Class : public Reference {
        public:
            X10Class(){ }
            
            /*********************************************************************************
             * Implementation-level object model functions assumed to be defined for all types
             *********************************************************************************/

            // classes that implement no interfaces will inherit this guy
            static ::x10aux::itable_entry empty_itable[1];
            virtual ::x10aux::itable_entry* _getITables() { return empty_itable; }


            virtual const ::x10aux::RuntimeType *_type() const = 0;

            /*********************************************************************************
             * X10-level functions assumed to be defined for all types
             *********************************************************************************/
            virtual x10_boolean equals(Any* other) {
                return this->_struct_equals(reinterpret_cast<Reference*>(other));
            }

            virtual x10_boolean _struct_equals(Reference* other) {
                return other == this;
            }
            
            virtual x10_int hashCode() { return ::x10aux::identity_hash_code(this); }

            virtual String* toString() { return ::x10aux::identity_to_string(this); }

            virtual String* typeName();

            // Like the destructor, but called only by dealloc_object()
            // To be overridden by native classes that have alloc'ed state
            // TODO: reconsider if we actually need this functionality.
            virtual void _destructor() { }

            static void dealloc_object(X10Class*);

            /*********************************************************************************
             * Serialization/Deserialization functions assumed to be defined for all types
             *********************************************************************************/
            virtual ::x10aux::serialization_id_t _get_serialization_id() = 0;
            virtual void _serialize_body(::x10aux::serialization_buffer &) = 0;

        private:
            // RTT intentionally private because the only usage is meant to be in this class;
            // not meant to be availble to subclasses.
            static ::x10aux::RuntimeType rtt;
            static const ::x10aux::RuntimeType* getRTT() { if (!rtt.isInitialized) _initRTT(); return &rtt; }
            static void _initRTT();
        };
    }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab:textwidth=100
