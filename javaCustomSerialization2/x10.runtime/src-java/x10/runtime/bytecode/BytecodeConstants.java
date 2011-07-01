/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.runtime.bytecode;

public interface BytecodeConstants {
	public static final int BC_nop = 0;
	public static final int BC_aconst_null = 1;
	public static final int BC_iconst_m1 = 2;
	public static final int BC_iconst_0 = 3;
	public static final int BC_iconst_1 = 4;
	public static final int BC_iconst_2 = 5;
	public static final int BC_iconst_3 = 6;
	public static final int BC_iconst_4 = 7;
	public static final int BC_iconst_5 = 8;
	public static final int BC_lconst_0 = 9;
	public static final int BC_lconst_1 = 10;
	public static final int BC_fconst_0 = 11;
	public static final int BC_fconst_1 = 12;
	public static final int BC_fconst_2 = 13;
	public static final int BC_dconst_0 = 14;
	public static final int BC_dconst_1 = 15;
	public static final int BC_bipush = 16;
	public static final int BC_sipush = 17;
	public static final int BC_ldc = 18;
	public static final int BC_ldc_w = 19;
	public static final int BC_ldc2_w = 20;
	public static final int BC_iload = 21;
	public static final int BC_lload = 22;
	public static final int BC_fload = 23;
	public static final int BC_dload = 24;
	public static final int BC_aload = 25;
	public static final int BC_iload_0 = 26;
	public static final int BC_iload_1 = 27;
	public static final int BC_iload_2 = 28;
	public static final int BC_iload_3 = 29;
	public static final int BC_lload_0 = 30;
	public static final int BC_lload_1 = 31;
	public static final int BC_lload_2 = 32;
	public static final int BC_lload_3 = 33;
	public static final int BC_fload_0 = 34;
	public static final int BC_fload_1 = 35;
	public static final int BC_fload_2 = 36;
	public static final int BC_fload_3 = 37;
	public static final int BC_dload_0 = 38;
	public static final int BC_dload_1 = 39;
	public static final int BC_dload_2 = 40;
	public static final int BC_dload_3 = 41;
	public static final int BC_aload_0 = 42;
	public static final int BC_aload_1 = 43;
	public static final int BC_aload_2 = 44;
	public static final int BC_aload_3 = 45;
	public static final int BC_iaload = 46;
	public static final int BC_laload = 47;
	public static final int BC_faload = 48;
	public static final int BC_daload = 49;
	public static final int BC_aaload = 50;
	public static final int BC_baload = 51;
	public static final int BC_caload = 52;
	public static final int BC_saload = 53;
	public static final int BC_istore = 54;
	public static final int BC_lstore = 55;
	public static final int BC_fstore = 56;
	public static final int BC_dstore = 57;
	public static final int BC_astore = 58;
	public static final int BC_istore_0 = 59;
	public static final int BC_istore_1 = 60;
	public static final int BC_istore_2 = 61;
	public static final int BC_istore_3 = 62;
	public static final int BC_lstore_0 = 63;
	public static final int BC_lstore_1 = 64;
	public static final int BC_lstore_2 = 65;
	public static final int BC_lstore_3 = 66;
	public static final int BC_fstore_0 = 67;
	public static final int BC_fstore_1 = 68;
	public static final int BC_fstore_2 = 69;
	public static final int BC_fstore_3 = 70;
	public static final int BC_dstore_0 = 71;
	public static final int BC_dstore_1 = 72;
	public static final int BC_dstore_2 = 73;
	public static final int BC_dstore_3 = 74;
	public static final int BC_astore_0 = 75;
	public static final int BC_astore_1 = 76;
	public static final int BC_astore_2 = 77;
	public static final int BC_astore_3 = 78;
	public static final int BC_iastore = 79;
	public static final int BC_lastore = 80;
	public static final int BC_fastore = 81;
	public static final int BC_dastore = 82;
	public static final int BC_aastore = 83;
	public static final int BC_bastore = 84;
	public static final int BC_castore = 85;
	public static final int BC_sastore = 86;
	public static final int BC_pop = 87;
	public static final int BC_pop2 = 88;
	public static final int BC_dup = 89;
	public static final int BC_dup_x1 = 90;
	public static final int BC_dup_x2 = 91;
	public static final int BC_dup2 = 92;
	public static final int BC_dup2_x1 = 93;
	public static final int BC_dup2_x2 = 94;
	public static final int BC_swap = 95;
	public static final int BC_iadd = 96;
	public static final int BC_ladd = 97;
	public static final int BC_fadd = 98;
	public static final int BC_dadd = 99;
	public static final int BC_isub = 100;
	public static final int BC_lsub = 101;
	public static final int BC_fsub = 102;
	public static final int BC_dsub = 103;
	public static final int BC_imul = 104;
	public static final int BC_lmul = 105;
	public static final int BC_fmul = 106;
	public static final int BC_dmul = 107;
	public static final int BC_idiv = 108;
	public static final int BC_ldiv = 109;
	public static final int BC_fdiv = 110;
	public static final int BC_ddiv = 111;
	public static final int BC_irem = 112;
	public static final int BC_lrem = 113;
	public static final int BC_frem = 114;
	public static final int BC_drem = 115;
	public static final int BC_ineg = 116;
	public static final int BC_lneg = 117;
	public static final int BC_fneg = 118;
	public static final int BC_dneg = 119;
	public static final int BC_ishl = 120;
	public static final int BC_lshl = 121;
	public static final int BC_ishr = 122;
	public static final int BC_lshr = 123;
	public static final int BC_iushr = 124;
	public static final int BC_lushr = 125;
	public static final int BC_iand = 126;
	public static final int BC_land = 127;
	public static final int BC_ior = 128;
	public static final int BC_lor = 129;
	public static final int BC_ixor = 130;
	public static final int BC_lxor = 131;
	public static final int BC_iinc = 132;
	public static final int BC_i2l = 133;
	public static final int BC_i2f = 134;
	public static final int BC_i2d = 135;
	public static final int BC_l2i = 136;
	public static final int BC_l2f = 137;
	public static final int BC_l2d = 138;
	public static final int BC_f2i = 139;
	public static final int BC_f2l = 140;
	public static final int BC_f2d = 141;
	public static final int BC_d2i = 142;
	public static final int BC_d2l = 143;
	public static final int BC_d2f = 144;
	public static final int BC_i2b = 145;
	public static final int BC_i2c = 146;
	public static final int BC_i2s = 147;
	public static final int BC_lcmp = 148;
	public static final int BC_fcmpl = 149;
	public static final int BC_fcmpg = 150;
	public static final int BC_dcmpl = 151;
	public static final int BC_dcmpg = 152;
	public static final int BC_ifeq = 153;
	public static final int BC_ifne = 154;
	public static final int BC_iflt = 155;
	public static final int BC_ifge = 156;
	public static final int BC_ifgt = 157;
	public static final int BC_ifle = 158;
	public static final int BC_if_icmpeq = 159;
	public static final int BC_if_icmpne = 160;
	public static final int BC_if_icmplt = 161;
	public static final int BC_if_icmpge = 162;
	public static final int BC_if_icmpgt = 163;
	public static final int BC_if_icmple = 164;
	public static final int BC_if_acmpeq = 165;
	public static final int BC_if_acmpne = 166;
	public static final int BC_goto = 167;
	public static final int BC_jsr = 168;
	public static final int BC_ret = 169;
	public static final int BC_tableswitch = 170;
	public static final int BC_lookupswitch = 171;
	public static final int BC_ireturn = 172;
	public static final int BC_lreturn = 173;
	public static final int BC_freturn = 174;
	public static final int BC_dreturn = 175;
	public static final int BC_areturn = 176;
	public static final int BC_return = 177;
	public static final int BC_getstatic = 178;
	public static final int BC_putstatic = 179;
	public static final int BC_getfield = 180;
	public static final int BC_putfield = 181;
	public static final int BC_invokevirtual = 182;
	public static final int BC_invokespecial = 183;
	public static final int BC_invokestatic = 184;
	public static final int BC_invokeinterface = 185;
	public static final int BC_xxxunusedxxx1 = 186;
	public static final int BC_new = 187;
	public static final int BC_newarray = 188;
	public static final int BC_anewarray = 189;
	public static final int BC_arraylength = 190;
	public static final int BC_athrow = 191;
	public static final int BC_checkcast = 192;
	public static final int BC_instanceof = 193;
	public static final int BC_monitorenter = 194;
	public static final int BC_monitorexit = 195;
	public static final int BC_wide = 196;
	public static final int BC_multianewarray = 197;
	public static final int BC_ifnull = 198;
	public static final int BC_ifnonnull = 199;
	public static final int BC_goto_w = 200;
	public static final int BC_jsr_w = 201;
	public static final int[] BCLENGTH = {
		1, //   0 (0x00) nop
		1, //   1 (0x01) aconst_null
		1, //   2 (0x02) iconst_m1
		1, //   3 (0x03) iconst_0
		1, //   4 (0x04) iconst_1
		1, //   5 (0x05) iconst_2
		1, //   6 (0x06) iconst_3
		1, //   7 (0x07) iconst_4
		1, //   8 (0x08) iconst_5
		1, //   9 (0x09) lconst_0
		1, //  10 (0x0a) lconst_1
		1, //  11 (0x0b) fconst_0
		1, //  12 (0x0c) fconst_1
		1, //  13 (0x0d) fconst_2
		1, //  14 (0x0e) dconst_0
		1, //  15 (0x0f) dconst_1
		2, //  16 (0x10) bipush
		3, //  17 (0x11) sipush
		2, //  18 (0x12) ldc
		3, //  19 (0x13) ldc_w
		3, //  20 (0x14) ldc2_w
		2, //  21 (0x15) iload
		2, //  22 (0x16) lload
		2, //  23 (0x17) fload
		2, //  24 (0x18) dload
		2, //  25 (0x19) aload
		1, //  26 (0x1a) iload_0
		1, //  27 (0x1b) iload_1
		1, //  28 (0x1c) iload_2
		1, //  29 (0x1d) iload_3
		1, //  30 (0x1e) lload_0
		1, //  31 (0x1f) lload_1
		1, //  32 (0x20) lload_2
		1, //  33 (0x21) lload_3
		1, //  34 (0x22) fload_0
		1, //  35 (0x23) fload_1
		1, //  36 (0x24) fload_2
		1, //  37 (0x25) fload_3
		1, //  38 (0x26) dload_0
		1, //  39 (0x27) dload_1
		1, //  40 (0x28) dload_2
		1, //  41 (0x29) dload_3
		1, //  42 (0x2a) aload_0
		1, //  43 (0x2b) aload_1
		1, //  44 (0x2c) aload_2
		1, //  45 (0x2d) aload_3
		1, //  46 (0x2e) iaload
		1, //  47 (0x2f) laload
		1, //  48 (0x30) faload
		1, //  49 (0x31) daload
		1, //  50 (0x32) aaload
		1, //  51 (0x33) baload
		1, //  52 (0x34) caload
		1, //  53 (0x35) saload
		2, //  54 (0x36) istore
		2, //  55 (0x37) lstore
		2, //  56 (0x38) fstore
		2, //  57 (0x39) dstore
		2, //  58 (0x3a) astore
		1, //  59 (0x3b) istore_0
		1, //  60 (0x3c) istore_1
		1, //  61 (0x3d) istore_2
		1, //  62 (0x3e) istore_3
		1, //  63 (0x3f) lstore_0
		1, //  64 (0x40) lstore_1
		1, //  65 (0x41) lstore_2
		1, //  66 (0x42) lstore_3
		1, //  67 (0x43) fstore_0
		1, //  68 (0x44) fstore_1
		1, //  69 (0x45) fstore_2
		1, //  70 (0x46) fstore_3
		1, //  71 (0x47) dstore_0
		1, //  72 (0x48) dstore_1
		1, //  73 (0x49) dstore_2
		1, //  74 (0x4a) dstore_3
		1, //  75 (0x4b) astore_0
		1, //  76 (0x4c) astore_1
		1, //  77 (0x4d) astore_2
		1, //  78 (0x4e) astore_3
		1, //  79 (0x4f) iastore
		1, //  80 (0x50) lastore
		1, //  81 (0x51) fastore
		1, //  82 (0x52) dastore
		1, //  83 (0x53) aastore
		1, //  84 (0x54) bastore
		1, //  85 (0x55) castore
		1, //  86 (0x56) sastore
		1, //  87 (0x57) pop
		1, //  88 (0x58) pop2
		1, //  89 (0x59) dup
		1, //  90 (0x5a) dup_x1
		1, //  91 (0x5b) dup_x2
		1, //  92 (0x5c) dup2
		1, //  93 (0x5d) dup2_x1
		1, //  94 (0x5e) dup2_x2
		1, //  95 (0x5f) swap
		1, //  96 (0x60) iadd
		1, //  97 (0x61) ladd
		1, //  98 (0x62) fadd
		1, //  99 (0x63) dadd
		1, // 100 (0x64) isub
		1, // 101 (0x65) lsub
		1, // 102 (0x66) fsub
		1, // 103 (0x67) dsub
		1, // 104 (0x68) imul
		1, // 105 (0x69) lmul
		1, // 106 (0x6a) fmul
		1, // 107 (0x6b) dmul
		1, // 108 (0x6c) idiv
		1, // 109 (0x6d) ldiv
		1, // 110 (0x6e) fdiv
		1, // 111 (0x6f) ddiv
		1, // 112 (0x70) irem
		1, // 113 (0x71) lrem
		1, // 114 (0x72) frem
		1, // 115 (0x73) drem
		1, // 116 (0x74) ineg
		1, // 117 (0x75) lneg
		1, // 118 (0x76) fneg
		1, // 119 (0x77) dneg
		1, // 120 (0x78) ishl
		1, // 121 (0x79) lshl
		1, // 122 (0x7a) ishr
		1, // 123 (0x7b) lshr
		1, // 124 (0x7c) iushr
		1, // 125 (0x7d) lushr
		1, // 126 (0x7e) iand
		1, // 127 (0x7f) land
		1, // 128 (0x80) ior
		1, // 129 (0x81) lor
		1, // 130 (0x82) ixor
		1, // 131 (0x83) lxor
		3, // 132 (0x84) iinc
		1, // 133 (0x85) i2l
		1, // 134 (0x86) i2f
		1, // 135 (0x87) i2d
		1, // 136 (0x88) l2i
		1, // 137 (0x89) l2f
		1, // 138 (0x8a) l2d
		1, // 139 (0x8b) f2i
		1, // 140 (0x8c) f2l
		1, // 141 (0x8d) f2d
		1, // 142 (0x8e) d2i
		1, // 143 (0x8f) d2l
		1, // 144 (0x90) d2f
		1, // 145 (0x91) i2b
		1, // 146 (0x92) i2c
		1, // 147 (0x93) i2s
		1, // 148 (0x94) lcmp
		1, // 149 (0x95) fcmpl
		1, // 150 (0x96) fcmpg
		1, // 151 (0x97) dcmpl
		1, // 152 (0x98) dcmpg
		3, // 153 (0x99) ifeq
		3, // 154 (0x9a) ifne
		3, // 155 (0x9b) iflt
		3, // 156 (0x9c) ifge
		3, // 157 (0x9d) ifgt
		3, // 158 (0x9e) ifle
		3, // 159 (0x9f) if_icmpeq
		3, // 160 (0xa0) if_icmpne
		3, // 161 (0xa1) if_icmplt
		3, // 162 (0xa2) if_icmpge
		3, // 163 (0xa3) if_icmpgt
		3, // 164 (0xa4) if_icmple
		3, // 165 (0xa5) if_acmpeq
		3, // 166 (0xa6) if_acmpne
		3, // 167 (0xa7) goto
		3, // 168 (0xa8) jsr
		2, // 169 (0xa9) ret
		-1, // 170 (0xaa) tableswitch
		-1, // 171 (0xab) lookupswitch
		1, // 172 (0xac) ireturn
		1, // 173 (0xad) lreturn
		1, // 174 (0xae) freturn
		1, // 175 (0xaf) dreturn
		1, // 176 (0xb0) areturn
		1, // 177 (0xb1) return
		3, // 178 (0xb2) getstatic
		3, // 179 (0xb3) putstatic
		3, // 180 (0xb4) getfield
		3, // 181 (0xb5) putfield
		3, // 182 (0xb6) invokevirtual
		3, // 183 (0xb7) invokespecial
		3, // 184 (0xb8) invokestatic
		5, // 185 (0xb9) invokeinterface
		1, // 186 (0xba) xxxunusedxxx1
		3, // 187 (0xbb) new
		2, // 188 (0xbc) newarray
		3, // 189 (0xbd) anewarray
		1, // 190 (0xbe) arraylength
		1, // 191 (0xbf) athrow
		3, // 192 (0xc0) checkcast
		3, // 193 (0xc1) instanceof
		1, // 194 (0xc2) monitorenter
		1, // 195 (0xc3) monitorexit
		-1, // 196 (0xc4) wide
		4, // 197 (0xc5) multianewarray
		3, // 198 (0xc6) ifnull
		3, // 199 (0xc7) ifnonnull
		5, // 200 (0xc8) goto_w
		5, // 201 (0xc9) jsr_w
	};
}
