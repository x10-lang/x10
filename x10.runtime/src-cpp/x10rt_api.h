void x10rt_init (int &argc, char **&argv);

struct x10rt_msg_params { unsigned long dest_place; unsigned type; void *msg; unsigned long len; };

void x10rt_register_msg_receiver (unsigned msg_type,
                                  void (*cb)(const x10rt_msg_params &));
void x10rt_register_get_receiver (unsigned msg_type,
                                  void *(*cb1)(const x10rt_msg_params &),
                                  void (*cb2)(const x10rt_msg_params &, unsigned long len));
void x10rt_register_put_receiver (unsigned msg_type,
                                  void *(*cb1)(const x10rt_msg_params &, unsigned long len),
                                  void (*cb2)(const x10rt_msg_params &, unsigned long len));

void x10rt_registration_complete (void);

unsigned long x10rt_nplaces (void);
unsigned long x10rt_here (void);

void *x10rt_msg_realloc (void *old, size_t old_sz, size_t new_sz);
void x10rt_send_msg (x10rt_msg_params &);

void *x10rt_get_realloc (void *old, size_t old_sz, size_t new_sz);
void x10rt_send_get (x10rt_msg_params &, void *buf, unsigned long len);

void *x10rt_put_realloc (void *old, size_t old_sz, size_t new_sz);
void x10rt_send_put (x10rt_msg_params &, void *buf, unsigned long len);

void x10rt_remote_xor (unsigned long place, unsigned long long addr, unsigned long long update);

void x10rt_remote_op_fence (void);

void x10rt_probe (void);

void x10rt_finalize (void); 
