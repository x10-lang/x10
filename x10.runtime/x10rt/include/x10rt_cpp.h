#ifndef X10RT_CPP_H
#define X10RT_CPP_H

#include <x10rt_types.h>


// This header is for C++ source files only.

// should never hit this, check specialisations are working
template<x10rt_red_type t> struct x10rt_red_type_info { };

template<> struct x10rt_red_type_info<X10RT_RED_TYPE_U8>  { typedef uint8_t Type; };
template<> struct x10rt_red_type_info<X10RT_RED_TYPE_S8>  { typedef int8_t Type; };
template<> struct x10rt_red_type_info<X10RT_RED_TYPE_U16> { typedef uint16_t Type; };
template<> struct x10rt_red_type_info<X10RT_RED_TYPE_S16> { typedef int16_t Type; };
template<> struct x10rt_red_type_info<X10RT_RED_TYPE_U32> { typedef uint32_t Type; };
template<> struct x10rt_red_type_info<X10RT_RED_TYPE_S32> { typedef int32_t Type; };
template<> struct x10rt_red_type_info<X10RT_RED_TYPE_U64> { typedef uint64_t Type; };
template<> struct x10rt_red_type_info<X10RT_RED_TYPE_S64> { typedef int64_t Type; };
template<> struct x10rt_red_type_info<X10RT_RED_TYPE_DBL> { typedef double Type; };
template<> struct x10rt_red_type_info<X10RT_RED_TYPE_FLT> { typedef float Type; };

// should never hit this, check specialisations are working
template<class T> inline x10rt_red_type x10rt_get_red_type (void) { return T::error; }

template<> inline x10rt_red_type x10rt_get_red_type<uint8_t>  (void) { return X10RT_RED_TYPE_U8; }
template<> inline x10rt_red_type x10rt_get_red_type<int8_t>   (void) { return X10RT_RED_TYPE_S8; }
template<> inline x10rt_red_type x10rt_get_red_type<uint16_t> (void) { return X10RT_RED_TYPE_U16; }
template<> inline x10rt_red_type x10rt_get_red_type<int16_t>  (void) { return X10RT_RED_TYPE_S16; }
template<> inline x10rt_red_type x10rt_get_red_type<uint32_t> (void) { return X10RT_RED_TYPE_U32; }
template<> inline x10rt_red_type x10rt_get_red_type<int32_t>  (void) { return X10RT_RED_TYPE_S32; }
template<> inline x10rt_red_type x10rt_get_red_type<uint64_t> (void) { return X10RT_RED_TYPE_U64; }
template<> inline x10rt_red_type x10rt_get_red_type<int64_t>  (void) { return X10RT_RED_TYPE_S64; }
template<> inline x10rt_red_type x10rt_get_red_type<double>   (void) { return X10RT_RED_TYPE_DBL; }
template<> inline x10rt_red_type x10rt_get_red_type<float>    (void) { return X10RT_RED_TYPE_FLT; }

#endif

// vim: tabstop=4:shiftwidth=4:expandtab:textwidth=100
