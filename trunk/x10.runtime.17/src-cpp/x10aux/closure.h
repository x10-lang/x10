#ifndef X10AUX_CLOSURE_H
#define X10AUX_CLOSURE_H

#include <x10aux/config.h>

#include <x10aux/RTT.h>
#include <x10aux/ref.h>
#include <x10aux/serialization.h>

#include <x10/lang/Value.h>


#define DESERIALIZE_CLOSURE(id) DESERIALIZE_CLOSURE_(id,closure_name(id))
#define DESERIALIZE_CLOSURE_(id,T) case id: do { \
    T* closure = new (x10aux::alloc<T>()) T(SERIALIZATION_MARKER()); \
    closure->_deserialize_fields(s); \
    return closure; \
    } while(0)


namespace x10 { namespace lang { class String; } }


namespace x10aux {

    class AnyClosure : public x10::lang::Value {

        public:

        x10_int id;



        AnyClosure(int id_) : id(id_) { }




        virtual void _serialize_fields(serialization_buffer& buf, addr_map&) {
            buf.write(id);
        }

        virtual void _deserialize_fields(serialization_buffer& buf) {
            id = buf.read<x10_int>();
        }

        void _serialize(serialization_buffer& buf, addr_map& m) {
            _serialize_fields(buf,m);
        }
    };

}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
