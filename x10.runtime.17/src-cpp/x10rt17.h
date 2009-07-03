#ifndef X10RT17_H
#define X10RT17_H

// include everything from this file

#include <x10aux/config.h>

// has to be first to ensure initialisation of pgas occurs before uses of x10aux::alloc
#include <x10aux/pgas.h>

#include <x10aux/class_cast.h>
#include <x10aux/ref.h>
#include <x10aux/reference_logger.h>
#include <x10aux/alloc.h>
#include <x10aux/serialization.h>
#include <x10aux/deserialization_dispatcher.h>
#include <x10aux/throw.h>
#include <x10aux/RTT.h>
#include <x10aux/assert.h>
#include <x10aux/init_dispatcher.h>
#include <x10aux/hash.h>
#include <x10aux/basic_functions.h>
#include <x10aux/atomic_ops.h>

#include <x10aux/itables.h>

#include <x10aux/boolean_utils.h>
#include <x10aux/byte_utils.h>
#include <x10aux/char_utils.h>
#include <x10aux/double_utils.h>
#include <x10aux/int_utils.h>
#include <x10aux/float_utils.h>
#include <x10aux/long_utils.h>
#include <x10aux/short_utils.h>
#include <x10aux/string_utils.h>
#include <x10aux/rail_utils.h>

#include <x10aux/math_utils.h>
#include <x10aux/system_utils.h>
#include <x10aux/iterator_utils.h>

#include <x10aux/cuda/cuda_utils.h>
#include <x10aux/cuda/bridge_buffer.h>
#include <x10aux/cuda/ring_buffer.h>

//#include <x10/io/File.h>
#include <x10/io/FileInputStream.h>
#include <x10/io/FileOutputStream.h>
#include <x10/io/NativeFile.h>
#include <x10/io/NativeInputStream.h>
#include <x10/io/NativeOutputStream.h>

#include <x10/lang/Box.h>
#include <x10/lang/Fun_0_0.h>
#include <x10/lang/Fun_0_1.h>
#include <x10/lang/Fun_0_2.h>
#include <x10/lang/Fun_0_3.h>
#include <x10/lang/Fun_0_4.h>
#include <x10/lang/Fun_0_5.h>
#include <x10/lang/Fun_0_6.h>
#include <x10/lang/Fun_0_7.h>
#include <x10/lang/Fun_0_8.h>
#include <x10/lang/Fun_0_9.h>
#include <x10/lang/Iterator.h>
#include <x10/lang/Iterable.h>
#include <x10/lang/Object.h>
#include <x10/lang/Rail.h>
#include <x10/lang/Ref.h>
#include <x10/lang/Settable.h>
#include <x10/lang/String.h>
#include <x10/lang/Throwable.h>
#include <x10/lang/ValRail.h>
#include <x10/lang/Value.h>
#include <x10/lang/VoidFun_0_0.h>
#include <x10/lang/VoidFun_0_1.h>
#include <x10/lang/VoidFun_0_2.h>
#include <x10/lang/VoidFun_0_3.h>
#include <x10/lang/VoidFun_0_4.h>
#include <x10/lang/VoidFun_0_5.h>
#include <x10/lang/VoidFun_0_6.h>
#include <x10/lang/VoidFun_0_7.h>
#include <x10/lang/VoidFun_0_8.h>
#include <x10/lang/VoidFun_0_9.h>

#include <x10/runtime/Deque.h>
#include <x10/runtime/Lock.h>
#include <x10/runtime/Thread.h>

#include <x10/util/GrowableRail.h>
#include <x10/util/concurrent/atomic/AtomicBoolean.h>
#include <x10/util/concurrent/atomic/AtomicInteger.h>
#include <x10/util/concurrent/atomic/AtomicLong.h>
#include <x10/util/concurrent/atomic/AtomicReference.h>

// has to be last to ensure the native classes have been included
#include <x10aux/bootstrap.h>

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
