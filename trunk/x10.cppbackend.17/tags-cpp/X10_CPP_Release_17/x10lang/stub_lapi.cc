#include <string.h>
#include <assert.h>
#include <limits.h>
#include <lapi.h>

#define MAX_LAPI_ADDRESSES 20
static void* address_table[MAX_LAPI_ADDRESSES];
static lapi_handle_t handle;

extern "C" {

int LAPI_Gfence(lapi_handle_t h) { return LAPI_SUCCESS; }
int LAPI_Fence(lapi_handle_t h) { return LAPI_SUCCESS; }
int LAPI_Probe(lapi_handle_t h) { return LAPI_SUCCESS; }
int LAPI_Waitcntr(lapi_handle_t h, lapi_cntr_t* c, int i, int* r) {
    assert (*c >= i);
    *c -= i;
    *r = *c;
    return LAPI_SUCCESS;
}
int LAPI_Getcntr(lapi_handle_t h, lapi_cntr_t* c, int* i) {
    *i = *c;
    return LAPI_SUCCESS;
}
int LAPI_Setcntr(lapi_handle_t h, lapi_cntr_t* c, int i) {
    *c = i;
    return LAPI_SUCCESS;
}
int LAPI_Address_init(lapi_handle_t h, void* p, void** a) {
    *a = p;
    return LAPI_SUCCESS;
}
int LAPI_Address_init64(lapi_handle_t h, lapi_long_t l, lapi_long_t* a) {
    *a = l;
    return LAPI_SUCCESS;
}
int LAPI_Addr_set(lapi_handle_t h, void* a, int i) {
    address_table[i] = a;
    return LAPI_SUCCESS;
}
int LAPI_Addr_get(lapi_handle_t h, void** a, int i) {
    if (i > 0 && i < MAX_LAPI_ADDRESSES)
        *a = address_table[i];
    else
        *a = NULL;
    return LAPI_SUCCESS;
}
int LAPI_Put(lapi_handle_t h, int t, ulong l, void *ta, void *sa, lapi_cntr_t *tc, lapi_cntr_t *sc, lapi_cntr_t *cc) {
    ::memcpy(ta, sa, l);
    return LAPI_SUCCESS;
}
int LAPI_Get(lapi_handle_t h, int t, ulong l, void *ta, void *sa, lapi_cntr_t *tc, lapi_cntr_t *sc) {
    ::memcpy(sa, ta, l);
    return LAPI_SUCCESS;
}
int LAPI_Amsend(lapi_handle_t h, int t, void *r, void *b, uint l, void *s, ulong n, lapi_cntr_t *tc, lapi_cntr_t *sc, lapi_cntr_t *cc) {
    void* (*hh)(lapi_handle_t*,void*,uint*,ulong*,compl_hndlr_t**,void**);
    LAPI_Addr_get(h, (void**)&hh, (int)r);
    if (hh == NULL)
        hh = (void* (*)(lapi_handle_t*,void*,uint*,ulong*,compl_hndlr_t**,void**))r;
    compl_hndlr_t* ch;
    lapi_return_info_t ri; ::memset(&ri, 0, sizeof(lapi_return_info_t));
    void* ui;
    void* d = hh(&h, b, &l, (ulong*)&ri, &ch, &ui);
    if (d != NULL && ri.ctl_flags != LAPI_BURY_MSG)
        ::memcpy(d, s, n);
    if (ch != NULL) ch(&h, ui);
    return LAPI_SUCCESS;
}
int LAPI_Amsendv(lapi_handle_t h, int t, void *r, void *b, uint l, lapi_vec_t *s, lapi_cntr_t *tc, lapi_cntr_t *sc, lapi_cntr_t *cc) {
    assert (false);
    return LAPI_SUCCESS;
}
int LAPI_Qenv(lapi_handle_t h, lapi_query_t q, int *r) {
    switch (q) {
    case TASK_ID: *r = 0; break;
    case NUM_TASKS: *r = 1; break;
    case MAX_UHDR_SZ: *r = INT_MAX; break;
    case LOC_ADDRTBL_SZ: *r = MAX_LAPI_ADDRESSES; break;
    case INTERRUPT_SET: break;
    }
    return LAPI_SUCCESS;
}
int LAPI_Senv(lapi_handle_t h, lapi_query_t q, int v) {
    switch (q) {
    case TASK_ID: assert (false); break;
    case NUM_TASKS: assert (false); break;
    case MAX_UHDR_SZ: assert (false); break;
    case LOC_ADDRTBL_SZ: assert (false); break;
    case INTERRUPT_SET: break;
    }
    return LAPI_SUCCESS;
}
int LAPI_Msg_string(int e, void* b) {
    assert (false);
    return LAPI_SUCCESS;
}
int LAPI_Util(lapi_handle_t h, lapi_util_t *u) {
    switch (u->Util_type) {
    case LAPI_GET_THREAD_FUNC:
        lapi_thread_func_t* tf = (lapi_thread_func_t*)u;
        tf->mutex_lock = NULL;
        tf->mutex_unlock = NULL;
        tf->mutex_trylock = NULL;
        break;
    default: assert (false); break;
    }
    return LAPI_SUCCESS;
}
int LAPI_Msgpoll(lapi_handle_t h, int c, lapi_msg_info_t *r) {
    assert (false);
    return LAPI_SUCCESS;
}
int LAPI_Init(lapi_handle_t *h, lapi_info_t *i) {
    *h = handle = &handle;
    return LAPI_SUCCESS;
}
int LAPI_Term(lapi_handle_t h) {
    return LAPI_SUCCESS;
}

}

