#include <x10/lang/GlobalRef.h>
#include <x10aux/reference_logger.h>

x10aux::RuntimeType x10::lang::GlobalRef<void>::rtt;

namespace x10 {
    namespace lang {
        
        void logGlobalReference(x10aux::ref<x10::lang::Object> obj) {
            #if defined(X10_USE_BDWGC) || defined(X10_DEBUG_REFERENCE_LOGGER)
            if (!obj->_isMortal()) {
                x10aux::ReferenceLogger::log(obj.operator->());
            }
            #endif
        }
    }
}


