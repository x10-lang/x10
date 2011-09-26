/*************************************************/
/* START of ElectrostaticDirectMethod */
#include <au/edu/anu/chem/mm/ElectrostaticDirectMethod.h>

#include <x10/lang/Object.h>
#include <x10/util/concurrent/Atomic.h>
#include <x10/lang/Int.h>
#include <x10/util/concurrent/OrderedLock.h>
#include <x10/util/Map.h>
#include <au/edu/anu/util/Timer.h>
#include <x10/lang/Boolean.h>
#include <x10/array/DistArray.h>
#include <x10/array/Array.h>
#include <au/edu/anu/chem/PointCharge.h>
#include <au/edu/anu/chem/mm/MMAtom.h>
#include <x10/array/Dist.h>
#include <x10/lang/Fun_0_1.h>
#include <x10/array/Point.h>
#include <au/edu/anu/chem/Atom.h>
#include <x10/util/IndexedMemoryChunk.h>
#include <x10/array/Region.h>
#include <x10/array/RectRegion1D.h>
#include <x10/array/RectLayout.h>
#include <x10/lang/Place.h>
#include <x10/lang/Double.h>
#include <x10/lang/FinishState.h>
#include <x10/lang/Runtime.h>
#include <x10/lang/Reducible.h>
#include <au/edu/anu/chem/mm/ElectrostaticDirectMethod__SumReducer.h>
#include <x10/lang/Throwable.h>
#include <x10/lang/Iterator.h>
#include <x10/lang/Iterable.h>
#include <x10/lang/VoidFun_0_0.h>
#include <x10/compiler/Finalization.h>
#include <x10/compiler/Abort.h>
#include <x10/compiler/CompilerFlags.h>
#include <x10x/vector/Point3d.h>
#include <x10/lang/Math.h>
#include <x10/compiler/AsyncClosure.h>
#include <x10/lang/RuntimeException.h>
#ifndef AU_EDU_ANU_CHEM_MM_ELECTROSTATICDIRECTMETHOD__CLOSURE__2_CLOSURE
#define AU_EDU_ANU_CHEM_MM_ELECTROSTATICDIRECTMETHOD__CLOSURE__2_CLOSURE
#include <x10/lang/Closure.h>
#include <x10/lang/Fun_0_1.h>
class au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__2 : public x10::lang::Closure {
    public:
    
    static x10::lang::Fun_0_1<x10_int, au::edu::anu::chem::PointCharge>::itable<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__2> _itable;
    static x10aux::itable_entry _itables[2];
    
    virtual x10aux::itable_entry* _getITables() { return _itables; }
    
    // closure body
    au::edu::anu::chem::PointCharge __apply(x10_int j) {
        
        //#line 46 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10Return_c
        return (__extension__ ({
            
            //#line 46 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
            au::edu::anu::chem::PointCharge alloc25966 =  au::edu::anu::chem::PointCharge::_alloc();
            
            //#line 46 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10ConstructorCall_c
            (alloc25966)->::au::edu::anu::chem::PointCharge::_constructor(
              x10aux::nullCheck((__extension__ ({
                  
                  //#line 46 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
                  x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > this30528 =
                    x10aux::nullCheck(atoms)->x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > >::__apply(
                      i);
                  
                  //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                  x10_int i030527 =
                    j;
                  
                  //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                  x10aux::ref<au::edu::anu::chem::mm::MMAtom> ret30529;
                  
                  //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                  goto __ret30530; __ret30530: {
                  {
                      
                      //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                      ret30529 =
                        (x10aux::nullCheck(this30528)->
                           FMGL(raw))->__apply(i030527);
                      
                      //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                      goto __ret30530_end_;
                  }goto __ret30530_end_; __ret30530_end_: ;
                  }
                  ret30529;
                  }))
                  )->
                    FMGL(centre),
              x10aux::nullCheck((__extension__ ({
                  
                  //#line 46 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
                  x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > this30537 =
                    x10aux::nullCheck(atoms)->x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > >::__apply(
                      i);
                  
                  //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                  x10_int i030536 =
                    j;
                  
                  //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                  x10aux::ref<au::edu::anu::chem::mm::MMAtom> ret30538;
                  
                  //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                  goto __ret30539; __ret30539: {
                  {
                      
                      //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                      ret30538 =
                        (x10aux::nullCheck(this30537)->
                           FMGL(raw))->__apply(i030536);
                      
                      //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                      goto __ret30539_end_;
                  }goto __ret30539_end_; __ret30539_end_: ;
                  }
                  ret30538;
                  }))
                  )->
                    FMGL(charge),
              x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
              alloc25966;
            }))
            ;
            
        }
        
        // captured environment
        x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > atoms;
        x10_int i;
        
        x10aux::serialization_id_t _get_serialization_id() {
            return _serialization_id;
        }
        
        void _serialize_body(x10aux::serialization_buffer &buf) {
            buf.write(this->atoms);
            buf.write(this->i);
        }
        
        template<class __T> static x10aux::ref<__T> _deserialize(x10aux::deserialization_buffer &buf) {
            au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__2* storage = x10aux::alloc<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__2>();
            buf.record_reference(x10aux::ref<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__2>(storage));
            x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > that_atoms = buf.read<x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > >();
            x10_int that_i = buf.read<x10_int>();
            x10aux::ref<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__2> this_ = new (storage) au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__2(that_atoms, that_i);
            return this_;
        }
        
        au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__2(x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > atoms, x10_int i) : atoms(atoms), i(i) { }
        
        static const x10aux::serialization_id_t _serialization_id;
        
        static const x10aux::RuntimeType* getRTT() { return x10aux::getRTT<x10::lang::Fun_0_1<x10_int, au::edu::anu::chem::PointCharge> >(); }
        virtual const x10aux::RuntimeType *_type() const { return x10aux::getRTT<x10::lang::Fun_0_1<x10_int, au::edu::anu::chem::PointCharge> >(); }
        
        x10aux::ref<x10::lang::String> toString() {
            return x10aux::string_utils::lit(this->toNativeString());
        }
        
        const char* toNativeString() {
            return "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10:46";
        }
        
        };
        
        #endif // AU_EDU_ANU_CHEM_MM_ELECTROSTATICDIRECTMETHOD__CLOSURE__2_CLOSURE
        #ifndef AU_EDU_ANU_CHEM_MM_ELECTROSTATICDIRECTMETHOD__CLOSURE__1_CLOSURE
#define AU_EDU_ANU_CHEM_MM_ELECTROSTATICDIRECTMETHOD__CLOSURE__1_CLOSURE
#include <x10/lang/Closure.h>
#include <x10/lang/Fun_0_1.h>
class au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__1 : public x10::lang::Closure {
    public:
    
    static x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > >::itable<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__1> _itable;
    static x10aux::itable_entry _itables[2];
    
    virtual x10aux::itable_entry* _getITables() { return _itables; }
    
    // closure body
    x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > __apply(x10aux::ref<x10::array::Point> id2) {
        
        //#line 45 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
        x10_int i = x10aux::nullCheck(id2)->x10::array::Point::__apply(((x10_int)0));
        
        //#line 45 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10Return_c
        return (__extension__ ({
            
            //#line 45 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > alloc25967 =
              
            x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> >((new (memset(x10aux::alloc<x10::array::Array<au::edu::anu::chem::PointCharge> >(), 0, sizeof(x10::array::Array<au::edu::anu::chem::PointCharge>))) x10::array::Array<au::edu::anu::chem::PointCharge>()))
            ;
            
            //#line 45 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.StmtExpr_c
            (__extension__ ({
                
                //#line 271 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_int size30624 = x10aux::nullCheck(x10aux::nullCheck(atoms)->x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > >::__apply(
                                                        i))->
                                      FMGL(size);
                
                //#line 271 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<x10::lang::Fun_0_1<x10_int, au::edu::anu::chem::PointCharge> > init30625 =
                  x10aux::class_cast_unchecked<x10aux::ref<x10::lang::Fun_0_1<x10_int, au::edu::anu::chem::PointCharge> > >(x10aux::ref<x10::lang::Fun_0_1<x10_int, au::edu::anu::chem::PointCharge> >(x10aux::ref<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__2>(new (x10aux::alloc<x10::lang::Fun_0_1<x10_int, au::edu::anu::chem::PointCharge> >(sizeof(au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__2)))au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__2(atoms, i))));
                {
                    
                    //#line 271 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                    (alloc25967)->::x10::lang::Object::_constructor();
                    
                    //#line 273 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10aux::ref<x10::array::Region> myReg30626 =
                      x10aux::class_cast<x10aux::ref<x10::array::Region> >((__extension__ ({
                        
                        //#line 273 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10aux::ref<x10::array::RectRegion1D> alloc1996230627 =
                          
                        x10aux::ref<x10::array::RectRegion1D>((new (memset(x10aux::alloc<x10::array::RectRegion1D>(), 0, sizeof(x10::array::RectRegion1D))) x10::array::RectRegion1D()))
                        ;
                        
                        //#line 273 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                        (alloc1996230627)->::x10::array::RectRegion1D::_constructor(
                          ((x10_int)0),
                          ((x10_int) ((size30624) - (((x10_int)1)))));
                        alloc1996230627;
                    }))
                    );
                    
                    //#line 274 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                    x10aux::nullCheck(alloc25967)->
                      FMGL(region) = myReg30626;
                    
                    //#line 274 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                    x10aux::nullCheck(alloc25967)->
                      FMGL(rank) = ((x10_int)1);
                    
                    //#line 274 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                    x10aux::nullCheck(alloc25967)->
                      FMGL(rect) = true;
                    
                    //#line 274 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                    x10aux::nullCheck(alloc25967)->
                      FMGL(zeroBased) = true;
                    
                    //#line 274 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                    x10aux::nullCheck(alloc25967)->
                      FMGL(rail) = true;
                    
                    //#line 274 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                    x10aux::nullCheck(alloc25967)->
                      FMGL(size) = size30624;
                    
                    //#line 276 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                    x10aux::nullCheck(alloc25967)->
                      FMGL(layout) = (__extension__ ({
                        
                        //#line 276 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::array::RectLayout alloc1996330628 =
                          
                        x10::array::RectLayout::_alloc();
                        
                        //#line 276 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.StmtExpr_c
                        (__extension__ ({
                            
                            //#line 97 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                            x10_int _max031450 =
                              ((x10_int) ((size30624) - (((x10_int)1))));
                            {
                                
                                //#line 98 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996330628->
                                  FMGL(rank) =
                                  ((x10_int)1);
                                
                                //#line 99 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996330628->
                                  FMGL(min0) =
                                  ((x10_int)0);
                                
                                //#line 100 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996330628->
                                  FMGL(delta0) =
                                  ((x10_int) ((((x10_int) ((_max031450) - (((x10_int)0))))) + (((x10_int)1))));
                                
                                //#line 101 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996330628->
                                  FMGL(size) =
                                  ((alloc1996330628->
                                      FMGL(delta0)) > (((x10_int)0)))
                                  ? (alloc1996330628->
                                       FMGL(delta0))
                                  : (((x10_int)0));
                                
                                //#line 103 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996330628->
                                  FMGL(min1) =
                                  ((x10_int)0);
                                
                                //#line 103 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996330628->
                                  FMGL(delta1) =
                                  ((x10_int)0);
                                
                                //#line 104 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996330628->
                                  FMGL(min2) =
                                  ((x10_int)0);
                                
                                //#line 104 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996330628->
                                  FMGL(delta2) =
                                  ((x10_int)0);
                                
                                //#line 105 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996330628->
                                  FMGL(min3) =
                                  ((x10_int)0);
                                
                                //#line 105 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996330628->
                                  FMGL(delta3) =
                                  ((x10_int)0);
                                
                                //#line 106 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996330628->
                                  FMGL(min) =
                                  X10_NULL;
                                
                                //#line 106 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996330628->
                                  FMGL(delta) =
                                  X10_NULL;
                            }
                            
                        }))
                        ;
                        alloc1996330628;
                    }))
                    ;
                    
                    //#line 277 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10_int n30629 = (__extension__ ({
                        
                        //#line 277 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::array::RectLayout this31452 =
                          x10aux::nullCheck(alloc25967)->
                            FMGL(layout);
                        this31452->FMGL(size);
                    }))
                    ;
                    
                    //#line 278 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10::util::IndexedMemoryChunk<au::edu::anu::chem::PointCharge > r30630 =
                      x10::util::IndexedMemoryChunk<void>::allocate<au::edu::anu::chem::PointCharge >(n30629, 8, false, false);
                    
                    //#line 279 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10_int i19980max199823063231947 =
                      ((x10_int) ((size30624) - (((x10_int)1))));
                    
                    //#line 279 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.For_c
                    {
                        x10_int i199803063331948;
                        for (
                             //#line 279 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                             i199803063331948 =
                               ((x10_int)0);
                             ((i199803063331948) <= (i19980max199823063231947));
                             
                             //#line 279 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                             i199803063331948 =
                               ((x10_int) ((i199803063331948) + (((x10_int)1)))))
                        {
                            
                            //#line 279 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i3063431949 =
                              i199803063331948;
                            
                            //#line 280 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                            (r30630)->__set(i3063431949, x10::lang::Fun_0_1<x10_int, au::edu::anu::chem::PointCharge>::__apply(x10aux::nullCheck(init30625), 
                              i3063431949));
                        }
                    }
                    
                    //#line 282 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                    x10aux::nullCheck(alloc25967)->
                      FMGL(raw) = r30630;
                }
                
            }))
            ;
            alloc25967;
        }))
        ;
        
    }
    
    // captured environment
    x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > atoms;
    
    x10aux::serialization_id_t _get_serialization_id() {
        return _serialization_id;
    }
    
    void _serialize_body(x10aux::serialization_buffer &buf) {
        buf.write(this->atoms);
    }
    
    template<class __T> static x10aux::ref<__T> _deserialize(x10aux::deserialization_buffer &buf) {
        au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__1* storage = x10aux::alloc<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__1>();
        buf.record_reference(x10aux::ref<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__1>(storage));
        x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > that_atoms = buf.read<x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > >();
        x10aux::ref<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__1> this_ = new (storage) au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__1(that_atoms);
        return this_;
    }
    
    au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__1(x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > atoms) : atoms(atoms) { }
    
    static const x10aux::serialization_id_t _serialization_id;
    
    static const x10aux::RuntimeType* getRTT() { return x10aux::getRTT<x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > >(); }
    virtual const x10aux::RuntimeType *_type() const { return x10aux::getRTT<x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > >(); }
    
    x10aux::ref<x10::lang::String> toString() {
        return x10aux::string_utils::lit(this->toNativeString());
    }
    
    const char* toNativeString() {
        return "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10:45-46";
    }

};

#endif // AU_EDU_ANU_CHEM_MM_ELECTROSTATICDIRECTMETHOD__CLOSURE__1_CLOSURE
#ifndef AU_EDU_ANU_CHEM_MM_ELECTROSTATICDIRECTMETHOD__CLOSURE__3_CLOSURE
#define AU_EDU_ANU_CHEM_MM_ELECTROSTATICDIRECTMETHOD__CLOSURE__3_CLOSURE
#include <x10/lang/Closure.h>
#include <x10/lang/Fun_0_1.h>
class au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__3 : public x10::lang::Closure {
    public:
    
    static x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > >::itable<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__3> _itable;
    static x10aux::itable_entry _itables[2];
    
    virtual x10aux::itable_entry* _getITables() { return _itables; }
    
    // closure body
    x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > __apply(x10aux::ref<x10::array::Point> id3) {
        
        //#line 48 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
        x10_int p = x10aux::nullCheck(id3)->x10::array::Point::__apply(((x10_int)0));
        
        //#line 48 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10Return_c
        return (__extension__ ({
            
            //#line 48 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > alloc25968 =
              
            x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > >((new (memset(x10aux::alloc<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > >(), 0, sizeof(x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > >))) x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > >()))
            ;
            
            //#line 48 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.StmtExpr_c
            (__extension__ ({
                
                //#line 243 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_int size31456 = x10aux::num_hosts;
                {
                    
                    //#line 243 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                    (alloc25968)->::x10::lang::Object::_constructor();
                    
                    //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10aux::ref<x10::array::Region> myReg31457 = x10aux::class_cast<x10aux::ref<x10::array::Region> >((__extension__ ({
                        
                        //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10aux::ref<x10::array::RectRegion1D> alloc1996031458 =
                          
                        x10aux::ref<x10::array::RectRegion1D>((new (memset(x10aux::alloc<x10::array::RectRegion1D>(), 0, sizeof(x10::array::RectRegion1D))) x10::array::RectRegion1D()))
                        ;
                        
                        //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                        (alloc1996031458)->::x10::array::RectRegion1D::_constructor(
                          ((x10_int)0),
                          ((x10_int) ((size31456) - (((x10_int)1)))));
                        alloc1996031458;
                    }))
                    );
                    
                    //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                    x10aux::nullCheck(alloc25968)->FMGL(region) =
                      myReg31457;
                    
                    //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                    x10aux::nullCheck(alloc25968)->FMGL(rank) =
                      ((x10_int)1);
                    
                    //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                    x10aux::nullCheck(alloc25968)->FMGL(rect) =
                      true;
                    
                    //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                    x10aux::nullCheck(alloc25968)->FMGL(zeroBased) =
                      true;
                    
                    //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                    x10aux::nullCheck(alloc25968)->FMGL(rail) =
                      true;
                    
                    //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                    x10aux::nullCheck(alloc25968)->FMGL(size) =
                      size31456;
                    
                    //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                    x10aux::nullCheck(alloc25968)->FMGL(layout) =
                      (__extension__ ({
                        
                        //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::array::RectLayout alloc1996131459 =
                          
                        x10::array::RectLayout::_alloc();
                        
                        //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.StmtExpr_c
                        (__extension__ ({
                            
                            //#line 97 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                            x10_int _max031463 =
                              ((x10_int) ((size31456) - (((x10_int)1))));
                            {
                                
                                //#line 98 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996131459->
                                  FMGL(rank) =
                                  ((x10_int)1);
                                
                                //#line 99 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996131459->
                                  FMGL(min0) =
                                  ((x10_int)0);
                                
                                //#line 100 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996131459->
                                  FMGL(delta0) =
                                  ((x10_int) ((((x10_int) ((_max031463) - (((x10_int)0))))) + (((x10_int)1))));
                                
                                //#line 101 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996131459->
                                  FMGL(size) =
                                  ((alloc1996131459->
                                      FMGL(delta0)) > (((x10_int)0)))
                                  ? (alloc1996131459->
                                       FMGL(delta0))
                                  : (((x10_int)0));
                                
                                //#line 103 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996131459->
                                  FMGL(min1) =
                                  ((x10_int)0);
                                
                                //#line 103 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996131459->
                                  FMGL(delta1) =
                                  ((x10_int)0);
                                
                                //#line 104 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996131459->
                                  FMGL(min2) =
                                  ((x10_int)0);
                                
                                //#line 104 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996131459->
                                  FMGL(delta2) =
                                  ((x10_int)0);
                                
                                //#line 105 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996131459->
                                  FMGL(min3) =
                                  ((x10_int)0);
                                
                                //#line 105 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996131459->
                                  FMGL(delta3) =
                                  ((x10_int)0);
                                
                                //#line 106 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996131459->
                                  FMGL(min) =
                                  X10_NULL;
                                
                                //#line 106 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996131459->
                                  FMGL(delta) =
                                  X10_NULL;
                            }
                            
                        }))
                        ;
                        alloc1996131459;
                    }))
                    ;
                    
                    //#line 250 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10_int n31460 = (__extension__ ({
                        
                        //#line 250 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::array::RectLayout this31465 =
                          x10aux::nullCheck(alloc25968)->
                            FMGL(layout);
                        this31465->FMGL(size);
                    }))
                    ;
                    
                    //#line 251 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                    x10aux::nullCheck(alloc25968)->FMGL(raw) =
                      x10::util::IndexedMemoryChunk<void>::allocate<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > >(n31460, 8, false, true);
                }
                
            }))
            ;
            alloc25968;
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
        au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__3* storage = x10aux::alloc<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__3>();
        buf.record_reference(x10aux::ref<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__3>(storage));
        x10aux::ref<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__3> this_ = new (storage) au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__3();
        return this_;
    }
    
    au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__3() { }
    
    static const x10aux::serialization_id_t _serialization_id;
    
    static const x10aux::RuntimeType* getRTT() { return x10aux::getRTT<x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > > >(); }
    virtual const x10aux::RuntimeType *_type() const { return x10aux::getRTT<x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > > >(); }
    
    x10aux::ref<x10::lang::String> toString() {
        return x10aux::string_utils::lit(this->toNativeString());
    }
    
    const char* toNativeString() {
        return "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10:48";
    }

};

#endif // AU_EDU_ANU_CHEM_MM_ELECTROSTATICDIRECTMETHOD__CLOSURE__3_CLOSURE
#ifndef AU_EDU_ANU_CHEM_MM_ELECTROSTATICDIRECTMETHOD__CLOSURE__5_CLOSURE
#define AU_EDU_ANU_CHEM_MM_ELECTROSTATICDIRECTMETHOD__CLOSURE__5_CLOSURE
#include <x10/lang/Closure.h>
#include <x10/lang/Fun_0_1.h>
class au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__5 : public x10::lang::Closure {
    public:
    
    static x10::lang::Fun_0_1<x10_int, au::edu::anu::chem::PointCharge>::itable<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__5> _itable;
    static x10aux::itable_entry _itables[2];
    
    virtual x10aux::itable_entry* _getITables() { return _itables; }
    
    // closure body
    au::edu::anu::chem::PointCharge __apply(x10_int j) {
        
        //#line 46 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10Return_c
        return (__extension__ ({
            
            //#line 46 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
            au::edu::anu::chem::PointCharge alloc25969 =  au::edu::anu::chem::PointCharge::_alloc();
            
            //#line 46 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10ConstructorCall_c
            (alloc25969)->::au::edu::anu::chem::PointCharge::_constructor(
              x10aux::nullCheck((__extension__ ({
                  
                  //#line 46 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
                  x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > this31470 =
                    x10aux::nullCheck(atoms)->x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > >::__apply(
                      i);
                  
                  //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                  x10_int i031469 =
                    j;
                  
                  //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                  x10aux::ref<au::edu::anu::chem::mm::MMAtom> ret31471;
                  
                  //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                  goto __ret31472; __ret31472: {
                  {
                      
                      //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                      ret31471 =
                        (x10aux::nullCheck(this31470)->
                           FMGL(raw))->__apply(i031469);
                      
                      //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                      goto __ret31472_end_;
                  }goto __ret31472_end_; __ret31472_end_: ;
                  }
                  ret31471;
                  }))
                  )->
                    FMGL(centre),
              x10aux::nullCheck((__extension__ ({
                  
                  //#line 46 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
                  x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > this31479 =
                    x10aux::nullCheck(atoms)->x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > >::__apply(
                      i);
                  
                  //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                  x10_int i031478 =
                    j;
                  
                  //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                  x10aux::ref<au::edu::anu::chem::mm::MMAtom> ret31480;
                  
                  //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                  goto __ret31481; __ret31481: {
                  {
                      
                      //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                      ret31480 =
                        (x10aux::nullCheck(this31479)->
                           FMGL(raw))->__apply(i031478);
                      
                      //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                      goto __ret31481_end_;
                  }goto __ret31481_end_; __ret31481_end_: ;
                  }
                  ret31480;
                  }))
                  )->
                    FMGL(charge),
              x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
              alloc25969;
            }))
            ;
            
        }
        
        // captured environment
        x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > atoms;
        x10_int i;
        
        x10aux::serialization_id_t _get_serialization_id() {
            return _serialization_id;
        }
        
        void _serialize_body(x10aux::serialization_buffer &buf) {
            buf.write(this->atoms);
            buf.write(this->i);
        }
        
        template<class __T> static x10aux::ref<__T> _deserialize(x10aux::deserialization_buffer &buf) {
            au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__5* storage = x10aux::alloc<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__5>();
            buf.record_reference(x10aux::ref<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__5>(storage));
            x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > that_atoms = buf.read<x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > >();
            x10_int that_i = buf.read<x10_int>();
            x10aux::ref<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__5> this_ = new (storage) au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__5(that_atoms, that_i);
            return this_;
        }
        
        au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__5(x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > atoms, x10_int i) : atoms(atoms), i(i) { }
        
        static const x10aux::serialization_id_t _serialization_id;
        
        static const x10aux::RuntimeType* getRTT() { return x10aux::getRTT<x10::lang::Fun_0_1<x10_int, au::edu::anu::chem::PointCharge> >(); }
        virtual const x10aux::RuntimeType *_type() const { return x10aux::getRTT<x10::lang::Fun_0_1<x10_int, au::edu::anu::chem::PointCharge> >(); }
        
        x10aux::ref<x10::lang::String> toString() {
            return x10aux::string_utils::lit(this->toNativeString());
        }
        
        const char* toNativeString() {
            return "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10:46";
        }
        
        };
        
        #endif // AU_EDU_ANU_CHEM_MM_ELECTROSTATICDIRECTMETHOD__CLOSURE__5_CLOSURE
        #ifndef AU_EDU_ANU_CHEM_MM_ELECTROSTATICDIRECTMETHOD__CLOSURE__4_CLOSURE
#define AU_EDU_ANU_CHEM_MM_ELECTROSTATICDIRECTMETHOD__CLOSURE__4_CLOSURE
#include <x10/lang/Closure.h>
#include <x10/lang/Fun_0_1.h>
class au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__4 : public x10::lang::Closure {
    public:
    
    static x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > >::itable<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__4> _itable;
    static x10aux::itable_entry _itables[2];
    
    virtual x10aux::itable_entry* _getITables() { return _itables; }
    
    // closure body
    x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > __apply(x10aux::ref<x10::array::Point> id2) {
        
        //#line 45 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
        x10_int i = x10aux::nullCheck(id2)->x10::array::Point::__apply(((x10_int)0));
        
        //#line 45 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10Return_c
        return (__extension__ ({
            
            //#line 45 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > alloc25970 =
              
            x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> >((new (memset(x10aux::alloc<x10::array::Array<au::edu::anu::chem::PointCharge> >(), 0, sizeof(x10::array::Array<au::edu::anu::chem::PointCharge>))) x10::array::Array<au::edu::anu::chem::PointCharge>()))
            ;
            
            //#line 45 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.StmtExpr_c
            (__extension__ ({
                
                //#line 271 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_int size31487 = x10aux::nullCheck(x10aux::nullCheck(atoms)->x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > >::__apply(
                                                        i))->
                                      FMGL(size);
                
                //#line 271 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<x10::lang::Fun_0_1<x10_int, au::edu::anu::chem::PointCharge> > init31488 =
                  x10aux::class_cast_unchecked<x10aux::ref<x10::lang::Fun_0_1<x10_int, au::edu::anu::chem::PointCharge> > >(x10aux::ref<x10::lang::Fun_0_1<x10_int, au::edu::anu::chem::PointCharge> >(x10aux::ref<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__5>(new (x10aux::alloc<x10::lang::Fun_0_1<x10_int, au::edu::anu::chem::PointCharge> >(sizeof(au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__5)))au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__5(atoms, i))));
                {
                    
                    //#line 271 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                    (alloc25970)->::x10::lang::Object::_constructor();
                    
                    //#line 273 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10aux::ref<x10::array::Region> myReg31489 =
                      x10aux::class_cast<x10aux::ref<x10::array::Region> >((__extension__ ({
                        
                        //#line 273 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10aux::ref<x10::array::RectRegion1D> alloc1996231490 =
                          
                        x10aux::ref<x10::array::RectRegion1D>((new (memset(x10aux::alloc<x10::array::RectRegion1D>(), 0, sizeof(x10::array::RectRegion1D))) x10::array::RectRegion1D()))
                        ;
                        
                        //#line 273 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                        (alloc1996231490)->::x10::array::RectRegion1D::_constructor(
                          ((x10_int)0),
                          ((x10_int) ((size31487) - (((x10_int)1)))));
                        alloc1996231490;
                    }))
                    );
                    
                    //#line 274 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                    x10aux::nullCheck(alloc25970)->
                      FMGL(region) = myReg31489;
                    
                    //#line 274 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                    x10aux::nullCheck(alloc25970)->
                      FMGL(rank) = ((x10_int)1);
                    
                    //#line 274 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                    x10aux::nullCheck(alloc25970)->
                      FMGL(rect) = true;
                    
                    //#line 274 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                    x10aux::nullCheck(alloc25970)->
                      FMGL(zeroBased) = true;
                    
                    //#line 274 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                    x10aux::nullCheck(alloc25970)->
                      FMGL(rail) = true;
                    
                    //#line 274 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                    x10aux::nullCheck(alloc25970)->
                      FMGL(size) = size31487;
                    
                    //#line 276 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                    x10aux::nullCheck(alloc25970)->
                      FMGL(layout) = (__extension__ ({
                        
                        //#line 276 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::array::RectLayout alloc1996331491 =
                          
                        x10::array::RectLayout::_alloc();
                        
                        //#line 276 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.StmtExpr_c
                        (__extension__ ({
                            
                            //#line 97 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                            x10_int _max031500 =
                              ((x10_int) ((size31487) - (((x10_int)1))));
                            {
                                
                                //#line 98 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996331491->
                                  FMGL(rank) =
                                  ((x10_int)1);
                                
                                //#line 99 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996331491->
                                  FMGL(min0) =
                                  ((x10_int)0);
                                
                                //#line 100 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996331491->
                                  FMGL(delta0) =
                                  ((x10_int) ((((x10_int) ((_max031500) - (((x10_int)0))))) + (((x10_int)1))));
                                
                                //#line 101 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996331491->
                                  FMGL(size) =
                                  ((alloc1996331491->
                                      FMGL(delta0)) > (((x10_int)0)))
                                  ? (alloc1996331491->
                                       FMGL(delta0))
                                  : (((x10_int)0));
                                
                                //#line 103 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996331491->
                                  FMGL(min1) =
                                  ((x10_int)0);
                                
                                //#line 103 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996331491->
                                  FMGL(delta1) =
                                  ((x10_int)0);
                                
                                //#line 104 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996331491->
                                  FMGL(min2) =
                                  ((x10_int)0);
                                
                                //#line 104 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996331491->
                                  FMGL(delta2) =
                                  ((x10_int)0);
                                
                                //#line 105 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996331491->
                                  FMGL(min3) =
                                  ((x10_int)0);
                                
                                //#line 105 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996331491->
                                  FMGL(delta3) =
                                  ((x10_int)0);
                                
                                //#line 106 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996331491->
                                  FMGL(min) =
                                  X10_NULL;
                                
                                //#line 106 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996331491->
                                  FMGL(delta) =
                                  X10_NULL;
                            }
                            
                        }))
                        ;
                        alloc1996331491;
                    }))
                    ;
                    
                    //#line 277 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10_int n31492 = (__extension__ ({
                        
                        //#line 277 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::array::RectLayout this31502 =
                          x10aux::nullCheck(alloc25970)->
                            FMGL(layout);
                        this31502->FMGL(size);
                    }))
                    ;
                    
                    //#line 278 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10::util::IndexedMemoryChunk<au::edu::anu::chem::PointCharge > r31493 =
                      x10::util::IndexedMemoryChunk<void>::allocate<au::edu::anu::chem::PointCharge >(n31492, 8, false, false);
                    
                    //#line 279 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10_int i19980max199823149531950 =
                      ((x10_int) ((size31487) - (((x10_int)1))));
                    
                    //#line 279 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.For_c
                    {
                        x10_int i199803149631951;
                        for (
                             //#line 279 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                             i199803149631951 =
                               ((x10_int)0);
                             ((i199803149631951) <= (i19980max199823149531950));
                             
                             //#line 279 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                             i199803149631951 =
                               ((x10_int) ((i199803149631951) + (((x10_int)1)))))
                        {
                            
                            //#line 279 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i3149731952 =
                              i199803149631951;
                            
                            //#line 280 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                            (r31493)->__set(i3149731952, x10::lang::Fun_0_1<x10_int, au::edu::anu::chem::PointCharge>::__apply(x10aux::nullCheck(init31488), 
                              i3149731952));
                        }
                    }
                    
                    //#line 282 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                    x10aux::nullCheck(alloc25970)->
                      FMGL(raw) = r31493;
                }
                
            }))
            ;
            alloc25970;
        }))
        ;
        
    }
    
    // captured environment
    x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > atoms;
    
    x10aux::serialization_id_t _get_serialization_id() {
        return _serialization_id;
    }
    
    void _serialize_body(x10aux::serialization_buffer &buf) {
        buf.write(this->atoms);
    }
    
    template<class __T> static x10aux::ref<__T> _deserialize(x10aux::deserialization_buffer &buf) {
        au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__4* storage = x10aux::alloc<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__4>();
        buf.record_reference(x10aux::ref<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__4>(storage));
        x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > that_atoms = buf.read<x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > >();
        x10aux::ref<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__4> this_ = new (storage) au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__4(that_atoms);
        return this_;
    }
    
    au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__4(x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > atoms) : atoms(atoms) { }
    
    static const x10aux::serialization_id_t _serialization_id;
    
    static const x10aux::RuntimeType* getRTT() { return x10aux::getRTT<x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > >(); }
    virtual const x10aux::RuntimeType *_type() const { return x10aux::getRTT<x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > >(); }
    
    x10aux::ref<x10::lang::String> toString() {
        return x10aux::string_utils::lit(this->toNativeString());
    }
    
    const char* toNativeString() {
        return "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10:45-46";
    }

};

#endif // AU_EDU_ANU_CHEM_MM_ELECTROSTATICDIRECTMETHOD__CLOSURE__4_CLOSURE
#ifndef AU_EDU_ANU_CHEM_MM_ELECTROSTATICDIRECTMETHOD__CLOSURE__6_CLOSURE
#define AU_EDU_ANU_CHEM_MM_ELECTROSTATICDIRECTMETHOD__CLOSURE__6_CLOSURE
#include <x10/lang/Closure.h>
#include <x10/lang/Fun_0_1.h>
class au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__6 : public x10::lang::Closure {
    public:
    
    static x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > >::itable<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__6> _itable;
    static x10aux::itable_entry _itables[2];
    
    virtual x10aux::itable_entry* _getITables() { return _itables; }
    
    // closure body
    x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > __apply(x10aux::ref<x10::array::Point> id3) {
        
        //#line 48 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
        x10_int p = x10aux::nullCheck(id3)->x10::array::Point::__apply(((x10_int)0));
        
        //#line 48 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10Return_c
        return (__extension__ ({
            
            //#line 48 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > alloc25971 =
              
            x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > >((new (memset(x10aux::alloc<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > >(), 0, sizeof(x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > >))) x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > >()))
            ;
            
            //#line 48 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.StmtExpr_c
            (__extension__ ({
                
                //#line 243 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_int size31506 = x10aux::num_hosts;
                {
                    
                    //#line 243 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                    (alloc25971)->::x10::lang::Object::_constructor();
                    
                    //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10aux::ref<x10::array::Region> myReg31507 = x10aux::class_cast<x10aux::ref<x10::array::Region> >((__extension__ ({
                        
                        //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10aux::ref<x10::array::RectRegion1D> alloc1996031508 =
                          
                        x10aux::ref<x10::array::RectRegion1D>((new (memset(x10aux::alloc<x10::array::RectRegion1D>(), 0, sizeof(x10::array::RectRegion1D))) x10::array::RectRegion1D()))
                        ;
                        
                        //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                        (alloc1996031508)->::x10::array::RectRegion1D::_constructor(
                          ((x10_int)0),
                          ((x10_int) ((size31506) - (((x10_int)1)))));
                        alloc1996031508;
                    }))
                    );
                    
                    //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                    x10aux::nullCheck(alloc25971)->FMGL(region) =
                      myReg31507;
                    
                    //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                    x10aux::nullCheck(alloc25971)->FMGL(rank) =
                      ((x10_int)1);
                    
                    //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                    x10aux::nullCheck(alloc25971)->FMGL(rect) =
                      true;
                    
                    //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                    x10aux::nullCheck(alloc25971)->FMGL(zeroBased) =
                      true;
                    
                    //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                    x10aux::nullCheck(alloc25971)->FMGL(rail) =
                      true;
                    
                    //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                    x10aux::nullCheck(alloc25971)->FMGL(size) =
                      size31506;
                    
                    //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                    x10aux::nullCheck(alloc25971)->FMGL(layout) =
                      (__extension__ ({
                        
                        //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::array::RectLayout alloc1996131509 =
                          
                        x10::array::RectLayout::_alloc();
                        
                        //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.StmtExpr_c
                        (__extension__ ({
                            
                            //#line 97 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                            x10_int _max031513 =
                              ((x10_int) ((size31506) - (((x10_int)1))));
                            {
                                
                                //#line 98 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996131509->
                                  FMGL(rank) =
                                  ((x10_int)1);
                                
                                //#line 99 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996131509->
                                  FMGL(min0) =
                                  ((x10_int)0);
                                
                                //#line 100 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996131509->
                                  FMGL(delta0) =
                                  ((x10_int) ((((x10_int) ((_max031513) - (((x10_int)0))))) + (((x10_int)1))));
                                
                                //#line 101 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996131509->
                                  FMGL(size) =
                                  ((alloc1996131509->
                                      FMGL(delta0)) > (((x10_int)0)))
                                  ? (alloc1996131509->
                                       FMGL(delta0))
                                  : (((x10_int)0));
                                
                                //#line 103 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996131509->
                                  FMGL(min1) =
                                  ((x10_int)0);
                                
                                //#line 103 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996131509->
                                  FMGL(delta1) =
                                  ((x10_int)0);
                                
                                //#line 104 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996131509->
                                  FMGL(min2) =
                                  ((x10_int)0);
                                
                                //#line 104 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996131509->
                                  FMGL(delta2) =
                                  ((x10_int)0);
                                
                                //#line 105 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996131509->
                                  FMGL(min3) =
                                  ((x10_int)0);
                                
                                //#line 105 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996131509->
                                  FMGL(delta3) =
                                  ((x10_int)0);
                                
                                //#line 106 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996131509->
                                  FMGL(min) =
                                  X10_NULL;
                                
                                //#line 106 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc1996131509->
                                  FMGL(delta) =
                                  X10_NULL;
                            }
                            
                        }))
                        ;
                        alloc1996131509;
                    }))
                    ;
                    
                    //#line 250 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10_int n31510 = (__extension__ ({
                        
                        //#line 250 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::array::RectLayout this31515 =
                          x10aux::nullCheck(alloc25971)->
                            FMGL(layout);
                        this31515->FMGL(size);
                    }))
                    ;
                    
                    //#line 251 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                    x10aux::nullCheck(alloc25971)->FMGL(raw) =
                      x10::util::IndexedMemoryChunk<void>::allocate<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > >(n31510, 8, false, true);
                }
                
            }))
            ;
            alloc25971;
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
        au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__6* storage = x10aux::alloc<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__6>();
        buf.record_reference(x10aux::ref<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__6>(storage));
        x10aux::ref<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__6> this_ = new (storage) au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__6();
        return this_;
    }
    
    au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__6() { }
    
    static const x10aux::serialization_id_t _serialization_id;
    
    static const x10aux::RuntimeType* getRTT() { return x10aux::getRTT<x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > > >(); }
    virtual const x10aux::RuntimeType *_type() const { return x10aux::getRTT<x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > > >(); }
    
    x10aux::ref<x10::lang::String> toString() {
        return x10aux::string_utils::lit(this->toNativeString());
    }
    
    const char* toNativeString() {
        return "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10:48";
    }

};

#endif // AU_EDU_ANU_CHEM_MM_ELECTROSTATICDIRECTMETHOD__CLOSURE__6_CLOSURE
#ifndef AU_EDU_ANU_CHEM_MM_ELECTROSTATICDIRECTMETHOD__CLOSURE__9_CLOSURE
#define AU_EDU_ANU_CHEM_MM_ELECTROSTATICDIRECTMETHOD__CLOSURE__9_CLOSURE
#include <x10/lang/Closure.h>
#include <x10/lang/VoidFun_0_0.h>
class au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__9 : public x10::lang::Closure {
    public:
    
    static x10::lang::VoidFun_0_0::itable<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__9> _itable;
    static x10aux::itable_entry _itables[2];
    
    virtual x10aux::itable_entry* _getITables() { return _itables; }
    
    // closure body
    void __apply() {
        {
            
            //#line 68 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::lang::Throwable> throwable32079 = X10_NULL;
            
            //#line 68 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": polyglot.ast.Try_c
            try {
                {
                    
                    //#line 68 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10Call_c
                    x10::lang::Runtime::enterAtomic();
                    {
                        
                        //#line 69 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.StmtExpr_c
                        (__extension__ ({
                            
                            //#line 69 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
                            x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > this31812 =
                              x10aux::nullCheck(otherAtoms)->x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > >::__apply(
                                nextPlace->
                                  FMGL(id));
                            
                            //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i031810 = p1;
                            
                            //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > v31811 =
                              myAtoms;
                            
                            //#line 508 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > ret31813;
                            {
                                
                                //#line 512 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                                (x10aux::nullCheck(this31812)->
                                   FMGL(raw))->__set(i031810, v31811);
                                
                                //#line 519 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                ret31813 = v31811;
                            }
                            ret31813;
                        }))
                        ;
                    }
                }
                
                //#line 68 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10Call_c
                x10::compiler::Finalization::plausibleThrow();
            }
            catch (x10aux::__ref& __ref__64) {
                x10aux::ref<x10::lang::Throwable>& __exc__ref__64 = (x10aux::ref<x10::lang::Throwable>&)__ref__64;
                if (true) {
                    x10aux::ref<x10::lang::Throwable> formal32080 =
                      static_cast<x10aux::ref<x10::lang::Throwable> >(__exc__ref__64);
                    {
                        
                        //#line 68 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalAssign_c
                        throwable32079 =
                          formal32080;
                    }
                } else
                throw;
            }
            
            //#line 68 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10If_c
            if ((!x10aux::struct_equals(X10_NULL,
                                        throwable32079)))
            {
                
                //#line 68 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10If_c
                if (x10aux::instanceof<x10aux::ref<x10::compiler::Abort> >(throwable32079))
                {
                    
                    //#line 68 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": polyglot.ast.Throw_c
                    x10aux::throwException(x10aux::nullCheck(throwable32079));
                }
                
            }
            
            //#line 68 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10If_c
            if (true) {
                
                //#line 68 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10Call_c
                x10::lang::Runtime::exitAtomic();
            }
            
            //#line 68 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10If_c
            if ((!x10aux::struct_equals(X10_NULL,
                                        throwable32079)))
            {
                
                //#line 68 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10If_c
                if (!(x10aux::instanceof<x10aux::ref<x10::compiler::Finalization> >(throwable32079)))
                {
                    
                    //#line 68 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": polyglot.ast.Throw_c
                    x10aux::throwException(x10aux::nullCheck(throwable32079));
                }
                
            }
            
        }
    }
    
    // captured environment
    x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > > > otherAtoms;
    x10::lang::Place nextPlace;
    x10_int p1;
    x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > myAtoms;
    
    x10aux::serialization_id_t _get_serialization_id() {
        return _serialization_id;
    }
    
    void _serialize_body(x10aux::serialization_buffer &buf) {
        buf.write(this->otherAtoms);
        buf.write(this->nextPlace);
        buf.write(this->p1);
        buf.write(this->myAtoms);
    }
    
    template<class __T> static x10aux::ref<__T> _deserialize(x10aux::deserialization_buffer &buf) {
        au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__9* storage = x10aux::alloc<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__9>();
        buf.record_reference(x10aux::ref<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__9>(storage));
        x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > > > that_otherAtoms = buf.read<x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > > > >();
        x10::lang::Place that_nextPlace = buf.read<x10::lang::Place>();
        x10_int that_p1 = buf.read<x10_int>();
        x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > that_myAtoms = buf.read<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > >();
        x10aux::ref<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__9> this_ = new (storage) au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__9(that_otherAtoms, that_nextPlace, that_p1, that_myAtoms);
        return this_;
    }
    
    au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__9(x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > > > otherAtoms, x10::lang::Place nextPlace, x10_int p1, x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > myAtoms) : otherAtoms(otherAtoms), nextPlace(nextPlace), p1(p1), myAtoms(myAtoms) { }
    
    static const x10aux::serialization_id_t _serialization_id;
    
    static const x10aux::RuntimeType* getRTT() { return x10aux::getRTT<x10::lang::VoidFun_0_0>(); }
    virtual const x10aux::RuntimeType *_type() const { return x10aux::getRTT<x10::lang::VoidFun_0_0>(); }
    
    x10aux::ref<x10::lang::String> toString() {
        return x10aux::string_utils::lit(this->toNativeString());
    }
    
    const char* toNativeString() {
        return "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10:67-71";
    }

};

#endif // AU_EDU_ANU_CHEM_MM_ELECTROSTATICDIRECTMETHOD__CLOSURE__9_CLOSURE
#ifndef AU_EDU_ANU_CHEM_MM_ELECTROSTATICDIRECTMETHOD__CLOSURE__10_CLOSURE
#define AU_EDU_ANU_CHEM_MM_ELECTROSTATICDIRECTMETHOD__CLOSURE__10_CLOSURE
#include <x10/lang/Closure.h>
#include <x10/lang/VoidFun_0_0.h>
class au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__10 : public x10::lang::Closure {
    public:
    
    static x10::lang::VoidFun_0_0::itable<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__10> _itable;
    static x10aux::itable_entry _itables[2];
    
    virtual x10aux::itable_entry* _getITables() { return _itables; }
    
    // closure body
    void __apply() {
        {
            
            //#line 90 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::lang::Throwable> throwable32082 = X10_NULL;
            
            //#line 90 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": polyglot.ast.Try_c
            try {
                {
                    
                    //#line 90 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10Call_c
                    x10::lang::Runtime::enterAtomic();
                    {
                        
                        //#line 91 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.StmtExpr_c
                        (__extension__ ({
                            
                            //#line 91 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
                            x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > this31847 =
                              x10aux::nullCheck(otherAtoms)->x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > >::__apply(
                                targetPlace->
                                  FMGL(id));
                            
                            //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i031845 = p1;
                            
                            //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > v31846 =
                              myAtoms;
                            
                            //#line 508 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > ret31848;
                            {
                                
                                //#line 512 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                                (x10aux::nullCheck(this31847)->
                                   FMGL(raw))->__set(i031845, v31846);
                                
                                //#line 519 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                ret31848 = v31846;
                            }
                            ret31848;
                        }))
                        ;
                    }
                }
                
                //#line 90 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10Call_c
                x10::compiler::Finalization::plausibleThrow();
            }
            catch (x10aux::__ref& __ref__65) {
                x10aux::ref<x10::lang::Throwable>& __exc__ref__65 = (x10aux::ref<x10::lang::Throwable>&)__ref__65;
                if (true) {
                    x10aux::ref<x10::lang::Throwable> formal32083 =
                      static_cast<x10aux::ref<x10::lang::Throwable> >(__exc__ref__65);
                    {
                        
                        //#line 90 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalAssign_c
                        throwable32082 =
                          formal32083;
                    }
                } else
                throw;
            }
            
            //#line 90 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10If_c
            if ((!x10aux::struct_equals(X10_NULL,
                                        throwable32082)))
            {
                
                //#line 90 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10If_c
                if (x10aux::instanceof<x10aux::ref<x10::compiler::Abort> >(throwable32082))
                {
                    
                    //#line 90 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": polyglot.ast.Throw_c
                    x10aux::throwException(x10aux::nullCheck(throwable32082));
                }
                
            }
            
            //#line 90 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10If_c
            if (true) {
                
                //#line 90 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10Call_c
                x10::lang::Runtime::exitAtomic();
            }
            
            //#line 90 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10If_c
            if ((!x10aux::struct_equals(X10_NULL,
                                        throwable32082)))
            {
                
                //#line 90 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10If_c
                if (!(x10aux::instanceof<x10aux::ref<x10::compiler::Finalization> >(throwable32082)))
                {
                    
                    //#line 90 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": polyglot.ast.Throw_c
                    x10aux::throwException(x10aux::nullCheck(throwable32082));
                }
                
            }
            
        }
    }
    
    // captured environment
    x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > > > otherAtoms;
    x10::lang::Place targetPlace;
    x10_int p1;
    x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > myAtoms;
    
    x10aux::serialization_id_t _get_serialization_id() {
        return _serialization_id;
    }
    
    void _serialize_body(x10aux::serialization_buffer &buf) {
        buf.write(this->otherAtoms);
        buf.write(this->targetPlace);
        buf.write(this->p1);
        buf.write(this->myAtoms);
    }
    
    template<class __T> static x10aux::ref<__T> _deserialize(x10aux::deserialization_buffer &buf) {
        au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__10* storage = x10aux::alloc<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__10>();
        buf.record_reference(x10aux::ref<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__10>(storage));
        x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > > > that_otherAtoms = buf.read<x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > > > >();
        x10::lang::Place that_targetPlace = buf.read<x10::lang::Place>();
        x10_int that_p1 = buf.read<x10_int>();
        x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > that_myAtoms = buf.read<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > >();
        x10aux::ref<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__10> this_ = new (storage) au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__10(that_otherAtoms, that_targetPlace, that_p1, that_myAtoms);
        return this_;
    }
    
    au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__10(x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > > > otherAtoms, x10::lang::Place targetPlace, x10_int p1, x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > myAtoms) : otherAtoms(otherAtoms), targetPlace(targetPlace), p1(p1), myAtoms(myAtoms) { }
    
    static const x10aux::serialization_id_t _serialization_id;
    
    static const x10aux::RuntimeType* getRTT() { return x10aux::getRTT<x10::lang::VoidFun_0_0>(); }
    virtual const x10aux::RuntimeType *_type() const { return x10aux::getRTT<x10::lang::VoidFun_0_0>(); }
    
    x10aux::ref<x10::lang::String> toString() {
        return x10aux::string_utils::lit(this->toNativeString());
    }
    
    const char* toNativeString() {
        return "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10:89-93";
    }

};

#endif // AU_EDU_ANU_CHEM_MM_ELECTROSTATICDIRECTMETHOD__CLOSURE__10_CLOSURE
#ifndef AU_EDU_ANU_CHEM_MM_ELECTROSTATICDIRECTMETHOD__CLOSURE__8_CLOSURE
#define AU_EDU_ANU_CHEM_MM_ELECTROSTATICDIRECTMETHOD__CLOSURE__8_CLOSURE
#include <x10/lang/Closure.h>
#include <x10/lang/VoidFun_0_0.h>
class au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__8 : public x10::lang::Closure {
    public:
    
    static x10::lang::VoidFun_0_0::itable<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__8> _itable;
    static x10aux::itable_entry _itables[2];
    
    virtual x10aux::itable_entry* _getITables() { return _itables; }
    
    // closure body
    void __apply() {
        
        //#line 61 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > myAtoms =
          x10aux::nullCheck(atoms)->x10::array::DistArray<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > >::__apply(
            p1);
        
        //#line 62 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
        x10_double energyThisPlace = 0.0;
        
        //#line 65 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
        x10::lang::Place nextPlace = (__extension__ ({
            
            //#line 65 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
            x10::lang::Place this31809 = x10::lang::Place::_make(x10aux::here);
            x10aux::nullCheck(this31809)->x10::lang::Place::next(
              ((x10_int)1));
        }))
        ;
        
        //#line 66 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10If_c
        if ((!x10aux::struct_equals(nextPlace, x10::lang::Place::_make(x10aux::here))))
        {
            
            //#line 67 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10Call_c
            x10::lang::Runtime::runUncountedAsync(
              nextPlace,
              x10aux::ref<x10::lang::VoidFun_0_0>(x10aux::ref<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__9>(new (x10aux::alloc<x10::lang::VoidFun_0_0>(sizeof(au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__9)))au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__9(otherAtoms, nextPlace, p1, myAtoms))));
        }
        
        //#line 75 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
        x10_int i25991max2599331996 = ((x10_int) ((x10aux::nullCheck(myAtoms)->
                                                     FMGL(size)) - (((x10_int)1))));
        
        //#line 75 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": polyglot.ast.For_c
        {
            x10_int i2599131997;
            for (
                 //#line 75 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
                 i2599131997 = ((x10_int)0); ((i2599131997) <= (i25991max2599331996));
                 
                 //#line 75 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalAssign_c
                 i2599131997 =
                   ((x10_int) ((i2599131997) + (((x10_int)1)))))
            {
                
                //#line 75 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
                x10_int i31998 =
                  i2599131997;
                
                //#line 76 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
                au::edu::anu::chem::PointCharge atomI31968 =
                  (__extension__ ({
                    
                    //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10_int i03182031969 =
                      i31998;
                    
                    //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    au::edu::anu::chem::PointCharge ret3182131970;
                    
                    //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                    goto __ret3182231971; __ret3182231971: {
                    {
                        
                        //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                        ret3182131970 =
                          (x10aux::nullCheck(myAtoms)->
                             FMGL(raw))->__apply(i03182031969);
                        
                        //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                        goto __ret3182231971_end_;
                    }goto __ret3182231971_end_; __ret3182231971_end_: ;
                    }
                    ret3182131970;
                    }))
                    ;
                    
                
                //#line 77 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
                x10_int i25975max2597731965 =
                  ((x10_int) ((i31998) - (((x10_int)1))));
                
                //#line 77 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": polyglot.ast.For_c
                {
                    x10_int i2597531966;
                    for (
                         //#line 77 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
                         i2597531966 =
                           ((x10_int)0);
                         ((i2597531966) <= (i25975max2597731965));
                         
                         //#line 77 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalAssign_c
                         i2597531966 =
                           ((x10_int) ((i2597531966) + (((x10_int)1)))))
                    {
                        
                        //#line 77 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
                        x10_int j31967 =
                          i2597531966;
                        
                        //#line 78 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
                        au::edu::anu::chem::PointCharge atomJ31953 =
                          (__extension__ ({
                            
                            //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i03182831954 =
                              j31967;
                            
                            //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            au::edu::anu::chem::PointCharge ret3182931955;
                            
                            //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                            goto __ret3183031956; __ret3183031956: {
                            {
                                
                                //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                ret3182931955 =
                                  (x10aux::nullCheck(myAtoms)->
                                     FMGL(raw))->__apply(i03182831954);
                                
                                //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                goto __ret3183031956_end_;
                            }goto __ret3183031956_end_; __ret3183031956_end_: ;
                            }
                            ret3182931955;
                            }))
                            ;
                            
                        
                        //#line 79 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalAssign_c
                        energyThisPlace =
                          ((energyThisPlace) + (((((((2.0) * (atomI31968->
                                                                FMGL(charge)))) * (atomJ31953->
                                                                                     FMGL(charge)))) / ((__extension__ ({
                            
                            //#line 79 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
                            x10x::vector::Point3d this3183731957 =
                              atomJ31953->
                                FMGL(centre);
                            
                            //#line 66 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                            x10x::vector::Point3d b3183631958 =
                              atomI31968->
                                FMGL(centre);
                            x10aux::math_utils::sqrt((__extension__ ({
                                
                                //#line 57 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                                x10x::vector::Point3d b3183831959 =
                                  b3183631958;
                                
                                //#line 57 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                                x10_double ret3184231960;
                                {
                                    
                                    //#line 58 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                                    x10_double di3183931961 =
                                      ((this3183731957->
                                          FMGL(i)) - (b3183831959->
                                                        FMGL(i)));
                                    
                                    //#line 59 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                                    x10_double dj3184031962 =
                                      ((this3183731957->
                                          FMGL(j)) - (b3183831959->
                                                        FMGL(j)));
                                    
                                    //#line 60 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                                    x10_double dk3184131963 =
                                      ((this3183731957->
                                          FMGL(k)) - (b3183831959->
                                                        FMGL(k)));
                                    
                                    //#line 61 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalAssign_c
                                    ret3184231960 =
                                      ((((((di3183931961) * (di3183931961))) + (((dj3184031962) * (dj3184031962))))) + (((dk3184131963) * (dk3184131963))));
                                }
                                ret3184231960;
                            }))
                            );
                        }))
                        ))));
                        }
                    }
                    
                }
                }
                
            
            //#line 83 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
            x10::lang::Place target =
              (__extension__ ({
                x10aux::nullCheck(nextPlace)->x10::lang::Place::next(
                  ((x10_int)1));
            }))
            ;
            
            //#line 84 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
            x10::lang::Place source =
              (__extension__ ({
                
                //#line 84 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
                x10::lang::Place this31844 =
                  x10::lang::Place::_make(x10aux::here);
                x10aux::nullCheck(this31844)->x10::lang::Place::next(
                  ((x10_int)-1));
            }))
            ;
            
            //#line 85 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10While_c
            while ((!x10aux::struct_equals(source,
                                           x10::lang::Place::_make(x10aux::here))))
            {
                
                //#line 86 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10If_c
                if ((!x10aux::struct_equals(target,
                                            x10::lang::Place::_make(x10aux::here))))
                {
                    
                    //#line 88 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
                    x10::lang::Place targetPlace =
                      target;
                    
                    //#line 89 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10Call_c
                    x10::lang::Runtime::runUncountedAsync(
                      targetPlace,
                      x10aux::ref<x10::lang::VoidFun_0_0>(x10aux::ref<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__10>(new (x10aux::alloc<x10::lang::VoidFun_0_0>(sizeof(au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__10)))au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__10(otherAtoms, targetPlace, p1, myAtoms))));
                }
                {
                    
                    //#line 97 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10Call_c
                    x10::lang::Runtime::ensureNotInAtomic();
                    {
                        
                        //#line 97 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
                        x10aux::ref<x10::lang::Throwable> throwable32085 =
                          X10_NULL;
                        
                        //#line 97 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": polyglot.ast.Try_c
                        try {
                            {
                                
                                //#line 97 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10Call_c
                                x10::lang::Runtime::enterAtomic();
                                
                                //#line 97 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10While_c
                                while (true)
                                {
                                    
                                    //#line 97 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10If_c
                                    if ((!x10aux::struct_equals(x10aux::nullCheck(x10aux::nullCheck(otherAtoms)->x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > >::__apply(
                                                                                    x10::lang::Place::_make(x10aux::here)->
                                                                                      FMGL(id)))->x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > >::__apply(
                                                                  source->
                                                                    FMGL(id)),
                                                                X10_NULL)))
                                    {
                                        {
                                         
                                        }
                                        
                                        //#line 97 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": polyglot.ast.Branch_c
                                        break;
                                    }
                                    
                                    //#line 97 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10Call_c
                                    x10::lang::Runtime::awaitAtomic();
                                }
                                
                            }
                            
                            //#line 97 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10Call_c
                            x10::compiler::Finalization::plausibleThrow();
                        }
                        catch (x10aux::__ref& __ref__66) {
                            x10aux::ref<x10::lang::Throwable>& __exc__ref__66 = (x10aux::ref<x10::lang::Throwable>&)__ref__66;
                            if (true) {
                                x10aux::ref<x10::lang::Throwable> formal32086 =
                                  static_cast<x10aux::ref<x10::lang::Throwable> >(__exc__ref__66);
                                {
                                    
                                    //#line 97 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalAssign_c
                                    throwable32085 =
                                      formal32086;
                                }
                            } else
                            throw;
                        }
                        
                        //#line 97 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10If_c
                        if ((!x10aux::struct_equals(X10_NULL,
                                                    throwable32085)))
                        {
                            
                            //#line 97 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10If_c
                            if (x10aux::instanceof<x10aux::ref<x10::compiler::Abort> >(throwable32085))
                            {
                                
                                //#line 97 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": polyglot.ast.Throw_c
                                x10aux::throwException(x10aux::nullCheck(throwable32085));
                            }
                            
                        }
                        
                        //#line 97 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10If_c
                        if (true)
                        {
                            
                            //#line 97 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10Call_c
                            x10::lang::Runtime::exitAtomic();
                        }
                        
                        //#line 97 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10If_c
                        if ((!x10aux::struct_equals(X10_NULL,
                                                    throwable32085)))
                        {
                            
                            //#line 97 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10If_c
                            if (!(x10aux::instanceof<x10aux::ref<x10::compiler::Finalization> >(throwable32085)))
                            {
                                
                                //#line 97 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": polyglot.ast.Throw_c
                                x10aux::throwException(x10aux::nullCheck(throwable32085));
                            }
                            
                        }
                        
                    }
                }
                
                //#line 100 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > other =
                  (__extension__ ({
                    
                    //#line 100 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
                    x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > this31856 =
                      x10aux::nullCheck(otherAtoms)->x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > >::__apply(
                        x10::lang::Place::_make(x10aux::here)->
                          FMGL(id));
                    
                    //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10_int i031855 =
                      source->
                        FMGL(id);
                    
                    //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > ret31857;
                    
                    //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                    goto __ret31858; __ret31858: {
                    {
                        
                        //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                        ret31857 =
                          (x10aux::nullCheck(this31856)->
                             FMGL(raw))->__apply(i031855);
                        
                        //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                        goto __ret31858_end_;
                    }goto __ret31858_end_; __ret31858_end_: ;
                    }
                    ret31857;
                    }))
                    ;
                    
                
                //#line 101 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
                x10_int i26023max2602531992 =
                  ((x10_int) ((x10aux::nullCheck(other)->
                                 FMGL(size)) - (((x10_int)1))));
                
                //#line 101 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": polyglot.ast.For_c
                {
                    x10_int i2602331993;
                    for (
                         //#line 101 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
                         i2602331993 =
                           ((x10_int)0);
                         ((i2602331993) <= (i26023max2602531992));
                         
                         //#line 101 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalAssign_c
                         i2602331993 =
                           ((x10_int) ((i2602331993) + (((x10_int)1)))))
                    {
                        
                        //#line 101 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
                        x10_int j31994 =
                          i2602331993;
                        
                        //#line 102 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
                        au::edu::anu::chem::PointCharge atomJ31987 =
                          (__extension__ ({
                            
                            //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i03186431988 =
                              j31994;
                            
                            //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            au::edu::anu::chem::PointCharge ret3186531989;
                            
                            //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                            goto __ret3186631990; __ret3186631990: {
                            {
                                
                                //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                ret3186531989 =
                                  (x10aux::nullCheck(other)->
                                     FMGL(raw))->__apply(i03186431988);
                                
                                //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                goto __ret3186631990_end_;
                            }goto __ret3186631990_end_; __ret3186631990_end_: ;
                            }
                            ret3186531989;
                            }))
                            ;
                            
                        
                        //#line 103 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
                        x10_int i26007max2600931984 =
                          ((x10_int) ((x10aux::nullCheck(myAtoms)->
                                         FMGL(size)) - (((x10_int)1))));
                        
                        //#line 103 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": polyglot.ast.For_c
                        {
                            x10_int i2600731985;
                            for (
                                 //#line 103 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
                                 i2600731985 =
                                   ((x10_int)0);
                                 ((i2600731985) <= (i26007max2600931984));
                                 
                                 //#line 103 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalAssign_c
                                 i2600731985 =
                                   ((x10_int) ((i2600731985) + (((x10_int)1)))))
                            {
                                
                                //#line 103 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
                                x10_int i31986 =
                                  i2600731985;
                                
                                //#line 104 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
                                au::edu::anu::chem::PointCharge atomI31972 =
                                  (__extension__ ({
                                    
                                    //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                    x10_int i03187231973 =
                                      i31986;
                                    
                                    //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                    au::edu::anu::chem::PointCharge ret3187331974;
                                    
                                    //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                    goto __ret3187431975; __ret3187431975: {
                                    {
                                        
                                        //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                        ret3187331974 =
                                          (x10aux::nullCheck(myAtoms)->
                                             FMGL(raw))->__apply(i03187231973);
                                        
                                        //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                        goto __ret3187431975_end_;
                                    }goto __ret3187431975_end_; __ret3187431975_end_: ;
                                    }
                                    ret3187331974;
                                    }))
                                    ;
                                    
                                
                                //#line 105 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalAssign_c
                                energyThisPlace =
                                  ((energyThisPlace) + (((((atomI31972->
                                                              FMGL(charge)) * (atomJ31987->
                                                                                 FMGL(charge)))) / ((__extension__ ({
                                    
                                    //#line 105 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
                                    x10x::vector::Point3d this3188131976 =
                                      atomJ31987->
                                        FMGL(centre);
                                    
                                    //#line 66 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                                    x10x::vector::Point3d b3188031977 =
                                      atomI31972->
                                        FMGL(centre);
                                    x10aux::math_utils::sqrt((__extension__ ({
                                        
                                        //#line 57 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                                        x10x::vector::Point3d b3188231978 =
                                          b3188031977;
                                        
                                        //#line 57 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                                        x10_double ret3188631979;
                                        {
                                            
                                            //#line 58 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                                            x10_double di3188331980 =
                                              ((this3188131976->
                                                  FMGL(i)) - (b3188231978->
                                                                FMGL(i)));
                                            
                                            //#line 59 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                                            x10_double dj3188431981 =
                                              ((this3188131976->
                                                  FMGL(j)) - (b3188231978->
                                                                FMGL(j)));
                                            
                                            //#line 60 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                                            x10_double dk3188531982 =
                                              ((this3188131976->
                                                  FMGL(k)) - (b3188231978->
                                                                FMGL(k)));
                                            
                                            //#line 61 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalAssign_c
                                            ret3188631979 =
                                              ((((((di3188331980) * (di3188331980))) + (((dj3188431981) * (dj3188431981))))) + (((dk3188531982) * (dk3188531982))));
                                        }
                                        ret3188631979;
                                    }))
                                    );
                                }))
                                ))));
                                }
                            }
                            
                        }
                        }
                        
                    
                    //#line 108 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalAssign_c
                    target =
                      (__extension__ ({
                        x10aux::nullCheck(target)->x10::lang::Place::next(
                          ((x10_int)1));
                    }))
                    ;
                    
                    //#line 109 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalAssign_c
                    source =
                      (__extension__ ({
                        x10aux::nullCheck(source)->x10::lang::Place::next(
                          ((x10_int)-1));
                    }))
                    ;
                }
                
                //#line 112 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10Call_c
                x10::lang::Runtime::makeOffer<x10_double >(
                  energyThisPlace);
                }
                
                // captured environment
                x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > atoms;
                x10_int p1;
                x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > > > otherAtoms;
                
                x10aux::serialization_id_t _get_serialization_id() {
                    return _serialization_id;
                }
                
                void _serialize_body(x10aux::serialization_buffer &buf) {
                    buf.write(this->atoms);
                    buf.write(this->p1);
                    buf.write(this->otherAtoms);
                }
                
                template<class __T> static x10aux::ref<__T> _deserialize(x10aux::deserialization_buffer &buf) {
                    au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__8* storage = x10aux::alloc<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__8>();
                    buf.record_reference(x10aux::ref<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__8>(storage));
                    x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > that_atoms = buf.read<x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > >();
                    x10_int that_p1 = buf.read<x10_int>();
                    x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > > > that_otherAtoms = buf.read<x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > > > >();
                    x10aux::ref<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__8> this_ = new (storage) au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__8(that_atoms, that_p1, that_otherAtoms);
                    return this_;
                }
                
                au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__8(x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > atoms, x10_int p1, x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > > > otherAtoms) : atoms(atoms), p1(p1), otherAtoms(otherAtoms) { }
                
                static const x10aux::serialization_id_t _serialization_id;
                
                static const x10aux::RuntimeType* getRTT() { return x10aux::getRTT<x10::lang::VoidFun_0_0>(); }
                virtual const x10aux::RuntimeType *_type() const { return x10aux::getRTT<x10::lang::VoidFun_0_0>(); }
                
                x10aux::ref<x10::lang::String> toString() {
                    return x10aux::string_utils::lit(this->toNativeString());
                }
                
                const char* toNativeString() {
                    return "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10:60-113";
                }
            
            };
            
            #endif // AU_EDU_ANU_CHEM_MM_ELECTROSTATICDIRECTMETHOD__CLOSURE__8_CLOSURE
            #ifndef AU_EDU_ANU_CHEM_MM_ELECTROSTATICDIRECTMETHOD__CLOSURE__7_CLOSURE
#define AU_EDU_ANU_CHEM_MM_ELECTROSTATICDIRECTMETHOD__CLOSURE__7_CLOSURE
#include <x10/lang/Closure.h>
#include <x10/lang/VoidFun_0_0.h>
class au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__7 : public x10::lang::Closure {
    public:
    
    static x10::lang::VoidFun_0_0::itable<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__7> _itable;
    static x10aux::itable_entry _itables[2];
    
    virtual x10aux::itable_entry* _getITables() { return _itables; }
    
    // closure body
    void __apply() {
        
        //#line 60 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": polyglot.ast.For_c
        {
            x10aux::ref<x10::lang::Iterator<x10aux::ref<x10::array::Point> > > id32066;
            for (
                 //#line 60 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
                 id32066 = x10aux::nullCheck(x10aux::nullCheck(x10aux::nullCheck(__lowerer__var__0__)->restriction(
                                                                 x10::lang::Place::_make(x10aux::here)))->
                                               FMGL(region))->iterator();
                 x10::lang::Iterator<x10aux::ref<x10::array::Point> >::hasNext(x10aux::nullCheck(id32066));
                 ) {
                
                //#line 60 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<x10::array::Point> id4 =
                  x10::lang::Iterator<x10aux::ref<x10::array::Point> >::next(x10aux::nullCheck(id32066));
                
                //#line 60 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
                x10_int p1 = x10aux::nullCheck(id4)->x10::array::Point::__apply(
                               ((x10_int)0));
                
                //#line 60 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10Call_c
                x10::lang::Runtime::runAsync(x10::lang::Place::_make(x10aux::here),
                                             x10aux::ref<x10::lang::VoidFun_0_0>(x10aux::ref<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__8>(new (x10aux::alloc<x10::lang::VoidFun_0_0>(sizeof(au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__8)))au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__8(atoms, p1, otherAtoms))));
            }
        }
        
    }
    
    // captured environment
    x10aux::ref<x10::array::Dist> __lowerer__var__0__;
    x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > atoms;
    x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > > > otherAtoms;
    
    x10aux::serialization_id_t _get_serialization_id() {
        return _serialization_id;
    }
    
    void _serialize_body(x10aux::serialization_buffer &buf) {
        buf.write(this->__lowerer__var__0__);
        buf.write(this->atoms);
        buf.write(this->otherAtoms);
    }
    
    template<class __T> static x10aux::ref<__T> _deserialize(x10aux::deserialization_buffer &buf) {
        au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__7* storage = x10aux::alloc<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__7>();
        buf.record_reference(x10aux::ref<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__7>(storage));
        x10aux::ref<x10::array::Dist> that___lowerer__var__0__ = buf.read<x10aux::ref<x10::array::Dist> >();
        x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > that_atoms = buf.read<x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > >();
        x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > > > that_otherAtoms = buf.read<x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > > > >();
        x10aux::ref<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__7> this_ = new (storage) au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__7(that___lowerer__var__0__, that_atoms, that_otherAtoms);
        return this_;
    }
    
    au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__7(x10aux::ref<x10::array::Dist> __lowerer__var__0__, x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > atoms, x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > > > otherAtoms) : __lowerer__var__0__(__lowerer__var__0__), atoms(atoms), otherAtoms(otherAtoms) { }
    
    static const x10aux::serialization_id_t _serialization_id;
    
    static const x10aux::RuntimeType* getRTT() { return x10aux::getRTT<x10::lang::VoidFun_0_0>(); }
    virtual const x10aux::RuntimeType *_type() const { return x10aux::getRTT<x10::lang::VoidFun_0_0>(); }
    
    x10aux::ref<x10::lang::String> toString() {
        return x10aux::string_utils::lit(this->toNativeString());
    }
    
    const char* toNativeString() {
        return "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10:60-113";
    }

};

#endif // AU_EDU_ANU_CHEM_MM_ELECTROSTATICDIRECTMETHOD__CLOSURE__7_CLOSURE

//#line 27 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10FieldDecl_c

//#line 27 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock> au::edu::anu::chem::mm::ElectrostaticDirectMethod::getOrderedLock(
  ) {
    
    //#line 27 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10Return_c
    return x10::util::concurrent::OrderedLock::getObjectLock(((x10aux::ref<au::edu::anu::chem::mm::ElectrostaticDirectMethod>)this)->
                                                               FMGL(X10__object_lock_id0));
    
}

//#line 27 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10FieldDecl_c
x10_int au::edu::anu::chem::mm::ElectrostaticDirectMethod::FMGL(X10__class_lock_id1);
void au::edu::anu::chem::mm::ElectrostaticDirectMethod::FMGL(X10__class_lock_id1__do_init)() {
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: au::edu::anu::chem::mm::ElectrostaticDirectMethod.X10$class_lock_id1");
    x10_int __var59__ = x10::util::concurrent::OrderedLock::createClassLock();
    FMGL(X10__class_lock_id1) = __var59__;
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
}
void au::edu::anu::chem::mm::ElectrostaticDirectMethod::FMGL(X10__class_lock_id1__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var60__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(X10__class_lock_id1__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var60__ != x10aux::UNINITIALIZED) goto WAIT;
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
                                                                       _SI_("WAITING for field: au::edu::anu::chem::mm::ElectrostaticDirectMethod.X10$class_lock_id1 to be initialized");
                                                                       while (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                                       _SI_("CONTINUING because field: au::edu::anu::chem::mm::ElectrostaticDirectMethod.X10$class_lock_id1 has been initialized");
                                                                       x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__61 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(au::edu::anu::chem::mm::ElectrostaticDirectMethod::FMGL(X10__class_lock_id1__init));

volatile x10aux::status au::edu::anu::chem::mm::ElectrostaticDirectMethod::FMGL(X10__class_lock_id1__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> au::edu::anu::chem::mm::ElectrostaticDirectMethod::FMGL(X10__class_lock_id1__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(X10__class_lock_id1) = buf.read<x10_int>();
    au::edu::anu::chem::mm::ElectrostaticDirectMethod::FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t au::edu::anu::chem::mm::ElectrostaticDirectMethod::FMGL(X10__class_lock_id1__id) =
  x10aux::StaticInitBroadcastDispatcher::addRoutine(au::edu::anu::chem::mm::ElectrostaticDirectMethod::FMGL(X10__class_lock_id1__deserialize));


//#line 27 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock>
  au::edu::anu::chem::mm::ElectrostaticDirectMethod::getStaticOrderedLock(
  ) {
    
    //#line 27 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 170 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/util/concurrent/OrderedLock.x10": x10.ast.X10LocalDecl_c
        x10_int lockId26110 = au::edu::anu::chem::mm::ElectrostaticDirectMethod::
                                FMGL(X10__class_lock_id1__get)();
        x10::util::Map<x10_int, x10aux::ref<x10::util::concurrent::OrderedLock> >::getOrThrow(x10aux::nullCheck(x10::util::concurrent::OrderedLock::
                                                                                                                  FMGL(lockMap__get)()), 
          lockId26110);
    }))
    ;
    
}

//#line 29 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10FieldDecl_c
x10_int au::edu::anu::chem::mm::ElectrostaticDirectMethod::FMGL(TIMER_INDEX_TOTAL) =
  ((x10_int)0);


//#line 31 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10FieldDecl_c
/** A multi-timer for the several segments of a single getEnergy invocation, indexed by the constants above. */
                                                                                                               //#line 33 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10FieldDecl_c
                                                                                                               
                                                                                                               //#line 36 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10FieldDecl_c
                                                                                                               /** The charges in the simulation, divided up into an array of ValRails, one for each place. */
                                                                                                                                                                                                              //#line 37 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10FieldDecl_c
                                                                                                                                                                                                              

//#line 43 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::chem::mm::ElectrostaticDirectMethod::_constructor(
  x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > atoms)
{
    
    //#line 43 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<x10::lang::Object>)this))->::x10::lang::Object::_constructor();
    
    //#line 43 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.AssignPropertyCall_c
    
    //#line 27 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10Call_c
    ((x10aux::ref<au::edu::anu::chem::mm::ElectrostaticDirectMethod>)this)->au::edu::anu::chem::mm::ElectrostaticDirectMethod::__fieldInitializers25763();
    
    //#line 44 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::mm::ElectrostaticDirectMethod>)this)->
      FMGL(atoms) =
      (__extension__ ({
        
        //#line 140 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::Dist> dist31453 =
          (__extension__ ({
            x10aux::class_cast<x10aux::ref<x10::array::Dist> >(x10::array::Dist::
                                                                 FMGL(UNIQUE__get)());
        }))
        ;
        
        //#line 140 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > init31454 =
          x10aux::class_cast_unchecked<x10aux::ref<x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > >(x10aux::ref<x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > >(x10aux::ref<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__1>(new (x10aux::alloc<x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > >(sizeof(au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__1)))au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__1(atoms))));
        (__extension__ ({
            
            //#line 140 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > alloc3048631455 =
              
            x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > >((new (memset(x10aux::alloc<x10::array::DistArray<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > >(), 0, sizeof(x10::array::DistArray<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > >))) x10::array::DistArray<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > >()))
            ;
            
            //#line 140 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10": x10.ast.X10ConstructorCall_c
            (alloc3048631455)->::x10::array::DistArray<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > >::_constructor(
              dist31453,
              init31454);
            alloc3048631455;
        }))
        ;
    }))
    ;
    
    //#line 47 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::mm::ElectrostaticDirectMethod>)this)->
      FMGL(otherAtoms) =
      (__extension__ ({
        
        //#line 140 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::Dist> dist31466 =
          (__extension__ ({
            x10aux::class_cast<x10aux::ref<x10::array::Dist> >(x10::array::Dist::
                                                                 FMGL(UNIQUE__get)());
        }))
        ;
        
        //#line 140 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > > > init31467 =
          x10aux::class_cast_unchecked<x10aux::ref<x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > > > >(x10aux::ref<x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > > >(x10aux::ref<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__3>(new (x10aux::alloc<x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > > >(sizeof(au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__3)))au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__3())));
        (__extension__ ({
            
            //#line 140 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > > > alloc3048631468 =
              
            x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > > >((new (memset(x10aux::alloc<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > > >(), 0, sizeof(x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > >))) x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > >()))
            ;
            
            //#line 140 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10": x10.ast.X10ConstructorCall_c
            (alloc3048631468)->::x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > >::_constructor(
              dist31466,
              init31467);
            alloc3048631468;
        }))
        ;
    }))
    ;
    
}
x10aux::ref<au::edu::anu::chem::mm::ElectrostaticDirectMethod> au::edu::anu::chem::mm::ElectrostaticDirectMethod::_make(
  x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > atoms)
{
    x10aux::ref<au::edu::anu::chem::mm::ElectrostaticDirectMethod> this_ = new (memset(x10aux::alloc<au::edu::anu::chem::mm::ElectrostaticDirectMethod>(), 0, sizeof(au::edu::anu::chem::mm::ElectrostaticDirectMethod))) au::edu::anu::chem::mm::ElectrostaticDirectMethod();
    this_->_constructor(atoms);
    return this_;
}



//#line 43 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::chem::mm::ElectrostaticDirectMethod::_constructor(
  x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > atoms,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    
    //#line 43 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<x10::lang::Object>)this))->::x10::lang::Object::_constructor();
    
    //#line 43 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.AssignPropertyCall_c
    
    //#line 27 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10Call_c
    ((x10aux::ref<au::edu::anu::chem::mm::ElectrostaticDirectMethod>)this)->au::edu::anu::chem::mm::ElectrostaticDirectMethod::__fieldInitializers25763();
    
    //#line 44 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::mm::ElectrostaticDirectMethod>)this)->
      FMGL(atoms) =
      (__extension__ ({
        
        //#line 140 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::Dist> dist31503 =
          (__extension__ ({
            x10aux::class_cast<x10aux::ref<x10::array::Dist> >(x10::array::Dist::
                                                                 FMGL(UNIQUE__get)());
        }))
        ;
        
        //#line 140 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > init31504 =
          x10aux::class_cast_unchecked<x10aux::ref<x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > >(x10aux::ref<x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > >(x10aux::ref<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__4>(new (x10aux::alloc<x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > >(sizeof(au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__4)))au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__4(atoms))));
        (__extension__ ({
            
            //#line 140 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > alloc3048631505 =
              
            x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > >((new (memset(x10aux::alloc<x10::array::DistArray<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > >(), 0, sizeof(x10::array::DistArray<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > >))) x10::array::DistArray<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > >()))
            ;
            
            //#line 140 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10": x10.ast.X10ConstructorCall_c
            (alloc3048631505)->::x10::array::DistArray<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > >::_constructor(
              dist31503,
              init31504);
            alloc3048631505;
        }))
        ;
    }))
    ;
    
    //#line 47 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::mm::ElectrostaticDirectMethod>)this)->
      FMGL(otherAtoms) =
      (__extension__ ({
        
        //#line 140 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::Dist> dist31516 =
          (__extension__ ({
            x10aux::class_cast<x10aux::ref<x10::array::Dist> >(x10::array::Dist::
                                                                 FMGL(UNIQUE__get)());
        }))
        ;
        
        //#line 140 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > > > init31517 =
          x10aux::class_cast_unchecked<x10aux::ref<x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > > > >(x10aux::ref<x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > > >(x10aux::ref<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__6>(new (x10aux::alloc<x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > > >(sizeof(au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__6)))au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__6())));
        (__extension__ ({
            
            //#line 140 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > > > alloc3048631518 =
              
            x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > > >((new (memset(x10aux::alloc<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > > >(), 0, sizeof(x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > >))) x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > >()))
            ;
            
            //#line 140 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10": x10.ast.X10ConstructorCall_c
            (alloc3048631518)->::x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > >::_constructor(
              dist31516,
              init31517);
            alloc3048631518;
        }))
        ;
    }))
    ;
    
    //#line 43 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::mm::ElectrostaticDirectMethod>)this)->
      FMGL(X10__object_lock_id0) =
      x10aux::nullCheck(paramLock)->getIndex();
    
}
x10aux::ref<au::edu::anu::chem::mm::ElectrostaticDirectMethod> au::edu::anu::chem::mm::ElectrostaticDirectMethod::_make(
  x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > atoms,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    x10aux::ref<au::edu::anu::chem::mm::ElectrostaticDirectMethod> this_ = new (memset(x10aux::alloc<au::edu::anu::chem::mm::ElectrostaticDirectMethod>(), 0, sizeof(au::edu::anu::chem::mm::ElectrostaticDirectMethod))) au::edu::anu::chem::mm::ElectrostaticDirectMethod();
    this_->_constructor(atoms,
    paramLock);
    return this_;
}



//#line 51 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10MethodDecl_c
x10_double au::edu::anu::chem::mm::ElectrostaticDirectMethod::getEnergy(
  ) {
    
    //#line 52 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10Call_c
    ((x10aux::ref<au::edu::anu::chem::mm::ElectrostaticDirectMethod>)this)->
      FMGL(timer)->au::edu::anu::util::Timer::start(
      ((x10_int)0));
    
    //#line 54 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
    x10_double directEnergy;
    {
        
        //#line 54 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::lang::FinishState> x10____var0 =
          x10aux::class_cast_unchecked<x10aux::ref<x10::lang::FinishState> >(x10::lang::Runtime::startCollectingFinish<x10_double >(
                                                                               x10aux::class_cast_unchecked<x10aux::ref<x10::lang::Reducible<x10_double> > >((__extension__ ({
                                                                                   
                                                                                   //#line 54 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
                                                                                   au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer alloc25972 =
                                                                                     
                                                                                   au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer::_alloc();
                                                                                   
                                                                                   //#line 54 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10ConstructorCall_c
                                                                                   (alloc25972)->::au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer::_constructor(
                                                                                     x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
                                                                                   alloc25972;
                                                                               }))
                                                                               )));
        {
            
            //#line 54 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::lang::Throwable> throwable32088 =
              X10_NULL;
            
            //#line 54 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": polyglot.ast.Try_c
            try {
                
                //#line 54 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": polyglot.ast.Try_c
                try {
                    {
                        
                        //#line 55 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
                        x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > atoms =
                          ((x10aux::ref<au::edu::anu::chem::mm::ElectrostaticDirectMethod>)this)->
                            FMGL(atoms);
                        
                        //#line 57 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
                        x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > > > otherAtoms =
                          ((x10aux::ref<au::edu::anu::chem::mm::ElectrostaticDirectMethod>)this)->
                            FMGL(otherAtoms);
                        {
                            {
                                
                                //#line 60 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10Call_c
                                x10::lang::Runtime::ensureNotInAtomic();
                                
                                //#line 60 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
                                x10aux::ref<x10::array::Dist> __lowerer__var__0__ =
                                  x10aux::nullCheck(atoms)->
                                    FMGL(dist);
                                
                                //#line 60 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": polyglot.ast.For_c
                                {
                                    x10aux::ref<x10::lang::Iterator<x10::lang::Place> > __lowerer__var__1__32074;
                                    for (
                                         //#line 60 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
                                         __lowerer__var__1__32074 =
                                           x10aux::nullCheck(x10aux::nullCheck(__lowerer__var__0__)->places())->iterator();
                                         x10::lang::Iterator<x10::lang::Place>::hasNext(x10aux::nullCheck(__lowerer__var__1__32074));
                                         )
                                    {
                                        
                                        //#line 60 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
                                        x10::lang::Place __lowerer__var__1__ =
                                          x10::lang::Iterator<x10::lang::Place>::next(x10aux::nullCheck(__lowerer__var__1__32074));
                                        
                                        //#line 60 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10Call_c
                                        x10::lang::Runtime::runAsync(
                                          __lowerer__var__1__,
                                          x10aux::ref<x10::lang::VoidFun_0_0>(x10aux::ref<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__7>(new (x10aux::alloc<x10::lang::VoidFun_0_0>(sizeof(au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__7)))au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__7(__lowerer__var__0__, atoms, otherAtoms))));
                                    }
                                }
                                
                            }
                        }
                    }
                }
                catch (x10aux::__ref& __ref__67) {
                    x10aux::ref<x10::lang::Throwable>& __exc__ref__67 = (x10aux::ref<x10::lang::Throwable>&)__ref__67;
                    if (true) {
                        x10aux::ref<x10::lang::Throwable> __lowerer__var__2__ =
                          static_cast<x10aux::ref<x10::lang::Throwable> >(__exc__ref__67);
                        {
                            
                            //#line 54 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10Call_c
                            x10::lang::Runtime::pushException(
                              __lowerer__var__2__);
                            
                            //#line 54 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": polyglot.ast.Throw_c
                            x10aux::throwException(x10aux::nullCheck(x10::lang::RuntimeException::_make()));
                        }
                    } else
                    throw;
                }
                
                //#line 54 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10Call_c
                x10::compiler::Finalization::plausibleThrow();
            }
            catch (x10aux::__ref& __ref__68) {
                x10aux::ref<x10::lang::Throwable>& __exc__ref__68 = (x10aux::ref<x10::lang::Throwable>&)__ref__68;
                if (true) {
                    x10aux::ref<x10::lang::Throwable> formal32089 =
                      static_cast<x10aux::ref<x10::lang::Throwable> >(__exc__ref__68);
                    {
                        
                        //#line 54 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalAssign_c
                        throwable32088 =
                          formal32089;
                    }
                } else
                throw;
            }
            
            //#line 54 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10If_c
            if ((!x10aux::struct_equals(X10_NULL,
                                        throwable32088)))
            {
                
                //#line 54 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10If_c
                if (x10aux::instanceof<x10aux::ref<x10::compiler::Abort> >(throwable32088))
                {
                    
                    //#line 54 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": polyglot.ast.Throw_c
                    x10aux::throwException(x10aux::nullCheck(throwable32088));
                }
                
            }
            
            //#line 54 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10If_c
            if (true) {
                
                //#line 54 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalAssign_c
                directEnergy = x10::lang::Runtime::stopCollectingFinish<x10_double >(
                                 x10____var0);
            }
            
            //#line 54 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10If_c
            if ((!x10aux::struct_equals(X10_NULL,
                                        throwable32088)))
            {
                
                //#line 54 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10If_c
                if (!(x10aux::instanceof<x10aux::ref<x10::compiler::Finalization> >(throwable32088)))
                {
                    
                    //#line 54 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": polyglot.ast.Throw_c
                    x10aux::throwException(x10aux::nullCheck(throwable32088));
                }
                
            }
            
        }
    }
    
    //#line 147 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10Call_c
    ((x10aux::ref<au::edu::anu::chem::mm::ElectrostaticDirectMethod>)this)->
      FMGL(timer)->au::edu::anu::util::Timer::stop(
      ((x10_int)0));
    
    //#line 148 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10Return_c
    return ((directEnergy) / (2.0));
    
}

//#line 27 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10MethodDecl_c
x10aux::ref<au::edu::anu::chem::mm::ElectrostaticDirectMethod>
  au::edu::anu::chem::mm::ElectrostaticDirectMethod::au__edu__anu__chem__mm__ElectrostaticDirectMethod____au__edu__anu__chem__mm__ElectrostaticDirectMethod__this(
  ) {
    
    //#line 27 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10Return_c
    return ((x10aux::ref<au::edu::anu::chem::mm::ElectrostaticDirectMethod>)this);
    
}

//#line 27 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10MethodDecl_c
void au::edu::anu::chem::mm::ElectrostaticDirectMethod::__fieldInitializers25763(
  ) {
    
    //#line 27 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::mm::ElectrostaticDirectMethod>)this)->
      FMGL(X10__object_lock_id0) = ((x10_int)-1);
    
    //#line 27 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::mm::ElectrostaticDirectMethod>)this)->
      FMGL(timer) = (__extension__ ({
        
        //#line 31 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::util::Timer> alloc25973 =
          
        x10aux::ref<au::edu::anu::util::Timer>((new (memset(x10aux::alloc<au::edu::anu::util::Timer>(), 0, sizeof(au::edu::anu::util::Timer))) au::edu::anu::util::Timer()))
        ;
        
        //#line 31 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10ConstructorCall_c
        (alloc25973)->::au::edu::anu::util::Timer::_constructor(
          ((x10_int)1),
          x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
        alloc25973;
    }))
    ;
    
    //#line 27 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::mm::ElectrostaticDirectMethod>)this)->
      FMGL(asyncComms) = true;
}
const x10aux::serialization_id_t au::edu::anu::chem::mm::ElectrostaticDirectMethod::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(au::edu::anu::chem::mm::ElectrostaticDirectMethod::_deserializer, x10aux::CLOSURE_KIND_NOT_ASYNC);

void au::edu::anu::chem::mm::ElectrostaticDirectMethod::_serialize_body(x10aux::serialization_buffer& buf) {
    x10::lang::Object::_serialize_body(buf);
    buf.write(this->FMGL(timer));
    buf.write(this->FMGL(asyncComms));
    buf.write(this->FMGL(atoms));
    buf.write(this->FMGL(otherAtoms));
    
}

x10aux::ref<x10::lang::Reference> au::edu::anu::chem::mm::ElectrostaticDirectMethod::_deserializer(x10aux::deserialization_buffer& buf) {
    x10aux::ref<au::edu::anu::chem::mm::ElectrostaticDirectMethod> this_ = new (memset(x10aux::alloc<au::edu::anu::chem::mm::ElectrostaticDirectMethod>(), 0, sizeof(au::edu::anu::chem::mm::ElectrostaticDirectMethod))) au::edu::anu::chem::mm::ElectrostaticDirectMethod();
    buf.record_reference(this_);
    this_->_deserialize_body(buf);
    return this_;
}

void au::edu::anu::chem::mm::ElectrostaticDirectMethod::_deserialize_body(x10aux::deserialization_buffer& buf) {
    x10::lang::Object::_deserialize_body(buf);
    FMGL(timer) = buf.read<x10aux::ref<au::edu::anu::util::Timer> >();
    FMGL(asyncComms) = buf.read<x10_boolean>();
    FMGL(atoms) = buf.read<x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > >();
    FMGL(otherAtoms) = buf.read<x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > > > >();
}

x10aux::RuntimeType au::edu::anu::chem::mm::ElectrostaticDirectMethod::rtt;
void au::edu::anu::chem::mm::ElectrostaticDirectMethod::_initRTT() {
    if (rtt.initStageOne(&rtt)) return;
    const x10aux::RuntimeType* parents[1] = { x10aux::getRTT<x10::lang::Object>()};
    rtt.initStageTwo("au.edu.anu.chem.mm.ElectrostaticDirectMethod",x10aux::RuntimeType::class_kind, 1, parents, 0, NULL, NULL);
}
x10::lang::Fun_0_1<x10_int, au::edu::anu::chem::PointCharge>::itable<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__2>au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__2::_itable(&x10::lang::Reference::equals, &x10::lang::Closure::hashCode, &au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__2::__apply, &au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__2::toString, &x10::lang::Closure::typeName);
x10aux::itable_entry au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__2::_itables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Fun_0_1<x10_int, au::edu::anu::chem::PointCharge> >, &au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__2::_itable),x10aux::itable_entry(NULL, NULL)};

const x10aux::serialization_id_t au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__2::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__2::_deserialize<x10::lang::Reference>,x10aux::CLOSURE_KIND_NOT_ASYNC);

x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > >::itable<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__1>au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__1::_itable(&x10::lang::Reference::equals, &x10::lang::Closure::hashCode, &au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__1::__apply, &au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__1::toString, &x10::lang::Closure::typeName);
x10aux::itable_entry au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__1::_itables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > >, &au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__1::_itable),x10aux::itable_entry(NULL, NULL)};

const x10aux::serialization_id_t au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__1::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__1::_deserialize<x10::lang::Reference>,x10aux::CLOSURE_KIND_NOT_ASYNC);

x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > >::itable<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__3>au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__3::_itable(&x10::lang::Reference::equals, &x10::lang::Closure::hashCode, &au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__3::__apply, &au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__3::toString, &x10::lang::Closure::typeName);
x10aux::itable_entry au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__3::_itables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > > >, &au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__3::_itable),x10aux::itable_entry(NULL, NULL)};

const x10aux::serialization_id_t au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__3::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__3::_deserialize<x10::lang::Reference>,x10aux::CLOSURE_KIND_NOT_ASYNC);

x10::lang::Fun_0_1<x10_int, au::edu::anu::chem::PointCharge>::itable<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__5>au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__5::_itable(&x10::lang::Reference::equals, &x10::lang::Closure::hashCode, &au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__5::__apply, &au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__5::toString, &x10::lang::Closure::typeName);
x10aux::itable_entry au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__5::_itables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Fun_0_1<x10_int, au::edu::anu::chem::PointCharge> >, &au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__5::_itable),x10aux::itable_entry(NULL, NULL)};

const x10aux::serialization_id_t au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__5::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__5::_deserialize<x10::lang::Reference>,x10aux::CLOSURE_KIND_NOT_ASYNC);

x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > >::itable<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__4>au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__4::_itable(&x10::lang::Reference::equals, &x10::lang::Closure::hashCode, &au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__4::__apply, &au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__4::toString, &x10::lang::Closure::typeName);
x10aux::itable_entry au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__4::_itables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > >, &au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__4::_itable),x10aux::itable_entry(NULL, NULL)};

const x10aux::serialization_id_t au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__4::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__4::_deserialize<x10::lang::Reference>,x10aux::CLOSURE_KIND_NOT_ASYNC);

x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > >::itable<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__6>au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__6::_itable(&x10::lang::Reference::equals, &x10::lang::Closure::hashCode, &au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__6::__apply, &au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__6::toString, &x10::lang::Closure::typeName);
x10aux::itable_entry au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__6::_itables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > > >, &au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__6::_itable),x10aux::itable_entry(NULL, NULL)};

const x10aux::serialization_id_t au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__6::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__6::_deserialize<x10::lang::Reference>,x10aux::CLOSURE_KIND_NOT_ASYNC);

x10::lang::VoidFun_0_0::itable<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__9>au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__9::_itable(&x10::lang::Reference::equals, &x10::lang::Closure::hashCode, &au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__9::__apply, &au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__9::toString, &x10::lang::Closure::typeName);
x10aux::itable_entry au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__9::_itables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::VoidFun_0_0>, &au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__9::_itable),x10aux::itable_entry(NULL, NULL)};

const x10aux::serialization_id_t au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__9::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__9::_deserialize<x10::lang::Reference>,x10aux::CLOSURE_KIND_NOT_ASYNC);

x10::lang::VoidFun_0_0::itable<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__10>au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__10::_itable(&x10::lang::Reference::equals, &x10::lang::Closure::hashCode, &au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__10::__apply, &au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__10::toString, &x10::lang::Closure::typeName);
x10aux::itable_entry au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__10::_itables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::VoidFun_0_0>, &au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__10::_itable),x10aux::itable_entry(NULL, NULL)};

const x10aux::serialization_id_t au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__10::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__10::_deserialize<x10::lang::Reference>,x10aux::CLOSURE_KIND_NOT_ASYNC);

x10::lang::VoidFun_0_0::itable<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__8>au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__8::_itable(&x10::lang::Reference::equals, &x10::lang::Closure::hashCode, &au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__8::__apply, &au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__8::toString, &x10::lang::Closure::typeName);
x10aux::itable_entry au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__8::_itables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::VoidFun_0_0>, &au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__8::_itable),x10aux::itable_entry(NULL, NULL)};

const x10aux::serialization_id_t au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__8::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__8::_deserialize<x10::lang::Reference>,x10aux::CLOSURE_KIND_SIMPLE_ASYNC);

x10::lang::VoidFun_0_0::itable<au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__7>au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__7::_itable(&x10::lang::Reference::equals, &x10::lang::Closure::hashCode, &au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__7::__apply, &au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__7::toString, &x10::lang::Closure::typeName);
x10aux::itable_entry au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__7::_itables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::VoidFun_0_0>, &au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__7::_itable),x10aux::itable_entry(NULL, NULL)};

const x10aux::serialization_id_t au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__7::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(au_edu_anu_chem_mm_ElectrostaticDirectMethod__closure__7::_deserialize<x10::lang::Reference>,x10aux::CLOSURE_KIND_SIMPLE_ASYNC);

/* END of ElectrostaticDirectMethod */
/*************************************************/
/*************************************************/
/* START of ElectrostaticDirectMethod$SumReducer */
#include <au/edu/anu/chem/mm/ElectrostaticDirectMethod__SumReducer.h>

#include <x10/lang/Any.h>
#include <x10/lang/Reducible.h>
#include <x10/util/concurrent/Atomic.h>
#include <x10/lang/Double.h>
#include <x10/lang/Int.h>
#include <x10/util/concurrent/OrderedLock.h>
#include <x10/util/Map.h>
#include <x10/lang/String.h>
#include <x10/compiler/Native.h>
#include <x10/compiler/NonEscaping.h>
#include <x10/lang/Boolean.h>
namespace au { namespace edu { namespace anu { namespace chem { namespace mm { 
class ElectrostaticDirectMethod__SumReducer_ibox0 : public x10::lang::IBox<au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer> {
public:
    static x10::lang::Any::itable<ElectrostaticDirectMethod__SumReducer_ibox0 > itable;
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
x10::lang::Any::itable<ElectrostaticDirectMethod__SumReducer_ibox0 >  ElectrostaticDirectMethod__SumReducer_ibox0::itable(&ElectrostaticDirectMethod__SumReducer_ibox0::equals, &ElectrostaticDirectMethod__SumReducer_ibox0::hashCode, &ElectrostaticDirectMethod__SumReducer_ibox0::toString, &ElectrostaticDirectMethod__SumReducer_ibox0::typeName);
} } } } } 
x10::lang::Any::itable<au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer >  au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer::_itable_0(&au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer::equals, &au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer::hashCode, &au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer::toString, &au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer::typeName);
namespace au { namespace edu { namespace anu { namespace chem { namespace mm { 
class ElectrostaticDirectMethod__SumReducer_ibox1 : public x10::lang::IBox<au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer> {
public:
    static x10::lang::Reducible<x10_double>::itable<ElectrostaticDirectMethod__SumReducer_ibox1 > itable;
    x10_boolean equals(x10aux::ref<x10::lang::Any> arg0) {
        return this->value->equals(arg0);
    }
    x10_int hashCode() {
        return this->value->hashCode();
    }
    x10_double __apply(x10_double arg0, x10_double arg1) {
        return this->value->__apply(arg0, arg1);
    }
    x10aux::ref<x10::lang::String> toString() {
        return this->value->toString();
    }
    x10aux::ref<x10::lang::String> typeName() {
        return this->value->typeName();
    }
    x10_double zero() {
        return this->value->zero();
    }
    
};
x10::lang::Reducible<x10_double>::itable<ElectrostaticDirectMethod__SumReducer_ibox1 >  ElectrostaticDirectMethod__SumReducer_ibox1::itable(&ElectrostaticDirectMethod__SumReducer_ibox1::equals, &ElectrostaticDirectMethod__SumReducer_ibox1::hashCode, &ElectrostaticDirectMethod__SumReducer_ibox1::__apply, &ElectrostaticDirectMethod__SumReducer_ibox1::toString, &ElectrostaticDirectMethod__SumReducer_ibox1::typeName, &ElectrostaticDirectMethod__SumReducer_ibox1::zero);
} } } } } 
x10::lang::Reducible<x10_double>::itable<au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer >  au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer::_itable_1(&au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer::equals, &au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer::hashCode, &au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer::__apply, &au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer::toString, &au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer::typeName, &au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer::zero);
x10aux::itable_entry au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer::_itables[3] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Any>, &au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer::_itable_0), x10aux::itable_entry(&x10aux::getRTT<x10::lang::Reducible<x10_double> >, &au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer::_itable_1), x10aux::itable_entry(NULL, (void*)x10aux::getRTT<au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer>())};
x10aux::itable_entry au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer::_iboxitables[3] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Any>, &au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer_ibox0::itable), x10aux::itable_entry(&x10aux::getRTT<x10::lang::Reducible<x10_double> >, &au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer_ibox1::itable), x10aux::itable_entry(NULL, (void*)x10aux::getRTT<au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer>())};

//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10FieldDecl_c

//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock> au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer::getOrderedLock(
  ) {
    
    //#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10Return_c
    return x10::util::concurrent::OrderedLock::getObjectLock((*this)->
                                                               FMGL(X10__object_lock_id0));
    
}

//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10FieldDecl_c
x10_int au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer::FMGL(X10__class_lock_id1);
void au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer::FMGL(X10__class_lock_id1__do_init)() {
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer.X10$class_lock_id1");
    x10_int __var72__ = x10::util::concurrent::OrderedLock::createClassLock();
    FMGL(X10__class_lock_id1) = __var72__;
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
}
void au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer::FMGL(X10__class_lock_id1__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var73__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(X10__class_lock_id1__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var73__ != x10aux::UNINITIALIZED) goto WAIT;
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
                                                                       _SI_("WAITING for field: au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer.X10$class_lock_id1 to be initialized");
                                                                       while (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                                       _SI_("CONTINUING because field: au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer.X10$class_lock_id1 has been initialized");
                                                                       x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__74 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer::FMGL(X10__class_lock_id1__init));

volatile x10aux::status au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer::FMGL(X10__class_lock_id1__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer::FMGL(X10__class_lock_id1__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(X10__class_lock_id1) = buf.read<x10_int>();
    au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer::FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer::FMGL(X10__class_lock_id1__id) =
  x10aux::StaticInitBroadcastDispatcher::addRoutine(au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer::FMGL(X10__class_lock_id1__deserialize));


//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock> au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer::getStaticOrderedLock(
  ) {
    
    //#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 170 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/util/concurrent/OrderedLock.x10": x10.ast.X10LocalDecl_c
        x10_int lockId31936 = au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer::
                                FMGL(X10__class_lock_id1__get)();
        x10::util::Map<x10_int, x10aux::ref<x10::util::concurrent::OrderedLock> >::getOrThrow(x10aux::nullCheck(x10::util::concurrent::OrderedLock::
                                                                                                                  FMGL(lockMap__get)()), 
          lockId31936);
    }))
    ;
    
}

//#line 152 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10MethodDecl_c

//#line 153 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10MethodDecl_c

//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::lang::String> au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer::typeName(
  ){
    return x10aux::type_name((*this));
}

//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::lang::String> au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer::toString(
  ) {
    
    //#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10Return_c
    return x10aux::string_utils::lit("struct au.edu.anu.chem.mm.ElectrostaticDirectMethod.SumReducer");
    
}

//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10MethodDecl_c

//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10MethodDecl_c
x10_boolean au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer::equals(
  x10aux::ref<x10::lang::Any> other) {
    
    //#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10If_c
    if (!(x10aux::instanceof<au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer>(other)))
    {
        
        //#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10Return_c
        return false;
        
    }
    
    //#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10Return_c
    return true;
    
}

//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10MethodDecl_c

//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10MethodDecl_c
x10_boolean au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer::_struct_equals(
  x10aux::ref<x10::lang::Any> other) {
    
    //#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10If_c
    if (!(x10aux::instanceof<au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer>(other)))
    {
        
        //#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10Return_c
        return false;
        
    }
    
    //#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10Return_c
    return true;
    
}

//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10MethodDecl_c

//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10MethodDecl_c

//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10ConstructorDecl_c


//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer::_constructor(
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    
    //#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.AssignPropertyCall_c
    
    //#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10LocalDecl_c
        au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer this3194432046 =
          (*this);
        {
            
            //#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10FieldAssign_c
            this3194432046->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
        }
        
    }))
    ;
    
    //#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10FieldAssign_c
    (*this)->
      FMGL(X10__object_lock_id0) =
      x10aux::nullCheck(paramLock)->getIndex();
    
}
au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer::_make(
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer this_; 
    this_->_constructor(paramLock);
    return this_;
}



//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10": x10.ast.X10MethodDecl_c
void au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer::_serialize(au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer this_, x10aux::serialization_buffer& buf) {
    
}

void au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer::_deserialize_body(x10aux::deserialization_buffer& buf) {
    
}


x10aux::RuntimeType au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer::rtt;
void au::edu::anu::chem::mm::ElectrostaticDirectMethod__SumReducer::_initRTT() {
    if (rtt.initStageOne(&rtt)) return;
    const x10aux::RuntimeType* parents[3] = { x10aux::getRTT<x10::lang::Any>(), x10aux::getRTT<x10::lang::Reducible<x10_double> >(), x10aux::getRTT<x10::lang::Any>()};
    rtt.initStageTwo("au.edu.anu.chem.mm.ElectrostaticDirectMethod.SumReducer",x10aux::RuntimeType::struct_kind, 3, parents, 0, NULL, NULL);
    rtt.containsPtrs = false;
}
/* END of ElectrostaticDirectMethod$SumReducer */
/*************************************************/
