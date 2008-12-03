#!/bin/tcsh
setenv CEO_MILESTONE `pwd`
setenv FFT_HOME $CEO_MILESTONE/fftw3
setenv MPI_CC /usr/bin/mpCC
setenv CC xlc

#setenv LAM_MPI_SESSION_PREFIX /tmp/lam-mwgs

setenv MP_TASK_AFFINITY MCM
setenv MP_EUIDEVICE sn_all
setenv MP_ADAPTER_USE dedicated
setenv MP_EUILIB us
setenv MP_CSS_INTERRUPT no    # This one really harms MPI's performance
#setenv MP_USE_BULK_XFER yes 
# setenv MP_EAGER_LIMIT 65536 
# setenv MP_PULSE 0 
# setenv MP_POLLING_INTERVAL 2000000000 
setenv MP_SINGLE_THREAD yes 
#setenv MP_WAIT_MODE poll
#setenv MP_BULK_MIN_MSG_SIZE 4096



setenv MP_MSG_API "lapi,mpi"
setenv MP_CPU_USE unique 
setenv LAPI_USE_SHM yes
setenv MP_SHARED_MEMORY yes 
