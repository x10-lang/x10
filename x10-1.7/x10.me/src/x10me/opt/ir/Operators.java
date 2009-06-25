/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file was derived from code developed by the
 *  Jikes RVM project (http://jikesrvm.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  See the COPYRIGHT.txt file distributed with this work for information
 *  regarding copyright ownership.
 */

package x10me.opt.ir;

/**
 * Interface containing one name for each Instruction Class.
 * The can be used in case statements that switch on the 
 * result of the Instruction.getOpcode() method.
 *
 * @see Operator
 */
public interface Operators {

  public final static char Addr2int = 0;
  public final static char Addr2long = Addr2int + 1;
  public final static char Arraylength = Addr2long + 1;
  public final static char Athrow = Arraylength + 1;
  public final static char AttemptAddr = Athrow + 1;
  public final static char AttemptInt = AttemptAddr + 1;
  public final static char AttemptLong = AttemptInt + 1;
  public final static char BBend = AttemptLong + 1;
  public final static char BooleanCmpAddr = BBend + 1;
  public final static char BooleanCmpDouble = BooleanCmpAddr + 1;
  public final static char BooleanCmpFloat = BooleanCmpDouble + 1;
  public final static char BooleanCmpInt = BooleanCmpFloat + 1;
  public final static char BooleanCmpLong = BooleanCmpInt + 1;
  public final static char BooleanNot = BooleanCmpLong + 1;
  public final static char BoundsCheck = BooleanNot + 1;
  public final static char ByteAload = BoundsCheck + 1;
  public final static char ByteAstore = ByteAload + 1;
  public final static char ByteLoad = ByteAstore + 1;
  public final static char ByteStore = ByteLoad + 1;
  public final static char Call = ByteStore + 1;
  public final static char Checkcast = Call + 1;
  public final static char CheckcastNotnull = Checkcast + 1;
  public final static char CheckcastUnresolved = CheckcastNotnull + 1;
  public final static char Double2float = CheckcastUnresolved + 1;
  public final static char Double2int = Double2float + 1;
  public final static char Double2long = Double2int + 1;
  public final static char DoubleAdd = Double2long + 1;
  public final static char DoubleAload = DoubleAdd + 1;
  public final static char DoubleAsLongBits = DoubleAload + 1;
  public final static char DoubleAstore = DoubleAsLongBits + 1;
  public final static char DoubleCmpg = DoubleAstore + 1;
  public final static char DoubleCmpl = DoubleCmpg + 1;
  public final static char DoubleCondMove = DoubleCmpl + 1;
  public final static char DoubleDiv = DoubleCondMove + 1;
  public final static char DoubleIfcmp = DoubleDiv + 1;
  public final static char DoubleLoad = DoubleIfcmp + 1;
  public final static char DoubleMove = DoubleLoad + 1;
  public final static char DoubleMul = DoubleMove + 1;
  public final static char DoubleNeg = DoubleMul + 1;
  public final static char DoubleRem = DoubleNeg + 1;
  public final static char DoubleSqrt = DoubleRem + 1;
  public final static char DoubleStore = DoubleSqrt + 1;
  public final static char DoubleSub = DoubleStore + 1;
  public final static char Float2double = DoubleSub + 1;
  public final static char Float2int = Float2double + 1;
  public final static char Float2long = Float2int + 1;
  public final static char FloatAdd = Float2long + 1;
  public final static char FloatAload = FloatAdd + 1;
  public final static char FloatAsIntBits = FloatAload + 1;
  public final static char FloatAstore = FloatAsIntBits + 1;
  public final static char FloatCmpg = FloatAstore + 1;
  public final static char FloatCmpl = FloatCmpg + 1;
  public final static char FloatCondMove = FloatCmpl + 1;
  public final static char FloatDiv = FloatCondMove + 1;
  public final static char FloatIfcmp = FloatDiv + 1;
  public final static char FloatLoad = FloatIfcmp + 1;
  public final static char FloatMove = FloatLoad + 1;
  public final static char FloatMul = FloatMove + 1;
  public final static char FloatNeg = FloatMul + 1;
  public final static char FloatRem = FloatNeg + 1;
  public final static char FloatSqrt = FloatRem + 1;
  public final static char FloatStore = FloatSqrt + 1;
  public final static char FloatSub = FloatStore + 1;
  public final static char GetArrayElementTibFromTib = FloatSub + 1;
  public final static char GetCaughtException = GetArrayElementTibFromTib + 1;
  public final static char GetClassTib = GetCaughtException + 1;
  public final static char GetDoesImplementFromTib = GetClassTib + 1;
  public final static char GetField = GetDoesImplementFromTib + 1;
  public final static char GetObjTib = GetField + 1;
  public final static char GetStatic = GetObjTib + 1;
  public final static char GetSuperclassIdsFromTib = GetStatic + 1;
  public final static char GetTimeBase = GetSuperclassIdsFromTib + 1;
  public final static char GetTypeFromTib = GetTimeBase + 1;
  public final static char Goto = GetTypeFromTib + 1;
  public final static char GuardCombine = Goto + 1;
  public final static char GuardCondMove = GuardCombine + 1;
  public final static char GuardMove = GuardCondMove + 1;
  public final static char IgClassTest = GuardMove + 1;
  public final static char IgMethodTest = IgClassTest + 1;
  public final static char IgPatchPoint = IgMethodTest + 1;
  public final static char InstanceOf = IgPatchPoint + 1;
  public final static char InstanceOfNotnull = InstanceOf + 1;
  public final static char InstanceOfUnresolved = InstanceOfNotnull + 1;
  public final static char InstrumentedEventCounter = InstanceOfUnresolved + 1;
  public final static char Int2addrsigext = InstrumentedEventCounter + 1;
  public final static char Int2addrzerext = Int2addrsigext + 1;
  public final static char Int2byte = Int2addrzerext + 1;
  public final static char Int2double = Int2byte + 1;
  public final static char Int2float = Int2double + 1;
  public final static char Int2long = Int2float + 1;
  public final static char Int2short = Int2long + 1;
  public final static char Int2ushort = Int2short + 1;
  public final static char IntAdd = Int2ushort + 1;
  public final static char IntAload = IntAdd + 1;
  public final static char IntAnd = IntAload + 1;
  public final static char IntAstore = IntAnd + 1;
  public final static char IntBitsAsFloat = IntAstore + 1;
  public final static char IntCondMove = IntBitsAsFloat + 1;
  public final static char IntDiv = IntCondMove + 1;
  public final static char IntIfcmp2 = IntDiv + 1;
  public final static char IntIfcmp = IntIfcmp2 + 1;
  public final static char IntLoad = IntIfcmp + 1;
  public final static char IntMove = IntLoad + 1;
  public final static char IntMul = IntMove + 1;
  public final static char IntNeg = IntMul + 1;
  public final static char IntNot = IntNeg + 1;
  public final static char IntOr = IntNot + 1;
  public final static char IntRem = IntOr + 1;
  public final static char IntShl = IntRem + 1;
  public final static char IntShr = IntShl + 1;
  public final static char IntStore = IntShr + 1;
  public final static char IntSub = IntStore + 1;
  public final static char IntUshr = IntSub + 1;
  public final static char IntXor = IntUshr + 1;
  public final static char IntZeroCheck = IntXor + 1;
  public final static char IrPrologue = IntZeroCheck + 1;
  public final static char Label = IrPrologue + 1;
  public final static char Long2addr = Label + 1;
  public final static char Long2double = Long2addr + 1;
  public final static char Long2float = Long2double + 1;
  public final static char Long2int = Long2float + 1;
  public final static char LongAdd = Long2int + 1;
  public final static char LongAload = LongAdd + 1;
  public final static char LongAnd = LongAload + 1;
  public final static char LongAstore = LongAnd + 1;
  public final static char LongBitsAsDouble = LongAstore + 1;
  public final static char LongCmp = LongBitsAsDouble + 1;
  public final static char LongCondMove = LongCmp + 1;
  public final static char LongDiv = LongCondMove + 1;
  public final static char LongIfcmp = LongDiv + 1;
  public final static char LongLoad = LongIfcmp + 1;
  public final static char LongMove = LongLoad + 1;
  public final static char LongMul = LongMove + 1;
  public final static char LongNeg = LongMul + 1;
  public final static char LongNot = LongNeg + 1;
  public final static char LongOr = LongNot + 1;
  public final static char LongRem = LongOr + 1;
  public final static char LongShl = LongRem + 1;
  public final static char LongShr = LongShl + 1;
  public final static char LongStore = LongShr + 1;
  public final static char LongSub = LongStore + 1;
  public final static char LongUshr = LongSub + 1;
  public final static char LongXor = LongUshr + 1;
  public final static char LongZeroCheck = LongXor + 1;
  public final static char LookupSwitch = LongZeroCheck + 1;
  public final static char LowTableSwitch = LookupSwitch + 1;
  public final static char MonitorEnter = LowTableSwitch + 1;
  public final static char MonitorExit = MonitorEnter + 1;
  public final static char MustImplementInterface = MonitorExit + 1;
  public final static char New = MustImplementInterface + 1;
  public final static char Newarray = New + 1;
  public final static char NewarrayUnresolved = Newarray + 1;
  public final static char NewObjMultiArray = NewarrayUnresolved + 1;
  public final static char NewUnresolved = NewObjMultiArray + 1;
  public final static char Nop = NewUnresolved + 1;
  public final static char NullCheck = Nop + 1;
  public final static char ObjArrayStoreCheck = NullCheck + 1;
  public final static char ObjArrayStoreCheckNotnull = ObjArrayStoreCheck + 1;
  public final static char Phi = ObjArrayStoreCheckNotnull + 1;
  public final static char Pi = Phi + 1;
  public final static char PrepareAddr = Pi + 1;
  public final static char PrepareInt = PrepareAddr + 1;
  public final static char PrepareLong = PrepareInt + 1;
  public final static char PutField = PrepareLong + 1;
  public final static char PutStatic = PutField + 1;
  public final static char ReadCeiling = PutStatic + 1;
  public final static char RefAdd = ReadCeiling + 1;
  public final static char RefAload = RefAdd + 1;
  public final static char RefAnd = RefAload + 1;
  public final static char RefAstore = RefAnd + 1;
  public final static char RefCondMove = RefAstore + 1;
  public final static char RefIfcmp = RefCondMove + 1;
  public final static char RefLoad = RefIfcmp + 1;
  public final static char RefMove = RefLoad + 1;
  public final static char RefNeg = RefMove + 1;
  public final static char RefNot = RefNeg + 1;
  public final static char RefOr = RefNot + 1;
  public final static char RefShl = RefOr + 1;
  public final static char RefShr = RefShl + 1;
  public final static char RefStore = RefShr + 1;
  public final static char RefSub = RefStore + 1;
  public final static char RefUshr = RefSub + 1;
  public final static char RefXor = RefUshr + 1;
  public final static char Resolve = RefXor + 1;
  public final static char ResolveMember = Resolve + 1;
  public final static char Return = ResolveMember + 1;
  public final static char SetCaughtException = Return + 1;
  public final static char ShortAload = SetCaughtException + 1;
  public final static char ShortAstore = ShortAload + 1;
  public final static char ShortLoad = ShortAstore + 1;
  public final static char ShortStore = ShortLoad + 1;
  public final static char Split = ShortStore + 1;
  public final static char Syscall = Split + 1;
  public final static char TableSwitch = Syscall + 1;
  public final static char Trap = TableSwitch + 1;
  public final static char UbyteAload = Trap + 1;
  public final static char UbyteLoad = UbyteAload + 1;
  public final static char UnintBegin = UbyteLoad + 1;
  public final static char UnintEnd = UnintBegin + 1;
  public final static char UshortAload = UnintEnd + 1;
  public final static char UshortLoad = UshortAload + 1;
  public final static char WriteFloor = UshortLoad + 1;
  public final static char YieldpointBackedge = WriteFloor + 1;
  public final static char YieldpointEpilogue = YieldpointBackedge + 1;
  public final static char YieldpointPrologue = YieldpointEpilogue + 1;
}


