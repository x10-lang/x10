/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

#ifdef __cplusplus
#ifndef X10RT_CPP_H
#define X10RT_CPP_H

#include <x10rt_types.h>
#include <complex>


/** \file 
 * Utilities for C++ source files.
 */

/** A template that allows statically fetching the C++ type from the #x10rt_red_type.  To be used as
 * follows:
 * \code
x10rt_red_type_info<some_type>::Type
 * \endcode
 */
template<x10rt_red_type t> struct x10rt_red_type_info { /**The type*/ typedef void Type; };

/** \copydoc x10rt_red_type_info */
template<> struct x10rt_red_type_info<X10RT_RED_TYPE_LOGICAL>  { /**The type*/ typedef bool Type; };
/** \copydoc x10rt_red_type_info */
template<> struct x10rt_red_type_info<X10RT_RED_TYPE_U8>  { /**The type*/ typedef uint8_t Type; };
/** \copydoc x10rt_red_type_info */
template<> struct x10rt_red_type_info<X10RT_RED_TYPE_S8>  { /**The type*/ typedef int8_t Type; };
/** \copydoc x10rt_red_type_info */
template<> struct x10rt_red_type_info<X10RT_RED_TYPE_U16> { /**The type*/ typedef uint16_t Type; };
/** \copydoc x10rt_red_type_info */
template<> struct x10rt_red_type_info<X10RT_RED_TYPE_S16> { /**The type*/ typedef int16_t Type; };
/** \copydoc x10rt_red_type_info */
template<> struct x10rt_red_type_info<X10RT_RED_TYPE_U32> { /**The type*/ typedef uint32_t Type; };
/** \copydoc x10rt_red_type_info */
template<> struct x10rt_red_type_info<X10RT_RED_TYPE_S32> { /**The type*/ typedef int32_t Type; };
/** \copydoc x10rt_red_type_info */
template<> struct x10rt_red_type_info<X10RT_RED_TYPE_U64> { /**The type*/ typedef uint64_t Type; };
/** \copydoc x10rt_red_type_info */
template<> struct x10rt_red_type_info<X10RT_RED_TYPE_S64> { /**The type*/ typedef int64_t Type; };
/** \copydoc x10rt_red_type_info */
template<> struct x10rt_red_type_info<X10RT_RED_TYPE_DBL> { /**The type*/ typedef double Type; };
/** \copydoc x10rt_red_type_info */
template<> struct x10rt_red_type_info<X10RT_RED_TYPE_FLT> { /**The type*/ typedef float Type; };
/** \copydoc x10rt_red_type_info */
template<> struct x10rt_red_type_info<X10RT_RED_TYPE_DBL_S32> { /**The type*/ typedef x10rt_dbl_s32 Type; };
/** \copydoc x10rt_red_type_info */
template<> struct x10rt_red_type_info<X10RT_RED_TYPE_COMPLEX_DBL> { /**The type*/ typedef std::complex<double> Type; };


/** A template that allows statically fetching the #x10rt_red_type from a c++ type.  To be used as
 * follows:
 * \code
x10rt_red_type<some_type>()
 * \endcode
 * Note that only some types are supported.  See #x10rt_red_type.
 */
template<class T> inline x10rt_red_type x10rt_get_red_type (void) { return T::error; }

/** \copydoc x10rt_red_type */
template<> inline x10rt_red_type x10rt_get_red_type<bool>  (void) { return X10RT_RED_TYPE_LOGICAL; }
/** \copydoc x10rt_red_type */
template<> inline x10rt_red_type x10rt_get_red_type<uint8_t>  (void) { return X10RT_RED_TYPE_U8; }
/** \copydoc x10rt_red_type */
template<> inline x10rt_red_type x10rt_get_red_type<int8_t>   (void) { return X10RT_RED_TYPE_S8; }
/** \copydoc x10rt_red_type */
template<> inline x10rt_red_type x10rt_get_red_type<uint16_t> (void) { return X10RT_RED_TYPE_U16; }
/** \copydoc x10rt_red_type */
template<> inline x10rt_red_type x10rt_get_red_type<int16_t>  (void) { return X10RT_RED_TYPE_S16; }
/** \copydoc x10rt_red_type */
template<> inline x10rt_red_type x10rt_get_red_type<uint32_t> (void) { return X10RT_RED_TYPE_U32; }
/** \copydoc x10rt_red_type */
template<> inline x10rt_red_type x10rt_get_red_type<int32_t>  (void) { return X10RT_RED_TYPE_S32; }
/** \copydoc x10rt_red_type */
template<> inline x10rt_red_type x10rt_get_red_type<uint64_t> (void) { return X10RT_RED_TYPE_U64; }
/** \copydoc x10rt_red_type */
template<> inline x10rt_red_type x10rt_get_red_type<int64_t>  (void) { return X10RT_RED_TYPE_S64; }
/** \copydoc x10rt_red_type */
template<> inline x10rt_red_type x10rt_get_red_type<double>   (void) { return X10RT_RED_TYPE_DBL; }
/** \copydoc x10rt_red_type */
template<> inline x10rt_red_type x10rt_get_red_type<float>    (void) { return X10RT_RED_TYPE_FLT; }
/** \copydoc x10rt_red_type */
template<> inline x10rt_red_type x10rt_get_red_type<x10rt_dbl_s32> (void) { return X10RT_RED_TYPE_DBL_S32; }
/** \copydoc x10rt_red_type */
template<> inline x10rt_red_type x10rt_get_red_type<std::complex<double> >   (void) { return X10RT_RED_TYPE_COMPLEX_DBL; }


#endif
#endif

// vim: tabstop=4:shiftwidth=4:expandtab:textwidth=100
