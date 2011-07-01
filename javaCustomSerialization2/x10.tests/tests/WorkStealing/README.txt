This directory contains test cases for testing the WorkStealing component of x10.

Usage instructions
1.Apply the scriptPatch to x10.tests/bin
  Why? Remove the "-h" check in newpgrp. In some GSA directory, the path contains "-h" pattern.
       Add "-MAIN_CLASS=" parameter for c++ path compiling

2.Define environment variables (only for CPP now
  Define X10C_EXTRA_OPTS
    export X10C_EXTRA_OPTS="-O -WORK_STEALING=true -sourcepath ../../../x10.dist/samples -sourcepath ../../../x10.dist/samples/work-stealing"
  note: the sourcepath is used to point the x10.dist's samples and work-stealing samples
  Define USE_X10CPP
    export USE_X10CPP=1
  Define work-stealing thread number
    export X10_NTHREADS=4

3. Run test in WorkStealing directory directly
  ../../bin/testScript -clean -f
  
  note, for some reasons, we need clean the temp files
    rm *.cc *.h *.inc
    
     