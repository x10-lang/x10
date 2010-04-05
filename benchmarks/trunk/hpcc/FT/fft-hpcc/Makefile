# Makefile for builiding hpcc fft routine

LIBFFT	:= libfftw3.a

all: $(LIBFFT)

SRCS = fft235.c wrapfftw.c zfft1d.c
OBJS = fft235.o wrapfftw.o zfft1d.o

$(LIBFFT): $(OBJS)
	${POSTCOMPILE_AR} ${POSTCOMPILE_ARFLAGS} $(LIBFFT) $(OBJS)
	${POSTCOMPILE_RANLIB} $@

%:%.o

%.o: %.c
	$(CXX) -Iinclude/ ${POSTCOMPILE_NATIVE_LIB_FLAGS} -c  $<

clean:
	${RM} *.o *.a

.PHONY:	all clean
