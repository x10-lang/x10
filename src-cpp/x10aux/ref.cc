#include <x10aux/config.h>

#include <x10aux/ref.h>
#include <x10/lang/String.h>

/*
template<> x10aux::ref<x10::lang::String>::ref(const x10::lang::String& val)
    : REF_INIT(new (x10aux::alloc<x10::lang::String>()) x10::lang::String(val))
{
    _R_("Copying '" << val << "' (" << (void*)&val << ") to " << this);
    INC(_val);
}
*/
// vim:tabstop=4:shiftwidth=4:expandtab
