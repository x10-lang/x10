.SUFFIXES: .o


MPI_CC  = /usr/bin/mpCC
CC      = xlc

FFT_HOME = /vol/x10/fftw3
FFT_INC  = $(FFT_HOME)/include
FFT_LIB  = $(FFT_HOME)/lib

MPI_INC     = /usr/local/include
PTHREAD_INC = /usr/include

CPAD_COLS = 1
AUX_LIBS  = ""

# CC/CFLAGS and should never be ?=, because Make always defines them
CFLAGS  =  -q64 -I. -I$(FFT_INC) -I$(MPI_INC) -I$(PTHREAD_INC) -O3 -DCPAD_COLS=$(CPAD_COLS) -qarch=pwr5 -qtune=pwr5 -qhot -qnoinline

COMOBJS = c_randdp.o verify.o init_exp.o

FFTW_INCL = -I. -I$(FFT_INC)
#FFTW_OBJS = $(COMOBJS) fft_fftw2.o
#FFTW_LIBS = -L$(FFT_LIB) -lfftw -lm

FFTW_OBJS = $(COMOBJS) fft_fftw3.o
FFTW_LIBS = -L$(FFT_LIB) -lfftw3 -lm 

ALL_OBJS = $(FFTW_OBJS) $(FFT_OBJS)

all: 
	@for c in AA BB CC DD; do			  \
	    $(MAKE) ft-mpi CLASS=$$c  || exit $?;  \
	    $(MAKE) ft-mpi-slabs CLASS=$$c  || exit $?;  \
	    $(MAKE) ft-mpi-pencils CLASS=$$c  || exit $?;  \
	    $(MAKE) ft-lapi-slabs CLASS=$$c  || exit $?;  \
	    $(MAKE) ft-lapi-pencils CLASS=$$c || exit $?; \
	done

$(COMOBJS): ft.h params.h $(COMOBJS:.o=.c)
	$(CC) $(CFLAGS) -c $(@:.o=.c)

fft_fftw3.o: ft.h params.h fft_fftw3.c
	$(CC) $(CFLAGS) -c fft_fftw3.c

ft-lapi-slabs: contrib/ft_lapi.c $(COMOBJS) fft_fftw3.c
	rm -rf fft_fftw3.o
	$(CC) $(CFLAGS) -DMAKE_FFTW_THREADSAFE=0 -c fft_fftw3.c 
	$(MPI_CC) -DCLASS=$(CLASS) $(CFLAGS) -I. -DFT_COMM=0 contrib/ft_lapi.c  \
		-o $@.$(CLASS) $(FFTW_OBJS) timers.c  -I$(FFT_INC) -L$(FFT_LIB) \
		-lfftw3 -lm $(AUX_LIBS) 

ft-lapi-pencils: contrib/ft_lapi.c $(COMOBJS) fft_fftw3.c
	rm -rf fft_fftw3.o
	$(CC) $(CFLAGS) -DMAKE_FFTW_THREADSAFE=0 -c fft_fftw3.c 
	$(MPI_CC) -DCLASS=$(CLASS) $(CFLAGS) -I. -DFT_COMM=1 contrib/ft_lapi.c  \
		-o $@.$(CLASS) $(FFTW_OBJS) timers.c  -I$(FFT_INC) -L$(FFT_LIB) \
		-lfftw3 -lm $(AUX_LIBS) 

ft-fftw-mpi: $(COMOBJS)
	if test "$(CPAD_COLS)" != 0 ; then echo "ERROR: CPAD_COLS=$(CPAD_COLS)" ; exit 1; fi
	$(MPI_CC) -DCLASS=$(CLASS) $(CFLAGS) -I. contrib/ft_fftw_mpi.c \
		-o $@.$(CLASS) $(FFT_OBJS) timers.c  -I$(FFT_INC) -L$(FFT_LIB) \
		 -lfftw_mpi -lfftw -lm $(AUX_LIBS) 

ft-mpi: contrib/ft_mpi.c $(COMOBJS) fft_fftw3.c
	rm -rf fft_fftw3.o
	$(CC) $(CFLAGS) -DMAKE_FFTW_THREADSAFE=0 -c fft_fftw3.c 
	$(MPI_CC) -DCLASS=$(CLASS) $(CFLAGS) -I. -DFT_COMM=2 contrib/ft_mpi.c   \
		-o $@.$(CLASS) $(FFTW_OBJS) timers.c  -I$(FFT_INC) -L$(FFT_LIB) \
		-lfftw3 -lm $(AUX_LIBS) 

ft-mpi-slabs: contrib/ft_mpi.c $(COMOBJS) fft_fftw3.c
	rm -rf fft_fftw3.o
	$(CC) $(CFLAGS) -DMAKE_FFTW_THREADSAFE=0 -c fft_fftw3.c 
	$(MPI_CC) -DCLASS=$(CLASS) $(CFLAGS) -I. -DFT_COMM=0 contrib/ft_mpi.c   \
		-o $@.$(CLASS) $(FFTW_OBJS) timers.c  -I$(FFT_INC) -L$(FFT_LIB) \
		-lfftw3 -lm $(AUX_LIBS) 

ft-mpi-pencils: contrib/ft_mpi.c  $(COMOBJS) fft_fftw3.c
	rm -rf fft_fftw3.o
	$(CC) $(CFLAGS) -DMAKE_FFTW_THREADSAFE=0 -c fft_fftw3.c
	$(MPI_CC) -DCLASS=$(CLASS) $(CFLAGS) -I. -DFT_COMM=1 contrib/ft_mpi.c   \
		-o $@.$(CLASS) $(FFTW_OBJS) timers.c  -I$(FFT_INC) -L$(FFT_LIB) \
		-lfftw3 -lm $(AUX_LIBS) 

clean:
	rm -rf *.o mpi-ft-bench ft-mpi.[ABCDSW][ABCDWS] ft-mpi-pencils.* \
	ft-fftw-mpi.* ft-lapi-pencils.* *~ ft-lapi-slabs.* ft-mpi-slabs.*

