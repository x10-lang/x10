#ifndef __X10_TYPES_H__
#define __X10_TYPES_H__

#include <stdio.h>
#include <sys/types.h>
#include <stdint.h>


#ifdef __cplusplus
#define EXTERN extern "C"
#else
#define EXTERN 
#endif

#ifndef __cplusplus
#define bool char
#endif


typedef bool     x10_boolean_t;
typedef int8_t   x10_byte_t;
typedef uint16_t x10_char_t;
typedef int16_t  x10_short_t;
typedef int32_t  x10_int_t;
typedef int64_t  x10_long_t;
typedef float    x10_float_t;
typedef double   x10_double_t;

typedef unsigned char* x10_addr_t;

/*	place 		*/
typedef x10_long_t	x10_place_t;

typedef enum { X10_OK, X10_NOT_OK} x10_err_t;

typedef struct
{
  x10_long_t finish_id; /* a dynamic UNIQUE number for every finish scope */
  x10_place_t finish_root; /* root of the async */
} x10_finish_record_t;

typedef struct
{
  x10_long_t handler;
} x10_async_closure_t;

typedef struct
{
  void* rts_handle;
  void* header_buf;
} x10_comm_handle_t;

/*	clock	*/
typedef struct
{
   /* not defined yet */
} x10_clock_t;

typedef struct
{
  x10_place_t loc;
  x10_addr_t addr;
} x10_proxy_t;

typedef x10_proxy_t x10_remote_ref_t;

/*	condition variables	*/
typedef  unsigned 	x10_condition_t;


#ifdef __cplusplus 
namespace X10
{
  typedef x10_addr_t Addr;

  typedef x10_err_t Err;

  typedef x10_place_t Place;

  typedef x10_async_closure_t AsyncClosure;

  typedef x10_finish_record_t FinishRecord;
  
  typedef x10_comm_handle_t CommHandle;
  
  typedef x10_clock_t Clock;
  
  typedef x10_proxy_t Proxy;

  typedef x10_remote_ref_t RemoteRef;

  typedef x10_condition_t Condition;  
}
#endif

#endif /* X10 TYPES */
