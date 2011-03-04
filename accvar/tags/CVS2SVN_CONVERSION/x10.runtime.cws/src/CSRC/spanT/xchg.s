lwarx 5,0,3
stwcx. 4,0,3
bne- fail_xchg 
addi 5, 5, -3
fail_xchg:
addi 5, 5, 3 
addi 3, 5, 0
isync

