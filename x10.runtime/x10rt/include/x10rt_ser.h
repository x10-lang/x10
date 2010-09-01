#ifndef X10RT_SER_H
#define X10RT_SER_H

#ifdef __cplusplus
#include <cstdlib>
#include <cstring>
#else
#include <stdlib.h>
#include <string.h>
#endif

struct x10rt_serbuf {
    x10rt_msg_params p;
    size_t cap;
};

// increase buffer by 30%
static inline void x10rt_serbuf_ensure (x10rt_serbuf *b, size_t sz)
{
    if (b->p.len+sz > b->cap) {
        size_t nu = ((b->p.len+sz) * 13) / 10;
        b->p.msg = realloc(b->p.msg, nu);
        b->cap = nu;
    }
}

static inline size_t x10rt_serbuf_record (x10rt_serbuf *b)
{
    return b->p.len;
}

static inline void x10rt_serbuf_init (x10rt_serbuf *b, x10rt_place dest, x10rt_msg_type id)
{
    b->p.msg = NULL;
    b->p.dest_place = dest;
    b->p.type = id;
    b->p.len = 0;
    b->cap = 0;
}

static inline void x10rt_serbuf_free (x10rt_serbuf *b)
{
    free(b->p.msg);
}

static inline void x10rt_swap_if_le (unsigned char *addr, size_t sz)
{
    (void) addr; (void) sz;
    #if defined(__i386__) || defined(__x86_64__)
    for (size_t i=0,j=sz-1 ; i<j ; ++i,--j) {
        unsigned char tmp = addr[i];
        addr[i] = addr[j];
        addr[j] = tmp;
    }
    #endif
}

static inline void x10rt_serbuf_write_at_ex (x10rt_serbuf *b, size_t at, const void *d_, size_t sz, size_t cnt)
{
    unsigned char *d = (unsigned char*)d_;
    unsigned char *bbuf = (unsigned char*)b->p.msg;
    for (size_t i=0 ; i<cnt ; ++i) {
        memcpy(&bbuf[at], d, sz);
        x10rt_swap_if_le(&bbuf[at], sz);
        at += sz;
        d += sz;
    }
}

static inline void x10rt_serbuf_write_ex (x10rt_serbuf *b, const void *d_, size_t sz, size_t cnt)
{
    x10rt_serbuf_ensure(b, sz*cnt);
    x10rt_serbuf_write_at_ex(b, b->p.len, d_, sz, cnt);
    b->p.len += cnt*sz;
}

#define x10rt_serbuf_write_at(b,a,d) x10rt_serbuf_write_at_ex(b, a, d, sizeof(*d), 1)
#define x10rt_serbuf_write(b,d) x10rt_serbuf_write_ex(b, d, sizeof(*d), 1)




struct x10rt_deserbuf {
    void *buf;
    size_t cursor;
};

static inline void x10rt_deserbuf_init (x10rt_deserbuf *b, const x10rt_msg_params *p)
{
    b->buf = p->msg;
    b->cursor = 0;
}

static inline void x10rt_deserbuf_read_ex (x10rt_deserbuf *b, void *d_, size_t sz, size_t cnt)
{
    unsigned char *d = (unsigned char*)d_;
    unsigned char *bbuf = (unsigned char*)b->buf;
    for (size_t i=0 ; i<cnt ; ++i) {
        memcpy(d, &bbuf[b->cursor], sz);
        x10rt_swap_if_le(d, sz);
        b->cursor += sz;
        d += sz;
    }
}

#define x10rt_deserbuf_read(b,d) x10rt_deserbuf_read_ex(b, d, sizeof(*d), 1)

#endif
