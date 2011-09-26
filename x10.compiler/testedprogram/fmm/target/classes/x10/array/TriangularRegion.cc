/*************************************************/
/* START of TriangularRegion */
#include <x10/array/TriangularRegion.h>

#include <x10/array/Region.h>
#include <x10/lang/Int.h>
#include <x10/lang/Boolean.h>
#include <x10/array/Point.h>
#include <x10/lang/Fun_0_1.h>
#include <x10/lang/ArrayIndexOutOfBoundsException.h>
#include <x10/lang/String.h>
#include <x10/lang/UnsupportedOperationException.h>
#include <x10/lang/IntRange.h>
#include <x10/array/RectRegion1D.h>
#include <x10/lang/Iterator.h>
#include <x10/array/TriangularRegion__TriangularRegionIterator.h>
#ifndef X10_ARRAY_TRIANGULARREGION__CLOSURE__1_CLOSURE
#define X10_ARRAY_TRIANGULARREGION__CLOSURE__1_CLOSURE
#include <x10/lang/Closure.h>
#include <x10/lang/Fun_0_1.h>
class x10_array_TriangularRegion__closure__1 : public x10::lang::Closure {
    public:
    
    static x10::lang::Fun_0_1<x10_int, x10_int>::itable<x10_array_TriangularRegion__closure__1> _itable;
    static x10aux::itable_entry _itables[2];
    
    virtual x10aux::itable_entry* _getITables() { return _itables; }
    
    // closure body
    x10_int __apply(x10_int i) {
        
        //#line 38 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10If_c
        if ((x10aux::struct_equals(i, ((x10_int)0)))) {
            
            //#line 38 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10Return_c
            return saved_this->FMGL(rowMin);
            
        } else 
        //#line 39 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10If_c
        if ((x10aux::struct_equals(i, ((x10_int)1)))) {
            
            //#line 39 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10Return_c
            return saved_this->FMGL(colMin);
            
        } else {
            
            //#line 40 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": polyglot.ast.Throw_c
            x10aux::throwException(x10aux::nullCheck(x10::lang::ArrayIndexOutOfBoundsException::_make(((((((x10aux::string_utils::lit("min: ")) + (i))) + (x10aux::string_utils::lit(" is not a valid rank for ")))) + (saved_this)))));
        }
        
    }
    
    // captured environment
    x10aux::ref<x10::array::TriangularRegion> saved_this;
    
    x10aux::serialization_id_t _get_serialization_id() {
        return _serialization_id;
    }
    
    void _serialize_body(x10aux::serialization_buffer &buf) {
        buf.write(this->saved_this);
    }
    
    template<class __T> static x10aux::ref<__T> _deserialize(x10aux::deserialization_buffer &buf) {
        x10_array_TriangularRegion__closure__1* storage = x10aux::alloc<x10_array_TriangularRegion__closure__1>();
        buf.record_reference(x10aux::ref<x10_array_TriangularRegion__closure__1>(storage));
        x10aux::ref<x10::array::TriangularRegion> that_saved_this = buf.read<x10aux::ref<x10::array::TriangularRegion> >();
        x10aux::ref<x10_array_TriangularRegion__closure__1> this_ = new (storage) x10_array_TriangularRegion__closure__1(that_saved_this);
        return this_;
    }
    
    x10_array_TriangularRegion__closure__1(x10aux::ref<x10::array::TriangularRegion> saved_this) : saved_this(saved_this) { }
    
    static const x10aux::serialization_id_t _serialization_id;
    
    static const x10aux::RuntimeType* getRTT() { return x10aux::getRTT<x10::lang::Fun_0_1<x10_int, x10_int> >(); }
    virtual const x10aux::RuntimeType *_type() const { return x10aux::getRTT<x10::lang::Fun_0_1<x10_int, x10_int> >(); }
    
    x10aux::ref<x10::lang::String> toString() {
        return x10aux::string_utils::lit(this->toNativeString());
    }
    
    const char* toNativeString() {
        return "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10:37-41";
    }

};

#endif // X10_ARRAY_TRIANGULARREGION__CLOSURE__1_CLOSURE
#ifndef X10_ARRAY_TRIANGULARREGION__CLOSURE__2_CLOSURE
#define X10_ARRAY_TRIANGULARREGION__CLOSURE__2_CLOSURE
#include <x10/lang/Closure.h>
#include <x10/lang/Fun_0_1.h>
class x10_array_TriangularRegion__closure__2 : public x10::lang::Closure {
    public:
    
    static x10::lang::Fun_0_1<x10_int, x10_int>::itable<x10_array_TriangularRegion__closure__2> _itable;
    static x10aux::itable_entry _itables[2];
    
    virtual x10aux::itable_entry* _getITables() { return _itables; }
    
    // closure body
    x10_int __apply(x10_int i) {
        
        //#line 44 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10If_c
        if ((x10aux::struct_equals(i, ((x10_int)0)))) {
            
            //#line 44 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10Return_c
            return ((x10_int) ((saved_this->FMGL(rowMin)) + (saved_this->
                                                               FMGL(dim))));
            
        } else 
        //#line 45 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10If_c
        if ((x10aux::struct_equals(i, ((x10_int)1)))) {
            
            //#line 45 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10Return_c
            return ((x10_int) ((saved_this->FMGL(colMin)) + (saved_this->
                                                               FMGL(dim))));
            
        } else {
            
            //#line 46 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": polyglot.ast.Throw_c
            x10aux::throwException(x10aux::nullCheck(x10::lang::ArrayIndexOutOfBoundsException::_make(((((((x10aux::string_utils::lit("max: ")) + (i))) + (x10aux::string_utils::lit(" is not a valid rank for ")))) + (saved_this)))));
        }
        
    }
    
    // captured environment
    x10aux::ref<x10::array::TriangularRegion> saved_this;
    
    x10aux::serialization_id_t _get_serialization_id() {
        return _serialization_id;
    }
    
    void _serialize_body(x10aux::serialization_buffer &buf) {
        buf.write(this->saved_this);
    }
    
    template<class __T> static x10aux::ref<__T> _deserialize(x10aux::deserialization_buffer &buf) {
        x10_array_TriangularRegion__closure__2* storage = x10aux::alloc<x10_array_TriangularRegion__closure__2>();
        buf.record_reference(x10aux::ref<x10_array_TriangularRegion__closure__2>(storage));
        x10aux::ref<x10::array::TriangularRegion> that_saved_this = buf.read<x10aux::ref<x10::array::TriangularRegion> >();
        x10aux::ref<x10_array_TriangularRegion__closure__2> this_ = new (storage) x10_array_TriangularRegion__closure__2(that_saved_this);
        return this_;
    }
    
    x10_array_TriangularRegion__closure__2(x10aux::ref<x10::array::TriangularRegion> saved_this) : saved_this(saved_this) { }
    
    static const x10aux::serialization_id_t _serialization_id;
    
    static const x10aux::RuntimeType* getRTT() { return x10aux::getRTT<x10::lang::Fun_0_1<x10_int, x10_int> >(); }
    virtual const x10aux::RuntimeType *_type() const { return x10aux::getRTT<x10::lang::Fun_0_1<x10_int, x10_int> >(); }
    
    x10aux::ref<x10::lang::String> toString() {
        return x10aux::string_utils::lit(this->toNativeString());
    }
    
    const char* toNativeString() {
        return "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10:43-47";
    }

};

#endif // X10_ARRAY_TRIANGULARREGION__CLOSURE__2_CLOSURE
x10::lang::Iterable<x10aux::ref<x10::array::Point> >::itable<x10::array::TriangularRegion >  x10::array::TriangularRegion::_itable_0(&x10::array::TriangularRegion::equals, &x10::array::TriangularRegion::hashCode, &x10::array::TriangularRegion::iterator, &x10::array::TriangularRegion::toString, &x10::array::TriangularRegion::typeName);
x10::lang::Any::itable<x10::array::TriangularRegion >  x10::array::TriangularRegion::_itable_1(&x10::array::TriangularRegion::equals, &x10::array::TriangularRegion::hashCode, &x10::array::TriangularRegion::toString, &x10::array::TriangularRegion::typeName);
x10aux::itable_entry x10::array::TriangularRegion::_itables[3] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Iterable<x10aux::ref<x10::array::Point> > >, &_itable_0), x10aux::itable_entry(&x10aux::getRTT<x10::lang::Any>, &_itable_1), x10aux::itable_entry(NULL, (void*)x10aux::getRTT<x10::array::TriangularRegion>())};
 /* static type TriangularRegion(rank: x10.lang.Int) = x10.array.TriangularRegion{self.rank==rank}; */ 

//#line 7 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10FieldDecl_c

//#line 8 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10FieldDecl_c

//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10FieldDecl_c

//#line 10 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10FieldDecl_c

//#line 16 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10ConstructorDecl_c
void x10::array::TriangularRegion::_constructor(x10_int rowMin, x10_int colMin,
                                                x10_int size,
                                                x10_boolean lower) {
    
    //#line 17 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 17 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::Region> this59251 = ((x10aux::ref<x10::array::Region>)this);
        {
            
            //#line 469 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10ConstructorCall_c
            (this59251)->::x10::lang::Object::_constructor();
            
            //#line 472 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this59251)->FMGL(rank) = ((x10_int)2);
            
            //#line 472 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this59251)->FMGL(rect) = false;
            
            //#line 472 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this59251)->FMGL(zeroBased) = true;
            
            //#line 472 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this59251)->FMGL(rail) = false;
        }
        
    }))
    ;
    
    //#line 16 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.AssignPropertyCall_c
    
    //#line 18 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<x10::array::TriangularRegion>)this)->FMGL(dim) = size;
    
    //#line 19 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<x10::array::TriangularRegion>)this)->FMGL(rowMin) = rowMin;
    
    //#line 20 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<x10::array::TriangularRegion>)this)->FMGL(colMin) = colMin;
    
    //#line 21 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<x10::array::TriangularRegion>)this)->FMGL(lower) = lower;
    
}
x10aux::ref<x10::array::TriangularRegion> x10::array::TriangularRegion::_make(
  x10_int rowMin,
  x10_int colMin,
  x10_int size,
  x10_boolean lower) {
    x10aux::ref<x10::array::TriangularRegion> this_ = new (memset(x10aux::alloc<x10::array::TriangularRegion>(), 0, sizeof(x10::array::TriangularRegion))) x10::array::TriangularRegion();
    this_->_constructor(rowMin, colMin, size, lower);
    return this_;
}



//#line 24 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10MethodDecl_c
x10_boolean x10::array::TriangularRegion::isConvex() {
    
    //#line 25 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10Return_c
    return true;
    
}

//#line 28 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10MethodDecl_c
x10_boolean x10::array::TriangularRegion::isEmpty() {
    
    //#line 29 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10Return_c
    return false;
    
}

//#line 32 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10MethodDecl_c
x10_int x10::array::TriangularRegion::indexOf(x10aux::ref<x10::array::Point> pt) {
    
    //#line 33 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10If_c
    if ((!x10aux::struct_equals(x10aux::nullCheck(pt)->FMGL(rank), ((x10_int)2))))
    {
        
        //#line 33 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10Return_c
        return ((x10_int)-1);
        
    }
    
    //#line 34 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10Return_c
    return ((x10_int) ((((x10_int) ((((x10_int) ((x10aux::nullCheck(pt)->x10::array::Point::__apply(
                                                    ((x10_int)0))) * (x10aux::nullCheck(pt)->x10::array::Point::__apply(
                                                                        ((x10_int)0)))))) / x10aux::zeroCheck(((x10_int)2))))) + (x10aux::nullCheck(pt)->x10::array::Point::__apply(
                                                                                                                                    ((x10_int)1)))));
    
}

//#line 37 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::lang::Fun_0_1<x10_int, x10_int> >
  x10::array::TriangularRegion::min(
  ) {
    
    //#line 37 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10Return_c
    return x10aux::ref<x10::lang::Fun_0_1<x10_int, x10_int> >(x10aux::ref<x10_array_TriangularRegion__closure__1>(new (x10aux::alloc<x10::lang::Fun_0_1<x10_int, x10_int> >(sizeof(x10_array_TriangularRegion__closure__1)))x10_array_TriangularRegion__closure__1(this)));
    
}

//#line 43 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::lang::Fun_0_1<x10_int, x10_int> >
  x10::array::TriangularRegion::max(
  ) {
    
    //#line 43 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10Return_c
    return x10aux::ref<x10::lang::Fun_0_1<x10_int, x10_int> >(x10aux::ref<x10_array_TriangularRegion__closure__2>(new (x10aux::alloc<x10::lang::Fun_0_1<x10_int, x10_int> >(sizeof(x10_array_TriangularRegion__closure__2)))x10_array_TriangularRegion__closure__2(this)));
    
}

//#line 49 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10MethodDecl_c
x10_int x10::array::TriangularRegion::size(
  ) {
    
    //#line 50 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10Return_c
    return ((x10_int) ((((x10_int) ((((x10aux::ref<x10::array::TriangularRegion>)this)->
                                       FMGL(dim)) * (((x10_int) ((((x10aux::ref<x10::array::TriangularRegion>)this)->
                                                                    FMGL(dim)) + (((x10_int)1)))))))) / x10aux::zeroCheck(((x10_int)2))));
    
}

//#line 53 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10MethodDecl_c
x10_boolean x10::array::TriangularRegion::contains(
  x10aux::ref<x10::array::Point> p) {
    
    //#line 54 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10If_c
    if ((x10aux::struct_equals(x10aux::nullCheck(p)->
                                 FMGL(rank),
                               ((x10_int)2))))
    {
        
        //#line 55 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10If_c
        if (((x10aux::nullCheck(p)->x10::array::Point::__apply(
                ((x10_int)0))) >= (((x10aux::ref<x10::array::TriangularRegion>)this)->
                                     FMGL(rowMin))) &&
            ((x10aux::nullCheck(p)->x10::array::Point::__apply(
                ((x10_int)0))) <= (((x10_int) ((((x10aux::ref<x10::array::TriangularRegion>)this)->
                                                  FMGL(rowMin)) + (((x10aux::ref<x10::array::TriangularRegion>)this)->
                                                                     FMGL(dim)))))))
        {
            
            //#line 56 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10If_c
            if (((x10aux::ref<x10::array::TriangularRegion>)this)->
                  FMGL(lower))
            {
                
                //#line 57 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10If_c
                if (((x10aux::nullCheck(p)->x10::array::Point::__apply(
                        ((x10_int)1))) >= (((x10aux::ref<x10::array::TriangularRegion>)this)->
                                             FMGL(colMin))) &&
                    ((x10aux::nullCheck(p)->x10::array::Point::__apply(
                        ((x10_int)1))) <= (x10aux::nullCheck(p)->x10::array::Point::__apply(
                                             ((x10_int)0)))))
                {
                    
                    //#line 58 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10Return_c
                    return true;
                    
                }
                
            }
            else
            
            //#line 62 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10If_c
            if (((x10aux::nullCheck(p)->x10::array::Point::__apply(
                    ((x10_int)1))) <= (((x10_int) ((((x10aux::ref<x10::array::TriangularRegion>)this)->
                                                      FMGL(colMin)) + (((x10aux::ref<x10::array::TriangularRegion>)this)->
                                                                         FMGL(dim)))))) &&
                ((x10aux::nullCheck(p)->x10::array::Point::__apply(
                    ((x10_int)1))) >= (x10aux::nullCheck(p)->x10::array::Point::__apply(
                                         ((x10_int)0)))))
            {
                
                //#line 63 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10Return_c
                return true;
                
            }
            
        }
        
        //#line 67 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10Return_c
        return false;
        
    }
    
    //#line 69 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": polyglot.ast.Throw_c
    x10aux::throwException(x10aux::nullCheck(x10::lang::UnsupportedOperationException::_make(((((x10aux::string_utils::lit("contains(")) + (p))) + (x10aux::string_utils::lit(")"))))));
}

//#line 72 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10MethodDecl_c
x10_boolean x10::array::TriangularRegion::contains(
  x10aux::ref<x10::array::Region> r) {
    
    //#line 74 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": polyglot.ast.Throw_c
    x10aux::throwException(x10aux::nullCheck(x10::lang::UnsupportedOperationException::_make(x10aux::string_utils::lit("contains(Region)"))));
}

//#line 77 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::Region> x10::array::TriangularRegion::complement(
  ) {
    
    //#line 79 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": polyglot.ast.Throw_c
    x10aux::throwException(x10aux::nullCheck(x10::lang::UnsupportedOperationException::_make(x10aux::string_utils::lit("complement()"))));
}

//#line 82 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::Region> x10::array::TriangularRegion::intersection(
  x10aux::ref<x10::array::Region> t) {
    
    //#line 84 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": polyglot.ast.Throw_c
    x10aux::throwException(x10aux::nullCheck(x10::lang::UnsupportedOperationException::_make(x10aux::string_utils::lit("intersection()"))));
}

//#line 87 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::Region> x10::array::TriangularRegion::product(
  x10aux::ref<x10::array::Region> r) {
    
    //#line 89 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": polyglot.ast.Throw_c
    x10aux::throwException(x10aux::nullCheck(x10::lang::UnsupportedOperationException::_make(x10aux::string_utils::lit("product()"))));
}

//#line 92 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::Region> x10::array::TriangularRegion::translate(
  x10aux::ref<x10::array::Point> v) {
    
    //#line 93 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10Return_c
    return x10aux::class_cast<x10aux::ref<x10::array::Region> >((__extension__ ({
        
        //#line 93 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::TriangularRegion> alloc58923 =
          
        x10aux::ref<x10::array::TriangularRegion>((new (memset(x10aux::alloc<x10::array::TriangularRegion>(), 0, sizeof(x10::array::TriangularRegion))) x10::array::TriangularRegion()))
        ;
        
        //#line 93 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.StmtExpr_c
        (__extension__ ({
            
            //#line 16 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10LocalDecl_c
            x10_int rowMin59253 = ((x10_int) ((((x10aux::ref<x10::array::TriangularRegion>)this)->
                                                 FMGL(rowMin)) + (x10aux::nullCheck(v)->x10::array::Point::__apply(
                                                                    ((x10_int)0)))));
            
            //#line 16 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10LocalDecl_c
            x10_int colMin59254 = ((x10_int) ((((x10aux::ref<x10::array::TriangularRegion>)this)->
                                                 FMGL(colMin)) + (x10aux::nullCheck(v)->x10::array::Point::__apply(
                                                                    ((x10_int)1)))));
            
            //#line 16 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10LocalDecl_c
            x10_int size59255 = ((x10aux::ref<x10::array::TriangularRegion>)this)->
                                  FMGL(dim);
            
            //#line 16 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10LocalDecl_c
            x10_boolean lower59256 = ((x10aux::ref<x10::array::TriangularRegion>)this)->
                                       FMGL(lower);
            {
                
                //#line 17 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.StmtExpr_c
                (__extension__ ({
                    {
                        
                        //#line 469 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10ConstructorCall_c
                        (alloc58923)->::x10::lang::Object::_constructor();
                        
                        //#line 472 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10FieldAssign_c
                        x10aux::nullCheck(alloc58923)->
                          FMGL(rank) = ((x10_int)2);
                        
                        //#line 472 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10FieldAssign_c
                        x10aux::nullCheck(alloc58923)->
                          FMGL(rect) = false;
                        
                        //#line 472 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10FieldAssign_c
                        x10aux::nullCheck(alloc58923)->
                          FMGL(zeroBased) =
                          true;
                        
                        //#line 472 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10FieldAssign_c
                        x10aux::nullCheck(alloc58923)->
                          FMGL(rail) = false;
                    }
                    
                }))
                ;
                
                //#line 18 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc58923)->
                  FMGL(dim) = size59255;
                
                //#line 19 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc58923)->
                  FMGL(rowMin) = rowMin59253;
                
                //#line 20 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc58923)->
                  FMGL(colMin) = colMin59254;
                
                //#line 21 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc58923)->
                  FMGL(lower) = lower59256;
            }
            
        }))
        ;
        alloc58923;
    }))
    );
    
}

//#line 96 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::Region> x10::array::TriangularRegion::projection(
  x10_int axis) {
    
    //#line 97 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": polyglot.ast.Switch_c
    switch (axis) {
        
        //#line 98 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": polyglot.ast.Case_c
        case ((x10_int)0): ;
        {
            
            //#line 99 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10Return_c
            return (__extension__ ({
                
                //#line 423 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10LocalDecl_c
                x10::lang::IntRange r59263 =
                  x10::lang::IntRange::_make(((x10aux::ref<x10::array::TriangularRegion>)this)->
                                               FMGL(rowMin), ((x10_int) ((((x10aux::ref<x10::array::TriangularRegion>)this)->
                                                                            FMGL(rowMin)) + (((x10aux::ref<x10::array::TriangularRegion>)this)->
                                                                                               FMGL(dim)))));
                x10aux::class_cast<x10aux::ref<x10::array::Region> >((__extension__ ({
                    
                    //#line 424 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10LocalDecl_c
                    x10aux::ref<x10::array::RectRegion1D> alloc2063159264 =
                      
                    x10aux::ref<x10::array::RectRegion1D>((new (memset(x10aux::alloc<x10::array::RectRegion1D>(), 0, sizeof(x10::array::RectRegion1D))) x10::array::RectRegion1D()))
                    ;
                    
                    //#line 424 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10ConstructorCall_c
                    (alloc2063159264)->::x10::array::RectRegion1D::_constructor(
                      r59263->
                        FMGL(min),
                      r59263->
                        FMGL(max));
                    alloc2063159264;
                }))
                );
            }))
            ;
            
        }
        //#line 100 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": polyglot.ast.Case_c
        case ((x10_int)1): ;
        {
            
            //#line 101 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10Return_c
            return (__extension__ ({
                
                //#line 423 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10LocalDecl_c
                x10::lang::IntRange r59265 =
                  x10::lang::IntRange::_make(((x10aux::ref<x10::array::TriangularRegion>)this)->
                                               FMGL(colMin), ((x10_int) ((((x10aux::ref<x10::array::TriangularRegion>)this)->
                                                                            FMGL(colMin)) + (((x10aux::ref<x10::array::TriangularRegion>)this)->
                                                                                               FMGL(dim)))));
                x10aux::class_cast<x10aux::ref<x10::array::Region> >((__extension__ ({
                    
                    //#line 424 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10LocalDecl_c
                    x10aux::ref<x10::array::RectRegion1D> alloc2063159266 =
                      
                    x10aux::ref<x10::array::RectRegion1D>((new (memset(x10aux::alloc<x10::array::RectRegion1D>(), 0, sizeof(x10::array::RectRegion1D))) x10::array::RectRegion1D()))
                    ;
                    
                    //#line 424 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10ConstructorCall_c
                    (alloc2063159266)->::x10::array::RectRegion1D::_constructor(
                      r59265->
                        FMGL(min),
                      r59265->
                        FMGL(max));
                    alloc2063159266;
                }))
                );
            }))
            ;
            
        }
        //#line 102 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": polyglot.ast.Case_c
        default: ;
        {
            
            //#line 103 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": polyglot.ast.Throw_c
            x10aux::throwException(x10aux::nullCheck(x10::lang::UnsupportedOperationException::_make(((((x10aux::string_utils::lit("projection(")) + (axis))) + (x10aux::string_utils::lit(")"))))));
        }
    }
}

//#line 107 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::Region> x10::array::TriangularRegion::eliminate(
  x10_int axis) {
    
    //#line 108 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": polyglot.ast.Switch_c
    switch (axis) {
        
        //#line 109 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": polyglot.ast.Case_c
        case ((x10_int)0): ;
        {
            
            //#line 110 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10Return_c
            return (__extension__ ({
                
                //#line 423 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10LocalDecl_c
                x10::lang::IntRange r59267 =
                  x10::lang::IntRange::_make(((x10aux::ref<x10::array::TriangularRegion>)this)->
                                               FMGL(colMin), ((x10_int) ((((x10aux::ref<x10::array::TriangularRegion>)this)->
                                                                            FMGL(colMin)) + (((x10aux::ref<x10::array::TriangularRegion>)this)->
                                                                                               FMGL(dim)))));
                x10aux::class_cast<x10aux::ref<x10::array::Region> >((__extension__ ({
                    
                    //#line 424 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10LocalDecl_c
                    x10aux::ref<x10::array::RectRegion1D> alloc2063159268 =
                      
                    x10aux::ref<x10::array::RectRegion1D>((new (memset(x10aux::alloc<x10::array::RectRegion1D>(), 0, sizeof(x10::array::RectRegion1D))) x10::array::RectRegion1D()))
                    ;
                    
                    //#line 424 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10ConstructorCall_c
                    (alloc2063159268)->::x10::array::RectRegion1D::_constructor(
                      r59267->
                        FMGL(min),
                      r59267->
                        FMGL(max));
                    alloc2063159268;
                }))
                );
            }))
            ;
            
        }
        //#line 111 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": polyglot.ast.Case_c
        case ((x10_int)1): ;
        {
            
            //#line 112 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10Return_c
            return (__extension__ ({
                
                //#line 423 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10LocalDecl_c
                x10::lang::IntRange r59269 =
                  x10::lang::IntRange::_make(((x10aux::ref<x10::array::TriangularRegion>)this)->
                                               FMGL(rowMin), ((x10_int) ((((x10aux::ref<x10::array::TriangularRegion>)this)->
                                                                            FMGL(rowMin)) + (((x10aux::ref<x10::array::TriangularRegion>)this)->
                                                                                               FMGL(dim)))));
                x10aux::class_cast<x10aux::ref<x10::array::Region> >((__extension__ ({
                    
                    //#line 424 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10LocalDecl_c
                    x10aux::ref<x10::array::RectRegion1D> alloc2063159270 =
                      
                    x10aux::ref<x10::array::RectRegion1D>((new (memset(x10aux::alloc<x10::array::RectRegion1D>(), 0, sizeof(x10::array::RectRegion1D))) x10::array::RectRegion1D()))
                    ;
                    
                    //#line 424 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10ConstructorCall_c
                    (alloc2063159270)->::x10::array::RectRegion1D::_constructor(
                      r59269->
                        FMGL(min),
                      r59269->
                        FMGL(max));
                    alloc2063159270;
                }))
                );
            }))
            ;
            
        }
        //#line 113 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": polyglot.ast.Case_c
        default: ;
        {
            
            //#line 114 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": polyglot.ast.Throw_c
            x10aux::throwException(x10aux::nullCheck(x10::lang::UnsupportedOperationException::_make(((((x10aux::string_utils::lit("projection(")) + (axis))) + (x10aux::string_utils::lit(")"))))));
        }
    }
}

//#line 118 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::Region> x10::array::TriangularRegion::boundingBox(
  ) {
    
    //#line 119 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10Return_c
    return x10aux::nullCheck(x10::lang::IntRange::_make(((x10aux::ref<x10::array::TriangularRegion>)this)->
                                                          FMGL(rowMin), ((x10_int) ((((x10aux::ref<x10::array::TriangularRegion>)this)->
                                                                                       FMGL(rowMin)) + (((x10aux::ref<x10::array::TriangularRegion>)this)->
                                                                                                          FMGL(dim))))))->x10::lang::IntRange::__times(
             x10::lang::IntRange::_make(((x10aux::ref<x10::array::TriangularRegion>)this)->
                                          FMGL(colMin), ((x10_int) ((((x10aux::ref<x10::array::TriangularRegion>)this)->
                                                                       FMGL(colMin)) + (((x10aux::ref<x10::array::TriangularRegion>)this)->
                                                                                          FMGL(dim))))));
    
}

//#line 122 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::Region> x10::array::TriangularRegion::computeBoundingBox(
  ) {
    
    //#line 123 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10Return_c
    return x10aux::nullCheck(x10::lang::IntRange::_make(((x10aux::ref<x10::array::TriangularRegion>)this)->
                                                          FMGL(rowMin), ((x10_int) ((((x10aux::ref<x10::array::TriangularRegion>)this)->
                                                                                       FMGL(rowMin)) + (((x10aux::ref<x10::array::TriangularRegion>)this)->
                                                                                                          FMGL(dim))))))->x10::lang::IntRange::__times(
             x10::lang::IntRange::_make(((x10aux::ref<x10::array::TriangularRegion>)this)->
                                          FMGL(colMin), ((x10_int) ((((x10aux::ref<x10::array::TriangularRegion>)this)->
                                                                       FMGL(colMin)) + (((x10aux::ref<x10::array::TriangularRegion>)this)->
                                                                                          FMGL(dim))))));
    
}

//#line 126 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::lang::Iterator<x10aux::ref<x10::array::Point> > >
  x10::array::TriangularRegion::iterator(
  ) {
    
    //#line 127 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10Return_c
    return x10aux::class_cast<x10aux::ref<x10::lang::Iterator<x10aux::ref<x10::array::Point> > > >((__extension__ ({
        
        //#line 127 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator> alloc58924 =
          
        x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator>((new (memset(x10aux::alloc<x10::array::TriangularRegion__TriangularRegionIterator>(), 0, sizeof(x10::array::TriangularRegion__TriangularRegionIterator))) x10::array::TriangularRegion__TriangularRegionIterator()))
        ;
        
        //#line 127 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10ConstructorCall_c
        (alloc58924)->::x10::array::TriangularRegion__TriangularRegionIterator::_constructor(
          ((x10aux::ref<x10::array::TriangularRegion>)this),
          ((x10aux::ref<x10::array::TriangularRegion>)this));
        alloc58924;
    }))
    );
    
}

//#line 167 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::lang::String> x10::array::TriangularRegion::toString(
  ) {
    
    //#line 168 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10LocalDecl_c
    x10aux::ref<x10::lang::String> triangleString =
      ((((((((((((((x10aux::string_utils::lit("triangular region ")) + (((x10aux::ref<x10::array::TriangularRegion>)this)->
                                                                          FMGL(colMin)))) + (x10aux::string_utils::lit("..")))) + (((x10_int) ((((x10aux::ref<x10::array::TriangularRegion>)this)->
                                                                                                                                                  FMGL(colMin)) + (((x10aux::ref<x10::array::TriangularRegion>)this)->
                                                                                                                                                                     FMGL(dim))))))) + (x10aux::string_utils::lit(",")))) + (((x10aux::ref<x10::array::TriangularRegion>)this)->
                                                                                                                                                                                                                               FMGL(rowMin)))) + (x10aux::string_utils::lit("..")))) + (((x10_int) ((((x10aux::ref<x10::array::TriangularRegion>)this)->
                                                                                                                                                                                                                                                                                                       FMGL(rowMin)) + (((x10aux::ref<x10::array::TriangularRegion>)this)->
                                                                                                                                                                                                                                                                                                                          FMGL(dim))))));
    
    //#line 169 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10If_c
    if (((x10aux::ref<x10::array::TriangularRegion>)this)->
          FMGL(lower)) {
        
        //#line 170 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10Return_c
        return ((x10aux::string_utils::lit("lower ")) + (triangleString));
        
    } else {
        
        //#line 172 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10Return_c
        return ((x10aux::string_utils::lit("upper ")) + (triangleString));
        
    }
    
}

//#line 3 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::TriangularRegion>
  x10::array::TriangularRegion::x10__array__TriangularRegion____x10__array__TriangularRegion__this(
  ) {
    
    //#line 3 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10Return_c
    return ((x10aux::ref<x10::array::TriangularRegion>)this);
    
}
const x10aux::serialization_id_t x10::array::TriangularRegion::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(x10::array::TriangularRegion::_deserializer, x10aux::CLOSURE_KIND_NOT_ASYNC);

void x10::array::TriangularRegion::_serialize_body(x10aux::serialization_buffer& buf) {
    x10::array::Region::_serialize_body(buf);
    buf.write(this->FMGL(dim));
    buf.write(this->FMGL(rowMin));
    buf.write(this->FMGL(colMin));
    buf.write(this->FMGL(lower));
    
}

x10aux::ref<x10::lang::Reference> x10::array::TriangularRegion::_deserializer(x10aux::deserialization_buffer& buf) {
    x10aux::ref<x10::array::TriangularRegion> this_ = new (memset(x10aux::alloc<x10::array::TriangularRegion>(), 0, sizeof(x10::array::TriangularRegion))) x10::array::TriangularRegion();
    buf.record_reference(this_);
    this_->_deserialize_body(buf);
    return this_;
}

void x10::array::TriangularRegion::_deserialize_body(x10aux::deserialization_buffer& buf) {
    x10::array::Region::_deserialize_body(buf);
    FMGL(dim) = buf.read<x10_int>();
    FMGL(rowMin) = buf.read<x10_int>();
    FMGL(colMin) = buf.read<x10_int>();
    FMGL(lower) = buf.read<x10_boolean>();
}

x10aux::RuntimeType x10::array::TriangularRegion::rtt;
void x10::array::TriangularRegion::_initRTT() {
    if (rtt.initStageOne(&rtt)) return;
    const x10aux::RuntimeType* parents[1] = { x10aux::getRTT<x10::array::Region>()};
    rtt.initStageTwo("x10.array.TriangularRegion",x10aux::RuntimeType::class_kind, 1, parents, 0, NULL, NULL);
}
x10::lang::Fun_0_1<x10_int, x10_int>::itable<x10_array_TriangularRegion__closure__1>x10_array_TriangularRegion__closure__1::_itable(&x10::lang::Reference::equals, &x10::lang::Closure::hashCode, &x10_array_TriangularRegion__closure__1::__apply, &x10_array_TriangularRegion__closure__1::toString, &x10::lang::Closure::typeName);
x10aux::itable_entry x10_array_TriangularRegion__closure__1::_itables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Fun_0_1<x10_int, x10_int> >, &x10_array_TriangularRegion__closure__1::_itable),x10aux::itable_entry(NULL, NULL)};

const x10aux::serialization_id_t x10_array_TriangularRegion__closure__1::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(x10_array_TriangularRegion__closure__1::_deserialize<x10::lang::Reference>,x10aux::CLOSURE_KIND_NOT_ASYNC);

x10::lang::Fun_0_1<x10_int, x10_int>::itable<x10_array_TriangularRegion__closure__2>x10_array_TriangularRegion__closure__2::_itable(&x10::lang::Reference::equals, &x10::lang::Closure::hashCode, &x10_array_TriangularRegion__closure__2::__apply, &x10_array_TriangularRegion__closure__2::toString, &x10::lang::Closure::typeName);
x10aux::itable_entry x10_array_TriangularRegion__closure__2::_itables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Fun_0_1<x10_int, x10_int> >, &x10_array_TriangularRegion__closure__2::_itable),x10aux::itable_entry(NULL, NULL)};

const x10aux::serialization_id_t x10_array_TriangularRegion__closure__2::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(x10_array_TriangularRegion__closure__2::_deserialize<x10::lang::Reference>,x10aux::CLOSURE_KIND_NOT_ASYNC);

/* END of TriangularRegion */
/*************************************************/
/*************************************************/
/* START of TriangularRegion$TriangularRegionIterator */
#include <x10/array/TriangularRegion__TriangularRegionIterator.h>

#include <x10/lang/Object.h>
#include <x10/lang/Iterator.h>
#include <x10/array/Point.h>
#include <x10/array/TriangularRegion.h>
#include <x10/lang/Int.h>
#include <x10/lang/Boolean.h>
#include <x10/array/Array.h>
x10::lang::Iterator<x10aux::ref<x10::array::Point> >::itable<x10::array::TriangularRegion__TriangularRegionIterator >  x10::array::TriangularRegion__TriangularRegionIterator::_itable_0(&x10::array::TriangularRegion__TriangularRegionIterator::equals, &x10::array::TriangularRegion__TriangularRegionIterator::hasNext, &x10::array::TriangularRegion__TriangularRegionIterator::hashCode, &x10::array::TriangularRegion__TriangularRegionIterator::next, &x10::array::TriangularRegion__TriangularRegionIterator::toString, &x10::array::TriangularRegion__TriangularRegionIterator::typeName);
x10::lang::Any::itable<x10::array::TriangularRegion__TriangularRegionIterator >  x10::array::TriangularRegion__TriangularRegionIterator::_itable_1(&x10::array::TriangularRegion__TriangularRegionIterator::equals, &x10::array::TriangularRegion__TriangularRegionIterator::hashCode, &x10::array::TriangularRegion__TriangularRegionIterator::toString, &x10::array::TriangularRegion__TriangularRegionIterator::typeName);
x10aux::itable_entry x10::array::TriangularRegion__TriangularRegionIterator::_itables[3] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Iterator<x10aux::ref<x10::array::Point> > >, &_itable_0), x10aux::itable_entry(&x10aux::getRTT<x10::lang::Any>, &_itable_1), x10aux::itable_entry(NULL, (void*)x10aux::getRTT<x10::array::TriangularRegion__TriangularRegionIterator>())};

//#line 3 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10FieldDecl_c

//#line 131 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10FieldDecl_c

//#line 132 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10FieldDecl_c

//#line 133 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10FieldDecl_c

//#line 134 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10FieldDecl_c

//#line 136 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10ConstructorDecl_c
void x10::array::TriangularRegion__TriangularRegionIterator::_constructor(
  x10aux::ref<x10::array::TriangularRegion> out__,
  x10aux::ref<x10::array::TriangularRegion> r) {
    
    //#line 3 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator>)this)->
      FMGL(out__) = out__;
    
    //#line 136 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<x10::lang::Object>)this))->::x10::lang::Object::_constructor();
    
    //#line 136 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.AssignPropertyCall_c
    
    //#line 130 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 130 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator> this5927159283 =
          ((x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator>)this);
        {
            
            //#line 130 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this5927159283)->FMGL(i) = ((x10_int)0);
            
            //#line 130 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this5927159283)->FMGL(j) = ((x10_int)0);
        }
        
    }))
    ;
    
    //#line 137 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator>)this)->
      FMGL(dim) = x10aux::nullCheck(r)->FMGL(dim);
    
    //#line 138 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator>)this)->
      FMGL(lower) = x10aux::nullCheck(r)->
                      FMGL(lower);
    
    //#line 139 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator>)this)->
      FMGL(i) = x10aux::nullCheck(r)->FMGL(rowMin);
    
    //#line 140 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator>)this)->
      FMGL(j) = x10aux::nullCheck(r)->FMGL(lower)
      ? (x10aux::nullCheck(r)->
           FMGL(colMin))
      : (((x10_int) ((x10aux::nullCheck(r)->
                        FMGL(colMin)) + (x10aux::nullCheck(r)->
                                           FMGL(dim)))));
    
}
x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator> x10::array::TriangularRegion__TriangularRegionIterator::_make(
  x10aux::ref<x10::array::TriangularRegion> out__,
  x10aux::ref<x10::array::TriangularRegion> r)
{
    x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator> this_ = new (memset(x10aux::alloc<x10::array::TriangularRegion__TriangularRegionIterator>(), 0, sizeof(x10::array::TriangularRegion__TriangularRegionIterator))) x10::array::TriangularRegion__TriangularRegionIterator();
    this_->_constructor(out__,
    r);
    return this_;
}



//#line 143 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10MethodDecl_c
x10_boolean x10::array::TriangularRegion__TriangularRegionIterator::hasNext(
  ) {
    
    //#line 144 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10If_c
    if (((((x10_int) ((((x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator>)this)->
                         FMGL(i)) - (x10aux::nullCheck(((x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator>)this)->
                                                         FMGL(out__))->
                                       FMGL(rowMin))))) <= (((x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator>)this)->
                                                              FMGL(dim))) &&
        ((((x10_int) ((((x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator>)this)->
                         FMGL(j)) - (x10aux::nullCheck(((x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator>)this)->
                                                         FMGL(out__))->
                                       FMGL(colMin))))) <= (((x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator>)this)->
                                                              FMGL(dim))))
    {
        
        //#line 144 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10Return_c
        return true;
        
    }
    else
    {
        
        //#line 145 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10Return_c
        return false;
        
    }
    
}

//#line 148 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::Point> x10::array::TriangularRegion__TriangularRegionIterator::next(
  ) {
    
    //#line 149 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10LocalDecl_c
    x10aux::ref<x10::array::Point> nextPoint =
      (__extension__ ({
        
        //#line 132 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::Array<x10_int> > a59274 =
          (x10aux::ref<x10::array::Array<x10_int> >)(__extension__ ({
            x10aux::ref<x10::array::Array<x10_int> > __var635__(x10::array::Array<x10_int>::_make(2));
            __var635__->__set(0, 
            (((x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator>)this)->
               FMGL(i)));
            __var635__->__set(1, 
            (((x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator>)this)->
               FMGL(j)));
            __var635__;
        }));
        x10::array::Point::make<x10_int >(
          a59274);
    }))
    ;
    
    //#line 150 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10If_c
    if (((x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator>)this)->
          FMGL(lower)) {
        
        //#line 151 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10If_c
        if (((((x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator>)this)->
                FMGL(j)) < (((x10_int) ((x10aux::nullCheck(((x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator>)this)->
                                                             FMGL(out__))->
                                           FMGL(colMin)) + (((x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator>)this)->
                                                              FMGL(dim)))))))
        {
            
            //#line 151 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.StmtExpr_c
            (__extension__ ({
                
                //#line 151 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator> x59275 =
                  ((x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator>)this);
                x10aux::nullCheck(x59275)->
                  FMGL(j) =
                  ((x10_int) ((x10aux::nullCheck(x59275)->
                                 FMGL(j)) + (((x10_int)1))));
            }))
            ;
        }
        else
        {
            
            //#line 153 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.StmtExpr_c
            (__extension__ ({
                
                //#line 153 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator> x59277 =
                  ((x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator>)this);
                x10aux::nullCheck(x59277)->
                  FMGL(i) =
                  ((x10_int) ((x10aux::nullCheck(x59277)->
                                 FMGL(i)) + (((x10_int)1))));
            }))
            ;
            
            //#line 154 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10FieldAssign_c
            ((x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator>)this)->
              FMGL(j) =
              ((x10_int) ((x10aux::nullCheck(((x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator>)this)->
                                               FMGL(out__))->
                             FMGL(colMin)) + (((x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator>)this)->
                                                FMGL(i))));
        }
        
    } else 
    //#line 157 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10If_c
    if (((((x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator>)this)->
            FMGL(j)) < (((x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator>)this)->
                          FMGL(i)))) {
        
        //#line 157 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.StmtExpr_c
        (__extension__ ({
            
            //#line 157 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator> x59279 =
              ((x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator>)this);
            x10aux::nullCheck(x59279)->FMGL(j) =
              ((x10_int) ((x10aux::nullCheck(x59279)->
                             FMGL(j)) + (((x10_int)1))));
        }))
        ;
    } else {
        
        //#line 159 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.StmtExpr_c
        (__extension__ ({
            
            //#line 159 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator> x59281 =
              ((x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator>)this);
            x10aux::nullCheck(x59281)->FMGL(i) =
              ((x10_int) ((x10aux::nullCheck(x59281)->
                             FMGL(i)) + (((x10_int)1))));
        }))
        ;
        
        //#line 160 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10FieldAssign_c
        ((x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator>)this)->
          FMGL(j) = x10aux::nullCheck(((x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator>)this)->
                                        FMGL(out__))->
                      FMGL(colMin);
    }
    
    //#line 163 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10Return_c
    return nextPoint;
    
}

//#line 130 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator>
  x10::array::TriangularRegion__TriangularRegionIterator::x10__array__TriangularRegion__TriangularRegionIterator____x10__array__TriangularRegion__TriangularRegionIterator__this(
  ) {
    
    //#line 130 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10Return_c
    return ((x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator>)this);
    
}

//#line 130 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::TriangularRegion>
  x10::array::TriangularRegion__TriangularRegionIterator::x10__array__TriangularRegion__TriangularRegionIterator____x10__array__TriangularRegion__this(
  ) {
    
    //#line 130 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10Return_c
    return ((x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator>)this)->
             FMGL(out__);
    
}

//#line 130 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10MethodDecl_c
void x10::array::TriangularRegion__TriangularRegionIterator::__fieldInitializers58171(
  ) {
    
    //#line 130 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator>)this)->
      FMGL(i) = ((x10_int)0);
    
    //#line 130 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator>)this)->
      FMGL(j) = ((x10_int)0);
}
const x10aux::serialization_id_t x10::array::TriangularRegion__TriangularRegionIterator::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(x10::array::TriangularRegion__TriangularRegionIterator::_deserializer, x10aux::CLOSURE_KIND_NOT_ASYNC);

void x10::array::TriangularRegion__TriangularRegionIterator::_serialize_body(x10aux::serialization_buffer& buf) {
    x10::lang::Object::_serialize_body(buf);
    buf.write(this->FMGL(dim));
    buf.write(this->FMGL(lower));
    buf.write(this->FMGL(i));
    buf.write(this->FMGL(j));
    buf.write(this->FMGL(out__));
    
}

x10aux::ref<x10::lang::Reference> x10::array::TriangularRegion__TriangularRegionIterator::_deserializer(x10aux::deserialization_buffer& buf) {
    x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator> this_ = new (memset(x10aux::alloc<x10::array::TriangularRegion__TriangularRegionIterator>(), 0, sizeof(x10::array::TriangularRegion__TriangularRegionIterator))) x10::array::TriangularRegion__TriangularRegionIterator();
    buf.record_reference(this_);
    this_->_deserialize_body(buf);
    return this_;
}

void x10::array::TriangularRegion__TriangularRegionIterator::_deserialize_body(x10aux::deserialization_buffer& buf) {
    x10::lang::Object::_deserialize_body(buf);
    FMGL(dim) = buf.read<x10_int>();
    FMGL(lower) = buf.read<x10_boolean>();
    FMGL(i) = buf.read<x10_int>();
    FMGL(j) = buf.read<x10_int>();
    FMGL(out__) = buf.read<x10aux::ref<x10::array::TriangularRegion> >();
}

x10aux::RuntimeType x10::array::TriangularRegion__TriangularRegionIterator::rtt;
void x10::array::TriangularRegion__TriangularRegionIterator::_initRTT() {
    if (rtt.initStageOne(&rtt)) return;
    const x10aux::RuntimeType* parents[2] = { x10aux::getRTT<x10::lang::Object>(), x10aux::getRTT<x10::lang::Iterator<x10aux::ref<x10::array::Point> > >()};
    rtt.initStageTwo("x10.array.TriangularRegion.TriangularRegionIterator",x10aux::RuntimeType::class_kind, 2, parents, 0, NULL, NULL);
}
/* END of TriangularRegion$TriangularRegionIterator */
/*************************************************/
