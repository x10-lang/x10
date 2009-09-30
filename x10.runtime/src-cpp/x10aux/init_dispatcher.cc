#include <x10aux/config.h>
#include <x10aux/init_dispatcher.h>

#include <stdio.h>

using namespace x10aux;
using namespace x10::lang;

InitDispatcher *InitDispatcher::it;

void
InitDispatcher::runInitializers() {
    if (NULL != it) {
        it->runInitializers_();
    }
}

void
InitDispatcher::runInitializers_() {
    for (int i=0 ; i<initc ; ++i) {
        initv[i]();
    }
    initc = -1;
}

void *
InitDispatcher::addInitializer(Initializer init) {
    if (NULL == it) {
        it = new (alloc<InitDispatcher>()) InitDispatcher();
    }
    it->addInitializer_(init);
    return (void*)0x7777; // we call addInitializer(...) to initialise the global vars
}

void
InitDispatcher::addInitializer_(Initializer init) {
    if (initc<0) {
        fprintf(stderr,"Adding initializer too late!");
        abort();
    }
    if (initsz<=(size_t)initc) {
        // grow slowly as this is init phase and we don't want to take
        // up RAM unnecessarily
        size_t newsz = initsz+1;
        // do not use GC
        initv = (Initializer*)::realloc(initv, newsz*sizeof(Initializer));
        initsz = newsz;
    }
    initv[initc++] = init;
}



// vim:tabstop=4:shiftwidth=4:expandtab
