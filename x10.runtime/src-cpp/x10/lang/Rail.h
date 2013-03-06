/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2013.
 */

#ifndef __X10_LANG_RAIL_H
#define __X10_LANG_RAIL_H

#include <x10rt.h>


#define X10_LANG_ITERABLE_H_NODEPS
#include <x10/lang/Iterable.h>
#undef X10_LANG_ITERABLE_H_NODEPS
#define X10_LANG_FUN_0_1_H_NODEPS
#include <x10/lang/Fun_0_1.h>
#undef X10_LANG_FUN_0_1_H_NODEPS
#define X10_LANG_FUN_0_1_H_NODEPS
#include <x10/lang/Fun_0_1.h>
#undef X10_LANG_FUN_0_1_H_NODEPS
#define X10_UTIL_INDEXEDMEMORYCHUNK_H_NODEPS
#include <x10/util/IndexedMemoryChunk.h>
#undef X10_UTIL_INDEXEDMEMORYCHUNK_H_NODEPS
#define X10_UTIL_INDEXEDMEMORYCHUNK_H_NODEPS
#include <x10/util/IndexedMemoryChunk.h>
#undef X10_UTIL_INDEXEDMEMORYCHUNK_H_NODEPS
#define X10_LANG_LONG_H_NODEPS
#include <x10/lang/Long.h>
#undef X10_LANG_LONG_H_NODEPS
#define X10_LANG_LONG_H_NODEPS
#include <x10/lang/Long.h>
#undef X10_LANG_LONG_H_NODEPS
namespace x10 { namespace lang { 
class Int;
} } 
namespace x10 { namespace lang { 
class LongRange;
} } 
namespace x10 { namespace lang { 
class Boolean;
} } 
namespace x10 { namespace lang { 
template<class TPMGL(T)> class Iterator;
} } 
namespace x10 { namespace lang { 
template<class TPMGL(T)> class RailIterator;
} } 
namespace x10 { namespace lang { 
class String;
} } 
namespace x10 { namespace util { 
class StringBuilder;
} } 
namespace x10 { namespace util { 
template<class TPMGL(T)> class ArrayList;
} } 
namespace x10 { namespace lang { 
class Char;
} } 
namespace x10 { namespace compiler { 
class Inline;
} } 
namespace x10 { namespace lang { 
class Unsafe__Token;
} } 
namespace x10 { namespace lang { 
class IllegalArgumentException;
} } 
namespace x10 { namespace lang { 

template<class TPMGL(T)> class Rail;
template <> class Rail<void>;
template<class TPMGL(T)> class Rail : public x10::lang::X10Class   {
    public:
    RTT_H_DECLS_CLASS
    
    x10_long FMGL(size);
    
    static x10aux::itable_entry _itables[5];
    
    virtual x10aux::itable_entry* _getITables() { return _itables; }
    
    static typename x10::lang::Iterable<TPMGL(T)>::template itable<x10::lang::Rail<TPMGL(T)> > _itable_0;
    
    static x10::lang::Any::itable<x10::lang::Rail<TPMGL(T)> > _itable_1;
    
    static typename x10::lang::Fun_0_1<x10_int, TPMGL(T)>::template itable<x10::lang::Rail<TPMGL(T)> > _itable_2;
    
    static typename x10::lang::Fun_0_1<x10_long, TPMGL(T)>::template itable<x10::lang::Rail<TPMGL(T)> > _itable_3;
    
    x10::lang::LongRange range();
    virtual x10::lang::Iterator<TPMGL(T)>* iterator();
    virtual x10::lang::String* toString();
    x10::util::IndexedMemoryChunk<TPMGL(T) > FMGL(raw);
    
    virtual x10::util::IndexedMemoryChunk<TPMGL(T) > raw();
    void _constructor(x10::util::IndexedMemoryChunk<TPMGL(T) > backingStore);
    
    static x10::lang::Rail<TPMGL(T)>* _make(x10::util::IndexedMemoryChunk<TPMGL(T) > backingStore);
    
    void _constructor();
    
    static x10::lang::Rail<TPMGL(T)>* _make();
    
    void _constructor(x10::lang::Unsafe__Token* id__123, x10_long size);
    
    static x10::lang::Rail<TPMGL(T)>* _make(x10::lang::Unsafe__Token* id__123,
                                            x10_long size);
    
    void _constructor(x10::lang::Rail<TPMGL(T)>* src);
    
    static x10::lang::Rail<TPMGL(T)>* _make(x10::lang::Rail<TPMGL(T)>* src);
    
    void _constructor(x10_long size);
    
    static x10::lang::Rail<TPMGL(T)>* _make(x10_long size);
    
    void _constructor(x10_long size, TPMGL(T) init);
    
    static x10::lang::Rail<TPMGL(T)>* _make(x10_long size, TPMGL(T) init);
    
    void _constructor(x10_long size, x10::lang::Fun_0_1<x10_long, TPMGL(T)>* init);
    
    static x10::lang::Rail<TPMGL(T)>* _make(x10_long size, x10::lang::Fun_0_1<x10_long, TPMGL(T)>* init);
    
    virtual TPMGL(T) __apply(x10_long index);
    virtual TPMGL(T) __set(x10_long index, TPMGL(T) v);
    virtual void clear();
    void _constructor(x10_int size);
    
    static x10::lang::Rail<TPMGL(T)>* _make(x10_int size);
    
    void _constructor(x10_int size, TPMGL(T) init);
    
    static x10::lang::Rail<TPMGL(T)>* _make(x10_int size, TPMGL(T) init);
    
    void _constructor(x10_int size, x10::lang::Fun_0_1<x10_int, TPMGL(T)>* init);
    
    static x10::lang::Rail<TPMGL(T)>* _make(x10_int size, x10::lang::Fun_0_1<x10_int, TPMGL(T)>* init);
    
    virtual TPMGL(T) __apply(x10_int index);
    virtual TPMGL(T) __set(x10_int index, TPMGL(T) v);
    virtual x10::lang::Rail<TPMGL(T)>* x10__lang__Rail____this__x10__lang__Rail(
      );
    
    // Serialization
    public: static const x10aux::serialization_id_t _serialization_id;
    
    public: x10aux::serialization_id_t _get_serialization_id() {
         return _serialization_id;
    }
    
    public: virtual void _serialize_body(x10aux::serialization_buffer& buf);
    
    public: static x10::lang::Reference* _deserializer(x10aux::deserialization_buffer& buf);
    
    public: void _deserialize_body(x10aux::deserialization_buffer& buf);
    
};

template<class TPMGL(T)> x10aux::RuntimeType x10::lang::Rail<TPMGL(T)>::rtt;
template<class TPMGL(T)> void x10::lang::Rail<TPMGL(T)>::_initRTT() {
    const x10aux::RuntimeType *canonical = x10aux::getRTT<x10::lang::Rail<void> >();
    if (rtt.initStageOne(canonical)) return;
    const x10aux::RuntimeType* parents[3] = { x10aux::getRTT<x10::lang::Iterable<TPMGL(T)> >(), x10aux::getRTT<x10::lang::Fun_0_1<x10_int, TPMGL(T)> >(), x10aux::getRTT<x10::lang::Fun_0_1<x10_long, TPMGL(T)> >()};
    const x10aux::RuntimeType* params[1] = { x10aux::getRTT<TPMGL(T)>()};
    x10aux::RuntimeType::Variance variances[1] = { x10aux::RuntimeType::invariant};
    const char *baseName = "x10.lang.Rail";
    rtt.initStageTwo(baseName, x10aux::RuntimeType::class_kind, 3, parents, 1, params, variances);
}

template <> class Rail<void> : public x10::lang::X10Class {
    public:
    static x10aux::RuntimeType rtt;
    static const x10aux::RuntimeType* getRTT() { return & rtt; }
    template<class TPMGL(T)> static void copy(x10::lang::Rail<TPMGL(T)>* src,
                                              x10::lang::Rail<TPMGL(T)>* dst);
    
    template<class TPMGL(T)> static void copy(x10::lang::Rail<TPMGL(T)>* src,
                                              x10_long srcIndex, x10::lang::Rail<TPMGL(T)>* dst,
                                              x10_long dstIndex, x10_long numElems);
    
    template<class TPMGL(T)> static void copy(x10::lang::Rail<TPMGL(T)>* src,
                                              x10_int srcIndex, x10::lang::Rail<TPMGL(T)>* dst,
                                              x10_int dstIndex,
                                              x10_int numElems);
    
    
};

} } 
#endif // X10_LANG_RAIL_H

namespace x10 { namespace lang { 
template<class TPMGL(T)> class Rail;
} } 

#ifndef X10_LANG_RAIL_H_NODEPS
#define X10_LANG_RAIL_H_NODEPS
#include <x10/lang/Iterable.h>
#include <x10/lang/Fun_0_1.h>
#include <x10/util/IndexedMemoryChunk.h>
#include <x10/lang/Long.h>
#include <x10/lang/Int.h>
#include <x10/lang/LongRange.h>
#include <x10/lang/Boolean.h>
#include <x10/lang/Iterator.h>
#include <x10/lang/RailIterator.h>
#include <x10/lang/String.h>
#include <x10/util/StringBuilder.h>
#include <x10/util/ArrayList.h>
#include <x10/lang/Char.h>
#include <x10/compiler/Inline.h>
#include <x10/lang/Unsafe__Token.h>
#include <x10/lang/IllegalArgumentException.h>
#ifndef X10_LANG_RAIL_H_GENERICS
#define X10_LANG_RAIL_H_GENERICS
#endif // X10_LANG_RAIL_H_GENERICS
#ifndef X10_LANG_RAIL_H_IMPLEMENTATION
#define X10_LANG_RAIL_H_IMPLEMENTATION
#include <x10/lang/Rail.h>


//#line 17 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.PropertyDecl_c
template<class TPMGL(T)> typename x10::lang::Iterable<TPMGL(T)>::template itable<x10::lang::Rail<TPMGL(T)> >  x10::lang::Rail<TPMGL(T)>::_itable_0(&x10::lang::Rail<TPMGL(T)>::equals, &x10::lang::Rail<TPMGL(T)>::hashCode, &x10::lang::Rail<TPMGL(T)>::iterator, &x10::lang::Rail<TPMGL(T)>::toString, &x10::lang::Rail<TPMGL(T)>::typeName);
template<class TPMGL(T)> x10::lang::Any::itable<x10::lang::Rail<TPMGL(T)> >  x10::lang::Rail<TPMGL(T)>::_itable_1(&x10::lang::Rail<TPMGL(T)>::equals, &x10::lang::Rail<TPMGL(T)>::hashCode, &x10::lang::Rail<TPMGL(T)>::toString, &x10::lang::Rail<TPMGL(T)>::typeName);
template<class TPMGL(T)> typename x10::lang::Fun_0_1<x10_int, TPMGL(T)>::template itable<x10::lang::Rail<TPMGL(T)> >  x10::lang::Rail<TPMGL(T)>::_itable_2(&x10::lang::Rail<TPMGL(T)>::equals, &x10::lang::Rail<TPMGL(T)>::hashCode, &x10::lang::Rail<TPMGL(T)>::__apply, &x10::lang::Rail<TPMGL(T)>::toString, &x10::lang::Rail<TPMGL(T)>::typeName);
template<class TPMGL(T)> typename x10::lang::Fun_0_1<x10_long, TPMGL(T)>::template itable<x10::lang::Rail<TPMGL(T)> >  x10::lang::Rail<TPMGL(T)>::_itable_3(&x10::lang::Rail<TPMGL(T)>::equals, &x10::lang::Rail<TPMGL(T)>::hashCode, &x10::lang::Rail<TPMGL(T)>::__apply, &x10::lang::Rail<TPMGL(T)>::toString, &x10::lang::Rail<TPMGL(T)>::typeName);
template<class TPMGL(T)> x10aux::itable_entry x10::lang::Rail<TPMGL(T)>::_itables[5] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Iterable<TPMGL(T)> >, &_itable_0), x10aux::itable_entry(&x10aux::getRTT<x10::lang::Any>, &_itable_1), x10aux::itable_entry(&x10aux::getRTT<x10::lang::Fun_0_1<x10_int, TPMGL(T)> >, &_itable_2), x10aux::itable_entry(&x10aux::getRTT<x10::lang::Fun_0_1<x10_long, TPMGL(T)> >, &_itable_3), x10aux::itable_entry(NULL, (void*)x10aux::getRTT<x10::lang::Rail<TPMGL(T)> >())};

//#line 19 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10MethodDecl_c
template<class TPMGL(T)> x10::lang::LongRange x10::lang::Rail<TPMGL(T)>::range(
  ) {
    
    //#line 19 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 19 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10LocalDecl_c
        x10::lang::LongRange alloc43169 =  x10::lang::LongRange::_alloc();
        
        //#line 39 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/LongRange.x10": x10.ast.X10LocalDecl_c
        x10_long min55577 = ((x10_long) (((x10_int)0)));
        
        //#line 39 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/LongRange.x10": x10.ast.X10LocalDecl_c
        x10_long max55578 = ((x10_long) ((this->FMGL(size)) - (((x10_long) (((x10_int)1))))));
        
        //#line 40 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/LongRange.x10": x10.ast.X10LocalDecl_c
        x10_boolean x55576 = (x10aux::struct_equals(min55577, ((x10_long)0ll)));
        
        //#line 41 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/LongRange.x10": Eval of x10.ast.X10FieldAssign_c
        alloc43169->FMGL(min) = min55577;
        
        //#line 41 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/LongRange.x10": Eval of x10.ast.X10FieldAssign_c
        alloc43169->FMGL(max) = max55578;
        
        //#line 41 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/LongRange.x10": Eval of x10.ast.X10FieldAssign_c
        alloc43169->FMGL(zeroBased) = x55576;
        alloc43169;
    }))
    ;
    
}

//#line 22 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10MethodDecl_c
template<class TPMGL(T)> x10::lang::Iterator<TPMGL(T)>* x10::lang::Rail<TPMGL(T)>::iterator(
  ) {
    
    //#line 22 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10Return_c
    return reinterpret_cast<x10::lang::Iterator<TPMGL(T)>*>((__extension__ ({
        
        //#line 22 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10LocalDecl_c
        x10::lang::RailIterator<TPMGL(T)>* alloc43170 =  ((new (memset(x10aux::alloc<x10::lang::RailIterator<TPMGL(T)> >(), 0, sizeof(x10::lang::RailIterator<TPMGL(T)>))) x10::lang::RailIterator<TPMGL(T)>()))
        ;
        
        //#line 23 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/RailIterator.x10": x10.ast.X10LocalDecl_c
        x10::lang::Rail<TPMGL(T)>* x55579 = this;
        
        //#line 19 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/RailIterator.x10": Eval of x10.ast.X10FieldAssign_c
        alloc43170->FMGL(cur) = ((x10_long)0ll);
        
        //#line 24 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/RailIterator.x10": Eval of x10.ast.X10FieldAssign_c
        alloc43170->FMGL(rail) = x55579;
        
        //#line 25 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/RailIterator.x10": Eval of x10.ast.X10FieldAssign_c
        alloc43170->FMGL(cur) = ((x10_long)0ll);
        alloc43170;
    }))
    );
    
}

//#line 24 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10MethodDecl_c
template<class TPMGL(T)> x10::lang::String* x10::lang::Rail<TPMGL(T)>::toString(
  ) {
    
    //#line 25 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10LocalDecl_c
    x10::util::StringBuilder* sb =  ((new (memset(x10aux::alloc<x10::util::StringBuilder>(), 0, sizeof(x10::util::StringBuilder))) x10::util::StringBuilder()))
    ;
    
    //#line 22 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/util/StringBuilder.x10": Eval of x10.ast.X10FieldAssign_c
    sb->FMGL(buf) = (__extension__ ({
        
        //#line 22 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/util/StringBuilder.x10": x10.ast.X10LocalDecl_c
        x10::util::ArrayList<x10_char>* alloc55580 =  ((new (memset(x10aux::alloc<x10::util::ArrayList<x10_char> >(), 0, sizeof(x10::util::ArrayList<x10_char>))) x10::util::ArrayList<x10_char>()))
        ;
        
        //#line 22 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/util/StringBuilder.x10": x10.ast.X10ConstructorCall_c
        (alloc55580)->::x10::util::ArrayList<x10_char>::_constructor();
        alloc55580;
    }))
    ;
    
    //#line 26 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": Eval of x10.ast.X10Call_c
    sb->add(x10aux::makeStringLit("["));
    
    //#line 27 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10LocalDecl_c
    x10_long sz = (__extension__ ({
        
        //#line 352 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Math.x10": x10.ast.X10LocalDecl_c
        x10_long a55569 = this->FMGL(size);
        
        //#line 352 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Math.x10": polyglot.ast.Empty_c
        ;
        ((a55569) < (((x10_long)10ll))) ? (a55569) : (((x10_long)10ll));
    }))
    ;
    
    //#line 28 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": polyglot.ast.For_c
    {
        x10_long i;
        for (
             //#line 28 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10LocalDecl_c
             i = ((x10_long)0ll); ((i) < (sz)); 
                                                //#line 28 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": Eval of x10.ast.X10LocalAssign_c
                                                i = ((x10_long) ((i) + (((x10_long)1ll)))))
        {
            
            //#line 29 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10If_c
            if (((i) > (((x10_long)0ll)))) {
                
                //#line 29 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": Eval of x10.ast.X10Call_c
                sb->add(x10aux::makeStringLit(","));
            }
            
            //#line 30 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": Eval of x10.ast.X10Call_c
            sb->add(x10::lang::String::__plus(x10aux::makeStringLit(""), (this->
                                                                            FMGL(raw))->__apply(i)));
        }
    }
    
    //#line 32 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10If_c
    if (((sz) < (this->FMGL(size)))) {
        
        //#line 32 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": Eval of x10.ast.X10Call_c
        sb->add(x10::lang::String::__plus(x10::lang::String::__plus(x10aux::makeStringLit("...(omitted "), ((x10_long) ((this->
                                                                                                                           FMGL(size)) - (sz)))), x10aux::makeStringLit(" elements)")));
    }
    
    //#line 33 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": Eval of x10.ast.X10Call_c
    sb->add(x10aux::makeStringLit("]"));
    
    //#line 34 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10Return_c
    return sb->toString();
    
}

//#line 39 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10FieldDecl_c

//#line 41 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10MethodDecl_c
template<class TPMGL(T)> x10::util::IndexedMemoryChunk<TPMGL(T) >
  x10::lang::Rail<TPMGL(T)>::raw() {
    
    //#line 41 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10Return_c
    return this->FMGL(raw);
    
}

//#line 43 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10ConstructorDecl_c
template<class TPMGL(T)> void x10::lang::Rail<TPMGL(T)>::_constructor(
                           x10::util::IndexedMemoryChunk<TPMGL(T) > backingStore) {
    
    //#line 44 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.AssignPropertyCall_c
    FMGL(size) = ((x10_long) ((backingStore)->length()));
    
    //#line 45 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": Eval of x10.ast.X10FieldAssign_c
    this->FMGL(raw) = backingStore;
}
template<class TPMGL(T)> x10::lang::Rail<TPMGL(T)>* x10::lang::Rail<TPMGL(T)>::_make(
                           x10::util::IndexedMemoryChunk<TPMGL(T) > backingStore)
{
    x10::lang::Rail<TPMGL(T)>* this_ = new (memset(x10aux::alloc<x10::lang::Rail<TPMGL(T)> >(), 0, sizeof(x10::lang::Rail<TPMGL(T)>))) x10::lang::Rail<TPMGL(T)>();
    this_->_constructor(backingStore);
    return this_;
}



//#line 49 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10ConstructorDecl_c
template<class TPMGL(T)> void x10::lang::Rail<TPMGL(T)>::_constructor(
                           ) {
    
    //#line 50 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.AssignPropertyCall_c
    FMGL(size) = ((x10_long)0ll);
    
    //#line 51 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": Eval of x10.ast.X10FieldAssign_c
    this->FMGL(raw) = x10::util::IndexedMemoryChunk<void>::allocate<TPMGL(T) >(((x10_int)0), 8, false, false);
}
template<class TPMGL(T)> x10::lang::Rail<TPMGL(T)>* x10::lang::Rail<TPMGL(T)>::_make(
                           ) {
    x10::lang::Rail<TPMGL(T)>* this_ = new (memset(x10aux::alloc<x10::lang::Rail<TPMGL(T)> >(), 0, sizeof(x10::lang::Rail<TPMGL(T)>))) x10::lang::Rail<TPMGL(T)>();
    this_->_constructor();
    return this_;
}



//#line 55 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10ConstructorDecl_c
template<class TPMGL(T)> void x10::lang::Rail<TPMGL(T)>::_constructor(
                           x10::lang::Unsafe__Token* id__123,
                           x10_long size) {
    
    //#line 56 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.AssignPropertyCall_c
    FMGL(size) = size;
    
    //#line 57 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": Eval of x10.ast.X10FieldAssign_c
    this->FMGL(raw) = x10::util::IndexedMemoryChunk<void>::allocate<TPMGL(T) >(size, 8, false, false);
}
template<class TPMGL(T)> x10::lang::Rail<TPMGL(T)>* x10::lang::Rail<TPMGL(T)>::_make(
                           x10::lang::Unsafe__Token* id__123,
                           x10_long size) {
    x10::lang::Rail<TPMGL(T)>* this_ = new (memset(x10aux::alloc<x10::lang::Rail<TPMGL(T)> >(), 0, sizeof(x10::lang::Rail<TPMGL(T)>))) x10::lang::Rail<TPMGL(T)>();
    this_->_constructor(id__123, size);
    return this_;
}



//#line 61 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10ConstructorDecl_c
template<class TPMGL(T)> void x10::lang::Rail<TPMGL(T)>::_constructor(
                           x10::lang::Rail<TPMGL(T)>* src) {
    
    //#line 62 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.AssignPropertyCall_c
    FMGL(size) = x10aux::nullCheck(src)->FMGL(size);
    
    //#line 63 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10LocalDecl_c
    x10_int size = ((x10_int) (x10aux::nullCheck(src)->FMGL(size)));
    
    //#line 64 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10LocalDecl_c
    x10::util::IndexedMemoryChunk<TPMGL(T) > dst = x10::util::IndexedMemoryChunk<void>::allocate<TPMGL(T) >(size, 8, false, false);
    
    //#line 66 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": Eval of x10.ast.X10Call_c
    x10::util::IndexedMemoryChunk<void>::copy<TPMGL(T) >(x10aux::nullCheck(src)->
                                                           FMGL(raw),((x10_int)0),dst,((x10_int)0),size);
    
    //#line 67 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": Eval of x10.ast.X10FieldAssign_c
    this->FMGL(raw) = dst;
}
template<class TPMGL(T)> x10::lang::Rail<TPMGL(T)>* x10::lang::Rail<TPMGL(T)>::_make(
                           x10::lang::Rail<TPMGL(T)>* src)
{
    x10::lang::Rail<TPMGL(T)>* this_ = new (memset(x10aux::alloc<x10::lang::Rail<TPMGL(T)> >(), 0, sizeof(x10::lang::Rail<TPMGL(T)>))) x10::lang::Rail<TPMGL(T)>();
    this_->_constructor(src);
    return this_;
}



//#line 72 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10ConstructorDecl_c
template<class TPMGL(T)> void x10::lang::Rail<TPMGL(T)>::_constructor(
                           x10_long size) {
    
    //#line 73 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.AssignPropertyCall_c
    FMGL(size) = size;
    
    //#line 74 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": Eval of x10.ast.X10FieldAssign_c
    this->FMGL(raw) = x10::util::IndexedMemoryChunk<void>::allocate<TPMGL(T) >(size, 8, false, true);
}
template<class TPMGL(T)> x10::lang::Rail<TPMGL(T)>* x10::lang::Rail<TPMGL(T)>::_make(
                           x10_long size) {
    x10::lang::Rail<TPMGL(T)>* this_ = new (memset(x10aux::alloc<x10::lang::Rail<TPMGL(T)> >(), 0, sizeof(x10::lang::Rail<TPMGL(T)>))) x10::lang::Rail<TPMGL(T)>();
    this_->_constructor(size);
    return this_;
}



//#line 77 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10ConstructorDecl_c
template<class TPMGL(T)> void x10::lang::Rail<TPMGL(T)>::_constructor(
                           x10_long size, TPMGL(T) init) {
    
    //#line 78 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.AssignPropertyCall_c
    FMGL(size) = size;
    
    //#line 79 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": Eval of x10.ast.X10FieldAssign_c
    this->FMGL(raw) = x10::util::IndexedMemoryChunk<void>::allocate<TPMGL(T) >(size, 8, false, false);
    
    //#line 80 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": polyglot.ast.For_c
    {
        x10_long i;
        for (
             //#line 80 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10LocalDecl_c
             i = ((x10_long)0ll); ((i) < (size)); 
                                                  //#line 80 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": Eval of x10.ast.X10LocalAssign_c
                                                  i = ((x10_long) ((i) + (((x10_long)1ll)))))
        {
            
            //#line 80 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": Eval of x10.ast.X10Call_c
            (this->FMGL(raw))->__set(i, init);
        }
    }
    
}
template<class TPMGL(T)> x10::lang::Rail<TPMGL(T)>* x10::lang::Rail<TPMGL(T)>::_make(
                           x10_long size, TPMGL(T) init) {
    x10::lang::Rail<TPMGL(T)>* this_ = new (memset(x10aux::alloc<x10::lang::Rail<TPMGL(T)> >(), 0, sizeof(x10::lang::Rail<TPMGL(T)>))) x10::lang::Rail<TPMGL(T)>();
    this_->_constructor(size, init);
    return this_;
}



//#line 83 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10ConstructorDecl_c
template<class TPMGL(T)> void x10::lang::Rail<TPMGL(T)>::_constructor(
                           x10_long size, x10::lang::Fun_0_1<x10_long, TPMGL(T)>* init) {
    
    //#line 84 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.AssignPropertyCall_c
    FMGL(size) = size;
    
    //#line 85 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": Eval of x10.ast.X10FieldAssign_c
    this->FMGL(raw) = x10::util::IndexedMemoryChunk<void>::allocate<TPMGL(T) >(size, 8, false, false);
    
    //#line 86 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": polyglot.ast.For_c
    {
        x10_long i;
        for (
             //#line 86 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10LocalDecl_c
             i = ((x10_long) (((x10_int)0))); ((i) < (size));
             
             //#line 86 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": Eval of x10.ast.X10LocalAssign_c
             i = ((x10_long) ((i) + (((x10_long)1ll))))) {
            
            //#line 86 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": Eval of x10.ast.X10Call_c
            (this->FMGL(raw))->__set(i, x10::lang::Fun_0_1<x10_long, TPMGL(T)>::__apply(x10aux::nullCheck(init), 
              i));
        }
    }
    
}
template<class TPMGL(T)> x10::lang::Rail<TPMGL(T)>* x10::lang::Rail<TPMGL(T)>::_make(
                           x10_long size, x10::lang::Fun_0_1<x10_long, TPMGL(T)>* init)
{
    x10::lang::Rail<TPMGL(T)>* this_ = new (memset(x10aux::alloc<x10::lang::Rail<TPMGL(T)> >(), 0, sizeof(x10::lang::Rail<TPMGL(T)>))) x10::lang::Rail<TPMGL(T)>();
    this_->_constructor(size, init);
    return this_;
}



//#line 89 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10MethodDecl_c
template<class TPMGL(T)> TPMGL(T) x10::lang::Rail<TPMGL(T)>::__apply(
  x10_long index) {
    
    //#line 89 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10Return_c
    return (this->FMGL(raw))->__apply(index);
    
}

//#line 91 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10MethodDecl_c
template<class TPMGL(T)> TPMGL(T) x10::lang::Rail<TPMGL(T)>::__set(
  x10_long index, TPMGL(T) v) {
    
    //#line 92 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": Eval of x10.ast.X10Call_c
    (this->FMGL(raw))->__set(index, v);
    
    //#line 93 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10Return_c
    return v;
    
}

//#line 96 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10MethodDecl_c

//#line 101 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10MethodDecl_c

//#line 108 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10MethodDecl_c
template<class TPMGL(T)> void x10::lang::Rail<TPMGL(T)>::clear(
  ) {
    
    //#line 109 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": Eval of x10.ast.X10Call_c
    (this->FMGL(raw))->clear(((x10_long) (((x10_int)0))), this->
                                                            FMGL(size));
}

//#line 114 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10ConstructorDecl_c
template<class TPMGL(T)> void x10::lang::Rail<TPMGL(T)>::_constructor(
                           x10_int size) {
    
    //#line 115 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.AssignPropertyCall_c
    FMGL(size) = ((x10_long) (size));
    
    //#line 116 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": Eval of x10.ast.X10FieldAssign_c
    this->FMGL(raw) = x10::util::IndexedMemoryChunk<void>::allocate<TPMGL(T) >(size, 8, false, true);
}
template<class TPMGL(T)> x10::lang::Rail<TPMGL(T)>* x10::lang::Rail<TPMGL(T)>::_make(
                           x10_int size) {
    x10::lang::Rail<TPMGL(T)>* this_ = new (memset(x10aux::alloc<x10::lang::Rail<TPMGL(T)> >(), 0, sizeof(x10::lang::Rail<TPMGL(T)>))) x10::lang::Rail<TPMGL(T)>();
    this_->_constructor(size);
    return this_;
}



//#line 119 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10ConstructorDecl_c
template<class TPMGL(T)> void x10::lang::Rail<TPMGL(T)>::_constructor(
                           x10_int size, TPMGL(T) init) {
    
    //#line 120 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.AssignPropertyCall_c
    FMGL(size) = ((x10_long) (size));
    
    //#line 121 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": Eval of x10.ast.X10FieldAssign_c
    this->FMGL(raw) = x10::util::IndexedMemoryChunk<void>::allocate<TPMGL(T) >(size, 8, false, false);
    
    //#line 122 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": polyglot.ast.For_c
    {
        x10_int i;
        for (
             //#line 122 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10LocalDecl_c
             i = ((x10_int)0); ((i) < (size)); 
                                               //#line 122 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": Eval of x10.ast.X10LocalAssign_c
                                               i = ((x10_int) ((i) + (((x10_int)1)))))
        {
            
            //#line 122 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": Eval of x10.ast.X10Call_c
            (this->FMGL(raw))->__set(i, init);
        }
    }
    
}
template<class TPMGL(T)> x10::lang::Rail<TPMGL(T)>* x10::lang::Rail<TPMGL(T)>::_make(
                           x10_int size, TPMGL(T) init) {
    x10::lang::Rail<TPMGL(T)>* this_ = new (memset(x10aux::alloc<x10::lang::Rail<TPMGL(T)> >(), 0, sizeof(x10::lang::Rail<TPMGL(T)>))) x10::lang::Rail<TPMGL(T)>();
    this_->_constructor(size, init);
    return this_;
}



//#line 125 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10ConstructorDecl_c
template<class TPMGL(T)> void x10::lang::Rail<TPMGL(T)>::_constructor(
                           x10_int size, x10::lang::Fun_0_1<x10_int, TPMGL(T)>* init) {
    
    //#line 126 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.AssignPropertyCall_c
    FMGL(size) = ((x10_long) (size));
    
    //#line 127 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": Eval of x10.ast.X10FieldAssign_c
    this->FMGL(raw) = x10::util::IndexedMemoryChunk<void>::allocate<TPMGL(T) >(size, 8, false, false);
    
    //#line 128 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": polyglot.ast.For_c
    {
        x10_int i;
        for (
             //#line 128 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10LocalDecl_c
             i = ((x10_int)0); ((i) < (size)); 
                                               //#line 128 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": Eval of x10.ast.X10LocalAssign_c
                                               i = ((x10_int) ((i) + (((x10_int)1)))))
        {
            
            //#line 128 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": Eval of x10.ast.X10Call_c
            (this->FMGL(raw))->__set(i, x10::lang::Fun_0_1<x10_int, TPMGL(T)>::__apply(x10aux::nullCheck(init), 
              i));
        }
    }
    
}
template<class TPMGL(T)> x10::lang::Rail<TPMGL(T)>* x10::lang::Rail<TPMGL(T)>::_make(
                           x10_int size, x10::lang::Fun_0_1<x10_int, TPMGL(T)>* init)
{
    x10::lang::Rail<TPMGL(T)>* this_ = new (memset(x10aux::alloc<x10::lang::Rail<TPMGL(T)> >(), 0, sizeof(x10::lang::Rail<TPMGL(T)>))) x10::lang::Rail<TPMGL(T)>();
    this_->_constructor(size, init);
    return this_;
}



//#line 131 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10MethodDecl_c
template<class TPMGL(T)> TPMGL(T) x10::lang::Rail<TPMGL(T)>::__apply(
  x10_int index) {
    
    //#line 131 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10Return_c
    return (this->FMGL(raw))->__apply(index);
    
}

//#line 133 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10MethodDecl_c
template<class TPMGL(T)> TPMGL(T) x10::lang::Rail<TPMGL(T)>::__set(
  x10_int index, TPMGL(T) v) {
    
    //#line 134 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": Eval of x10.ast.X10Call_c
    (this->FMGL(raw))->__set(index, v);
    
    //#line 135 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10Return_c
    return v;
    
}

//#line 138 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10MethodDecl_c

//#line 17 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10MethodDecl_c
template<class TPMGL(T)> x10::lang::Rail<TPMGL(T)>* x10::lang::Rail<TPMGL(T)>::x10__lang__Rail____this__x10__lang__Rail(
  ) {
    
    //#line 17 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10Return_c
    return this;
    
}
template<class TPMGL(T)> const x10aux::serialization_id_t x10::lang::Rail<TPMGL(T)>::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(x10::lang::Rail<TPMGL(T)>::_deserializer, x10aux::CLOSURE_KIND_NOT_ASYNC);

template<class TPMGL(T)> void x10::lang::Rail<TPMGL(T)>::_serialize_body(x10aux::serialization_buffer& buf) {
    buf.write(this->FMGL(raw));
    buf.write(this->FMGL(size));
    
}

template<class TPMGL(T)> x10::lang::Reference* x10::lang::Rail<TPMGL(T)>::_deserializer(x10aux::deserialization_buffer& buf) {
    x10::lang::Rail<TPMGL(T)>* this_ = new (memset(x10aux::alloc<x10::lang::Rail<TPMGL(T)> >(), 0, sizeof(x10::lang::Rail<TPMGL(T)>))) x10::lang::Rail<TPMGL(T)>();
    buf.record_reference(this_);
    this_->_deserialize_body(buf);
    return this_;
}

template<class TPMGL(T)> void x10::lang::Rail<TPMGL(T)>::_deserialize_body(x10aux::deserialization_buffer& buf) {
    FMGL(raw) = buf.read<x10::util::IndexedMemoryChunk<TPMGL(T) > >();
    FMGL(size) = buf.read<x10_long>();
}

template<class TPMGL(T)> void x10::lang::Rail<void>::copy(x10::lang::Rail<TPMGL(T)>* src,
                                                          x10::lang::Rail<TPMGL(T)>* dst)
{
    
    //#line 97 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10If_c
    if ((!x10aux::struct_equals(x10aux::nullCheck(src)->FMGL(size),
                                x10aux::nullCheck(dst)->FMGL(size))))
    {
        
        //#line 97 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": polyglot.ast.Throw_c
        x10aux::throwException(x10aux::nullCheck(x10::lang::IllegalArgumentException::_make(x10aux::makeStringLit("source and destination do not have equal size"))));
    }
    
    //#line 98 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": Eval of x10.ast.X10Call_c
    x10::util::IndexedMemoryChunk<void>::copy<TPMGL(T) >(x10aux::nullCheck(src)->
                                                           FMGL(raw),((x10_int)0),x10aux::nullCheck(dst)->
                                                                                    FMGL(raw),((x10_int)0),(x10aux::nullCheck(src)->
                                                                                                              FMGL(raw))->length());
}
template<class TPMGL(T)> void x10::lang::Rail<void>::copy(x10::lang::Rail<TPMGL(T)>* src,
                                                          x10_long srcIndex,
                                                          x10::lang::Rail<TPMGL(T)>* dst,
                                                          x10_long dstIndex,
                                                          x10_long numElems)
{
    
    //#line 103 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": polyglot.ast.For_c
    {
        x10_long i;
        for (
             //#line 103 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10LocalDecl_c
             i = ((x10_long)0ll); ((i) < (numElems)); 
                                                      //#line 103 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": Eval of x10.ast.X10LocalAssign_c
                                                      i =
                                                        ((x10_long) ((i) + (((x10_long)1ll)))))
        {
            
            //#line 91 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10LocalDecl_c
            x10_long index55581 = ((x10_long) ((dstIndex) + (i)));
            
            //#line 91 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10LocalDecl_c
            TPMGL(T) v55582 = (__extension__ ({
                
                //#line 89 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10LocalDecl_c
                x10_long index55583 = ((x10_long) ((srcIndex) + (i)));
                (x10aux::nullCheck(src)->FMGL(raw))->__apply(index55583);
            }))
            ;
            
            //#line 91 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": x10.ast.X10LocalDecl_c
            TPMGL(T) ret55584;
            
            //#line 92 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": Eval of x10.ast.X10Call_c
            (x10aux::nullCheck(dst)->FMGL(raw))->__set(index55581, v55582);
            
            //#line 93 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": Eval of x10.ast.X10LocalAssign_c
            ret55584 = v55582;
            
            //#line 91 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": Eval of x10.ast.X10Local_c
            ret55584;
        }
    }
    
}
template<class TPMGL(T)> void x10::lang::Rail<void>::copy(x10::lang::Rail<TPMGL(T)>* src,
                                                          x10_int srcIndex,
                                                          x10::lang::Rail<TPMGL(T)>* dst,
                                                          x10_int dstIndex,
                                                          x10_int numElems)
{
    
    //#line 141 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Rail.x10": Eval of x10.ast.X10Call_c
    x10::util::IndexedMemoryChunk<void>::copy<TPMGL(T) >(x10aux::nullCheck(src)->
                                                           FMGL(raw),srcIndex,x10aux::nullCheck(dst)->
                                                                                FMGL(raw),dstIndex,numElems);
}
#endif // X10_LANG_RAIL_H_IMPLEMENTATION
#endif // __X10_LANG_RAIL_H_NODEPS
