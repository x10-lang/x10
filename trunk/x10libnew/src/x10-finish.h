#ifndef X10FINISH_H_
#define X10FINISH_H_

#include "x10-common.h"

namespace x10lib{
namespace finish{

/* visible to external world */
error_t start_finish (async_handler_t handler, int numargs, ...);
error_t stop_finish (async_handler_t handler, int numargs, ...);

/* not visible to external world */
void propagate_exception (async_handler_t handler, int numargs, ...);

}
}

#endif
