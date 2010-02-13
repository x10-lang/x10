#include <x10aux/debug.h>

// A hook at the start of every X10 method.
void _X10_Entry_Hook() { }

// A hook at the end of every X10 method.
void _X10_Exit_Hook() { }

// A hook at the start of every X10 executable statement.
// Follows any method start hook, and precedes any method end hook.
void _X10_Statement_Hook() { }

// vim:tabstop=4:shiftwidth=4:expandtab
