#ifndef X10_LANG_IBOX_H
#define X10_LANG_IBOX_H

#include <x10/lang/IBox.struct_h>

namespace x10 {
    namespace lang {

        template <class T> inline x10aux::itable_entry* getITablesForIBox(T value) { return value->_getIBoxITables(); } 

        // TODO: As soon as we change the basic numeric types to implement interfaces
        //       other than Any at the X10 level, we will have to change this code to
        //       return to the itables for IBox that we handwrite for the primitives.
        //       As long as the only interface that is implemented by the C++ built-in primitives
        //       is Any, we don't actually need the itables at runtime because the methods of
        //       Any are all implemented via @Native annotations that go to templatized functions
        //       in x10aux.  We do need these stub methods as specializations of the general getITablesForIBox
        //       template so that when IBox<x10_int> etc are instantiated, we don't try to call
        //       _getIBoxITables() on a C++ primitive type.
        inline x10aux::itable_entry *getITablesForIBox(x10_boolean) { assert(false); return NULL; }
        inline x10aux::itable_entry *getITablesForIBox(x10_byte) { assert(false); return NULL; }
        inline x10aux::itable_entry *getITablesForIBox(x10_ubyte) { assert(false); return NULL; }
        inline x10aux::itable_entry *getITablesForIBox(x10_char) { assert(false); return NULL; }
        inline x10aux::itable_entry *getITablesForIBox(x10_short) { assert(false); return NULL; }
        inline x10aux::itable_entry *getITablesForIBox(x10_ushort) { assert(false); return NULL; }
        inline x10aux::itable_entry *getITablesForIBox(x10_int) { assert(false); return NULL; }
        inline x10aux::itable_entry *getITablesForIBox(x10_uint) { assert(false); return NULL; }
        inline x10aux::itable_entry *getITablesForIBox(x10_long) { assert(false); return NULL; }
        inline x10aux::itable_entry *getITablesForIBox(x10_ulong) { assert(false); return NULL; }
        inline x10aux::itable_entry *getITablesForIBox(x10_float) { assert(false); return NULL; }
        inline x10aux::itable_entry *getITablesForIBox(x10_double) { assert(false); return NULL; }

        template<class T> x10aux::itable_entry* IBox<T>::_getITables() { return getITablesForIBox(value); } 

        template<class T> const x10aux::RuntimeType* IBox<T>::_type() const { return x10aux::getRTT<T>(); }
        template<class T> const x10aux::RuntimeType* IBox<T>::getRTT() { return x10aux::getRTT<T>(); }

        template<class T> x10aux::ref<String> IBox<T>::toString() { return x10aux::to_string(value); }

        template<class T> x10_int IBox<T>::hashCode() { return x10aux::hash_code(value); }
            
        // TODO: Need to implement serialization/deserialization support for this class!
        template<class T> x10aux::serialization_id_t IBox<T>::_get_serialization_id() {
            assert(false);
            return -1000;
        }

        template<class T> void IBox<T>::_serialize_body(x10aux::serialization_buffer &) {
            assert(false);
        }
    }
}


#endif
