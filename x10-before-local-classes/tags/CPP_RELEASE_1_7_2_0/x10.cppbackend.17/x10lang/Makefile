X10LIB ?= ../../x10.lib
#override CXXFLAGS += -g
override CXXFLAGS += -I$(X10LIB)/include -I.
ifneq ($(wildcard /usr/include/lapi.h),) # AIX
CXX = mpCC_r
override CXXFLAGS -= -g
override CXXFLAGS += -q64
override CXXFLAGS += -O3 -qinline -qarch=pwr5 -qtune=pwr5 -qhot
override CXXFLAGS += -qrtti=all -DNDEBUG
# FIXME
ARFLAGS := -X64 $(ARFLAGS)
endif

ALL_LIBS=libx10lang.a $(if $(wildcard /usr/include/lapi.h),,liblapi.a) libx10lib.a

all:	$(ALL_LIBS)

%.o:	%.cc $(wildcard %.h) $(if $(wildcard /usr/include/lapi.h),,lapi.h)
	$(CXX) $(CXXFLAGS) -o $@ -c $<

lib%.a:	%.o
	$(AR) $(ARFLAGS) $@ $(filter %.o,$^)

lapi.h:
	$(RM) $@
	touch $@
	@echo '/***************************************************************************' >> $@
	@echo '** This file is a dummy header to allow building on systems without LAPI. **' >> $@
	@echo '***************************************************************************/' >> $@
	@echo '#ifndef __LAPI__H__' >> $@
	@echo '#define __LAPI__H__' >> $@
	@echo '#ifdef __cplusplus' >> $@
	@echo 'extern "C" {' >> $@
	@echo '#endif' >> $@
	@echo 'typedef unsigned int uint;' >> $@
	@echo 'typedef unsigned long ulong;' >> $@
	@echo 'typedef unsigned long long lapi_long_t;' >> $@
	@echo 'typedef void* lapi_handle_t;' >> $@
	@echo 'typedef int lapi_cntr_t;' >> $@
	@echo 'typedef int (*lapi_gen_func_t)(lapi_handle_t);' >> $@
	@echo '#define LAPI_GEN_IOVECTOR 0' >> $@
	@echo 'typedef struct { void** info; ulong* len; int num_vecs; int vec_type; } lapi_vec_t;' >> $@
	@echo '#define LAPI_LOCAL_STATE 1' >> $@
	@echo 'typedef struct { void* udata_one_pkt_ptr; int ctl_flags; int ret_flags; } lapi_return_info_t;' >> $@
	@echo '#define LAPI_BURY_MSG 1' >> $@
	@echo '#define LAPI_SUCCESS 0' >> $@
	@echo 'typedef void (compl_hndlr_t)(lapi_handle_t*,void*);' >> $@
	@echo 'typedef struct { int status; } lapi_msg_info_t;' >> $@
	@echo 'int LAPI_Gfence(lapi_handle_t h);' >> $@
	@echo 'int LAPI_Fence(lapi_handle_t h);' >> $@
	@echo 'int LAPI_Probe(lapi_handle_t h);' >> $@
	@echo 'int LAPI_Waitcntr(lapi_handle_t h, lapi_cntr_t* c, int i, int* r);' >> $@
	@echo 'int LAPI_Getcntr(lapi_handle_t h, lapi_cntr_t* c, int* i);' >> $@
	@echo 'int LAPI_Setcntr(lapi_handle_t h, lapi_cntr_t* c, int i);' >> $@
	@echo 'int LAPI_Address_init(lapi_handle_t h, void* p, void** a);' >> $@
	@echo 'int LAPI_Address_init64(lapi_handle_t h, lapi_long_t l, lapi_long_t* a);' >> $@
	@echo 'int LAPI_Addr_set(lapi_handle_t h, void* a, int i);' >> $@
	@echo 'int LAPI_Addr_get(lapi_handle_t h, void** a, int i);' >> $@
	@echo 'int LAPI_Put(lapi_handle_t h, int t, ulong l, void *ta, void *sa, lapi_cntr_t *tc, lapi_cntr_t *sc, lapi_cntr_t *cc);' >> $@
	@echo 'int LAPI_Get(lapi_handle_t h, int t, ulong l, void *ta, void *sa, lapi_cntr_t *tc, lapi_cntr_t *sc);' >> $@
	@echo 'int LAPI_Amsend(lapi_handle_t h, int t, void *r, void *b, uint l, void *s, ulong n, lapi_cntr_t *tc, lapi_cntr_t *sc, lapi_cntr_t *cc);' >> $@
	@echo 'int LAPI_Amsendv(lapi_handle_t h, int t, void *r, void *b, uint l, lapi_vec_t *s, lapi_cntr_t *tc, lapi_cntr_t *sc, lapi_cntr_t *cc);' >> $@
	@echo 'typedef enum {' >> $@
	@echo '    TASK_ID = 0,' >> $@
	@echo '    NUM_TASKS = 1,' >> $@
	@echo '    MAX_UHDR_SZ = 2,' >> $@
	@echo '    LOC_ADDRTBL_SZ = 4,' >> $@
	@echo '    INTERRUPT_SET = 8,' >> $@
	@echo '} lapi_query_t;' >> $@
	@echo 'int LAPI_Qenv(lapi_handle_t h, lapi_query_t q, int *r);' >> $@
	@echo 'int LAPI_Senv(lapi_handle_t h, lapi_query_t q, int v);' >> $@
	@echo 'int LAPI_Msg_string(int e, void* b);' >> $@
	@echo 'typedef enum { LAPI_GET_THREAD_FUNC = 0 } lapi_util_type_t;' >> $@
	@echo 'typedef struct {' >> $@
	@echo '    lapi_util_type_t Util_type;' >> $@
	@echo '    lapi_gen_func_t mutex_lock;' >> $@
	@echo '    lapi_gen_func_t mutex_unlock;' >> $@
	@echo '    lapi_gen_func_t mutex_trylock;' >> $@
	@echo '} lapi_thread_func_t;' >> $@
	@echo 'typedef union {' >> $@
	@echo '    lapi_util_type_t Util_type;' >> $@
	@echo '    lapi_thread_func_t ThreadFunc;' >> $@
	@echo '} lapi_util_t;' >> $@
	@echo 'int LAPI_Util(lapi_handle_t h, lapi_util_t *u);' >> $@
	@echo '#define LAPI_COMPLETE       0' >> $@
	@echo '#define LAPI_SEND_COMPLETE  1' >> $@
	@echo '#define LAPI_RECV_COMPLETE  2' >> $@
	@echo '#define LAPI_BOTH_COMPLETE  3' >> $@
	@echo '#define LAPI_POLLING_NET    4' >> $@
	@echo '#define LAPI_DISP_CNTR      8' >> $@
	@echo 'int LAPI_Msgpoll(lapi_handle_t h, int c, lapi_msg_info_t *r);' >> $@
	@echo '#define LAPI_MAX_MSG_SZ 0x7fffffff' >> $@
	@echo 'typedef struct { struct { int* add_udp_addrs; }* add_info; } lapi_info_t;' >> $@
	@echo 'int LAPI_Init(lapi_handle_t *h, lapi_info_t *i);' >> $@
	@echo 'int LAPI_Term(lapi_handle_t h);' >> $@
	@echo '#ifdef __cplusplus' >> $@
	@echo '} // extern "C"' >> $@
	@echo '#endif' >> $@
	@echo '#endif' >> $@

.INTERMEDIATE: stub_lapi.o
liblapi.a: stub_lapi.o
	$(AR) $(ARFLAGS) $@ $(filter %.o,$^)

libx10lib.a: stub_x10lib.o
	$(AR) $(ARFLAGS) $@ $(filter %.o,$^)

x10lang.o: x10lang.h

clean:;	-$(RM) $(ALL_LIBS) lapi.h *.o

.PHONY: all clean

