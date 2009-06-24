#ifndef X10AUX_INIT_DISPATCHER_H
#define X10AUX_INIT_DISPATCHER_H

#include <x10aux/config.h>
#include <x10aux/alloc.h>
#include <x10aux/RTT.h>

namespace x10aux {

    typedef void (*Initializer)();

    class InitDispatcher {
        protected:
        static InitDispatcher *it;

        Initializer *initv;
        int initc;
        size_t initsz;

        public:
        InitDispatcher () : initv(NULL), initc(0), initsz(0) { }
        ~InitDispatcher () { dealloc(initv); }
        
        static void runInitializers();
        void runInitializers_(); 

        static void* addInitializer(Initializer init);
        void addInitializer_(Initializer init);
    };
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
