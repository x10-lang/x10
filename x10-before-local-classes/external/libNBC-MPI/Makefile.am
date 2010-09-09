include_HEADERS = nbc.h

sources = \
	$(include_HEADERS) \
	nbc.c \
	nbc_fortran.c \
	nbc_iallgather.c \
	nbc_iallgatherv.c \
	nbc_iallreduce.c \
	nbc_ialltoall.c \
	nbc_ialltoallv.c \
	nbc_ibarrier.c \
	nbc_ibcast.c \
	nbc_igather.c \
	nbc_igatherv.c \
	nbc_ireduce.c \
	nbc_ireduce_scatter.c \
	nbc_iscan.c \
	nbc_iscatter.c \
	nbc_iscatterv.c \
	nbc_icart_shift_xchg.c \
	nbc_ineighbor_xchg.c \
	nbc_ineighbor_xchgv.c \
	nbc_op.c \
	nbc_internal.h \
	LICENSE \
	CHANGELOG

noinst_HEADERS = nbc_internal.h nbc_ompi_include.h

if HAVE_OFED
ofdir = of
oflibs = of/libof.la
endif

SUBDIRS = libdict $(ofdir)

lib_LTLIBRARIES = libnbc.la

libnbc_la_SOURCES = $(sources)
libnbc_la_LIBADD = libdict/libdict.la $(oflibs)

