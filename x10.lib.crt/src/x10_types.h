#ifndef __X10_TYPES_H__
#define __X10_TYPES_H__

#include <stdio.h>

#ifdef __cplusplus
#define EXTERN extern "C"
#else
#define EXTERN 
#endif

#ifndef __cplusplus
#define bool char
#endif

typedef unsigned char* x10_addr_t;

/*	place 		*/
typedef unsigned 	x10_place_t;

/*	async handler 	*/
typedef unsigned 	x10_async_handler_t;

typedef enum { X10_OK, X10_NOT_OK} x10_err_t;

typedef struct
{
  int finish_id; /* a dynamic UNIQUE number for every finish scope */
  x10_place_t finish_root; /* root of the async */
} x10_finish_record_t;

typedef struct
{
  x10_async_handler_t handler;
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

#endif /* X10 TYPES */
