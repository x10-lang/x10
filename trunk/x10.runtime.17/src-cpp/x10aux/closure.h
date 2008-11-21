#ifndef X10AUX_CLOSURE_H
#define X10AUX_CLOSURE_H

#include <x10aux/config.h>

#include <x10aux/RTT.h>

#include <x10/x10.h> // pgas


#define CONCAT(A,B) A##B

#define closure_name(id) CONCAT(__closure__,id)
#define args_name(closure) CONCAT(closure,_args)


#define closure_class_and_args_struct(id, extends, type, UnpackedBody, formals, structure) \
    structure \
    class closure_name(id) : public extends { \
     private: args_name(closure_name(id))* args; \
     public: type apply formals UnpackedBody; \
             closure_name(id) (args_name(closure_name(id))* arg) { args = arg; } \
        virtual const x10aux::RuntimeType *_type() const { return x10aux::getRTT<extends>(); } \
    }


#define closure_unpacked_body(ignorable, body, unpack) { unpack body }

#define closure_instantiation(id, env) \
    x10aux::ref <closure_name(id)> \
       (new (x10aux::alloc<closure_name(id)>()) \
             closure_name(id) (new (x10aux::alloc<args_name(closure_name(id))>()) \
                                    args_name(closure_name(id)) env))




namespace x10aux {
    struct closure_args : public x10_async_closure_t {
//        const int ___dummy;
//        closure_args() : ___dummy(0) { };
        closure_args() { };
        inline closure_args* ptr() { return this; }
    };
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
