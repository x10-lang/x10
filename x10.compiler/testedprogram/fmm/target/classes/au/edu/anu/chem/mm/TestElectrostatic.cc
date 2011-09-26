/*************************************************/
/* START of TestElectrostatic */
#include <au/edu/anu/chem/mm/TestElectrostatic.h>

#ifndef AU_EDU_ANU_CHEM_MM_TESTELECTROSTATIC__CLOSURE__1_CLOSURE
#define AU_EDU_ANU_CHEM_MM_TESTELECTROSTATIC__CLOSURE__1_CLOSURE
#include <x10/lang/Closure.h>
#include <x10/lang/Fun_0_1.h>
class au_edu_anu_chem_mm_TestElectrostatic__closure__1 : public x10::lang::Closure {
    public:
    
    static x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::util::ArrayList<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > >::itable<au_edu_anu_chem_mm_TestElectrostatic__closure__1> _itable;
    static x10aux::itable_entry _itables[2];
    
    virtual x10aux::itable_entry* _getITables() { return _itables; }
    
    // closure body
    x10aux::ref<x10::util::ArrayList<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > __apply(x10aux::ref<x10::array::Point> id__18) {
        
        //#line 59 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10Return_c
        return (__extension__ ({
            
            //#line 59 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::util::ArrayList<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > alloc23953 =
              
            x10aux::ref<x10::util::ArrayList<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > >((new (memset(x10aux::alloc<x10::util::ArrayList<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > >(), 0, sizeof(x10::util::ArrayList<x10aux::ref<au::edu::anu::chem::mm::MMAtom> >))) x10::util::ArrayList<x10aux::ref<au::edu::anu::chem::mm::MMAtom> >()))
            ;
            
            //#line 59 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10ConstructorCall_c
            (alloc23953)->::x10::util::ArrayList<x10aux::ref<au::edu::anu::chem::mm::MMAtom> >::_constructor();
            alloc23953;
        }))
        ;
        
    }
    
    // captured environment
    
    x10aux::serialization_id_t _get_serialization_id() {
        return _serialization_id;
    }
    
    void _serialize_body(x10aux::serialization_buffer &buf) {
        
    }
    
    template<class __T> static x10aux::ref<__T> _deserialize(x10aux::deserialization_buffer &buf) {
        au_edu_anu_chem_mm_TestElectrostatic__closure__1* storage = x10aux::alloc<au_edu_anu_chem_mm_TestElectrostatic__closure__1>();
        buf.record_reference(x10aux::ref<au_edu_anu_chem_mm_TestElectrostatic__closure__1>(storage));
        x10aux::ref<au_edu_anu_chem_mm_TestElectrostatic__closure__1> this_ = new (storage) au_edu_anu_chem_mm_TestElectrostatic__closure__1();
        return this_;
    }
    
    au_edu_anu_chem_mm_TestElectrostatic__closure__1() { }
    
    static const x10aux::serialization_id_t _serialization_id;
    
    static const x10aux::RuntimeType* getRTT() { return x10aux::getRTT<x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::util::ArrayList<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > >(); }
    virtual const x10aux::RuntimeType *_type() const { return x10aux::getRTT<x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::util::ArrayList<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > >(); }
    
    x10aux::ref<x10::lang::String> toString() {
        return x10aux::string_utils::lit(this->toNativeString());
    }
    
    const char* toNativeString() {
        return "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10:59";
    }

};

#endif // AU_EDU_ANU_CHEM_MM_TESTELECTROSTATIC__CLOSURE__1_CLOSURE
#ifndef AU_EDU_ANU_CHEM_MM_TESTELECTROSTATIC__CLOSURE__2_CLOSURE
#define AU_EDU_ANU_CHEM_MM_TESTELECTROSTATIC__CLOSURE__2_CLOSURE
#include <x10/lang/Closure.h>
#include <x10/lang/VoidFun_0_0.h>
class au_edu_anu_chem_mm_TestElectrostatic__closure__2 : public x10::lang::Closure {
    public:
    
    static x10::lang::VoidFun_0_0::itable<au_edu_anu_chem_mm_TestElectrostatic__closure__2> _itable;
    static x10aux::itable_entry _itables[2];
    
    virtual x10aux::itable_entry* _getITables() { return _itables; }
    
    // closure body
    void __apply() {
        
        //#line 74 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::chem::mm::MMAtom> atom =  x10aux::ref<au::edu::anu::chem::mm::MMAtom>((new (memset(x10aux::alloc<au::edu::anu::chem::mm::MMAtom>(), 0, sizeof(au::edu::anu::chem::mm::MMAtom))) au::edu::anu::chem::mm::MMAtom()))
        ;
        
        //#line 74 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10ConstructorCall_c
        (atom)->::au::edu::anu::chem::mm::MMAtom::_constructor((__extension__ ({
                                                                   
                                                                   //#line 74 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10LocalDecl_c
                                                                   x10x::vector::Point3d alloc23954 =
                                                                     
                                                                   x10x::vector::Point3d::_alloc();
                                                                   
                                                                   //#line 74 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10ConstructorCall_c
                                                                   (alloc23954)->::x10x::vector::Point3d::_constructor(
                                                                     x,
                                                                     y,
                                                                     z,
                                                                     x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
                                                                   alloc23954;
                                                               }))
                                                               ,
                                                               ((x10_double) (charge)),
                                                               x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
        {
            
            //#line 76 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::lang::Throwable> throwable34379 =
              X10_NULL;
            
            //#line 76 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": polyglot.ast.Try_c
            try {
                {
                    
                    //#line 76 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10Call_c
                    x10::lang::Runtime::enterAtomic();
                    {
                        
                        //#line 76 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10Call_c
                        x10aux::nullCheck(tempAtoms->x10::array::DistArray<x10aux::ref<x10::util::ArrayList<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > >::__apply(
                                            p))->add(
                          atom);
                    }
                }
                
                //#line 76 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10Call_c
                x10::compiler::Finalization::plausibleThrow();
            }
            catch (x10aux::__ref& __ref__99) {
                x10aux::ref<x10::lang::Throwable>& __exc__ref__99 = (x10aux::ref<x10::lang::Throwable>&)__ref__99;
                if (true) {
                    x10aux::ref<x10::lang::Throwable> formal34380 =
                      static_cast<x10aux::ref<x10::lang::Throwable> >(__exc__ref__99);
                    {
                        
                        //#line 76 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10LocalAssign_c
                        throwable34379 =
                          formal34380;
                    }
                } else
                throw;
            }
            
            //#line 76 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10If_c
            if ((!x10aux::struct_equals(X10_NULL,
                                        throwable34379)))
            {
                
                //#line 76 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10If_c
                if (x10aux::instanceof<x10aux::ref<x10::compiler::Abort> >(throwable34379))
                {
                    
                    //#line 76 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": polyglot.ast.Throw_c
                    x10aux::throwException(x10aux::nullCheck(throwable34379));
                }
                
            }
            
            //#line 76 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10If_c
            if (true) {
                
                //#line 76 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10Call_c
                x10::lang::Runtime::exitAtomic();
            }
            
            //#line 76 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10If_c
            if ((!x10aux::struct_equals(X10_NULL,
                                        throwable34379)))
            {
                
                //#line 76 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10If_c
                if (!(x10aux::instanceof<x10aux::ref<x10::compiler::Finalization> >(throwable34379)))
                {
                    
                    //#line 76 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": polyglot.ast.Throw_c
                    x10aux::throwException(x10aux::nullCheck(throwable34379));
                }
                
            }
            
        }
    }
    
    // captured environment
    x10_double x;
    x10_double y;
    x10_double z;
    x10_int charge;
    x10aux::ref<x10::array::DistArray<x10aux::ref<x10::util::ArrayList<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > tempAtoms;
    x10_int p;
    
    x10aux::serialization_id_t _get_serialization_id() {
        return _serialization_id;
    }
    
    void _serialize_body(x10aux::serialization_buffer &buf) {
        buf.write(this->x);
        buf.write(this->y);
        buf.write(this->z);
        buf.write(this->charge);
        buf.write(this->tempAtoms);
        buf.write(this->p);
    }
    
    template<class __T> static x10aux::ref<__T> _deserialize(x10aux::deserialization_buffer &buf) {
        au_edu_anu_chem_mm_TestElectrostatic__closure__2* storage = x10aux::alloc<au_edu_anu_chem_mm_TestElectrostatic__closure__2>();
        buf.record_reference(x10aux::ref<au_edu_anu_chem_mm_TestElectrostatic__closure__2>(storage));
        x10_double that_x = buf.read<x10_double>();
        x10_double that_y = buf.read<x10_double>();
        x10_double that_z = buf.read<x10_double>();
        x10_int that_charge = buf.read<x10_int>();
        x10aux::ref<x10::array::DistArray<x10aux::ref<x10::util::ArrayList<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > that_tempAtoms = buf.read<x10aux::ref<x10::array::DistArray<x10aux::ref<x10::util::ArrayList<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > >();
        x10_int that_p = buf.read<x10_int>();
        x10aux::ref<au_edu_anu_chem_mm_TestElectrostatic__closure__2> this_ = new (storage) au_edu_anu_chem_mm_TestElectrostatic__closure__2(that_x, that_y, that_z, that_charge, that_tempAtoms, that_p);
        return this_;
    }
    
    au_edu_anu_chem_mm_TestElectrostatic__closure__2(x10_double x, x10_double y, x10_double z, x10_int charge, x10aux::ref<x10::array::DistArray<x10aux::ref<x10::util::ArrayList<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > tempAtoms, x10_int p) : x(x), y(y), z(z), charge(charge), tempAtoms(tempAtoms), p(p) { }
    
    static const x10aux::serialization_id_t _serialization_id;
    
    static const x10aux::RuntimeType* getRTT() { return x10aux::getRTT<x10::lang::VoidFun_0_0>(); }
    virtual const x10aux::RuntimeType *_type() const { return x10aux::getRTT<x10::lang::VoidFun_0_0>(); }
    
    x10aux::ref<x10::lang::String> toString() {
        return x10aux::string_utils::lit(this->toNativeString());
    }
    
    const char* toNativeString() {
        return "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10:73-77";
    }

};

#endif // AU_EDU_ANU_CHEM_MM_TESTELECTROSTATIC__CLOSURE__2_CLOSURE
#ifndef AU_EDU_ANU_CHEM_MM_TESTELECTROSTATIC__CLOSURE__3_CLOSURE
#define AU_EDU_ANU_CHEM_MM_TESTELECTROSTATIC__CLOSURE__3_CLOSURE
#include <x10/lang/Closure.h>
#include <x10/lang/Fun_0_1.h>
class au_edu_anu_chem_mm_TestElectrostatic__closure__3 : public x10::lang::Closure {
    public:
    
    static x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > >::itable<au_edu_anu_chem_mm_TestElectrostatic__closure__3> _itable;
    static x10aux::itable_entry _itables[2];
    
    virtual x10aux::itable_entry* _getITables() { return _itables; }
    
    // closure body
    x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > __apply(x10aux::ref<x10::array::Point> id19) {
        
        //#line 80 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10LocalDecl_c
        x10_int p = x10aux::nullCheck(id19)->x10::array::Point::__apply(((x10_int)0));
        
        //#line 80 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10Return_c
        return x10aux::nullCheck((tempAtoms->x10::array::DistArray<x10aux::ref<x10::util::ArrayList<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > >::__apply(
                                    p)))->toArray();
        
    }
    
    // captured environment
    x10aux::ref<x10::array::DistArray<x10aux::ref<x10::util::ArrayList<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > tempAtoms;
    
    x10aux::serialization_id_t _get_serialization_id() {
        return _serialization_id;
    }
    
    void _serialize_body(x10aux::serialization_buffer &buf) {
        buf.write(this->tempAtoms);
    }
    
    template<class __T> static x10aux::ref<__T> _deserialize(x10aux::deserialization_buffer &buf) {
        au_edu_anu_chem_mm_TestElectrostatic__closure__3* storage = x10aux::alloc<au_edu_anu_chem_mm_TestElectrostatic__closure__3>();
        buf.record_reference(x10aux::ref<au_edu_anu_chem_mm_TestElectrostatic__closure__3>(storage));
        x10aux::ref<x10::array::DistArray<x10aux::ref<x10::util::ArrayList<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > that_tempAtoms = buf.read<x10aux::ref<x10::array::DistArray<x10aux::ref<x10::util::ArrayList<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > >();
        x10aux::ref<au_edu_anu_chem_mm_TestElectrostatic__closure__3> this_ = new (storage) au_edu_anu_chem_mm_TestElectrostatic__closure__3(that_tempAtoms);
        return this_;
    }
    
    au_edu_anu_chem_mm_TestElectrostatic__closure__3(x10aux::ref<x10::array::DistArray<x10aux::ref<x10::util::ArrayList<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > tempAtoms) : tempAtoms(tempAtoms) { }
    
    static const x10aux::serialization_id_t _serialization_id;
    
    static const x10aux::RuntimeType* getRTT() { return x10aux::getRTT<x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > >(); }
    virtual const x10aux::RuntimeType *_type() const { return x10aux::getRTT<x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > >(); }
    
    x10aux::ref<x10::lang::String> toString() {
        return x10aux::string_utils::lit(this->toNativeString());
    }
    
    const char* toNativeString() {
        return "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10:80";
    }

};

#endif // AU_EDU_ANU_CHEM_MM_TESTELECTROSTATIC__CLOSURE__3_CLOSURE

//#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10FieldDecl_c

//#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock> au::edu::anu::chem::mm::TestElectrostatic::getOrderedLock(
  ) {
    
    //#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10Return_c
    return x10::util::concurrent::OrderedLock::getObjectLock(((x10aux::ref<au::edu::anu::chem::mm::TestElectrostatic>)this)->
                                                               FMGL(X10__object_lock_id0));
    
}

//#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10FieldDecl_c
x10_int au::edu::anu::chem::mm::TestElectrostatic::FMGL(X10__class_lock_id1);
void au::edu::anu::chem::mm::TestElectrostatic::FMGL(X10__class_lock_id1__do_init)() {
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: au::edu::anu::chem::mm::TestElectrostatic.X10$class_lock_id1");
    x10_int __var88__ = x10::util::concurrent::OrderedLock::createClassLock();
    FMGL(X10__class_lock_id1) = __var88__;
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
}
void au::edu::anu::chem::mm::TestElectrostatic::FMGL(X10__class_lock_id1__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var89__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(X10__class_lock_id1__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var89__ != x10aux::UNINITIALIZED) goto WAIT;
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
                                                                       _SI_("WAITING for field: au::edu::anu::chem::mm::TestElectrostatic.X10$class_lock_id1 to be initialized");
                                                                       while (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                                       _SI_("CONTINUING because field: au::edu::anu::chem::mm::TestElectrostatic.X10$class_lock_id1 has been initialized");
                                                                       x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__90 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(au::edu::anu::chem::mm::TestElectrostatic::FMGL(X10__class_lock_id1__init));

volatile x10aux::status au::edu::anu::chem::mm::TestElectrostatic::FMGL(X10__class_lock_id1__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> au::edu::anu::chem::mm::TestElectrostatic::FMGL(X10__class_lock_id1__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(X10__class_lock_id1) = buf.read<x10_int>();
    au::edu::anu::chem::mm::TestElectrostatic::FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t au::edu::anu::chem::mm::TestElectrostatic::FMGL(X10__class_lock_id1__id) =
  x10aux::StaticInitBroadcastDispatcher::addRoutine(au::edu::anu::chem::mm::TestElectrostatic::FMGL(X10__class_lock_id1__deserialize));


//#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock>
  au::edu::anu::chem::mm::TestElectrostatic::getStaticOrderedLock(
  ) {
    
    //#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 170 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/util/concurrent/OrderedLock.x10": x10.ast.X10LocalDecl_c
        x10_int lockId32091 = au::edu::anu::chem::mm::TestElectrostatic::
                                FMGL(X10__class_lock_id1__get)();
        x10::util::Map<x10_int, x10aux::ref<x10::util::concurrent::OrderedLock> >::getOrThrow(x10aux::nullCheck(x10::util::concurrent::OrderedLock::
                                                                                                                  FMGL(lockMap__get)()), 
          lockId32091);
    }))
    ;
    
}

//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10FieldDecl_c
x10_long au::edu::anu::chem::mm::TestElectrostatic::FMGL(RANDOM_SEED) =
  ((x10_long)10101110ll);


//#line 25 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10FieldDecl_c
x10aux::ref<x10::util::Random> au::edu::anu::chem::mm::TestElectrostatic::FMGL(R);
void au::edu::anu::chem::mm::TestElectrostatic::FMGL(R__do_init)() {
    FMGL(R__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: au::edu::anu::chem::mm::TestElectrostatic.R");
    x10aux::ref<x10::util::Random> __var92__ =
      x10::util::Random::_make(((x10_long)10101110ll));
    FMGL(R) = __var92__;
    FMGL(R__status) = x10aux::INITIALIZED;
}
void au::edu::anu::chem::mm::TestElectrostatic::FMGL(R__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var93__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(R__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var93__ != x10aux::UNINITIALIZED) goto WAIT;
        FMGL(R__do_init)();
        x10aux::StaticInitBroadcastDispatcher::broadcastStaticField(FMGL(R),
                                                                    FMGL(R__id));
        // Notify all waiting threads
        x10aux::StaticInitBroadcastDispatcher::lock();
        x10aux::StaticInitBroadcastDispatcher::notify();
    }
    WAIT:
    if (FMGL(R__status) != x10aux::INITIALIZED) {
                                                     x10aux::StaticInitBroadcastDispatcher::lock();
                                                     _SI_("WAITING for field: au::edu::anu::chem::mm::TestElectrostatic.R to be initialized");
                                                     while (FMGL(R__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                     _SI_("CONTINUING because field: au::edu::anu::chem::mm::TestElectrostatic.R has been initialized");
                                                     x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__94 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(au::edu::anu::chem::mm::TestElectrostatic::FMGL(R__init));

volatile x10aux::status au::edu::anu::chem::mm::TestElectrostatic::FMGL(R__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> au::edu::anu::chem::mm::TestElectrostatic::FMGL(R__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(R) = buf.read<x10aux::ref<x10::util::Random> >();
    au::edu::anu::chem::mm::TestElectrostatic::FMGL(R__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t au::edu::anu::chem::mm::TestElectrostatic::FMGL(R__id) =
  x10aux::StaticInitBroadcastDispatcher::addRoutine(au::edu::anu::chem::mm::TestElectrostatic::FMGL(R__deserialize));


//#line 27 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10FieldDecl_c
x10_double au::edu::anu::chem::mm::TestElectrostatic::FMGL(NOISE) =
  0.25;


//#line 30 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10FieldDecl_c

//#line 36 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10MethodDecl_c
x10_double au::edu::anu::chem::mm::TestElectrostatic::sizeOfCentralCluster(
  ) {
    
    //#line 36 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10Return_c
    return 80.0;
    
}

//#line 38 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10MethodDecl_c
void au::edu::anu::chem::mm::TestElectrostatic::logTime(
  x10aux::ref<x10::lang::String> desc,
  x10_int timerIndex,
  x10aux::ref<au::edu::anu::util::Timer> timer,
  x10_boolean printNewline) {
    
    //#line 39 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10If_c
    if (printNewline) {
        
        //#line 40 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10Call_c
        x10aux::nullCheck(x10::io::Console::
                            FMGL(OUT))->printf(
          ((desc) + (x10aux::string_utils::lit(": %g seconds\n"))),
          x10aux::class_cast_unchecked<x10aux::ref<x10::lang::Any> >(((((x10_double) ((__extension__ ({
              
              //#line 40 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10LocalDecl_c
              x10aux::ref<x10::array::Array<x10_long> > this32093 =
                x10aux::nullCheck(timer)->
                  FMGL(total);
              
              //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
              x10_int i032092 =
                timerIndex;
              
              //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
              x10_long ret32094;
              
              //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
              goto __ret32095; __ret32095: {
              {
                  
                  //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                  ret32094 =
                    (x10aux::nullCheck(this32093)->
                       FMGL(raw))->__apply(i032092);
                  
                  //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                  goto __ret32095_end_;
              }goto __ret32095_end_; __ret32095_end_: ;
              }
              ret32094;
              }))
              ))) / (1.0E9))));
        } else {
            
            //#line 42 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10Call_c
            x10aux::nullCheck(x10::io::Console::
                                FMGL(OUT))->printf(
              ((desc) + (x10aux::string_utils::lit(": %g seconds"))),
              x10aux::class_cast_unchecked<x10aux::ref<x10::lang::Any> >(((((x10_double) ((__extension__ ({
                  
                  //#line 42 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10LocalDecl_c
                  x10aux::ref<x10::array::Array<x10_long> > this32102 =
                    x10aux::nullCheck(timer)->
                      FMGL(total);
                  
                  //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                  x10_int i032101 =
                    timerIndex;
                  
                  //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                  x10_long ret32103;
                  
                  //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                  goto __ret32104; __ret32104: {
                  {
                      
                      //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                      ret32103 =
                        (x10aux::nullCheck(this32102)->
                           FMGL(raw))->__apply(i032101);
                      
                      //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                      goto __ret32104_end_;
                  }goto __ret32104_end_; __ret32104_end_: ;
                  }
                  ret32103;
                  }))
                  ))) / (1.0E9))));
            }
            
    }
    
    //#line 46 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10MethodDecl_c
    void au::edu::anu::chem::mm::TestElectrostatic::logTime(
      x10aux::ref<x10::lang::String> desc,
      x10_int timerIndex,
      x10aux::ref<au::edu::anu::util::Timer> timer) {
        
        //#line 47 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10Call_c
        ((x10aux::ref<au::edu::anu::chem::mm::TestElectrostatic>)this)->logTime(
          desc,
          timerIndex,
          timer,
          true);
    }
    
    //#line 56 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10MethodDecl_c
    x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > >
      au::edu::anu::chem::mm::TestElectrostatic::generateAtoms(
      x10_int numAtoms) {
        
        //#line 59 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::DistArray<x10aux::ref<x10::util::ArrayList<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > tempAtoms =
          (__extension__ ({
            
            //#line 140 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::array::Dist> dist33149 =
              (__extension__ ({
                x10aux::class_cast<x10aux::ref<x10::array::Dist> >(x10::array::Dist::
                                                                     FMGL(UNIQUE__get)());
            }))
            ;
            
            //#line 140 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::util::ArrayList<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > init33150 =
              x10aux::class_cast_unchecked<x10aux::ref<x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::util::ArrayList<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > >(x10aux::ref<x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::util::ArrayList<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > >(x10aux::ref<au_edu_anu_chem_mm_TestElectrostatic__closure__1>(new (x10aux::alloc<x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::util::ArrayList<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > >(sizeof(au_edu_anu_chem_mm_TestElectrostatic__closure__1)))au_edu_anu_chem_mm_TestElectrostatic__closure__1())));
            (__extension__ ({
                
                //#line 140 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<x10::array::DistArray<x10aux::ref<x10::util::ArrayList<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > alloc3048633151 =
                  
                x10aux::ref<x10::array::DistArray<x10aux::ref<x10::util::ArrayList<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > >((new (memset(x10aux::alloc<x10::array::DistArray<x10aux::ref<x10::util::ArrayList<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > >(), 0, sizeof(x10::array::DistArray<x10aux::ref<x10::util::ArrayList<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > >))) x10::array::DistArray<x10aux::ref<x10::util::ArrayList<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > >()))
                ;
                
                //#line 140 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10": x10.ast.X10ConstructorCall_c
                (alloc3048633151)->::x10::array::DistArray<x10aux::ref<x10::util::ArrayList<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > >::_constructor(
                  dist33149,
                  init33150);
                alloc3048633151;
            }))
            ;
        }))
        ;
        
        //#line 60 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10LocalDecl_c
        x10_int gridSize = x10aux::double_utils::toInt(x10aux::math_utils::ceil(x10aux::math_utils::cbrt(((x10_double) (numAtoms)))));
        
        //#line 62 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10LocalDecl_c
        x10_double clusterStart = ((((80.0) / (2.0))) - (((((x10aux::ref<au::edu::anu::chem::mm::TestElectrostatic>)this)->sizeOfCentralCluster()) / (2.0))));
        
        //#line 63 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10LocalDecl_c
        x10_int gridPoint = ((x10_int)0);
        {
            
            //#line 64 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10Call_c
            x10::lang::Runtime::ensureNotInAtomic();
            
            //#line 64 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::lang::FinishState> x10____var1 =
              x10::lang::Runtime::startFinish();
            {
                
                //#line 64 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<x10::lang::Throwable> throwable34382 =
                  X10_NULL;
                
                //#line 64 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": polyglot.ast.Try_c
                try {
                    
                    //#line 64 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": polyglot.ast.Try_c
                    try {
                        {
                            
                            //#line 64 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": polyglot.ast.For_c
                            {
                                x10_int i;
                                for (
                                     //#line 64 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10LocalDecl_c
                                     i = ((x10_int)0);
                                     ((i) < (numAtoms));
                                     
                                     //#line 64 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10LocalAssign_c
                                     i =
                                       ((x10_int) ((i) + (((x10_int)1)))))
                                {
                                    
                                    //#line 65 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10LocalDecl_c
                                    x10_int gridX =
                                      ((x10_int) ((gridPoint) / x10aux::zeroCheck(((x10_int) ((gridSize) * (gridSize))))));
                                    
                                    //#line 66 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10LocalDecl_c
                                    x10_int gridY =
                                      ((x10_int) ((((x10_int) ((gridPoint) - (((x10_int) ((((x10_int) ((gridX) * (gridSize)))) * (gridSize))))))) / x10aux::zeroCheck(gridSize)));
                                    
                                    //#line 67 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10LocalDecl_c
                                    x10_int gridZ =
                                      ((x10_int) ((((x10_int) ((gridPoint) - (((x10_int) ((((x10_int) ((gridX) * (gridSize)))) * (gridSize))))))) - (((x10_int) ((gridY) * (gridSize))))));
                                    
                                    //#line 68 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10LocalDecl_c
                                    x10_double x =
                                      ((clusterStart) + (((((((((x10_double) (gridX))) + (0.5))) + (au::edu::anu::chem::mm::TestElectrostatic::randomNoise()))) * (((((x10aux::ref<au::edu::anu::chem::mm::TestElectrostatic>)this)->sizeOfCentralCluster()) / (((x10_double) (gridSize))))))));
                                    
                                    //#line 69 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10LocalDecl_c
                                    x10_double y =
                                      ((clusterStart) + (((((((((x10_double) (gridY))) + (0.5))) + (au::edu::anu::chem::mm::TestElectrostatic::randomNoise()))) * (((((x10aux::ref<au::edu::anu::chem::mm::TestElectrostatic>)this)->sizeOfCentralCluster()) / (((x10_double) (gridSize))))))));
                                    
                                    //#line 70 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10LocalDecl_c
                                    x10_double z =
                                      ((clusterStart) + (((((((((x10_double) (gridZ))) + (0.5))) + (au::edu::anu::chem::mm::TestElectrostatic::randomNoise()))) * (((((x10aux::ref<au::edu::anu::chem::mm::TestElectrostatic>)this)->sizeOfCentralCluster()) / (((x10_double) (gridSize))))))));
                                    
                                    //#line 71 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10LocalDecl_c
                                    x10_int charge =
                                      (x10aux::struct_equals(((x10_int) ((i) % x10aux::zeroCheck(((x10_int)2)))),
                                                             ((x10_int)0)))
                                      ? (((x10_int)1))
                                      : (((x10_int)-1));
                                    
                                    //#line 72 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10LocalDecl_c
                                    x10_int p =
                                      ((x10aux::ref<au::edu::anu::chem::mm::TestElectrostatic>)this)->getPlaceId(
                                        x,
                                        y,
                                        z);
                                    
                                    //#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10Call_c
                                    x10::lang::Runtime::runAsync(
                                      (__extension__ ({
                                          
                                          //#line 127 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Place.x10": x10.ast.X10LocalDecl_c
                                          x10_int id33152 =
                                            p;
                                          (__extension__ ({
                                              
                                              //#line 127 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Place.x10": x10.ast.X10LocalDecl_c
                                              x10::lang::Place alloc3180633153 =
                                                
                                              x10::lang::Place::_alloc();
                                              
                                              //#line 127 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Place.x10": x10.ast.X10ConstructorCall_c
                                              (alloc3180633153)->::x10::lang::Place::_constructor(
                                                id33152);
                                              alloc3180633153;
                                          }))
                                          ;
                                      }))
                                      ,
                                      x10aux::ref<x10::lang::VoidFun_0_0>(x10aux::ref<au_edu_anu_chem_mm_TestElectrostatic__closure__2>(new (x10aux::alloc<x10::lang::VoidFun_0_0>(sizeof(au_edu_anu_chem_mm_TestElectrostatic__closure__2)))au_edu_anu_chem_mm_TestElectrostatic__closure__2(x, y, z, charge, tempAtoms, p))));
                                    
                                    //#line 78 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10LocalAssign_c
                                    gridPoint =
                                      ((x10_int) ((gridPoint) + (((x10_int)1))));
                                }
                            }
                            
                        }
                    }
                    catch (x10aux::__ref& __ref__100) {
                        x10aux::ref<x10::lang::Throwable>& __exc__ref__100 = (x10aux::ref<x10::lang::Throwable>&)__ref__100;
                        if (true) {
                            x10aux::ref<x10::lang::Throwable> __lowerer__var__3__ =
                              static_cast<x10aux::ref<x10::lang::Throwable> >(__exc__ref__100);
                            {
                                
                                //#line 64 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10Call_c
                                x10::lang::Runtime::pushException(
                                  __lowerer__var__3__);
                                
                                //#line 64 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": polyglot.ast.Throw_c
                                x10aux::throwException(x10aux::nullCheck(x10::lang::RuntimeException::_make()));
                            }
                        } else
                        throw;
                    }
                    
                    //#line 64 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10Call_c
                    x10::compiler::Finalization::plausibleThrow();
                }
                catch (x10aux::__ref& __ref__101) {
                    x10aux::ref<x10::lang::Throwable>& __exc__ref__101 = (x10aux::ref<x10::lang::Throwable>&)__ref__101;
                    if (true) {
                        x10aux::ref<x10::lang::Throwable> formal34383 =
                          static_cast<x10aux::ref<x10::lang::Throwable> >(__exc__ref__101);
                        {
                            
                            //#line 64 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10LocalAssign_c
                            throwable34382 =
                              formal34383;
                        }
                    } else
                    throw;
                }
                
                //#line 64 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10If_c
                if ((!x10aux::struct_equals(X10_NULL,
                                            throwable34382)))
                {
                    
                    //#line 64 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10If_c
                    if (x10aux::instanceof<x10aux::ref<x10::compiler::Abort> >(throwable34382))
                    {
                        
                        //#line 64 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": polyglot.ast.Throw_c
                        x10aux::throwException(x10aux::nullCheck(throwable34382));
                    }
                    
                }
                
                //#line 64 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10If_c
                if (true) {
                    
                    //#line 64 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10Call_c
                    x10::lang::Runtime::stopFinish(
                      x10____var1);
                }
                
                //#line 64 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10If_c
                if ((!x10aux::struct_equals(X10_NULL,
                                            throwable34382)))
                {
                    
                    //#line 64 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10If_c
                    if (!(x10aux::instanceof<x10aux::ref<x10::compiler::Finalization> >(throwable34382)))
                    {
                        
                        //#line 64 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": polyglot.ast.Throw_c
                        x10aux::throwException(x10aux::nullCheck(throwable34382));
                    }
                    
                }
                
            }
        }
        
        //#line 80 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > atoms =
          (__extension__ ({
            
            //#line 140 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::array::Dist> dist33154 =
              (__extension__ ({
                x10aux::class_cast<x10aux::ref<x10::array::Dist> >(x10::array::Dist::
                                                                     FMGL(UNIQUE__get)());
            }))
            ;
            
            //#line 140 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > init33155 =
              x10aux::class_cast_unchecked<x10aux::ref<x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > >(x10aux::ref<x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > >(x10aux::ref<au_edu_anu_chem_mm_TestElectrostatic__closure__3>(new (x10aux::alloc<x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > >(sizeof(au_edu_anu_chem_mm_TestElectrostatic__closure__3)))au_edu_anu_chem_mm_TestElectrostatic__closure__3(tempAtoms))));
            (__extension__ ({
                
                //#line 140 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > alloc3048633156 =
                  
                x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > >((new (memset(x10aux::alloc<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > >(), 0, sizeof(x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > >))) x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > >()))
                ;
                
                //#line 140 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10": x10.ast.X10ConstructorCall_c
                (alloc3048633156)->::x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > >::_constructor(
                  dist33154,
                  init33155);
                alloc3048633156;
            }))
            ;
        }))
        ;
        
        //#line 81 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10Return_c
        return atoms;
        
    }
    
    //#line 88 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10MethodDecl_c
    x10_int au::edu::anu::chem::mm::TestElectrostatic::getPlaceId(
      x10_double x,
      x10_double y,
      x10_double z) {
        
        //#line 89 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10Return_c
        return x10aux::double_utils::toInt(((((x) / (80.0))) * (((x10_double) (x10aux::num_hosts)))));
        
    }
    
    //#line 96 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10MethodDecl_c
    x10_double au::edu::anu::chem::mm::TestElectrostatic::randomNoise(
      ) {
        
        //#line 97 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10Return_c
        return ((((au::edu::anu::chem::mm::TestElectrostatic::
                     FMGL(R__get)()->nextDouble()) - (0.5))) * (0.25));
        
    }
    
    //#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10MethodDecl_c
    x10aux::ref<au::edu::anu::chem::mm::TestElectrostatic>
      au::edu::anu::chem::mm::TestElectrostatic::au__edu__anu__chem__mm__TestElectrostatic____au__edu__anu__chem__mm__TestElectrostatic__this(
      ) {
        
        //#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10Return_c
        return ((x10aux::ref<au::edu::anu::chem::mm::TestElectrostatic>)this);
        
    }
    
    //#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10ConstructorDecl_c
    void au::edu::anu::chem::mm::TestElectrostatic::_constructor(
      ) {
        
        //#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10ConstructorCall_c
        (((x10aux::ref<x10::lang::Object>)this))->::x10::lang::Object::_constructor();
        
        //#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.AssignPropertyCall_c
        
        //#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.StmtExpr_c
        (__extension__ ({
            
            //#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<au::edu::anu::chem::mm::TestElectrostatic> this3436734373 =
              ((x10aux::ref<au::edu::anu::chem::mm::TestElectrostatic>)this);
            {
                
                //#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(this3436734373)->
                  FMGL(X10__object_lock_id0) =
                  ((x10_int)-1);
                
                //#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(this3436734373)->
                  FMGL(SIZE) = 80.0;
            }
            
        }))
        ;
        
    }
    x10aux::ref<au::edu::anu::chem::mm::TestElectrostatic> au::edu::anu::chem::mm::TestElectrostatic::_make(
      ) {
        x10aux::ref<au::edu::anu::chem::mm::TestElectrostatic> this_ = new (memset(x10aux::alloc<au::edu::anu::chem::mm::TestElectrostatic>(), 0, sizeof(au::edu::anu::chem::mm::TestElectrostatic))) au::edu::anu::chem::mm::TestElectrostatic();
        this_->_constructor();
        return this_;
    }
    
    
    
    //#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10ConstructorDecl_c
    void au::edu::anu::chem::mm::TestElectrostatic::_constructor(
      x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
    {
        
        //#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10ConstructorCall_c
        (((x10aux::ref<x10::lang::Object>)this))->::x10::lang::Object::_constructor();
        
        //#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.AssignPropertyCall_c
        
        //#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.StmtExpr_c
        (__extension__ ({
            
            //#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<au::edu::anu::chem::mm::TestElectrostatic> this3437034374 =
              ((x10aux::ref<au::edu::anu::chem::mm::TestElectrostatic>)this);
            {
                
                //#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(this3437034374)->
                  FMGL(X10__object_lock_id0) =
                  ((x10_int)-1);
                
                //#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(this3437034374)->
                  FMGL(SIZE) =
                  80.0;
            }
            
        }))
        ;
        
        //#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10FieldAssign_c
        ((x10aux::ref<au::edu::anu::chem::mm::TestElectrostatic>)this)->
          FMGL(X10__object_lock_id0) =
          x10aux::nullCheck(paramLock)->getIndex();
        
    }
    x10aux::ref<au::edu::anu::chem::mm::TestElectrostatic> au::edu::anu::chem::mm::TestElectrostatic::_make(
      x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
    {
        x10aux::ref<au::edu::anu::chem::mm::TestElectrostatic> this_ = new (memset(x10aux::alloc<au::edu::anu::chem::mm::TestElectrostatic>(), 0, sizeof(au::edu::anu::chem::mm::TestElectrostatic))) au::edu::anu::chem::mm::TestElectrostatic();
        this_->_constructor(paramLock);
        return this_;
    }
    
    
    
    //#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10MethodDecl_c
    void au::edu::anu::chem::mm::TestElectrostatic::__fieldInitializers23633(
      ) {
        
        //#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10FieldAssign_c
        ((x10aux::ref<au::edu::anu::chem::mm::TestElectrostatic>)this)->
          FMGL(X10__object_lock_id0) = ((x10_int)-1);
        
        //#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10": x10.ast.X10FieldAssign_c
        ((x10aux::ref<au::edu::anu::chem::mm::TestElectrostatic>)this)->
          FMGL(SIZE) = 80.0;
    }
    const x10aux::serialization_id_t au::edu::anu::chem::mm::TestElectrostatic::_serialization_id = 
        x10aux::DeserializationDispatcher::addDeserializer(au::edu::anu::chem::mm::TestElectrostatic::_deserializer, x10aux::CLOSURE_KIND_NOT_ASYNC);
    
    void au::edu::anu::chem::mm::TestElectrostatic::_serialize_body(x10aux::serialization_buffer& buf) {
        x10::lang::Object::_serialize_body(buf);
        buf.write(this->FMGL(SIZE));
        
    }
    
    x10aux::ref<x10::lang::Reference> au::edu::anu::chem::mm::TestElectrostatic::_deserializer(x10aux::deserialization_buffer& buf) {
        x10aux::ref<au::edu::anu::chem::mm::TestElectrostatic> this_ = new (memset(x10aux::alloc<au::edu::anu::chem::mm::TestElectrostatic>(), 0, sizeof(au::edu::anu::chem::mm::TestElectrostatic))) au::edu::anu::chem::mm::TestElectrostatic();
        buf.record_reference(this_);
        this_->_deserialize_body(buf);
        return this_;
    }
    
    void au::edu::anu::chem::mm::TestElectrostatic::_deserialize_body(x10aux::deserialization_buffer& buf) {
        x10::lang::Object::_deserialize_body(buf);
        FMGL(SIZE) = buf.read<x10_double>();
    }
    
    
x10aux::RuntimeType au::edu::anu::chem::mm::TestElectrostatic::rtt;
void au::edu::anu::chem::mm::TestElectrostatic::_initRTT() {
    if (rtt.initStageOne(&rtt)) return;
    const x10aux::RuntimeType* parents[1] = { x10aux::getRTT<x10::lang::Object>()};
    rtt.initStageTwo("au.edu.anu.chem.mm.TestElectrostatic",x10aux::RuntimeType::class_kind, 1, parents, 0, NULL, NULL);
}
x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::util::ArrayList<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > >::itable<au_edu_anu_chem_mm_TestElectrostatic__closure__1>au_edu_anu_chem_mm_TestElectrostatic__closure__1::_itable(&x10::lang::Reference::equals, &x10::lang::Closure::hashCode, &au_edu_anu_chem_mm_TestElectrostatic__closure__1::__apply, &au_edu_anu_chem_mm_TestElectrostatic__closure__1::toString, &x10::lang::Closure::typeName);
x10aux::itable_entry au_edu_anu_chem_mm_TestElectrostatic__closure__1::_itables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::util::ArrayList<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > >, &au_edu_anu_chem_mm_TestElectrostatic__closure__1::_itable),x10aux::itable_entry(NULL, NULL)};

const x10aux::serialization_id_t au_edu_anu_chem_mm_TestElectrostatic__closure__1::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(au_edu_anu_chem_mm_TestElectrostatic__closure__1::_deserialize<x10::lang::Reference>,x10aux::CLOSURE_KIND_NOT_ASYNC);

x10::lang::VoidFun_0_0::itable<au_edu_anu_chem_mm_TestElectrostatic__closure__2>au_edu_anu_chem_mm_TestElectrostatic__closure__2::_itable(&x10::lang::Reference::equals, &x10::lang::Closure::hashCode, &au_edu_anu_chem_mm_TestElectrostatic__closure__2::__apply, &au_edu_anu_chem_mm_TestElectrostatic__closure__2::toString, &x10::lang::Closure::typeName);
x10aux::itable_entry au_edu_anu_chem_mm_TestElectrostatic__closure__2::_itables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::VoidFun_0_0>, &au_edu_anu_chem_mm_TestElectrostatic__closure__2::_itable),x10aux::itable_entry(NULL, NULL)};

const x10aux::serialization_id_t au_edu_anu_chem_mm_TestElectrostatic__closure__2::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(au_edu_anu_chem_mm_TestElectrostatic__closure__2::_deserialize<x10::lang::Reference>,x10aux::CLOSURE_KIND_SIMPLE_ASYNC);

x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > >::itable<au_edu_anu_chem_mm_TestElectrostatic__closure__3>au_edu_anu_chem_mm_TestElectrostatic__closure__3::_itable(&x10::lang::Reference::equals, &x10::lang::Closure::hashCode, &au_edu_anu_chem_mm_TestElectrostatic__closure__3::__apply, &au_edu_anu_chem_mm_TestElectrostatic__closure__3::toString, &x10::lang::Closure::typeName);
x10aux::itable_entry au_edu_anu_chem_mm_TestElectrostatic__closure__3::_itables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > >, &au_edu_anu_chem_mm_TestElectrostatic__closure__3::_itable),x10aux::itable_entry(NULL, NULL)};

const x10aux::serialization_id_t au_edu_anu_chem_mm_TestElectrostatic__closure__3::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(au_edu_anu_chem_mm_TestElectrostatic__closure__3::_deserialize<x10::lang::Reference>,x10aux::CLOSURE_KIND_NOT_ASYNC);

/* END of TestElectrostatic */
/*************************************************/
