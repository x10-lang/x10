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

#ifndef X10_LANG_OBJECT_H
#define X10_LANG_OBJECT_H

#include <x10aux/config.h>
#include <x10aux/ref.h>
#include <x10aux/RTT.h>
#include <x10aux/serialization.h>

#include <x10/lang/Reference.h>

#define X10_LANG_ANY_H_NODEPS
#include <x10/lang/Any.h>
#undef X10_LANG_ANY_H_NODEPS

namespace x10 {
    namespace io { class SerialData; }
    namespace lang {

        class String;

        class Object : public Reference {
        private:
            static x10aux::itable_entry _itables[1];

        public:
            RTT_H_DECLS_CLASS;

            virtual x10aux::itable_entry* _getITables() { return _itables; }
            
            static x10aux::ref<Object> _make();

            void _constructor() { }

            void _constructor(x10aux::ref<x10::io::SerialData>) { }
            
            static const x10aux::serialization_id_t _serialization_id;

            virtual x10aux::serialization_id_t _get_serialization_id() { return _serialization_id; };

            virtual void _serialize_body(x10aux::serialization_buffer &buf) { }

            virtual bool _custom_deserialization() { return false; }

            static x10aux::ref<Reference> _deserializer(x10aux::deserialization_buffer &);

            virtual void _deserialize_body(x10aux::deserialization_buffer &buf) { }

            template<class T> friend class x10aux::ref;

            // Will be overriden by classes that implement x10.lang.Runtime.Mortal to return true.
            virtual x10_boolean _isMortal() { return false; }

            virtual x10_int hashCode() { return identityHashCode(this); }

            static inline x10_int identityHashCode(x10aux::ref<Object> obj) {
                // STEP 1: Figure out the address to use as for the object.
                void *v = (void*)(obj.operator->());

                // STEP 2: Combine the bits of the pointer into a 32 bit integer.
                //         Note: intentionally not doing some type-punning pointer thing here as
                //         the behavior of that is somewhat underdefined and tends to expose
                //         "interesting" behavior in C++ compilers (especially at high optimization level).
                uint64_t v2 = (uint64_t)v;
                x10_int lower = (x10_int)(v2 & 0xffffffff);
                x10_int upper = (x10_int)(v2 >> 32);
                x10_int hc = lower ^ upper;
                return hc;
            }
            
            virtual x10aux::ref<String> toString();

            virtual x10aux::ref<x10::lang::String> typeName();

            // Like the destructor, but called only by dealloc_object()
            // Needed only for native classes that have alloc'ed state
            virtual void _destructor() { }

            static void dealloc_object(Object*);
        };
    }
}


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
