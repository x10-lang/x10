/*************************************************/
/* START of Polar3d */
#include <x10x/polar/Polar3d.h>

#include <x10/lang/Any.h>
#include <x10/util/concurrent/Atomic.h>
#include <x10/lang/Int.h>
#include <x10/util/concurrent/OrderedLock.h>
#include <x10/util/Map.h>
#include <x10/lang/Double.h>
#include <x10x/vector/Point3d.h>
#include <x10/lang/Math.h>
#include <x10x/vector/Tuple3d.h>
#include <x10/lang/String.h>
#include <x10/compiler/Native.h>
#include <x10/compiler/NonEscaping.h>
#include <x10/lang/Boolean.h>
namespace x10x { namespace polar { 
class Polar3d_ibox0 : public x10::lang::IBox<x10x::polar::Polar3d> {
public:
    static x10::lang::Any::itable<Polar3d_ibox0 > itable;
    x10_boolean equals(x10aux::ref<x10::lang::Any> arg0) {
        return this->value->equals(arg0);
    }
    x10_int hashCode() {
        return this->value->hashCode();
    }
    x10aux::ref<x10::lang::String> toString() {
        return this->value->toString();
    }
    x10aux::ref<x10::lang::String> typeName() {
        return this->value->typeName();
    }
    
};
x10::lang::Any::itable<Polar3d_ibox0 >  Polar3d_ibox0::itable(&Polar3d_ibox0::equals, &Polar3d_ibox0::hashCode, &Polar3d_ibox0::toString, &Polar3d_ibox0::typeName);
} } 
x10::lang::Any::itable<x10x::polar::Polar3d >  x10x::polar::Polar3d::_itable_0(&x10x::polar::Polar3d::equals, &x10x::polar::Polar3d::hashCode, &x10x::polar::Polar3d::toString, &x10x::polar::Polar3d::typeName);
x10aux::itable_entry x10x::polar::Polar3d::_itables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Any>, &x10x::polar::Polar3d::_itable_0), x10aux::itable_entry(NULL, (void*)x10aux::getRTT<x10x::polar::Polar3d>())};
x10aux::itable_entry x10x::polar::Polar3d::_iboxitables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Any>, &x10x::polar::Polar3d_ibox0::itable), x10aux::itable_entry(NULL, (void*)x10aux::getRTT<x10x::polar::Polar3d>())};

//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10FieldDecl_c

//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock> x10x::polar::Polar3d::getOrderedLock(
  ) {
    
    //#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10Return_c
    return x10::util::concurrent::OrderedLock::getObjectLock((*this)->
                                                               FMGL(X10__object_lock_id0));
    
}

//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10FieldDecl_c
x10_int x10x::polar::Polar3d::FMGL(X10__class_lock_id1);
void x10x::polar::Polar3d::FMGL(X10__class_lock_id1__do_init)() {
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: x10x::polar::Polar3d.X10$class_lock_id1");
    x10_int __var474__ = x10::util::concurrent::OrderedLock::createClassLock();
    FMGL(X10__class_lock_id1) = __var474__;
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
}
void x10x::polar::Polar3d::FMGL(X10__class_lock_id1__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var475__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(X10__class_lock_id1__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var475__ != x10aux::UNINITIALIZED) goto WAIT;
        FMGL(X10__class_lock_id1__do_init)();
        x10aux::StaticInitBroadcastDispatcher::broadcastStaticField(FMGL(X10__class_lock_id1),
                                                                    FMGL(X10__class_lock_id1__id));
        // Notify all waiting threads
        x10aux::StaticInitBroadcastDispatcher::lock();
        x10aux::StaticInitBroadcastDispatcher::notify();
    }
    WAIT:
    if (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) {
                                                                       x10aux::StaticInitBroadcastDispatcher::lock();
                                                                       _SI_("WAITING for field: x10x::polar::Polar3d.X10$class_lock_id1 to be initialized");
                                                                       while (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                                       _SI_("CONTINUING because field: x10x::polar::Polar3d.X10$class_lock_id1 has been initialized");
                                                                       x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__476 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(x10x::polar::Polar3d::FMGL(X10__class_lock_id1__init));

volatile x10aux::status x10x::polar::Polar3d::FMGL(X10__class_lock_id1__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> x10x::polar::Polar3d::FMGL(X10__class_lock_id1__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(X10__class_lock_id1) = buf.read<x10_int>();
    x10x::polar::Polar3d::FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t x10x::polar::Polar3d::FMGL(X10__class_lock_id1__id) =
  x10aux::StaticInitBroadcastDispatcher::addRoutine(x10x::polar::Polar3d::FMGL(X10__class_lock_id1__deserialize));


//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock> x10x::polar::Polar3d::getStaticOrderedLock(
  ) {
    
    //#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 170 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/util/concurrent/OrderedLock.x10": x10.ast.X10LocalDecl_c
        x10_int lockId57110 = x10x::polar::Polar3d::
                                FMGL(X10__class_lock_id1__get)();
        x10::util::Map<x10_int, x10aux::ref<x10::util::concurrent::OrderedLock> >::getOrThrow(x10aux::nullCheck(x10::util::concurrent::OrderedLock::
                                                                                                                  FMGL(lockMap__get)()), 
          lockId57110);
    }))
    ;
    
}

//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10FieldDecl_c

//#line 22 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10FieldDecl_c

//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10FieldDecl_c

//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10ConstructorDecl_c


//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10ConstructorDecl_c
void x10x::polar::Polar3d::_constructor(x10_double r,
                                        x10_double theta,
                                        x10_double phi,
                                        x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    
    //#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.AssignPropertyCall_c
    
    //#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10LocalDecl_c
        x10x::polar::Polar3d this5711457118 =
          (*this);
        {
            
            //#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10FieldAssign_c
            this5711457118->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
        }
        
    }))
    ;
    
    //#line 26 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10FieldAssign_c
    (*this)->
      FMGL(r) =
      r;
    
    //#line 27 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10FieldAssign_c
    (*this)->
      FMGL(theta) =
      theta;
    
    //#line 28 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10FieldAssign_c
    (*this)->
      FMGL(phi) =
      phi;
    
    //#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10FieldAssign_c
    (*this)->
      FMGL(X10__object_lock_id0) =
      x10aux::nullCheck(paramLock)->getIndex();
    
}
x10x::polar::Polar3d x10x::polar::Polar3d::_make(
  x10_double r,
  x10_double theta,
  x10_double phi,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    x10x::polar::Polar3d this_; 
    this_->_constructor(r,
    theta,
    phi,
    paramLock);
    return this_;
}



//#line 32 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10MethodDecl_c
x10x::vector::Point3d x10x::polar::Polar3d::toPoint3d(
  ) {
    
    //#line 33 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10LocalDecl_c
    x10_double sineTheta = x10aux::math_utils::sin((*this)->
                                                     FMGL(theta));
    
    //#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10LocalDecl_c
        x10x::vector::Point3d alloc43262 =
          
        x10x::vector::Point3d::_alloc();
        
        //#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10ConstructorCall_c
        (alloc43262)->::x10x::vector::Point3d::_constructor(
          (((((*this)->
                FMGL(r)) * (sineTheta))) * (x10aux::math_utils::cos((*this)->
                                                                      FMGL(phi)))),
          (((((*this)->
                FMGL(r)) * (sineTheta))) * (x10aux::math_utils::sin((*this)->
                                                                      FMGL(phi)))),
          (((*this)->
              FMGL(r)) * (x10aux::math_utils::cos((*this)->
                                                    FMGL(theta)))),
          x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
        alloc43262;
    }))
    ;
    
}

//#line 41 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10MethodDecl_c
x10x::polar::Polar3d x10x::polar::Polar3d::getPolar3d(
  x10aux::ref<x10x::vector::Tuple3d> point) {
    
    //#line 42 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10LocalDecl_c
    x10_double rxy2 = ((((x10x::vector::Tuple3d::i(x10aux::nullCheck(point))) * (x10x::vector::Tuple3d::i(x10aux::nullCheck(point))))) + (((x10x::vector::Tuple3d::j(x10aux::nullCheck(point))) * (x10x::vector::Tuple3d::j(x10aux::nullCheck(point))))));
    
    //#line 43 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10LocalDecl_c
    x10_double r2 = ((rxy2) + (((x10x::vector::Tuple3d::k(x10aux::nullCheck(point))) * (x10x::vector::Tuple3d::k(x10aux::nullCheck(point))))));
    
    //#line 44 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10LocalDecl_c
    x10_double r = x10aux::math_utils::sqrt(r2);
    
    //#line 45 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10LocalDecl_c
    x10_double phi;
    
    //#line 46 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10LocalDecl_c
    x10_double theta;
    
    //#line 47 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10If_c
    if ((x10aux::struct_equals(rxy2, 0.0)))
    {
        
        //#line 48 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10If_c
        if (((x10x::vector::Tuple3d::k(x10aux::nullCheck(point))) >= (0.0)))
        {
            
            //#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10LocalAssign_c
            theta =
              0.0;
        }
        else
        {
            
            //#line 51 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10LocalAssign_c
            theta =
              3.141592653589793;
        }
        
        //#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10LocalAssign_c
        phi =
          0.0;
    }
    else
    {
        
        //#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10LocalDecl_c
        x10_double rxy =
          x10aux::math_utils::sqrt(rxy2);
        
        //#line 56 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10LocalAssign_c
        theta =
          x10aux::math_utils::acos(((x10x::vector::Tuple3d::k(x10aux::nullCheck(point))) / (r)));
        
        //#line 57 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10LocalAssign_c
        phi =
          x10aux::math_utils::acos(((x10x::vector::Tuple3d::i(x10aux::nullCheck(point))) / (rxy)));
        
        //#line 58 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10If_c
        if (((x10x::vector::Tuple3d::j(x10aux::nullCheck(point))) < (0.0)))
        {
            
            //#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10LocalAssign_c
            phi =
              ((6.283185307179586) - (phi));
        }
        
    }
    
    //#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10LocalDecl_c
        x10x::polar::Polar3d alloc43263 =
          
        x10x::polar::Polar3d::_alloc();
        
        //#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10ConstructorCall_c
        (alloc43263)->::x10x::polar::Polar3d::_constructor(
          r,
          theta,
          phi,
          x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
        alloc43263;
    }))
    ;
    
}

//#line 72 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10MethodDecl_c
x10x::polar::Polar3d x10x::polar::Polar3d::rotate(
  x10_double alpha,
  x10_double beta) {
    
    //#line 73 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10LocalDecl_c
    x10_double newPhi = (((*this)->FMGL(phi)) + (alpha));
    
    //#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10LocalDecl_c
    x10_double tempTheta = ((((3.141592653589793) / (2.0))) < ((*this)->
                                                                 FMGL(phi))) &&
    (((*this)->
        FMGL(phi)) < (((9.42477796076938) / (2.0))))
      ? ((*this)->
           FMGL(theta))
      : ((-((*this)->
              FMGL(theta))));
    
    //#line 76 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10LocalDecl_c
    x10_double newTheta = ((tempTheta) + (beta));
    
    //#line 77 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10If_c
    if (((newTheta) > (6.283185307179586)))
    {
        
        //#line 78 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10LocalAssign_c
        newTheta =
          ((newTheta) - (6.283185307179586));
    }
    
    //#line 82 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10If_c
    if (((newTheta) < (0.0))) {
        
        //#line 83 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10LocalAssign_c
        newTheta = (-(newTheta));
    } else 
    //#line 85 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10If_c
    if (((newTheta) > (3.141592653589793)))
    {
        
        //#line 86 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10LocalAssign_c
        newTheta =
          ((6.283185307179586) - (newTheta));
    }
    
    //#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10If_c
    if (((newPhi) >= (6.283185307179586)))
    {
        
        //#line 91 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10LocalAssign_c
        newPhi =
          ((newPhi) - (6.283185307179586));
    }
    else
    
    //#line 92 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10If_c
    if (((newPhi) < (0.0)))
    {
        
        //#line 93 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10LocalAssign_c
        newPhi =
          ((newPhi) + (6.283185307179586));
    }
    
    //#line 95 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 95 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10LocalDecl_c
        x10x::polar::Polar3d alloc43264 =
          
        x10x::polar::Polar3d::_alloc();
        
        //#line 95 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10ConstructorCall_c
        (alloc43264)->::x10x::polar::Polar3d::_constructor(
          (*this)->
            FMGL(r),
          newTheta,
          newPhi,
          x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
        alloc43264;
    }))
    ;
    
}

//#line 98 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::lang::String> x10x::polar::Polar3d::toString(
  ) {
    
    //#line 99 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10Return_c
    return ((((((((((((x10aux::string_utils::lit("(r:")) + ((*this)->
                                                              FMGL(r)))) + (x10aux::string_utils::lit(",theta:")))) + ((*this)->
                                                                                                                         FMGL(theta)))) + (x10aux::string_utils::lit(",phi:")))) + ((*this)->
                                                                                                                                                                                      FMGL(phi)))) + (x10aux::string_utils::lit(")")));
    
}

//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::lang::String> x10x::polar::Polar3d::typeName(
  ){
    return x10aux::type_name((*this));
}

//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10MethodDecl_c
x10_int x10x::polar::Polar3d::hashCode() {
    
    //#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10LocalDecl_c
    x10_int result = ((x10_int)1);
    
    //#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10LocalAssign_c
    result = ((x10_int) ((((x10_int) ((((x10_int)8191)) * (result)))) + (x10aux::hash_code((*this)->
                                                                                             FMGL(r)))));
    
    //#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10LocalAssign_c
    result = ((x10_int) ((((x10_int) ((((x10_int)8191)) * (result)))) + (x10aux::hash_code((*this)->
                                                                                             FMGL(theta)))));
    
    //#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10LocalAssign_c
    result = ((x10_int) ((((x10_int) ((((x10_int)8191)) * (result)))) + (x10aux::hash_code((*this)->
                                                                                             FMGL(phi)))));
    
    //#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10Return_c
    return result;
    
}

//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10MethodDecl_c
x10_boolean x10x::polar::Polar3d::equals(
  x10aux::ref<x10::lang::Any> other) {
    
    //#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10If_c
    if (!(x10aux::instanceof<x10x::polar::Polar3d>(other)))
    {
        
        //#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10Return_c
        return false;
        
    }
    
    //#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10Return_c
    return (*this)->x10x::polar::Polar3d::equals(
             x10aux::class_cast<x10x::polar::Polar3d>(other));
    
}

//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10MethodDecl_c

//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10MethodDecl_c
x10_boolean x10x::polar::Polar3d::_struct_equals(
  x10aux::ref<x10::lang::Any> other) {
    
    //#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10If_c
    if (!(x10aux::instanceof<x10x::polar::Polar3d>(other)))
    {
        
        //#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10Return_c
        return false;
        
    }
    
    //#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10Return_c
    return (*this)->x10x::polar::Polar3d::_struct_equals(
             x10aux::class_cast<x10x::polar::Polar3d>(other));
    
}

//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10MethodDecl_c

//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10MethodDecl_c

//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10MethodDecl_c
void x10x::polar::Polar3d::_serialize(x10x::polar::Polar3d this_, x10aux::serialization_buffer& buf) {
    buf.write(this_->FMGL(r));
    buf.write(this_->FMGL(theta));
    buf.write(this_->FMGL(phi));
    
}

void x10x::polar::Polar3d::_deserialize_body(x10aux::deserialization_buffer& buf) {
    FMGL(r) = buf.read<x10_double>();
    FMGL(theta) = buf.read<x10_double>();
    FMGL(phi) = buf.read<x10_double>();
}


x10aux::RuntimeType x10x::polar::Polar3d::rtt;
void x10x::polar::Polar3d::_initRTT() {
    if (rtt.initStageOne(&rtt)) return;
    const x10aux::RuntimeType* parents[2] = { x10aux::getRTT<x10::lang::Any>(), x10aux::getRTT<x10::lang::Any>()};
    rtt.initStageTwo("x10x.polar.Polar3d",x10aux::RuntimeType::struct_kind, 2, parents, 0, NULL, NULL);
    rtt.containsPtrs = false;
}
/* END of Polar3d */
/*************************************************/
