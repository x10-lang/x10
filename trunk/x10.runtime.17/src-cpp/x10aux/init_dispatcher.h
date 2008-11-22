#ifndef X10AUX_INIT_DISPATCHER_H
#define X10AUX_INIT_DISPATCHER_H

#include <x10aux/config.h>
#include <x10aux/alloc.h>

namespace x10aux {

    typedef void (*Initializer)(); // should really be this

    class InitDispatcher {
        protected:
        static InitDispatcher *it;

        static void ensure_created() {
            if (it==NULL) it = new (alloc<InitDispatcher>()) InitDispatcher();
        }

        Initializer *initv;
        int initc;
        size_t initsz;

        public:
        InitDispatcher () : initv(NULL), initc(0), initsz(0) { }
        ~InitDispatcher () { dealloc(initv); }
        
        static void runInitializers() {
            ensure_created();
            it->runInitializers_();
        }
        void runInitializers_() {
            for (int i=0 ; i<initc ; ++i) {
                initv[i]();
            }
            initc = -1;
        }

        static void* addInitializer(Initializer init) {
            ensure_created();
            it->addInitializer_(init);
            return (void*)0x7777; // we call addInitializer(...) to initialise the global vars
        }
        void addInitializer_(Initializer init) {
            if (initc<0) {
                fprintf(stderr,"Adding initializer too late!");
                abort();
            }
            if (initsz<=(size_t)initc) {
                // grow slowly as this is init phase and we don't want to take
                // up RAM unnecessarily
                size_t newsz = initsz+1;
                initv = realloc(initv,initsz*sizeof(Initializer),newsz*sizeof(Initializer));
                initsz = newsz;
            }
            initv[initc++] = init;
        }
    };

}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
