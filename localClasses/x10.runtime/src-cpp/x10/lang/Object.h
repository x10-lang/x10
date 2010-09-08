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
    namespace lang {

        class String;

        class Object : public Reference {
        private:
            static x10aux::itable_entry _itables[1];

        public:
            RTT_H_DECLS_CLASS;

            virtual x10aux::itable_entry* _getITables() { return _itables; }
            
            static x10aux::ref<Object> _make();

            x10aux::ref<Object> _constructor() {
                return this;
            }

            
            static const x10aux::serialization_id_t _serialization_id;

            static void _serialize(x10aux::ref<Object> this_,
                                   x10aux::serialization_buffer &buf);

            static const x10aux::serialization_id_t _interface_serialization_id;
            // Do not override
            virtual x10aux::serialization_id_t _get_interface_serialization_id() {
                _S_("===> Object's _get_interface_serialization_id() called");
                return _interface_serialization_id;
            }
            // Do not override
            virtual void _serialize_interface(x10aux::serialization_buffer &buf);

            // A helper method for serializing reference state
            // Client responsible for checking for null
            static void _serialize_reference(x10aux::ref<Object> this_,
                                             x10aux::serialization_buffer &buf);

            virtual x10aux::serialization_id_t _get_serialization_id() { return _serialization_id; };

            virtual void _serialize_body(x10aux::serialization_buffer &buf) { }

            virtual bool _custom_deserialization() { return false; }

            template<class T> static x10aux::ref<T> _deserializer(x10aux::deserialization_buffer &);

            template<class T> static x10aux::ref<T> _deserialize(x10aux::deserialization_buffer &buf);

            // TODO: for Object, a single bit (0/1 === Null/NonNull) is now sufficient.
            struct _reference_state {
                x10aux::x10_addr_t ref;
            };
            // A helper method for deserializing reference state
            // Client responsible for checking for null
            static Object::_reference_state _deserialize_reference_state(x10aux::deserialization_buffer &buf) {
                _reference_state rr;
                rr.ref = buf.read<x10aux::x10_addr_t>();
                if (rr.ref == 0) {
                    _S_("Deserializing a "<<ANSI_SER<<ANSI_BOLD<<"null reference"<<ANSI_RESET<<" from buf: "<<&buf);
                } else {
                    _S_("Deserializing a "<<ANSI_SER<<ANSI_BOLD<<"non-null reference "<<rr.ref<<ANSI_RESET<<" from buf: "<<&buf);
                }
                return rr;
            }

            virtual void _deserialize_body(x10aux::deserialization_buffer &buf) { }

            template<class T> friend class x10aux::ref;

            // Will be overriden by classes that implement x10.lang.Runtime.Mortal to return true.
            // Used in _serialize_reference to disable reference logging for specific classes.
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

        template<class T> x10aux::ref<T> Object::_deserializer(x10aux::deserialization_buffer &buf) {
            x10aux::ref<Object> this_ = new (x10aux::alloc<Object>()) Object();
            buf.record_reference(this_);
            this_->_deserialize_body(buf);
            return this_;
        }

        template<class T> x10aux::ref<T> Object::_deserialize(x10aux::deserialization_buffer &buf) {
            // extract the id
            x10aux::serialization_id_t id = buf.read<x10aux::serialization_id_t>();
            _reference_state rr = _deserialize_reference_state(buf);
            if (0 == rr.ref) {
                return x10aux::null;
            } else {
                x10aux::ref<Object> res;
                _S_("Deserializing a "<<ANSI_SER<<ANSI_BOLD<<"class"<<ANSI_RESET<<
                        " (with id "<<id<<")  from buf: "<<&buf);
                // execute a callback to instantiate the right concrete class
                res = x10aux::DeserializationDispatcher::create<T>(buf, id);
                _S_("Deserialized a "<<ANSI_SER<<ANSI_BOLD<<"class"<<ANSI_RESET<<
                    " "<<res->_type()->name());
                return res;
            }
        }
    }
}


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
