#ifndef X10AUX_BOOTSTRAP_H
#define X10AUX_BOOTSTRAP_H

#include <x10aux/config.h>
#include <x10aux/pgas.h>
#include <x10aux/alloc.h>
#include <x10aux/string_utils.h>
#include <x10aux/init_dispatcher.h>

#include <x10/lang/VoidFun_0_0.h>
#include <x10/lang/String.h>
#include <x10/lang/Rail.h>

#include <x10/lang/Throwable.h>

#include <x10/runtime/Thread.h>

#ifdef __CYGWIN__
extern "C" int setlinebuf(FILE *);
#endif
namespace x10 { namespace lang { template<class T> class Rail; } }

namespace x10aux {

    extern x10_int exitCode;

    typedef void (*ApplicationMainFunction)(ref<x10::lang::Rail<ref<x10::lang::String> > >);

    class BootStrapClosure : public x10::lang::Value,
                             public virtual x10::lang::VoidFun_0_0
    {
        protected:

        ApplicationMainFunction main;
        ref<x10::lang::Rail<ref<x10::lang::String> > > args;
        public:

        // closure body
        void apply () {
            // Invoke the application main().
            main(args);
        }

        BootStrapClosure(ApplicationMainFunction main_,
                         ref<x10::lang::Rail<ref<x10::lang::String> > > args_)
          : main(main_), args(args_)
        { }

        virtual x10_boolean _struct_equals(x10aux::ref<x10::lang::Object> p0) {
            return false; // FIXME: should we be able to compare function types structurally?
        }

        const x10aux::RuntimeType *_type() const {return x10aux::getRTT<x10::lang::VoidFun_0_0>();}

        ref<x10::lang::String> toString() {
            return x10::lang::String::Lit("x10aux::BootStrapClosure ("__FILELINE__")");
        }

    };

    extern void initialize_xrx();

    template<class Runtime, class T> int template_main(int ac, char **av) {
    
        x10aux::ref<x10::lang::Rail<x10aux::ref<x10::lang::String> > > args =
            x10aux::convert_args(ac, av);

#ifndef NO_EXCEPTIONS
        try {
#endif
            setlinebuf(stdout);

            x10aux::barrier();

            // Initialise enough state to make this 'main' thread look like a normal x10 thread
            // (e.g. make Thread::CurrentThread work properly).
            x10::runtime::Thread::_make(x10aux::null, x10::lang::String::Lit("thread-main"));
            x10aux::initialize_xrx();
            // Initialise the static fields of x10 classes.
            x10aux::InitDispatcher::runInitializers();

            // Construct closure to invoke the user's "public static def main(Rail[String]) : Void"
            // if at place 0 otherwise wait for asyncs.
            x10aux::ref<x10::lang::VoidFun_0_0> main_closure =
                new (x10aux::alloc<x10::lang::VoidFun_0_0>(sizeof(x10aux::BootStrapClosure)))
                    x10aux::BootStrapClosure(T::main,args);

            Runtime::start(main_closure); // use XRX
            //main_closure->apply(); // bypass XRX
            //sleep(3);

#ifndef NO_EXCEPTIONS
        } catch(int exitCode) {

            x10aux::exitCode = exitCode;

        } catch(x10aux::__ref& e) {

            // Assume that only throwables can be thrown
            // and things are never thrown by interface (always cast to a value/object class)
            x10aux::ref<x10::lang::Throwable> &e_ =
                static_cast<x10aux::ref<x10::lang::Throwable>&>(e);

            fprintf(stderr, "Uncaught exception at place %ld: %s\n", x10aux::here(),
                                                                     e_->toString()->c_str());

            e_->printStackTrace();

            x10aux::exitCode = 1;

        } catch(...) {

            fprintf(stderr, "Caught unrecognised exception at place %ld\n", x10aux::here());
            x10aux::exitCode = 1;

        }
#endif

        //fprintf(stderr, "Done with main in place %d", (int)x10aux::here());

        x10aux::free_args(args);

        x10aux::shutdown();

        if (getenv("X10_RXTX")!=NULL)
            fprintf(stderr, "Place: %ld   rx: %lld   tx: %lld\n",
                x10aux::here(),
                (long long)x10aux::deserialized_bytes,
                (long long)x10aux::serialized_bytes);

        return x10aux::exitCode;
    }


}

#endif

// vim:tabstop=4:shiftwidth=4:expandtab
