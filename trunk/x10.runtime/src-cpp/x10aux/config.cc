#include <cstdlib>

#include <x10aux/config.h>

bool x10aux::use_ansi_colors_;
bool x10aux::trace_alloc_;
bool x10aux::trace_init_;
bool x10aux::trace_x10rt_;
bool x10aux::trace_ser_;

bool x10aux::init_config_bools_done;

void x10aux::init_config_bools (void)
{
    use_ansi_colors_ = NULL==getenv("X10_NO_ANSI_COLORS");
    trace_alloc_ = getenv("X10_TRACE_ALLOC") || getenv("X10_TRACE_ALL");
    trace_init_ = getenv("X10_TRACE_INIT") || getenv("X10_TRACE_ALL");
    trace_x10rt_ = getenv("X10_TRACE_X10RT") || getenv("X10_TRACE_NET") || getenv("X10_TRACE_ALL");
    trace_ser_ = getenv("X10_TRACE_SER") || getenv("X10_TRACE_NET") || getenv("X10_TRACE_ALL");
    init_config_bools_done = true;
}
