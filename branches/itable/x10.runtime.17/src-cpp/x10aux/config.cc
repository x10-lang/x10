#include <cstdlib>

#include <x10aux/config.h>

bool x10aux::use_ansi_colors;
bool x10aux::trace_alloc;
bool x10aux::trace_init;
bool x10aux::trace_x10rt;
bool x10aux::trace_ser;

void x10aux::init_config_bools (void)
{
    use_ansi_colors = NULL==getenv("X10_NO_ANSI_COLORS");
    trace_alloc = getenv("X10_TRACE_ALLOC") || getenv("X10_TRACE_ALL");
    trace_init = getenv("X10_TRACE_INIT") || getenv("X10_TRACE_ALL");
    trace_x10rt = getenv("X10_TRACE_X10RT") || getenv("X10_TRACE_ALL");
    trace_ser = getenv("X10_TRACE_SER") || getenv("X10_TRACE_ALL");
}
