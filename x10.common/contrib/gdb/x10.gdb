# a few things that are useful for debugging x10 programs with gdb

set print static-members off
break x10aux::throwException
handle SIGPWR SIGXCPU nostop noprint
