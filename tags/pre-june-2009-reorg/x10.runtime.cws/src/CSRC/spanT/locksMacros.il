#pragma mc_func backoffLock { \
"38c00fa0" /* addi 6,0,4000 ## load constant 4000 into r6 (initialize counter)*/\
"38a0ffff" /* addi 5,0,-1 ## load constant -1 into r5*/\
"7c801828" /* TRY: lwarx 4,0,3 ## loadReserve mem[addr=r3] into r4*/\
"7c042800" /* cmp 0,0,4,5 ## compare r4 with r5 and set CR0-Bit2 if equal */\
"41a20010" /* bc 13,2,FAIL ## branch to FAIL if CR0-Bit2 is set*/\
"7ca0192d" /* stwcx. 5,0,3 ## store r5 into mem[addr=r3];set CR0-Bit2 if success
*/\
"4c00012c" /* isync */\
"41a2001c" /* bc 13,2,BYE ## branch to BYE if CR0-Bit2 is set*/\
"4c00012c" /* isync ## instruction sync */\
"38c6ffff" /* FAIL: addi 6,6,-1 ## FAIL: decrement r6 by 1*/\
"2c060000" /* cmpi 0,0,6,0 ## compare r6 with constant 0 and set CR0-Bit2 if equ
al */\
"4082ffdc" /* bc 4,2,TRY ## branch to TRY if CR0-Bit2 is set*/\
"4c00012c" /* isync */\
"38850000" /* addi 4,5,0 ## move r5 (invalidContents) into r4*/\
"38640000" /* BYE: addi 3,4,0 ## SUCCESS: copy r4 into r3 to return value read f
rom mem*/\
}
#pragma reg_killed_by backoffLock  gr0,gr3,gr4,gr5,gr6,cr0

#pragma mc_func spin_lock{\
 "7cc01828" /*TRY: lwarx 6, 0, 3 */\
 "2c060000" /*cmpwi 6,0*/\
 "4082fff8" /*bc 4,2,TRY*/\
 "4c00012c" /*isync*/\
 "7c80192d" /* stwcx. 4, 0, 3*/\
 "4082ffec" /*bc 4,2, TRY*/\
  "4c00012c" /* isync ## instruction sync */\
 "7c0004ac" /*sync*/\
}
#pragma reg_killed_by spin_lock gr0, gr3, gr4, gr6

#pragma mc_func spin_unlock{\
"7c0004ac" /*sync*/ \
"38800000" /*addi 4, 0, 0*/\
"90830000" /*stw 4,0(3)*/\
}
#pragma reg_killed_by spin_unlock gr0,gr3, gr4

#pragma mc_func spin_lock_i{\
 "38a00000" /*addi 5,0,0*/\
 "38a50001" /*TRY: addi 5,5,1*/\
 "7cc01828" /*lwarx 6, 0, 3*/\
 "2c060000" /*cmpwi 6,0*/\
 "4082fff4" /*bc 4,2,TRY*/\
 "4c00012c" /*isync*/\
 "7c80192d" /* stwcx. 4, 0, 3*/\
 "4082ffe8" /*bc 4,2, TRY*/\
 "38650000" /*addi 3, 5, 0*/\
 "7c0004ac" /*sync*/\
}
#pragma reg_killed_by spin_lock_i gr0, gr3, gr4, gr5, gr6
