#include <inttypes.h>
#ifdef __INTEL_COMPILER
#include <stdint.h>
#endif

#include <sys/time.h>
#include <stdlib.h>
#include <stdio.h>

#include "ft.h"

static char *FFTimers_descr[T_NUMTIMERS] = {
		"Setup", "1D FFT Cols", "1D FFT Rows", "1D FFT Last",
		"Evolve Computation", "Checksums",
		"Exchange", "Exchange Wait", 
		"Barrier Waits", "Barrier at checksum", "Total"
};

#if defined(__BERKELEY_UPC__) && defined(BUPC_TICK_MAX)
typedef bupc_tick_t ft_timer_t;
#else
typedef uint64_t    ft_timer_t;
#endif

static ft_timer_t FTTimers_begin[T_NUMTIMERS];
static ft_timer_t FTTimers_total[T_NUMTIMERS] = { 0 };


uint64_t    timer_val(int tid);
void	    timer_update(int tid, int action);
void	    timer_clear();
char	    *timer_descr(int tid);

/*
 * Timers
 */
#if defined(__BERKELEY_UPC__) && defined(BUPC_TICK_MAX)
#  define get_ticks() bupc_ticks_now()
#else
#if 0
static inline
#endif
uint64_t get_ticks() {
  uint64_t retval;
  struct timeval tv;
  if (gettimeofday(&tv, NULL)) {
    perror("gettimeofday");
    abort();
  }
  retval = ((int64_t)tv.tv_sec) * 1000000 + tv.tv_usec;
  return retval;
}
#endif

void
timer_update(int tid, int action)
{
    if (action == FT_TIME_BEGIN) {
	FTTimers_begin[tid] = get_ticks();
    }
    else { /* FT_TIME_END */
	FTTimers_total[tid] += (get_ticks() - FTTimers_begin[tid]);
    }
}

void timer_clear()
{
	int i;
	for(i=0; i<T_NUMTIMERS; i++) {
		FTTimers_total[i] = 0;
	}
}

uint64_t timer_val(int tid)
{
    #if defined(__BERKELEY_UPC__) && defined(BUPC_TICK_MAX)
      return (uint64_t) bupc_ticks_to_us(FTTimers_total[tid]);
    #else
      return FTTimers_total[tid];
    #endif
}

char *timer_descr(int tid)
{
    return FFTimers_descr[tid];
}


