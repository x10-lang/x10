#ifndef X10AUX_CLOSURE_H
#define X10AUX_CLOSURE_H

#include <x10aux/config.h>

#include <x10aux/RTT.h>

#include <x10/x10.h> // pgas


#define CONCAT(A,B) A##B

#define CLOSURE_NAME(id) CONCAT(__closure__,id)


#define DESERIALIZE_CLOSURE(id) DESERIALIZE_CLOSURE_(id,closure_name(id))
#define DESERIALIZE_CLOSURE_(id,T) case id: do { \
    T* closure = new (x10aux::alloc<T>()) T(SERIALIZATION_MARKER()); \
    closure->_deserialize_fields(s); \
    return closure; \
    } while(0)



namespace x10aux {

    class AnyClosure {
        public:
        int id;
        AnyClosure(int id_) : id(id_) { }
    };

}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
