#ifndef __X10_LANG_SETTABLE_H
#define __X10_LANG_SETTABLE_H

#include <x10aux/config.h>

#include <x10/lang/Object.h>

namespace x10 { namespace lang { 

class Ref;

template<class FMGL(I), class FMGL(V)> class Settable  : public virtual x10::lang::Object {
    public:
    class RTT : public x10aux::RuntimeType {
        public:
        static RTT * const it;
        virtual void init() {
            initParents(2, x10aux::getRTT<x10::lang::Ref>(), x10aux::getRTT<x10::lang::Object>());
        }
        virtual const char *name() const { 
            static const char *name = 
                x10aux::alloc_printf("x10.lang.Settable[%s,%s]",
                                     x10aux::getRTT<FMGL(I)>()->name(),
                                     x10aux::getRTT<FMGL(V)>()->name());
            return name;
        }
    };
    virtual const x10aux::RuntimeType *_type () const {
        return x10aux::getRTT<x10::lang::Settable<FMGL(I), FMGL(V)> >();
    }
    
    virtual FMGL(V) set(FMGL(V) v, FMGL(I) i) = 0;
    
};

} } 

namespace x10 { namespace lang { 


//#line 16 "/home/spark/x10-cvs/x10.runtime.17/src-x10/x10/lang/Settable.x10"
template<class FMGL(I), class FMGL(V)> typename x10::lang::Settable<FMGL(I), FMGL(V)>::RTT * const x10::lang::Settable<FMGL(I), FMGL(V)>::RTT::it = 
    new (x10aux::alloc<typename x10::lang::Settable<FMGL(I), FMGL(V)>::RTT>()) typename x10::lang::Settable<FMGL(I), FMGL(V)>::RTT();

} } 

#endif // X10_LANG_SETTABLE_H
