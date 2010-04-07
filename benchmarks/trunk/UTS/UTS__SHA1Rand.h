#ifndef __UTS__SHA1RAND_H
#define __UTS__SHA1RAND_H

#include <x10rt.h>


#define X10_LANG_STRUCT_H_NODEPS
#include <x10/lang/Struct.h>
#undef X10_LANG_STRUCT_H_NODEPS
#define X10_LANG_ANY_H_NODEPS
#include <x10/lang/Any.h>
#undef X10_LANG_ANY_H_NODEPS
namespace x10 { namespace lang { 
class Int;
} } 
#include <x10/lang/Int.struct_h>
#include <UTS__SHA1Rand.struct_h>

class UTS__SHA1Rand_methods  {
    public:
    static void _instance_init(UTS__SHA1Rand& this_);
    
    static void _constructor(UTS__SHA1Rand& this_, x10_int seed) {
        x10::lang::Struct_methods::_constructor(this_);
        this_.FMGL(cxx_sha1_rng).init((int)seed);
    }
    inline static UTS__SHA1Rand _make(x10_int seed) {
        UTS__SHA1Rand this_; 
        _constructor(this_, seed);
        return this_;
    }
    
    static void _constructor(UTS__SHA1Rand& this_, UTS__SHA1Rand parent, x10_int spawn_number)
    {
        x10::lang::Struct_methods::_constructor(this_);
        this_.FMGL(cxx_sha1_rng).init(parent->FMGL(cxx_sha1_rng), (int)spawn_number);
    }
    inline static UTS__SHA1Rand _make(
             UTS__SHA1Rand parent,
             x10_int spawn_number)
    {
        UTS__SHA1Rand this_; 
        _constructor(this_, parent,
        spawn_number);
        return this_;
    }
    
    static x10_int
      apply(
      UTS__SHA1Rand this_) {
        
        //#line 17 "/Users/pkambadu/x10-trunk/x10.tests/examples/Benchmarks/UTS/UTS.x10": x10.ast.X10Return_c
        return this_.FMGL(cxx_sha1_rng)();
        
    }
    static x10_boolean at(UTS__SHA1Rand this_, x10aux::ref<x10::lang::Object> obj) { return true; }
    static x10_boolean at(UTS__SHA1Rand this_, x10::lang::Place place) { return true; }
    static x10::lang::Place home(UTS__SHA1Rand this_) { /* FIXME: Should probably call Place_methods::make, but don't want to include Place.h */ x10::lang::Place tmp; tmp->FMGL(id)=x10aux::here; return tmp; }
    static x10aux::ref<x10::lang::String> typeName(UTS__SHA1Rand this_) { return this_->typeName(); }
    static x10_boolean equals(UTS__SHA1Rand this_, x10aux::ref<x10::lang::Any> that) { return this_->equals(that); }
    static x10_boolean equals(UTS__SHA1Rand this_, UTS__SHA1Rand that) { return this_->equals(that); }
    static x10aux::ref<x10::lang::String> toString(UTS__SHA1Rand this_) { return this_->toString(); }
    static x10_int hashCode(UTS__SHA1Rand this_) { return this_->hashCode(); }
    
};
#endif // UTS__SHA1RAND_H

class UTS__SHA1Rand;

#ifndef UTS__SHA1RAND_H_NODEPS
#define UTS__SHA1RAND_H_NODEPS
#include <x10/lang/Struct.h>
#include <x10/lang/Any.h>
#include <x10/lang/Int.h>
#ifndef UTS__SHA1RAND_H_GENERICS
#define UTS__SHA1RAND_H_GENERICS
#endif // UTS__SHA1RAND_H_GENERICS
#endif // __UTS__SHA1RAND_H_NODEPS
