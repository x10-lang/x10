// %W% - %E%


#include "TL.c" 
#include "RB-Tree.h"
//#include "TL.h"

// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

// Red-Black Tree implementation

#if _POLLVALID
#define POLL(act) { if (!TxValid(Self)) { act;}} 
#else
#define POLL(act) {;}
#endif

#define RB_NODE_READSIZE (20)
#define SPLIT_LOOKUP (60)
#define SPLIT_FIX (5)

static int Alloc = 2 ; 


// TODO: Institute either TxIW or TXLDIW
// TxIW = Intent-to-write hint
// TXLDIW = Load with Intent-to-subsequently-write
// These are useful with the TLRW forms where we can avoid
// a R->W upgrade and just immediately acquire W permissions.
// This is useful for the TLRW forms.
// TxIW is simply a hint, however, and any implementation is
// free to ignore it, binding the operator to (0).  
// {TXLDIW;TXST} is preferrable to {TxIW;TxLD;TxST} as the 
// former requires one less barrier check.  

#define LDNODE(o,f)   ((node_t *) (TXLDF((o),f)))
#define LDNODEIW(o,f) ((node_t *) (TXLDFIW((o),f)))

#define LDNODE_IF(o,f)   ((node_t *) (TXLDF_IF((o),f)))
#define LDNODE_TEST(o,f)   ((node_t *) (TXLDF_TEST((o),f)))
#define LDNODE_PURE(o,f)   ((node_t *) (TXLDF_PURE((o),f)))
#define LDNODE_RH1_SP(o,f)   ((node_t *) (TXLDF_RH1_SP((o),f)))
#define LDNODE_RH1NOREC_SP(o,f)   ((node_t *) (TXLDF_RH1NOREC_SP((o),f)))
#define LDNODE_SPLIT(o,f)   ((node_t *) (TXLDF_SPLIT((o),f)))
#define LDNODE_SPLIT_FB(o,f)   ((node_t *) (TXLDF_SPLIT_FB((o),f)))

// See also:
// * Doug Lea's j.u.TreeMap.java
//   TreeMap.java, in turn, was derived from the CLR book.  
// * Keir Fraser's rb_stm.c and rb_lock_serialisedwriters.c in libLtx.  
//
// Following Doug Lea's TreeMap example, we avoid the use of the magic
// "nil" sentinel pointers.  The sentinel is simply a convenience and 
// is not fundamental to the algorithm.  We forgo the sentinel as
// it is a source of false+ data conflicts in transactions.  Relatedly,
// even with locks, use of a nil sentil can result in considerable
// cache coherency traffic on traditional SMPs.  
//
// TODO-FIXME:
// * Add TreeMap.java pollFirstEntry() AKA "DeleteMinimum()"
// * Add TreeMap.java ceiling and floor operators.
//   Ceiling : >= : getCeilingEntry(), CeilingKey(), etc.   
//   Higher  : >
//   Floor   : <= 
//   Lower   : <
// * ElementAt(n) 
//   Returns the n-th element in the tree.
//   Exhibits key-locality and data-locality with RAR. 
//   Common Sub-tree locality.  
//   Use FirstEntry() and then apply Successor(). 
//   

node_t * _lookup_hw (Thread * Self, KVMap * s, int k) {
  node_t * p = s->root; 
  while (p != NULL) {
    int cmp = k - (p->k) ; 
	p->v;
    if (cmp == 0) {
	  return p ; 
	}
    p = (cmp < 0) ? p->l : p->r ; 
    POLL(return NULL) ; 
  }
  return NULL ; 
}

node_t * _lookup_hw_if (Thread * Self, KVMap * s, int k) {
  node_t * p = LDNODE_IF(s,root); 
  while (p != NULL) {
    int cmp = k - TXLDF_IF(p,k) ; 
    if (cmp == 0) return p ; 
    p = (cmp < 0) ? LDNODE_IF(p,l) : LDNODE_IF(p,r) ; 
    POLL(return NULL) ; 
  }
  return NULL ; 
}

node_t * _lookup (Thread * Self, KVMap * s, int k) {
  int cnt = 0;
	
  node_t * p = LDNODE(s,root); 
  while (p != NULL) {
    int cmp = k - TXLDF(p,k) ; 
    TXLDF(p,v) ; 
	for (int z=0; z < RB_NODE_READSIZE; z++) {
		TXLDF(p,v) ; 
	}
	cnt++;
	if (cnt > SPLIT_LOOKUP)
	{
		cnt = 0;
	}
    if (cmp == 0) return p ; 
    p = (cmp < 0) ? LDNODE(p,l) : LDNODE(p,r) ; 
    POLL(return NULL) ; 
  }
  return NULL ; 
}

node_t * _lookup_pure (Thread * Self, KVMap * s, int k) {
  int cnt = 0;
  node_t * p = LDNODE_PURE(s,root); 
  while (p != NULL) {
    int cmp = k - TXLDF_PURE(p,k) ; 
    TXLDF_PURE(p,v) ; 
	for (int z=0; z < RB_NODE_READSIZE; z++) {
		TXLDF_PURE(p,v) ; 
	}
	cnt++;
	if (cnt > SPLIT_LOOKUP)
	{
		cnt = 0;
	}
    if (cmp == 0) return p ; 
    p = (cmp < 0) ? LDNODE_PURE(p,l) : LDNODE_PURE(p,r) ; 
    POLL(return NULL) ; 
  }
  return NULL ; 
}

node_t * _lookup_IF (Thread * Self, KVMap * s, int k) {
  node_t * p = LDNODE_IF(s,root); 
  while (p != NULL) {
    int cmp = k - TXLDF_IF(p,k) ; 
    TXLDF_IF(p,v) ; 
    for (int z=0; z < RB_NODE_READSIZE; z++) {
		TXLDF_IF(p,v) ; 
	}
    if (cmp == 0) return p ; 
    p = (cmp < 0) ? LDNODE_IF(p,l) : LDNODE_IF(p,r) ; 
    POLL(return NULL) ; 
  }
  return NULL ; 
}

node_t * _lookup_RH1_slow_path (Thread * Self, KVMap * s, int k) {
  node_t * p = LDNODE_RH1_SP(s,root); 
  while (p != NULL) {
    int cmp = k - TXLDF_RH1_SP(p,k) ; 
    TXLDF_RH1_SP(p,v) ; 
	int cnt = 0;
	for (int z=0; z < RB_NODE_READSIZE; z++) {
		TXLDF_RH1_SP(p,v) ; 	
		cnt++;
		if (cnt < 10000000)
		{
			TXLDF_RH1_SP(p,v) ; 	
		} 
		
	}
    if (cmp == 0) return p ; 
    p = (cmp < 0) ? LDNODE_RH1_SP(p,l) : LDNODE_RH1_SP(p,r) ; 
    POLL(return NULL) ; 
  }
  return NULL ; 
}

node_t * _lookup_RH1Norec (Thread * Self, KVMap * s, int k) {
  node_t * p = LDNODE_RH1NOREC_SP(s,root); 
  while (p != NULL) {
    int cmp = k - TXLDF_RH1NOREC_SP(p,k) ; 
    TXLDF_RH1NOREC_SP(p,v) ; 
	for (int z=0; z < RB_NODE_READSIZE; z++) {
		TXLDF_RH1NOREC_SP(p,v) ; 
	}
    if (cmp == 0) return p ; 
    p = (cmp < 0) ? LDNODE_RH1NOREC_SP(p,l) : LDNODE_RH1NOREC_SP(p,r) ; 
    POLL(return NULL) ; 
  }
  return NULL ; 
}

node_t * _lookup_SplitTM (Thread * Self, KVMap * s, int k) {
  int cnt = 0;
  int total_cnt = 0;
  SPLIT_LOAD(s,root);
  node_t * p = LDNODE_SPLIT(s,root); 
  while (p != NULL) {
	SPLIT_LOAD(p,k);
    int cmp = k - TXLDF_SPLIT(p,k) ; 
	SPLIT_LOAD(p,v);
    TXLDF_SPLIT(p,v) ; 
	for (int z=0; z < RB_NODE_READSIZE; z++) {
		SPLIT_LOAD(p,v);
		TXLDF_SPLIT(p,v) ; 
		cnt++;
		/*if (cntx > 20)
		{
			//TX_SPLIT(Self);
			cnt = 0;
		}*/
	}
    if (cmp == 0) {
		total_cnt += cnt;
		if (Self->max_tree_depth < total_cnt) {
			Self->max_tree_depth = total_cnt;
		}
		return p ; 
    }
	cnt++;
	if (cnt > SPLIT_LOOKUP)
	{
		TX_SPLIT(Self, __LINE__);
		total_cnt += cnt;
		cnt = 0;
	}
	if (cmp < 0) {
		SPLIT_LOAD(p,l);
		p = LDNODE_SPLIT(p,l);
	} else {
		SPLIT_LOAD(p,r);
		p = LDNODE_SPLIT(p,r);		
	}
//    p = (cmp < 0) ? LDNODE_SPLIT(p,l) : LDNODE_SPLIT(p,r) ; 
    POLL(return NULL) ; 
  }

  total_cnt += cnt;
  if (Self->max_tree_depth < total_cnt) {
    Self->max_tree_depth = total_cnt;
  }
  return NULL ; 
}

node_t * _lookup_SplitTM_FB (Thread * Self, KVMap * s, int k) {
  int total_cnt = 0;

  node_t * p = LDNODE_SPLIT_FB(s,root); 
  while (p != NULL) {

    int cmp = k - TXLDF_SPLIT_FB(p,k) ; 

    TXLDF_SPLIT_FB(p,v) ; 
	for (int z=0; z < RB_NODE_READSIZE; z++) {
		TXLDF_SPLIT_FB(p,v) ; 
		total_cnt++;
	}
    if (cmp == 0) {
		if (Self->max_tree_depth < total_cnt) {
			Self->max_tree_depth = total_cnt;
		}
		return p ; 
    }
	total_cnt++;

	if (cmp < 0) {
		p = LDNODE_SPLIT_FB(p,l);
	} else {
		p = LDNODE_SPLIT_FB(p,r);		
	}
	
  }

  if (Self->max_tree_depth < total_cnt) {
    Self->max_tree_depth = total_cnt;
  }
  return NULL ; 
}

//  Balancing operations.
// 
//  Implementations of rebalancings during insertion and deletion are
//  slightly different than the CLR version.  Rather than using dummy
//  nilnodes, we use a set of accessors that deal properly with null.  They
//  are used to avoid messiness surrounding nullness checks in the main
//  algorithms.
// 
// From CLR 


static void rotateLeft (Thread * Self, set_t * s, node_t * x) {
  node_t * r = LDNODEIW(x,r);      // AKA r, y
  node_t * rl = LDNODE(r,l) ; 
  TXSTF(x,r,rl);
  if (rl != NULL) { 
    TXSTF(rl,p,x) ; 
  } 
  // TODO: compute p = xp = x->p.  Use xp for R-Values in following
  node_t * xp = LDNODEIW(x,p) ; 
  TXSTF(r,p,xp) ; 
  if (xp == NULL)
    TXSTF(s,root,r);
  else if (LDNODE(xp,l) == x)
    TXSTF(xp,l,r) ; 
  else
    TXSTF(xp,r,r) ; 
  TXSTF(r,l,x) ; 
  TXSTF(x,p,r) ; 
}


static void rotateRight (Thread * Self, set_t * s, node_t * x) {
  node_t * l = LDNODEIW(x,l);      // AKA l,y
  node_t * lr = LDNODE(l,r) ; 
  TXSTF(x,l,lr) ; 
  if (lr != NULL) {
    TXSTF(lr,p,x);
  }
  // TODO: compute xp or p = x->p
  node_t * xp = LDNODEIW(x,p) ; 
  TXSTF(l,p,xp) ; 
  if (xp == NULL)
    TXSTF(s,root,l);
  else if (LDNODE(xp,r) == x)
    TXSTF(xp,r,l);
  else 
    TXSTF(xp,l,l);
  TXSTF(l,r,x);
  TXSTF(x,p,l);
}

static inline node_t * _parentOf (Thread * Self, node_t * n) {
  return n ? LDNODE(n,p) : NULL ; 
}

#define parentOf(n) _parentOf(Self,(n))

static inline node_t * _leftOf (Thread * Self, node_t * n) {
  return n ? LDNODE(n,l) : NULL ; 
}

#define leftOf(n) _leftOf(Self,(n))

static inline node_t * _rightOf (Thread * Self, node_t * n) { 
  return n ? LDNODE(n,r) : NULL ; 
}

#define rightOf(n) _rightOf(Self, (n))

static inline int _colorOf (Thread * Self, node_t * n) { 
  // TODO: TXLDF instead of LDNODE
  return n ? (long)(LDNODE(n,c)) : BLACK ; 
}

#define colorOf(n) _colorOf(Self, (n)) 

static inline void _setColor (Thread * Self, node_t * n, int c) { 
  if (n != NULL) TXSTF(n,c,c) ; 
}

#define setColor(n,c) _setColor(Self, (n),(c))

/////

static void rotateLeft_pure (Thread * Self, set_t * s, node_t * x) {
  node_t * r = LDNODE_PURE(x,r);      // AKA r, y
  node_t * rl = LDNODE_PURE(r,l) ; 
  TXSTF_PURE(x,r,rl);
  if (rl != NULL) { 
    TXSTF_PURE(rl,p,x) ; 
  } 
  // TODO: compute p = xp = x->p.  Use xp for R-Values in following
  node_t * xp = LDNODE_PURE(x,p) ; 
  TXSTF_PURE(r,p,xp) ; 
  if (xp == NULL)
    TXSTF_PURE(s,root,r);
  else if (LDNODE_PURE(xp,l) == x)
    TXSTF_PURE(xp,l,r) ; 
  else
    TXSTF_PURE(xp,r,r) ; 
  TXSTF_PURE(r,l,x) ; 
  TXSTF_PURE(x,p,r) ; 
}


static void rotateRight_pure (Thread * Self, set_t * s, node_t * x) {
  node_t * l = LDNODE_PURE(x,l);      // AKA l,y
  node_t * lr = LDNODE_PURE(l,r) ; 
  TXSTF_PURE(x,l,lr) ; 
  if (lr != NULL) {
    TXSTF_PURE(lr,p,x);
  }
  // TODO: compute xp or p = x->p
  node_t * xp = LDNODE_PURE(x,p) ; 
  TXSTF_PURE(l,p,xp) ; 
  if (xp == NULL)
    TXSTF_PURE(s,root,l);
  else if (LDNODE_PURE(xp,r) == x)
    TXSTF_PURE(xp,r,l);
  else 
    TXSTF_PURE(xp,l,l);
  TXSTF_PURE(l,r,x);
  TXSTF_PURE(x,p,l);
}

static void rotateLeft_RH1_fast_path (Thread * Self, set_t * s, node_t * x) {
  node_t * r = LDNODE_PURE(x,r);      // AKA r, y
  node_t * rl = LDNODE_PURE(r,l) ; 
  TXSTF_RH1_FP(x,r,rl);
  if (rl != NULL) { 
    TXSTF_RH1_FP(rl,p,x) ; 
  } 
  // TODO: compute p = xp = x->p.  Use xp for R-Values in following
  node_t * xp = LDNODE_PURE(x,p) ; 
  TXSTF_RH1_FP(r,p,xp) ; 
  if (xp == NULL)
    TXSTF_RH1_FP(s,root,r);
  else if (LDNODE_PURE(xp,l) == x)
    TXSTF_RH1_FP(xp,l,r) ; 
  else
    TXSTF_RH1_FP(xp,r,r) ; 
  TXSTF_RH1_FP(r,l,x) ; 
  TXSTF_RH1_FP(x,p,r) ; 
}


static void rotateRight_RH1_fast_path (Thread * Self, set_t * s, node_t * x) {
  node_t * l = LDNODE_PURE(x,l);      // AKA l,y
  node_t * lr = LDNODE_PURE(l,r) ; 
  TXSTF_RH1_FP(x,l,lr) ; 
  if (lr != NULL) {
    TXSTF_RH1_FP(lr,p,x);
  }
  // TODO: compute xp or p = x->p
  node_t * xp = LDNODE_PURE(x,p) ; 
  TXSTF_RH1_FP(l,p,xp) ; 
  if (xp == NULL)
    TXSTF_RH1_FP(s,root,l);
  else if (LDNODE_PURE(xp,r) == x)
    TXSTF_RH1_FP(xp,r,l);
  else 
    TXSTF_RH1_FP(xp,l,l);
  TXSTF_RH1_FP(l,r,x);
  TXSTF_RH1_FP(x,p,l);
}

static inline node_t * _parentOf_pure (Thread * Self, node_t * n) {
  return n ? LDNODE_PURE(n,p) : NULL ; 
}

#define parentOf_pure(n) _parentOf_pure(Self,(n))

static inline node_t * _leftOf_pure (Thread * Self, node_t * n) {
  return n ? LDNODE_PURE(n,l) : NULL ; 
}

#define leftOf_pure(n) _leftOf_pure(Self,(n))

static inline node_t * _rightOf_pure (Thread * Self, node_t * n) { 
  return n ? LDNODE_PURE(n,r) : NULL ; 
}

#define rightOf_pure(n) _rightOf_pure(Self, (n))

static inline int _colorOf_pure (Thread * Self, node_t * n) { 
  // TODO: TXLDF instead of LDNODE
  return n ? (long)(LDNODE_PURE(n,c)) : BLACK ; 
}

#define colorOf_pure(n) _colorOf_pure(Self, (n)) 

static inline void _setColor_pure (Thread * Self, node_t * n, int c) { 
  if (n != NULL) TXSTF_PURE(n,c,c) ; 
}

#define setColor_pure(n,c) _setColor_pure(Self, (n),(c))

static inline void _setColor_RH1_fast_path (Thread * Self, node_t * n, int c) { 
  if (n != NULL) TXSTF_RH1_FP(n,c,c) ; 
}

#define setColor_RH1_fast_path(n,c) _setColor_RH1_fast_path(Self, (n),(c))

//////////////////////++
static void rotateLeft_RH1_slow_path (Thread * Self, set_t * s, node_t * x) {
  node_t * r = LDNODE_RH1_SP(x,r);      // AKA r, y
  node_t * rl = LDNODE_RH1_SP(r,l) ; 
  TXSTF_RH1_SP(x,r,rl);
  if (rl != NULL) { 
    TXSTF_RH1_SP(rl,p,x) ; 
  } 
  // TODO: compute p = xp = x->p.  Use xp for R-Values in following
  node_t * xp = LDNODE_RH1_SP(x,p) ; 
  TXSTF_RH1_SP(r,p,xp) ; 
  if (xp == NULL)
    TXSTF_RH1_SP(s,root,r);
  else if (LDNODE_RH1_SP(xp,l) == x)
    TXSTF_RH1_SP(xp,l,r) ; 
  else
    TXSTF_RH1_SP(xp,r,r) ; 
  TXSTF_RH1_SP(r,l,x) ; 
  TXSTF_RH1_SP(x,p,r) ; 
}


static void rotateRight_RH1_slow_path (Thread * Self, set_t * s, node_t * x) {
  node_t * l = LDNODE_RH1_SP(x,l);      // AKA l,y
  node_t * lr = LDNODE_RH1_SP(l,r) ; 
  TXSTF_RH1_SP(x,l,lr) ; 
  if (lr != NULL) {
    TXSTF_RH1_SP(lr,p,x);
  }
  // TODO: compute xp or p = x->p
  node_t * xp = LDNODE_RH1_SP(x,p) ; 
  TXSTF_RH1_SP(l,p,xp) ; 
  if (xp == NULL)
    TXSTF_RH1_SP(s,root,l);
  else if (LDNODE_RH1_SP(xp,r) == x)
    TXSTF_RH1_SP(xp,r,l);
  else 
    TXSTF_RH1_SP(xp,l,l);
  TXSTF_RH1_SP(l,r,x);
  TXSTF_RH1_SP(x,p,l);
}

static inline node_t * _parentOf_RH1_slow_path (Thread * Self, node_t * n) {
  return n ? LDNODE_RH1_SP(n,p) : NULL ; 
}

#define parentOf_RH1_slow_path(n) _parentOf_RH1_slow_path(Self,(n))

static inline node_t * _leftOf_RH1_slow_path (Thread * Self, node_t * n) {
  return n ? LDNODE_RH1_SP(n,l) : NULL ; 
}

#define leftOf_RH1_slow_path(n) _leftOf_RH1_slow_path(Self,(n))

static inline node_t * _rightOf_RH1_slow_path (Thread * Self, node_t * n) { 
  return n ? LDNODE_RH1_SP(n,r) : NULL ; 
}

#define rightOf_RH1_slow_path(n) _rightOf_RH1_slow_path(Self, (n))

static inline int _colorOf_RH1_slow_path (Thread * Self, node_t * n) { 
  // TODO: TXLDF instead of LDNODE
  return n ? (long)(LDNODE_RH1_SP(n,c)) : BLACK ; 
}

#define colorOf_RH1_slow_path(n) _colorOf_RH1_slow_path(Self, (n)) 

static inline void _setColor_RH1_slow_path (Thread * Self, node_t * n, int c) { 
  if (n != NULL) TXSTF_RH1_SP(n,c,c) ; 
}

#define setColor_RH1_slow_path(n,c) _setColor_RH1_slow_path(Self, (n),(c))

//////////////////////++
static void rotateLeft_IF (Thread * Self, set_t * s, node_t * x) {
  node_t * r = LDNODE_IF(x,r);      // AKA r, y
  node_t * rl = LDNODE_IF(r,l) ; 
  TXSTF_IF(x,r,rl);
  if (rl != NULL) { 
    TXSTF_IF(rl,p,x) ; 
  } 
  // TODO: compute p = xp = x->p.  Use xp for R-Values in following
  node_t * xp = LDNODE_IF(x,p) ; 
  TXSTF_IF(r,p,xp) ; 
  if (xp == NULL)
    TXSTF_IF(s,root,r);
  else if (LDNODE_IF(xp,l) == x)
    TXSTF_IF(xp,l,r) ; 
  else
    TXSTF_IF(xp,r,r) ; 
  TXSTF_IF(r,l,x) ; 
  TXSTF_IF(x,p,r) ; 
}


static void rotateRight_IF (Thread * Self, set_t * s, node_t * x) {
  node_t * l = LDNODE_IF(x,l);      // AKA l,y
  node_t * lr = LDNODE_IF(l,r) ; 
  TXSTF_IF(x,l,lr) ; 
  if (lr != NULL) {
    TXSTF_IF(lr,p,x);
  }
  // TODO: compute xp or p = x->p
  node_t * xp = LDNODE_IF(x,p) ; 
  TXSTF_IF(l,p,xp) ; 
  if (xp == NULL)
    TXSTF_IF(s,root,l);
  else if (LDNODE_IF(xp,r) == x)
    TXSTF_IF(xp,r,l);
  else 
    TXSTF_IF(xp,l,l);
  TXSTF_IF(l,r,x);
  TXSTF_IF(x,p,l);
}

static inline node_t * _parentOf_IF (Thread * Self, node_t * n) {
  return n ? LDNODE_IF(n,p) : NULL ; 
}

#define parentOf_IF(n) _parentOf_IF(Self,(n))

static inline node_t * _leftOf_IF (Thread * Self, node_t * n) {
  return n ? LDNODE_IF(n,l) : NULL ; 
}

#define leftOf_IF(n) _leftOf_IF(Self,(n))

static inline node_t * _rightOf_IF (Thread * Self, node_t * n) { 
  return n ? LDNODE_IF(n,r) : NULL ; 
}

#define rightOf_IF(n) _rightOf_IF(Self, (n))

static inline int _colorOf_IF (Thread * Self, node_t * n) { 
  // TODO: TXLDF instead of LDNODE
  return n ? (long)(LDNODE_IF(n,c)) : BLACK ; 
}

#define colorOf_IF(n) _colorOf_IF(Self, (n)) 

static inline void _setColor_IF (Thread * Self, node_t * n, int c) { 
  if (n != NULL) TXSTF_IF(n,c,c) ; 
}

#define setColor_IF(n,c) _setColor_IF(Self, (n),(c))


/////----
//////////////////////++
static void rotateLeft_RH1Norec (Thread * Self, set_t * s, node_t * x) {
  node_t * r = LDNODE_RH1NOREC_SP(x,r);      // AKA r, y
  node_t * rl = LDNODE_RH1NOREC_SP(r,l) ; 
  TXSTF_RH1NOREC_SP(x,r,rl);
  if (rl != NULL) { 
    TXSTF_RH1NOREC_SP(rl,p,x) ; 
  } 
  // TODO: compute p = xp = x->p.  Use xp for R-Values in following
  node_t * xp = LDNODE_RH1NOREC_SP(x,p) ; 
  TXSTF_RH1NOREC_SP(r,p,xp) ; 
  if (xp == NULL)
    TXSTF_RH1NOREC_SP(s,root,r);
  else if (LDNODE_RH1NOREC_SP(xp,l) == x)
    TXSTF_RH1NOREC_SP(xp,l,r) ; 
  else
    TXSTF_RH1NOREC_SP(xp,r,r) ; 
  TXSTF_RH1NOREC_SP(r,l,x) ; 
  TXSTF_RH1NOREC_SP(x,p,r) ; 
}


static void rotateRight_RH1Norec (Thread * Self, set_t * s, node_t * x) {
  node_t * l = LDNODE_RH1NOREC_SP(x,l);      // AKA l,y
  node_t * lr = LDNODE_RH1NOREC_SP(l,r) ; 
  TXSTF_RH1NOREC_SP(x,l,lr) ; 
  if (lr != NULL) {
    TXSTF_RH1NOREC_SP(lr,p,x);
  }
  // TODO: compute xp or p = x->p
  node_t * xp = LDNODE_RH1NOREC_SP(x,p) ; 
  TXSTF_RH1NOREC_SP(l,p,xp) ; 
  if (xp == NULL)
    TXSTF_RH1NOREC_SP(s,root,l);
  else if (LDNODE_RH1NOREC_SP(xp,r) == x)
    TXSTF_RH1NOREC_SP(xp,r,l);
  else 
    TXSTF_RH1NOREC_SP(xp,l,l);
  TXSTF_RH1NOREC_SP(l,r,x);
  TXSTF_RH1NOREC_SP(x,p,l);
}

static inline node_t * _parentOf_RH1Norec (Thread * Self, node_t * n) {
  return n ? LDNODE_RH1NOREC_SP(n,p) : NULL ; 
}

#define parentOf_RH1Norec(n) _parentOf_RH1Norec(Self,(n))

static inline node_t * _leftOf_RH1Norec (Thread * Self, node_t * n) {
  return n ? LDNODE_RH1NOREC_SP(n,l) : NULL ; 
}

#define leftOf_RH1Norec(n) _leftOf_RH1Norec(Self,(n))

static inline node_t * _rightOf_RH1Norec (Thread * Self, node_t * n) { 
  return n ? LDNODE_RH1NOREC_SP(n,r) : NULL ; 
}

#define rightOf_RH1Norec(n) _rightOf_RH1Norec(Self, (n))

static inline int _colorOf_RH1Norec (Thread * Self, node_t * n) { 
  // TODO: TXLDF instead of LDNODE
  return n ? (long)(LDNODE_RH1NOREC_SP(n,c)) : BLACK ; 
}

#define colorOf_RH1Norec(n) _colorOf_RH1Norec(Self, (n)) 

static inline void _setColor_RH1Norec (Thread * Self, node_t * n, int c) { 
  if (n != NULL) TXSTF_RH1NOREC_SP(n,c,c) ; 
}

#define setColor_RH1Norec(n,c) _setColor_RH1Norec(Self, (n),(c))


/////----
//////////////////////++
static void rotateLeft_SplitTM (Thread * Self, set_t * s, node_t * x) {
	SPLIT_LOAD(x,r); 
	node_t * r = LDNODE_SPLIT(x,r);      // AKA r, y
	SPLIT_LOAD(r,l); 
	node_t * rl = LDNODE_SPLIT(r,l) ; 
	TXSTF_SPLIT(x,r,rl);
	if (rl != NULL) { 
		TXSTF_SPLIT(rl,p,x) ; 
	} 
// TODO: compute p = xp = x->p.  Use xp for R-Values in following
	SPLIT_LOAD(x,p); 
	node_t * xp = LDNODE_SPLIT(x,p) ; 
	TXSTF_SPLIT(r,p,xp) ; 
	if (xp == NULL) {
		TXSTF_SPLIT(s,root,r);
	} else {
		SPLIT_LOAD(xp,l);
		if (LDNODE_SPLIT(xp,l) == x) {
			TXSTF_SPLIT(xp,l,r) ; 
		} else {
			TXSTF_SPLIT(xp,r,r) ; 
		}
	}
	TXSTF_SPLIT(r,l,x) ; 
	TXSTF_SPLIT(x,p,r) ; 
}


static void rotateRight_SplitTM (Thread * Self, set_t * s, node_t * x) {
	SPLIT_LOAD(x,l);
	node_t * l = LDNODE_SPLIT(x,l);      // AKA l,y
	SPLIT_LOAD(l,r);
	node_t * lr = LDNODE_SPLIT(l,r) ; 
	TXSTF_SPLIT(x,l,lr) ; 
	if (lr != NULL) {
		TXSTF_SPLIT(lr,p,x);
	}
// TODO: compute xp or p = x->p
	SPLIT_LOAD(x,p);
	node_t * xp = LDNODE_SPLIT(x,p) ; 
	TXSTF_SPLIT(l,p,xp) ; 
	if (xp == NULL) {
		TXSTF_SPLIT(s,root,l);
	} else { 
		SPLIT_LOAD(xp,r);
		if (LDNODE_SPLIT(xp,r) == x) {
			TXSTF_SPLIT(xp,r,l);
		} else {
			TXSTF_SPLIT(xp,l,l);
		}
	}
	TXSTF_SPLIT(l,r,x);
	TXSTF_SPLIT(x,p,l);
}

static inline node_t * _parentOf_SplitTM (Thread * Self, node_t * n) {
  if (n) {
	SPLIT_LOAD(n,p);
	return LDNODE_SPLIT(n,p);
  } else {
	return NULL;
  }
}

#define parentOf_SplitTM(n) _parentOf_SplitTM(Self,(n))

static inline node_t * _leftOf_SplitTM (Thread * Self, node_t * n) {
	if (n) {
		SPLIT_LOAD(n,l);
		return LDNODE_SPLIT(n,l);
	} else {
		return NULL;
	}
}

#define leftOf_SplitTM(n) _leftOf_SplitTM(Self,(n))

static inline node_t * _rightOf_SplitTM (Thread * Self, node_t * n) { 
	if (n) {
		SPLIT_LOAD(n,r);
		return LDNODE_SPLIT(n,r);
	} else {
		return NULL;
	}
}

#define rightOf_SplitTM(n) _rightOf_SplitTM(Self, (n))

static inline int _colorOf_SplitTM (Thread * Self, node_t * n) { 
	if (n) {
		SPLIT_LOAD(n,c);
		return (long)LDNODE_SPLIT(n,c);
	} else {
		return BLACK;
	}  
}

#define colorOf_SplitTM(n) _colorOf_SplitTM(Self, (n)) 

static inline void _setColor_SplitTM (Thread * Self, node_t * n, int c) { 
  if (n != NULL) TXSTF_SPLIT(n,c,c) ; 
}

#define setColor_SplitTM(n,c) _setColor_SplitTM(Self, (n),(c))


/////----
//////////////////////++
static void rotateLeft_SplitTM_FB (Thread * Self, set_t * s, node_t * x) {
  node_t * r = LDNODE_SPLIT_FB(x,r);      // AKA r, y
  node_t * rl = LDNODE_SPLIT_FB(r,l) ; 
  TXSTF_SPLIT_FB(x,r,rl);
  if (rl != NULL) { 
    TXSTF_SPLIT_FB(rl,p,x) ; 
  } 
  // TODO: compute p = xp = x->p.  Use xp for R-Values in following
  node_t * xp = LDNODE_SPLIT_FB(x,p) ; 
  TXSTF_SPLIT_FB(r,p,xp) ; 
  if (xp == NULL)
    TXSTF_SPLIT_FB(s,root,r);
  else if (LDNODE_SPLIT_FB(xp,l) == x)
    TXSTF_SPLIT_FB(xp,l,r) ; 
  else
    TXSTF_SPLIT_FB(xp,r,r) ; 
  TXSTF_SPLIT_FB(r,l,x) ; 
  TXSTF_SPLIT_FB(x,p,r) ; 
}


static void rotateRight_SplitTM_FB (Thread * Self, set_t * s, node_t * x) {
  node_t * l = LDNODE_SPLIT_FB(x,l);      // AKA l,y
  node_t * lr = LDNODE_SPLIT_FB(l,r) ; 
  TXSTF_SPLIT_FB(x,l,lr) ; 
  if (lr != NULL) {
    TXSTF_SPLIT_FB(lr,p,x);
  }
  // TODO: compute xp or p = x->p
  node_t * xp = LDNODE_SPLIT_FB(x,p) ; 
  TXSTF_SPLIT_FB(l,p,xp) ; 
  if (xp == NULL)
    TXSTF_SPLIT_FB(s,root,l);
  else if (LDNODE_SPLIT_FB(xp,r) == x)
    TXSTF_SPLIT_FB(xp,r,l);
  else 
    TXSTF_SPLIT_FB(xp,l,l);
  TXSTF_SPLIT_FB(l,r,x);
  TXSTF_SPLIT_FB(x,p,l);
}

static inline node_t * _parentOf_SplitTM_FB (Thread * Self, node_t * n) {
  return n ? LDNODE_SPLIT_FB(n,p) : NULL ; 
}

#define parentOf_SplitTM_FB(n) _parentOf_SplitTM_FB(Self,(n))

static inline node_t * _leftOf_SplitTM_FB (Thread * Self, node_t * n) {
  return n ? LDNODE_SPLIT_FB(n,l) : NULL ; 
}

#define leftOf_SplitTM_FB(n) _leftOf_SplitTM_FB(Self,(n))

static inline node_t * _rightOf_SplitTM_FB (Thread * Self, node_t * n) { 
  return n ? LDNODE_SPLIT_FB(n,r) : NULL ; 
}

#define rightOf_SplitTM_FB(n) _rightOf_SplitTM_FB(Self, (n))

static inline int _colorOf_SplitTM_FB (Thread * Self, node_t * n) { 
  // TODO: TXLDF instead of LDNODE
  return n ? (long)(LDNODE_SPLIT_FB(n,c)) : BLACK ; 
}

#define colorOf_SplitTM_FB(n) _colorOf_SplitTM_FB(Self, (n)) 

static inline void _setColor_SplitTM_FB (Thread * Self, node_t * n, int c) { 
  if (n != NULL) TXSTF_SPLIT_FB(n,c,c) ; 
}

#define setColor_SplitTM_FB(n,c) _setColor_SplitTM_FB(Self, (n),(c))

////-----


static void fixAfterInsertion(Thread * Self, set_t * s, node_t * x) {
  TXSTF(x,c,RED) ; 

  while (x != NULL && x != LDNODE(s,root)) { 
    node_t * xp = LDNODE(x,p) ; 
    if (TXLDF(xp,c) != RED) break ; 

    POLL(return) ; 
    // TODO: cache g = ppx = parentOf(parentOf(x))
    if (parentOf(x) == leftOf(parentOf(parentOf(x)))) {
      node_t *  y = rightOf(parentOf(parentOf(x)));
      if (colorOf(y) == RED) {
        setColor(parentOf(x), BLACK);
        setColor(y, BLACK);
        setColor(parentOf(parentOf(x)), RED);
        x = parentOf(parentOf(x));
      } else {
        if (x == rightOf(parentOf(x))) {
          x = parentOf(x);
          rotateLeft(Self, s, x);
        }
        setColor(parentOf(x), BLACK);
        setColor(parentOf(parentOf(x)), RED);
        if (parentOf(parentOf(x)) != NULL)
          rotateRight(Self, s, parentOf(parentOf(x)));
      }
    } else {
      node_t * y = leftOf(parentOf(parentOf(x)));
      if (colorOf(y) == RED) {
        setColor(parentOf(x), BLACK);
        setColor(y, BLACK);
        setColor(parentOf(parentOf(x)), RED);
        x = parentOf(parentOf(x));
      } else {
        if (x == leftOf(parentOf(x))) {
          x = parentOf(x);
          rotateRight(Self, s,x);
        }
        setColor(parentOf(x),  BLACK);
        setColor(parentOf(parentOf(x)), RED);
        if (parentOf(parentOf(x)) != NULL)
          rotateLeft(Self, s, parentOf(parentOf(x)));
      }
    }
  }
  node_t * ro = LDNODE(s,root) ; 
  if (TXLDF(ro,c) != BLACK) {
    TXSTF(ro,c,BLACK) ; 
  }
}

static void fixAfterInsertion_pure(Thread * Self, set_t * s, node_t * x) {
  TXSTF_PURE(x,c,RED) ; 

  while (x != NULL && x != LDNODE_PURE(s,root)) { 
    node_t * xp = LDNODE_PURE(x,p) ; 
    if (TXLDF_PURE(xp,c) != RED) break ; 

    POLL(return) ; 
    // TODO: cache g = ppx = parentOf(parentOf(x))
    if (parentOf_pure(x) == leftOf_pure(parentOf_pure(parentOf_pure(x)))) {
      node_t *  y = rightOf_pure(parentOf_pure(parentOf_pure(x)));
      if (colorOf_pure(y) == RED) {
        setColor_pure(parentOf_pure(x), BLACK);
        setColor_pure(y, BLACK);
        setColor_pure(parentOf_pure(parentOf_pure(x)), RED);
        x = parentOf_pure(parentOf_pure(x));
      } else {
        if (x == rightOf_pure(parentOf_pure(x))) {
          x = parentOf_pure(x);
          rotateLeft_pure(Self, s, x);
        }
        setColor_pure(parentOf_pure(x), BLACK);
        setColor_pure(parentOf_pure(parentOf_pure(x)), RED);
        if (parentOf_pure(parentOf_pure(x)) != NULL)
          rotateRight_pure(Self, s, parentOf_pure(parentOf_pure(x)));
      }
    } else {
      node_t * y = leftOf_pure(parentOf_pure(parentOf_pure(x)));
      if (colorOf_pure(y) == RED) {
        setColor_pure(parentOf_pure(x), BLACK);
        setColor_pure(y, BLACK);
        setColor_pure(parentOf_pure(parentOf_pure(x)), RED);
        x = parentOf_pure(parentOf_pure(x));
      } else {
        if (x == leftOf_pure(parentOf_pure(x))) {
          x = parentOf_pure(x);
          rotateRight_pure(Self, s,x);
        }
        setColor_pure(parentOf_pure(x),  BLACK);
        setColor_pure(parentOf_pure(parentOf_pure(x)), RED);
        if (parentOf_pure(parentOf_pure(x)) != NULL)
          rotateLeft_pure(Self, s, parentOf_pure(parentOf_pure(x)));
      }
    }
  }
  node_t * ro = LDNODE_PURE(s,root) ; 
  if (TXLDF_PURE(ro,c) != BLACK) {
    TXSTF_PURE(ro,c,BLACK) ; 
  }
}

static void fixAfterInsertion_IF(Thread * Self, set_t * s, node_t * x) {
  TXSTF_IF(x,c,RED) ; 

  while (x != NULL && x != LDNODE_IF(s,root)) { 
    node_t * xp = LDNODE_IF(x,p) ; 
    if (TXLDF_IF(xp,c) != RED) break ; 

    POLL(return) ; 
    // TODO: cache g = ppx = parentOf(parentOf(x))
    if (parentOf_IF(x) == leftOf_IF(parentOf_IF(parentOf_IF(x)))) {
      node_t *  y = rightOf_IF(parentOf_IF(parentOf_IF(x)));
      if (colorOf_IF(y) == RED) {
        setColor_IF(parentOf_IF(x), BLACK);
        setColor_IF(y, BLACK);
        setColor_IF(parentOf_IF(parentOf_IF(x)), RED);
        x = parentOf_IF(parentOf_IF(x));
      } else {
        if (x == rightOf_IF(parentOf_IF(x))) {
          x = parentOf_IF(x);
          rotateLeft_IF(Self, s, x);
        }
        setColor_IF(parentOf_IF(x), BLACK);
        setColor_IF(parentOf_IF(parentOf_IF(x)), RED);
        if (parentOf_IF(parentOf_IF(x)) != NULL)
          rotateRight_IF(Self, s, parentOf_IF(parentOf_IF(x)));
      }
    } else {
      node_t * y = leftOf_IF(parentOf_IF(parentOf_IF(x)));
      if (colorOf_IF(y) == RED) {
        setColor_IF(parentOf_IF(x), BLACK);
        setColor_IF(y, BLACK);
        setColor_IF(parentOf_IF(parentOf_IF(x)), RED);
        x = parentOf_IF(parentOf_IF(x));
      } else {
        if (x == leftOf_IF(parentOf_IF(x))) {
          x = parentOf_IF(x);
          rotateRight_IF(Self, s,x);
        }
        setColor_IF(parentOf_IF(x),  BLACK);
        setColor_IF(parentOf_IF(parentOf_IF(x)), RED);
        if (parentOf_IF(parentOf_IF(x)) != NULL)
          rotateLeft_IF(Self, s, parentOf_IF(parentOf_IF(x)));
      }
    }
  }
  node_t * ro = LDNODE_IF(s,root) ; 
  if (TXLDF_IF(ro,c) != BLACK) {
    TXSTF_IF(ro,c,BLACK) ; 
  }
}

static void fixAfterInsertion_RH1_fast_path(Thread * Self, set_t * s, node_t * x) {
  TXSTF_RH1_FP(x,c,RED) ; 

  while (x != NULL && x != LDNODE_PURE(s,root)) { 
    node_t * xp = LDNODE_PURE(x,p) ; 
    if (TXLDF_PURE(xp,c) != RED) break ; 

    POLL(return) ; 
    // TODO: cache g = ppx = parentOf(parentOf(x))
    if (parentOf_pure(x) == leftOf_pure(parentOf_pure(parentOf_pure(x)))) {
      node_t *  y = rightOf_pure(parentOf_pure(parentOf_pure(x)));
      if (colorOf_pure(y) == RED) {
        setColor_RH1_fast_path(parentOf_pure(x), BLACK);
        setColor_RH1_fast_path(y, BLACK);
        setColor_RH1_fast_path(parentOf_pure(parentOf_pure(x)), RED);
        x = parentOf_pure(parentOf_pure(x));
      } else {
        if (x == rightOf_pure(parentOf_pure(x))) {
          x = parentOf_pure(x);
          rotateLeft_RH1_fast_path(Self, s, x);
        }
        setColor_RH1_fast_path(parentOf_pure(x), BLACK);
        setColor_RH1_fast_path(parentOf_pure(parentOf_pure(x)), RED);
        if (parentOf_pure(parentOf_pure(x)) != NULL)
          rotateRight_RH1_fast_path(Self, s, parentOf_pure(parentOf_pure(x)));
      }
    } else {
      node_t * y = leftOf_pure(parentOf_pure(parentOf_pure(x)));
      if (colorOf_pure(y) == RED) {
        setColor_RH1_fast_path(parentOf_pure(x), BLACK);
        setColor_RH1_fast_path(y, BLACK);
        setColor_RH1_fast_path(parentOf_pure(parentOf_pure(x)), RED);
        x = parentOf_pure(parentOf_pure(x));
      } else {
        if (x == leftOf_pure(parentOf_pure(x))) {
          x = parentOf_pure(x);
          rotateRight_RH1_fast_path(Self, s,x);
        }
        setColor_RH1_fast_path(parentOf_pure(x),  BLACK);
        setColor_RH1_fast_path(parentOf_pure(parentOf_pure(x)), RED);
        if (parentOf_pure(parentOf_pure(x)) != NULL)
          rotateLeft_RH1_fast_path(Self, s, parentOf_pure(parentOf_pure(x)));
      }
    }
  }
  node_t * ro = LDNODE_PURE(s,root) ; 
  if (TXLDF_PURE(ro,c) != BLACK) {
    TXSTF_RH1_FP(ro,c,BLACK) ; 
  }
}

static void fixAfterInsertion_RH1_slow_path(Thread * Self, set_t * s, node_t * x) {
  TXSTF_RH1_SP(x,c,RED) ; 

  while (x != NULL && x != LDNODE_RH1_SP(s,root)) { 
    node_t * xp = LDNODE_RH1_SP(x,p) ; 
    if (TXLDF_RH1_SP(xp,c) != RED) break ; 

    POLL(return) ; 
    // TODO: cache g = ppx = parentOf(parentOf(x))
    if (parentOf_RH1_slow_path(x) == leftOf_RH1_slow_path(parentOf_RH1_slow_path(parentOf_RH1_slow_path(x)))) {
      node_t *  y = rightOf_RH1_slow_path(parentOf_RH1_slow_path(parentOf_RH1_slow_path(x)));
      if (colorOf_RH1_slow_path(y) == RED) {
        setColor_RH1_slow_path(parentOf_RH1_slow_path(x), BLACK);
        setColor_RH1_slow_path(y, BLACK);
        setColor_RH1_slow_path(parentOf_RH1_slow_path(parentOf_RH1_slow_path(x)), RED);
        x = parentOf_RH1_slow_path(parentOf_RH1_slow_path(x));
      } else {
        if (x == rightOf_RH1_slow_path(parentOf_RH1_slow_path(x))) {
          x = parentOf_RH1_slow_path(x);
          rotateLeft_RH1_slow_path(Self, s, x);
        }
        setColor_RH1_slow_path(parentOf_RH1_slow_path(x), BLACK);
        setColor_RH1_slow_path(parentOf_RH1_slow_path(parentOf_RH1_slow_path(x)), RED);
        if (parentOf_RH1_slow_path(parentOf_RH1_slow_path(x)) != NULL)
          rotateRight_RH1_slow_path(Self, s, parentOf_RH1_slow_path(parentOf_RH1_slow_path(x)));
      }
    } else {
      node_t * y = leftOf_RH1_slow_path(parentOf_RH1_slow_path(parentOf_RH1_slow_path(x)));
      if (colorOf_RH1_slow_path(y) == RED) {
        setColor_RH1_slow_path(parentOf_RH1_slow_path(x), BLACK);
        setColor_RH1_slow_path(y, BLACK);
        setColor_RH1_slow_path(parentOf_RH1_slow_path(parentOf_RH1_slow_path(x)), RED);
        x = parentOf_RH1_slow_path(parentOf_RH1_slow_path(x));
      } else {
        if (x == leftOf_RH1_slow_path(parentOf_RH1_slow_path(x))) {
          x = parentOf_RH1_slow_path(x);
          rotateRight_RH1_slow_path(Self, s,x);
        }
        setColor_RH1_slow_path(parentOf_RH1_slow_path(x),  BLACK);
        setColor_RH1_slow_path(parentOf_RH1_slow_path(parentOf_RH1_slow_path(x)), RED);
        if (parentOf_RH1_slow_path(parentOf_RH1_slow_path(x)) != NULL)
          rotateLeft_RH1_slow_path(Self, s, parentOf_RH1_slow_path(parentOf_RH1_slow_path(x)));
      }
    }
  }
  node_t * ro = LDNODE_RH1_SP(s,root) ; 
  if (TXLDF_RH1_SP(ro,c) != BLACK) {
    TXSTF_RH1_SP(ro,c,BLACK) ; 
  }
}

static void fixAfterInsertion_RH1Norec(Thread * Self, set_t * s, node_t * x) {
  TXSTF_RH1NOREC_SP(x,c,RED) ; 

  while (x != NULL && x != LDNODE_RH1NOREC_SP(s,root)) { 
    node_t * xp = LDNODE_RH1NOREC_SP(x,p) ; 
    if (TXLDF_RH1NOREC_SP(xp,c) != RED) break ; 

    POLL(return) ; 
    // TODO: cache g = ppx = parentOf(parentOf(x))
    if (parentOf_RH1Norec(x) == leftOf_RH1Norec(parentOf_RH1Norec(parentOf_RH1Norec(x)))) {
      node_t *  y = rightOf_RH1Norec(parentOf_RH1Norec(parentOf_RH1Norec(x)));
      if (colorOf_RH1Norec(y) == RED) {
        setColor_RH1Norec(parentOf_RH1Norec(x), BLACK);
        setColor_RH1Norec(y, BLACK);
        setColor_RH1Norec(parentOf_RH1Norec(parentOf_RH1Norec(x)), RED);
        x = parentOf_RH1Norec(parentOf_RH1Norec(x));
      } else {
        if (x == rightOf_RH1Norec(parentOf_RH1Norec(x))) {
          x = parentOf_RH1Norec(x);
          rotateLeft_RH1Norec(Self, s, x);
        }
        setColor_RH1Norec(parentOf_RH1Norec(x), BLACK);
        setColor_RH1Norec(parentOf_RH1Norec(parentOf_RH1Norec(x)), RED);
        if (parentOf_RH1Norec(parentOf_RH1Norec(x)) != NULL)
          rotateRight_RH1Norec(Self, s, parentOf_RH1Norec(parentOf_RH1Norec(x)));
      }
    } else {
      node_t * y = leftOf_RH1Norec(parentOf_RH1Norec(parentOf_RH1Norec(x)));
      if (colorOf_RH1Norec(y) == RED) {
        setColor_RH1Norec(parentOf_RH1Norec(x), BLACK);
        setColor_RH1Norec(y, BLACK);
        setColor_RH1Norec(parentOf_RH1Norec(parentOf_RH1Norec(x)), RED);
        x = parentOf_RH1Norec(parentOf_RH1Norec(x));
      } else {
        if (x == leftOf_RH1Norec(parentOf_RH1Norec(x))) {
          x = parentOf_RH1Norec(x);
          rotateRight_RH1Norec(Self, s,x);
        }
        setColor_RH1Norec(parentOf_RH1Norec(x),  BLACK);
        setColor_RH1Norec(parentOf_RH1Norec(parentOf_RH1Norec(x)), RED);
        if (parentOf_RH1Norec(parentOf_RH1Norec(x)) != NULL)
          rotateLeft_RH1Norec(Self, s, parentOf_RH1Norec(parentOf_RH1Norec(x)));
      }
    }
  }
  node_t * ro = LDNODE_RH1NOREC_SP(s,root) ; 
  if (TXLDF_RH1NOREC_SP(ro,c) != BLACK) {
    TXSTF_RH1NOREC_SP(ro,c,BLACK) ; 
  }
}

////
static void fixAfterInsertion_SplitTM(Thread * Self, set_t * s, node_t * x) {
	TXSTF_SPLIT(x,c,RED) ; 

	int cnt = 0;
	SPLIT_LOAD(s,root);
	while (x != NULL && x != LDNODE_SPLIT(s,root)) { 
		if (cnt > SPLIT_FIX)
		{
			TX_SPLIT(Self, __LINE__);
			cnt = 0;
		}
		cnt++;
		
		SPLIT_LOAD(x,p);
		node_t * xp = LDNODE_SPLIT(x,p) ; 
		SPLIT_LOAD(xp,c);
		if (TXLDF_SPLIT(xp,c) != RED) break;

	    POLL(return) ; 
	    // TODO: cache g = ppx = parentOf(parentOf(x))
	    if (parentOf_SplitTM(x) == leftOf_SplitTM(parentOf_SplitTM(parentOf_SplitTM(x)))) {
	      node_t *  y = rightOf_SplitTM(parentOf_SplitTM(parentOf_SplitTM(x)));
	      if (colorOf_SplitTM(y) == RED) {
	        setColor_SplitTM(parentOf_SplitTM(x), BLACK);
	        setColor_SplitTM(y, BLACK);
	        setColor_SplitTM(parentOf_SplitTM(parentOf_SplitTM(x)), RED);
	        x = parentOf_SplitTM(parentOf_SplitTM(x));
	      } else {
	        if (x == rightOf_SplitTM(parentOf_SplitTM(x))) {
	          x = parentOf_SplitTM(x);
	          rotateLeft_SplitTM(Self, s, x);
	        }
	        setColor_SplitTM(parentOf_SplitTM(x), BLACK);
	        setColor_SplitTM(parentOf_SplitTM(parentOf_SplitTM(x)), RED);
	        if (parentOf_SplitTM(parentOf_SplitTM(x)) != NULL)
	          rotateRight_SplitTM(Self, s, parentOf_SplitTM(parentOf_SplitTM(x)));
	      }
	    } else {
	      node_t * y = leftOf_SplitTM(parentOf_SplitTM(parentOf_SplitTM(x)));
	      if (colorOf_SplitTM(y) == RED) {
	        setColor_SplitTM(parentOf_SplitTM(x), BLACK);
	        setColor_SplitTM(y, BLACK);
	        setColor_SplitTM(parentOf_SplitTM(parentOf_SplitTM(x)), RED);
	        x = parentOf_SplitTM(parentOf_SplitTM(x));
	      } else {
	        if (x == leftOf_SplitTM(parentOf_SplitTM(x))) {
	          x = parentOf_SplitTM(x);
	          rotateRight_SplitTM(Self, s,x);
	        }
	        setColor_SplitTM(parentOf_SplitTM(x),  BLACK);
	        setColor_SplitTM(parentOf_SplitTM(parentOf_SplitTM(x)), RED);
	        if (parentOf_SplitTM(parentOf_SplitTM(x)) != NULL)
	          rotateLeft_SplitTM(Self, s, parentOf_SplitTM(parentOf_SplitTM(x)));
	      }
	    }
	}

	SPLIT_LOAD(s,root); 
	node_t * ro = LDNODE_SPLIT(s,root) ; 
	SPLIT_LOAD(ro, c); 
	if (TXLDF_SPLIT(ro,c) != BLACK) {
		TXSTF_SPLIT(ro,c,BLACK) ; 
	}
}

static void fixAfterInsertion_SplitTM_FB(Thread * Self, set_t * s, node_t * x) {
	TXSTF_SPLIT_FB(x,c,RED) ; 

	while (x != NULL && x != LDNODE_SPLIT_FB(s,root)) { 
		
		node_t * xp = LDNODE_SPLIT_FB(x,p) ; 
		if (TXLDF_SPLIT_FB(xp,c) != RED) break;

	    POLL(return) ; 
	    // TODO: cache g = ppx = parentOf(parentOf(x))
	    if (parentOf_SplitTM_FB(x) == leftOf_SplitTM_FB(parentOf_SplitTM_FB(parentOf_SplitTM_FB(x)))) {
	      node_t *  y = rightOf_SplitTM_FB(parentOf_SplitTM_FB(parentOf_SplitTM_FB(x)));
	      if (colorOf_SplitTM_FB(y) == RED) {
	        setColor_SplitTM_FB(parentOf_SplitTM_FB(x), BLACK);
	        setColor_SplitTM_FB(y, BLACK);
	        setColor_SplitTM_FB(parentOf_SplitTM_FB(parentOf_SplitTM_FB(x)), RED);
	        x = parentOf_SplitTM_FB(parentOf_SplitTM_FB(x));
	      } else {
	        if (x == rightOf_SplitTM_FB(parentOf_SplitTM_FB(x))) {
	          x = parentOf_SplitTM_FB(x);
	          rotateLeft_SplitTM_FB(Self, s, x);
	        }
	        setColor_SplitTM_FB(parentOf_SplitTM_FB(x), BLACK);
	        setColor_SplitTM_FB(parentOf_SplitTM_FB(parentOf_SplitTM_FB(x)), RED);
	        if (parentOf_SplitTM_FB(parentOf_SplitTM_FB(x)) != NULL)
	          rotateRight_SplitTM_FB(Self, s, parentOf_SplitTM_FB(parentOf_SplitTM_FB(x)));
	      }
	    } else {
	      node_t * y = leftOf_SplitTM_FB(parentOf_SplitTM_FB(parentOf_SplitTM_FB(x)));
	      if (colorOf_SplitTM_FB(y) == RED) {
	        setColor_SplitTM_FB(parentOf_SplitTM_FB(x), BLACK);
	        setColor_SplitTM_FB(y, BLACK);
	        setColor_SplitTM_FB(parentOf_SplitTM_FB(parentOf_SplitTM_FB(x)), RED);
	        x = parentOf_SplitTM_FB(parentOf_SplitTM_FB(x));
	      } else {
	        if (x == leftOf_SplitTM_FB(parentOf_SplitTM_FB(x))) {
	          x = parentOf_SplitTM_FB(x);
	          rotateRight_SplitTM_FB(Self, s,x);
	        }
	        setColor_SplitTM_FB(parentOf_SplitTM_FB(x),  BLACK);
	        setColor_SplitTM_FB(parentOf_SplitTM_FB(parentOf_SplitTM_FB(x)), RED);
	        if (parentOf_SplitTM_FB(parentOf_SplitTM_FB(x)) != NULL)
	          rotateLeft_SplitTM_FB(Self, s, parentOf_SplitTM_FB(parentOf_SplitTM_FB(x)));
	      }
	    }
	}

	node_t * ro = LDNODE_SPLIT_FB(s,root) ; 
	if (TXLDF_SPLIT_FB(ro,c) != BLACK) {
		TXSTF_SPLIT_FB(ro,c,BLACK) ; 
	}
}

// _insert() has putIfAbsent() semantics
// If the key already exists in the tree _insert() returns a pointer to the 
// node bearing that key and does not modify the tree, otherwise if the key
// is not in the tree it inserts (k,v) into the tree using node n.

static node_t *  _insert (Thread * Self, set_t * s, int k, int v, node_t * n) { 
  node_t * t  = LDNODE(s,root) ; 
  if (t == NULL) {
    if (n == NULL) return NULL ; 
    // Note: the following STs don't really need to be transactional.  
    TXSTF(n,l, NULL) ; 
    TXSTF(n,r, NULL) ; 
    TXSTF(n,p, NULL) ; 
    TXSTF(n,k, k) ; 
    TXSTF(n,v, v) ; 
    TXSTF(n,c, BLACK) ; 
    TXSTF(s,root, n) ; 
    return NULL ; 
  }

  for (;;) { 
    POLL(return NULL) ; 
    intptr_t cmp = k - TXLDF(t,k) ; 
	for (int z=0; z < RB_NODE_READSIZE; z++) {
		TXLDF(t,k) ; 
	}
    if (cmp == 0) {
      return t ; 
    } else if (cmp < 0) {
      node_t * tl = LDNODE(t,l) ; 
      if (tl != NULL) {
        t = tl ; 
      } else {
        TXSTF(n,l, NULL) ; 
        TXSTF(n,r, NULL) ; 
        TXSTF(n,k, k) ; 
        TXSTF(n,v, v) ; 
#if 0
        TXSTF(n,c, BLACK) ; // fixAfterInsertion() will set RED
#endif
        TXSTF(n,p, t) ; 
        TXSTF(t,l, n) ; 
        fixAfterInsertion(Self, s,n);
        return NULL ;
      }
    } else { // cmp > 0
      node_t * tr = LDNODE(t,r) ; 
      if (tr != NULL) {
        t = tr;
      } else {
        TXSTF(n,l, NULL) ; 
        TXSTF(n,r, NULL) ; 
        TXSTF(n,k, k) ; 
        TXSTF(n,v, v) ; 
#if 0
        TXSTF(n,c, BLACK) ; // fixAfterInsertion() will set RED
#endif
        TXSTF(n,p, t); 
        TXSTF(t,r, n) ; 
        fixAfterInsertion(Self, s,n);
        return NULL;
      }
    }
  }
}

static node_t *  _insert_pure(Thread * Self, set_t * s, int k, int v, node_t * n) { 
  node_t * t  = LDNODE_PURE(s,root) ; 
  if (t == NULL) {
    if (n == NULL) return NULL ; 
    // Note: the following STs don't really need to be transactional.  
    TXSTF_PURE(n,l, NULL) ; 
    TXSTF_PURE(n,r, NULL) ; 
    TXSTF_PURE(n,p, NULL) ; 
    TXSTF_PURE(n,k, k) ; 
    TXSTF_PURE(n,v, v) ; 
    TXSTF_PURE(n,c, BLACK) ; 
    TXSTF_PURE(s,root, n) ; 
    return NULL ; 
  }

  for (;;) { 
    POLL(return NULL) ; 
    intptr_t cmp = k - TXLDF_PURE(t,k) ; 
    for (int z=0; z < RB_NODE_READSIZE; z++) {
		TXLDF_PURE(t,k) ; 
	}
    if (cmp == 0) {
      return t ; 
    } else if (cmp < 0) {
      node_t * tl = LDNODE_PURE(t,l) ; 
      if (tl != NULL) {
        t = tl ; 
      } else {
        TXSTF_PURE(n,l, NULL) ; 
        TXSTF_PURE(n,r, NULL) ; 
        TXSTF_PURE(n,k, k) ; 
        TXSTF_PURE(n,v, v) ; 
#if 0
        TXSTF(n,c, BLACK) ; // fixAfterInsertion() will set RED
#endif
        TXSTF_PURE(n,p, t) ; 
        TXSTF_PURE(t,l, n) ; 
        fixAfterInsertion_pure(Self, s,n);
        return NULL ;
      }
    } else { // cmp > 0
      node_t * tr = LDNODE_PURE(t,r) ; 
      if (tr != NULL) {
        t = tr;
      } else {
        TXSTF_PURE(n,l, NULL) ; 
        TXSTF_PURE(n,r, NULL) ; 
        TXSTF_PURE(n,k, k) ; 
        TXSTF_PURE(n,v, v) ; 
#if 0
        TXSTF(n,c, BLACK) ; // fixAfterInsertion() will set RED
#endif
        TXSTF_PURE(n,p, t); 
        TXSTF_PURE(t,r, n) ; 
        fixAfterInsertion_pure(Self, s,n);
        return NULL;
      }
    }
  }
}

static node_t *  _insert_IF(Thread * Self, set_t * s, int k, int v, node_t * n) { 
  node_t * t  = LDNODE_IF(s,root) ; 
  if (t == NULL) {
    if (n == NULL) return NULL ; 
    // Note: the following STs don't really need to be transactional.  
    TXSTF_IF(n,l, NULL) ; 
    TXSTF_IF(n,r, NULL) ; 
    TXSTF_IF(n,p, NULL) ; 
    TXSTF_IF(n,k, k) ; 
    TXSTF_IF(n,v, v) ; 
    TXSTF_IF(n,c, BLACK) ; 
    TXSTF_IF(s,root, n) ; 
    return NULL ; 
  }

  for (;;) { 
    POLL(return NULL) ; 
    intptr_t cmp = k - TXLDF_IF(t,k) ; 
	for (int z=0; z < RB_NODE_READSIZE; z++) {
		TXLDF_IF(t,k) ; 
	}
    if (cmp == 0) {
      return t ; 
    } else if (cmp < 0) {
      node_t * tl = LDNODE_IF(t,l) ; 
      if (tl != NULL) {
        t = tl ; 
      } else {
        TXSTF_IF(n,l, NULL) ; 
        TXSTF_IF(n,r, NULL) ; 
        TXSTF_IF(n,k, k) ; 
        TXSTF_IF(n,v, v) ; 
#if 0
        TXSTF(n,c, BLACK) ; // fixAfterInsertion() will set RED
#endif
        TXSTF_IF(n,p, t) ; 
        TXSTF_IF(t,l, n) ; 
        fixAfterInsertion_pure(Self, s,n);
        return NULL ;
      }
    } else { // cmp > 0
      node_t * tr = LDNODE_IF(t,r) ; 
      if (tr != NULL) {
        t = tr;
      } else {
        TXSTF_IF(n,l, NULL) ; 
        TXSTF_IF(n,r, NULL) ; 
        TXSTF_IF(n,k, k) ; 
        TXSTF_IF(n,v, v) ; 
#if 0
        TXSTF(n,c, BLACK) ; // fixAfterInsertion() will set RED
#endif
        TXSTF_IF(n,p, t); 
        TXSTF_IF(t,r, n) ; 
        fixAfterInsertion_pure(Self, s,n);
        return NULL;
      }
    }
  }
}

static node_t *  _insert_RH1_fast_path(Thread * Self, set_t * s, int k, int v, node_t * n) { 
  node_t * t  = LDNODE_PURE(s,root) ; 
  if (t == NULL) {
    if (n == NULL) return NULL ; 
    // Note: the following STs don't really need to be transactional.  
    TXSTF_RH1_FP(n,l, NULL) ; 
    TXSTF_RH1_FP(n,r, NULL) ; 
    TXSTF_RH1_FP(n,p, NULL) ; 
    TXSTF_RH1_FP(n,k, k) ; 
    TXSTF_RH1_FP(n,v, v) ; 
    TXSTF_RH1_FP(n,c, BLACK) ; 
    TXSTF_RH1_FP(s,root, n) ; 
    return NULL ; 
  }

  for (;;) { 
    POLL(return NULL) ; 
    intptr_t cmp = k - TXLDF_PURE(t,k) ; 
	for (int z=0; z < RB_NODE_READSIZE; z++) {
		TXLDF_PURE(t,k) ; 
	}
    if (cmp == 0) {
      return t ; 
    } else if (cmp < 0) {
      node_t * tl = LDNODE_PURE(t,l) ; 
      if (tl != NULL) {
        t = tl ; 
      } else {
        TXSTF_RH1_FP(n,l, NULL) ; 
        TXSTF_RH1_FP(n,r, NULL) ; 
        TXSTF_RH1_FP(n,k, k) ; 
        TXSTF_RH1_FP(n,v, v) ; 
#if 0
        TXSTF(n,c, BLACK) ; // fixAfterInsertion() will set RED
#endif
        TXSTF_RH1_FP(n,p, t) ; 
        TXSTF_RH1_FP(t,l, n) ; 
        fixAfterInsertion_RH1_fast_path(Self, s,n);
        return NULL ;
      }
    } else { // cmp > 0
      node_t * tr = LDNODE_PURE(t,r) ; 
      if (tr != NULL) {
        t = tr;
      } else {
        TXSTF_RH1_FP(n,l, NULL) ; 
        TXSTF_RH1_FP(n,r, NULL) ; 
        TXSTF_RH1_FP(n,k, k) ; 
        TXSTF_RH1_FP(n,v, v) ; 
#if 0
        TXSTF(n,c, BLACK) ; // fixAfterInsertion() will set RED
#endif
        TXSTF_RH1_FP(n,p, t); 
        TXSTF_RH1_FP(t,r, n) ; 
        fixAfterInsertion_RH1_fast_path(Self, s,n);
        return NULL;
      }
    }
  }
}

static node_t *  _insert_RH1_slow_path(Thread * Self, set_t * s, int k, int v, node_t * n) { 
  node_t * t  = LDNODE_RH1_SP(s,root) ; 
  if (t == NULL) {
    if (n == NULL) return NULL ; 
    // Note: the following STs don't really need to be transactional.  
    TXSTF_RH1_SP(n,l, NULL) ; 
    TXSTF_RH1_SP(n,r, NULL) ; 
    TXSTF_RH1_SP(n,p, NULL) ; 
    TXSTF_RH1_SP(n,k, k) ; 
    TXSTF_RH1_SP(n,v, v) ; 
    TXSTF_RH1_SP(n,c, BLACK) ; 
    TXSTF_RH1_SP(s,root, n) ; 
    return NULL ; 
  }

  for (;;) { 
    POLL(return NULL) ; 
    intptr_t cmp = k - TXLDF_RH1_SP(t,k) ; 
	for (int z=0; z < RB_NODE_READSIZE; z++) {
		TXLDF_RH1_SP(t,k) ; 
	}
    if (cmp == 0) {
      return t ; 
    } else if (cmp < 0) {
      node_t * tl = LDNODE_RH1_SP(t,l) ; 
      if (tl != NULL) {
        t = tl ; 
      } else {
        TXSTF_RH1_SP(n,l, NULL) ; 
        TXSTF_RH1_SP(n,r, NULL) ; 
        TXSTF_RH1_SP(n,k, k) ; 
        TXSTF_RH1_SP(n,v, v) ; 
#if 0
        TXSTF(n,c, BLACK) ; // fixAfterInsertion() will set RED
#endif
        TXSTF_RH1_SP(n,p, t) ; 
        TXSTF_RH1_SP(t,l, n) ; 
        fixAfterInsertion_RH1_slow_path(Self, s,n);
        return NULL ;
      }
    } else { // cmp > 0
      node_t * tr = LDNODE_RH1_SP(t,r) ; 
      if (tr != NULL) {
        t = tr;
      } else {
        TXSTF_RH1_SP(n,l, NULL) ; 
        TXSTF_RH1_SP(n,r, NULL) ; 
        TXSTF_RH1_SP(n,k, k) ; 
        TXSTF_RH1_SP(n,v, v) ; 
#if 0
        TXSTF(n,c, BLACK) ; // fixAfterInsertion() will set RED
#endif
        TXSTF_RH1_SP(n,p, t); 
        TXSTF_RH1_SP(t,r, n) ; 
        fixAfterInsertion_RH1_slow_path(Self, s,n);
        return NULL;
      }
    }
  }
}

static node_t *  _insert_RH1Norec_slow_path(Thread * Self, set_t * s, int k, int v, node_t * n) { 
  node_t * t  = LDNODE_RH1NOREC_SP(s,root) ; 
  if (t == NULL) {
    if (n == NULL) return NULL ; 
    // Note: the following STs don't really need to be transactional.  
    TXSTF_RH1NOREC_SP(n,l, NULL) ; 
    TXSTF_RH1NOREC_SP(n,r, NULL) ; 
    TXSTF_RH1NOREC_SP(n,p, NULL) ; 
    TXSTF_RH1NOREC_SP(n,k, k) ; 
    TXSTF_RH1NOREC_SP(n,v, v) ; 
    TXSTF_RH1NOREC_SP(n,c, BLACK) ; 
    TXSTF_RH1NOREC_SP(s,root, n) ; 
    return NULL ; 
  }

  for (;;) { 
    POLL(return NULL) ; 
    intptr_t cmp = k - TXLDF_RH1NOREC_SP(t,k) ; 
	for (int z=0; z < RB_NODE_READSIZE; z++) {
		TXLDF_RH1NOREC_SP(t,k) ; 
	}
    if (cmp == 0) {
      return t ; 
    } else if (cmp < 0) {
      node_t * tl = LDNODE_RH1NOREC_SP(t,l) ; 
      if (tl != NULL) {
        t = tl ; 
      } else {
        TXSTF_RH1NOREC_SP(n,l, NULL) ; 
        TXSTF_RH1NOREC_SP(n,r, NULL) ; 
        TXSTF_RH1NOREC_SP(n,k, k) ; 
        TXSTF_RH1NOREC_SP(n,v, v) ; 
#if 0
        TXSTF(n,c, BLACK) ; // fixAfterInsertion() will set RED
#endif
        TXSTF_RH1NOREC_SP(n,p, t) ; 
        TXSTF_RH1NOREC_SP(t,l, n) ; 
        fixAfterInsertion_RH1Norec(Self, s,n);
        return NULL ;
      }
    } else { // cmp > 0
      node_t * tr = LDNODE_RH1NOREC_SP(t,r) ; 
      if (tr != NULL) {
        t = tr;
      } else {
        TXSTF_RH1NOREC_SP(n,l, NULL) ; 
        TXSTF_RH1NOREC_SP(n,r, NULL) ; 
        TXSTF_RH1NOREC_SP(n,k, k) ; 
        TXSTF_RH1NOREC_SP(n,v, v) ; 
#if 0
        TXSTF(n,c, BLACK) ; // fixAfterInsertion() will set RED
#endif
        TXSTF_RH1NOREC_SP(n,p, t); 
        TXSTF_RH1NOREC_SP(t,r, n) ; 
        fixAfterInsertion_RH1Norec(Self, s,n);
        return NULL;
      }
    }
  }
}

static node_t *  _insert_SplitTM(Thread * Self, set_t * s, int k, int v, node_t * n) { 
  int cnt = 0;
  
  SPLIT_LOAD(s,root);
  node_t * t  = LDNODE_SPLIT(s,root) ; 
  if (t == NULL) {
    if (n == NULL) return NULL ; 
    // Note: the following STs don't really need to be transactional.  
    TXSTF_SPLIT(n,l, NULL) ; 
    TXSTF_SPLIT(n,r, NULL) ; 
    TXSTF_SPLIT(n,p, NULL) ; 
    TXSTF_SPLIT(n,k, k) ; 
    TXSTF_SPLIT(n,v, v) ; 
    TXSTF_SPLIT(n,c, BLACK) ; 
    TXSTF_SPLIT(s,root, n) ; 
    return NULL ; 
  }

  for (;;) { 
    POLL(return NULL) ; 
    SPLIT_LOAD(t,k);    
    intptr_t cmp = k - TXLDF_SPLIT(t,k) ; 
	for (int z=0; z < RB_NODE_READSIZE; z++) {
		TXLDF_SPLIT(t,k) ; 
		cnt++;
	}
	cnt++;
	if (cnt > SPLIT_LOOKUP)
	{
		TX_SPLIT(Self, __LINE__);
		cnt = 0;
	}
    if (cmp == 0) {
      return t ; 
    } else if (cmp < 0) {
      SPLIT_LOAD(t,l);    
  	  node_t * tl = LDNODE_SPLIT(t,l) ; 
      if (tl != NULL) {
        t = tl ; 
      } else {
        TXSTF_SPLIT(n,l, NULL) ; 
        TXSTF_SPLIT(n,r, NULL) ; 
        TXSTF_SPLIT(n,k, k) ; 
        TXSTF_SPLIT(n,v, v) ; 
#if 0
        TXSTF(n,c, BLACK) ; // fixAfterInsertion() will set RED
#endif
        TXSTF_SPLIT(n,p, t) ; 
        TXSTF_SPLIT(t,l, n) ; 
        
		TX_SPLIT(Self, __LINE__);
		//printf("FIX!\n");
		fixAfterInsertion_SplitTM(Self, s,n);
        return NULL ;
      }
    } else { // cmp > 0
      SPLIT_LOAD(t,r);    
      node_t * tr = LDNODE_SPLIT(t,r) ; 
      if (tr != NULL) {
        t = tr;
      } else {
        TXSTF_SPLIT(n,l, NULL) ; 
        TXSTF_SPLIT(n,r, NULL) ; 
        TXSTF_SPLIT(n,k, k) ; 
        TXSTF_SPLIT(n,v, v) ; 
#if 0
        TXSTF(n,c, BLACK) ; // fixAfterInsertion() will set RED
#endif
        TXSTF_SPLIT(n,p, t); 
        TXSTF_SPLIT(t,r, n) ; 
		
		TX_SPLIT(Self, __LINE__);
		//printf("FIX!\n");
        fixAfterInsertion_SplitTM(Self, s,n);
        return NULL;
      }
    }
  }
}

static node_t *  _insert_SplitTM_FB(Thread * Self, set_t * s, int k, int v, node_t * n) { 
  
  node_t * t  = LDNODE_SPLIT_FB(s,root) ; 
  if (t == NULL) {
    if (n == NULL) return NULL ; 
    // Note: the following STs don't really need to be transactional.  
    TXSTF_SPLIT_FB(n,l, NULL) ; 
    TXSTF_SPLIT_FB(n,r, NULL) ; 
    TXSTF_SPLIT_FB(n,p, NULL) ; 
    TXSTF_SPLIT_FB(n,k, k) ; 
    TXSTF_SPLIT_FB(n,v, v) ; 
    TXSTF_SPLIT_FB(n,c, BLACK) ; 
    TXSTF_SPLIT_FB(s,root, n) ; 
    return NULL ; 
  }

  for (;;) { 
    POLL(return NULL) ; 
    intptr_t cmp = k - TXLDF_SPLIT_FB(t,k) ; 
	for (int z=0; z < RB_NODE_READSIZE; z++) {
		TXLDF_SPLIT_FB(t,k) ; 
	}
	
    if (cmp == 0) {
      return t ; 
    } else if (cmp < 0) {
      node_t * tl = LDNODE_SPLIT_FB(t,l) ; 
      if (tl != NULL) {
        t = tl ; 
      } else {
        TXSTF_SPLIT_FB(n,l, NULL) ; 
        TXSTF_SPLIT_FB(n,r, NULL) ; 
        TXSTF_SPLIT_FB(n,k, k) ; 
        TXSTF_SPLIT_FB(n,v, v) ; 
#if 0
        TXSTF(n,c, BLACK) ; // fixAfterInsertion() will set RED
#endif
        TXSTF_SPLIT_FB(n,p, t) ; 
        TXSTF_SPLIT_FB(t,l, n) ; 
        

		//printf("FIX!\n");
		fixAfterInsertion_SplitTM_FB(Self, s,n);
        return NULL ;
      }
    } else { // cmp > 0
      node_t * tr = LDNODE_SPLIT_FB(t,r) ; 
      if (tr != NULL) {
        t = tr;
      } else {
        TXSTF_SPLIT_FB(n,l, NULL) ; 
        TXSTF_SPLIT_FB(n,r, NULL) ; 
        TXSTF_SPLIT_FB(n,k, k) ; 
        TXSTF_SPLIT_FB(n,v, v) ; 
#if 0
        TXSTF(n,c, BLACK) ; // fixAfterInsertion() will set RED
#endif
        TXSTF_SPLIT_FB(n,p, t); 
        TXSTF_SPLIT_FB(t,r, n) ; 
		
		//printf("FIX!\n");
        fixAfterInsertion_SplitTM_FB(Self, s,n);
        return NULL;
      }
    }
  }
}

// Return the given node's successor node---the node which has the
// next key in the the left to right ordering. If the node has
// no successor, a null pointer is returned rather than a pointer to
// the nil node.

static node_t * _successor(Thread * Self, node_t * t) {
  if (t == NULL)
    return NULL;
  else if (LDNODE(t,r) != NULL) {
    node_t * p = LDNODE(t,r) ; 
    while (LDNODE(p,l) != NULL) { 
      p = LDNODE(p,l) ; 
      POLL(return NULL) ; 
    }
    return p;
  } else {
    node_t * p = LDNODE(t,p);
    node_t * ch = t;
    while (p != NULL && ch == LDNODE(p,r)) {
      ch = p;
      p = LDNODE(p,p) ; 
      POLL(return NULL) ; 
    }
    return p;
  }
}

static node_t * _successor_SplitTM(Thread * Self, node_t * t) {
	if (t == NULL) {
		return NULL;
	} else { 
		SPLIT_LOAD(t,r);
		if (LDNODE_SPLIT(t,r) != NULL) {
			SPLIT_LOAD(t,r);
			node_t * p = LDNODE_SPLIT(t,r) ; 
			SPLIT_LOAD(p,l);
			while (LDNODE_SPLIT(p,l) != NULL) { 
				SPLIT_LOAD(p,l);
				p = LDNODE_SPLIT(p,l) ; 
				POLL(return NULL) ; 
			}
			return p;
		} else {
			SPLIT_LOAD(t,p);
			node_t * p = LDNODE_SPLIT(t,p);
			node_t * ch = t;
			SPLIT_LOAD(p,r);
			while (p != NULL && ch == LDNODE_SPLIT(p,r)) {
				ch = p;
				SPLIT_LOAD(p,p);      			
				p = LDNODE_SPLIT(p,p) ; 
				POLL(return NULL) ; 
			}
			return p;
		}
	}
}

static node_t * _successor_SplitTM_FB(Thread * Self, node_t * t) {
  if (t == NULL)
    return NULL;
  else if (LDNODE_SPLIT_FB(t,r) != NULL) {
    node_t * p = LDNODE_SPLIT_FB(t,r) ; 
    while (LDNODE_SPLIT_FB(p,l) != NULL) { 
      p = LDNODE_SPLIT_FB(p,l) ; 
      POLL(return NULL) ; 
    }
    return p;
  } else {
    node_t * p = LDNODE_SPLIT_FB(t,p);
    node_t * ch = t;
    while (p != NULL && ch == LDNODE_SPLIT_FB(p,r)) {
      ch = p;
      p = LDNODE_SPLIT_FB(p,p) ; 
      POLL(return NULL) ; 
    }
    return p;
  }
}

static node_t * _successor_pure(Thread * Self, node_t * t) {
  if (t == NULL)
    return NULL;
  else if (LDNODE_PURE(t,r) != NULL) {
    node_t * p = LDNODE_PURE(t,r) ; 
    while (LDNODE_PURE(p,l) != NULL) { 
      p = LDNODE_PURE(p,l) ; 
      POLL(return NULL) ; 
    }
    return p;
  } else {
    node_t * p = LDNODE_PURE(t,p);
    node_t * ch = t;
    while (p != NULL && ch == LDNODE_PURE(p,r)) {
      ch = p;
      p = LDNODE_PURE(p,p) ; 
      POLL(return NULL) ; 
    }
    return p;
  }
}

static node_t * _successor_RH1_slow_path(Thread * Self, node_t * t) {
  if (t == NULL)
    return NULL;
  else if (LDNODE_RH1_SP(t,r) != NULL) {
    node_t * p = LDNODE_RH1_SP(t,r) ; 
    while (LDNODE_RH1_SP(p,l) != NULL) { 
      p = LDNODE_RH1_SP(p,l) ; 
      POLL(return NULL) ; 
    }
    return p;
  } else {
    node_t * p = LDNODE_RH1_SP(t,p);
    node_t * ch = t;
    while (p != NULL && ch == LDNODE_RH1_SP(p,r)) {
      ch = p;
      p = LDNODE_RH1_SP(p,p) ; 
      POLL(return NULL) ; 
    }
    return p;
  }
}

static void fixAfterDeletion(Thread * Self, set_t * s, node_t *  x) {
  while (x != LDNODE(s,root) && colorOf(x) == BLACK) {
    POLL(return) ; 
    if (x == leftOf(parentOf(x))) {
      node_t * sib = rightOf(parentOf(x));
      if (colorOf(sib) == RED) {
        setColor(sib, BLACK);
        setColor(parentOf(x), RED);
        rotateLeft(Self, s, parentOf(x));
        sib = rightOf(parentOf(x));
      }

      if (colorOf(leftOf(sib))  == BLACK &&
        colorOf(rightOf(sib)) == BLACK) {
        setColor(sib,  RED);
        x = parentOf(x);
      } else {
        if (colorOf(rightOf(sib)) == BLACK) {
           setColor(leftOf(sib), BLACK);
           setColor(sib, RED);
           rotateRight(Self, s, sib);
           sib = rightOf(parentOf(x));
        }
        setColor(sib, colorOf(parentOf(x)));
        setColor(parentOf(x), BLACK);
        setColor(rightOf(sib), BLACK);
        rotateLeft(Self, s, parentOf(x));
        // TODO: consider break ...
        x = LDNODE(s,root) ; 
      }
    } else { // symmetric
      node_t * sib = leftOf(parentOf(x));

      if (colorOf(sib) == RED) {
        setColor(sib, BLACK);
        setColor(parentOf(x), RED);
        rotateRight(Self, s, parentOf(x));
        sib = leftOf(parentOf(x));
      }

      if (colorOf(rightOf(sib)) == BLACK &&
        colorOf(leftOf(sib)) == BLACK) {
        setColor(sib,  RED);
        x = parentOf(x);
      } else {
        if (colorOf(leftOf(sib)) == BLACK) {
           setColor(rightOf(sib), BLACK);
           setColor(sib, RED);
           rotateLeft(Self, s, sib);
           sib = leftOf(parentOf(x));
        }
        setColor(sib, colorOf(parentOf(x)));
        setColor(parentOf(x), BLACK);
        setColor(leftOf(sib), BLACK);
        rotateRight(Self, s, parentOf(x));
        // TODO: consider break ...
        x = LDNODE(s,root) ; 
      }
    }
  }

  if (x != NULL && TXLDF(x,c) != BLACK) {
    TXSTF(x,c, BLACK) ; 
  }
}

///
static void fixAfterDeletion_SplitTM(Thread * Self, set_t * s, node_t *  x) {
	int cnt = 0;

	SPLIT_LOAD(s,root);
	while (x != LDNODE_SPLIT(s,root) && colorOf_SplitTM(x) == BLACK) {
		cnt++;
		if (cnt > SPLIT_FIX)
		{
			TX_SPLIT(Self, __LINE__);
			cnt = 0;
		}

		if (x == leftOf_SplitTM(parentOf_SplitTM(x))) {
			node_t * sib = rightOf_SplitTM(parentOf_SplitTM(x));
			if (colorOf_SplitTM(sib) == RED) {
				setColor_SplitTM(sib, BLACK);
				setColor_SplitTM(parentOf_SplitTM(x), RED);
				rotateLeft_SplitTM(Self, s, parentOf_SplitTM(x));
				sib = rightOf_SplitTM(parentOf_SplitTM(x));
			}

			if (colorOf_SplitTM(leftOf_SplitTM(sib))  == BLACK &&
			colorOf_SplitTM(rightOf_SplitTM(sib)) == BLACK) {
				setColor_SplitTM(sib,  RED);
				x = parentOf_SplitTM(x);
			} else {
				if (colorOf_SplitTM(rightOf_SplitTM(sib)) == BLACK) {
					setColor_SplitTM(leftOf_SplitTM(sib), BLACK);
					setColor_SplitTM(sib, RED);
					rotateRight_SplitTM(Self, s, sib);
					sib = rightOf_SplitTM(parentOf_SplitTM(x));
				}
				setColor_SplitTM(sib, colorOf_SplitTM(parentOf_SplitTM(x)));
				setColor_SplitTM(parentOf_SplitTM(x), BLACK);
				setColor_SplitTM(rightOf_SplitTM(sib), BLACK);
				rotateLeft_SplitTM(Self, s, parentOf_SplitTM(x));
			// TODO: consider break ...
				SPLIT_LOAD(s,root);
				x = LDNODE_SPLIT(s,root) ; 
			}
		} else { // symmetric
			node_t * sib = leftOf_SplitTM(parentOf_SplitTM(x));

			if (colorOf_SplitTM(sib) == RED) {
				setColor_SplitTM(sib, BLACK);
				setColor_SplitTM(parentOf_SplitTM(x), RED);
				rotateRight_SplitTM(Self, s, parentOf_SplitTM(x));
				sib = leftOf_SplitTM(parentOf_SplitTM(x));
			}

			if (colorOf_SplitTM(rightOf_SplitTM(sib)) == BLACK &&
			colorOf_SplitTM(leftOf_SplitTM(sib)) == BLACK) {
				setColor_SplitTM(sib,  RED);
				x = parentOf_SplitTM(x);
			} else {
				if (colorOf_SplitTM(leftOf_SplitTM(sib)) == BLACK) {
					setColor_SplitTM(rightOf_SplitTM(sib), BLACK);
					setColor_SplitTM(sib, RED);
					rotateLeft_SplitTM(Self, s, sib);
					sib = leftOf_SplitTM(parentOf_SplitTM(x));
				}
				setColor_SplitTM(sib, colorOf_SplitTM(parentOf_SplitTM(x)));
				setColor_SplitTM(parentOf_SplitTM(x), BLACK);
				setColor_SplitTM(leftOf_SplitTM(sib), BLACK);
				rotateRight_SplitTM(Self, s, parentOf_SplitTM(x));
			// TODO: consider break ...
				SPLIT_LOAD(s,root);
				x = LDNODE_SPLIT(s,root) ; 
			}
		}

	}

	if (x != NULL) {
		SPLIT_LOAD(x,c);
		if (TXLDF_SPLIT(x,c) != BLACK) {
			TXSTF_SPLIT(x,c, BLACK) ; 
		}
	}
	
}

static void fixAfterDeletion_SplitTM_FB(Thread * Self, set_t * s, node_t *  x) {

	while (x != LDNODE_SPLIT_FB(s,root) && colorOf_SplitTM_FB(x) == BLACK) {

		if (x == leftOf_SplitTM_FB(parentOf_SplitTM_FB(x))) {
			node_t * sib = rightOf_SplitTM_FB(parentOf_SplitTM_FB(x));
			if (colorOf_SplitTM_FB(sib) == RED) {
				setColor_SplitTM_FB(sib, BLACK);
				setColor_SplitTM_FB(parentOf_SplitTM_FB(x), RED);
				rotateLeft_SplitTM_FB(Self, s, parentOf_SplitTM_FB(x));
				sib = rightOf_SplitTM_FB(parentOf_SplitTM_FB(x));
			}

			if (colorOf_SplitTM_FB(leftOf_SplitTM_FB(sib))  == BLACK &&
			colorOf_SplitTM_FB(rightOf_SplitTM_FB(sib)) == BLACK) {
				setColor_SplitTM_FB(sib,  RED);
				x = parentOf_SplitTM_FB(x);
			} else {
				if (colorOf_SplitTM_FB(rightOf_SplitTM_FB(sib)) == BLACK) {
					setColor_SplitTM_FB(leftOf_SplitTM_FB(sib), BLACK);
					setColor_SplitTM_FB(sib, RED);
					rotateRight_SplitTM_FB(Self, s, sib);
					sib = rightOf_SplitTM_FB(parentOf_SplitTM_FB(x));
				}
				setColor_SplitTM_FB(sib, colorOf_SplitTM_FB(parentOf_SplitTM_FB(x)));
				setColor_SplitTM_FB(parentOf_SplitTM_FB(x), BLACK);
				setColor_SplitTM_FB(rightOf_SplitTM_FB(sib), BLACK);
				rotateLeft_SplitTM_FB(Self, s, parentOf_SplitTM_FB(x));
			// TODO: consider break ...
				x = LDNODE_SPLIT_FB(s,root) ; 
			}
		} else { // symmetric
			node_t * sib = leftOf_SplitTM_FB(parentOf_SplitTM_FB(x));

			if (colorOf_SplitTM_FB(sib) == RED) {
				setColor_SplitTM_FB(sib, BLACK);
				setColor_SplitTM_FB(parentOf_SplitTM_FB(x), RED);
				rotateRight_SplitTM_FB(Self, s, parentOf_SplitTM_FB(x));
				sib = leftOf_SplitTM_FB(parentOf_SplitTM_FB(x));
			}

			if (colorOf_SplitTM_FB(rightOf_SplitTM_FB(sib)) == BLACK &&
			colorOf_SplitTM_FB(leftOf_SplitTM_FB(sib)) == BLACK) {
				setColor_SplitTM_FB(sib,  RED);
				x = parentOf_SplitTM_FB(x);
			} else {
				if (colorOf_SplitTM_FB(leftOf_SplitTM_FB(sib)) == BLACK) {
					setColor_SplitTM_FB(rightOf_SplitTM_FB(sib), BLACK);
					setColor_SplitTM_FB(sib, RED);
					rotateLeft_SplitTM_FB(Self, s, sib);
					sib = leftOf_SplitTM_FB(parentOf_SplitTM_FB(x));
				}
				setColor_SplitTM_FB(sib, colorOf_SplitTM_FB(parentOf_SplitTM_FB(x)));
				setColor_SplitTM_FB(parentOf_SplitTM_FB(x), BLACK);
				setColor_SplitTM_FB(leftOf_SplitTM_FB(sib), BLACK);
				rotateRight_SplitTM_FB(Self, s, parentOf_SplitTM_FB(x));
			// TODO: consider break ...
				x = LDNODE_SPLIT_FB(s,root) ; 
			}
		}

	}

	if (x != NULL && TXLDF_SPLIT_FB(x,c) != BLACK) {
		TXSTF_SPLIT_FB(x,c, BLACK) ; 
	}
	
}

///
static void fixAfterDeletion_pure(Thread * Self, set_t * s, node_t *  x) {
  while (x != LDNODE_PURE(s,root) && colorOf_pure(x) == BLACK) {
    POLL(return) ; 
    if (x == leftOf_pure(parentOf_pure(x))) {
      node_t * sib = rightOf_pure(parentOf_pure(x));
      if (colorOf_pure(sib) == RED) {
        setColor_pure(sib, BLACK);
        setColor_pure(parentOf_pure(x), RED);
        rotateLeft_pure(Self, s, parentOf_pure(x));
        sib = rightOf_pure(parentOf_pure(x));
      }

      if (colorOf_pure(leftOf_pure(sib))  == BLACK &&
        colorOf_pure(rightOf_pure(sib)) == BLACK) {
        setColor_pure(sib,  RED);
        x = parentOf_pure(x);
      } else {
        if (colorOf_pure(rightOf_pure(sib)) == BLACK) {
           setColor_pure(leftOf_pure(sib), BLACK);
           setColor_pure(sib, RED);
           rotateRight_pure(Self, s, sib);
           sib = rightOf_pure(parentOf_pure(x));
        }
        setColor_pure(sib, colorOf_pure(parentOf_pure(x)));
        setColor_pure(parentOf_pure(x), BLACK);
        setColor_pure(rightOf_pure(sib), BLACK);
        rotateLeft_pure(Self, s, parentOf_pure(x));
        // TODO: consider break ...
        x = LDNODE_PURE(s,root) ; 
      }
    } else { // symmetric
      node_t * sib = leftOf_pure(parentOf_pure(x));

      if (colorOf_pure(sib) == RED) {
        setColor_pure(sib, BLACK);
        setColor_pure(parentOf_pure(x), RED);
        rotateRight_pure(Self, s, parentOf_pure(x));
        sib = leftOf_pure(parentOf_pure(x));
      }

      if (colorOf_pure(rightOf_pure(sib)) == BLACK &&
        colorOf_pure(leftOf_pure(sib)) == BLACK) {
        setColor_pure(sib,  RED);
        x = parentOf_pure(x);
      } else {
        if (colorOf_pure(leftOf_pure(sib)) == BLACK) {
           setColor_pure(rightOf_pure(sib), BLACK);
           setColor_pure(sib, RED);
           rotateLeft_pure(Self, s, sib);
           sib = leftOf_pure(parentOf_pure(x));
        }
        setColor_pure(sib, colorOf_pure(parentOf_pure(x)));
        setColor_pure(parentOf_pure(x), BLACK);
        setColor_pure(leftOf_pure(sib), BLACK);
        rotateRight_pure(Self, s, parentOf_pure(x));
        // TODO: consider break ...
        x = LDNODE_PURE(s,root) ; 
      }
    }
  }

  if (x != NULL && TXLDF_PURE(x,c) != BLACK) {
    TXSTF_PURE(x,c, BLACK) ; 
  }
}

static void fixAfterDeletion_RH1_slow_path(Thread * Self, set_t * s, node_t *  x) {
  while (x != LDNODE_RH1_SP(s,root) && colorOf_RH1_slow_path(x) == BLACK) {
    POLL(return) ; 
    if (x == leftOf_RH1_slow_path(parentOf_RH1_slow_path(x))) {
      node_t * sib = rightOf_RH1_slow_path(parentOf_RH1_slow_path(x));
      if (colorOf_RH1_slow_path(sib) == RED) {
        setColor_RH1_slow_path(sib, BLACK);
        setColor_RH1_slow_path(parentOf_RH1_slow_path(x), RED);
        rotateLeft_RH1_slow_path(Self, s, parentOf_RH1_slow_path(x));
        sib = rightOf_RH1_slow_path(parentOf_RH1_slow_path(x));
      }

      if (colorOf_RH1_slow_path(leftOf_RH1_slow_path(sib))  == BLACK &&
        colorOf_RH1_slow_path(rightOf_RH1_slow_path(sib)) == BLACK) {
        setColor_RH1_slow_path(sib,  RED);
        x = parentOf_RH1_slow_path(x);
      } else {
        if (colorOf_RH1_slow_path(rightOf_RH1_slow_path(sib)) == BLACK) {
           setColor_RH1_slow_path(leftOf_RH1_slow_path(sib), BLACK);
           setColor_RH1_slow_path(sib, RED);
           rotateRight_RH1_slow_path(Self, s, sib);
           sib = rightOf_RH1_slow_path(parentOf_RH1_slow_path(x));
        }
        setColor_RH1_slow_path(sib, colorOf_RH1_slow_path(parentOf_RH1_slow_path(x)));
        setColor_RH1_slow_path(parentOf_RH1_slow_path(x), BLACK);
        setColor_RH1_slow_path(rightOf_RH1_slow_path(sib), BLACK);
        rotateLeft_RH1_slow_path(Self, s, parentOf_RH1_slow_path(x));
        // TODO: consider break ...
        x = LDNODE_RH1_SP(s,root) ; 
      }
    } else { // symmetric
      node_t * sib = leftOf_RH1_slow_path(parentOf_RH1_slow_path(x));

      if (colorOf_RH1_slow_path(sib) == RED) {
        setColor_RH1_slow_path(sib, BLACK);
        setColor_RH1_slow_path(parentOf_RH1_slow_path(x), RED);
        rotateRight_RH1_slow_path(Self, s, parentOf_RH1_slow_path(x));
        sib = leftOf_RH1_slow_path(parentOf_RH1_slow_path(x));
      }

      if (colorOf_RH1_slow_path(rightOf_RH1_slow_path(sib)) == BLACK &&
        colorOf_RH1_slow_path(leftOf_RH1_slow_path(sib)) == BLACK) {
        setColor_RH1_slow_path(sib,  RED);
        x = parentOf_RH1_slow_path(x);
      } else {
        if (colorOf_RH1_slow_path(leftOf_RH1_slow_path(sib)) == BLACK) {
           setColor_RH1_slow_path(rightOf_RH1_slow_path(sib), BLACK);
           setColor_RH1_slow_path(sib, RED);
           rotateLeft_RH1_slow_path(Self, s, sib);
           sib = leftOf_RH1_slow_path(parentOf_RH1_slow_path(x));
        }
        setColor_RH1_slow_path(sib, colorOf_RH1_slow_path(parentOf_RH1_slow_path(x)));
        setColor_RH1_slow_path(parentOf_RH1_slow_path(x), BLACK);
        setColor_RH1_slow_path(leftOf_RH1_slow_path(sib), BLACK);
        rotateRight_RH1_slow_path(Self, s, parentOf_RH1_slow_path(x));
        // TODO: consider break ...
        x = LDNODE_RH1_SP(s,root) ; 
      }
    }
  }

  if (x != NULL && TXLDF_RH1_SP(x,c) != BLACK) {
    TXSTF_RH1_SP(x,c, BLACK) ; 
  }
}

static node_t * _delete (Thread * Self, set_t * s, node_t * p) { 
  // If strictly internal, copy successor's element to p and then make p
  // point to successor.
  if (LDNODE(p,l) != NULL && LDNODE(p,r) != NULL) {
    node_t * s = _successor (Self, p);
    TXSTF(p,k, LDNODE(s,k)) ; 
    TXSTF(p,v, LDNODE(s,v)) ; 
    p = s;
  } // p has 2 children

  // Start fixup at replacement node, if it exists.
  node_t * replacement = (LDNODE(p,l) != NULL) ? LDNODE(p,l) : LDNODE(p,r);

  if (replacement != NULL) {
    // Link replacement to parent
    // TODO: precompute pp = p->p and substitute below ...
    // This is safe as long as we can guarantee that replacement != p.  
    TXSTF (replacement, p, LDNODEIW(p,p)) ; 
    node_t * pp = LDNODE(p,p) ; 
    if (pp == NULL)
      TXSTF(s, root, replacement) ; 
    else if (p == LDNODE(pp,l)) 
      TXSTF(pp,l, replacement) ; 
    else
      TXSTF(pp,r, replacement) ; 

    // Null out links so they are OK to use by fixAfterDeletion.
    TXSTF (p,l, NULL) ; 
    TXSTF (p,r, NULL) ; 
    TXSTF (p,p, NULL) ; 

    // Fix replacement
    if (TXLDF(p,c) == BLACK)
      fixAfterDeletion(Self, s, replacement);
  } else if (LDNODE(p,p) == NULL) { // return if we are the only node.
    TXSTF (s, root, NULL) ; 
  } else { //  No children. Use self as phantom replacement and unlink.
    if (TXLDF(p,c) == BLACK)
      fixAfterDeletion(Self, s,p);

    // CONSIDER: use LDNODEIW(p,p) as it's likely that p is not the root node,
    // in which case p->p will be non-null and we'll then store into p->p.  
    node_t * pp = LDNODE(p,p) ; 
    if (pp != NULL) {
      if (p == LDNODE(pp,l))
        TXSTF(pp,l, NULL) ; 
      else if (p == LDNODE(pp,r))
        TXSTF(pp,r, NULL) ; 
      TXSTF(p,p, NULL) ; 
    }
  }
  return p ; 
}

static node_t * _delete_SplitTM (Thread * Self, set_t * s, node_t * p) { 
  // If strictly internal, copy successor's element to p and then make p
  // point to successor.
  SPLIT_LOAD(p,l);
  SPLIT_LOAD(p,r);
  if (LDNODE_SPLIT(p,l) != NULL && LDNODE_SPLIT(p,r) != NULL) {
    node_t * s = _successor_SplitTM (Self, p);
    SPLIT_LOAD(s,k);
    SPLIT_LOAD(s,v);
	TXSTF_SPLIT(p,k, LDNODE_SPLIT(s,k)) ; 
    TXSTF_SPLIT(p,v, LDNODE_SPLIT(s,v)) ; 
    p = s;
  } // p has 2 children

  // Start fixup at replacement node, if it exists.
  SPLIT_LOAD(p,l);
  SPLIT_LOAD(p,r);
  SPLIT_LOAD(p,p);
  SPLIT_LOAD(p,c);
  node_t * replacement = (LDNODE_SPLIT(p,l) != NULL) ? LDNODE_SPLIT(p,l) : LDNODE_SPLIT(p,r);

  if (replacement != NULL) {
    // Link replacement to parent
    // TODO: precompute pp = p->p and substitute below ...
    // This is safe as long as we can guarantee that replacement != p.  
    SPLIT_LOAD(p,p);
	TXSTF_SPLIT (replacement, p, LDNODE_SPLIT(p,p)) ; 
    node_t * pp = LDNODE_SPLIT(p,p) ; 
    if (pp == NULL)
      TXSTF_SPLIT(s, root, replacement) ; 
    else {
		SPLIT_LOAD(pp,l);
		if (p == LDNODE_SPLIT(pp,l)) 
      		TXSTF_SPLIT(pp,l, replacement) ; 
    	else
      		TXSTF_SPLIT(pp,r, replacement) ; 
	}
    // Null out links so they are OK to use by fixAfterDeletion.
    TXSTF_SPLIT (p,l, NULL) ; 
    TXSTF_SPLIT (p,r, NULL) ; 
    TXSTF_SPLIT (p,p, NULL) ; 

    // Fix replacement
    SPLIT_LOAD(p,c);
	if (TXLDF_SPLIT(p,c) == BLACK) {
	   TX_SPLIT(Self, __LINE__);
       fixAfterDeletion_SplitTM(Self, s, replacement);
	}
  } else if (LDNODE_SPLIT(p,p) == NULL) { // return if we are the only node.
    TXSTF_SPLIT (s, root, NULL) ; 
  } else { //  No children. Use self as phantom replacement and unlink.
    if (TXLDF_SPLIT(p,c) == BLACK) {
      TX_SPLIT(Self, __LINE__);
      fixAfterDeletion_SplitTM(Self, s,p);
	}

    // CONSIDER: use LDNODE_SPLITIW(p,p) as it's likely that p is not the root node,
    // in which case p->p will be non-null and we'll then store into p->p.  
	SPLIT_LOAD(p,p);
	node_t * pp = LDNODE_SPLIT(p,p) ; 
	if (pp != NULL) {
		SPLIT_LOAD(pp,l);
		if (p == LDNODE_SPLIT(pp,l))
			TXSTF_SPLIT(pp,l, NULL) ; 
		else {
			SPLIT_LOAD(pp,r);
			if (p == LDNODE_SPLIT(pp,r))
				TXSTF_SPLIT(pp,r, NULL) ; 
		}
		TXSTF_SPLIT(p,p, NULL) ; 
	}
  }
  return p ; 
}

static node_t * _delete_SplitTM_FB (Thread * Self, set_t * s, node_t * p) { 
  // If strictly internal, copy successor's element to p and then make p
  // point to successor.
  if (LDNODE_SPLIT_FB(p,l) != NULL && LDNODE_SPLIT_FB(p,r) != NULL) {
    node_t * s = _successor_SplitTM_FB (Self, p);
    TXSTF_SPLIT_FB(p,k, LDNODE_SPLIT_FB(s,k)) ; 
    TXSTF_SPLIT_FB(p,v, LDNODE_SPLIT_FB(s,v)) ; 
    p = s;
  } // p has 2 children

  // Start fixup at replacement node, if it exists.
  node_t * replacement = (LDNODE_SPLIT_FB(p,l) != NULL) ? LDNODE_SPLIT_FB(p,l) : LDNODE_SPLIT_FB(p,r);

  if (replacement != NULL) {
    // Link replacement to parent
    // TODO: precompute pp = p->p and substitute below ...
    // This is safe as long as we can guarantee that replacement != p.  
    TXSTF_SPLIT_FB (replacement, p, LDNODE_SPLIT_FB(p,p)) ; 
    node_t * pp = LDNODE_SPLIT_FB(p,p) ; 
    if (pp == NULL)
      TXSTF_SPLIT_FB(s, root, replacement) ; 
    else if (p == LDNODE_SPLIT_FB(pp,l)) 
      TXSTF_SPLIT_FB(pp,l, replacement) ; 
    else
      TXSTF_SPLIT_FB(pp,r, replacement) ; 

    // Null out links so they are OK to use by fixAfterDeletion.
    TXSTF_SPLIT_FB (p,l, NULL) ; 
    TXSTF_SPLIT_FB (p,r, NULL) ; 
    TXSTF_SPLIT_FB (p,p, NULL) ; 

    // Fix replacement
    if (TXLDF_SPLIT_FB(p,c) == BLACK) {
       fixAfterDeletion_SplitTM_FB(Self, s, replacement);
	}
  } else if (LDNODE_SPLIT_FB(p,p) == NULL) { // return if we are the only node.
    TXSTF_SPLIT_FB (s, root, NULL) ; 
  } else { //  No children. Use self as phantom replacement and unlink.
    if (TXLDF_SPLIT_FB(p,c) == BLACK) {
      fixAfterDeletion_SplitTM_FB(Self, s,p);
	}

    // CONSIDER: use LDNODE_SPLIT_FBIW(p,p) as it's likely that p is not the root node,
    // in which case p->p will be non-null and we'll then store into p->p.  
	node_t * pp = LDNODE_SPLIT_FB(p,p) ; 
	if (pp != NULL) {
		if (p == LDNODE_SPLIT_FB(pp,l))
			TXSTF_SPLIT_FB(pp,l, NULL) ; 
		else if (p == LDNODE_SPLIT_FB(pp,r))
			TXSTF_SPLIT_FB(pp,r, NULL) ; 
		TXSTF_SPLIT_FB(p,p, NULL) ; 
	}
  }
  return p ; 
}

///
static node_t * _delete_pure (Thread * Self, set_t * s, node_t * p) { 
  // If strictly internal, copy successor's element to p and then make p
  // point to successor.
  if (LDNODE_PURE(p,l) != NULL && LDNODE_PURE(p,r) != NULL) {
    node_t * s = _successor_pure (Self, p);
    TXSTF_PURE(p,k, (intptr_t)LDNODE_PURE(s,k)) ; 
    TXSTF_PURE(p,v, (intptr_t)LDNODE_PURE(s,v)) ; 
    p = s;
  } // p has 2 children

  // Start fixup at replacement node, if it exists.
  node_t * replacement = (LDNODE_PURE(p,l) != NULL) ? LDNODE_PURE(p,l) : LDNODE_PURE(p,r);

  if (replacement != NULL) {
    // Link replacement to parent
    // TODO: precompute pp = p->p and substitute below ...
    // This is safe as long as we can guarantee that replacement != p.  
    TXSTF_PURE (replacement, p, LDNODE_PURE(p,p)) ; 
    node_t * pp = LDNODE_PURE(p,p) ; 
    if (pp == NULL)
      TXSTF_PURE(s, root, replacement) ; 
    else if (p == LDNODE_PURE(pp,l)) 
      TXSTF_PURE(pp,l, replacement) ; 
    else
      TXSTF_PURE(pp,r, replacement) ; 

    // Null out links so they are OK to use by fixAfterDeletion.
    TXSTF_PURE (p,l, NULL) ; 
    TXSTF_PURE (p,r, NULL) ; 
    TXSTF_PURE (p,p, NULL) ; 

    // Fix replacement
    if (TXLDF_PURE(p,c) == BLACK) {
       fixAfterDeletion_pure(Self, s, replacement);
	}
  } else if (LDNODE_PURE(p,p) == NULL) { // return if we are the only node.
    TXSTF_PURE (s, root, NULL) ; 
  } else { //  No children. Use self as phantom replacement and unlink.
    if (TXLDF_PURE(p,c) == BLACK) {
      fixAfterDeletion_pure(Self, s,p);
	}

    // CONSIDER: use LDNODE_PUREIW(p,p) as it's likely that p is not the root node,
    // in which case p->p will be non-null and we'll then store into p->p.  
    node_t * pp = LDNODE_PURE(p,p) ; 
    if (pp != NULL) {
      if (p == LDNODE_PURE(pp,l))
        TXSTF_PURE(pp,l, NULL) ; 
      else if (p == LDNODE_PURE(pp,r))
        TXSTF_PURE(pp,r, NULL) ; 
      TXSTF_PURE(p,p, NULL) ; 
    }
  }
  return p ; 
}

static node_t * _delete_RH1_slow_path (Thread * Self, set_t * s, node_t * p) { 
  // If strictly internal, copy successor's element to p and then make p
  // point to successor.
  if (LDNODE_RH1_SP(p,l) != NULL && LDNODE_RH1_SP(p,r) != NULL) {
    node_t * s = _successor_RH1_slow_path (Self, p);
    TXSTF_RH1_SP(p,k, (intptr_t)LDNODE_RH1_SP(s,k)) ; 
    TXSTF_RH1_SP(p,v, (intptr_t)LDNODE_RH1_SP(s,v)) ; 
    p = s;
  } // p has 2 children

  // Start fixup at replacement node, if it exists.
  node_t * replacement = (LDNODE_RH1_SP(p,l) != NULL) ? LDNODE_RH1_SP(p,l) : LDNODE_RH1_SP(p,r);

  if (replacement != NULL) {
    // Link replacement to parent
    // TODO: precompute pp = p->p and substitute below ...
    // This is safe as long as we can guarantee that replacement != p.  
    TXSTF_RH1_SP (replacement, p, LDNODE_RH1_SP(p,p)) ; 
    node_t * pp = LDNODE_RH1_SP(p,p) ; 
    if (pp == NULL)
      TXSTF_RH1_SP(s, root, replacement) ; 
    else if (p == LDNODE_RH1_SP(pp,l)) 
      TXSTF_RH1_SP(pp,l, replacement) ; 
    else
      TXSTF_RH1_SP(pp,r, replacement) ; 

    // Null out links so they are OK to use by fixAfterDeletion.
    TXSTF_RH1_SP (p,l, NULL) ; 
    TXSTF_RH1_SP (p,r, NULL) ; 
    TXSTF_RH1_SP (p,p, NULL) ; 

    // Fix replacement
    if (TXLDF_RH1_SP(p,c) == BLACK) {
       fixAfterDeletion_RH1_slow_path(Self, s, replacement);
	}
  } else if (LDNODE_RH1_SP(p,p) == NULL) { // return if we are the only node.
    TXSTF_RH1_SP (s, root, NULL) ; 
  } else { //  No children. Use self as phantom replacement and unlink.
    if (TXLDF_RH1_SP(p,c) == BLACK) {
      fixAfterDeletion_RH1_slow_path(Self, s,p);
	}

    // CONSIDER: use LDNODE_RH1_SPIW(p,p) as it's likely that p is not the root node,
    // in which case p->p will be non-null and we'll then store into p->p.  
    node_t * pp = LDNODE_RH1_SP(p,p) ; 
    if (pp != NULL) {
      if (p == LDNODE_RH1_SP(pp,l))
        TXSTF_RH1_SP(pp,l, NULL) ; 
      else if (p == LDNODE_RH1_SP(pp,r))
        TXSTF_RH1_SP(pp,r, NULL) ; 
      TXSTF_RH1_SP(p,p, NULL) ; 
    }
  }
  return p ; 
}

// -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
// Diagnostic section
// -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

static node_t * FirstEntry (set_t * s) {
  node_t * p = s->root ; 
  if (p != NULL) {
    while (p->l != NULL)
      p = p->l;
  }
  return p;
}

static node_t * successor(node_t * t) {
  if (t == NULL)
    return NULL;
  else if (t->r != NULL) {
    node_t * p = t->r;
    while (p->l != NULL)
      p = p->l ; 
    return p;
  } else {
    node_t * p = t->p;
    node_t * ch = t;
    while (p != NULL && ch == p->r) {
      ch = p;
      p = p->p ; 
    }
    return p;
  }
}


static node_t * predecessor (node_t * t) {
  if (t == NULL)
    return NULL;
  else if (t->l != NULL) {
    node_t * p = t->l ; 
    while (p->r != NULL) {
      p = p->r;
    }
    return p;
  } else {
    node_t * p = t->p;
    node_t * ch = t;
    while (p != NULL && ch == p->l) {
      ch = p;
      p = p->p ; 
    }
    return p;
  }
}

// Post-validate the RB-Tree structure - structural integrity check.
// Assumes no concurrent access.
//
// Compute the BH (BlackHeight) and validate the tree.
//
// This function recursively verifies that the given binary subtree satisfies
// three of the red black properties. It checks that every red node has only
// black children. It makes sure that each node is either red or black. And it
// checks that every path has the same count of black nodes from root to leaf.
// It returns the blackheight of the given subtree; this allows blackheights to
// be computed recursively and compared for left and right siblings for
// mismatches. It does _not check for every nil node being black.
// The return value of this function is the
// black height of the subtree rooted at the node ``root'', or zero if the
// subtree is not red-black.
// 

static int verify_redblack (node_t * root, int depth) { 
  if (root == NULL) return 1 ; 
  int height_left  = verify_redblack(root->l, depth+1);
  int height_right = verify_redblack(root->r, depth+1);
  if (height_left == 0 || height_right == 0) {
    return 0;
  }
  if (height_left != height_right) { 
    printf ("[INTEGRITY] Imbalance @depth=%d : %d %d\n", depth, height_left, height_right) ; 
    if (0) return 0;
  }

  if (root->l != NULL && root->l->p != root) { 
    printf ("[INTEGRITY] lineage\n") ; 
  }
  if (root->r != NULL && root->r->p != root) { 
    printf ("[INTEGRITY] lineage\n") ; 
  }

  // Red-Black alternation
  if (root->c == RED) {
    if (root->l != NULL && root->l->c != BLACK) {
      printf ("[INTEGRITY] VERIFY %d\n", __LINE__) ; 
      return 0;
    }
    if (root->r != NULL && root->r->c != BLACK) {
      printf ("[INTEGRITY] VERIFY %d\n", __LINE__) ; 
      return 0;
    }
    return height_left;
  }
  if (root->c != BLACK) {
    printf ("[INTEGRITY] VERIFY %d\n", __LINE__) ; 
    return 0;
  }
  return height_left + 1;
}

// Verify or validate the RB tree.  

int kv_verify (set_t * s, int Verbose) { 
  node_t * root = s->root ; 
  if (root == NULL) return 1 ; 
  if (Verbose) {
    printf ("Structural integrity check: ") ; 
  }
  if (0) { 
    printf ("  root=%X: key=%d color=%d\n", root, root->k, root->c) ; 
  }

  if (root->p != NULL) {
    printf ("  [INTEGRITY] root %X parent=%X\n", root, root->p) ; 
    return -1 ; 
  }
  if (root->c != BLACK) { 
    printf ("  [INTEGRITY] root %X color=%X\n", root, root->c) ; 
  }

  // Weak check of binary-tree property
  int ctr = 0 ; 
  node_t * its = FirstEntry(s) ; 
  while (its != NULL) { 
    ctr ++ ; 
    node_t * child = its->l ; 
    if (child != NULL && child->p != its) { 
      printf ("[INTEGRITY] Bad parent\n") ; 
    }
    child = its->r ; 
    if (child != NULL && child->p != its) { 
      printf ("[INTEGRITY] Bad parent\n") ; 
    }
    node_t * nxt = successor (its) ; 
    if (nxt == NULL) break ; 
    if (its->k >= nxt->k) { 
      printf ("[INTEGRITY] Key order %X (%d %d) %X (%d %d)\n", 
        its, its->k, its->v, nxt, nxt->k, nxt->v) ; 
      return -3;  
    }
    its = nxt ; 
  }

  int vfy = verify_redblack (root, 0) ; 
  if (Verbose) { 
    printf (" Nodes=%d Depth=%d\n", ctr, vfy) ; 
  }
  return vfy ; 
}


// ========================[API and Accessors]========================

void kv_access(Thread *Self, LockRecord *LockTab)
{

	TXLDF_PURE(LockTab, LockWord);
	TXSTF_PURE(LockTab, LockWord, 0); 
	TXLDF_IF(LockTab, LockWord);
	TXSTF_IF(LockTab, LockWord, 0); 

	TXSTF_RH1_FP(LockTab, LockWord, 0); 

	TXLDF_RH1_SP(LockTab, LockWord);
	TXSTF_RH1_SP(LockTab, LockWord, 0);
}

KVMap *kv_create(int maxcount, void * cmp) {
  KVMap * n = (KVMap * ) malloc (sizeof(*n)) ; 
  n->root = NULL ; 
#ifdef BENCH_HASH
  for (int i=0; i < HASH_SIZE; i++)
  {
    n->hash[i] = NULL;
  }
#endif
  return n ; 
}

static node_t  * GetNode (Thread * Self) {
  static int step = 0 ; 
  node_t * n = NULL ; 

  n = Self->NodeCache ; 
  if (n != NULL) {
    Self->NodeCache = n->NextFree ; 
    return n; 
  }

  if (n == NULL) { 
    // Consider: memalign() or with padding
    n = (node_t *) malloc (sizeof(*n)) ; 
    n->NextFree = NULL ; 
    if (0) memset (n, 0, sizeof(*n)) ; 
  }
  return n ; 
}

static void ReleaseNode (Thread * Self, node_t * n) { 
  const int _Alloc = Alloc ; 

  // Explicit privatization
  if (_Alloc & 0x10) {
    TxSterilize (Self, n, sizeof(*n)) ; 
  }

  // TSM and immortal -- Thread local caches are unbounded
  if ((_Alloc & 0xF) == 2) { 
    n->NextFree = Self->NodeCache ; 
    Self->NodeCache = n ; 
    return ; 
  }

  // Bounded cache -- Per-thread cache limited to at most one node.
  if ((_Alloc & 0xF) == 1) { 
    if (Self->NodeCache == NULL) {
      Self->NodeCache = n ; 
      return ; 
    }
  }

  // Explicit privatization ....
  if (NEVER) TxSterilize (Self, n, sizeof(*n)) ; 

  // In diagnostic mode we clobber the node to assist
  // in detecting use-after-free errors.
  if (NEVER) memset (n, 0xFF, sizeof(*n)) ; 
  free (n) ; 
}

// ld_insert AKA putIfAbsent

int kv_insert(Thread * Self, KVMap *dict, int Key, int Val) {
  Self->InFunc = "insert" ;       
  node_t * node = GetNode(Self) ; 
  TXSTART() ; 
  node_t * ex = _insert(Self, dict, Key, Val, node);
  TXEND ; 
  
  if (ex != NULL) ReleaseNode (Self, node) ; 
  return (ex == NULL) ; 

}

int kv_delete(Thread * Self, KVMap *dict, int Key) {
  Self->InFunc = "delete:lookup";     
  TXSTART() ; 
  node_t * node = _lookup(Self, dict, Key);
  if (node != NULL) {
    Self->InFunc = "delete:unlink";
    node = _delete(Self, dict, node);
  }
  TXEND ;

  if (node != NULL) ReleaseNode(Self, node) ;
  return (node != NULL) ;
}

int kv_delete_SplitTM(Thread * Self, KVMap *dict, int Key) {
	Self->InFunc = "delete:lookup";     
	
	TXSTART_SPLIT();
	if (Self->is_software == 1) {
		return -1;
	}
	node_t * node = _lookup_SplitTM(Self, dict, Key);
	if (node != NULL) {
		Self->InFunc = "delete:unlink";
		node = _delete_SplitTM(Self, dict, node);
	}
	TXEND_SPLIT(Self) ;
	
	if (node != NULL) {
		Self->keysum -= Key;
		ReleaseNode(Self, node) ;
	}
	return (node != NULL) ;
}

int kv_delete_SplitTM_FB(Thread * Self, KVMap *dict, int Key) {
	Self->InFunc = "delete:lookup";     
	
	TXSTART_SPLIT_FB();
	node_t * node = _lookup_SplitTM_FB(Self, dict, Key);
	if (node != NULL) {
		Self->InFunc = "delete:unlink";
		node = _delete_SplitTM_FB(Self, dict, node);
	}
	TXEND_SPLIT_FB(Self) ;
	
	if (node != NULL) {
		Self->keysum -= Key;
		ReleaseNode(Self, node) ;
	}
	return (node != NULL) ;
}

int kv_delete_pure(Thread * Self, KVMap *dict, int Key) {
	Self->InFunc = "delete:lookup";     
	
	if (0 == HTM_Start(Self))
	{
		return -1;
	}
	
	node_t * node = _lookup_pure(Self, dict, Key);
	if (node != NULL) {
		Self->InFunc = "delete:unlink";
		node = _delete_pure(Self, dict, node);
	}
	HTM_Commit();
	
	if (node != NULL) {
		Self->keysum -= Key;
		ReleaseNode(Self, node) ;
	}
	return (node != NULL) ;
}

int kv_delete_RH1_slow_path(Thread * Self, KVMap *dict, int Key) {
	Self->InFunc = "delete:lookup";     
	
	TXSTART_RH1_SP();
	
	node_t * node = _lookup_RH1_slow_path(Self, dict, Key);
	if (node != NULL) {
		Self->InFunc = "delete:unlink";
		node = _delete_RH1_slow_path(Self, dict, node);
	}
	TXEND_RH1_SP;
	
	if (node != NULL) {
		Self->keysum -= Key;
		ReleaseNode(Self, node) ;
	}
	return (node != NULL) ;
}


// ld_put AKA ld_set

int kv_put (Thread * Self, KVMap * dict, int Key, int Val) {
  Self->InFunc = "put" ;
  node_t * nn = GetNode(Self) ;
  TXSTART();
  node_t * ex = _insert (Self, dict, Key, Val, nn) ;
  if (ex != NULL) {
    TXSTF(ex,v,Val) ;
    TXEND ;
    ReleaseNode (Self, nn);
    return 0  ;
  } else {
    TXEND ;
    return 1;
  }
}

int kv_put_raw (Thread * Self, KVMap * dict, int Key, int Val) {
	Self->InFunc = "put" ;
	node_t * nn = GetNode(Self) ;
	
	node_t * ex = _insert_pure (Self, dict, Key, Val, nn) ;
	
	if (ex != NULL) {
		TXSTF_PURE(ex,v,Val) ;
		ReleaseNode (Self, nn);
		return 0  ;
	} else {
		return 1;
	}
}

int kv_put_pure (Thread * Self, KVMap * dict, int Key, int Val) {
	Self->InFunc = "put" ;
	node_t * nn = GetNode(Self) ;
	
	if (0 == HTM_Start(Self))
	{
		return -1;
	}
	
	node_t * ex = _insert_pure (Self, dict, Key, Val, nn) ;
	
	if (ex != NULL) {
		TXSTF_PURE(ex,v,Val) ;
		HTM_Commit();
		ReleaseNode (Self, nn);
		return 0  ;
	} else {
		HTM_Commit();
		Self->keysum += Key;
		return 1;
	}
}

int kv_put_RH1Norec_fast_path (Thread * Self, KVMap * dict, int Key, int Val) {
	Self->InFunc = "put" ;
	node_t * nn = GetNode(Self) ;
	
	Self->is_writer = 1;
	if (0 == TxStart_RH1Norec_fast_path(Self))
	{
		Self->is_writer = 0;
		return -1;
	}
	
	node_t * ex = _insert_pure (Self, dict, Key, Val, nn) ;
	
	if (ex != NULL) {
		TXSTF_PURE(ex,v,Val) ;
		TxCommit_RH1Norec_fast_path(Self);
		ReleaseNode (Self, nn);
		Self->is_writer = 0;
		return 0  ;
	} else {
		TxCommit_RH1Norec_fast_path(Self);
		Self->is_writer = 0;
		return 1;
	}
}

int kv_put_IF_fast_path (Thread * Self, KVMap * dict, int Key, int Val) {
	Self->InFunc = "put" ;
	node_t * nn = GetNode(Self) ;
	
	if (0 == TxStart_IF_fast_path(Self))
	{
		return -1;
	}
	
	node_t * ex = _insert_IF (Self, dict, Key, Val, nn) ;
	
	if (ex != NULL) {
		TXSTF_IF(ex,v,Val) ;
		TxCommit_IF_fast_path(Self);
		ReleaseNode (Self, nn);
		return 0  ;
	} else {
		TxCommit_IF_fast_path(Self);
		return 1;
	}
}

int kv_put_RH1_fast_path (Thread * Self, KVMap * dict, int Key, int Val) {
	Self->InFunc = "put" ;
	node_t * nn = GetNode(Self) ;
	
	if (0 == TxStart_RH1_fast_path(Self))
	{
		return -1;
	}
	
	node_t * ex = _insert_RH1_fast_path (Self, dict, Key, Val, nn) ;
	
	if (ex != NULL) {
		TXSTF_RH1_FP(ex,v,Val) ;
		TxCommit_RH1_fast_path(Self);
		ReleaseNode (Self, nn);
		return 0  ;
	} else {
		TxCommit_RH1_fast_path(Self);
		return 1;
	}
}

int kv_put_RH1_slow_path (Thread * Self, KVMap * dict, int Key, int Val) {
	Self->InFunc = "put" ;
	node_t * nn = GetNode(Self) ;
	
	TXSTART_RH1_SP();
	
	node_t * ex = _insert_RH1_slow_path (Self, dict, Key, Val, nn) ;
	
	if (ex != NULL) {
		TXSTF_RH1_SP(ex,v,Val) ;
		TXEND_RH1_SP ;
		ReleaseNode (Self, nn);
		return 0  ;
	} else {
		TXEND_RH1_SP ;
		Self->keysum += Key;
		return 1;
	}
}

int kv_put_RH1Norec_slow_path (Thread * Self, KVMap * dict, int Key, int Val) {
	Self->InFunc = "put" ;
	node_t * nn = GetNode(Self) ;
	
	TXSTART_RH1NOREC_SP();
	
	node_t * ex = _insert_RH1Norec_slow_path (Self, dict, Key, Val, nn) ;
	
	if (ex != NULL) {
		TXSTF_RH1NOREC_SP(ex,v,Val) ;
		TXEND_RH1NOREC_SP ;
		ReleaseNode (Self, nn);
		return 0  ;
	} else {
		TXEND_RH1NOREC_SP ;
		return 1;
	}
}

int kv_put_SplitTM (Thread * Self, KVMap * dict, int Key, int Val) {
	Self->InFunc = "put" ;
	node_t * nn = GetNode(Self) ;
	
	TXSTART_SPLIT();
	if (Self->is_software == 1) {
		ReleaseNode (Self, nn);
		return -1;
	}
	
	node_t * ex = _insert_SplitTM (Self, dict, Key, Val, nn) ;
	
	if (ex != NULL) {
		TXSTF_SPLIT(ex,v,Val) ;
		TXEND_SPLIT(Self) ;
		ReleaseNode (Self, nn);
		return 0  ;
	} else {
		TXEND_SPLIT(Self) ;
		Self->keysum += Key;
		return 1;
	}
}

int kv_put_SplitTM_FB (Thread * Self, KVMap * dict, int Key, int Val) {
	Self->InFunc = "put" ;
	node_t * nn = GetNode(Self) ;
	
	TXSTART_SPLIT_FB();
	
	node_t * ex = _insert_SplitTM_FB (Self, dict, Key, Val, nn) ;
	
	if (ex != NULL) {
		TXSTF_SPLIT_FB(ex,v,Val) ;
		TXEND_SPLIT_FB(Self) ;
		ReleaseNode (Self, nn);
		return 0  ;
	} else {
		TXEND_SPLIT_FB(Self) ;
		Self->keysum += Key;
		return 1;
	}
}

int kv_get (Thread * Self, KVMap  * dict, int Key) {
  Self->InFunc = "get" ;
  TXSTART();
  node_t * n = _lookup(Self, dict, Key) ;
  if (n != NULL) {
    int val = TXLDF(n,v) ;
    TXEND ;
    return val ;
  }
  TXEND ;
  return 0 ;
}

int kv_get_pure (Thread * Self, KVMap  * dict, int Key) {
	Self->InFunc = "get" ;
	if (0 == HTM_Start(Self))
	{
		return -1;
	}
	node_t * n = _lookup_pure(Self, dict, Key) ;
	if (n != NULL) {
		int val = TXLDF_PURE(n,v) ;
		HTM_Commit();
		return val ;
	}
	HTM_Commit();
	return 0 ;
}

int kv_get_RH1Norec_fast_path (Thread * Self, KVMap  * dict, int Key) {
	Self->InFunc = "get" ;
	Self->is_writer = 0;
	if (0 == TxStart_RH1Norec_fast_path(Self))
	{
		return -1;
	}
	node_t * n = _lookup_pure(Self, dict, Key) ;
	if (n != NULL) {
		int val = TXLDF_PURE(n,v) ;
		TxCommit_RH1Norec_fast_path(Self);
		return val ;
	}
	TxCommit_RH1Norec_fast_path(Self);
	return 0 ;
}

int kv_get_IF_fast_path (Thread * Self, KVMap  * dict, int Key) {
	Self->InFunc = "get" ;
	if (0 == TxStart_IF_fast_path(Self))
	{
		return -1;
	}
	node_t * n = _lookup_IF(Self, dict, Key) ;
	if (n != NULL) {
		int val = TXLDF_IF(n,v) ;
		TxCommit_IF_fast_path(Self);
		return val ;
	}
	TxCommit_IF_fast_path(Self);
	return 0 ;
}

int kv_get_RH1_fast_path (Thread * Self, KVMap  * dict, int Key) {
	Self->InFunc = "get" ;
	if (0 == TxStart_RH1_fast_path(Self))
	{
		return -1;
	}
	node_t * n = _lookup_pure(Self, dict, Key) ;
	if (n != NULL) {
		int val = TXLDF_PURE(n,v) ;
		TxCommit_RH1_fast_path(Self);
		return val ;
	}
	TxCommit_RH1_fast_path(Self);
	return 0 ;
}

int kv_get_RH1_slow_path (Thread * Self, KVMap  * dict, int Key) {
	Self->InFunc = "get" ;
	
	TXSTART_RH1_SP();
	
	node_t * n = _lookup_RH1_slow_path(Self, dict, Key) ;
	if (n != NULL) {
		int val = TXLDF_RH1_SP(n,v) ;
		TXEND_RH1_SP;
		return val ;
	}
	TXEND_RH1_SP;
	return 0 ;
}

int kv_get_RH1Norec_slow_path (Thread * Self, KVMap  * dict, int Key) {
	Self->InFunc = "get" ;
	
	TXSTART_RH1NOREC_SP();
	
	node_t * n = _lookup_RH1Norec(Self, dict, Key) ;
	if (n != NULL) {
		int val = TXLDF_RH1NOREC_SP(n,v) ;
		TXEND_RH1NOREC_SP;
		return val ;
	}
	TXEND_RH1NOREC_SP;
	return 0 ;
}

int kv_get_SplitTM (Thread * Self, KVMap  * dict, int Key) {
	Self->InFunc = "get" ;
	
	//printf("kv_get_SplitTM 1\n");
	TXSTART_SPLIT();
	if (Self->is_software == 1) {
		return -1;
	}
	
	node_t * n = _lookup_SplitTM(Self, dict, Key) ;
	if (n != NULL) {
		int val = TXLDF_SPLIT(n,v) ;
		TXEND_SPLIT(Self) ;
		return val ;
	}
	
	TXEND_SPLIT(Self) ;
	//printf("kv_get_SplitTM 2\n");
	return 0 ;
}

int kv_get_SplitTM_FB (Thread * Self, KVMap  * dict, int Key) {
	Self->InFunc = "get" ;
	
	TXSTART_SPLIT_FB();
	
	node_t * n = _lookup_SplitTM_FB(Self, dict, Key) ;
	if (n != NULL) {
		int val = TXLDF_SPLIT_FB(n,v) ;
		TXEND_SPLIT_FB(Self) ;
		return val ;
	}
	TXEND_SPLIT_FB(Self) ;
	return 0 ;
}

// ld_contains AKA ld_lookup

int kv_contains (Thread * Self, KVMap * dict, int Key) {
  Self->InFunc = "contains" ;
  TXSTART() ;
  node_t * n = _lookup(Self, dict, Key) ;
  TXEND ;
  return n != NULL ;
}

int kv_contains_raw (Thread * Self, KVMap * dict, int Key) {
  Self->InFunc = "contains" ;

  node_t * n = _lookup_pure(Self, dict, Key) ;

  return n != NULL ;
}


// Gets the entry corresponding to the specified key; if no such entry
// exists, returns the entry for the least key greater than the specified
// key; if no such entry exists (i.e., the greatest key in the Tree is less
// than the specified key), returns NULL.
// Ceil() is a specialized variant of _lookup() : returns node >= Key.  
// See getCeilingEntry() in TreeMap.java
//
// TODO-FIXME: add tree structural integrity asserts to Ceil(), NextHigher(). 

static node_t * Ceil (Thread * Self, KVMap * dict, int Key) {
  node_t * p = LDNODE(dict,root); 
  while (p != NULL) {
    POLL(return NULL); 
    const int cmp = Key - TXLDF(p,k) ; 
    if (cmp == 0) break ;   // EQUIV:  return p
    if (cmp < 0) {
      node_t * const Left = LDNODE(p,l) ; 
      if (Left != NULL) {
        p = Left ; 
      } else {
        break ;         // EQUIV: return p 
      } 
    } else { 
      node_t *const Right = LDNODE(p,r) ; 
      if (Right != NULL) { 
        p = Right ; 
      } else { 
        // Ascending toward higher nodes
        node_t * parent = LDNODE(p,p) ; 
        node_t * ch = p ; 
        while (parent != NULL && ch == LDNODE(parent,r)) { 
          ch = parent ; 
          parent = LDNODE(parent,p) ; 
          POLL(break) ; 
        }
        return parent ; 
      } 
    }
  }
  return p ; 
}

// Returns the entry for the least key greater than the specified key.  
// Conceptually: return E where E > K.  
// See TreeMap.java getHigherEntry()

static node_t * NextHigher (Thread * Self, KVMap * dict, int Key) {
  node_t * p = LDNODE(dict,root); 
  while (p != NULL) {
    POLL(return NULL) ; 
    const int cmp = Key - TXLDF(p,k) ; 
    if (cmp < 0) {
      node_t * const Left = LDNODE(p,l) ; 
      if (Left != NULL) {
        p = Left ; 
      } else {
        break ;     // Equiv: return p
      }
    } else {
      node_t * const Right = LDNODE(p,r) ; 
      if (Right != NULL) { 
        p = Right ; 
      } else { 
        // Ascend to higher nodes in tree
        // A slightly better loop formulation would be:
        //   for
        //     ch = p ; 
        //     p = p.parent
        //     if p == null || ch != p.right break
        node_t * parent = LDNODE(p,p) ; 
        node_t * ch = p ; 
        while (parent != NULL && ch == LDNODE(parent,r)) { 
          ch = parent ; 
          parent = LDNODE(parent,p) ; 
          POLL(break) ; 
        }
        return parent ; 
      }
    }
  }
  return p ; 
}

// ReadKeys() : Enumerates next N keys > Key in ascending key order.
// Return value: >= 0 --> Number of keys returned via FoundKeys
// Return value: < 0  --> abort
// Note that if we abort part way after having reported M < N keys then we could
// just return M.  That is, allow partial "short" segments to be returned.  
// Exhibits lots of intra-txn key-locality, 1oral and spatial locality -- RAR. 
// Common sub-tree locality.


int kv_ReadKeys (Thread * Self, KVMap * dict, int Key, int * FoundKeys, int N) {
  Self->InFunc = "ElementAt" ;
  TXSTART({ return -1; }) ;             // Expose abort to caller
  int Found = 0 ;
  // Consider: start with either Ceil() or NextHigher().  
  node_t * cursor = NextHigher (Self, dict, Key) ;
  while (cursor != NULL && Found < N) {
    FoundKeys[Found++] = TXLDF(cursor,k) ;
    cursor = _successor (Self, cursor) ;
  }
  TXEND ;
  return Found ;
}

static int Patience = 100 ; 

// ForEach primitive : iterator/enumerator/visitor
// Consider using for loop with FirstEntry() and successor
// Consider using closure-style callbacks.  

int  kv_ForEach (Thread * Self, KVMap * dict, int From, int Upto, int Irrevocable , int * Sum) {
  Self->InFunc = "ForEach" ;
  TXSTART ({return 0;})     // Expose abort to caller
  *Sum = 0 ;
  // TODO-FIXME: pass IrrevocablePatience value
  // If (IrrevocablePatience > 0) TxIrrevocable (Self, IrrevocablePatience) 
  if (Irrevocable) {
    TxIrrevocable (Self, Patience) ;
  }
  for (; From < Upto; From++) {
    node_t * const n = _lookup (Self, dict, From) ;
    if (n != NULL) {
      // Or perhaps just count the # of nodes in the range instead
      // tallying the values
      int v = TXLDF(n,v) ;
      if (0) { TXSTF(n,v,v) ; }
      *Sum += v ;
    }
  }
  TXEND ;
  return 1 ;
}


char * kv_init() {
  char * p = getenv ("ALLOC") ; 
  Alloc = p ? strtol (p, NULL, 0) : Alloc ; 
  p = getenv ("PATIENCE") ; 
  Patience = p ? strtol (p, NULL, 0) : Patience ; 
  printf ("RedBlack (Alloc=%d, Patience=%d)\n", Alloc, Patience) ; 
  return "" ; 
}

/***node_t * _LIST_lookup (Thread * Self, KVMap * s, int k) {
  node_t * p = LDNODE(s,root); 
  while (p != NULL) {
    int cmp = k - TXLDF(p,k) ; 
	TXLDF(p,v);
	if (cmp == 0) return p ; 
    p = LDNODE(p,r) ; 
    POLL(return NULL) ; 
  }
  return NULL ; 
}

node_t * _LIST_lookup_IF (Thread * Self, KVMap * s, int k) {
  node_t * p = LDNODE_IF(s,root); 
  while (p != NULL) {
    int cmp = k - TXLDF_IF(p,k) ; 
	if (cmp == 0) return p ; 
    p = LDNODE_IF(p,r) ; 
    POLL(return NULL) ; 
  }
  return NULL ; 
}

node_t * _LIST_lookup_RH1_slow_path (Thread * Self, KVMap * s, int k) {
  node_t * p = LDNODE_RH1_SP(s,root); 
  while (p != NULL) {
    int cmp = k - TXLDF_RH1_SP(p,k) ; 
	if (cmp == 0) return p ; 
    p = LDNODE_RH1_SP(p,r) ; 
    POLL(return NULL) ; 
  }
  return NULL ; 
}

node_t * _LIST_lookup_RH1Norec_slow_path (Thread * Self, KVMap * s, int k) {
  node_t * p = LDNODE_RH1NOREC_SP(s,root); 
  while (p != NULL) {
    int cmp = k - TXLDF_RH1NOREC_SP(p,k) ; 
	if (cmp == 0) return p ; 
    p = LDNODE_RH1NOREC_SP(p,r) ; 
    POLL(return NULL) ; 
  }
  return NULL ; 
}

node_t * _LIST_lookup_pure (Thread * Self, KVMap * s, int k) {
  node_t * p = s->root; 
  while (p != NULL) {
    int cmp = k - p->k ; 
	if (cmp == 0) return p ; 
    p = p->r ; 
    POLL(return NULL) ; 
  }
  return NULL ; 
}

static node_t *  _LIST_insert (Thread * Self, set_t * s, int k, int v, node_t * n) { 
  node_t * cur  = LDNODE(s,root) ; 
  if (cur == NULL) {
    if (n == NULL) return NULL ; 
    // Note: the following STs don't really need to be transactional.  
    TXSTF(n,l, NULL) ; 
	TXSTF(n,r, NULL) ; 
    TXSTF(n,k, k) ; 
    TXSTF(n,v, v) ; 
    TXSTF(s,root, n) ; 
    return NULL ; 
  }

  for (;;) { 
    POLL(return NULL) ; 
    intptr_t cmp = k - TXLDF(cur,k) ; 
    if (cmp == 0) {
      return cur ; 
    } else if (cmp > 0) {
      node_t * next = LDNODE(cur,r) ; 
      if (next != NULL) {
        cur = next ; 
      } else {
        TXSTF(n,l, cur) ; 
        TXSTF(n,r, NULL) ; 
        TXSTF(n,k, k) ; 
        TXSTF(n,v, v) ; 
        TXSTF(cur,r, n) ; 
        return NULL ;
      }
    } else { // cmp < 0
      node_t * prev = LDNODE(cur,l) ; 
      if (prev != NULL) {
        TXSTF(prev,r, n) ; 
        TXSTF(n,l, prev) ; 
		TXSTF(n,r, cur) ; 
		TXSTF(cur,l, n) ;
        TXSTF(n,k, k) ; 
        TXSTF(n,v, v) ; 
		return NULL ;
      } else {
        TXSTF(s,root, n) ; 
		TXSTF(n,l, NULL) ; 
        TXSTF(n,r, cur) ; 
        TXSTF(cur,l, n) ;
		TXSTF(n,k, k) ; 
        TXSTF(n,v, v) ; 
        return NULL;
      }
    }
  }
}

static node_t *  _LIST_insert_pure (Thread * Self, set_t * s, int k, int v, node_t * n) { 
  node_t * cur  = LDNODE_PURE(s,root) ; 
  if (cur == NULL) {
    if (n == NULL) return NULL ; 
    // Note: the following STs don't really need to be transactional.  
    TXSTF_PURE(n,l, NULL) ; 
	TXSTF_PURE(n,r, NULL) ; 
    TXSTF_PURE(n,k, k) ; 
    TXSTF_PURE(n,v, v) ; 
    TXSTF_PURE(s,root, n) ; 
    return NULL ; 
  }

  for (;;) { 
    POLL(return NULL) ; 
    intptr_t cmp = k - TXLDF_PURE(cur,k) ; 
    if (cmp == 0) {
      return cur ; 
    } else if (cmp > 0) {
      node_t * next = LDNODE_PURE(cur,r) ; 
      if (next != NULL) {
        cur = next ; 
      } else {
        TXSTF_PURE(n,l, cur) ; 
        TXSTF_PURE(n,r, NULL) ; 
        TXSTF_PURE(n,k, k) ; 
        TXSTF_PURE(n,v, v) ; 
        TXSTF_PURE(cur,r, n) ; 
        return NULL ;
      }
    } else { // cmp < 0
      node_t * prev = LDNODE_PURE(cur,l) ; 
      if (prev != NULL) {
        TXSTF_PURE(prev,r, n) ; 
        TXSTF_PURE(n,l, prev) ; 
		TXSTF_PURE(n,r, cur) ; 
		TXSTF_PURE(cur,l, n) ;
        TXSTF_PURE(n,k, k) ; 
        TXSTF_PURE(n,v, v) ; 
		return NULL ;
      } else {
        TXSTF_PURE(s,root, n) ; 
		TXSTF_PURE(n,l, NULL) ; 
        TXSTF_PURE(n,r, cur) ; 
        TXSTF_PURE(cur,l, n) ;
		TXSTF_PURE(n,k, k) ; 
        TXSTF_PURE(n,v, v) ; 
        return NULL;
      }
    }
  }
}

static void _LIST_delete (Thread * Self, set_t * s, node_t * p) { 
  node_t * prev = LDNODE(p,l);
  node_t * next = LDNODE(p,r);
  if (prev != NULL)
  {
	TXSTF(prev, r, next); 
  } else
  {
	TXSTF(s, root, next); 
  }
  if (next != NULL)
  {
	TXSTF(next, l, prev); 
  } 
}

int LIST_add (Thread * Self, KVMap * dict, int Key, int Val)  {
  Self->InFunc = "put" ;
  node_t * nn = GetNode(Self) ;
  TXSTART();
  node_t * ex = _LIST_insert (Self, dict, Key, Val, nn) ;
  if (ex != NULL) {
    TXSTF(ex,v,Val) ;
    TXEND ;
    ReleaseNode (Self, nn);
    return 0  ;
  } else {
    TXEND ;
	return 1;
  }
}

int LIST_add_raw (Thread * Self, KVMap * dict, int Key, int Val)  {
  Self->InFunc = "put" ;
  node_t * nn = GetNode(Self) ;

  node_t * ex = _LIST_insert_pure (Self, dict, Key, Val, nn) ;
  if (ex != NULL) {
    ex->v = Val ;
    ReleaseNode (Self, nn);
    return 0  ;
  } else {
	return 1;
  }
}

int LIST_contains (Thread * Self, KVMap * dict, int Key) {
  Self->InFunc = "contains" ;
  TXSTART() ;
  node_t * n = _LIST_lookup(Self, dict, Key) ;
  TXEND ;
  return n != NULL ;
}

int LIST_contains_raw (Thread * Self, KVMap * dict, int Key) {
  Self->InFunc = "contains" ;

  node_t * n = _LIST_lookup_pure(Self, dict, Key) ;

  return n != NULL ;
}

int LIST_del (Thread * Self, KVMap *dict, int Key) {
  Self->InFunc = "delete:lookup";     
  TXSTART() ; 
  node_t * node = _LIST_lookup(Self, dict, Key);
  if (node != NULL) {
    Self->InFunc = "delete:unlink";
    _LIST_delete(Self, dict, node);
  }
  TXEND ;

  if (node != NULL) ReleaseNode(Self, node) ;
  return (node != NULL) ;
}

// HASH

int _HASH_Hash(Thread * Self, int Key)
{
#ifdef BENCH_HASH
	return Key % HASH_SIZE;
#endif
}

node_t * _HASH_lookup (Thread * Self, KVMap * s, int k) {
  int bin = _HASH_Hash(Self, k);
  //printf("search %d %d\n", bin, k);
  node_t * p = LDNODE(s,hash[bin]); 
  while (p != NULL) {
    int cmp = k - TXLDF(p,k) ;
	TXLDF(p,v);
	if (cmp == 0) return p ; 
    p = LDNODE(p,r) ; 
    POLL(return NULL) ; 
  }
  return NULL ; 
}

node_t * _HASH_lookup_RH1_slow_path (Thread * Self, KVMap * s, int k) {
  int bin = _HASH_Hash(Self, k);
  //printf("search %d %d\n", bin, k);
  node_t * p = LDNODE_RH1_SP(s,hash[bin]); 
  while (p != NULL) {
    int cmp = k - TXLDF_RH1_SP(p,k) ;
	TXLDF_RH1_SP(p,v);
	if (cmp == 0) return p ; 
    p = LDNODE_RH1_SP(p,r) ; 
    POLL(return NULL) ; 
  }
  return NULL ; 
}

node_t * _HASH_lookup_IF (Thread * Self, KVMap * s, int k) {
  int bin = _HASH_Hash(Self, k);
  //printf("search %d %d\n", bin, k);
  node_t * p = LDNODE_IF(s,hash[bin]); 
  while (p != NULL) {
    int cmp = k - TXLDF_IF(p,k) ; 
	if (cmp == 0) return p ; 
    p = LDNODE_IF(p,r) ; 
    POLL(return NULL) ; 
  }
  return NULL ; 
}

node_t * _HASH_lookup_test (Thread * Self, KVMap * s, int k) {
  int bin = _HASH_Hash(Self, k);
  int index;
  //printf("search %d %d\n", bin, k);
  node_t * p = s->hash[bin];
  (Self->rsp++)->addr = (volatile intptr_t *)&(s->hash[bin]) ;
  index = PSLOCK_INDEX(Self->rsp->addr);
  read_arr[Self->UniqID][index] = Self->local_counter;
  while (p != NULL) {
    int cmp = k - p->k ; 
	(Self->rsp++)->addr = (volatile intptr_t *)&(p->k) ;
	index = PSLOCK_INDEX(Self->rsp->addr);
	read_arr[Self->UniqID][index] = Self->local_counter;
	if (cmp == 0) return p ; 
    p = p->r ; 
	(Self->rsp++)->addr = (volatile intptr_t *)&(p->r) ;
	index = PSLOCK_INDEX(Self->rsp->addr);
	read_arr[Self->UniqID][index] = Self->local_counter;
    POLL(return NULL) ; 
  }
  return NULL ; 
}

node_t * _HASH_lookup_pure (Thread * Self, KVMap * s, int k) {
  int bin = _HASH_Hash(Self, k);
  //printf("search %d %d\n", bin, k);
  node_t * p = s->hash[bin]; 
  while (p != NULL) {
    int cmp = k - (p->k) ; 
	if (cmp == 0) return p ; 
    p = p->r ; 
    POLL(return NULL) ; 
  }
  return NULL ; 
}

int HASH_bin_size (Thread * Self, KVMap * s, int bin) {
  int size = 0;
  node_t * p = s->hash[bin]; 
  while (p != NULL) {
    size++;
    p = p->r ; 
  }
  return size ; 
}

static node_t *  _HASH_insert (Thread * Self, set_t * s, int k, int v, node_t * n) { 
  int bin = _HASH_Hash(Self, k);
  
  //printf("insert %d\n", bin);
  node_t * cur  = LDNODE(s,hash[bin]) ; 
  if (cur == NULL) {
    if (n == NULL) return NULL ; 
    // Note: the following STs don't really need to be transactional.  
    TXSTF(n,l, NULL) ; 
	TXSTF(n,r, NULL) ; 
    TXSTF(n,k, k) ; 
    TXSTF(n,v, v) ; 
    TXSTF(s,hash[bin], n) ; 
    return NULL ; 
  }

  for (;;) { 
    POLL(return NULL) ; 
    intptr_t cmp = k - TXLDF(cur,k) ; 
    if (cmp == 0) {
      return cur ; 
    } else if (cmp > 0) {
      node_t * next = LDNODE(cur,r) ; 
      if (next != NULL) {
        cur = next ; 
      } else {
        TXSTF(n,l, cur) ; 
        TXSTF(n,r, NULL) ; 
        TXSTF(n,k, k) ; 
        TXSTF(n,v, v) ; 
        TXSTF(cur,r, n) ; 
        return NULL ;
      }
    } else { // cmp < 0
      node_t * prev = LDNODE(cur,l) ; 
      if (prev != NULL) {
        TXSTF(prev,r, n) ; 
        TXSTF(n,l, prev) ; 
		TXSTF(n,r, cur) ; 
		TXSTF(cur,l, n) ;
        TXSTF(n,k, k) ; 
        TXSTF(n,v, v) ; 
		return NULL ;
      } else {
        TXSTF(s,hash[bin], n) ; 
		TXSTF(n,l, NULL) ; 
        TXSTF(n,r, cur) ; 
        TXSTF(cur,l, n) ;
		TXSTF(n,k, k) ; 
        TXSTF(n,v, v) ; 
        return NULL;
      }
    }
  }
}

static node_t *  _HASH_insert_pure (Thread * Self, set_t * s, int k, int v, node_t * n) { 
  int bin = _HASH_Hash(Self, k);
  
  //printf("insert %d\n", bin);
  node_t * cur  = LDNODE_PURE(s,hash[bin]) ; 
  if (cur == NULL) {
    if (n == NULL) return NULL ; 
    // Note: the following STs don't really need to be transactional.  
    TXSTF_PURE(n,l, NULL) ; 
	TXSTF_PURE(n,r, NULL) ; 
    TXSTF_PURE(n,k, k) ; 
    TXSTF_PURE(n,v, v) ; 
    TXSTF_PURE(s,hash[bin], n) ; 
    return NULL ; 
  }

  for (;;) { 
    POLL(return NULL) ; 
    intptr_t cmp = k - TXLDF_PURE(cur,k) ; 
    if (cmp == 0) {
      return cur ; 
    } else if (cmp > 0) {
      node_t * next = LDNODE_PURE(cur,r) ; 
      if (next != NULL) {
        cur = next ; 
      } else {
        TXSTF_PURE(n,l, cur) ; 
        TXSTF_PURE(n,r, NULL) ; 
        TXSTF_PURE(n,k, k) ; 
        TXSTF_PURE(n,v, v) ; 
        TXSTF_PURE(cur,r, n) ; 
        return NULL ;
      }
    } else { // cmp < 0
      node_t * prev = LDNODE_PURE(cur,l) ; 
      if (prev != NULL) {
        TXSTF_PURE(prev,r, n) ; 
        TXSTF_PURE(n,l, prev) ; 
		TXSTF_PURE(n,r, cur) ; 
		TXSTF_PURE(cur,l, n) ;
        TXSTF_PURE(n,k, k) ; 
        TXSTF_PURE(n,v, v) ; 
		return NULL ;
      } else {
        TXSTF_PURE(s,hash[bin], n) ; 
		TXSTF_PURE(n,l, NULL) ; 
        TXSTF_PURE(n,r, cur) ; 
        TXSTF_PURE(cur,l, n) ;
		TXSTF_PURE(n,k, k) ; 
        TXSTF_PURE(n,v, v) ; 
        return NULL;
      }
    }
  }
}

static void _HASH_delete (Thread * Self, set_t * s, int bin, node_t * p) { 
  node_t * prev = LDNODE(p,l);
  node_t * next = LDNODE(p,r);
  if (prev != NULL)
  {
	TXSTF(prev, r, next); 
  } else
  {
	TXSTF(s, hash[bin], next); 
  }
  if (next != NULL)
  {
	TXSTF(next, l, prev); 
  } 
}

int HASH_contains (Thread * Self, KVMap * dict, int Key) {
  Self->InFunc = "contains" ;
  TXSTART() ;
  node_t * n = _HASH_lookup(Self, dict, Key) ;
  TXEND ;
  return n != NULL ;
}

int HASH_contains_raw (Thread * Self, KVMap * dict, int Key) {
  Self->InFunc = "contains" ;

  node_t * n = _HASH_lookup_pure(Self, dict, Key) ;

  return n != NULL ;
}

int HASH_add (Thread * Self, KVMap * dict, int Key, int Val)  {
  Self->InFunc = "put" ;
  node_t * nn = GetNode(Self) ;
  TXSTART();
  node_t * ex = _HASH_insert (Self, dict, Key, Val, nn) ;
  if (ex != NULL) {
    TXSTF(ex,v,Val) ;
    TXEND ;
    ReleaseNode (Self, nn);
    return 0  ;
  } else {
    TXEND ;
	return 1;
  }
}

int HASH_add_raw (Thread * Self, KVMap * dict, int Key, int Val)  {
  Self->InFunc = "put" ;
  node_t * nn = GetNode(Self) ;

  node_t * ex = _HASH_insert_pure (Self, dict, Key, Val, nn) ;
  if (ex != NULL) {
    TXSTF_PURE(ex,v,Val) ;
    ReleaseNode (Self, nn);
    return 0  ;
  } else {
	return 1;
  }
}
int HASH_del (Thread * Self, KVMap *dict, int Key) {
  Self->InFunc = "delete:lookup";     
  TXSTART() ; 
  node_t * node = _HASH_lookup(Self, dict, Key);
  if (node != NULL) {
    Self->InFunc = "delete:unlink";
    _HASH_delete(Self, dict, _HASH_Hash(Self, Key), node);
  }
  TXEND ;

  if (node != NULL) ReleaseNode(Self, node) ;
  return (node != NULL) ;
}***/




