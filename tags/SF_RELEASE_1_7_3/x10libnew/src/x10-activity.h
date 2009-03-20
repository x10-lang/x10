#ifndef X10ACTIVITY_H_
#define X10ACTIVITY_H_

#include "x10-common.h"
#include "x10-switch.h"

namespace x10lib{

/**
 * Spawn an async locally.
 */
 error_t async_spawn (async_handler_t handler, int numargs, ...);

		
/**
 * Spawn a clocked async locally.
 */
 error_t async_spawn_clocked (async_handler_t handler, clock_t clocks, int numargs, ...);

/**
 * Spawn a switched async locally. Note: switches must always propagated to spawned activities.
 * That is, if an activity A spawns another activity B, it must always pass on its switch (if any) to B.
 */
 error_t async_spawn_switched (async_handler_t handler, switch_t clocks, int numargs, ...);


/**
 * Spawn an async in the given place.
 */
 error_t async_spawn (place_t place, async_handler_t handler, int numargs, ...);

/**
 * Spawn a clocked async in the given place.
 */
 error_t async_spawn_clocked (place_t place, async_handler_t handler, clock_t clocks, int numargs, ...);


/**
 * Spawn a clocked async in the given place.
 */
 error_t async_spawn_switched (place_t place, async_handler_t handler, switch_t clocks, int numargs, ...);

}
#endif /*X10ACTIVITY_H_*/
