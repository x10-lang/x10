package x10.runtime.bytecode;

import static x10.runtime.bytecode.ByteArrayUtil.*;

public class BytecodeUtil implements BytecodeConstants {
	public static int getBytecodeLength(byte[] code, int o, int start) {
		int c = code[o] & 0xFF;
		int x = BCLENGTH[c];
		if (x < 0) { // Variable-length bytecode
			int pad = 3 - (o - start)%4;
			switch (c) {
			case BC_tableswitch:
			{
				int lo = getInt(code, o+pad+4);
				int hi = getInt(code, o+pad+8);
				x = pad + 12 + (hi-lo+1);
				break;
			}
			case BC_lookupswitch:
			{
				int n = getInt(code, o+pad+4);
				x = pad + 8 + n*8;
				break;
			}
			case BC_wide:
			{
				int c1 = code[o+1] & 0xFF;
				x = (c1 == BC_iinc) ? 6 : 4;
				break;
			}
			}
		}
		return x;
	}
}
