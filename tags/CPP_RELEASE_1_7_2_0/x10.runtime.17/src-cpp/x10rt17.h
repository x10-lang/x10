#ifndef X10RT17_H
#define X10RT17_H

// include everything from this file

#include <x10aux/config.h>

// has to be first to ensure initialisation of pgas occurs before uses of x10aux::alloc
#include <x10aux/pgas.h>

#include <x10aux/bootstrap.h>
#include <x10aux/class_cast.h>
#include <x10aux/ref.h>
#include <x10aux/alloc.h>
#include <x10aux/serialization.h>
#include <x10aux/deserialization_dispatcher.h>
#include <x10aux/throw.h>
#include <x10aux/RTT.h>
#include <x10aux/assert.h>
#include <x10aux/init_dispatcher.h>

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


#include <x10/io/EOFException.h>
//#include <x10/io/File.h>
#include <x10/io/FileInputStream.h>
#include <x10/io/FileOutputStream.h>
#include <x10/io/FileNotFoundException.h>
#include <x10/io/IOException.h>
#include <x10/io/NativeFile.h>
#include <x10/io/NativeInputStream.h>
#include <x10/io/NativeOutputStream.h>

#include <x10/lang/ArrayIndexOutOfBoundsException.h>
#include <x10/lang/BadPlaceException.h>
#include <x10/lang/Box.h>
#include <x10/lang/ClassCastException.h>
#include <x10/lang/Exception.h>
#include <x10/lang/Fun_0_0.h>
#include <x10/lang/Fun_0_1.h>
#include <x10/lang/Fun_0_2.h>
#include <x10/lang/Iterator.h>
#include <x10/lang/NullPointerException.h>
#include <x10/lang/Object.h>
#include <x10/lang/Rail.h>
#include <x10/lang/Ref.h>
#include <x10/lang/RuntimeException.h>
#include <x10/lang/String.h>
#include <x10/lang/Throwable.h>
#include <x10/lang/ValRail.h>
#include <x10/lang/Value.h>
#include <x10/lang/VoidFun_0_0.h>
#include <x10/lang/VoidFun_0_1.h>

#include <x10/runtime/Lock.h>
#include <x10/runtime/Thread.h>
#include <x10/runtime/InterruptedException.h>

#include <x10/util/GrowableRail.h>

using namespace x10::lang;

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
