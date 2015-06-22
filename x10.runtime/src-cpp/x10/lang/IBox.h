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

#ifndef X10_LANG_IBOX_H
#define X10_LANG_IBOX_H

#include <x10/lang/Reference.h>

namespace x10 {
    namespace lang {
        class String;
        
        /**
         * This is a class that exists only at the C++ implementation level,
         * not at the X10 language level.  Therefore it does not have an
         * associated RTT.
         * 
         * The purpose of this class is to enable struct values to be boxed
         * when they are assigned/casts to variables of interface types.
         * When that happens, we need to provide a subclass of Reference
         * that contains the struct and redirects all interface methods to
         * the appropriate methods of the struct.
         */
        template <class T> class IBox : public Reference {
        protected:
           IBox(){ }
        public:
            T value;
            
            IBox(T val) : value(val) { }

            virtual ::x10aux::itable_entry* _getITables();

            virtual const ::x10aux::RuntimeType *_type() const;
            static const ::x10aux::RuntimeType* getRTT();

            virtual x10_boolean _struct_equals(Reference* other);
            
            virtual String* toString();

            virtual x10_int hashCode();
            
            static const ::x10aux::serialization_id_t _serialization_id;

            virtual ::x10aux::serialization_id_t _get_serialization_id() {
              return _serialization_id;
            }

            virtual void _serialize_body(::x10aux::serialization_buffer &);

            static Reference* _deserializer(::x10aux::deserialization_buffer& buf);
        };
    }
}

#endif /* X10_LANG_IBOX_H */

#ifndef X10_LANG_IBOX_NODEPS
#define X10_LANG_IBOX_NODEPS

#include <x10/lang/Complex.h>

namespace x10 {
    namespace lang {

        template <class T> inline ::x10aux::itable_entry* getITablesForIBox(T value) { return value->_getIBoxITables(); } 

        extern ::x10aux::itable_entry itable_Boolean[3];
        extern ::x10aux::itable_entry itable_Byte[6];
        extern ::x10aux::itable_entry itable_UByte[6];
        extern ::x10aux::itable_entry itable_Char[4];
        extern ::x10aux::itable_entry itable_Short[6];
        extern ::x10aux::itable_entry itable_UShort[6];
        extern ::x10aux::itable_entry itable_Int[6];
        extern ::x10aux::itable_entry itable_UInt[6];
        extern ::x10aux::itable_entry itable_Long[6];
        extern ::x10aux::itable_entry itable_ULong[6];
        extern ::x10aux::itable_entry itable_Float[5];
        extern ::x10aux::itable_entry itable_Double[5];
        extern ::x10aux::itable_entry itable_Complex[3];
        
        inline ::x10aux::itable_entry *getITablesForIBox(x10_boolean) { return itable_Boolean; }
        inline ::x10aux::itable_entry *getITablesForIBox(x10_byte) { return itable_Byte; }
        inline ::x10aux::itable_entry *getITablesForIBox(x10_ubyte) { return itable_UByte; }
        inline ::x10aux::itable_entry *getITablesForIBox(x10_char) { return itable_Char; }
        inline ::x10aux::itable_entry *getITablesForIBox(x10_short) { return itable_Short; }
        inline ::x10aux::itable_entry *getITablesForIBox(x10_ushort) { return itable_UShort; }
        inline ::x10aux::itable_entry *getITablesForIBox(x10_int) { return itable_Int; }
        inline ::x10aux::itable_entry *getITablesForIBox(x10_uint) { return itable_UInt; }
        inline ::x10aux::itable_entry *getITablesForIBox(x10_long) { return itable_Long; }
        inline ::x10aux::itable_entry *getITablesForIBox(x10_ulong) { return itable_ULong; }
        inline ::x10aux::itable_entry *getITablesForIBox(x10_float) { return itable_Float; }
        inline ::x10aux::itable_entry *getITablesForIBox(x10_double) { return itable_Double; }
        inline ::x10aux::itable_entry *getITablesForIBox(x10_complex) { return itable_Complex; }

        template<class T> ::x10aux::itable_entry* IBox<T>::_getITables() { return getITablesForIBox(value); } 

        template<class T> const ::x10aux::RuntimeType* IBox<T>::_type() const { return ::x10aux::getRTT<T>(); }
        template<class T> const ::x10aux::RuntimeType* IBox<T>::getRTT() { return ::x10aux::getRTT<T>(); }

        template<class T> String* IBox<T>::toString() { return ::x10aux::to_string(value); }

        template<class T> x10_int IBox<T>::hashCode() { return ::x10aux::hash_code(value); }

        template <class T> x10_boolean IBox<T>::_struct_equals(Reference* other) {
            if (NULL != other && _type()->equals(other->_type())) {
                // implies that other is also an IBox<T>
                IBox<T>* otherAsIBox = reinterpret_cast<IBox<T>*>(other);
                return ::x10aux::struct_equals(value, otherAsIBox->value);
            } else {
                // If I'm an IBox<T> and the other guy is not an IBox<T> then has to be false.
                return false;
            }
        }
        
        template<class T> const ::x10aux::serialization_id_t x10::lang::IBox<T>::_serialization_id = 
            ::x10aux::DeserializationDispatcher::addDeserializer(::x10::lang::IBox<T>::_deserializer);

        template<class T> void x10::lang::IBox<T>::_serialize_body(::x10aux::serialization_buffer &buf) {
            buf.write(value);
        }
        
        template<class T> Reference* x10::lang::IBox<T>::_deserializer(::x10aux::deserialization_buffer& buf) {
            IBox<T> * storage = ::x10aux::alloc<IBox<T> >();
            buf.record_reference(storage);
            T tmp = buf.read<T>();
            ::x10::lang::IBox<T>* this_ = new (storage) ::x10::lang::IBox<T>(tmp);
            return this_;
        }
    }
}

#endif /* X10_LANG_IBOX_NODEPS */
