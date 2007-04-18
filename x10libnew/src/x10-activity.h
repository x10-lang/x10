#ifndef X10ACTIVITY_H_
#define X10ACTIVITY_H_

typedef int32_t x10_async_arg_t;
typedef uint8_t x10_async_handler_t;

namespace x10lib{

/**
 * Spawn an async locally.
 */
extern int x10_async_spawn(x10_async_handler_t handler, int numargs, ...);

/**
 * Spawn a clocked async locally.
 */
extern int x10_async_spawn_clocked(x10_async_handler_t handler, x10_clock_t* clocks, int numargs, ...);

/**
 * Spawn a switched async locally. Note: switches must always propagated to spawned activities.
 * That is, if an activity A spawns another activity B, it must always pass on its switch (if any) to B.
 */
extern int x10_async_spawn_switched(x10_async_handler_t handler, x10_switch_t* clocks, int numargs, ...);

/**
 * Spawn an async in the given place.
 */
extern int x10_async_spawn(x10_place_t place, x10_async_handler_t handler, int numargs, ...);
/**
 * Spawn a clocked async in the given place.
 */
extern int x10_async_spawn_clocked(x10_place_t place, x10_async_handler_t handler, x10_clock_t* clocks, int numargs, ...);
/**
 * Spawn a clocked async in the given place.
 */
extern int x10_async_spawn_switched(x10_place_t place, x10_async_handler_t handler, x10_switch_t* clocks, int numargs, ...);

}


#endif /*X10ACTIVITY_H_*/
