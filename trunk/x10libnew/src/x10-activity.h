#ifndef X10ACTIVITY_H_
#define X10ACTIVITY_H_

typedef int32_t X10_async_arg_t;
typedef uint8_t X10_async_handler_t;

/**
 * Spawn an async locally.
 */
extern int X10_async_spawn(X10_async_handler_t handler, int numargs, ...);

/**
 * Spawn a clocked async locally.
 */
extern int X10_async_spawn_clocked(X10_async_handler_t handler, X10_clock_t* clocks, int numargs, ...);

/**
 * Spawn a switched async locally. Note: switches must always propagated to spawned activities.
 * That is, if an activity A spawns another activity B, it must always pass on its switch (if any) to B.
 */
extern int X10_async_spawn_switched(X10_async_handler_t handler, X10_switch_t* clocks, int numargs, ...);

/**
 * Spawn an async in the given place.
 */
extern int X10_async_spawn(X10_place_t place, X10_async_handler_t handler, int numargs, ...);
/**
 * Spawn a clocked async in the given place.
 */
extern int X10_async_spawn_clocked(X10_place_t place, X10_async_handler_t handler, X10_clock_t* clocks, int numargs, ...);
/**
 * Spawn a clocked async in the given place.
 */
extern int X10_async_spawn_switched(X10_place_t place, X10_async_handler_t handler, X10_switch_t* clocks, int numargs, ...);



#endif /*X10ACTIVITY_H_*/
