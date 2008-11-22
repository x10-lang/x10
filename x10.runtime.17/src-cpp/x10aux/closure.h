#ifndef X10AUX_CLOSURE_H
#define X10AUX_CLOSURE_H

#include <x10aux/config.h>

#include <x10aux/RTT.h>
#include <x10aux/ref.h>

#include <x10/x10.h> // pgas


#define CONCAT(A,B) A##B

#define CLOSURE_NAME(id) CONCAT(__closure__,id)


#define DESERIALIZE_CLOSURE(id) DESERIALIZE_CLOSURE_(id,closure_name(id))
#define DESERIALIZE_CLOSURE_(id,T) case id: do { \
    T* closure = new (x10aux::alloc<T>()) T(SERIALIZATION_MARKER()); \
    closure->_deserialize_fields(s); \
    return closure; \
    } while(0)


namespace x10 { namespace lang { class String; } }


namespace x10aux {

    class AnyClosure {
        public:
        int id;
        AnyClosure(int id_) : id(id_) { }
        virtual ~AnyClosure() { }
        //virtual ref<x10::lang::String> toString() = 0;
        virtual x10_int hashCode() { return (x10_int)this; }
        //FIXME: returning true here is probably suboptimal
        virtual x10_boolean equals(ref<x10::lang::Object>) { return true; }
    };

}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
