#ifndef X10_LANG_IBOX_H
#define X10_LANG_IBOX_H

#include <x10/lang/IBox.struct_h>

namespace x10 {
    namespace lang {

        template <class T> inline x10aux::itable_entry* getITablesForIBox(T value) { return value->_getIBoxITables(); } 

        // TODO: As soon as we change the basic numeric types to implement interfaces
        //       at the X10 level, we will have to change this code to return to the
        //       itables for IBox that we handwrite for the primitives.
        inline x10aux::itable_entry *getITablesForIBox(x10_boolean) { return NULL; }
        inline x10aux::itable_entry *getITablesForIBox(x10_byte) { return NULL; }
        inline x10aux::itable_entry *getITablesForIBox(x10_ubyte) { return NULL; }
        inline x10aux::itable_entry *getITablesForIBox(x10_char) { return NULL; }
        inline x10aux::itable_entry *getITablesForIBox(x10_short) { return NULL; }
        inline x10aux::itable_entry *getITablesForIBox(x10_ushort) { return NULL; }
        inline x10aux::itable_entry *getITablesForIBox(x10_int) { return NULL; }
        inline x10aux::itable_entry *getITablesForIBox(x10_uint) { return NULL; }
        inline x10aux::itable_entry *getITablesForIBox(x10_long) { return NULL; }
        inline x10aux::itable_entry *getITablesForIBox(x10_ulong) { return NULL; }
        inline x10aux::itable_entry *getITablesForIBox(x10_float) { return NULL; }
        inline x10aux::itable_entry *getITablesForIBox(x10_double) { return NULL; }

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
