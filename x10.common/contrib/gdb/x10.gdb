# a few things that are useful for debugging x10 programs with gdb

set print static-members off
break x10aux::throwNPE
break x10aux::throwBPE
break x10aux::throwArrayIndexOutOfBoundsException
break x10aux::throwOOME
handle SIGPWR SIGXCPU nostop noprint
