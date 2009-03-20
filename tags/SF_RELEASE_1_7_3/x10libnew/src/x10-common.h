#ifndef X10_COMMON_H
#define X10_COMMON_H

#include <iostream>
//#include <assert>

namespace x10lib 
{

typedef int place_t;

typedef long long  gas_ref_t;

typedef int async_arg_t;

typedef char async_handler_t;

typedef enum { X10_OK, X10_NOT_OK, X10_ILLEGAL_ARG} error_t;

extern int init (async_handler_t* handlers, int num);

extern int finalize();

extern int cleanup();

}	

#endif /*X10_COMMON_H*/
