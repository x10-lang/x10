/*************************************************/
/* START of Tuple3d */
#include <x10x/vector/Tuple3d.h>

#include <x10/lang/Any.h>
#include <x10/util/concurrent/Atomic.h>
#include <x10/lang/Double.h>
#include <x10/lang/String.h>
x10aux::RuntimeType x10x::vector::Tuple3d::rtt;
void x10x::vector::Tuple3d::_initRTT() {
    if (rtt.initStageOne(&rtt)) return;
    const x10aux::RuntimeType* parents[1] = { x10aux::getRTT<x10::lang::Any>()};
    rtt.initStageTwo("x10x.vector.Tuple3d",x10aux::RuntimeType::interface_kind, 1, parents, 0, NULL, NULL);
}
/* END of Tuple3d */
/*************************************************/
