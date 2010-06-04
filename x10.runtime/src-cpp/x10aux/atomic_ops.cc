#include "atomic_ops.h"

#if !defined(_LP64)
#include <x10/lang/Lock__ReentrantLock.h>
x10aux::ref<x10::lang::Lock__ReentrantLock> x10aux::atomic_ops::_longOperationLock = x10::lang::Lock__ReentrantLock::_make();

void x10aux::atomic_ops::lock() {
    _longOperationLock->lock();
}

void x10aux::atomic_ops::unlock() {
    _longOperationLock->unlock();
}
#endif

