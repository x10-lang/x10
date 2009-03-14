#ifndef __X10_LANG_ITERABLE_H
#define __X10_LANG_ITERABLE_H

#include <x10aux/config.h>

#include <x10/lang/Object.h>

namespace x10 { namespace lang { 

class Ref;

template<class FMGL(T)> class Iterable  : public virtual x10::lang::Object
 {
    public:
    class RTT : public x10aux::RuntimeType {
        public:
        static RTT * const it;
        virtual void init() {
            initParents(2, x10aux::getRTT<x10::lang::Ref>(), x10aux::getRTT<x10::lang::Object>());
        }
        virtual const char *name() const { 
            static const char *name = 
                x10aux::alloc_printf("x10.lang.Iterable[%s]",
                                     x10aux::getRTT<FMGL(T)>()->name());
            return name;
        }
    };
    virtual const x10aux::RuntimeType *_type () const {
        return x10aux::getRTT<x10::lang::Iterable<FMGL(T)> >();
    }
    
    virtual x10aux::ref<x10::lang::Iterator<FMGL(T) > > iterator() = 0;
    
};

} } 

namespace x10 { namespace lang { 

//#line 20 "/home/spark/x10-cvs/x10.runtime.17/src-x10/x10/lang/Iterable.x10"
template<class FMGL(T)> typename x10::lang::Iterable<FMGL(T)>::RTT * const x10::lang::Iterable<FMGL(T)>::RTT::it = 
    new (x10aux::alloc<typename x10::lang::Iterable<FMGL(T)>::RTT>()) typename x10::lang::Iterable<FMGL(T)>::RTT();

} } 

#endif // X10_LANG_ITERABLE_H
