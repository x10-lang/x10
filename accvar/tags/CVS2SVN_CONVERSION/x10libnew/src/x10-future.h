#ifndef _FUTURE_H_
#define _FUTURE_H_

namespace x10lib{

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

#endif _FUTURE_H_