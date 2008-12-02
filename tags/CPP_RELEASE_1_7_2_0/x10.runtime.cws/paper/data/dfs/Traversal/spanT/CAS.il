.inline CASW
 cas [%o0], %o1, %o2
 mov %o2, %o0
 membar #LoadLoad | #LoadStore 
.end

.inline spin_lock
1:
mov 0,%o2
cas [%o0],%o2,%o1 
tst %o1 
be 3f 
nop 
2:
ld [%o0], %o2
tst %o2
bne 2b
nop
ba,a 1b
3:
membar #LoadLoad | #LoadStore 
.end

.inline spin_unlock
membar #StoreStore 
membar #LoadStore !RMO only 
mov 0, %o1
st %o1,[%o0]
.end
