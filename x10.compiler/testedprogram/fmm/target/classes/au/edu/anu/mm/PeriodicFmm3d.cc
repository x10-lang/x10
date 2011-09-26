/*************************************************/
/* START of PeriodicFmm3d */
#include <au/edu/anu/mm/PeriodicFmm3d.h>

#ifndef AU_EDU_ANU_MM_PERIODICFMM3D__CLOSURE__1_CLOSURE
#define AU_EDU_ANU_MM_PERIODICFMM3D__CLOSURE__1_CLOSURE
#include <x10/lang/Closure.h>
#include <x10/lang/VoidFun_0_0.h>
class au_edu_anu_mm_PeriodicFmm3d__closure__1 : public x10::lang::Closure {
    public:
    
    static x10::lang::VoidFun_0_0::itable<au_edu_anu_mm_PeriodicFmm3d__closure__1> _itable;
    static x10aux::itable_entry _itables[2];
    
    virtual x10aux::itable_entry* _getITables() { return _itables; }
    
    // closure body
    void __apply() {
        
        //#line 72 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
        saved_this->prefetchRemoteAtoms();
    }
    
    // captured environment
    x10aux::ref<au::edu::anu::mm::PeriodicFmm3d> saved_this;
    
    x10aux::serialization_id_t _get_serialization_id() {
        return _serialization_id;
    }
    
    void _serialize_body(x10aux::serialization_buffer &buf) {
        buf.write(this->saved_this);
    }
    
    template<class __T> static x10aux::ref<__T> _deserialize(x10aux::deserialization_buffer &buf) {
        au_edu_anu_mm_PeriodicFmm3d__closure__1* storage = x10aux::alloc<au_edu_anu_mm_PeriodicFmm3d__closure__1>();
        buf.record_reference(x10aux::ref<au_edu_anu_mm_PeriodicFmm3d__closure__1>(storage));
        x10aux::ref<au::edu::anu::mm::PeriodicFmm3d> that_saved_this = buf.read<x10aux::ref<au::edu::anu::mm::PeriodicFmm3d> >();
        x10aux::ref<au_edu_anu_mm_PeriodicFmm3d__closure__1> this_ = new (storage) au_edu_anu_mm_PeriodicFmm3d__closure__1(that_saved_this);
        return this_;
    }
    
    au_edu_anu_mm_PeriodicFmm3d__closure__1(x10aux::ref<au::edu::anu::mm::PeriodicFmm3d> saved_this) : saved_this(saved_this) { }
    
    static const x10aux::serialization_id_t _serialization_id;
    
    static const x10aux::RuntimeType* getRTT() { return x10aux::getRTT<x10::lang::VoidFun_0_0>(); }
    virtual const x10aux::RuntimeType *_type() const { return x10aux::getRTT<x10::lang::VoidFun_0_0>(); }
    
    x10aux::ref<x10::lang::String> toString() {
        return x10aux::string_utils::lit(this->toNativeString());
    }
    
    const char* toNativeString() {
        return "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10:71-73";
    }

};

#endif // AU_EDU_ANU_MM_PERIODICFMM3D__CLOSURE__1_CLOSURE
#ifndef AU_EDU_ANU_MM_PERIODICFMM3D__CLOSURE__2_CLOSURE
#define AU_EDU_ANU_MM_PERIODICFMM3D__CLOSURE__2_CLOSURE
#include <x10/lang/Closure.h>
#include <x10/lang/VoidFun_0_0.h>
class au_edu_anu_mm_PeriodicFmm3d__closure__2 : public x10::lang::Closure {
    public:
    
    static x10::lang::VoidFun_0_0::itable<au_edu_anu_mm_PeriodicFmm3d__closure__2> _itable;
    static x10aux::itable_entry _itables[2];
    
    virtual x10aux::itable_entry* _getITables() { return _itables; }
    
    // closure body
    void __apply() {
        
        //#line 97 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::mm::MultipoleExpansion> > > macroMultipoles =
          
        x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::mm::MultipoleExpansion> > >((new (memset(x10aux::alloc<x10::array::Array<x10aux::ref<au::edu::anu::mm::MultipoleExpansion> > >(), 0, sizeof(x10::array::Array<x10aux::ref<au::edu::anu::mm::MultipoleExpansion> >))) x10::array::Array<x10aux::ref<au::edu::anu::mm::MultipoleExpansion> >()))
        ;
        
        //#line 97 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.StmtExpr_c
        (__extension__ ({
            
            //#line 243 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
            x10_int size37326 = ((x10_int) ((numShells) + (((x10_int)1))));
            {
                
                //#line 243 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                (macroMultipoles)->::x10::lang::Object::_constructor();
                
                //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<x10::array::Region> myReg37327 = x10aux::class_cast<x10aux::ref<x10::array::Region> >((__extension__ ({
                    
                    //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10aux::ref<x10::array::RectRegion1D> alloc1996037328 =
                      
                    x10aux::ref<x10::array::RectRegion1D>((new (memset(x10aux::alloc<x10::array::RectRegion1D>(), 0, sizeof(x10::array::RectRegion1D))) x10::array::RectRegion1D()))
                    ;
                    
                    //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                    (alloc1996037328)->::x10::array::RectRegion1D::_constructor(
                      ((x10_int)0),
                      ((x10_int) ((size37326) - (((x10_int)1)))));
                    alloc1996037328;
                }))
                );
                
                //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                macroMultipoles->FMGL(region) = myReg37327;
                
                //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                macroMultipoles->FMGL(rank) = ((x10_int)1);
                
                //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                macroMultipoles->FMGL(rect) = true;
                
                //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                macroMultipoles->FMGL(zeroBased) = true;
                
                //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                macroMultipoles->FMGL(rail) = true;
                
                //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                macroMultipoles->FMGL(size) = size37326;
                
                //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                macroMultipoles->FMGL(layout) = (__extension__ ({
                    
                    //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10::array::RectLayout alloc1996137329 =  x10::array::RectLayout::_alloc();
                    
                    //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.StmtExpr_c
                    (__extension__ ({
                        
                        //#line 97 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                        x10_int _max037333 = ((x10_int) ((size37326) - (((x10_int)1))));
                        {
                            
                            //#line 98 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996137329->FMGL(rank) = ((x10_int)1);
                            
                            //#line 99 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996137329->FMGL(min0) = ((x10_int)0);
                            
                            //#line 100 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996137329->FMGL(delta0) = ((x10_int) ((((x10_int) ((_max037333) - (((x10_int)0))))) + (((x10_int)1))));
                            
                            //#line 101 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996137329->FMGL(size) = ((alloc1996137329->
                                                              FMGL(delta0)) > (((x10_int)0)))
                              ? (alloc1996137329->
                                   FMGL(delta0))
                              : (((x10_int)0));
                            
                            //#line 103 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996137329->FMGL(min1) =
                              ((x10_int)0);
                            
                            //#line 103 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996137329->FMGL(delta1) =
                              ((x10_int)0);
                            
                            //#line 104 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996137329->FMGL(min2) =
                              ((x10_int)0);
                            
                            //#line 104 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996137329->FMGL(delta2) =
                              ((x10_int)0);
                            
                            //#line 105 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996137329->FMGL(min3) =
                              ((x10_int)0);
                            
                            //#line 105 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996137329->FMGL(delta3) =
                              ((x10_int)0);
                            
                            //#line 106 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996137329->FMGL(min) = X10_NULL;
                            
                            //#line 106 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996137329->FMGL(delta) =
                              X10_NULL;
                        }
                        
                    }))
                    ;
                    alloc1996137329;
                }))
                ;
                
                //#line 250 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_int n37330 = (__extension__ ({
                    
                    //#line 250 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10::array::RectLayout this37335 = macroMultipoles->
                                                         FMGL(layout);
                    this37335->FMGL(size);
                }))
                ;
                
                //#line 251 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                macroMultipoles->FMGL(raw) = x10::util::IndexedMemoryChunk<void>::allocate<x10aux::ref<au::edu::anu::mm::MultipoleExpansion> >(n37330, 8, false, true);
            }
            
        }))
        ;
        
        //#line 98 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::mm::LocalExpansion> > > macroLocalTranslations =
          
        x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::mm::LocalExpansion> > >((new (memset(x10aux::alloc<x10::array::Array<x10aux::ref<au::edu::anu::mm::LocalExpansion> > >(), 0, sizeof(x10::array::Array<x10aux::ref<au::edu::anu::mm::LocalExpansion> >))) x10::array::Array<x10aux::ref<au::edu::anu::mm::LocalExpansion> >()))
        ;
        
        //#line 98 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.StmtExpr_c
        (__extension__ ({
            
            //#line 243 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
            x10_int size37336 = ((x10_int) ((numShells) + (((x10_int)1))));
            {
                
                //#line 243 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                (macroLocalTranslations)->::x10::lang::Object::_constructor();
                
                //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<x10::array::Region> myReg37337 =
                  x10aux::class_cast<x10aux::ref<x10::array::Region> >((__extension__ ({
                    
                    //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10aux::ref<x10::array::RectRegion1D> alloc1996037338 =
                      
                    x10aux::ref<x10::array::RectRegion1D>((new (memset(x10aux::alloc<x10::array::RectRegion1D>(), 0, sizeof(x10::array::RectRegion1D))) x10::array::RectRegion1D()))
                    ;
                    
                    //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                    (alloc1996037338)->::x10::array::RectRegion1D::_constructor(
                      ((x10_int)0),
                      ((x10_int) ((size37336) - (((x10_int)1)))));
                    alloc1996037338;
                }))
                );
                
                //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                macroLocalTranslations->FMGL(region) =
                  myReg37337;
                
                //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                macroLocalTranslations->FMGL(rank) =
                  ((x10_int)1);
                
                //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                macroLocalTranslations->FMGL(rect) =
                  true;
                
                //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                macroLocalTranslations->FMGL(zeroBased) =
                  true;
                
                //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                macroLocalTranslations->FMGL(rail) =
                  true;
                
                //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                macroLocalTranslations->FMGL(size) =
                  size37336;
                
                //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                macroLocalTranslations->FMGL(layout) =
                  (__extension__ ({
                    
                    //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10::array::RectLayout alloc1996137339 =
                      
                    x10::array::RectLayout::_alloc();
                    
                    //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.StmtExpr_c
                    (__extension__ ({
                        
                        //#line 97 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                        x10_int _max037343 =
                          ((x10_int) ((size37336) - (((x10_int)1))));
                        {
                            
                            //#line 98 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996137339->
                              FMGL(rank) =
                              ((x10_int)1);
                            
                            //#line 99 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996137339->
                              FMGL(min0) =
                              ((x10_int)0);
                            
                            //#line 100 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996137339->
                              FMGL(delta0) =
                              ((x10_int) ((((x10_int) ((_max037343) - (((x10_int)0))))) + (((x10_int)1))));
                            
                            //#line 101 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996137339->
                              FMGL(size) =
                              ((alloc1996137339->
                                  FMGL(delta0)) > (((x10_int)0)))
                              ? (alloc1996137339->
                                   FMGL(delta0))
                              : (((x10_int)0));
                            
                            //#line 103 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996137339->
                              FMGL(min1) =
                              ((x10_int)0);
                            
                            //#line 103 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996137339->
                              FMGL(delta1) =
                              ((x10_int)0);
                            
                            //#line 104 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996137339->
                              FMGL(min2) =
                              ((x10_int)0);
                            
                            //#line 104 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996137339->
                              FMGL(delta2) =
                              ((x10_int)0);
                            
                            //#line 105 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996137339->
                              FMGL(min3) =
                              ((x10_int)0);
                            
                            //#line 105 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996137339->
                              FMGL(delta3) =
                              ((x10_int)0);
                            
                            //#line 106 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996137339->
                              FMGL(min) =
                              X10_NULL;
                            
                            //#line 106 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996137339->
                              FMGL(delta) =
                              X10_NULL;
                        }
                        
                    }))
                    ;
                    alloc1996137339;
                }))
                ;
                
                //#line 250 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_int n37340 = (__extension__ ({
                    
                    //#line 250 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10::array::RectLayout this37345 =
                      macroLocalTranslations->
                        FMGL(layout);
                    this37345->FMGL(size);
                }))
                ;
                
                //#line 251 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                macroLocalTranslations->FMGL(raw) =
                  x10::util::IndexedMemoryChunk<void>::allocate<x10aux::ref<au::edu::anu::mm::LocalExpansion> >(n37340, 8, false, true);
            }
            
        }))
        ;
        
        //#line 99 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::FmmBox> topLevelBox =
          x10aux::nullCheck((__extension__ ({
              
              //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
              x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > ret37347;
              
              //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
              goto __ret37348; __ret37348: {
              {
                  
                  //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                  ret37347 =
                    (x10aux::nullCheck(boxes)->
                       FMGL(raw))->__apply(((x10_int)0));
                  
                  //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                  goto __ret37348_end_;
              }goto __ret37348_end_; __ret37348_end_: ;
              }
              ret37347;
              }))
              )->x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> >::__apply(
                ((x10_int)0),
                ((x10_int)0),
                ((x10_int)0));
          
        
        //#line 100 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.StmtExpr_c
        (__extension__ ({
            
            //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<au::edu::anu::mm::MultipoleExpansion> v37355 =
              x10aux::nullCheck(topLevelBox)->
                FMGL(multipoleExp);
            
            //#line 508 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<au::edu::anu::mm::MultipoleExpansion> ret37356;
            {
                
                //#line 512 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                (macroMultipoles->
                   FMGL(raw))->__set(((x10_int)0), v37355);
                
                //#line 519 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                ret37356 =
                  v37355;
            }
            ret37356;
        }))
        ;
        
        //#line 102 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::MultipoleExpansion> macroTranslation =
          
        x10aux::ref<au::edu::anu::mm::MultipoleExpansion>((new (memset(x10aux::alloc<au::edu::anu::mm::MultipoleExpansion>(), 0, sizeof(au::edu::anu::mm::MultipoleExpansion))) au::edu::anu::mm::MultipoleExpansion()))
        ;
        
        //#line 102 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10ConstructorCall_c
        (macroTranslation)->::au::edu::anu::mm::MultipoleExpansion::_constructor(
          numTerms,
          x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
        
        //#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.For_c
        {
            x10aux::ref<x10::lang::Iterator<x10aux::ref<x10::array::Point> > > id24677;
            for (
                 //#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                 id24677 =
                   x10aux::nullCheck(au::edu::anu::mm::PeriodicFmm3d::
                                       FMGL(threeCube__get)())->iterator();
                 x10::lang::Iterator<x10aux::ref<x10::array::Point> >::hasNext(x10aux::nullCheck(id24677));
                 )
            {
                
                //#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<x10::array::Point> id410 =
                  x10::lang::Iterator<x10aux::ref<x10::array::Point> >::next(x10aux::nullCheck(id24677));
                
                //#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                x10_int i =
                  x10aux::nullCheck(id410)->x10::array::Point::__apply(
                    ((x10_int)0));
                
                //#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                x10_int j =
                  x10aux::nullCheck(id410)->x10::array::Point::__apply(
                    ((x10_int)1));
                
                //#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                x10_int k =
                  x10aux::nullCheck(id410)->x10::array::Point::__apply(
                    ((x10_int)2));
                
                //#line 106 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                x10x::vector::Vector3d translationVector =
                  
                x10x::vector::Vector3d::_alloc();
                
                //#line 106 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10ConstructorCall_c
                (translationVector)->::x10x::vector::Vector3d::_constructor(
                  ((((x10_double) (i))) * (size)),
                  ((((x10_double) (j))) * (size)),
                  ((((x10_double) (k))) * (size)),
                  x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
                
                //#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<au::edu::anu::mm::MultipoleExpansion> translation =
                  au::edu::anu::mm::MultipoleExpansion::getOlm(
                    x10aux::class_cast_unchecked<x10aux::ref<x10x::vector::Tuple3d> >(translationVector),
                    numTerms);
                
                //#line 110 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
                x10aux::nullCheck(macroTranslation)->unsafeAdd(
                  x10aux::class_cast_unchecked<x10aux::ref<au::edu::anu::mm::Expansion> >(translation));
            }
        }
        
        //#line 112 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.StmtExpr_c
        (__extension__ ({
            
            //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<au::edu::anu::mm::MultipoleExpansion> v38472 =
              (__extension__ ({
                
                //#line 112 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<au::edu::anu::mm::MultipoleExpansion> alloc24661 =
                  
                x10aux::ref<au::edu::anu::mm::MultipoleExpansion>((new (memset(x10aux::alloc<au::edu::anu::mm::MultipoleExpansion>(), 0, sizeof(au::edu::anu::mm::MultipoleExpansion))) au::edu::anu::mm::MultipoleExpansion()))
                ;
                
                //#line 112 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10ConstructorCall_c
                (alloc24661)->::au::edu::anu::mm::MultipoleExpansion::_constructor(
                  numTerms,
                  x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
                alloc24661;
            }))
            ;
            
            //#line 508 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<au::edu::anu::mm::MultipoleExpansion> ret38473;
            {
                
                //#line 512 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                (macroMultipoles->
                   FMGL(raw))->__set(((x10_int)1), v38472);
                
                //#line 519 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                ret38473 =
                  v38472;
            }
            ret38473;
        }))
        ;
        
        //#line 113 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
        x10aux::nullCheck((__extension__ ({
            
            //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<au::edu::anu::mm::MultipoleExpansion> ret38481;
            
            //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
            goto __ret38482; __ret38482: {
            {
                
                //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                ret38481 =
                  (macroMultipoles->
                     FMGL(raw))->__apply(((x10_int)1));
                
                //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                goto __ret38482_end_;
            }goto __ret38482_end_; __ret38482_end_: ;
            }
            ret38481;
            }))
            )->translateAndAddMultipole(
              macroTranslation,
              (__extension__ ({
                  
                  //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                  x10aux::ref<au::edu::anu::mm::MultipoleExpansion> ret38489;
                  
                  //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                  goto __ret38490; __ret38490: {
                  {
                      
                      //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                      ret38489 =
                        (macroMultipoles->
                           FMGL(raw))->__apply(((x10_int)0));
                      
                      //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                      goto __ret38490_end_;
                  }goto __ret38490_end_; __ret38490_end_: ;
                  }
                  ret38489;
                  }))
                  );
        
        //#line 117 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.StmtExpr_c
        (__extension__ ({
            
            //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<au::edu::anu::mm::LocalExpansion> v39339 =
              (__extension__ ({
                
                //#line 117 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<au::edu::anu::mm::LocalExpansion> alloc24662 =
                  
                x10aux::ref<au::edu::anu::mm::LocalExpansion>((new (memset(x10aux::alloc<au::edu::anu::mm::LocalExpansion>(), 0, sizeof(au::edu::anu::mm::LocalExpansion))) au::edu::anu::mm::LocalExpansion()))
                ;
                
                //#line 117 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10ConstructorCall_c
                (alloc24662)->::au::edu::anu::mm::LocalExpansion::_constructor(
                  numTerms,
                  x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
                alloc24662;
            }))
            ;
            
            //#line 508 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<au::edu::anu::mm::LocalExpansion> ret39340;
            {
                
                //#line 512 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                (macroLocalTranslations->
                   FMGL(raw))->__set(((x10_int)0), v39339);
                
                //#line 519 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                ret39340 =
                  v39339;
            }
            ret39340;
        }))
        ;
        
        //#line 118 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.For_c
        {
            x10aux::ref<x10::lang::Iterator<x10aux::ref<x10::array::Point> > > id24697;
            for (
                 //#line 118 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                 id24697 =
                   x10aux::nullCheck(au::edu::anu::mm::PeriodicFmm3d::
                                       FMGL(nineCube__get)())->iterator();
                 x10::lang::Iterator<x10aux::ref<x10::array::Point> >::hasNext(x10aux::nullCheck(id24697));
                 )
            {
                
                //#line 118 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<x10::array::Point> id411 =
                  x10::lang::Iterator<x10aux::ref<x10::array::Point> >::next(x10aux::nullCheck(id24697));
                
                //#line 118 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                x10_int i =
                  x10aux::nullCheck(id411)->x10::array::Point::__apply(
                    ((x10_int)0));
                
                //#line 118 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                x10_int j =
                  x10aux::nullCheck(id411)->x10::array::Point::__apply(
                    ((x10_int)1));
                
                //#line 118 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                x10_int k =
                  x10aux::nullCheck(id411)->x10::array::Point::__apply(
                    ((x10_int)2));
                
                //#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10If_c
                if (((::abs(i)) > (((x10_int)1))) ||
                    ((::abs(j)) > (((x10_int)1))) ||
                    ((::abs(k)) > (((x10_int)1))))
                {
                    
                    //#line 121 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                    x10x::vector::Vector3d translationVector =
                      
                    x10x::vector::Vector3d::_alloc();
                    
                    //#line 121 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10ConstructorCall_c
                    (translationVector)->::x10x::vector::Vector3d::_constructor(
                      ((((x10_double) (i))) * (size)),
                      ((((x10_double) (j))) * (size)),
                      ((((x10_double) (k))) * (size)),
                      x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
                    
                    //#line 124 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                    x10aux::ref<au::edu::anu::mm::LocalExpansion> transform =
                      au::edu::anu::mm::LocalExpansion::getMlm(
                        x10aux::class_cast_unchecked<x10aux::ref<x10x::vector::Tuple3d> >(translationVector),
                        numTerms);
                    
                    //#line 125 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
                    x10aux::nullCheck((__extension__ ({
                        
                        //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10aux::ref<au::edu::anu::mm::LocalExpansion> ret39348;
                        
                        //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                        goto __ret39349; __ret39349: {
                        {
                            
                            //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                            ret39348 =
                              (macroLocalTranslations->
                                 FMGL(raw))->__apply(((x10_int)0));
                            
                            //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                            goto __ret39349_end_;
                        }goto __ret39349_end_; __ret39349_end_: ;
                        }
                        ret39348;
                        }))
                        )->unsafeAdd(
                          x10aux::class_cast_unchecked<x10aux::ref<au::edu::anu::mm::Expansion> >(transform));
                    }
                    
                }
            }
            
        
        //#line 128 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.StmtExpr_c
        (__extension__ ({
            
            //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<au::edu::anu::mm::LocalExpansion> v39364 =
              x10aux::nullCheck((__extension__ ({
                  
                  //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                  x10aux::ref<au::edu::anu::mm::LocalExpansion> ret39356;
                  
                  //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                  goto __ret39357; __ret39357: {
                  {
                      
                      //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                      ret39356 =
                        (macroLocalTranslations->
                           FMGL(raw))->__apply(((x10_int)0));
                      
                      //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                      goto __ret39357_end_;
                  }goto __ret39357_end_; __ret39357_end_: ;
                  }
                  ret39356;
                  }))
                  )->getMacroscopicParent();
              
            
            //#line 508 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<au::edu::anu::mm::LocalExpansion> ret39365;
            {
                
                //#line 512 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                (macroLocalTranslations->
                   FMGL(raw))->__set(((x10_int)1), v39364);
                
                //#line 519 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                ret39365 =
                  v39364;
            }
            ret39365;
            }))
            ;
        
        //#line 131 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.For_c
        {
            x10_int shell;
            for (
                 //#line 131 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                 shell =
                   ((x10_int)2);
                 ((shell) <= (numShells));
                 
                 //#line 131 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalAssign_c
                 shell =
                   ((x10_int) ((shell) + (((x10_int)1)))))
            {
                
                //#line 132 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalAssign_c
                macroTranslation =
                  x10aux::nullCheck(macroTranslation)->getMacroscopicParent();
                
                //#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.StmtExpr_c
                (__extension__ ({
                    
                    //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10_int i039372 =
                      shell;
                    
                    //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10aux::ref<au::edu::anu::mm::MultipoleExpansion> v39373 =
                      (__extension__ ({
                        
                        //#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                        x10aux::ref<au::edu::anu::mm::MultipoleExpansion> alloc24663 =
                          
                        x10aux::ref<au::edu::anu::mm::MultipoleExpansion>((new (memset(x10aux::alloc<au::edu::anu::mm::MultipoleExpansion>(), 0, sizeof(au::edu::anu::mm::MultipoleExpansion))) au::edu::anu::mm::MultipoleExpansion()))
                        ;
                        
                        //#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10ConstructorCall_c
                        (alloc24663)->::au::edu::anu::mm::MultipoleExpansion::_constructor(
                          numTerms,
                          x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
                        alloc24663;
                    }))
                    ;
                    
                    //#line 508 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10aux::ref<au::edu::anu::mm::MultipoleExpansion> ret39374;
                    {
                        
                        //#line 512 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                        (macroMultipoles->
                           FMGL(raw))->__set(i039372, v39373);
                        
                        //#line 519 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                        ret39374 =
                          v39373;
                    }
                    ret39374;
                }))
                ;
                
                //#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
                x10aux::nullCheck((__extension__ ({
                    
                    //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10_int i039381 =
                      shell;
                    
                    //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10aux::ref<au::edu::anu::mm::MultipoleExpansion> ret39382;
                    
                    //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                    goto __ret39383; __ret39383: {
                    {
                        
                        //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                        ret39382 =
                          (macroMultipoles->
                             FMGL(raw))->__apply(i039381);
                        
                        //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                        goto __ret39383_end_;
                    }goto __ret39383_end_; __ret39383_end_: ;
                    }
                    ret39382;
                    }))
                    )->translateAndAddMultipole(
                      macroTranslation,
                      (__extension__ ({
                          
                          //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                          x10_int i039389 =
                            ((x10_int) ((shell) - (((x10_int)1))));
                          
                          //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                          x10aux::ref<au::edu::anu::mm::MultipoleExpansion> ret39390;
                          
                          //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                          goto __ret39391; __ret39391: {
                          {
                              
                              //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                              ret39390 =
                                (macroMultipoles->
                                   FMGL(raw))->__apply(i039389);
                              
                              //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                              goto __ret39391_end_;
                          }goto __ret39391_end_; __ret39391_end_: ;
                          }
                          ret39390;
                          }))
                          );
                
                //#line 136 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.StmtExpr_c
                (__extension__ ({
                    
                    //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10_int i039405 =
                      shell;
                    
                    //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10aux::ref<au::edu::anu::mm::LocalExpansion> v39406 =
                      x10aux::nullCheck((__extension__ ({
                          
                          //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                          x10_int i039397 =
                            ((x10_int) ((shell) - (((x10_int)1))));
                          
                          //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                          x10aux::ref<au::edu::anu::mm::LocalExpansion> ret39398;
                          
                          //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                          goto __ret39399; __ret39399: {
                          {
                              
                              //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                              ret39398 =
                                (macroLocalTranslations->
                                   FMGL(raw))->__apply(i039397);
                              
                              //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                              goto __ret39399_end_;
                          }goto __ret39399_end_; __ret39399_end_: ;
                          }
                          ret39398;
                          }))
                          )->getMacroscopicParent();
                      
                    
                    //#line 508 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10aux::ref<au::edu::anu::mm::LocalExpansion> ret39407;
                    {
                        
                        //#line 512 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                        (macroLocalTranslations->
                           FMGL(raw))->__set(i039405, v39406);
                        
                        //#line 519 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                        ret39407 =
                          v39406;
                    }
                    ret39407;
                    }))
                    ;
                }
                }
                
                //#line 140 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.For_c
                {
                    x10_int shell;
                    for (
                         //#line 140 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                         shell =
                           ((x10_int)0);
                         ((shell) <= (numShells));
                         
                         //#line 140 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalAssign_c
                         shell =
                           ((x10_int) ((shell) + (((x10_int)1)))))
                    {
                        
                        //#line 141 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                        x10aux::ref<au::edu::anu::mm::LocalExpansion> localExpansion =
                          (__extension__ ({
                            
                            //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i039414 =
                              shell;
                            
                            //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10aux::ref<au::edu::anu::mm::LocalExpansion> ret39415;
                            
                            //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                            goto __ret39416; __ret39416: {
                            {
                                
                                //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                ret39415 =
                                  (macroLocalTranslations->
                                     FMGL(raw))->__apply(i039414);
                                
                                //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                goto __ret39416_end_;
                            }goto __ret39416_end_; __ret39416_end_: ;
                            }
                            ret39415;
                            }))
                            ;
                            
                        
                        //#line 142 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
                        x10aux::nullCheck(x10aux::nullCheck(topLevelBox)->
                                            FMGL(localExp))->transformAndAddToLocal(
                          localExpansion,
                          (__extension__ ({
                              
                              //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                              x10_int i039422 =
                                shell;
                              
                              //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                              x10aux::ref<au::edu::anu::mm::MultipoleExpansion> ret39423;
                              
                              //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                              goto __ret39424; __ret39424: {
                              {
                                  
                                  //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                  ret39423 =
                                    (macroMultipoles->
                                       FMGL(raw))->__apply(i039422);
                                  
                                  //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                  goto __ret39424_end_;
                              }goto __ret39424_end_; __ret39424_end_: ;
                              }
                              ret39423;
                              }))
                              );
                        }
                        }
                        
                }
                
                // captured environment
                x10_int numShells;
                x10aux::ref<x10::array::Array<x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > > > boxes;
                x10_int numTerms;
                x10_double size;
                
                x10aux::serialization_id_t _get_serialization_id() {
                    return _serialization_id;
                }
                
                void _serialize_body(x10aux::serialization_buffer &buf) {
                    buf.write(this->numShells);
                    buf.write(this->boxes);
                    buf.write(this->numTerms);
                    buf.write(this->size);
                }
                
                template<class __T> static x10aux::ref<__T> _deserialize(x10aux::deserialization_buffer &buf) {
                    au_edu_anu_mm_PeriodicFmm3d__closure__2* storage = x10aux::alloc<au_edu_anu_mm_PeriodicFmm3d__closure__2>();
                    buf.record_reference(x10aux::ref<au_edu_anu_mm_PeriodicFmm3d__closure__2>(storage));
                    x10_int that_numShells = buf.read<x10_int>();
                    x10aux::ref<x10::array::Array<x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > > > that_boxes = buf.read<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > > > >();
                    x10_int that_numTerms = buf.read<x10_int>();
                    x10_double that_size = buf.read<x10_double>();
                    x10aux::ref<au_edu_anu_mm_PeriodicFmm3d__closure__2> this_ = new (storage) au_edu_anu_mm_PeriodicFmm3d__closure__2(that_numShells, that_boxes, that_numTerms, that_size);
                    return this_;
                }
                
                au_edu_anu_mm_PeriodicFmm3d__closure__2(x10_int numShells, x10aux::ref<x10::array::Array<x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > > > boxes, x10_int numTerms, x10_double size) : numShells(numShells), boxes(boxes), numTerms(numTerms), size(size) { }
                
                static const x10aux::serialization_id_t _serialization_id;
                
                static const x10aux::RuntimeType* getRTT() { return x10aux::getRTT<x10::lang::VoidFun_0_0>(); }
                virtual const x10aux::RuntimeType *_type() const { return x10aux::getRTT<x10::lang::VoidFun_0_0>(); }
                
                x10aux::ref<x10::lang::String> toString() {
                    return x10aux::string_utils::lit(this->toNativeString());
                }
                
                const char* toNativeString() {
                    return "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10:96-145";
                }
                
                };
                
                #endif // AU_EDU_ANU_MM_PERIODICFMM3D__CLOSURE__2_CLOSURE
                #ifndef AU_EDU_ANU_MM_PERIODICFMM3D__CLOSURE__5_CLOSURE
#define AU_EDU_ANU_MM_PERIODICFMM3D__CLOSURE__5_CLOSURE
#include <x10/lang/Closure.h>
#include <x10/lang/VoidFun_0_0.h>
class au_edu_anu_mm_PeriodicFmm3d__closure__5 : public x10::lang::Closure {
    public:
    
    static x10::lang::VoidFun_0_0::itable<au_edu_anu_mm_PeriodicFmm3d__closure__5> _itable;
    static x10aux::itable_entry _itables[2];
    
    virtual x10aux::itable_entry* _getITables() { return _itables; }
    
    // closure body
    void __apply() {
        
        //#line 168 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
        au::edu::anu::chem::PointCharge remoteAtom40551 =  au::edu::anu::chem::PointCharge::_alloc();
        
        //#line 168 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10ConstructorCall_c
        (remoteAtom40551)->::au::edu::anu::chem::PointCharge::_constructor(
          offsetCentre40537,
          charge40536,
          x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
        
        //#line 169 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::FmmLeafBox> leafBox40552 = x10aux::class_cast<x10aux::ref<au::edu::anu::mm::FmmLeafBox> >(x10aux::nullCheck(lowestLevelBoxes)->x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> >::__apply(
                                                                                                                                  boxIndex40550));
        
        //#line 170 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
        x10aux::nullCheck(leafBox40552)->addAtom(remoteAtom40551);
    }
    
    // captured environment
    x10x::vector::Point3d offsetCentre40537;
    x10_double charge40536;
    x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > lowestLevelBoxes;
    x10aux::ref<x10::array::Point> boxIndex40550;
    
    x10aux::serialization_id_t _get_serialization_id() {
        return _serialization_id;
    }
    
    void _serialize_body(x10aux::serialization_buffer &buf) {
        buf.write(this->offsetCentre40537);
        buf.write(this->charge40536);
        buf.write(this->lowestLevelBoxes);
        buf.write(this->boxIndex40550);
    }
    
    template<class __T> static x10aux::ref<__T> _deserialize(x10aux::deserialization_buffer &buf) {
        au_edu_anu_mm_PeriodicFmm3d__closure__5* storage = x10aux::alloc<au_edu_anu_mm_PeriodicFmm3d__closure__5>();
        buf.record_reference(x10aux::ref<au_edu_anu_mm_PeriodicFmm3d__closure__5>(storage));
        x10x::vector::Point3d that_offsetCentre40537 = buf.read<x10x::vector::Point3d>();
        x10_double that_charge40536 = buf.read<x10_double>();
        x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > that_lowestLevelBoxes = buf.read<x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > >();
        x10aux::ref<x10::array::Point> that_boxIndex40550 = buf.read<x10aux::ref<x10::array::Point> >();
        x10aux::ref<au_edu_anu_mm_PeriodicFmm3d__closure__5> this_ = new (storage) au_edu_anu_mm_PeriodicFmm3d__closure__5(that_offsetCentre40537, that_charge40536, that_lowestLevelBoxes, that_boxIndex40550);
        return this_;
    }
    
    au_edu_anu_mm_PeriodicFmm3d__closure__5(x10x::vector::Point3d offsetCentre40537, x10_double charge40536, x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > lowestLevelBoxes, x10aux::ref<x10::array::Point> boxIndex40550) : offsetCentre40537(offsetCentre40537), charge40536(charge40536), lowestLevelBoxes(lowestLevelBoxes), boxIndex40550(boxIndex40550) { }
    
    static const x10aux::serialization_id_t _serialization_id;
    
    static const x10aux::RuntimeType* getRTT() { return x10aux::getRTT<x10::lang::VoidFun_0_0>(); }
    virtual const x10aux::RuntimeType *_type() const { return x10aux::getRTT<x10::lang::VoidFun_0_0>(); }
    
    x10aux::ref<x10::lang::String> toString() {
        return x10aux::string_utils::lit(this->toNativeString());
    }
    
    const char* toNativeString() {
        return "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10:167-171";
    }

};

#endif // AU_EDU_ANU_MM_PERIODICFMM3D__CLOSURE__5_CLOSURE
#ifndef AU_EDU_ANU_MM_PERIODICFMM3D__CLOSURE__4_CLOSURE
#define AU_EDU_ANU_MM_PERIODICFMM3D__CLOSURE__4_CLOSURE
#include <x10/lang/Closure.h>
#include <x10/lang/VoidFun_0_0.h>
class au_edu_anu_mm_PeriodicFmm3d__closure__4 : public x10::lang::Closure {
    public:
    
    static x10::lang::VoidFun_0_0::itable<au_edu_anu_mm_PeriodicFmm3d__closure__4> _itable;
    static x10aux::itable_entry _itables[2];
    
    virtual x10aux::itable_entry* _getITables() { return _itables; }
    
    // closure body
    void __apply() {
        
        //#line 158 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
        x10x::vector::Vector3d myDipole = x10x::vector::Vector3d::FMGL(NULL__get)();
        
        //#line 159 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > localAtoms =
          x10aux::nullCheck(atoms)->x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > >::__apply(
            p1);
        {
            
            //#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
            x10::lang::Runtime::ensureNotInAtomic();
            
            //#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::lang::FinishState> x10____var3 =
              x10::lang::Runtime::startFinish();
            {
                
                //#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<x10::lang::Throwable> throwable40680 =
                  X10_NULL;
                
                //#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.Try_c
                try {
                    
                    //#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.Try_c
                    try {
                        {
                            
                            //#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                            x10_int i24717max2471940554 =
                              ((x10_int) ((x10aux::nullCheck(localAtoms)->
                                             FMGL(size)) - (((x10_int)1))));
                            
                            //#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.For_c
                            {
                                x10_int i2471740555;
                                for (
                                     //#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                     i2471740555 = ((x10_int)0);
                                     ((i2471740555) <= (i24717max2471940554));
                                     
                                     //#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalAssign_c
                                     i2471740555 =
                                       ((x10_int) ((i2471740555) + (((x10_int)1)))))
                                {
                                    
                                    //#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                    x10_int i40556 =
                                      i2471740555;
                                    
                                    //#line 161 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                    x10aux::ref<au::edu::anu::chem::mm::MMAtom> atom40532 =
                                      (__extension__ ({
                                        
                                        //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                        x10_int i03943040533 =
                                          i40556;
                                        
                                        //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                        x10aux::ref<au::edu::anu::chem::mm::MMAtom> ret3943140534;
                                        
                                        //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                        goto __ret3943240535; __ret3943240535: {
                                        {
                                            
                                            //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                            ret3943140534 =
                                              (x10aux::nullCheck(localAtoms)->
                                                 FMGL(raw))->__apply(i03943040533);
                                            
                                            //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                            goto __ret3943240535_end_;
                                        }goto __ret3943240535_end_; __ret3943240535_end_: ;
                                        }
                                        ret3943140534;
                                        }))
                                        ;
                                        
                                    
                                    //#line 162 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                    x10_double charge40536 =
                                      x10aux::nullCheck(atom40532)->
                                        FMGL(charge);
                                    
                                    //#line 163 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                    x10x::vector::Point3d offsetCentre40537 =
                                      (__extension__ ({
                                        
                                        //#line 163 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                        x10x::vector::Point3d this3943940538 =
                                          x10aux::nullCheck(atom40532)->
                                            FMGL(centre);
                                        
                                        //#line 31 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                                        x10x::vector::Vector3d that3943840539 =
                                          offset;
                                        (__extension__ ({
                                            
                                            //#line 27 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                                            x10x::vector::Vector3d b3944040540 =
                                              that3943840539;
                                            (__extension__ ({
                                                
                                                //#line 28 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                                                x10x::vector::Point3d alloc242373944140541 =
                                                  
                                                x10x::vector::Point3d::_alloc();
                                                
                                                //#line 28 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10ConstructorCall_c
                                                (alloc242373944140541)->::x10x::vector::Point3d::_constructor(
                                                  ((this3943940538->
                                                      FMGL(i)) + ((__extension__ ({
                                                      b3944040540->
                                                        FMGL(i);
                                                  }))
                                                  )),
                                                  ((this3943940538->
                                                      FMGL(j)) + ((__extension__ ({
                                                      b3944040540->
                                                        FMGL(j);
                                                  }))
                                                  )),
                                                  ((this3943940538->
                                                      FMGL(k)) + ((__extension__ ({
                                                      b3944040540->
                                                        FMGL(k);
                                                  }))
                                                  )),
                                                  x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
                                                alloc242373944140541;
                                            }))
                                            ;
                                        }))
                                        ;
                                    }))
                                    ;
                                    
                                    //#line 164 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalAssign_c
                                    myDipole =
                                      (__extension__ ({
                                        
                                        //#line 33 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
                                        x10x::vector::Vector3d that3944640542 =
                                          (__extension__ ({
                                            
                                            //#line 164 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                            x10x::vector::Vector3d this3944340543 =
                                              (__extension__ ({
                                                
                                                //#line 164 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                                x10x::vector::Vector3d alloc2466540544 =
                                                  
                                                x10x::vector::Vector3d::_alloc();
                                                
                                                //#line 164 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10ConstructorCall_c
                                                (alloc2466540544)->::x10x::vector::Vector3d::_constructor(
                                                  x10aux::class_cast_unchecked<x10aux::ref<x10x::vector::Tuple3d> >(offsetCentre40537),
                                                  x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
                                                alloc2466540544;
                                            }))
                                            ;
                                            
                                            //#line 72 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
                                            x10_double that3944240545 =
                                              charge40536;
                                            (__extension__ ({
                                                
                                                //#line 81 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
                                                x10_double c3944440546 =
                                                  that3944240545;
                                                (__extension__ ({
                                                    
                                                    //#line 82 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
                                                    x10x::vector::Vector3d alloc253393944540547 =
                                                      
                                                    x10x::vector::Vector3d::_alloc();
                                                    
                                                    //#line 82 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10ConstructorCall_c
                                                    (alloc253393944540547)->::x10x::vector::Vector3d::_constructor(
                                                      ((this3944340543->
                                                          FMGL(i)) * (c3944440546)),
                                                      ((this3944340543->
                                                          FMGL(j)) * (c3944440546)),
                                                      ((this3944340543->
                                                          FMGL(k)) * (c3944440546)),
                                                      x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
                                                    alloc253393944540547;
                                                }))
                                                ;
                                            }))
                                            ;
                                        }))
                                        ;
                                        (__extension__ ({
                                            
                                            //#line 37 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
                                            x10x::vector::Vector3d b3944740548 =
                                              that3944640542;
                                            (__extension__ ({
                                                
                                                //#line 38 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
                                                x10x::vector::Vector3d alloc253363944840549 =
                                                  
                                                x10x::vector::Vector3d::_alloc();
                                                
                                                //#line 38 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10ConstructorCall_c
                                                (alloc253363944840549)->::x10x::vector::Vector3d::_constructor(
                                                  ((myDipole->
                                                      FMGL(i)) + ((__extension__ ({
                                                      b3944740548->
                                                        FMGL(i);
                                                  }))
                                                  )),
                                                  ((myDipole->
                                                      FMGL(j)) + ((__extension__ ({
                                                      b3944740548->
                                                        FMGL(j);
                                                  }))
                                                  )),
                                                  ((myDipole->
                                                      FMGL(k)) + ((__extension__ ({
                                                      b3944740548->
                                                        FMGL(k);
                                                  }))
                                                  )),
                                                  x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
                                                alloc253363944840549;
                                            }))
                                            ;
                                        }))
                                        ;
                                    }))
                                    ;
                                    
                                    //#line 165 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                    x10aux::ref<x10::array::Point> boxIndex40550 =
                                      au::edu::anu::mm::Fmm3d::getLowestLevelBoxIndex(
                                        offsetCentre40537,
                                        lowestLevelDim,
                                        size);
                                    
                                    //#line 167 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
                                    x10::lang::Runtime::runAsync(
                                      x10aux::nullCheck(x10aux::nullCheck(lowestLevelBoxes)->
                                                          FMGL(dist))->__apply(
                                        x10aux::nullCheck(boxIndex40550)->x10::array::Point::__apply(
                                          ((x10_int)0)),
                                        x10aux::nullCheck(boxIndex40550)->x10::array::Point::__apply(
                                          ((x10_int)1)),
                                        x10aux::nullCheck(boxIndex40550)->x10::array::Point::__apply(
                                          ((x10_int)2))),
                                      x10aux::ref<x10::lang::VoidFun_0_0>(x10aux::ref<au_edu_anu_mm_PeriodicFmm3d__closure__5>(new (x10aux::alloc<x10::lang::VoidFun_0_0>(sizeof(au_edu_anu_mm_PeriodicFmm3d__closure__5)))au_edu_anu_mm_PeriodicFmm3d__closure__5(offsetCentre40537, charge40536, lowestLevelBoxes, boxIndex40550))));
                                    }
                                }
                                
                            }
                        }
                        catch (x10aux::__ref& __ref__133) {
                            x10aux::ref<x10::lang::Throwable>& __exc__ref__133 = (x10aux::ref<x10::lang::Throwable>&)__ref__133;
                            if (true) {
                                x10aux::ref<x10::lang::Throwable> __lowerer__var__5__ =
                                  static_cast<x10aux::ref<x10::lang::Throwable> >(__exc__ref__133);
                                {
                                    
                                    //#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
                                    x10::lang::Runtime::pushException(
                                      __lowerer__var__5__);
                                    
                                    //#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.Throw_c
                                    x10aux::throwException(x10aux::nullCheck(x10::lang::RuntimeException::_make()));
                                }
                            } else
                            throw;
                        }
                    
                    //#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
                    x10::compiler::Finalization::plausibleThrow();
                    }
                    catch (x10aux::__ref& __ref__134) {
                        x10aux::ref<x10::lang::Throwable>& __exc__ref__134 = (x10aux::ref<x10::lang::Throwable>&)__ref__134;
                        if (true) {
                            x10aux::ref<x10::lang::Throwable> formal40681 =
                              static_cast<x10aux::ref<x10::lang::Throwable> >(__exc__ref__134);
                            {
                                
                                //#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalAssign_c
                                throwable40680 =
                                  formal40681;
                            }
                        } else
                        throw;
                    }
                
                //#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10If_c
                if ((!x10aux::struct_equals(X10_NULL,
                                            throwable40680)))
                {
                    
                    //#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10If_c
                    if (x10aux::instanceof<x10aux::ref<x10::compiler::Abort> >(throwable40680))
                    {
                        
                        //#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.Throw_c
                        x10aux::throwException(x10aux::nullCheck(throwable40680));
                    }
                    
                }
                
                //#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10If_c
                if (true) {
                    
                    //#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
                    x10::lang::Runtime::stopFinish(
                      x10____var3);
                }
                
                //#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10If_c
                if ((!x10aux::struct_equals(X10_NULL,
                                            throwable40680)))
                {
                    
                    //#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10If_c
                    if (!(x10aux::instanceof<x10aux::ref<x10::compiler::Finalization> >(throwable40680)))
                    {
                        
                        //#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.Throw_c
                        x10aux::throwException(x10aux::nullCheck(throwable40680));
                    }
                    
                }
                }
            }
        
        //#line 173 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
        x10::lang::Runtime::makeOffer<x10x::vector::Vector3d >(
          myDipole);
        }
        
        // captured environment
        x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > atoms;
        x10aux::ref<x10::array::Point> p1;
        x10x::vector::Vector3d offset;
        x10_int lowestLevelDim;
        x10_double size;
        x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > lowestLevelBoxes;
        
        x10aux::serialization_id_t _get_serialization_id() {
            return _serialization_id;
        }
        
        void _serialize_body(x10aux::serialization_buffer &buf) {
            buf.write(this->atoms);
            buf.write(this->p1);
            buf.write(this->offset);
            buf.write(this->lowestLevelDim);
            buf.write(this->size);
            buf.write(this->lowestLevelBoxes);
        }
        
        template<class __T> static x10aux::ref<__T> _deserialize(x10aux::deserialization_buffer &buf) {
            au_edu_anu_mm_PeriodicFmm3d__closure__4* storage = x10aux::alloc<au_edu_anu_mm_PeriodicFmm3d__closure__4>();
            buf.record_reference(x10aux::ref<au_edu_anu_mm_PeriodicFmm3d__closure__4>(storage));
            x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > that_atoms = buf.read<x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > >();
            x10aux::ref<x10::array::Point> that_p1 = buf.read<x10aux::ref<x10::array::Point> >();
            x10x::vector::Vector3d that_offset = buf.read<x10x::vector::Vector3d>();
            x10_int that_lowestLevelDim = buf.read<x10_int>();
            x10_double that_size = buf.read<x10_double>();
            x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > that_lowestLevelBoxes = buf.read<x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > >();
            x10aux::ref<au_edu_anu_mm_PeriodicFmm3d__closure__4> this_ = new (storage) au_edu_anu_mm_PeriodicFmm3d__closure__4(that_atoms, that_p1, that_offset, that_lowestLevelDim, that_size, that_lowestLevelBoxes);
            return this_;
        }
        
        au_edu_anu_mm_PeriodicFmm3d__closure__4(x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > atoms, x10aux::ref<x10::array::Point> p1, x10x::vector::Vector3d offset, x10_int lowestLevelDim, x10_double size, x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > lowestLevelBoxes) : atoms(atoms), p1(p1), offset(offset), lowestLevelDim(lowestLevelDim), size(size), lowestLevelBoxes(lowestLevelBoxes) { }
        
        static const x10aux::serialization_id_t _serialization_id;
        
        static const x10aux::RuntimeType* getRTT() { return x10aux::getRTT<x10::lang::VoidFun_0_0>(); }
        virtual const x10aux::RuntimeType *_type() const { return x10aux::getRTT<x10::lang::VoidFun_0_0>(); }
        
        x10aux::ref<x10::lang::String> toString() {
            return x10aux::string_utils::lit(this->toNativeString());
        }
        
        const char* toNativeString() {
            return "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10:157-174";
        }
    
    };
    
    #endif // AU_EDU_ANU_MM_PERIODICFMM3D__CLOSURE__4_CLOSURE
    #ifndef AU_EDU_ANU_MM_PERIODICFMM3D__CLOSURE__3_CLOSURE
#define AU_EDU_ANU_MM_PERIODICFMM3D__CLOSURE__3_CLOSURE
#include <x10/lang/Closure.h>
#include <x10/lang/VoidFun_0_0.h>
class au_edu_anu_mm_PeriodicFmm3d__closure__3 : public x10::lang::Closure {
    public:
    
    static x10::lang::VoidFun_0_0::itable<au_edu_anu_mm_PeriodicFmm3d__closure__3> _itable;
    static x10aux::itable_entry _itables[2];
    
    virtual x10aux::itable_entry* _getITables() { return _itables; }
    
    // closure body
    void __apply() {
        
        //#line 157 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.For_c
        {
            x10aux::ref<x10::lang::Iterator<x10aux::ref<x10::array::Point> > > p140641;
            for (
                 //#line 157 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                 p140641 = x10aux::nullCheck(x10aux::nullCheck(x10aux::nullCheck(__lowerer__var__6__)->restriction(
                                                                 x10::lang::Place::_make(x10aux::here)))->
                                               FMGL(region))->iterator();
                 x10::lang::Iterator<x10aux::ref<x10::array::Point> >::hasNext(x10aux::nullCheck(p140641));
                 ) {
                
                //#line 157 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<x10::array::Point> p1 =
                  x10::lang::Iterator<x10aux::ref<x10::array::Point> >::next(x10aux::nullCheck(p140641));
                
                //#line 157 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
                x10::lang::Runtime::runAsync(x10::lang::Place::_make(x10aux::here),
                                             x10aux::ref<x10::lang::VoidFun_0_0>(x10aux::ref<au_edu_anu_mm_PeriodicFmm3d__closure__4>(new (x10aux::alloc<x10::lang::VoidFun_0_0>(sizeof(au_edu_anu_mm_PeriodicFmm3d__closure__4)))au_edu_anu_mm_PeriodicFmm3d__closure__4(atoms, p1, offset, lowestLevelDim, size, lowestLevelBoxes))));
            }
        }
        
    }
    
    // captured environment
    x10aux::ref<x10::array::Dist> __lowerer__var__6__;
    x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > atoms;
    x10x::vector::Vector3d offset;
    x10_int lowestLevelDim;
    x10_double size;
    x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > lowestLevelBoxes;
    
    x10aux::serialization_id_t _get_serialization_id() {
        return _serialization_id;
    }
    
    void _serialize_body(x10aux::serialization_buffer &buf) {
        buf.write(this->__lowerer__var__6__);
        buf.write(this->atoms);
        buf.write(this->offset);
        buf.write(this->lowestLevelDim);
        buf.write(this->size);
        buf.write(this->lowestLevelBoxes);
    }
    
    template<class __T> static x10aux::ref<__T> _deserialize(x10aux::deserialization_buffer &buf) {
        au_edu_anu_mm_PeriodicFmm3d__closure__3* storage = x10aux::alloc<au_edu_anu_mm_PeriodicFmm3d__closure__3>();
        buf.record_reference(x10aux::ref<au_edu_anu_mm_PeriodicFmm3d__closure__3>(storage));
        x10aux::ref<x10::array::Dist> that___lowerer__var__6__ = buf.read<x10aux::ref<x10::array::Dist> >();
        x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > that_atoms = buf.read<x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > >();
        x10x::vector::Vector3d that_offset = buf.read<x10x::vector::Vector3d>();
        x10_int that_lowestLevelDim = buf.read<x10_int>();
        x10_double that_size = buf.read<x10_double>();
        x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > that_lowestLevelBoxes = buf.read<x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > >();
        x10aux::ref<au_edu_anu_mm_PeriodicFmm3d__closure__3> this_ = new (storage) au_edu_anu_mm_PeriodicFmm3d__closure__3(that___lowerer__var__6__, that_atoms, that_offset, that_lowestLevelDim, that_size, that_lowestLevelBoxes);
        return this_;
    }
    
    au_edu_anu_mm_PeriodicFmm3d__closure__3(x10aux::ref<x10::array::Dist> __lowerer__var__6__, x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > atoms, x10x::vector::Vector3d offset, x10_int lowestLevelDim, x10_double size, x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > lowestLevelBoxes) : __lowerer__var__6__(__lowerer__var__6__), atoms(atoms), offset(offset), lowestLevelDim(lowestLevelDim), size(size), lowestLevelBoxes(lowestLevelBoxes) { }
    
    static const x10aux::serialization_id_t _serialization_id;
    
    static const x10aux::RuntimeType* getRTT() { return x10aux::getRTT<x10::lang::VoidFun_0_0>(); }
    virtual const x10aux::RuntimeType *_type() const { return x10aux::getRTT<x10::lang::VoidFun_0_0>(); }
    
    x10aux::ref<x10::lang::String> toString() {
        return x10aux::string_utils::lit(this->toNativeString());
    }
    
    const char* toNativeString() {
        return "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10:157-174";
    }

};

#endif // AU_EDU_ANU_MM_PERIODICFMM3D__CLOSURE__3_CLOSURE
#ifndef AU_EDU_ANU_MM_PERIODICFMM3D__CLOSURE__7_CLOSURE
#define AU_EDU_ANU_MM_PERIODICFMM3D__CLOSURE__7_CLOSURE
#include <x10/lang/Closure.h>
#include <x10/lang/VoidFun_0_0.h>
class au_edu_anu_mm_PeriodicFmm3d__closure__7 : public x10::lang::Closure {
    public:
    
    static x10::lang::VoidFun_0_0::itable<au_edu_anu_mm_PeriodicFmm3d__closure__7> _itable;
    static x10aux::itable_entry _itables[2];
    
    virtual x10aux::itable_entry* _getITables() { return _itables; }
    
    // closure body
    void __apply() {
        
        //#line 184 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::FmmLeafBox> box = x10aux::class_cast<x10aux::ref<au::edu::anu::mm::FmmLeafBox> >(x10aux::nullCheck(lowestLevelBoxes)->x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> >::__apply(
                                                                                                                         boxIndex));
        
        //#line 185 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10If_c
        if ((x10aux::struct_equals(x10aux::nullCheck(x10aux::nullCheck(box)->
                                                       FMGL(atoms))->size(),
                                   ((x10_int)0)))) {
            
            //#line 186 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
            x10aux::nullCheck(lowestLevelBoxes)->x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> >::__set(
              boxIndex,
              x10aux::class_cast_unchecked<x10aux::ref<au::edu::anu::mm::FmmBox> >(X10_NULL));
        }
        
    }
    
    // captured environment
    x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > lowestLevelBoxes;
    x10aux::ref<x10::array::Point> boxIndex;
    
    x10aux::serialization_id_t _get_serialization_id() {
        return _serialization_id;
    }
    
    void _serialize_body(x10aux::serialization_buffer &buf) {
        buf.write(this->lowestLevelBoxes);
        buf.write(this->boxIndex);
    }
    
    template<class __T> static x10aux::ref<__T> _deserialize(x10aux::deserialization_buffer &buf) {
        au_edu_anu_mm_PeriodicFmm3d__closure__7* storage = x10aux::alloc<au_edu_anu_mm_PeriodicFmm3d__closure__7>();
        buf.record_reference(x10aux::ref<au_edu_anu_mm_PeriodicFmm3d__closure__7>(storage));
        x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > that_lowestLevelBoxes = buf.read<x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > >();
        x10aux::ref<x10::array::Point> that_boxIndex = buf.read<x10aux::ref<x10::array::Point> >();
        x10aux::ref<au_edu_anu_mm_PeriodicFmm3d__closure__7> this_ = new (storage) au_edu_anu_mm_PeriodicFmm3d__closure__7(that_lowestLevelBoxes, that_boxIndex);
        return this_;
    }
    
    au_edu_anu_mm_PeriodicFmm3d__closure__7(x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > lowestLevelBoxes, x10aux::ref<x10::array::Point> boxIndex) : lowestLevelBoxes(lowestLevelBoxes), boxIndex(boxIndex) { }
    
    static const x10aux::serialization_id_t _serialization_id;
    
    static const x10aux::RuntimeType* getRTT() { return x10aux::getRTT<x10::lang::VoidFun_0_0>(); }
    virtual const x10aux::RuntimeType *_type() const { return x10aux::getRTT<x10::lang::VoidFun_0_0>(); }
    
    x10aux::ref<x10::lang::String> toString() {
        return x10aux::string_utils::lit(this->toNativeString());
    }
    
    const char* toNativeString() {
        return "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10:183-188";
    }

};

#endif // AU_EDU_ANU_MM_PERIODICFMM3D__CLOSURE__7_CLOSURE
#ifndef AU_EDU_ANU_MM_PERIODICFMM3D__CLOSURE__6_CLOSURE
#define AU_EDU_ANU_MM_PERIODICFMM3D__CLOSURE__6_CLOSURE
#include <x10/lang/Closure.h>
#include <x10/lang/VoidFun_0_0.h>
class au_edu_anu_mm_PeriodicFmm3d__closure__6 : public x10::lang::Closure {
    public:
    
    static x10::lang::VoidFun_0_0::itable<au_edu_anu_mm_PeriodicFmm3d__closure__6> _itable;
    static x10aux::itable_entry _itables[2];
    
    virtual x10aux::itable_entry* _getITables() { return _itables; }
    
    // closure body
    void __apply() {
        
        //#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.For_c
        {
            x10aux::ref<x10::lang::Iterator<x10aux::ref<x10::array::Point> > > boxIndex40653;
            for (
                 //#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                 boxIndex40653 = x10aux::nullCheck(x10aux::nullCheck(x10aux::nullCheck(__lowerer__var__9__)->restriction(
                                                                       x10::lang::Place::_make(x10aux::here)))->
                                                     FMGL(region))->iterator();
                 x10::lang::Iterator<x10aux::ref<x10::array::Point> >::hasNext(x10aux::nullCheck(boxIndex40653));
                 ) {
                
                //#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<x10::array::Point> boxIndex =
                  x10::lang::Iterator<x10aux::ref<x10::array::Point> >::next(x10aux::nullCheck(boxIndex40653));
                
                //#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
                x10::lang::Runtime::runAsync(x10::lang::Place::_make(x10aux::here),
                                             x10aux::ref<x10::lang::VoidFun_0_0>(x10aux::ref<au_edu_anu_mm_PeriodicFmm3d__closure__7>(new (x10aux::alloc<x10::lang::VoidFun_0_0>(sizeof(au_edu_anu_mm_PeriodicFmm3d__closure__7)))au_edu_anu_mm_PeriodicFmm3d__closure__7(lowestLevelBoxes, boxIndex))));
            }
        }
        
    }
    
    // captured environment
    x10aux::ref<x10::array::Dist> __lowerer__var__9__;
    x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > lowestLevelBoxes;
    
    x10aux::serialization_id_t _get_serialization_id() {
        return _serialization_id;
    }
    
    void _serialize_body(x10aux::serialization_buffer &buf) {
        buf.write(this->__lowerer__var__9__);
        buf.write(this->lowestLevelBoxes);
    }
    
    template<class __T> static x10aux::ref<__T> _deserialize(x10aux::deserialization_buffer &buf) {
        au_edu_anu_mm_PeriodicFmm3d__closure__6* storage = x10aux::alloc<au_edu_anu_mm_PeriodicFmm3d__closure__6>();
        buf.record_reference(x10aux::ref<au_edu_anu_mm_PeriodicFmm3d__closure__6>(storage));
        x10aux::ref<x10::array::Dist> that___lowerer__var__9__ = buf.read<x10aux::ref<x10::array::Dist> >();
        x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > that_lowestLevelBoxes = buf.read<x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > >();
        x10aux::ref<au_edu_anu_mm_PeriodicFmm3d__closure__6> this_ = new (storage) au_edu_anu_mm_PeriodicFmm3d__closure__6(that___lowerer__var__9__, that_lowestLevelBoxes);
        return this_;
    }
    
    au_edu_anu_mm_PeriodicFmm3d__closure__6(x10aux::ref<x10::array::Dist> __lowerer__var__9__, x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > lowestLevelBoxes) : __lowerer__var__9__(__lowerer__var__9__), lowestLevelBoxes(lowestLevelBoxes) { }
    
    static const x10aux::serialization_id_t _serialization_id;
    
    static const x10aux::RuntimeType* getRTT() { return x10aux::getRTT<x10::lang::VoidFun_0_0>(); }
    virtual const x10aux::RuntimeType *_type() const { return x10aux::getRTT<x10::lang::VoidFun_0_0>(); }
    
    x10aux::ref<x10::lang::String> toString() {
        return x10aux::string_utils::lit(this->toNativeString());
    }
    
    const char* toNativeString() {
        return "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10:183-188";
    }

};

#endif // AU_EDU_ANU_MM_PERIODICFMM3D__CLOSURE__6_CLOSURE
#ifndef AU_EDU_ANU_MM_PERIODICFMM3D__CLOSURE__8_CLOSURE
#define AU_EDU_ANU_MM_PERIODICFMM3D__CLOSURE__8_CLOSURE
#include <x10/lang/Closure.h>
#include <x10/lang/VoidFun_0_0.h>
class au_edu_anu_mm_PeriodicFmm3d__closure__8 : public x10::lang::Closure {
    public:
    
    static x10::lang::VoidFun_0_0::itable<au_edu_anu_mm_PeriodicFmm3d__closure__8> _itable;
    static x10aux::itable_entry _itables[2];
    
    virtual x10aux::itable_entry* _getITables() { return _itables; }
    
    // closure body
    void __apply() {
        
        //#line 197 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
        au::edu::anu::chem::PointCharge remoteAtom =  au::edu::anu::chem::PointCharge::_alloc();
        
        //#line 197 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10ConstructorCall_c
        (remoteAtom)->::au::edu::anu::chem::PointCharge::_constructor(offsetCentre,
                                                                      charge,
                                                                      x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
        
        //#line 198 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::FmmLeafBox> leafBox = x10aux::class_cast<x10aux::ref<au::edu::anu::mm::FmmLeafBox> >(x10aux::nullCheck(lowestLevelBoxes)->x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> >::__apply(
                                                                                                                             boxIndex));
        
        //#line 199 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
        x10x::vector::Vector3d boxLocation = (__extension__ ({
            
            //#line 199 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
            x10x::vector::Point3d this40411 = x10aux::nullCheck(leafBox)->getCentre(
                                                size);
            
            //#line 49 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
            x10x::vector::Point3d b40409 = offsetCentre;
            (__extension__ ({
                
                //#line 50 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                x10x::vector::Vector3d alloc2423940410 =
                  
                x10x::vector::Vector3d::_alloc();
                
                //#line 50 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10ConstructorCall_c
                (alloc2423940410)->::x10x::vector::Vector3d::_constructor(
                  ((this40411->
                      FMGL(i)) - (b40409->
                                    FMGL(i))),
                  ((this40411->
                      FMGL(j)) - (b40409->
                                    FMGL(j))),
                  ((this40411->
                      FMGL(k)) - (b40409->
                                    FMGL(k))),
                  x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
                alloc2423940410;
            }))
            ;
        }))
        ;
        
        //#line 200 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::MultipoleExpansion> atomExpansion =
          au::edu::anu::mm::MultipoleExpansion::getOlm(
            charge,
            x10aux::class_cast_unchecked<x10aux::ref<x10x::vector::Tuple3d> >(boxLocation),
            numTerms);
        
        //#line 202 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
        x10aux::nullCheck(leafBox)->addAtom(
          remoteAtom);
        
        //#line 203 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
        x10aux::nullCheck(x10aux::nullCheck(leafBox)->
                            FMGL(multipoleExp))->add(
          x10aux::class_cast_unchecked<x10aux::ref<au::edu::anu::mm::Expansion> >(atomExpansion));
    }
    
    // captured environment
    x10x::vector::Point3d offsetCentre;
    x10_double charge;
    x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > lowestLevelBoxes;
    x10aux::ref<x10::array::Point> boxIndex;
    x10_double size;
    x10_int numTerms;
    
    x10aux::serialization_id_t _get_serialization_id() {
        return _serialization_id;
    }
    
    void _serialize_body(x10aux::serialization_buffer &buf) {
        buf.write(this->offsetCentre);
        buf.write(this->charge);
        buf.write(this->lowestLevelBoxes);
        buf.write(this->boxIndex);
        buf.write(this->size);
        buf.write(this->numTerms);
    }
    
    template<class __T> static x10aux::ref<__T> _deserialize(x10aux::deserialization_buffer &buf) {
        au_edu_anu_mm_PeriodicFmm3d__closure__8* storage = x10aux::alloc<au_edu_anu_mm_PeriodicFmm3d__closure__8>();
        buf.record_reference(x10aux::ref<au_edu_anu_mm_PeriodicFmm3d__closure__8>(storage));
        x10x::vector::Point3d that_offsetCentre = buf.read<x10x::vector::Point3d>();
        x10_double that_charge = buf.read<x10_double>();
        x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > that_lowestLevelBoxes = buf.read<x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > >();
        x10aux::ref<x10::array::Point> that_boxIndex = buf.read<x10aux::ref<x10::array::Point> >();
        x10_double that_size = buf.read<x10_double>();
        x10_int that_numTerms = buf.read<x10_int>();
        x10aux::ref<au_edu_anu_mm_PeriodicFmm3d__closure__8> this_ = new (storage) au_edu_anu_mm_PeriodicFmm3d__closure__8(that_offsetCentre, that_charge, that_lowestLevelBoxes, that_boxIndex, that_size, that_numTerms);
        return this_;
    }
    
    au_edu_anu_mm_PeriodicFmm3d__closure__8(x10x::vector::Point3d offsetCentre, x10_double charge, x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > lowestLevelBoxes, x10aux::ref<x10::array::Point> boxIndex, x10_double size, x10_int numTerms) : offsetCentre(offsetCentre), charge(charge), lowestLevelBoxes(lowestLevelBoxes), boxIndex(boxIndex), size(size), numTerms(numTerms) { }
    
    static const x10aux::serialization_id_t _serialization_id;
    
    static const x10aux::RuntimeType* getRTT() { return x10aux::getRTT<x10::lang::VoidFun_0_0>(); }
    virtual const x10aux::RuntimeType *_type() const { return x10aux::getRTT<x10::lang::VoidFun_0_0>(); }
    
    x10aux::ref<x10::lang::String> toString() {
        return x10aux::string_utils::lit(this->toNativeString());
    }
    
    const char* toNativeString() {
        return "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10:196-204";
    }

};

#endif // AU_EDU_ANU_MM_PERIODICFMM3D__CLOSURE__8_CLOSURE
#ifndef AU_EDU_ANU_MM_PERIODICFMM3D__CLOSURE__10_CLOSURE
#define AU_EDU_ANU_MM_PERIODICFMM3D__CLOSURE__10_CLOSURE
#include <x10/lang/Closure.h>
#include <x10/lang/VoidFun_0_0.h>
class au_edu_anu_mm_PeriodicFmm3d__closure__10 : public x10::lang::Closure {
    public:
    
    static x10::lang::VoidFun_0_0::itable<au_edu_anu_mm_PeriodicFmm3d__closure__10> _itable;
    static x10aux::itable_entry _itables[2];
    
    virtual x10aux::itable_entry* _getITables() { return _itables; }
    
    // closure body
    void __apply() {
        
        //#line 265 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::LocallyEssentialTree> myLET = x10aux::nullCheck(locallyEssentialTrees)->x10::array::DistArray<x10aux::ref<au::edu::anu::mm::LocallyEssentialTree> >::__apply(
                                                                      p1);
        
        //#line 266 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > cachedAtoms =
          x10aux::nullCheck(myLET)->
            FMGL(cachedAtoms);
        
        //#line 267 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
        x10_double thisPlaceEnergy = 0.0;
        
        //#line 268 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.For_c
        {
            x10aux::ref<x10::lang::Iterator<x10aux::ref<x10::array::Point> > > id24813;
            for (
                 //#line 268 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                 id24813 = x10aux::nullCheck(x10aux::nullCheck(x10aux::nullCheck(lowestLevelBoxes)->
                                                                 FMGL(dist))->__apply(
                                               x10::lang::Place::_make(x10aux::here)))->iterator();
                 x10::lang::Iterator<x10aux::ref<x10::array::Point> >::hasNext(x10aux::nullCheck(id24813));
                 ) {
                
                //#line 268 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<x10::array::Point> id412 =
                  x10::lang::Iterator<x10aux::ref<x10::array::Point> >::next(x10aux::nullCheck(id24813));
                
                //#line 268 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                x10_int x1 = x10aux::nullCheck(id412)->x10::array::Point::__apply(
                               ((x10_int)0));
                
                //#line 268 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                x10_int y1 = x10aux::nullCheck(id412)->x10::array::Point::__apply(
                               ((x10_int)1));
                
                //#line 268 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                x10_int z1 = x10aux::nullCheck(id412)->x10::array::Point::__apply(
                               ((x10_int)2));
                
                //#line 269 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<au::edu::anu::mm::FmmLeafBox> box1 =
                  x10aux::class_cast<x10aux::ref<au::edu::anu::mm::FmmLeafBox> >(x10aux::nullCheck(lowestLevelBoxes)->x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> >::__apply(
                                                                                   x1,
                                                                                   y1,
                                                                                   z1));
                
                //#line 270 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10If_c
                if ((!x10aux::struct_equals(box1,
                                            X10_NULL)))
                {
                    
                    //#line 271 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                    x10_int i24749max2475140614 =
                      ((x10_int) ((x10aux::nullCheck(x10aux::nullCheck(box1)->
                                                       FMGL(atoms))->size()) - (((x10_int)1))));
                    
                    //#line 271 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.For_c
                    {
                        x10_int i2474940615;
                        for (
                             //#line 271 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                             i2474940615 =
                               ((x10_int)0);
                             ((i2474940615) <= (i24749max2475140614));
                             
                             //#line 271 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalAssign_c
                             i2474940615 =
                               ((x10_int) ((i2474940615) + (((x10_int)1)))))
                        {
                            
                            //#line 271 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                            x10_int atomIndex140616 =
                              i2474940615;
                            
                            //#line 273 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                            au::edu::anu::chem::PointCharge atom140570 =
                              x10aux::nullCheck(x10aux::nullCheck(box1)->
                                                  FMGL(atoms))->__apply(
                                atomIndex140616);
                            
                            //#line 274 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                            x10_int i24733max2473540567 =
                              ((x10_int) ((atomIndex140616) - (((x10_int)1))));
                            
                            //#line 274 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.For_c
                            {
                                x10_int i2473340568;
                                for (
                                     //#line 274 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                     i2473340568 =
                                       ((x10_int)0);
                                     ((i2473340568) <= (i24733max2473540567));
                                     
                                     //#line 274 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalAssign_c
                                     i2473340568 =
                                       ((x10_int) ((i2473340568) + (((x10_int)1)))))
                                {
                                    
                                    //#line 274 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                    x10_int sameBoxAtomIndex40569 =
                                      i2473340568;
                                    
                                    //#line 275 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                    au::edu::anu::chem::PointCharge sameBoxAtom40557 =
                                      x10aux::nullCheck(x10aux::nullCheck(box1)->
                                                          FMGL(atoms))->__apply(
                                        sameBoxAtomIndex40569);
                                    
                                    //#line 276 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                    x10_double pairEnergy40558 =
                                      ((((atom140570->
                                            FMGL(charge)) * (sameBoxAtom40557->
                                                               FMGL(charge)))) / ((__extension__ ({
                                        
                                        //#line 276 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                        x10x::vector::Point3d this4047340559 =
                                          atom140570->
                                            FMGL(centre);
                                        
                                        //#line 66 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                                        x10x::vector::Point3d b4047240560 =
                                          sameBoxAtom40557->
                                            FMGL(centre);
                                        x10aux::math_utils::sqrt((__extension__ ({
                                            
                                            //#line 57 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                                            x10x::vector::Point3d b4047440561 =
                                              b4047240560;
                                            
                                            //#line 57 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                                            x10_double ret4047840562;
                                            {
                                                
                                                //#line 58 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                                                x10_double di4047540563 =
                                                  ((this4047340559->
                                                      FMGL(i)) - (b4047440561->
                                                                    FMGL(i)));
                                                
                                                //#line 59 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                                                x10_double dj4047640564 =
                                                  ((this4047340559->
                                                      FMGL(j)) - (b4047440561->
                                                                    FMGL(j)));
                                                
                                                //#line 60 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                                                x10_double dk4047740565 =
                                                  ((this4047340559->
                                                      FMGL(k)) - (b4047440561->
                                                                    FMGL(k)));
                                                
                                                //#line 61 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalAssign_c
                                                ret4047840562 =
                                                  ((((((di4047540563) * (di4047540563))) + (((dj4047640564) * (dj4047640564))))) + (((dk4047740565) * (dk4047740565))));
                                            }
                                            ret4047840562;
                                        }))
                                        );
                                    }))
                                    ));
                                    
                                    //#line 277 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalAssign_c
                                    thisPlaceEnergy =
                                      ((thisPlaceEnergy) + (pairEnergy40558));
                                }
                            }
                            
                        }
                    }
                    
                    //#line 282 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                    x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Point> > > uList =
                      x10aux::nullCheck(box1)->getUList();
                    
                    //#line 283 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                    x10_int i24797max2479940618 =
                      ((x10_int) ((x10aux::nullCheck(uList)->
                                     FMGL(size)) - (((x10_int)1))));
                    
                    //#line 283 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.For_c
                    {
                        x10_int i2479740619;
                        for (
                             //#line 283 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                             i2479740619 =
                               ((x10_int)0);
                             ((i2479740619) <= (i24797max2479940618));
                             
                             //#line 283 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalAssign_c
                             i2479740619 =
                               ((x10_int) ((i2479740619) + (((x10_int)1)))))
                        {
                            
                            //#line 283 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                            x10_int p40620 =
                              i2479740619;
                            
                            //#line 284 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                            x10aux::ref<x10::array::Point> boxIndex240604 =
                              (__extension__ ({
                                
                                //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10_int i04048040605 =
                                  p40620;
                                
                                //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10aux::ref<x10::array::Point> ret4048140606;
                                
                                //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                goto __ret4048240607; __ret4048240607: {
                                {
                                    
                                    //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                    ret4048140606 =
                                      (x10aux::nullCheck(uList)->
                                         FMGL(raw))->__apply(i04048040605);
                                    
                                    //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                    goto __ret4048240607_end_;
                                }goto __ret4048240607_end_; __ret4048240607_end_: ;
                                }
                                ret4048140606;
                                }))
                                ;
                                
                            
                            //#line 286 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                            x10_int x240608 =
                              x10aux::nullCheck(boxIndex240604)->x10::array::Point::__apply(
                                ((x10_int)0));
                            
                            //#line 287 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                            x10_int y240609 =
                              x10aux::nullCheck(boxIndex240604)->x10::array::Point::__apply(
                                ((x10_int)1));
                            
                            //#line 288 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                            x10_int z240610 =
                              x10aux::nullCheck(boxIndex240604)->x10::array::Point::__apply(
                                ((x10_int)2));
                            
                            //#line 289 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                            x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > boxAtoms40611 =
                              x10aux::nullCheck(cachedAtoms)->x10::array::DistArray<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > >::__apply(
                                x240608,
                                y240609,
                                z240610);
                            
                            //#line 290 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10If_c
                            if ((!x10aux::struct_equals(boxAtoms40611,
                                                        X10_NULL)))
                            {
                                
                                //#line 291 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                x10x::vector::Vector3d translation40612 =
                                  au::edu::anu::mm::PeriodicFmm3d::getTranslation(
                                    lowestLevelDim,
                                    size,
                                    x240608,
                                    y240609,
                                    z240610);
                                
                                //#line 292 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                x10_int i24781max2478340601 =
                                  ((x10_int) ((x10aux::nullCheck(boxAtoms40611)->
                                                 FMGL(size)) - (((x10_int)1))));
                                
                                //#line 292 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.For_c
                                {
                                    x10_int i2478140602;
                                    for (
                                         //#line 292 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                         i2478140602 =
                                           ((x10_int)0);
                                         ((i2478140602) <= (i24781max2478340601));
                                         
                                         //#line 292 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalAssign_c
                                         i2478140602 =
                                           ((x10_int) ((i2478140602) + (((x10_int)1)))))
                                    {
                                        
                                        //#line 292 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                        x10_int otherBoxAtomIndex40603 =
                                          i2478140602;
                                        
                                        //#line 293 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                        au::edu::anu::chem::PointCharge atom240591 =
                                          (__extension__ ({
                                            
                                            //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                            x10_int i04048840592 =
                                              otherBoxAtomIndex40603;
                                            
                                            //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                            au::edu::anu::chem::PointCharge ret4048940593;
                                            
                                            //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                            goto __ret4049040594; __ret4049040594: {
                                            {
                                                
                                                //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                ret4048940593 =
                                                  (x10aux::nullCheck(boxAtoms40611)->
                                                     FMGL(raw))->__apply(i04048840592);
                                                
                                                //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                                goto __ret4049040594_end_;
                                            }goto __ret4049040594_end_; __ret4049040594_end_: ;
                                            }
                                            ret4048940593;
                                            }))
                                            ;
                                            
                                        
                                        //#line 294 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                        x10x::vector::Point3d translatedCentre40595 =
                                          (__extension__ ({
                                            
                                            //#line 294 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                            x10x::vector::Point3d this4049740596 =
                                              atom240591->
                                                FMGL(centre);
                                            
                                            //#line 31 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                                            x10x::vector::Vector3d that4049640597 =
                                              translation40612;
                                            (__extension__ ({
                                                
                                                //#line 27 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                                                x10x::vector::Vector3d b4049840598 =
                                                  that4049640597;
                                                (__extension__ ({
                                                    
                                                    //#line 28 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                                                    x10x::vector::Point3d alloc242374049940599 =
                                                      
                                                    x10x::vector::Point3d::_alloc();
                                                    
                                                    //#line 28 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10ConstructorCall_c
                                                    (alloc242374049940599)->::x10x::vector::Point3d::_constructor(
                                                      ((this4049740596->
                                                          FMGL(i)) + ((__extension__ ({
                                                          b4049840598->
                                                            FMGL(i);
                                                      }))
                                                      )),
                                                      ((this4049740596->
                                                          FMGL(j)) + ((__extension__ ({
                                                          b4049840598->
                                                            FMGL(j);
                                                      }))
                                                      )),
                                                      ((this4049740596->
                                                          FMGL(k)) + ((__extension__ ({
                                                          b4049840598->
                                                            FMGL(k);
                                                      }))
                                                      )),
                                                      x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
                                                    alloc242374049940599;
                                                }))
                                                ;
                                            }))
                                            ;
                                        }))
                                        ;
                                        
                                        //#line 295 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                        x10_int i24765max2476740588 =
                                          ((x10_int) ((x10aux::nullCheck(x10aux::nullCheck(box1)->
                                                                           FMGL(atoms))->size()) - (((x10_int)1))));
                                        
                                        //#line 295 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.For_c
                                        {
                                            x10_int i2476540589;
                                            for (
                                                 //#line 295 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                                 i2476540589 =
                                                   ((x10_int)0);
                                                 ((i2476540589) <= (i24765max2476740588));
                                                 
                                                 //#line 295 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalAssign_c
                                                 i2476540589 =
                                                   ((x10_int) ((i2476540589) + (((x10_int)1)))))
                                            {
                                                
                                                //#line 295 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                                x10_int atomIndex140590 =
                                                  i2476540589;
                                                
                                                //#line 296 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                                au::edu::anu::chem::PointCharge atom140571 =
                                                  x10aux::nullCheck(x10aux::nullCheck(box1)->
                                                                      FMGL(atoms))->__apply(
                                                    atomIndex140590);
                                                
                                                //#line 297 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                                x10_double distance40572 =
                                                  (__extension__ ({
                                                    
                                                    //#line 297 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                                    x10x::vector::Point3d this4050140573 =
                                                      atom140571->
                                                        FMGL(centre);
                                                    
                                                    //#line 66 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                                                    x10x::vector::Point3d b4050040574 =
                                                      translatedCentre40595;
                                                    x10aux::math_utils::sqrt((__extension__ ({
                                                        
                                                        //#line 57 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                                                        x10x::vector::Point3d b4050240575 =
                                                          b4050040574;
                                                        
                                                        //#line 57 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                                                        x10_double ret4050640576;
                                                        {
                                                            
                                                            //#line 58 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                                                            x10_double di4050340577 =
                                                              ((this4050140573->
                                                                  FMGL(i)) - (b4050240575->
                                                                                FMGL(i)));
                                                            
                                                            //#line 59 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                                                            x10_double dj4050440578 =
                                                              ((this4050140573->
                                                                  FMGL(j)) - (b4050240575->
                                                                                FMGL(j)));
                                                            
                                                            //#line 60 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                                                            x10_double dk4050540579 =
                                                              ((this4050140573->
                                                                  FMGL(k)) - (b4050240575->
                                                                                FMGL(k)));
                                                            
                                                            //#line 61 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalAssign_c
                                                            ret4050640576 =
                                                              ((((((di4050340577) * (di4050340577))) + (((dj4050440578) * (dj4050440578))))) + (((dk4050540579) * (dk4050540579))));
                                                        }
                                                        ret4050640576;
                                                    }))
                                                    );
                                                }))
                                                ;
                                                
                                                //#line 298 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10If_c
                                                if ((!x10aux::struct_equals(distance40572,
                                                                            0.0)))
                                                {
                                                    
                                                    //#line 299 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalAssign_c
                                                    thisPlaceEnergy =
                                                      ((thisPlaceEnergy) + (((((atom140571->
                                                                                  FMGL(charge)) * (atom240591->
                                                                                                     FMGL(charge)))) / ((__extension__ ({
                                                        
                                                        //#line 299 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                                        x10x::vector::Point3d this4050940580 =
                                                          atom140571->
                                                            FMGL(centre);
                                                        
                                                        //#line 66 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                                                        x10x::vector::Point3d b4050840581 =
                                                          translatedCentre40595;
                                                        x10aux::math_utils::sqrt((__extension__ ({
                                                            
                                                            //#line 57 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                                                            x10x::vector::Point3d b4051040582 =
                                                              b4050840581;
                                                            
                                                            //#line 57 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                                                            x10_double ret4051440583;
                                                            {
                                                                
                                                                //#line 58 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                                                                x10_double di4051140584 =
                                                                  ((this4050940580->
                                                                      FMGL(i)) - (b4051040582->
                                                                                    FMGL(i)));
                                                                
                                                                //#line 59 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                                                                x10_double dj4051240585 =
                                                                  ((this4050940580->
                                                                      FMGL(j)) - (b4051040582->
                                                                                    FMGL(j)));
                                                                
                                                                //#line 60 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                                                                x10_double dk4051340586 =
                                                                  ((this4050940580->
                                                                      FMGL(k)) - (b4051040582->
                                                                                    FMGL(k)));
                                                                
                                                                //#line 61 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalAssign_c
                                                                ret4051440583 =
                                                                  ((((((di4051140584) * (di4051140584))) + (((dj4051240585) * (dj4051240585))))) + (((dk4051340586) * (dk4051340586))));
                                                            }
                                                            ret4051440583;
                                                        }))
                                                        );
                                                    }))
                                                    ))));
                                                }
                                                
                                            }
                                        }
                                        }
                                    }
                                    
                                }
                                
                            }
                            }
                            
                    }
                    
                }
                }
                
            
            //#line 307 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
            x10::lang::Runtime::makeOffer<x10_double >(
              thisPlaceEnergy);
        }
        
        // captured environment
        x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::LocallyEssentialTree> > > locallyEssentialTrees;
        x10aux::ref<x10::array::Point> p1;
        x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > lowestLevelBoxes;
        x10_int lowestLevelDim;
        x10_double size;
        
        x10aux::serialization_id_t _get_serialization_id() {
            return _serialization_id;
        }
        
        void _serialize_body(x10aux::serialization_buffer &buf) {
            buf.write(this->locallyEssentialTrees);
            buf.write(this->p1);
            buf.write(this->lowestLevelBoxes);
            buf.write(this->lowestLevelDim);
            buf.write(this->size);
        }
        
        template<class __T> static x10aux::ref<__T> _deserialize(x10aux::deserialization_buffer &buf) {
            au_edu_anu_mm_PeriodicFmm3d__closure__10* storage = x10aux::alloc<au_edu_anu_mm_PeriodicFmm3d__closure__10>();
            buf.record_reference(x10aux::ref<au_edu_anu_mm_PeriodicFmm3d__closure__10>(storage));
            x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::LocallyEssentialTree> > > that_locallyEssentialTrees = buf.read<x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::LocallyEssentialTree> > > >();
            x10aux::ref<x10::array::Point> that_p1 = buf.read<x10aux::ref<x10::array::Point> >();
            x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > that_lowestLevelBoxes = buf.read<x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > >();
            x10_int that_lowestLevelDim = buf.read<x10_int>();
            x10_double that_size = buf.read<x10_double>();
            x10aux::ref<au_edu_anu_mm_PeriodicFmm3d__closure__10> this_ = new (storage) au_edu_anu_mm_PeriodicFmm3d__closure__10(that_locallyEssentialTrees, that_p1, that_lowestLevelBoxes, that_lowestLevelDim, that_size);
            return this_;
        }
        
        au_edu_anu_mm_PeriodicFmm3d__closure__10(x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::LocallyEssentialTree> > > locallyEssentialTrees, x10aux::ref<x10::array::Point> p1, x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > lowestLevelBoxes, x10_int lowestLevelDim, x10_double size) : locallyEssentialTrees(locallyEssentialTrees), p1(p1), lowestLevelBoxes(lowestLevelBoxes), lowestLevelDim(lowestLevelDim), size(size) { }
        
        static const x10aux::serialization_id_t _serialization_id;
        
        static const x10aux::RuntimeType* getRTT() { return x10aux::getRTT<x10::lang::VoidFun_0_0>(); }
        virtual const x10aux::RuntimeType *_type() const { return x10aux::getRTT<x10::lang::VoidFun_0_0>(); }
        
        x10aux::ref<x10::lang::String> toString() {
            return x10aux::string_utils::lit(this->toNativeString());
        }
        
        const char* toNativeString() {
            return "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10:264-308";
        }
        
        };
        
        #endif // AU_EDU_ANU_MM_PERIODICFMM3D__CLOSURE__10_CLOSURE
        #ifndef AU_EDU_ANU_MM_PERIODICFMM3D__CLOSURE__9_CLOSURE
#define AU_EDU_ANU_MM_PERIODICFMM3D__CLOSURE__9_CLOSURE
#include <x10/lang/Closure.h>
#include <x10/lang/VoidFun_0_0.h>
class au_edu_anu_mm_PeriodicFmm3d__closure__9 : public x10::lang::Closure {
    public:
    
    static x10::lang::VoidFun_0_0::itable<au_edu_anu_mm_PeriodicFmm3d__closure__9> _itable;
    static x10aux::itable_entry _itables[2];
    
    virtual x10aux::itable_entry* _getITables() { return _itables; }
    
    // closure body
    void __apply() {
        
        //#line 264 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.For_c
        {
            x10aux::ref<x10::lang::Iterator<x10aux::ref<x10::array::Point> > > p140671;
            for (
                 //#line 264 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                 p140671 = x10aux::nullCheck(x10aux::nullCheck(x10aux::nullCheck(__lowerer__var__13__)->restriction(
                                                                 x10::lang::Place::_make(x10aux::here)))->
                                               FMGL(region))->iterator();
                 x10::lang::Iterator<x10aux::ref<x10::array::Point> >::hasNext(x10aux::nullCheck(p140671));
                 ) {
                
                //#line 264 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<x10::array::Point> p1 =
                  x10::lang::Iterator<x10aux::ref<x10::array::Point> >::next(x10aux::nullCheck(p140671));
                
                //#line 264 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
                x10::lang::Runtime::runAsync(x10::lang::Place::_make(x10aux::here),
                                             x10aux::ref<x10::lang::VoidFun_0_0>(x10aux::ref<au_edu_anu_mm_PeriodicFmm3d__closure__10>(new (x10aux::alloc<x10::lang::VoidFun_0_0>(sizeof(au_edu_anu_mm_PeriodicFmm3d__closure__10)))au_edu_anu_mm_PeriodicFmm3d__closure__10(locallyEssentialTrees, p1, lowestLevelBoxes, lowestLevelDim, size))));
            }
        }
        
    }
    
    // captured environment
    x10aux::ref<x10::array::Dist> __lowerer__var__13__;
    x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::LocallyEssentialTree> > > locallyEssentialTrees;
    x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > lowestLevelBoxes;
    x10_int lowestLevelDim;
    x10_double size;
    
    x10aux::serialization_id_t _get_serialization_id() {
        return _serialization_id;
    }
    
    void _serialize_body(x10aux::serialization_buffer &buf) {
        buf.write(this->__lowerer__var__13__);
        buf.write(this->locallyEssentialTrees);
        buf.write(this->lowestLevelBoxes);
        buf.write(this->lowestLevelDim);
        buf.write(this->size);
    }
    
    template<class __T> static x10aux::ref<__T> _deserialize(x10aux::deserialization_buffer &buf) {
        au_edu_anu_mm_PeriodicFmm3d__closure__9* storage = x10aux::alloc<au_edu_anu_mm_PeriodicFmm3d__closure__9>();
        buf.record_reference(x10aux::ref<au_edu_anu_mm_PeriodicFmm3d__closure__9>(storage));
        x10aux::ref<x10::array::Dist> that___lowerer__var__13__ = buf.read<x10aux::ref<x10::array::Dist> >();
        x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::LocallyEssentialTree> > > that_locallyEssentialTrees = buf.read<x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::LocallyEssentialTree> > > >();
        x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > that_lowestLevelBoxes = buf.read<x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > >();
        x10_int that_lowestLevelDim = buf.read<x10_int>();
        x10_double that_size = buf.read<x10_double>();
        x10aux::ref<au_edu_anu_mm_PeriodicFmm3d__closure__9> this_ = new (storage) au_edu_anu_mm_PeriodicFmm3d__closure__9(that___lowerer__var__13__, that_locallyEssentialTrees, that_lowestLevelBoxes, that_lowestLevelDim, that_size);
        return this_;
    }
    
    au_edu_anu_mm_PeriodicFmm3d__closure__9(x10aux::ref<x10::array::Dist> __lowerer__var__13__, x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::LocallyEssentialTree> > > locallyEssentialTrees, x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > lowestLevelBoxes, x10_int lowestLevelDim, x10_double size) : __lowerer__var__13__(__lowerer__var__13__), locallyEssentialTrees(locallyEssentialTrees), lowestLevelBoxes(lowestLevelBoxes), lowestLevelDim(lowestLevelDim), size(size) { }
    
    static const x10aux::serialization_id_t _serialization_id;
    
    static const x10aux::RuntimeType* getRTT() { return x10aux::getRTT<x10::lang::VoidFun_0_0>(); }
    virtual const x10aux::RuntimeType *_type() const { return x10aux::getRTT<x10::lang::VoidFun_0_0>(); }
    
    x10aux::ref<x10::lang::String> toString() {
        return x10aux::string_utils::lit(this->toNativeString());
    }
    
    const char* toNativeString() {
        return "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10:264-308";
    }

};

#endif // AU_EDU_ANU_MM_PERIODICFMM3D__CLOSURE__9_CLOSURE

//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10FieldDecl_c

//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock> au::edu::anu::mm::PeriodicFmm3d::getOrderedLock(
  ) {
    
    //#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Return_c
    return x10::util::concurrent::OrderedLock::getObjectLock(((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->
                                                               FMGL(X10__object_lock_id0));
    
}

//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10FieldDecl_c
x10_int au::edu::anu::mm::PeriodicFmm3d::FMGL(X10__class_lock_id1);
void au::edu::anu::mm::PeriodicFmm3d::FMGL(X10__class_lock_id1__do_init)() {
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: au::edu::anu::mm::PeriodicFmm3d.X10$class_lock_id1");
    x10_int __var118__ = x10::util::concurrent::OrderedLock::createClassLock();
    FMGL(X10__class_lock_id1) = __var118__;
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
}
void au::edu::anu::mm::PeriodicFmm3d::FMGL(X10__class_lock_id1__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var119__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(X10__class_lock_id1__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var119__ != x10aux::UNINITIALIZED) goto WAIT;
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
                                                                       _SI_("WAITING for field: au::edu::anu::mm::PeriodicFmm3d.X10$class_lock_id1 to be initialized");
                                                                       while (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                                       _SI_("CONTINUING because field: au::edu::anu::mm::PeriodicFmm3d.X10$class_lock_id1 has been initialized");
                                                                       x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__120 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(au::edu::anu::mm::PeriodicFmm3d::FMGL(X10__class_lock_id1__init));

volatile x10aux::status au::edu::anu::mm::PeriodicFmm3d::FMGL(X10__class_lock_id1__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> au::edu::anu::mm::PeriodicFmm3d::FMGL(X10__class_lock_id1__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(X10__class_lock_id1) = buf.read<x10_int>();
    au::edu::anu::mm::PeriodicFmm3d::FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t au::edu::anu::mm::PeriodicFmm3d::FMGL(X10__class_lock_id1__id) =
  x10aux::StaticInitBroadcastDispatcher::addRoutine(au::edu::anu::mm::PeriodicFmm3d::FMGL(X10__class_lock_id1__deserialize));


//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock>
  au::edu::anu::mm::PeriodicFmm3d::getStaticOrderedLock(
  ) {
    
    //#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 170 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/util/concurrent/OrderedLock.x10": x10.ast.X10LocalDecl_c
        x10_int lockId34568 = au::edu::anu::mm::PeriodicFmm3d::
                                FMGL(X10__class_lock_id1__get)();
        x10::util::Map<x10_int, x10aux::ref<x10::util::concurrent::OrderedLock> >::getOrThrow(x10aux::nullCheck(x10::util::concurrent::OrderedLock::
                                                                                                                  FMGL(lockMap__get)()), 
          lockId34568);
    }))
    ;
    
}

//#line 36 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10FieldDecl_c
/** The number of concentric shells of copies of the unit cell. */
                                                                  //#line 38 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10FieldDecl_c
                                                                  x10_int
                                                                    au::edu::anu::mm::PeriodicFmm3d::FMGL(TIMER_INDEX_MACROSCOPIC) =
                                                                    ((x10_int)8);
                                                                  
                                                                  
                                                                  //#line 41 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10FieldDecl_c
                                                                  /** A region representing a cube of 3x3x3 boxes, used for constructing macroscopic multipoles. */x10aux::ref<x10::array::Region>
                                                                                                                                                                     au::edu::anu::mm::PeriodicFmm3d::FMGL(threeCube);
                                                                  void au::edu::anu::mm::PeriodicFmm3d::FMGL(threeCube__do_init)() {
                                                                      FMGL(threeCube__status) = x10aux::INITIALIZING;
                                                                      _SI_("Doing static initialisation for field: au::edu::anu::mm::PeriodicFmm3d.threeCube");
                                                                      x10aux::ref<x10::array::Region>
                                                                        __var122__ =
                                                                        x10aux::nullCheck(x10::lang::IntRange::_make(((x10_int)-1), ((x10_int)1)))->x10::lang::IntRange::__times(
                                                                          x10::lang::IntRange::_make(((x10_int)-1), ((x10_int)1)))->__times(
                                                                          x10::array::Region::__implicit_convert(
                                                                            x10::lang::IntRange::_make(((x10_int)-1), ((x10_int)1))));
                                                                      FMGL(threeCube) = __var122__;
                                                                      FMGL(threeCube__status) = x10aux::INITIALIZED;
                                                                  }
                                                                  void au::edu::anu::mm::PeriodicFmm3d::FMGL(threeCube__init)() {
                                                                      if (x10aux::here == 0) {
                                                                          x10aux::status __var123__ =
                                                                            (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(threeCube__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
                                                                          if (__var123__ != x10aux::UNINITIALIZED) goto WAIT;
                                                                          FMGL(threeCube__do_init)();
                                                                          x10aux::StaticInitBroadcastDispatcher::broadcastStaticField(FMGL(threeCube),
                                                                                                                                      FMGL(threeCube__id));
                                                                          // Notify all waiting threads
                                                                          x10aux::StaticInitBroadcastDispatcher::lock();
                                                                          x10aux::StaticInitBroadcastDispatcher::notify();
                                                                      }
                                                                      WAIT:
                                                                      if (FMGL(threeCube__status) != x10aux::INITIALIZED) {
                                                                                                                               x10aux::StaticInitBroadcastDispatcher::lock();
                                                                                                                               _SI_("WAITING for field: au::edu::anu::mm::PeriodicFmm3d.threeCube to be initialized");
                                                                                                                               while (FMGL(threeCube__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                                                                                               _SI_("CONTINUING because field: au::edu::anu::mm::PeriodicFmm3d.threeCube has been initialized");
                                                                                                                               x10aux::StaticInitBroadcastDispatcher::unlock();
                                                                      }
                                                                  }
                                                                  static void* __init__124 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(au::edu::anu::mm::PeriodicFmm3d::FMGL(threeCube__init));
                                                                  
                                                                  volatile x10aux::status au::edu::anu::mm::PeriodicFmm3d::FMGL(threeCube__status);
                                                                  // extract value from a buffer
                                                                  x10aux::ref<x10::lang::Reference>
                                                                    au::edu::anu::mm::PeriodicFmm3d::FMGL(threeCube__deserialize)(x10aux::deserialization_buffer &buf) {
                                                                      FMGL(threeCube) =
                                                                        buf.read<x10aux::ref<x10::array::Region> >();
                                                                      au::edu::anu::mm::PeriodicFmm3d::FMGL(threeCube__status) = x10aux::INITIALIZED;
                                                                      // Notify all waiting threads
                                                                      x10aux::StaticInitBroadcastDispatcher::lock();
                                                                      x10aux::StaticInitBroadcastDispatcher::notify();
                                                                      return X10_NULL;
                                                                  }
                                                                  const x10aux::serialization_id_t au::edu::anu::mm::PeriodicFmm3d::FMGL(threeCube__id) =
                                                                    x10aux::StaticInitBroadcastDispatcher::addRoutine(au::edu::anu::mm::PeriodicFmm3d::FMGL(threeCube__deserialize));
                                                                  
                                                                  
                                                                  //#line 44 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10FieldDecl_c
                                                                  /** A region representing a cube of 9x9x9 boxes, used for interacting with macroscopic multipoles. */x10aux::ref<x10::array::Region>
                                                                                                                                                                         au::edu::anu::mm::PeriodicFmm3d::FMGL(nineCube);
                                                                  void au::edu::anu::mm::PeriodicFmm3d::FMGL(nineCube__do_init)() {
                                                                      FMGL(nineCube__status) = x10aux::INITIALIZING;
                                                                      _SI_("Doing static initialisation for field: au::edu::anu::mm::PeriodicFmm3d.nineCube");
                                                                      x10aux::ref<x10::array::Region>
                                                                        __var125__ =
                                                                        x10aux::nullCheck(x10::lang::IntRange::_make(((x10_int)-4), ((x10_int)4)))->x10::lang::IntRange::__times(
                                                                          x10::lang::IntRange::_make(((x10_int)-4), ((x10_int)4)))->__times(
                                                                          x10::array::Region::__implicit_convert(
                                                                            x10::lang::IntRange::_make(((x10_int)-4), ((x10_int)4))));
                                                                      FMGL(nineCube) = __var125__;
                                                                      FMGL(nineCube__status) = x10aux::INITIALIZED;
                                                                  }
                                                                  void au::edu::anu::mm::PeriodicFmm3d::FMGL(nineCube__init)() {
                                                                      if (x10aux::here == 0) {
                                                                          x10aux::status __var126__ =
                                                                            (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(nineCube__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
                                                                          if (__var126__ != x10aux::UNINITIALIZED) goto WAIT;
                                                                          FMGL(nineCube__do_init)();
                                                                          x10aux::StaticInitBroadcastDispatcher::broadcastStaticField(FMGL(nineCube),
                                                                                                                                      FMGL(nineCube__id));
                                                                          // Notify all waiting threads
                                                                          x10aux::StaticInitBroadcastDispatcher::lock();
                                                                          x10aux::StaticInitBroadcastDispatcher::notify();
                                                                      }
                                                                      WAIT:
                                                                      if (FMGL(nineCube__status) != x10aux::INITIALIZED) {
                                                                                                                              x10aux::StaticInitBroadcastDispatcher::lock();
                                                                                                                              _SI_("WAITING for field: au::edu::anu::mm::PeriodicFmm3d.nineCube to be initialized");
                                                                                                                              while (FMGL(nineCube__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                                                                                              _SI_("CONTINUING because field: au::edu::anu::mm::PeriodicFmm3d.nineCube has been initialized");
                                                                                                                              x10aux::StaticInitBroadcastDispatcher::unlock();
                                                                      }
                                                                  }
                                                                  static void* __init__127 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(au::edu::anu::mm::PeriodicFmm3d::FMGL(nineCube__init));
                                                                  
                                                                  volatile x10aux::status au::edu::anu::mm::PeriodicFmm3d::FMGL(nineCube__status);
                                                                  // extract value from a buffer
                                                                  x10aux::ref<x10::lang::Reference>
                                                                    au::edu::anu::mm::PeriodicFmm3d::FMGL(nineCube__deserialize)(x10aux::deserialization_buffer &buf) {
                                                                      FMGL(nineCube) =
                                                                        buf.read<x10aux::ref<x10::array::Region> >();
                                                                      au::edu::anu::mm::PeriodicFmm3d::FMGL(nineCube__status) = x10aux::INITIALIZED;
                                                                      // Notify all waiting threads
                                                                      x10aux::StaticInitBroadcastDispatcher::lock();
                                                                      x10aux::StaticInitBroadcastDispatcher::notify();
                                                                      return X10_NULL;
                                                                  }
                                                                  const x10aux::serialization_id_t au::edu::anu::mm::PeriodicFmm3d::FMGL(nineCube__id) =
                                                                    x10aux::StaticInitBroadcastDispatcher::addRoutine(au::edu::anu::mm::PeriodicFmm3d::FMGL(nineCube__deserialize));
                                                                  
                                                                  

//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::mm::PeriodicFmm3d::_constructor(
  x10_double density,
  x10_int numTerms,
  x10x::vector::Point3d topLeftFront,
  x10_double size,
  x10_int numAtoms,
  x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > atoms,
  x10_int numShells) {
    
    //#line 64 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<au::edu::anu::mm::Fmm3d>)this))->::au::edu::anu::mm::Fmm3d::_constructor(
      density,
      numTerms,
      ((x10_int)1),
      topLeftFront,
      size,
      numAtoms,
      atoms,
      ((x10_int)0),
      true);
    
    //#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.AssignPropertyCall_c
    
    //#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::PeriodicFmm3d> this3731240530 =
          ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this);
        {
            
            //#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this3731240530)->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
        }
        
    }))
    ;
    
    //#line 65 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->
      FMGL(numShells) = numShells;
    
}
x10aux::ref<au::edu::anu::mm::PeriodicFmm3d> au::edu::anu::mm::PeriodicFmm3d::_make(
  x10_double density,
  x10_int numTerms,
  x10x::vector::Point3d topLeftFront,
  x10_double size,
  x10_int numAtoms,
  x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > atoms,
  x10_int numShells) {
    x10aux::ref<au::edu::anu::mm::PeriodicFmm3d> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::PeriodicFmm3d>(), 0, sizeof(au::edu::anu::mm::PeriodicFmm3d))) au::edu::anu::mm::PeriodicFmm3d();
    this_->_constructor(density, numTerms,
    topLeftFront,
    size,
    numAtoms,
    atoms,
    numShells);
    return this_;
}



//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::mm::PeriodicFmm3d::_constructor(
  x10_double density,
  x10_int numTerms,
  x10x::vector::Point3d topLeftFront,
  x10_double size,
  x10_int numAtoms,
  x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > atoms,
  x10_int numShells,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    
    //#line 64 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<au::edu::anu::mm::Fmm3d>)this))->::au::edu::anu::mm::Fmm3d::_constructor(
      density,
      numTerms,
      ((x10_int)1),
      topLeftFront,
      size,
      numAtoms,
      atoms,
      ((x10_int)0),
      true);
    
    //#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.AssignPropertyCall_c
    
    //#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::PeriodicFmm3d> this3731540531 =
          ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this);
        {
            
            //#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this3731540531)->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
        }
        
    }))
    ;
    
    //#line 65 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->
      FMGL(numShells) =
      numShells;
    
    //#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->
      FMGL(X10__object_lock_id0) =
      x10aux::nullCheck(paramLock)->getIndex();
    
}
x10aux::ref<au::edu::anu::mm::PeriodicFmm3d> au::edu::anu::mm::PeriodicFmm3d::_make(
  x10_double density,
  x10_int numTerms,
  x10x::vector::Point3d topLeftFront,
  x10_double size,
  x10_int numAtoms,
  x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > atoms,
  x10_int numShells,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    x10aux::ref<au::edu::anu::mm::PeriodicFmm3d> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::PeriodicFmm3d>(), 0, sizeof(au::edu::anu::mm::PeriodicFmm3d))) au::edu::anu::mm::PeriodicFmm3d();
    this_->_constructor(density,
    numTerms,
    topLeftFront,
    size,
    numAtoms,
    atoms,
    numShells,
    paramLock);
    return this_;
}



//#line 68 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10MethodDecl_c
x10_double au::edu::anu::mm::PeriodicFmm3d::calculateEnergy(
  ) {
    
    //#line 69 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
    ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->
      FMGL(timer)->au::edu::anu::util::Timer::start(
      ((x10_int)0));
    {
        
        //#line 70 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
        x10::lang::Runtime::ensureNotInAtomic();
        
        //#line 70 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::lang::FinishState> x10____var2 =
          x10::lang::Runtime::startFinish();
        {
            
            //#line 70 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::lang::Throwable> throwable40677 =
              X10_NULL;
            
            //#line 70 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.Try_c
            try {
                
                //#line 70 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.Try_c
                try {
                    {
                        
                        //#line 71 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
                        x10::lang::Runtime::runAsync(
                          x10aux::ref<x10::lang::VoidFun_0_0>(x10aux::ref<au_edu_anu_mm_PeriodicFmm3d__closure__1>(new (x10aux::alloc<x10::lang::VoidFun_0_0>(sizeof(au_edu_anu_mm_PeriodicFmm3d__closure__1)))au_edu_anu_mm_PeriodicFmm3d__closure__1(this))));
                        
                        //#line 74 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
                        ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->multipoleLowestLevel();
                        
                        //#line 76 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
                        ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->combineMultipoles();
                        
                        //#line 77 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
                        ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->combineMacroscopicExpansions();
                        
                        //#line 78 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
                        ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->transformToLocal();
                    }
                }
                catch (x10aux::__ref& __ref__129) {
                    x10aux::ref<x10::lang::Throwable>& __exc__ref__129 = (x10aux::ref<x10::lang::Throwable>&)__ref__129;
                    if (true) {
                        x10aux::ref<x10::lang::Throwable> __lowerer__var__4__ =
                          static_cast<x10aux::ref<x10::lang::Throwable> >(__exc__ref__129);
                        {
                            
                            //#line 70 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
                            x10::lang::Runtime::pushException(
                              __lowerer__var__4__);
                            
                            //#line 70 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.Throw_c
                            x10aux::throwException(x10aux::nullCheck(x10::lang::RuntimeException::_make()));
                        }
                    } else
                    throw;
                }
                
                //#line 70 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
                x10::compiler::Finalization::plausibleThrow();
            }
            catch (x10aux::__ref& __ref__130) {
                x10aux::ref<x10::lang::Throwable>& __exc__ref__130 = (x10aux::ref<x10::lang::Throwable>&)__ref__130;
                if (true) {
                    x10aux::ref<x10::lang::Throwable> formal40678 =
                      static_cast<x10aux::ref<x10::lang::Throwable> >(__exc__ref__130);
                    {
                        
                        //#line 70 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalAssign_c
                        throwable40677 =
                          formal40678;
                    }
                } else
                throw;
            }
            
            //#line 70 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10If_c
            if ((!x10aux::struct_equals(X10_NULL,
                                        throwable40677)))
            {
                
                //#line 70 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10If_c
                if (x10aux::instanceof<x10aux::ref<x10::compiler::Abort> >(throwable40677))
                {
                    
                    //#line 70 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.Throw_c
                    x10aux::throwException(x10aux::nullCheck(throwable40677));
                }
                
            }
            
            //#line 70 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10If_c
            if (true) {
                
                //#line 70 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
                x10::lang::Runtime::stopFinish(
                  x10____var2);
            }
            
            //#line 70 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10If_c
            if ((!x10aux::struct_equals(X10_NULL,
                                        throwable40677)))
            {
                
                //#line 70 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10If_c
                if (!(x10aux::instanceof<x10aux::ref<x10::compiler::Finalization> >(throwable40677)))
                {
                    
                    //#line 70 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.Throw_c
                    x10aux::throwException(x10aux::nullCheck(throwable40677));
                }
                
            }
            
        }
    }
    
    //#line 80 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
    x10_double totalEnergy = ((((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->getDirectEnergy()) + (((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->getFarFieldEnergy()));
    
    //#line 81 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
    ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->
      FMGL(timer)->au::edu::anu::util::Timer::stop(
      ((x10_int)0));
    
    //#line 82 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Return_c
    return totalEnergy;
    
}

//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10MethodDecl_c
void au::edu::anu::mm::PeriodicFmm3d::combineMacroscopicExpansions(
  ) {
    
    //#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
    ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->
      FMGL(timer)->au::edu::anu::util::Timer::start(
      ((x10_int)8));
    
    //#line 92 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
    x10_int numShells = ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->
                          FMGL(numShells);
    
    //#line 93 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
    x10_int numTerms = ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->
                         FMGL(numTerms);
    
    //#line 94 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
    x10_double size = ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->
                        FMGL(size);
    
    //#line 95 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
    x10aux::ref<x10::array::Array<x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > > > boxes =
      ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->
        FMGL(boxes);
    
    //#line 96 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
    x10::lang::Runtime::runAt(x10aux::nullCheck(x10aux::nullCheck((__extension__ ({
                                                    
                                                    //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                    x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > ret37319;
                                                    
                                                    //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                                    goto __ret37320; __ret37320: {
                                                    {
                                                        
                                                        //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                        ret37319 =
                                                          (x10aux::nullCheck(boxes)->
                                                             FMGL(raw))->__apply(((x10_int)0));
                                                        
                                                        //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                                        goto __ret37320_end_;
                                                    }goto __ret37320_end_; __ret37320_end_: ;
                                                    }
                                                    ret37319;
                                                    }))
                                                    )->
                                                      FMGL(dist))->__apply(
                                                  ((x10_int)0),
                                                  ((x10_int)0),
                                                  ((x10_int)0)),
                              x10aux::ref<x10::lang::VoidFun_0_0>(x10aux::ref<au_edu_anu_mm_PeriodicFmm3d__closure__2>(new (x10aux::alloc<x10::lang::VoidFun_0_0>(sizeof(au_edu_anu_mm_PeriodicFmm3d__closure__2)))au_edu_anu_mm_PeriodicFmm3d__closure__2(numShells, boxes, numTerms, size))));
    
    //#line 146 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
    ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->
      FMGL(timer)->au::edu::anu::util::Timer::stop(
      ((x10_int)8));
    }
    

//#line 149 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10MethodDecl_c
void au::edu::anu::mm::PeriodicFmm3d::assignAtomsToBoxes(
  ) {
    
    //#line 150 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
    ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->
      FMGL(timer)->au::edu::anu::util::Timer::start(
      ((x10_int)7));
    
    //#line 151 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
    x10x::vector::Vector3d offset = ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->
                                      FMGL(offset);
    
    //#line 152 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
    x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > lowestLevelBoxes =
      ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->
        FMGL(lowestLevelBoxes);
    
    //#line 153 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
    x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > atoms =
      ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->
        FMGL(atoms);
    
    //#line 154 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
    x10_int lowestLevelDim = ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->
                               FMGL(lowestLevelDim);
    
    //#line 155 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
    x10_double size = ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->
                        FMGL(size);
    
    //#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
    x10x::vector::Vector3d dipole;
    {
        
        //#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::lang::FinishState> x10____var4 =
          x10aux::class_cast_unchecked<x10aux::ref<x10::lang::FinishState> >(x10::lang::Runtime::startCollectingFinish<x10x::vector::Vector3d >(
                                                                               x10aux::class_cast_unchecked<x10aux::ref<x10::lang::Reducible<x10x::vector::Vector3d> > >((__extension__ ({
                                                                                   
                                                                                   //#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                                                                   au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer alloc24664 =
                                                                                     
                                                                                   au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer::_alloc();
                                                                                   
                                                                                   //#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10ConstructorCall_c
                                                                                   (alloc24664)->::au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer::_constructor(
                                                                                     x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
                                                                                   alloc24664;
                                                                               }))
                                                                               )));
        {
            
            //#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::lang::Throwable> throwable40683 =
              X10_NULL;
            
            //#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.Try_c
            try {
                
                //#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.Try_c
                try {
                    {
                        {
                            
                            //#line 157 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
                            x10::lang::Runtime::ensureNotInAtomic();
                            
                            //#line 157 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                            x10aux::ref<x10::array::Dist> __lowerer__var__6__ =
                              x10aux::nullCheck(atoms)->
                                FMGL(dist);
                            
                            //#line 157 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.For_c
                            {
                                x10aux::ref<x10::lang::Iterator<x10::lang::Place> > __lowerer__var__7__40643;
                                for (
                                     //#line 157 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                     __lowerer__var__7__40643 =
                                       x10aux::nullCheck(x10aux::nullCheck(__lowerer__var__6__)->places())->iterator();
                                     x10::lang::Iterator<x10::lang::Place>::hasNext(x10aux::nullCheck(__lowerer__var__7__40643));
                                     ) {
                                    
                                    //#line 157 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                    x10::lang::Place __lowerer__var__7__ =
                                      x10::lang::Iterator<x10::lang::Place>::next(x10aux::nullCheck(__lowerer__var__7__40643));
                                    
                                    //#line 157 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
                                    x10::lang::Runtime::runAsync(
                                      __lowerer__var__7__,
                                      x10aux::ref<x10::lang::VoidFun_0_0>(x10aux::ref<au_edu_anu_mm_PeriodicFmm3d__closure__3>(new (x10aux::alloc<x10::lang::VoidFun_0_0>(sizeof(au_edu_anu_mm_PeriodicFmm3d__closure__3)))au_edu_anu_mm_PeriodicFmm3d__closure__3(__lowerer__var__6__, atoms, offset, lowestLevelDim, size, lowestLevelBoxes))));
                                }
                            }
                            
                        }
                    }
                }
                catch (x10aux::__ref& __ref__135) {
                    x10aux::ref<x10::lang::Throwable>& __exc__ref__135 = (x10aux::ref<x10::lang::Throwable>&)__ref__135;
                    if (true) {
                        x10aux::ref<x10::lang::Throwable> __lowerer__var__8__ =
                          static_cast<x10aux::ref<x10::lang::Throwable> >(__exc__ref__135);
                        {
                            
                            //#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
                            x10::lang::Runtime::pushException(
                              __lowerer__var__8__);
                            
                            //#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.Throw_c
                            x10aux::throwException(x10aux::nullCheck(x10::lang::RuntimeException::_make()));
                        }
                    } else
                    throw;
                }
                
                //#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
                x10::compiler::Finalization::plausibleThrow();
            }
            catch (x10aux::__ref& __ref__136) {
                x10aux::ref<x10::lang::Throwable>& __exc__ref__136 = (x10aux::ref<x10::lang::Throwable>&)__ref__136;
                if (true) {
                    x10aux::ref<x10::lang::Throwable> formal40684 =
                      static_cast<x10aux::ref<x10::lang::Throwable> >(__exc__ref__136);
                    {
                        
                        //#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalAssign_c
                        throwable40683 =
                          formal40684;
                    }
                } else
                throw;
            }
            
            //#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10If_c
            if ((!x10aux::struct_equals(X10_NULL,
                                        throwable40683)))
            {
                
                //#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10If_c
                if (x10aux::instanceof<x10aux::ref<x10::compiler::Abort> >(throwable40683))
                {
                    
                    //#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.Throw_c
                    x10aux::throwException(x10aux::nullCheck(throwable40683));
                }
                
            }
            
            //#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10If_c
            if (true) {
                
                //#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalAssign_c
                dipole = x10::lang::Runtime::stopCollectingFinish<x10x::vector::Vector3d >(
                           x10____var4);
            }
            
            //#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10If_c
            if ((!x10aux::struct_equals(X10_NULL,
                                        throwable40683)))
            {
                
                //#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10If_c
                if (!(x10aux::instanceof<x10aux::ref<x10::compiler::Finalization> >(throwable40683)))
                {
                    
                    //#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.Throw_c
                    x10aux::throwException(x10aux::nullCheck(throwable40683));
                }
                
            }
            
        }
    }
    
    //#line 177 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
    ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->cancelDipole(
      dipole);
    {
        
        //#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
        x10::lang::Runtime::ensureNotInAtomic();
        
        //#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::lang::FinishState> x10____var5 =
          x10::lang::Runtime::startFinish();
        {
            
            //#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::lang::Throwable> throwable40686 =
              X10_NULL;
            
            //#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.Try_c
            try {
                
                //#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.Try_c
                try {
                    {
                        {
                            
                            //#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
                            x10::lang::Runtime::ensureNotInAtomic();
                            
                            //#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                            x10aux::ref<x10::array::Dist> __lowerer__var__9__ =
                              x10aux::nullCheck(lowestLevelBoxes)->
                                FMGL(dist);
                            
                            //#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.For_c
                            {
                                x10aux::ref<x10::lang::Iterator<x10::lang::Place> > __lowerer__var__10__40655;
                                for (
                                     //#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                     __lowerer__var__10__40655 =
                                       x10aux::nullCheck(x10aux::nullCheck(__lowerer__var__9__)->places())->iterator();
                                     x10::lang::Iterator<x10::lang::Place>::hasNext(x10aux::nullCheck(__lowerer__var__10__40655));
                                     ) {
                                    
                                    //#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                    x10::lang::Place __lowerer__var__10__ =
                                      x10::lang::Iterator<x10::lang::Place>::next(x10aux::nullCheck(__lowerer__var__10__40655));
                                    
                                    //#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
                                    x10::lang::Runtime::runAsync(
                                      __lowerer__var__10__,
                                      x10aux::ref<x10::lang::VoidFun_0_0>(x10aux::ref<au_edu_anu_mm_PeriodicFmm3d__closure__6>(new (x10aux::alloc<x10::lang::VoidFun_0_0>(sizeof(au_edu_anu_mm_PeriodicFmm3d__closure__6)))au_edu_anu_mm_PeriodicFmm3d__closure__6(__lowerer__var__9__, lowestLevelBoxes))));
                                }
                            }
                            
                        }
                    }
                }
                catch (x10aux::__ref& __ref__137) {
                    x10aux::ref<x10::lang::Throwable>& __exc__ref__137 = (x10aux::ref<x10::lang::Throwable>&)__ref__137;
                    if (true) {
                        x10aux::ref<x10::lang::Throwable> __lowerer__var__11__ =
                          static_cast<x10aux::ref<x10::lang::Throwable> >(__exc__ref__137);
                        {
                            
                            //#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
                            x10::lang::Runtime::pushException(
                              __lowerer__var__11__);
                            
                            //#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.Throw_c
                            x10aux::throwException(x10aux::nullCheck(x10::lang::RuntimeException::_make()));
                        }
                    } else
                    throw;
                }
                
                //#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
                x10::compiler::Finalization::plausibleThrow();
            }
            catch (x10aux::__ref& __ref__138) {
                x10aux::ref<x10::lang::Throwable>& __exc__ref__138 = (x10aux::ref<x10::lang::Throwable>&)__ref__138;
                if (true) {
                    x10aux::ref<x10::lang::Throwable> formal40687 =
                      static_cast<x10aux::ref<x10::lang::Throwable> >(__exc__ref__138);
                    {
                        
                        //#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalAssign_c
                        throwable40686 =
                          formal40687;
                    }
                } else
                throw;
            }
            
            //#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10If_c
            if ((!x10aux::struct_equals(X10_NULL,
                                        throwable40686)))
            {
                
                //#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10If_c
                if (x10aux::instanceof<x10aux::ref<x10::compiler::Abort> >(throwable40686))
                {
                    
                    //#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.Throw_c
                    x10aux::throwException(x10aux::nullCheck(throwable40686));
                }
                
            }
            
            //#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10If_c
            if (true) {
                
                //#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
                x10::lang::Runtime::stopFinish(
                  x10____var5);
            }
            
            //#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10If_c
            if ((!x10aux::struct_equals(X10_NULL,
                                        throwable40686)))
            {
                
                //#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10If_c
                if (!(x10aux::instanceof<x10aux::ref<x10::compiler::Finalization> >(throwable40686)))
                {
                    
                    //#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.Throw_c
                    x10aux::throwException(x10aux::nullCheck(throwable40686));
                }
                
            }
            
        }
    }
    
    //#line 189 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
    ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->
      FMGL(timer)->au::edu::anu::util::Timer::stop(
      ((x10_int)7));
}

//#line 192 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10MethodDecl_c
void au::edu::anu::mm::PeriodicFmm3d::addAtomToLowestLevelBoxAsync(
  x10aux::ref<x10::array::Point> boxIndex,
  x10x::vector::Point3d offsetCentre,
  x10_double charge) {
    
    //#line 193 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
    x10_double size = ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->
                        FMGL(size);
    
    //#line 194 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
    x10_int numTerms = ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->
                         FMGL(numTerms);
    
    //#line 195 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
    x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > lowestLevelBoxes =
      ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->
        FMGL(lowestLevelBoxes);
    
    //#line 196 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
    x10::lang::Runtime::runAsync(x10aux::nullCheck(x10aux::nullCheck(lowestLevelBoxes)->
                                                     FMGL(dist))->__apply(
                                   boxIndex),
                                 x10aux::ref<x10::lang::VoidFun_0_0>(x10aux::ref<au_edu_anu_mm_PeriodicFmm3d__closure__8>(new (x10aux::alloc<x10::lang::VoidFun_0_0>(sizeof(au_edu_anu_mm_PeriodicFmm3d__closure__8)))au_edu_anu_mm_PeriodicFmm3d__closure__8(offsetCentre, charge, lowestLevelBoxes, boxIndex, size, numTerms))));
}

//#line 212 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10MethodDecl_c
x10x::vector::Vector3d au::edu::anu::mm::PeriodicFmm3d::cancelDipole(
  x10x::vector::Vector3d dipole) {
    
    //#line 214 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
    x10x::vector::Vector3d newDipole = dipole;
    {
        
        //#line 215 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
        x10::lang::Runtime::ensureNotInAtomic();
        
        //#line 215 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::lang::FinishState> x10____var6 =
          x10::lang::Runtime::startFinish();
        {
            
            //#line 215 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::lang::Throwable> throwable40689 =
              X10_NULL;
            
            //#line 215 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.Try_c
            try {
                
                //#line 215 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.Try_c
                try {
                    {
                        
                        //#line 216 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                        x10x::vector::Point3d p1 =
                          (__extension__ ({
                            
                            //#line 216 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                            x10x::vector::Point3d this40413 =
                              (__extension__ ({
                                
                                //#line 216 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                x10x::vector::Point3d alloc24666 =
                                  
                                x10x::vector::Point3d::_alloc();
                                
                                //#line 216 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10ConstructorCall_c
                                (alloc24666)->::x10x::vector::Point3d::_constructor(
                                  ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->
                                    FMGL(size),
                                  0.0,
                                  0.0,
                                  x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
                                alloc24666;
                            }))
                            ;
                            
                            //#line 31 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                            x10x::vector::Vector3d that40412 =
                              ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->
                                FMGL(offset);
                            (__extension__ ({
                                
                                //#line 27 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                                x10x::vector::Vector3d b40414 =
                                  that40412;
                                (__extension__ ({
                                    
                                    //#line 28 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                                    x10x::vector::Point3d alloc2423740415 =
                                      
                                    x10x::vector::Point3d::_alloc();
                                    
                                    //#line 28 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10ConstructorCall_c
                                    (alloc2423740415)->::x10x::vector::Point3d::_constructor(
                                      ((this40413->
                                          FMGL(i)) + ((__extension__ ({
                                          b40414->
                                            FMGL(i);
                                      }))
                                      )),
                                      ((this40413->
                                          FMGL(j)) + ((__extension__ ({
                                          b40414->
                                            FMGL(j);
                                      }))
                                      )),
                                      ((this40413->
                                          FMGL(k)) + ((__extension__ ({
                                          b40414->
                                            FMGL(k);
                                      }))
                                      )),
                                      x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
                                    alloc2423740415;
                                }))
                                ;
                            }))
                            ;
                        }))
                        ;
                        
                        //#line 217 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                        x10_double q1 = (((-(dipole->
                                               FMGL(i)))) / (((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->
                                                               FMGL(size)));
                        
                        //#line 218 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
                        ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->addAtomToLowestLevelBoxAsync(
                          (__extension__ ({
                              
                              //#line 126 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10": x10.ast.X10LocalDecl_c
                              x10_int i040416 =
                                ((x10_int) ((((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->
                                               FMGL(lowestLevelDim)) - (((x10_int)1))));
                              (__extension__ ({
                                  
                                  //#line 126 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10": x10.ast.X10LocalDecl_c
                                  x10aux::ref<x10::array::Point> alloc2994140419 =
                                    
                                  x10aux::ref<x10::array::Point>((new (memset(x10aux::alloc<x10::array::Point>(), 0, sizeof(x10::array::Point))) x10::array::Point()))
                                  ;
                                  
                                  //#line 126 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10": x10.ast.X10ConstructorCall_c
                                  (alloc2994140419)->::x10::array::Point::_constructor(
                                    i040416,
                                    ((x10_int)0),
                                    ((x10_int)0));
                                  alloc2994140419;
                              }))
                              ;
                          }))
                          ,
                          p1,
                          q1);
                        
                        //#line 219 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalAssign_c
                        newDipole = (__extension__ ({
                            
                            //#line 33 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
                            x10x::vector::Vector3d that40424 =
                              (__extension__ ({
                                
                                //#line 219 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                x10x::vector::Vector3d this40421 =
                                  (__extension__ ({
                                    
                                    //#line 219 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                    x10x::vector::Vector3d alloc24667 =
                                      
                                    x10x::vector::Vector3d::_alloc();
                                    
                                    //#line 219 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10ConstructorCall_c
                                    (alloc24667)->::x10x::vector::Vector3d::_constructor(
                                      x10aux::class_cast_unchecked<x10aux::ref<x10x::vector::Tuple3d> >(p1),
                                      x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
                                    alloc24667;
                                }))
                                ;
                                
                                //#line 72 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
                                x10_double that40420 =
                                  q1;
                                (__extension__ ({
                                    
                                    //#line 81 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
                                    x10_double c40422 =
                                      that40420;
                                    (__extension__ ({
                                        
                                        //#line 82 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
                                        x10x::vector::Vector3d alloc2533940423 =
                                          
                                        x10x::vector::Vector3d::_alloc();
                                        
                                        //#line 82 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10ConstructorCall_c
                                        (alloc2533940423)->::x10x::vector::Vector3d::_constructor(
                                          ((this40421->
                                              FMGL(i)) * (c40422)),
                                          ((this40421->
                                              FMGL(j)) * (c40422)),
                                          ((this40421->
                                              FMGL(k)) * (c40422)),
                                          x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
                                        alloc2533940423;
                                    }))
                                    ;
                                }))
                                ;
                            }))
                            ;
                            (__extension__ ({
                                
                                //#line 37 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
                                x10x::vector::Vector3d b40425 =
                                  that40424;
                                (__extension__ ({
                                    
                                    //#line 38 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
                                    x10x::vector::Vector3d alloc2533640426 =
                                      
                                    x10x::vector::Vector3d::_alloc();
                                    
                                    //#line 38 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10ConstructorCall_c
                                    (alloc2533640426)->::x10x::vector::Vector3d::_constructor(
                                      ((newDipole->
                                          FMGL(i)) + ((__extension__ ({
                                          b40425->
                                            FMGL(i);
                                      }))
                                      )),
                                      ((newDipole->
                                          FMGL(j)) + ((__extension__ ({
                                          b40425->
                                            FMGL(j);
                                      }))
                                      )),
                                      ((newDipole->
                                          FMGL(k)) + ((__extension__ ({
                                          b40425->
                                            FMGL(k);
                                      }))
                                      )),
                                      x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
                                    alloc2533640426;
                                }))
                                ;
                            }))
                            ;
                        }))
                        ;
                        
                        //#line 221 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                        x10x::vector::Point3d p2 =
                          (__extension__ ({
                            
                            //#line 221 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                            x10x::vector::Point3d this40428 =
                              (__extension__ ({
                                
                                //#line 221 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                x10x::vector::Point3d alloc24668 =
                                  
                                x10x::vector::Point3d::_alloc();
                                
                                //#line 221 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10ConstructorCall_c
                                (alloc24668)->::x10x::vector::Point3d::_constructor(
                                  0.0,
                                  ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->
                                    FMGL(size),
                                  0.0,
                                  x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
                                alloc24668;
                            }))
                            ;
                            
                            //#line 31 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                            x10x::vector::Vector3d that40427 =
                              ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->
                                FMGL(offset);
                            (__extension__ ({
                                
                                //#line 27 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                                x10x::vector::Vector3d b40429 =
                                  that40427;
                                (__extension__ ({
                                    
                                    //#line 28 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                                    x10x::vector::Point3d alloc2423740430 =
                                      
                                    x10x::vector::Point3d::_alloc();
                                    
                                    //#line 28 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10ConstructorCall_c
                                    (alloc2423740430)->::x10x::vector::Point3d::_constructor(
                                      ((this40428->
                                          FMGL(i)) + ((__extension__ ({
                                          b40429->
                                            FMGL(i);
                                      }))
                                      )),
                                      ((this40428->
                                          FMGL(j)) + ((__extension__ ({
                                          b40429->
                                            FMGL(j);
                                      }))
                                      )),
                                      ((this40428->
                                          FMGL(k)) + ((__extension__ ({
                                          b40429->
                                            FMGL(k);
                                      }))
                                      )),
                                      x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
                                    alloc2423740430;
                                }))
                                ;
                            }))
                            ;
                        }))
                        ;
                        
                        //#line 222 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                        x10_double q2 = (((-(dipole->
                                               FMGL(j)))) / (((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->
                                                               FMGL(size)));
                        
                        //#line 223 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
                        ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->addAtomToLowestLevelBoxAsync(
                          (__extension__ ({
                              
                              //#line 126 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10": x10.ast.X10LocalDecl_c
                              x10_int i140432 =
                                ((x10_int) ((((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->
                                               FMGL(lowestLevelDim)) - (((x10_int)1))));
                              (__extension__ ({
                                  
                                  //#line 126 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10": x10.ast.X10LocalDecl_c
                                  x10aux::ref<x10::array::Point> alloc2994140434 =
                                    
                                  x10aux::ref<x10::array::Point>((new (memset(x10aux::alloc<x10::array::Point>(), 0, sizeof(x10::array::Point))) x10::array::Point()))
                                  ;
                                  
                                  //#line 126 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10": x10.ast.X10ConstructorCall_c
                                  (alloc2994140434)->::x10::array::Point::_constructor(
                                    ((x10_int)0),
                                    i140432,
                                    ((x10_int)0));
                                  alloc2994140434;
                              }))
                              ;
                          }))
                          ,
                          p2,
                          q2);
                        
                        //#line 224 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalAssign_c
                        newDipole = (__extension__ ({
                            
                            //#line 33 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
                            x10x::vector::Vector3d that40439 =
                              (__extension__ ({
                                
                                //#line 224 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                x10x::vector::Vector3d this40436 =
                                  (__extension__ ({
                                    
                                    //#line 224 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                    x10x::vector::Vector3d alloc24669 =
                                      
                                    x10x::vector::Vector3d::_alloc();
                                    
                                    //#line 224 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10ConstructorCall_c
                                    (alloc24669)->::x10x::vector::Vector3d::_constructor(
                                      x10aux::class_cast_unchecked<x10aux::ref<x10x::vector::Tuple3d> >(p2),
                                      x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
                                    alloc24669;
                                }))
                                ;
                                
                                //#line 72 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
                                x10_double that40435 =
                                  q2;
                                (__extension__ ({
                                    
                                    //#line 81 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
                                    x10_double c40437 =
                                      that40435;
                                    (__extension__ ({
                                        
                                        //#line 82 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
                                        x10x::vector::Vector3d alloc2533940438 =
                                          
                                        x10x::vector::Vector3d::_alloc();
                                        
                                        //#line 82 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10ConstructorCall_c
                                        (alloc2533940438)->::x10x::vector::Vector3d::_constructor(
                                          ((this40436->
                                              FMGL(i)) * (c40437)),
                                          ((this40436->
                                              FMGL(j)) * (c40437)),
                                          ((this40436->
                                              FMGL(k)) * (c40437)),
                                          x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
                                        alloc2533940438;
                                    }))
                                    ;
                                }))
                                ;
                            }))
                            ;
                            (__extension__ ({
                                
                                //#line 37 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
                                x10x::vector::Vector3d b40440 =
                                  that40439;
                                (__extension__ ({
                                    
                                    //#line 38 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
                                    x10x::vector::Vector3d alloc2533640441 =
                                      
                                    x10x::vector::Vector3d::_alloc();
                                    
                                    //#line 38 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10ConstructorCall_c
                                    (alloc2533640441)->::x10x::vector::Vector3d::_constructor(
                                      ((newDipole->
                                          FMGL(i)) + ((__extension__ ({
                                          b40440->
                                            FMGL(i);
                                      }))
                                      )),
                                      ((newDipole->
                                          FMGL(j)) + ((__extension__ ({
                                          b40440->
                                            FMGL(j);
                                      }))
                                      )),
                                      ((newDipole->
                                          FMGL(k)) + ((__extension__ ({
                                          b40440->
                                            FMGL(k);
                                      }))
                                      )),
                                      x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
                                    alloc2533640441;
                                }))
                                ;
                            }))
                            ;
                        }))
                        ;
                        
                        //#line 227 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                        x10x::vector::Point3d p3 =
                          (__extension__ ({
                            
                            //#line 227 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                            x10x::vector::Point3d this40443 =
                              (__extension__ ({
                                
                                //#line 227 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                x10x::vector::Point3d alloc24670 =
                                  
                                x10x::vector::Point3d::_alloc();
                                
                                //#line 227 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10ConstructorCall_c
                                (alloc24670)->::x10x::vector::Point3d::_constructor(
                                  0.0,
                                  0.0,
                                  ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->
                                    FMGL(size),
                                  x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
                                alloc24670;
                            }))
                            ;
                            
                            //#line 31 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                            x10x::vector::Vector3d that40442 =
                              ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->
                                FMGL(offset);
                            (__extension__ ({
                                
                                //#line 27 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                                x10x::vector::Vector3d b40444 =
                                  that40442;
                                (__extension__ ({
                                    
                                    //#line 28 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                                    x10x::vector::Point3d alloc2423740445 =
                                      
                                    x10x::vector::Point3d::_alloc();
                                    
                                    //#line 28 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10ConstructorCall_c
                                    (alloc2423740445)->::x10x::vector::Point3d::_constructor(
                                      ((this40443->
                                          FMGL(i)) + ((__extension__ ({
                                          b40444->
                                            FMGL(i);
                                      }))
                                      )),
                                      ((this40443->
                                          FMGL(j)) + ((__extension__ ({
                                          b40444->
                                            FMGL(j);
                                      }))
                                      )),
                                      ((this40443->
                                          FMGL(k)) + ((__extension__ ({
                                          b40444->
                                            FMGL(k);
                                      }))
                                      )),
                                      x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
                                    alloc2423740445;
                                }))
                                ;
                            }))
                            ;
                        }))
                        ;
                        
                        //#line 228 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                        x10_double q3 = (((-(dipole->
                                               FMGL(k)))) / (((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->
                                                               FMGL(size)));
                        
                        //#line 229 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
                        ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->addAtomToLowestLevelBoxAsync(
                          (__extension__ ({
                              
                              //#line 126 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10": x10.ast.X10LocalDecl_c
                              x10_int i240448 =
                                ((x10_int) ((((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->
                                               FMGL(lowestLevelDim)) - (((x10_int)1))));
                              (__extension__ ({
                                  
                                  //#line 126 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10": x10.ast.X10LocalDecl_c
                                  x10aux::ref<x10::array::Point> alloc2994140449 =
                                    
                                  x10aux::ref<x10::array::Point>((new (memset(x10aux::alloc<x10::array::Point>(), 0, sizeof(x10::array::Point))) x10::array::Point()))
                                  ;
                                  
                                  //#line 126 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10": x10.ast.X10ConstructorCall_c
                                  (alloc2994140449)->::x10::array::Point::_constructor(
                                    ((x10_int)0),
                                    ((x10_int)0),
                                    i240448);
                                  alloc2994140449;
                              }))
                              ;
                          }))
                          ,
                          p3,
                          q3);
                        
                        //#line 230 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalAssign_c
                        newDipole = (__extension__ ({
                            
                            //#line 33 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
                            x10x::vector::Vector3d that40454 =
                              (__extension__ ({
                                
                                //#line 230 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                x10x::vector::Vector3d this40451 =
                                  (__extension__ ({
                                    
                                    //#line 230 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                    x10x::vector::Vector3d alloc24671 =
                                      
                                    x10x::vector::Vector3d::_alloc();
                                    
                                    //#line 230 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10ConstructorCall_c
                                    (alloc24671)->::x10x::vector::Vector3d::_constructor(
                                      x10aux::class_cast_unchecked<x10aux::ref<x10x::vector::Tuple3d> >(p3),
                                      x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
                                    alloc24671;
                                }))
                                ;
                                
                                //#line 72 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
                                x10_double that40450 =
                                  q3;
                                (__extension__ ({
                                    
                                    //#line 81 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
                                    x10_double c40452 =
                                      that40450;
                                    (__extension__ ({
                                        
                                        //#line 82 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
                                        x10x::vector::Vector3d alloc2533940453 =
                                          
                                        x10x::vector::Vector3d::_alloc();
                                        
                                        //#line 82 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10ConstructorCall_c
                                        (alloc2533940453)->::x10x::vector::Vector3d::_constructor(
                                          ((this40451->
                                              FMGL(i)) * (c40452)),
                                          ((this40451->
                                              FMGL(j)) * (c40452)),
                                          ((this40451->
                                              FMGL(k)) * (c40452)),
                                          x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
                                        alloc2533940453;
                                    }))
                                    ;
                                }))
                                ;
                            }))
                            ;
                            (__extension__ ({
                                
                                //#line 37 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
                                x10x::vector::Vector3d b40455 =
                                  that40454;
                                (__extension__ ({
                                    
                                    //#line 38 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
                                    x10x::vector::Vector3d alloc2533640456 =
                                      
                                    x10x::vector::Vector3d::_alloc();
                                    
                                    //#line 38 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10ConstructorCall_c
                                    (alloc2533640456)->::x10x::vector::Vector3d::_constructor(
                                      ((newDipole->
                                          FMGL(i)) + ((__extension__ ({
                                          b40455->
                                            FMGL(i);
                                      }))
                                      )),
                                      ((newDipole->
                                          FMGL(j)) + ((__extension__ ({
                                          b40455->
                                            FMGL(j);
                                      }))
                                      )),
                                      ((newDipole->
                                          FMGL(k)) + ((__extension__ ({
                                          b40455->
                                            FMGL(k);
                                      }))
                                      )),
                                      x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
                                    alloc2533640456;
                                }))
                                ;
                            }))
                            ;
                        }))
                        ;
                        
                        //#line 233 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                        x10x::vector::Point3d p0 =
                          (__extension__ ({
                            
                            //#line 233 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                            x10x::vector::Point3d this40458 =
                              (__extension__ ({
                                
                                //#line 233 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                x10x::vector::Point3d alloc24672 =
                                  
                                x10x::vector::Point3d::_alloc();
                                
                                //#line 233 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10ConstructorCall_c
                                (alloc24672)->::x10x::vector::Point3d::_constructor(
                                  0.0,
                                  0.0,
                                  0.0,
                                  x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
                                alloc24672;
                            }))
                            ;
                            
                            //#line 31 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                            x10x::vector::Vector3d that40457 =
                              ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->
                                FMGL(offset);
                            (__extension__ ({
                                
                                //#line 27 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                                x10x::vector::Vector3d b40459 =
                                  that40457;
                                (__extension__ ({
                                    
                                    //#line 28 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                                    x10x::vector::Point3d alloc2423740460 =
                                      
                                    x10x::vector::Point3d::_alloc();
                                    
                                    //#line 28 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10ConstructorCall_c
                                    (alloc2423740460)->::x10x::vector::Point3d::_constructor(
                                      ((this40458->
                                          FMGL(i)) + ((__extension__ ({
                                          b40459->
                                            FMGL(i);
                                      }))
                                      )),
                                      ((this40458->
                                          FMGL(j)) + ((__extension__ ({
                                          b40459->
                                            FMGL(j);
                                      }))
                                      )),
                                      ((this40458->
                                          FMGL(k)) + ((__extension__ ({
                                          b40459->
                                            FMGL(k);
                                      }))
                                      )),
                                      x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
                                    alloc2423740460;
                                }))
                                ;
                            }))
                            ;
                        }))
                        ;
                        
                        //#line 234 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                        x10_double q0 = (-(((((q1) + (q2))) + (q3))));
                        
                        //#line 235 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
                        ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->addAtomToLowestLevelBoxAsync(
                          (__extension__ ({
                              (__extension__ ({
                                  
                                  //#line 126 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10": x10.ast.X10LocalDecl_c
                                  x10aux::ref<x10::array::Point> alloc2994140464 =
                                    
                                  x10aux::ref<x10::array::Point>((new (memset(x10aux::alloc<x10::array::Point>(), 0, sizeof(x10::array::Point))) x10::array::Point()))
                                  ;
                                  
                                  //#line 126 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10": x10.ast.X10ConstructorCall_c
                                  (alloc2994140464)->::x10::array::Point::_constructor(
                                    ((x10_int)0),
                                    ((x10_int)0),
                                    ((x10_int)0));
                                  alloc2994140464;
                              }))
                              ;
                          }))
                          ,
                          p0,
                          q0);
                        
                        //#line 236 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalAssign_c
                        newDipole = (__extension__ ({
                            
                            //#line 33 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
                            x10x::vector::Vector3d that40469 =
                              (__extension__ ({
                                
                                //#line 236 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                x10x::vector::Vector3d this40466 =
                                  (__extension__ ({
                                    
                                    //#line 236 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                    x10x::vector::Vector3d alloc24673 =
                                      
                                    x10x::vector::Vector3d::_alloc();
                                    
                                    //#line 236 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10ConstructorCall_c
                                    (alloc24673)->::x10x::vector::Vector3d::_constructor(
                                      x10aux::class_cast_unchecked<x10aux::ref<x10x::vector::Tuple3d> >(p0),
                                      x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
                                    alloc24673;
                                }))
                                ;
                                
                                //#line 72 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
                                x10_double that40465 =
                                  q0;
                                (__extension__ ({
                                    
                                    //#line 81 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
                                    x10_double c40467 =
                                      that40465;
                                    (__extension__ ({
                                        
                                        //#line 82 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
                                        x10x::vector::Vector3d alloc2533940468 =
                                          
                                        x10x::vector::Vector3d::_alloc();
                                        
                                        //#line 82 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10ConstructorCall_c
                                        (alloc2533940468)->::x10x::vector::Vector3d::_constructor(
                                          ((this40466->
                                              FMGL(i)) * (c40467)),
                                          ((this40466->
                                              FMGL(j)) * (c40467)),
                                          ((this40466->
                                              FMGL(k)) * (c40467)),
                                          x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
                                        alloc2533940468;
                                    }))
                                    ;
                                }))
                                ;
                            }))
                            ;
                            (__extension__ ({
                                
                                //#line 37 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
                                x10x::vector::Vector3d b40470 =
                                  that40469;
                                (__extension__ ({
                                    
                                    //#line 38 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
                                    x10x::vector::Vector3d alloc2533640471 =
                                      
                                    x10x::vector::Vector3d::_alloc();
                                    
                                    //#line 38 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10ConstructorCall_c
                                    (alloc2533640471)->::x10x::vector::Vector3d::_constructor(
                                      ((newDipole->
                                          FMGL(i)) + ((__extension__ ({
                                          b40470->
                                            FMGL(i);
                                      }))
                                      )),
                                      ((newDipole->
                                          FMGL(j)) + ((__extension__ ({
                                          b40470->
                                            FMGL(j);
                                      }))
                                      )),
                                      ((newDipole->
                                          FMGL(k)) + ((__extension__ ({
                                          b40470->
                                            FMGL(k);
                                      }))
                                      )),
                                      x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
                                    alloc2533640471;
                                }))
                                ;
                            }))
                            ;
                        }))
                        ;
                    }
                }
                catch (x10aux::__ref& __ref__141) {
                    x10aux::ref<x10::lang::Throwable>& __exc__ref__141 = (x10aux::ref<x10::lang::Throwable>&)__ref__141;
                    if (true) {
                        x10aux::ref<x10::lang::Throwable> __lowerer__var__12__ =
                          static_cast<x10aux::ref<x10::lang::Throwable> >(__exc__ref__141);
                        {
                            
                            //#line 215 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
                            x10::lang::Runtime::pushException(
                              __lowerer__var__12__);
                            
                            //#line 215 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.Throw_c
                            x10aux::throwException(x10aux::nullCheck(x10::lang::RuntimeException::_make()));
                        }
                    } else
                    throw;
                }
                
                //#line 215 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
                x10::compiler::Finalization::plausibleThrow();
            }
            catch (x10aux::__ref& __ref__142) {
                x10aux::ref<x10::lang::Throwable>& __exc__ref__142 = (x10aux::ref<x10::lang::Throwable>&)__ref__142;
                if (true) {
                    x10aux::ref<x10::lang::Throwable> formal40690 =
                      static_cast<x10aux::ref<x10::lang::Throwable> >(__exc__ref__142);
                    {
                        
                        //#line 215 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalAssign_c
                        throwable40689 =
                          formal40690;
                    }
                } else
                throw;
            }
            
            //#line 215 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10If_c
            if ((!x10aux::struct_equals(X10_NULL,
                                        throwable40689)))
            {
                
                //#line 215 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10If_c
                if (x10aux::instanceof<x10aux::ref<x10::compiler::Abort> >(throwable40689))
                {
                    
                    //#line 215 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.Throw_c
                    x10aux::throwException(x10aux::nullCheck(throwable40689));
                }
                
            }
            
            //#line 215 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10If_c
            if (true) {
                
                //#line 215 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
                x10::lang::Runtime::stopFinish(
                  x10____var6);
            }
            
            //#line 215 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10If_c
            if ((!x10aux::struct_equals(X10_NULL,
                                        throwable40689)))
            {
                
                //#line 215 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10If_c
                if (!(x10aux::instanceof<x10aux::ref<x10::compiler::Finalization> >(throwable40689)))
                {
                    
                    //#line 215 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.Throw_c
                    x10aux::throwException(x10aux::nullCheck(throwable40689));
                }
                
            }
            
        }
    }
    
    //#line 246 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Return_c
    return newDipole;
    
}

//#line 255 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10MethodDecl_c
x10_double au::edu::anu::mm::PeriodicFmm3d::getDirectEnergy(
  ) {
    
    //#line 257 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
    ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->
      FMGL(timer)->au::edu::anu::util::Timer::start(
      ((x10_int)2));
    
    //#line 259 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
    x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::LocallyEssentialTree> > > locallyEssentialTrees =
      ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->
        FMGL(locallyEssentialTrees);
    
    //#line 260 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
    x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > lowestLevelBoxes =
      ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->
        FMGL(lowestLevelBoxes);
    
    //#line 261 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
    x10_int lowestLevelDim = ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->
                               FMGL(lowestLevelDim);
    
    //#line 262 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
    x10_double size = ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->
                        FMGL(size);
    
    //#line 263 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
    x10_double directEnergy;
    {
        
        //#line 263 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::lang::FinishState> x10____var7 =
          x10aux::class_cast_unchecked<x10aux::ref<x10::lang::FinishState> >(x10::lang::Runtime::startCollectingFinish<x10_double >(
                                                                               x10aux::class_cast_unchecked<x10aux::ref<x10::lang::Reducible<x10_double> > >((__extension__ ({
                                                                                   
                                                                                   //#line 263 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                                                                   au::edu::anu::mm::Fmm3d__SumReducer alloc24674 =
                                                                                     
                                                                                   au::edu::anu::mm::Fmm3d__SumReducer::_alloc();
                                                                                   
                                                                                   //#line 263 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10ConstructorCall_c
                                                                                   (alloc24674)->::au::edu::anu::mm::Fmm3d__SumReducer::_constructor(
                                                                                     x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
                                                                                   alloc24674;
                                                                               }))
                                                                               )));
        {
            
            //#line 263 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::lang::Throwable> throwable40692 =
              X10_NULL;
            
            //#line 263 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.Try_c
            try {
                
                //#line 263 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.Try_c
                try {
                    {
                        {
                            
                            //#line 264 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
                            x10::lang::Runtime::ensureNotInAtomic();
                            
                            //#line 264 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                            x10aux::ref<x10::array::Dist> __lowerer__var__13__ =
                              x10aux::nullCheck(locallyEssentialTrees)->
                                FMGL(dist);
                            
                            //#line 264 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.For_c
                            {
                                x10aux::ref<x10::lang::Iterator<x10::lang::Place> > __lowerer__var__14__40673;
                                for (
                                     //#line 264 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                     __lowerer__var__14__40673 =
                                       x10aux::nullCheck(x10aux::nullCheck(__lowerer__var__13__)->places())->iterator();
                                     x10::lang::Iterator<x10::lang::Place>::hasNext(x10aux::nullCheck(__lowerer__var__14__40673));
                                     ) {
                                    
                                    //#line 264 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                                    x10::lang::Place __lowerer__var__14__ =
                                      x10::lang::Iterator<x10::lang::Place>::next(x10aux::nullCheck(__lowerer__var__14__40673));
                                    
                                    //#line 264 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
                                    x10::lang::Runtime::runAsync(
                                      __lowerer__var__14__,
                                      x10aux::ref<x10::lang::VoidFun_0_0>(x10aux::ref<au_edu_anu_mm_PeriodicFmm3d__closure__9>(new (x10aux::alloc<x10::lang::VoidFun_0_0>(sizeof(au_edu_anu_mm_PeriodicFmm3d__closure__9)))au_edu_anu_mm_PeriodicFmm3d__closure__9(__lowerer__var__13__, locallyEssentialTrees, lowestLevelBoxes, lowestLevelDim, size))));
                                }
                            }
                            
                        }
                    }
                }
                catch (x10aux::__ref& __ref__144) {
                    x10aux::ref<x10::lang::Throwable>& __exc__ref__144 = (x10aux::ref<x10::lang::Throwable>&)__ref__144;
                    if (true) {
                        x10aux::ref<x10::lang::Throwable> __lowerer__var__15__ =
                          static_cast<x10aux::ref<x10::lang::Throwable> >(__exc__ref__144);
                        {
                            
                            //#line 263 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
                            x10::lang::Runtime::pushException(
                              __lowerer__var__15__);
                            
                            //#line 263 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.Throw_c
                            x10aux::throwException(x10aux::nullCheck(x10::lang::RuntimeException::_make()));
                        }
                    } else
                    throw;
                }
                
                //#line 263 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
                x10::compiler::Finalization::plausibleThrow();
            }
            catch (x10aux::__ref& __ref__145) {
                x10aux::ref<x10::lang::Throwable>& __exc__ref__145 = (x10aux::ref<x10::lang::Throwable>&)__ref__145;
                if (true) {
                    x10aux::ref<x10::lang::Throwable> formal40693 =
                      static_cast<x10aux::ref<x10::lang::Throwable> >(__exc__ref__145);
                    {
                        
                        //#line 263 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalAssign_c
                        throwable40692 =
                          formal40693;
                    }
                } else
                throw;
            }
            
            //#line 263 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10If_c
            if ((!x10aux::struct_equals(X10_NULL,
                                        throwable40692)))
            {
                
                //#line 263 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10If_c
                if (x10aux::instanceof<x10aux::ref<x10::compiler::Abort> >(throwable40692))
                {
                    
                    //#line 263 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.Throw_c
                    x10aux::throwException(x10aux::nullCheck(throwable40692));
                }
                
            }
            
            //#line 263 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10If_c
            if (true) {
                
                //#line 263 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalAssign_c
                directEnergy = x10::lang::Runtime::stopCollectingFinish<x10_double >(
                                 x10____var7);
            }
            
            //#line 263 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10If_c
            if ((!x10aux::struct_equals(X10_NULL,
                                        throwable40692)))
            {
                
                //#line 263 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10If_c
                if (!(x10aux::instanceof<x10aux::ref<x10::compiler::Finalization> >(throwable40692)))
                {
                    
                    //#line 263 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": polyglot.ast.Throw_c
                    x10aux::throwException(x10aux::nullCheck(throwable40692));
                }
                
            }
            
        }
    }
    
    //#line 310 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Call_c
    ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->
      FMGL(timer)->au::edu::anu::util::Timer::stop(
      ((x10_int)2));
    
    //#line 312 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Return_c
    return directEnergy;
    
}

//#line 319 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10MethodDecl_c
x10x::vector::Vector3d au::edu::anu::mm::PeriodicFmm3d::getTranslation(
  x10_int lowestLevelDim,
  x10_double size,
  x10_int x,
  x10_int y,
  x10_int z) {
    
    //#line 320 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
    x10_double translationX = 0.0;
    
    //#line 321 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10If_c
    if (((x) >= (lowestLevelDim))) {
        
        //#line 322 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalAssign_c
        translationX = size;
    } else 
    //#line 323 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10If_c
    if (((x) < (((x10_int)0)))) {
        
        //#line 324 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalAssign_c
        translationX = (-(size));
    }
    
    //#line 327 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
    x10_double translationY = ((x10_double) (((x10_int)0)));
    
    //#line 328 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10If_c
    if (((y) >= (lowestLevelDim))) {
        
        //#line 329 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalAssign_c
        translationY = size;
    } else 
    //#line 330 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10If_c
    if (((y) < (((x10_int)0)))) {
        
        //#line 331 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalAssign_c
        translationY = (-(size));
    }
    
    //#line 334 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
    x10_double translationZ = ((x10_double) (((x10_int)0)));
    
    //#line 335 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10If_c
    if (((z) >= (lowestLevelDim))) {
        
        //#line 336 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalAssign_c
        translationZ = size;
    } else 
    //#line 337 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10If_c
    if (((z) < (((x10_int)0)))) {
        
        //#line 338 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalAssign_c
        translationZ = (-(size));
    }
    
    //#line 340 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 340 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
        x10x::vector::Vector3d alloc24675 =
          
        x10x::vector::Vector3d::_alloc();
        
        //#line 340 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10ConstructorCall_c
        (alloc24675)->::x10x::vector::Vector3d::_constructor(
          translationX,
          translationY,
          translationZ,
          x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
        alloc24675;
    }))
    ;
    
}

//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10MethodDecl_c
x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>
  au::edu::anu::mm::PeriodicFmm3d::au__edu__anu__mm__PeriodicFmm3d____au__edu__anu__mm__PeriodicFmm3d__this(
  ) {
    
    //#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Return_c
    return ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this);
    
}

//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10MethodDecl_c
void au::edu::anu::mm::PeriodicFmm3d::__fieldInitializers24245(
  ) {
    
    //#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>)this)->
      FMGL(X10__object_lock_id0) = ((x10_int)-1);
}
const x10aux::serialization_id_t au::edu::anu::mm::PeriodicFmm3d::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(au::edu::anu::mm::PeriodicFmm3d::_deserializer, x10aux::CLOSURE_KIND_NOT_ASYNC);

void au::edu::anu::mm::PeriodicFmm3d::_serialize_body(x10aux::serialization_buffer& buf) {
    au::edu::anu::mm::Fmm3d::_serialize_body(buf);
    buf.write(this->FMGL(numShells));
    
}

x10aux::ref<x10::lang::Reference> au::edu::anu::mm::PeriodicFmm3d::_deserializer(x10aux::deserialization_buffer& buf) {
    x10aux::ref<au::edu::anu::mm::PeriodicFmm3d> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::PeriodicFmm3d>(), 0, sizeof(au::edu::anu::mm::PeriodicFmm3d))) au::edu::anu::mm::PeriodicFmm3d();
    buf.record_reference(this_);
    this_->_deserialize_body(buf);
    return this_;
}

void au::edu::anu::mm::PeriodicFmm3d::_deserialize_body(x10aux::deserialization_buffer& buf) {
    au::edu::anu::mm::Fmm3d::_deserialize_body(buf);
    FMGL(numShells) = buf.read<x10_int>();
    
}

x10aux::RuntimeType au::edu::anu::mm::PeriodicFmm3d::rtt;
void au::edu::anu::mm::PeriodicFmm3d::_initRTT() {
    if (rtt.initStageOne(&rtt)) return;
    const x10aux::RuntimeType* parents[1] = { x10aux::getRTT<au::edu::anu::mm::Fmm3d>()};
    rtt.initStageTwo("au.edu.anu.mm.PeriodicFmm3d",x10aux::RuntimeType::class_kind, 1, parents, 0, NULL, NULL);
}
x10::lang::VoidFun_0_0::itable<au_edu_anu_mm_PeriodicFmm3d__closure__1>au_edu_anu_mm_PeriodicFmm3d__closure__1::_itable(&x10::lang::Reference::equals, &x10::lang::Closure::hashCode, &au_edu_anu_mm_PeriodicFmm3d__closure__1::__apply, &au_edu_anu_mm_PeriodicFmm3d__closure__1::toString, &x10::lang::Closure::typeName);
x10aux::itable_entry au_edu_anu_mm_PeriodicFmm3d__closure__1::_itables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::VoidFun_0_0>, &au_edu_anu_mm_PeriodicFmm3d__closure__1::_itable),x10aux::itable_entry(NULL, NULL)};

const x10aux::serialization_id_t au_edu_anu_mm_PeriodicFmm3d__closure__1::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(au_edu_anu_mm_PeriodicFmm3d__closure__1::_deserialize<x10::lang::Reference>,x10aux::CLOSURE_KIND_SIMPLE_ASYNC);

x10::lang::VoidFun_0_0::itable<au_edu_anu_mm_PeriodicFmm3d__closure__2>au_edu_anu_mm_PeriodicFmm3d__closure__2::_itable(&x10::lang::Reference::equals, &x10::lang::Closure::hashCode, &au_edu_anu_mm_PeriodicFmm3d__closure__2::__apply, &au_edu_anu_mm_PeriodicFmm3d__closure__2::toString, &x10::lang::Closure::typeName);
x10aux::itable_entry au_edu_anu_mm_PeriodicFmm3d__closure__2::_itables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::VoidFun_0_0>, &au_edu_anu_mm_PeriodicFmm3d__closure__2::_itable),x10aux::itable_entry(NULL, NULL)};

const x10aux::serialization_id_t au_edu_anu_mm_PeriodicFmm3d__closure__2::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(au_edu_anu_mm_PeriodicFmm3d__closure__2::_deserialize<x10::lang::Reference>,x10aux::CLOSURE_KIND_NOT_ASYNC);

x10::lang::VoidFun_0_0::itable<au_edu_anu_mm_PeriodicFmm3d__closure__5>au_edu_anu_mm_PeriodicFmm3d__closure__5::_itable(&x10::lang::Reference::equals, &x10::lang::Closure::hashCode, &au_edu_anu_mm_PeriodicFmm3d__closure__5::__apply, &au_edu_anu_mm_PeriodicFmm3d__closure__5::toString, &x10::lang::Closure::typeName);
x10aux::itable_entry au_edu_anu_mm_PeriodicFmm3d__closure__5::_itables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::VoidFun_0_0>, &au_edu_anu_mm_PeriodicFmm3d__closure__5::_itable),x10aux::itable_entry(NULL, NULL)};

const x10aux::serialization_id_t au_edu_anu_mm_PeriodicFmm3d__closure__5::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(au_edu_anu_mm_PeriodicFmm3d__closure__5::_deserialize<x10::lang::Reference>,x10aux::CLOSURE_KIND_SIMPLE_ASYNC);

x10::lang::VoidFun_0_0::itable<au_edu_anu_mm_PeriodicFmm3d__closure__4>au_edu_anu_mm_PeriodicFmm3d__closure__4::_itable(&x10::lang::Reference::equals, &x10::lang::Closure::hashCode, &au_edu_anu_mm_PeriodicFmm3d__closure__4::__apply, &au_edu_anu_mm_PeriodicFmm3d__closure__4::toString, &x10::lang::Closure::typeName);
x10aux::itable_entry au_edu_anu_mm_PeriodicFmm3d__closure__4::_itables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::VoidFun_0_0>, &au_edu_anu_mm_PeriodicFmm3d__closure__4::_itable),x10aux::itable_entry(NULL, NULL)};

const x10aux::serialization_id_t au_edu_anu_mm_PeriodicFmm3d__closure__4::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(au_edu_anu_mm_PeriodicFmm3d__closure__4::_deserialize<x10::lang::Reference>,x10aux::CLOSURE_KIND_SIMPLE_ASYNC);

x10::lang::VoidFun_0_0::itable<au_edu_anu_mm_PeriodicFmm3d__closure__3>au_edu_anu_mm_PeriodicFmm3d__closure__3::_itable(&x10::lang::Reference::equals, &x10::lang::Closure::hashCode, &au_edu_anu_mm_PeriodicFmm3d__closure__3::__apply, &au_edu_anu_mm_PeriodicFmm3d__closure__3::toString, &x10::lang::Closure::typeName);
x10aux::itable_entry au_edu_anu_mm_PeriodicFmm3d__closure__3::_itables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::VoidFun_0_0>, &au_edu_anu_mm_PeriodicFmm3d__closure__3::_itable),x10aux::itable_entry(NULL, NULL)};

const x10aux::serialization_id_t au_edu_anu_mm_PeriodicFmm3d__closure__3::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(au_edu_anu_mm_PeriodicFmm3d__closure__3::_deserialize<x10::lang::Reference>,x10aux::CLOSURE_KIND_SIMPLE_ASYNC);

x10::lang::VoidFun_0_0::itable<au_edu_anu_mm_PeriodicFmm3d__closure__7>au_edu_anu_mm_PeriodicFmm3d__closure__7::_itable(&x10::lang::Reference::equals, &x10::lang::Closure::hashCode, &au_edu_anu_mm_PeriodicFmm3d__closure__7::__apply, &au_edu_anu_mm_PeriodicFmm3d__closure__7::toString, &x10::lang::Closure::typeName);
x10aux::itable_entry au_edu_anu_mm_PeriodicFmm3d__closure__7::_itables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::VoidFun_0_0>, &au_edu_anu_mm_PeriodicFmm3d__closure__7::_itable),x10aux::itable_entry(NULL, NULL)};

const x10aux::serialization_id_t au_edu_anu_mm_PeriodicFmm3d__closure__7::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(au_edu_anu_mm_PeriodicFmm3d__closure__7::_deserialize<x10::lang::Reference>,x10aux::CLOSURE_KIND_SIMPLE_ASYNC);

x10::lang::VoidFun_0_0::itable<au_edu_anu_mm_PeriodicFmm3d__closure__6>au_edu_anu_mm_PeriodicFmm3d__closure__6::_itable(&x10::lang::Reference::equals, &x10::lang::Closure::hashCode, &au_edu_anu_mm_PeriodicFmm3d__closure__6::__apply, &au_edu_anu_mm_PeriodicFmm3d__closure__6::toString, &x10::lang::Closure::typeName);
x10aux::itable_entry au_edu_anu_mm_PeriodicFmm3d__closure__6::_itables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::VoidFun_0_0>, &au_edu_anu_mm_PeriodicFmm3d__closure__6::_itable),x10aux::itable_entry(NULL, NULL)};

const x10aux::serialization_id_t au_edu_anu_mm_PeriodicFmm3d__closure__6::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(au_edu_anu_mm_PeriodicFmm3d__closure__6::_deserialize<x10::lang::Reference>,x10aux::CLOSURE_KIND_SIMPLE_ASYNC);

x10::lang::VoidFun_0_0::itable<au_edu_anu_mm_PeriodicFmm3d__closure__8>au_edu_anu_mm_PeriodicFmm3d__closure__8::_itable(&x10::lang::Reference::equals, &x10::lang::Closure::hashCode, &au_edu_anu_mm_PeriodicFmm3d__closure__8::__apply, &au_edu_anu_mm_PeriodicFmm3d__closure__8::toString, &x10::lang::Closure::typeName);
x10aux::itable_entry au_edu_anu_mm_PeriodicFmm3d__closure__8::_itables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::VoidFun_0_0>, &au_edu_anu_mm_PeriodicFmm3d__closure__8::_itable),x10aux::itable_entry(NULL, NULL)};

const x10aux::serialization_id_t au_edu_anu_mm_PeriodicFmm3d__closure__8::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(au_edu_anu_mm_PeriodicFmm3d__closure__8::_deserialize<x10::lang::Reference>,x10aux::CLOSURE_KIND_SIMPLE_ASYNC);

x10::lang::VoidFun_0_0::itable<au_edu_anu_mm_PeriodicFmm3d__closure__10>au_edu_anu_mm_PeriodicFmm3d__closure__10::_itable(&x10::lang::Reference::equals, &x10::lang::Closure::hashCode, &au_edu_anu_mm_PeriodicFmm3d__closure__10::__apply, &au_edu_anu_mm_PeriodicFmm3d__closure__10::toString, &x10::lang::Closure::typeName);
x10aux::itable_entry au_edu_anu_mm_PeriodicFmm3d__closure__10::_itables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::VoidFun_0_0>, &au_edu_anu_mm_PeriodicFmm3d__closure__10::_itable),x10aux::itable_entry(NULL, NULL)};

const x10aux::serialization_id_t au_edu_anu_mm_PeriodicFmm3d__closure__10::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(au_edu_anu_mm_PeriodicFmm3d__closure__10::_deserialize<x10::lang::Reference>,x10aux::CLOSURE_KIND_SIMPLE_ASYNC);

x10::lang::VoidFun_0_0::itable<au_edu_anu_mm_PeriodicFmm3d__closure__9>au_edu_anu_mm_PeriodicFmm3d__closure__9::_itable(&x10::lang::Reference::equals, &x10::lang::Closure::hashCode, &au_edu_anu_mm_PeriodicFmm3d__closure__9::__apply, &au_edu_anu_mm_PeriodicFmm3d__closure__9::toString, &x10::lang::Closure::typeName);
x10aux::itable_entry au_edu_anu_mm_PeriodicFmm3d__closure__9::_itables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::VoidFun_0_0>, &au_edu_anu_mm_PeriodicFmm3d__closure__9::_itable),x10aux::itable_entry(NULL, NULL)};

const x10aux::serialization_id_t au_edu_anu_mm_PeriodicFmm3d__closure__9::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(au_edu_anu_mm_PeriodicFmm3d__closure__9::_deserialize<x10::lang::Reference>,x10aux::CLOSURE_KIND_SIMPLE_ASYNC);

/* END of PeriodicFmm3d */
/*************************************************/
/*************************************************/
/* START of PeriodicFmm3d$VectorSumReducer */
#include <au/edu/anu/mm/PeriodicFmm3d__VectorSumReducer.h>

#include <x10/lang/Any.h>
#include <x10/lang/Reducible.h>
#include <x10x/vector/Vector3d.h>
#include <x10/util/concurrent/Atomic.h>
#include <x10/lang/Int.h>
#include <x10/util/concurrent/OrderedLock.h>
#include <x10/util/Map.h>
#include <x10/lang/Double.h>
#include <x10/lang/String.h>
#include <x10/compiler/Native.h>
#include <x10/compiler/NonEscaping.h>
#include <x10/lang/Boolean.h>
namespace au { namespace edu { namespace anu { namespace mm { 
class PeriodicFmm3d__VectorSumReducer_ibox0 : public x10::lang::IBox<au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer> {
public:
    static x10::lang::Any::itable<PeriodicFmm3d__VectorSumReducer_ibox0 > itable;
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
x10::lang::Any::itable<PeriodicFmm3d__VectorSumReducer_ibox0 >  PeriodicFmm3d__VectorSumReducer_ibox0::itable(&PeriodicFmm3d__VectorSumReducer_ibox0::equals, &PeriodicFmm3d__VectorSumReducer_ibox0::hashCode, &PeriodicFmm3d__VectorSumReducer_ibox0::toString, &PeriodicFmm3d__VectorSumReducer_ibox0::typeName);
} } } } 
x10::lang::Any::itable<au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer >  au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer::_itable_0(&au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer::equals, &au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer::hashCode, &au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer::toString, &au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer::typeName);
namespace au { namespace edu { namespace anu { namespace mm { 
class PeriodicFmm3d__VectorSumReducer_ibox1 : public x10::lang::IBox<au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer> {
public:
    static x10::lang::Reducible<x10x::vector::Vector3d>::itable<PeriodicFmm3d__VectorSumReducer_ibox1 > itable;
    x10_boolean equals(x10aux::ref<x10::lang::Any> arg0) {
        return this->value->equals(arg0);
    }
    x10_int hashCode() {
        return this->value->hashCode();
    }
    x10x::vector::Vector3d __apply(x10x::vector::Vector3d arg0, x10x::vector::Vector3d arg1) {
        return this->value->__apply(arg0, arg1);
    }
    x10aux::ref<x10::lang::String> toString() {
        return this->value->toString();
    }
    x10aux::ref<x10::lang::String> typeName() {
        return this->value->typeName();
    }
    x10x::vector::Vector3d zero() {
        return this->value->zero();
    }
    
};
x10::lang::Reducible<x10x::vector::Vector3d>::itable<PeriodicFmm3d__VectorSumReducer_ibox1 >  PeriodicFmm3d__VectorSumReducer_ibox1::itable(&PeriodicFmm3d__VectorSumReducer_ibox1::equals, &PeriodicFmm3d__VectorSumReducer_ibox1::hashCode, &PeriodicFmm3d__VectorSumReducer_ibox1::__apply, &PeriodicFmm3d__VectorSumReducer_ibox1::toString, &PeriodicFmm3d__VectorSumReducer_ibox1::typeName, &PeriodicFmm3d__VectorSumReducer_ibox1::zero);
} } } } 
x10::lang::Reducible<x10x::vector::Vector3d>::itable<au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer >  au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer::_itable_1(&au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer::equals, &au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer::hashCode, &au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer::__apply, &au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer::toString, &au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer::typeName, &au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer::zero);
x10aux::itable_entry au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer::_itables[3] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Any>, &au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer::_itable_0), x10aux::itable_entry(&x10aux::getRTT<x10::lang::Reducible<x10x::vector::Vector3d> >, &au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer::_itable_1), x10aux::itable_entry(NULL, (void*)x10aux::getRTT<au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer>())};
x10aux::itable_entry au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer::_iboxitables[3] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Any>, &au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer_ibox0::itable), x10aux::itable_entry(&x10aux::getRTT<x10::lang::Reducible<x10x::vector::Vector3d> >, &au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer_ibox1::itable), x10aux::itable_entry(NULL, (void*)x10aux::getRTT<au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer>())};

//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10FieldDecl_c

//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock> au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer::getOrderedLock(
  ) {
    
    //#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Return_c
    return x10::util::concurrent::OrderedLock::getObjectLock((*this)->
                                                               FMGL(X10__object_lock_id0));
    
}

//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10FieldDecl_c
x10_int au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer::FMGL(X10__class_lock_id1);
void au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer::FMGL(X10__class_lock_id1__do_init)() {
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer.X10$class_lock_id1");
    x10_int __var150__ = x10::util::concurrent::OrderedLock::createClassLock();
    FMGL(X10__class_lock_id1) = __var150__;
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
}
void au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer::FMGL(X10__class_lock_id1__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var151__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(X10__class_lock_id1__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var151__ != x10aux::UNINITIALIZED) goto WAIT;
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
                                                                       _SI_("WAITING for field: au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer.X10$class_lock_id1 to be initialized");
                                                                       while (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                                       _SI_("CONTINUING because field: au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer.X10$class_lock_id1 has been initialized");
                                                                       x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__152 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer::FMGL(X10__class_lock_id1__init));

volatile x10aux::status au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer::FMGL(X10__class_lock_id1__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer::FMGL(X10__class_lock_id1__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(X10__class_lock_id1) = buf.read<x10_int>();
    au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer::FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer::FMGL(X10__class_lock_id1__id) =
  x10aux::StaticInitBroadcastDispatcher::addRoutine(au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer::FMGL(X10__class_lock_id1__deserialize));


//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock> au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer::getStaticOrderedLock(
  ) {
    
    //#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 170 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/util/concurrent/OrderedLock.x10": x10.ast.X10LocalDecl_c
        x10_int lockId40516 = au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer::
                                FMGL(X10__class_lock_id1__get)();
        x10::util::Map<x10_int, x10aux::ref<x10::util::concurrent::OrderedLock> >::getOrThrow(x10aux::nullCheck(x10::util::concurrent::OrderedLock::
                                                                                                                  FMGL(lockMap__get)()), 
          lockId40516);
    }))
    ;
    
}

//#line 344 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10MethodDecl_c
x10x::vector::Vector3d au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer::zero(
  ) {
    
    //#line 344 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Return_c
    return x10x::vector::Vector3d::FMGL(NULL__get)();
    
}

//#line 345 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10MethodDecl_c
x10x::vector::Vector3d au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer::__apply(
  x10x::vector::Vector3d a,
  x10x::vector::Vector3d b) {
    
    //#line 345 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 33 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
        x10x::vector::Vector3d that40517 =
          b;
        (__extension__ ({
            
            //#line 37 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
            x10x::vector::Vector3d b40518 =
              that40517;
            (__extension__ ({
                
                //#line 38 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
                x10x::vector::Vector3d alloc2533640519 =
                  
                x10x::vector::Vector3d::_alloc();
                
                //#line 38 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10ConstructorCall_c
                (alloc2533640519)->::x10x::vector::Vector3d::_constructor(
                  ((a->
                      FMGL(i)) + ((__extension__ ({
                      b40518->
                        FMGL(i);
                  }))
                  )),
                  ((a->
                      FMGL(j)) + ((__extension__ ({
                      b40518->
                        FMGL(j);
                  }))
                  )),
                  ((a->
                      FMGL(k)) + ((__extension__ ({
                      b40518->
                        FMGL(k);
                  }))
                  )),
                  x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
                alloc2533640519;
            }))
            ;
        }))
        ;
    }))
    ;
    
}

//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::lang::String> au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer::typeName(
  ){
    return x10aux::type_name((*this));
}

//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::lang::String> au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer::toString(
  ) {
    
    //#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Return_c
    return x10aux::string_utils::lit("struct au.edu.anu.mm.PeriodicFmm3d.VectorSumReducer");
    
}

//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10MethodDecl_c

//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10MethodDecl_c
x10_boolean au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer::equals(
  x10aux::ref<x10::lang::Any> other) {
    
    //#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10If_c
    if (!(x10aux::instanceof<au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer>(other)))
    {
        
        //#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Return_c
        return false;
        
    }
    
    //#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Return_c
    return true;
    
}

//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10MethodDecl_c

//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10MethodDecl_c
x10_boolean au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer::_struct_equals(
  x10aux::ref<x10::lang::Any> other) {
    
    //#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10If_c
    if (!(x10aux::instanceof<au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer>(other)))
    {
        
        //#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Return_c
        return false;
        
    }
    
    //#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10Return_c
    return true;
    
}

//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10MethodDecl_c

//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10MethodDecl_c

//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10ConstructorDecl_c


//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer::_constructor(
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    
    //#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.AssignPropertyCall_c
    
    //#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
        au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer this4052740622 =
          (*this);
        {
            
            //#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10FieldAssign_c
            this4052740622->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
        }
        
    }))
    ;
    
    //#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10FieldAssign_c
    (*this)->
      FMGL(X10__object_lock_id0) =
      x10aux::nullCheck(paramLock)->getIndex();
    
}
au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer::_make(
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer this_; 
    this_->_constructor(paramLock);
    return this_;
}



//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10": x10.ast.X10MethodDecl_c
void au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer::_serialize(au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer this_, x10aux::serialization_buffer& buf) {
    
}

void au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer::_deserialize_body(x10aux::deserialization_buffer& buf) {
    
}


x10aux::RuntimeType au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer::rtt;
void au::edu::anu::mm::PeriodicFmm3d__VectorSumReducer::_initRTT() {
    if (rtt.initStageOne(&rtt)) return;
    const x10aux::RuntimeType* parents[3] = { x10aux::getRTT<x10::lang::Any>(), x10aux::getRTT<x10::lang::Reducible<x10x::vector::Vector3d> >(), x10aux::getRTT<x10::lang::Any>()};
    rtt.initStageTwo("au.edu.anu.mm.PeriodicFmm3d.VectorSumReducer",x10aux::RuntimeType::struct_kind, 3, parents, 0, NULL, NULL);
    rtt.containsPtrs = false;
}
/* END of PeriodicFmm3d$VectorSumReducer */
/*************************************************/
