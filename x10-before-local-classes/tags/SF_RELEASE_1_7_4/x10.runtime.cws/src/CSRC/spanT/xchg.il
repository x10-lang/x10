#pragma mc_func Xchg { \
"7ca01828"             /*   lwarx 0,0,1 */\
"7c80192d"              /*   stwcx. 2,0,1*/\
"40820008"              /*   bne- here   */\
"38a5fffd"		/*   addi 3, 1, 0*/\
"38a50003"		\
"38650003"		\
"38650000"		\
"4c00012c"             /*    isync */\
}
#pragma reg_killed_by Xchg  gr0,gr3,gr4,gr5,cr0
