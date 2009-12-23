#ifndef X10_LANG_IBOX_H
#define X10_LANG_IBOX_H

#include <x10/lang/Reference.h>

namespace x10 {
    namespace lang {

        class String;
        
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

        
        /**
         * This is a class that exists only at the C++ implementation level,
         * not at the X10 language level.  Therefore it does not have an
         * associated RTT.
         * 
         * The purpose of this class is to enable struct values to be boxed
         * when they are assigned/casts to variables of interface types.
         * When that happens, all we need to provide is a 
         */
        template <class T> class IBox : public Reference {
        public:
            T value;
            
            IBox(T val) : value(val) {
                location = x10aux::here;
            }

            virtual x10aux::itable_entry* _getITables() { return getITablesForIBox(value); } 

            virtual const x10aux::RuntimeType *_type() const { return x10aux::getRTT<T>(); }
            static const x10aux::RuntimeType* getRTT() { return x10aux::getRTT<T>(); }

            virtual x10aux::ref<String> toString() { return x10aux::to_string(value); }
            
            // TODO: Need to implement serialization/deserialization support for this class!

            virtual x10aux::serialization_id_t _get_serialization_id() {
                assert(false);
                return -1000;
            }

            virtual void _serialize_body(x10aux::serialization_buffer &) {
                assert(false);
            }
        };
    }
}


#endif
