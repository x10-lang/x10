#ifndef X10AUX_ASYNC_SWITCH_H
#define X10AUX_ASYNC_SWITCH_H

#include <x10aux/config.h>
#include <x10aux/alloc.h>
#include <x10aux/serialization.h>

namespace x10aux {

    typedef void (*Invoker)(serialization_buffer &buf);

    template<> inline std::string typeName<Invoker>() { return "Invoker"; }


    // TODO: factor out common code from here and init_dispatcher to some common superclass
    class AsyncSwitch {
        protected:
        static AsyncSwitch *it;

        static void ensure_created() {
            if (it==NULL) it = new (alloc<AsyncSwitch>()) AsyncSwitch();
        }

        Invoker *initv;
        int initc;
        size_t initsz;

        public:
        AsyncSwitch () : initv(NULL), initc(0), initsz(0) { }
        ~AsyncSwitch () { dealloc(initv); }
        
        static void dispatch(serialization_buffer &buf) {
            ensure_created(); // this can probably be taken out or #ifdefed out in performance code
            it->dispatch_(buf);
        }

        void dispatch_(serialization_buffer &buf) {
            x10_int id = buf.read<x10_int>();
            initv[id](buf);
        }

        static x10_int addInvoker(Invoker init) {
            ensure_created();
            return it->addInvoker_(init);
        }
        x10_int addInvoker_(Invoker init) {
            if (initsz<=(size_t)initc) {
                // grow slowly as this is init phase and we don't want to take
                // up RAM unnecessarily
                size_t newsz = initsz+1;
                initv = realloc(initv,initsz*sizeof(Invoker),newsz*sizeof(Invoker));
                initsz = newsz;
            }
            initv[initc] = init;
            return initc++;
            
        }
    };

    template<> inline std::string typeName<AsyncSwitch>() { return "AsyncSwitch"; }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
