#ifndef X10AUX_EXIT_CODE_H
#define X10AUX_EXIT_CODE_H

#include <x10aux/config.h>
#include <x10aux/closure.h>

#include <x10/lang/VoidFun_0_0.h>
#include <x10/lang/String.h>

namespace x10 { namespace lang { template<class T> class Rail; } }

namespace x10aux {

    typedef void (*ApplicationMainFunction)(ref<x10::lang::Rail<ref<x10::lang::String> > >);

    class BootStrapClosure : public x10aux::AnyClosure,
                             public virtual x10::lang::VoidFun_0_0
    {
        protected:

        ApplicationMainFunction main;
        ref<x10::lang::Rail<ref<x10::lang::String> > > args;
        public:

        // closure body
        void apply () {
            if (x10_here()==0) {
                // At the first node, execute the user's "main" method
                main(args);
            } else {
                // At all other nodes, wait for asynchronous messages
                x10_wait();
            }
        }

        void _serialize_fields(x10aux::serialization_buffer &, x10aux::addr_map&) { abort();}

        void _deserialize_fields(x10aux::serialization_buffer &) { abort(); }

        BootStrapClosure(ApplicationMainFunction main_,
                         ref<x10::lang::Rail<ref<x10::lang::String> > > args_)
          : x10aux::AnyClosure(-1), main(main_), args(args_)
        { }


        const x10aux::RuntimeType *_type() const {return x10aux::getRTT<x10::lang::VoidFun_0_0>();}

        ref<x10::lang::String> toString() {
            return x10::lang::String::Lit("x10aux::BootStrapClosure ("__FILELINE__")");
        }

    };


    extern x10_int exitCode;
}

#endif

// vim:tabstop=4:shiftwidth=4:expandtab
