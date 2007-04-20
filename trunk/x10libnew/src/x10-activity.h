#ifndef X10ACTIVITY_H_
#define X10ACTIVITY_H_

#include "x10-common.h"

namespace x10lib{


namespace acts{

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
 
 
/**
 * Handling Futures
 *
 */
 
template<typename result_t>
class Future {
	public:
   		Future () {};
   		result_t force ();
  
 	private:
 		result_t getResult();
 		void setResult(const result_t& result);
};
 
 /**
  * Create future locally using either clocks or switches
  * (Future<result_t> is the return type) 
  */ 
template<typename result_t>
Future<result_t> future_spawn (async_handler_t handler, int numargs, ...);
template<typename result_t>
Future<result_t> future_spawn_clocked (async_handler_t handler, clock_t clocks, int numargs, ...);
template<typename result_t>
Future<result_t> future_spawn_switched (async_handler_t handler, switch_t clocks, int numargs, ...);

/**
  * Create future remotely using either clocks or switches
  * (Future<result_t> is the return type)
  */ 
template<typename result_t>
Future<result_t> future_spawn (place_t place, async_handler_t handler, int numargs, ...);
template<typename result_t>
Future<result_t> future_spawn_clocked (place_t place, async_handler_t handler, clock_t clocks, int numargs, ...);
template<typename result_t>
Future<result_t> future_spawn_switched (place_t place, async_handler_t handler, switch_t clocks, int numargs, ...);

}
}

#endif /*X10ACTIVITY_H_*/
