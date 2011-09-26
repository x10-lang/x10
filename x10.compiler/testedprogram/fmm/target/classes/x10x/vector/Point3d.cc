/*************************************************/
/* START of Point3d */
#include <x10x/vector/Point3d.h>

#include <x10/lang/Any.h>
#include <x10x/vector/Tuple3d.h>
#include <x10/util/concurrent/Atomic.h>
#include <x10/lang/Double.h>
#include <x10/lang/Int.h>
#include <x10/util/concurrent/OrderedLock.h>
#include <x10/util/Map.h>
#include <x10/lang/String.h>
#include <x10x/vector/Vector3d.h>
#include <x10/compiler/Inline.h>
#include <x10/lang/Math.h>
#include <x10/compiler/Native.h>
#include <x10/compiler/NonEscaping.h>
#include <x10/lang/Boolean.h>

//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.PropertyDecl_c

//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.PropertyDecl_c

//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.PropertyDecl_c
namespace x10x { namespace vector { 
class Point3d_ibox0 : public x10::lang::IBox<x10x::vector::Point3d> {
public:
    static x10::lang::Any::itable<Point3d_ibox0 > itable;
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
x10::lang::Any::itable<Point3d_ibox0 >  Point3d_ibox0::itable(&Point3d_ibox0::equals, &Point3d_ibox0::hashCode, &Point3d_ibox0::toString, &Point3d_ibox0::typeName);
} } 
x10::lang::Any::itable<x10x::vector::Point3d >  x10x::vector::Point3d::_itable_0(&x10x::vector::Point3d::equals, &x10x::vector::Point3d::hashCode, &x10x::vector::Point3d::toString, &x10x::vector::Point3d::typeName);
namespace x10x { namespace vector { 
class Point3d_ibox1 : public x10::lang::IBox<x10x::vector::Point3d> {
public:
    static x10x::vector::Tuple3d::itable<Point3d_ibox1 > itable;
    x10_boolean equals(x10aux::ref<x10::lang::Any> arg0) {
        return this->value->equals(arg0);
    }
    x10_int hashCode() {
        return this->value->hashCode();
    }
    x10_double i() {
        return this->value->i();
    }
    x10_double j() {
        return this->value->j();
    }
    x10_double k() {
        return this->value->k();
    }
    x10aux::ref<x10::lang::String> toString() {
        return this->value->toString();
    }
    x10aux::ref<x10::lang::String> typeName() {
        return this->value->typeName();
    }
    
};
x10x::vector::Tuple3d::itable<Point3d_ibox1 >  Point3d_ibox1::itable(&Point3d_ibox1::equals, &Point3d_ibox1::hashCode, &Point3d_ibox1::i, &Point3d_ibox1::j, &Point3d_ibox1::k, &Point3d_ibox1::toString, &Point3d_ibox1::typeName);
} } 
x10x::vector::Tuple3d::itable<x10x::vector::Point3d >  x10x::vector::Point3d::_itable_1(&x10x::vector::Point3d::equals, &x10x::vector::Point3d::hashCode, &x10x::vector::Point3d::i, &x10x::vector::Point3d::j, &x10x::vector::Point3d::k, &x10x::vector::Point3d::toString, &x10x::vector::Point3d::typeName);
x10aux::itable_entry x10x::vector::Point3d::_itables[3] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Any>, &x10x::vector::Point3d::_itable_0), x10aux::itable_entry(&x10aux::getRTT<x10x::vector::Tuple3d>, &x10x::vector::Point3d::_itable_1), x10aux::itable_entry(NULL, (void*)x10aux::getRTT<x10x::vector::Point3d>())};
x10aux::itable_entry x10x::vector::Point3d::_iboxitables[3] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Any>, &x10x::vector::Point3d_ibox0::itable), x10aux::itable_entry(&x10aux::getRTT<x10x::vector::Tuple3d>, &x10x::vector::Point3d_ibox1::itable), x10aux::itable_entry(NULL, (void*)x10aux::getRTT<x10x::vector::Point3d>())};

//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10FieldDecl_c

//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock> x10x::vector::Point3d::getOrderedLock(
  ) {
    
    //#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10Return_c
    return x10::util::concurrent::OrderedLock::getObjectLock((*this)->
                                                               FMGL(X10__object_lock_id0));
    
}

//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10FieldDecl_c
x10_int x10x::vector::Point3d::FMGL(X10__class_lock_id1);
void x10x::vector::Point3d::FMGL(X10__class_lock_id1__do_init)() {
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: x10x::vector::Point3d.X10$class_lock_id1");
    x10_int __var11__ = x10::util::concurrent::OrderedLock::createClassLock();
    FMGL(X10__class_lock_id1) = __var11__;
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
}
void x10x::vector::Point3d::FMGL(X10__class_lock_id1__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var12__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(X10__class_lock_id1__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var12__ != x10aux::UNINITIALIZED) goto WAIT;
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
                                                                       _SI_("WAITING for field: x10x::vector::Point3d.X10$class_lock_id1 to be initialized");
                                                                       while (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                                       _SI_("CONTINUING because field: x10x::vector::Point3d.X10$class_lock_id1 has been initialized");
                                                                       x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__13 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(x10x::vector::Point3d::FMGL(X10__class_lock_id1__init));

volatile x10aux::status x10x::vector::Point3d::FMGL(X10__class_lock_id1__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> x10x::vector::Point3d::FMGL(X10__class_lock_id1__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(X10__class_lock_id1) = buf.read<x10_int>();
    x10x::vector::Point3d::FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t x10x::vector::Point3d::FMGL(X10__class_lock_id1__id) =
  x10aux::StaticInitBroadcastDispatcher::addRoutine(x10x::vector::Point3d::FMGL(X10__class_lock_id1__deserialize));


//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock> x10x::vector::Point3d::getStaticOrderedLock(
  ) {
    
    //#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 170 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/util/concurrent/OrderedLock.x10": x10.ast.X10LocalDecl_c
        x10_int lockId24840 = x10x::vector::Point3d::
                                FMGL(X10__class_lock_id1__get)();
        x10::util::Map<x10_int, x10aux::ref<x10::util::concurrent::OrderedLock> >::getOrThrow(x10aux::nullCheck(x10::util::concurrent::OrderedLock::
                                                                                                                  FMGL(lockMap__get)()), 
          lockId24840);
    }))
    ;
    
}

//#line 10 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10ConstructorDecl_c


//#line 10 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10ConstructorDecl_c
void x10x::vector::Point3d::_constructor(
  x10_double i,
  x10_double j,
  x10_double k,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    
    //#line 11 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.AssignPropertyCall_c
    FMGL(i) = i;
    FMGL(j) = j;
    FMGL(k) = k;
    
    //#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
        x10x::vector::Point3d this2484425360 =
          (*this);
        {
            
            //#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10FieldAssign_c
            this2484425360->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
        }
        
    }))
    ;
    
    //#line 10 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10FieldAssign_c
    (*this)->
      FMGL(X10__object_lock_id0) =
      x10aux::nullCheck(paramLock)->getIndex();
    
}
x10x::vector::Point3d x10x::vector::Point3d::_make(
  x10_double i,
  x10_double j,
  x10_double k,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    x10x::vector::Point3d this_; 
    this_->_constructor(i,
    j,
    k,
    paramLock);
    return this_;
}



//#line 14 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10ConstructorDecl_c
void x10x::vector::Point3d::_constructor(
  x10aux::ref<x10x::vector::Tuple3d> t) {
    
    //#line 15 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10ConstructorCall_c
    ((*this))->::x10x::vector::Point3d::_constructor(
      x10x::vector::Tuple3d::i(x10aux::nullCheck(t)),
      x10x::vector::Tuple3d::j(x10aux::nullCheck(t)),
      x10x::vector::Tuple3d::k(x10aux::nullCheck(t)));
    
}
x10x::vector::Point3d x10x::vector::Point3d::_make(
  x10aux::ref<x10x::vector::Tuple3d> t) {
    x10x::vector::Point3d this_; 
    this_->_constructor(t);
    return this_;
}



//#line 14 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10ConstructorDecl_c
void x10x::vector::Point3d::_constructor(
  x10aux::ref<x10x::vector::Tuple3d> t,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    
    //#line 15 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10ConstructorCall_c
    ((*this))->::x10x::vector::Point3d::_constructor(
      x10x::vector::Tuple3d::i(x10aux::nullCheck(t)),
      x10x::vector::Tuple3d::j(x10aux::nullCheck(t)),
      x10x::vector::Tuple3d::k(x10aux::nullCheck(t)));
    
    //#line 14 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10FieldAssign_c
    (*this)->
      FMGL(X10__object_lock_id0) =
      x10aux::nullCheck(paramLock)->getIndex();
    
}
x10x::vector::Point3d x10x::vector::Point3d::_make(
  x10aux::ref<x10x::vector::Tuple3d> t,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    x10x::vector::Point3d this_; 
    this_->_constructor(t,
    paramLock);
    return this_;
}



//#line 18 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10MethodDecl_c

//#line 19 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10MethodDecl_c

//#line 20 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10MethodDecl_c

//#line 22 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::lang::String> x10x::vector::Point3d::toString(
  ) {
    
    //#line 22 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10Return_c
    return ((((((((((((x10aux::string_utils::lit("(")) + ((*this)->
                                                            FMGL(i)))) + (x10aux::string_utils::lit("i + ")))) + ((*this)->
                                                                                                                    FMGL(j)))) + (x10aux::string_utils::lit("j + ")))) + ((*this)->
                                                                                                                                                                            FMGL(k)))) + (x10aux::string_utils::lit("k)")));
    
}

//#line 27 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10MethodDecl_c
x10x::vector::Point3d x10x::vector::Point3d::add(
  x10x::vector::Vector3d b) {
    
    //#line 28 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 28 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
        x10x::vector::Point3d alloc24237 =
          
        x10x::vector::Point3d::_alloc();
        
        //#line 28 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10ConstructorCall_c
        (alloc24237)->::x10x::vector::Point3d::_constructor(
          (((*this)->
              FMGL(i)) + ((__extension__ ({
              b->
                FMGL(i);
          }))
          )),
          (((*this)->
              FMGL(j)) + ((__extension__ ({
              b->
                FMGL(j);
          }))
          )),
          (((*this)->
              FMGL(k)) + ((__extension__ ({
              b->
                FMGL(k);
          }))
          )),
          x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
        alloc24237;
    }))
    ;
    
}

//#line 31 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10MethodDecl_c
x10x::vector::Point3d x10x::vector::Point3d::__plus(
  x10x::vector::Vector3d that) {
    
    //#line 32 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 32 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
        x10x::vector::Point3d this25345 =
          (*this);
        
        //#line 27 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
        x10x::vector::Vector3d b25343 = that;
        (__extension__ ({
            
            //#line 28 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
            x10x::vector::Point3d alloc2423725344 =
              
            x10x::vector::Point3d::_alloc();
            
            //#line 28 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10ConstructorCall_c
            (alloc2423725344)->::x10x::vector::Point3d::_constructor(
              ((this25345->
                  FMGL(i)) + ((__extension__ ({
                  b25343->
                    FMGL(i);
              }))
              )),
              ((this25345->
                  FMGL(j)) + ((__extension__ ({
                  b25343->
                    FMGL(j);
              }))
              )),
              ((this25345->
                  FMGL(k)) + ((__extension__ ({
                  b25343->
                    FMGL(k);
              }))
              )),
              x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
            alloc2423725344;
        }))
        ;
    }))
    ;
    
}

//#line 38 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10MethodDecl_c
x10x::vector::Point3d x10x::vector::Point3d::__times(
  x10_double that) {
    
    //#line 39 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 39 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
        x10x::vector::Point3d this25348 =
          (*this);
        
        //#line 42 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
        x10_double c25346 = that;
        (__extension__ ({
            
            //#line 43 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
            x10x::vector::Point3d alloc2423825347 =
              
            x10x::vector::Point3d::_alloc();
            
            //#line 43 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10ConstructorCall_c
            (alloc2423825347)->::x10x::vector::Point3d::_constructor(
              ((this25348->
                  FMGL(i)) * (c25346)),
              ((this25348->
                  FMGL(j)) * (c25346)),
              ((this25348->
                  FMGL(k)) * (c25346)),
              x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
            alloc2423825347;
        }))
        ;
    }))
    ;
    
}

//#line 42 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10MethodDecl_c
x10x::vector::Point3d x10x::vector::Point3d::scale(
  x10_double c) {
    
    //#line 43 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 43 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
        x10x::vector::Point3d alloc24238 =
          
        x10x::vector::Point3d::_alloc();
        
        //#line 43 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10ConstructorCall_c
        (alloc24238)->::x10x::vector::Point3d::_constructor(
          (((*this)->
              FMGL(i)) * (c)),
          (((*this)->
              FMGL(j)) * (c)),
          (((*this)->
              FMGL(k)) * (c)),
          x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
        alloc24238;
    }))
    ;
    
}

//#line 49 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10MethodDecl_c
x10x::vector::Vector3d x10x::vector::Point3d::vector(
  x10x::vector::Point3d b) {
    
    //#line 50 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 50 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
        x10x::vector::Vector3d alloc24239 =
          
        x10x::vector::Vector3d::_alloc();
        
        //#line 50 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10ConstructorCall_c
        (alloc24239)->::x10x::vector::Vector3d::_constructor(
          (((*this)->
              FMGL(i)) - (b->
                            FMGL(i))),
          (((*this)->
              FMGL(j)) - (b->
                            FMGL(j))),
          (((*this)->
              FMGL(k)) - (b->
                            FMGL(k))),
          x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
        alloc24239;
    }))
    ;
    
}

//#line 53 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10MethodDecl_c
x10x::vector::Vector3d x10x::vector::Point3d::__minus(
  x10x::vector::Point3d that) {
    
    //#line 54 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 54 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
        x10x::vector::Point3d this25351 =
          (*this);
        
        //#line 49 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
        x10x::vector::Point3d b25349 = that;
        (__extension__ ({
            
            //#line 50 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
            x10x::vector::Vector3d alloc2423925350 =
              
            x10x::vector::Vector3d::_alloc();
            
            //#line 50 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10ConstructorCall_c
            (alloc2423925350)->::x10x::vector::Vector3d::_constructor(
              ((this25351->
                  FMGL(i)) - (b25349->
                                FMGL(i))),
              ((this25351->
                  FMGL(j)) - (b25349->
                                FMGL(j))),
              ((this25351->
                  FMGL(k)) - (b25349->
                                FMGL(k))),
              x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
            alloc2423925350;
        }))
        ;
    }))
    ;
    
}

//#line 57 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10MethodDecl_c

//#line 66 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10MethodDecl_c
x10_double x10x::vector::Point3d::distance(
  x10x::vector::Point3d b) {
    
    //#line 67 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10Return_c
    return x10aux::math_utils::sqrt((__extension__ ({
        
        //#line 67 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
        x10x::vector::Point3d this25356 =
          (*this);
        
        //#line 57 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
        x10x::vector::Point3d b25352 = b;
        
        //#line 57 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
        x10_double ret25357;
        {
            
            //#line 58 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
            x10_double di25353 = ((this25356->
                                     FMGL(i)) - (b25352->
                                                   FMGL(i)));
            
            //#line 59 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
            x10_double dj25354 = ((this25356->
                                     FMGL(j)) - (b25352->
                                                   FMGL(j)));
            
            //#line 60 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
            x10_double dk25355 = ((this25356->
                                     FMGL(k)) - (b25352->
                                                   FMGL(k)));
            
            //#line 61 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalAssign_c
            ret25357 = ((((((di25353) * (di25353))) + (((dj25354) * (dj25354))))) + (((dk25355) * (dk25355))));
        }
        ret25357;
    }))
    );
    
}

//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::lang::String> x10x::vector::Point3d::typeName(
  ){
    return x10aux::type_name((*this));
}

//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10MethodDecl_c
x10_int x10x::vector::Point3d::hashCode() {
    
    //#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
    x10_int result = ((x10_int)1);
    
    //#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalAssign_c
    result = ((x10_int) ((((x10_int) ((((x10_int)8191)) * (result)))) + (x10aux::hash_code((*this)->
                                                                                             FMGL(i)))));
    
    //#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalAssign_c
    result = ((x10_int) ((((x10_int) ((((x10_int)8191)) * (result)))) + (x10aux::hash_code((*this)->
                                                                                             FMGL(j)))));
    
    //#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalAssign_c
    result = ((x10_int) ((((x10_int) ((((x10_int)8191)) * (result)))) + (x10aux::hash_code((*this)->
                                                                                             FMGL(k)))));
    
    //#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10Return_c
    return result;
    
}

//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10MethodDecl_c
x10_boolean x10x::vector::Point3d::equals(
  x10aux::ref<x10::lang::Any> other) {
    
    //#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10If_c
    if (!(x10aux::instanceof<x10x::vector::Point3d>(other)))
    {
        
        //#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10Return_c
        return false;
        
    }
    
    //#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10Return_c
    return (*this)->x10x::vector::Point3d::equals(
             x10aux::class_cast<x10x::vector::Point3d>(other));
    
}

//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10MethodDecl_c

//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10MethodDecl_c
x10_boolean x10x::vector::Point3d::_struct_equals(
  x10aux::ref<x10::lang::Any> other) {
    
    //#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10If_c
    if (!(x10aux::instanceof<x10x::vector::Point3d>(other)))
    {
        
        //#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10Return_c
        return false;
        
    }
    
    //#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10Return_c
    return (*this)->x10x::vector::Point3d::_struct_equals(
             x10aux::class_cast<x10x::vector::Point3d>(other));
    
}

//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10MethodDecl_c

//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10MethodDecl_c

//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10MethodDecl_c
void x10x::vector::Point3d::_serialize(x10x::vector::Point3d this_, x10aux::serialization_buffer& buf) {
    buf.write(this_->FMGL(i));
    buf.write(this_->FMGL(j));
    buf.write(this_->FMGL(k));
    
}

void x10x::vector::Point3d::_deserialize_body(x10aux::deserialization_buffer& buf) {
    FMGL(i) = buf.read<x10_double>();
    FMGL(j) = buf.read<x10_double>();
    FMGL(k) = buf.read<x10_double>();
}


x10aux::RuntimeType x10x::vector::Point3d::rtt;
void x10x::vector::Point3d::_initRTT() {
    if (rtt.initStageOne(&rtt)) return;
    const x10aux::RuntimeType* parents[3] = { x10aux::getRTT<x10::lang::Any>(), x10aux::getRTT<x10x::vector::Tuple3d>(), x10aux::getRTT<x10::lang::Any>()};
    rtt.initStageTwo("x10x.vector.Point3d",x10aux::RuntimeType::struct_kind, 3, parents, 0, NULL, NULL);
    rtt.containsPtrs = false;
}
/* END of Point3d */
/*************************************************/
