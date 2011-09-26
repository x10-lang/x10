package au.edu.anu.chem;

public class BondType
extends x10.core.Struct
  implements x10.util.concurrent.Atomic,
              x10.x10rt.X10JavaSerializable 
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, BondType.class);
    
    public static final x10.rtt.RuntimeType<BondType> $RTT = new x10.rtt.NamedType<BondType>(
    "au.edu.anu.chem.BondType", /* base class */BondType.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.STRUCT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(BondType $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        java.lang.String description = (java.lang.String) $deserializer.readRef();
        $_obj.description = description;
        $_obj.bondOrder = $deserializer.readDouble();
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        BondType $_obj = new BondType((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        $serializer.write(this.description);
        $serializer.write(this.bondOrder);
        
    }
    
    // zero value constructor
    public BondType(final java.lang.System $dummy) { this.description = null; this.bondOrder = 0.0; }
    // constructor just for allocation
    public BondType(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
public java.lang.String
          description;
        
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
public double
          bondOrder;
        
        
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
public int
          X10$object_lock_id0;
        
        
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
public x10.util.concurrent.OrderedLock
                                                                                                        getOrderedLock(
                                                                                                        ){
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final int t58923 =
              this.
                X10$object_lock_id0;
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final x10.util.concurrent.OrderedLock t58924 =
              x10.util.concurrent.OrderedLock.getLock((int)(t58923));
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
return t58924;
        }
        
        
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
public static int
          X10$class_lock_id1 =
          x10.util.concurrent.OrderedLock.createNewLockID();
        
        
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
public static x10.util.concurrent.OrderedLock
                                                                                                        getStaticOrderedLock(
                                                                                                        ){
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final int t58925 =
              au.edu.anu.chem.BondType.X10$class_lock_id1;
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final x10.util.concurrent.OrderedLock t58926 =
              x10.util.concurrent.OrderedLock.getLock((int)(t58925));
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
return t58926;
        }
        
        
//#line 17 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
// creation method for java code
        public static au.edu.anu.chem.BondType $make(final java.lang.String description,
                                                     final double bondOrder){return new au.edu.anu.chem.BondType((java.lang.System[]) null).$init(description,bondOrder);}
        
        // constructor for non-virtual call
        final public au.edu.anu.chem.BondType au$edu$anu$chem$BondType$$init$S(final java.lang.String description,
                                                                               final double bondOrder) { {
                                                                                                                
//#line 18 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
this.description = description;
                                                                                                                this.bondOrder = bondOrder;
                                                                                                                
                                                                                                                
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final au.edu.anu.chem.BondType this5891758972 =
                                                                                                                  this;
                                                                                                                
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
this5891758972.X10$object_lock_id0 = -1;
                                                                                                            }
                                                                                                            return this;
                                                                                                            }
        
        // constructor
        public au.edu.anu.chem.BondType $init(final java.lang.String description,
                                              final double bondOrder){return au$edu$anu$chem$BondType$$init$S(description,bondOrder);}
        
        
        
//#line 17 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
// creation method for java code
        public static au.edu.anu.chem.BondType $make(final java.lang.String description,
                                                     final double bondOrder,
                                                     final x10.util.concurrent.OrderedLock paramLock){return new au.edu.anu.chem.BondType((java.lang.System[]) null).$init(description,bondOrder,paramLock);}
        
        // constructor for non-virtual call
        final public au.edu.anu.chem.BondType au$edu$anu$chem$BondType$$init$S(final java.lang.String description,
                                                                               final double bondOrder,
                                                                               final x10.util.concurrent.OrderedLock paramLock) { {
                                                                                                                                         
//#line 18 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
this.description = description;
                                                                                                                                         this.bondOrder = bondOrder;
                                                                                                                                         
                                                                                                                                         
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final au.edu.anu.chem.BondType this5892058973 =
                                                                                                                                           this;
                                                                                                                                         
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
this5892058973.X10$object_lock_id0 = -1;
                                                                                                                                         
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final int t58927 =
                                                                                                                                           paramLock.getIndex();
                                                                                                                                         
//#line 17 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
this.X10$object_lock_id0 = ((int)(t58927));
                                                                                                                                     }
                                                                                                                                     return this;
                                                                                                                                     }
        
        // constructor
        public au.edu.anu.chem.BondType $init(final java.lang.String description,
                                              final double bondOrder,
                                              final x10.util.concurrent.OrderedLock paramLock){return au$edu$anu$chem$BondType$$init$S(description,bondOrder,paramLock);}
        
        
        
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
public static au.edu.anu.chem.BondType
          WEAK_BOND;
        
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
public static au.edu.anu.chem.BondType
          NO_BOND;
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
public static au.edu.anu.chem.BondType
          SINGLE_BOND;
        
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
public static au.edu.anu.chem.BondType
          DOUBLE_BOND;
        
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
public static au.edu.anu.chem.BondType
          TRIPLE_BOND;
        
//#line 26 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
public static au.edu.anu.chem.BondType
          QUADRUPLE_BOND;
        
//#line 27 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
public static au.edu.anu.chem.BondType
          AROMATIC_BOND;
        
//#line 28 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
public static au.edu.anu.chem.BondType
          AMIDE_BOND;
        
//#line 29 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
public static au.edu.anu.chem.BondType
          IONIC_BOND;
        
        
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final public boolean
                                                                                                        isStrongBond$O(
                                                                                                        ){
            
//#line 35 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final au.edu.anu.chem.BondType t58928 =
              ((au.edu.anu.chem.BondType)(au.edu.anu.chem.BondType.getInitialized$NO_BOND()));
            
//#line 35 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
boolean t58930 =
              (!x10.rtt.Equality.equalsequals((this),(t58928)));
            
//#line 35 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
if (t58930) {
                
//#line 35 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final au.edu.anu.chem.BondType t58929 =
                  ((au.edu.anu.chem.BondType)(au.edu.anu.chem.BondType.getInitialized$WEAK_BOND()));
                
//#line 35 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
t58930 = (!x10.rtt.Equality.equalsequals((this),(t58929)));
            }
            
//#line 35 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final boolean t58931 =
              t58930;
            
//#line 35 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
return t58931;
        }
        
        
//#line 38 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final public java.lang.String
                                                                                                        toString(
                                                                                                        ){
            
//#line 38 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final java.lang.String t58932 =
              ((java.lang.String)(description));
            
//#line 38 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
return t58932;
        }
        
        
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final public java.lang.String
                                                                                                        typeName$O(
                                                                                                        ){try {return x10.rtt.Types.typeName(this);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final public int
                                                                                                        hashCode(
                                                                                                        ){
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
int result =
              1;
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final int t58933 =
              result;
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final int t58935 =
              ((8191) * (((int)(t58933))));
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final java.lang.String t58934 =
              ((java.lang.String)(this.
                                    description));
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final int t58936 =
              (t58934).hashCode();
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final int t58937 =
              ((t58935) + (((int)(t58936))));
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
result = t58937;
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final int t58938 =
              result;
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final int t58940 =
              ((8191) * (((int)(t58938))));
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final double t58939 =
              this.
                bondOrder;
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final int t58941 =
              x10.rtt.Types.hashCode(t58939);
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final int t58942 =
              ((t58940) + (((int)(t58941))));
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
result = t58942;
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final int t58943 =
              result;
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
return t58943;
        }
        
        
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final public boolean
                                                                                                        equals(
                                                                                                        java.lang.Object other){
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final java.lang.Object t58944 =
              other;
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final boolean t58945 =
              au.edu.anu.chem.BondType.$RTT.instanceOf(t58944);
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final boolean t58946 =
              !(t58945);
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
if (t58946) {
                
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
return false;
            }
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final java.lang.Object t58947 =
              other;
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final au.edu.anu.chem.BondType t58948 =
              ((au.edu.anu.chem.BondType)x10.rtt.Types.asStruct(au.edu.anu.chem.BondType.$RTT,t58947));
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final boolean t58949 =
              this.equals(((au.edu.anu.chem.BondType)(t58948)));
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
return t58949;
        }
        
        
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final public boolean
                                                                                                        equals(
                                                                                                        au.edu.anu.chem.BondType other){
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final java.lang.String t58951 =
              ((java.lang.String)(this.
                                    description));
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final au.edu.anu.chem.BondType t58950 =
              other;
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final java.lang.String t58952 =
              ((java.lang.String)(t58950.
                                    description));
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
boolean t58956 =
              x10.rtt.Equality.equalsequals((t58951),(t58952));
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
if (t58956) {
                
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final double t58954 =
                  this.
                    bondOrder;
                
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final au.edu.anu.chem.BondType t58953 =
                  other;
                
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final double t58955 =
                  t58953.
                    bondOrder;
                
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
t58956 = ((double) t58954) ==
                ((double) t58955);
            }
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final boolean t58957 =
              t58956;
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
return t58957;
        }
        
        
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final public boolean
                                                                                                        _struct_equals$O(
                                                                                                        java.lang.Object other){
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final java.lang.Object t58958 =
              other;
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final boolean t58959 =
              au.edu.anu.chem.BondType.$RTT.instanceOf(t58958);
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final boolean t58960 =
              !(t58959);
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
if (t58960) {
                
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
return false;
            }
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final java.lang.Object t58961 =
              other;
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final au.edu.anu.chem.BondType t58962 =
              ((au.edu.anu.chem.BondType)x10.rtt.Types.asStruct(au.edu.anu.chem.BondType.$RTT,t58961));
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final boolean t58963 =
              this._struct_equals$O(((au.edu.anu.chem.BondType)(t58962)));
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
return t58963;
        }
        
        
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final public boolean
                                                                                                        _struct_equals$O(
                                                                                                        au.edu.anu.chem.BondType other){
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final java.lang.String t58965 =
              ((java.lang.String)(this.
                                    description));
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final au.edu.anu.chem.BondType t58964 =
              other;
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final java.lang.String t58966 =
              ((java.lang.String)(t58964.
                                    description));
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
boolean t58970 =
              x10.rtt.Equality.equalsequals((t58965),(t58966));
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
if (t58970) {
                
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final double t58968 =
                  this.
                    bondOrder;
                
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final au.edu.anu.chem.BondType t58967 =
                  other;
                
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final double t58969 =
                  t58967.
                    bondOrder;
                
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
t58970 = ((double) t58968) ==
                ((double) t58969);
            }
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final boolean t58971 =
              t58970;
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
return t58971;
        }
        
        
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final public au.edu.anu.chem.BondType
                                                                                                        au$edu$anu$chem$BondType$$au$edu$anu$chem$BondType$this(
                                                                                                        ){
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
return au.edu.anu.chem.BondType.this;
        }
        
        
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
final private void
                                                                                                        __fieldInitializers58892(
                                                                                                        ){
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10"
this.X10$object_lock_id0 = -1;
        }
        
        final public static void
          __fieldInitializers58892$P(
          final au.edu.anu.chem.BondType BondType){
            BondType.__fieldInitializers58892();
        }
        
        public static int
          fieldId$IONIC_BOND;
        final public static x10.core.concurrent.AtomicInteger
          initStatus$IONIC_BOND =
          new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
        public static int
          fieldId$AMIDE_BOND;
        final public static x10.core.concurrent.AtomicInteger
          initStatus$AMIDE_BOND =
          new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
        public static int
          fieldId$AROMATIC_BOND;
        final public static x10.core.concurrent.AtomicInteger
          initStatus$AROMATIC_BOND =
          new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
        public static int
          fieldId$QUADRUPLE_BOND;
        final public static x10.core.concurrent.AtomicInteger
          initStatus$QUADRUPLE_BOND =
          new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
        public static int
          fieldId$TRIPLE_BOND;
        final public static x10.core.concurrent.AtomicInteger
          initStatus$TRIPLE_BOND =
          new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
        public static int
          fieldId$DOUBLE_BOND;
        final public static x10.core.concurrent.AtomicInteger
          initStatus$DOUBLE_BOND =
          new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
        public static int
          fieldId$SINGLE_BOND;
        final public static x10.core.concurrent.AtomicInteger
          initStatus$SINGLE_BOND =
          new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
        public static int
          fieldId$NO_BOND;
        final public static x10.core.concurrent.AtomicInteger
          initStatus$NO_BOND =
          new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
        public static int
          fieldId$WEAK_BOND;
        final public static x10.core.concurrent.AtomicInteger
          initStatus$WEAK_BOND =
          new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
        
        public static void
          getDeserialized$WEAK_BOND(
          byte[] buf){
            if (((boolean) x10.x10rt.X10JavaSerializable.CUSTOM_JAVA_SERIALIZATION) ==
                  ((boolean) true)) {
                au.edu.anu.chem.BondType.WEAK_BOND = ((au.edu.anu.chem.BondType)(x10.runtime.impl.java.InitDispatcher.deserializeField(((byte[])(buf)))));
            } else {
                au.edu.anu.chem.BondType.WEAK_BOND = ((au.edu.anu.chem.BondType)(((au.edu.anu.chem.BondType)x10.rtt.Types.asStruct(au.edu.anu.chem.BondType.$RTT,x10.runtime.impl.java.InitDispatcher.deserializeField(((byte[])(buf)))))));
            }
            au.edu.anu.chem.BondType.initStatus$WEAK_BOND.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
            x10.runtime.impl.java.InitDispatcher.lockInitialized();
            x10.runtime.impl.java.InitDispatcher.notifyInitialized();
        }
        
        public static au.edu.anu.chem.BondType
          getInitialized$WEAK_BOND(
          ){
            if (((int) x10.lang.Runtime.hereInt$O()) ==
                ((int) 0)) {
                if (au.edu.anu.chem.BondType.initStatus$WEAK_BOND.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                                (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                    au.edu.anu.chem.BondType.WEAK_BOND = ((au.edu.anu.chem.BondType)(new au.edu.anu.chem.BondType((java.lang.System[]) null).$init(((java.lang.String)("Weak bond")),
                                                                                                                                                   ((double)(0.0)),
                                                                                                                                                   x10.util.concurrent.OrderedLock.createNewLock())));
                    if (((boolean) x10.x10rt.X10JavaSerializable.CUSTOM_JAVA_SERIALIZATION) ==
                          ((boolean) true)) {
                        x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(au.edu.anu.chem.BondType.WEAK_BOND)),
                                                                                  (int)(au.edu.anu.chem.BondType.fieldId$WEAK_BOND));
                    } else {
                        x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(au.edu.anu.chem.BondType.WEAK_BOND)),
                                                                                  (int)(au.edu.anu.chem.BondType.fieldId$WEAK_BOND));
                    }
                    au.edu.anu.chem.BondType.initStatus$WEAK_BOND.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                }
            }
            if (((int) au.edu.anu.chem.BondType.initStatus$WEAK_BOND.get()) !=
                ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                x10.runtime.impl.java.InitDispatcher.lockInitialized();
                while (((int) au.edu.anu.chem.BondType.initStatus$WEAK_BOND.get()) !=
                       ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                    x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                }
                x10.runtime.impl.java.InitDispatcher.unlockInitialized();
            }
            return au.edu.anu.chem.BondType.WEAK_BOND;
        }
        
        public static void
          getDeserialized$NO_BOND(
          byte[] buf){
            if (((boolean) x10.x10rt.X10JavaSerializable.CUSTOM_JAVA_SERIALIZATION) ==
                  ((boolean) true)) {
                au.edu.anu.chem.BondType.NO_BOND = ((au.edu.anu.chem.BondType)(x10.runtime.impl.java.InitDispatcher.deserializeField(((byte[])(buf)))));
            } else {
                au.edu.anu.chem.BondType.NO_BOND = ((au.edu.anu.chem.BondType)(((au.edu.anu.chem.BondType)x10.rtt.Types.asStruct(au.edu.anu.chem.BondType.$RTT,x10.runtime.impl.java.InitDispatcher.deserializeField(((byte[])(buf)))))));
            }
            au.edu.anu.chem.BondType.initStatus$NO_BOND.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
            x10.runtime.impl.java.InitDispatcher.lockInitialized();
            x10.runtime.impl.java.InitDispatcher.notifyInitialized();
        }
        
        public static au.edu.anu.chem.BondType
          getInitialized$NO_BOND(
          ){
            if (((int) x10.lang.Runtime.hereInt$O()) ==
                ((int) 0)) {
                if (au.edu.anu.chem.BondType.initStatus$NO_BOND.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                              (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                    au.edu.anu.chem.BondType.NO_BOND = ((au.edu.anu.chem.BondType)(new au.edu.anu.chem.BondType((java.lang.System[]) null).$init(((java.lang.String)("No bond")),
                                                                                                                                                 ((double)(0.0)),
                                                                                                                                                 x10.util.concurrent.OrderedLock.createNewLock())));
                    if (((boolean) x10.x10rt.X10JavaSerializable.CUSTOM_JAVA_SERIALIZATION) ==
                          ((boolean) true)) {
                        x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(au.edu.anu.chem.BondType.NO_BOND)),
                                                                                  (int)(au.edu.anu.chem.BondType.fieldId$NO_BOND));
                    } else {
                        x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(au.edu.anu.chem.BondType.NO_BOND)),
                                                                                  (int)(au.edu.anu.chem.BondType.fieldId$NO_BOND));
                    }
                    au.edu.anu.chem.BondType.initStatus$NO_BOND.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                }
            }
            if (((int) au.edu.anu.chem.BondType.initStatus$NO_BOND.get()) !=
                ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                x10.runtime.impl.java.InitDispatcher.lockInitialized();
                while (((int) au.edu.anu.chem.BondType.initStatus$NO_BOND.get()) !=
                       ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                    x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                }
                x10.runtime.impl.java.InitDispatcher.unlockInitialized();
            }
            return au.edu.anu.chem.BondType.NO_BOND;
        }
        
        public static void
          getDeserialized$SINGLE_BOND(
          byte[] buf){
            if (((boolean) x10.x10rt.X10JavaSerializable.CUSTOM_JAVA_SERIALIZATION) ==
                  ((boolean) true)) {
                au.edu.anu.chem.BondType.SINGLE_BOND = ((au.edu.anu.chem.BondType)(x10.runtime.impl.java.InitDispatcher.deserializeField(((byte[])(buf)))));
            } else {
                au.edu.anu.chem.BondType.SINGLE_BOND = ((au.edu.anu.chem.BondType)(((au.edu.anu.chem.BondType)x10.rtt.Types.asStruct(au.edu.anu.chem.BondType.$RTT,x10.runtime.impl.java.InitDispatcher.deserializeField(((byte[])(buf)))))));
            }
            au.edu.anu.chem.BondType.initStatus$SINGLE_BOND.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
            x10.runtime.impl.java.InitDispatcher.lockInitialized();
            x10.runtime.impl.java.InitDispatcher.notifyInitialized();
        }
        
        public static au.edu.anu.chem.BondType
          getInitialized$SINGLE_BOND(
          ){
            if (((int) x10.lang.Runtime.hereInt$O()) ==
                ((int) 0)) {
                if (au.edu.anu.chem.BondType.initStatus$SINGLE_BOND.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                                  (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                    au.edu.anu.chem.BondType.SINGLE_BOND = ((au.edu.anu.chem.BondType)(new au.edu.anu.chem.BondType((java.lang.System[]) null).$init(((java.lang.String)("Single bond")),
                                                                                                                                                     ((double)(1.0)),
                                                                                                                                                     x10.util.concurrent.OrderedLock.createNewLock())));
                    if (((boolean) x10.x10rt.X10JavaSerializable.CUSTOM_JAVA_SERIALIZATION) ==
                          ((boolean) true)) {
                        x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(au.edu.anu.chem.BondType.SINGLE_BOND)),
                                                                                  (int)(au.edu.anu.chem.BondType.fieldId$SINGLE_BOND));
                    } else {
                        x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(au.edu.anu.chem.BondType.SINGLE_BOND)),
                                                                                  (int)(au.edu.anu.chem.BondType.fieldId$SINGLE_BOND));
                    }
                    au.edu.anu.chem.BondType.initStatus$SINGLE_BOND.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                }
            }
            if (((int) au.edu.anu.chem.BondType.initStatus$SINGLE_BOND.get()) !=
                ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                x10.runtime.impl.java.InitDispatcher.lockInitialized();
                while (((int) au.edu.anu.chem.BondType.initStatus$SINGLE_BOND.get()) !=
                       ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                    x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                }
                x10.runtime.impl.java.InitDispatcher.unlockInitialized();
            }
            return au.edu.anu.chem.BondType.SINGLE_BOND;
        }
        
        public static void
          getDeserialized$DOUBLE_BOND(
          byte[] buf){
            if (((boolean) x10.x10rt.X10JavaSerializable.CUSTOM_JAVA_SERIALIZATION) ==
                  ((boolean) true)) {
                au.edu.anu.chem.BondType.DOUBLE_BOND = ((au.edu.anu.chem.BondType)(x10.runtime.impl.java.InitDispatcher.deserializeField(((byte[])(buf)))));
            } else {
                au.edu.anu.chem.BondType.DOUBLE_BOND = ((au.edu.anu.chem.BondType)(((au.edu.anu.chem.BondType)x10.rtt.Types.asStruct(au.edu.anu.chem.BondType.$RTT,x10.runtime.impl.java.InitDispatcher.deserializeField(((byte[])(buf)))))));
            }
            au.edu.anu.chem.BondType.initStatus$DOUBLE_BOND.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
            x10.runtime.impl.java.InitDispatcher.lockInitialized();
            x10.runtime.impl.java.InitDispatcher.notifyInitialized();
        }
        
        public static au.edu.anu.chem.BondType
          getInitialized$DOUBLE_BOND(
          ){
            if (((int) x10.lang.Runtime.hereInt$O()) ==
                ((int) 0)) {
                if (au.edu.anu.chem.BondType.initStatus$DOUBLE_BOND.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                                  (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                    au.edu.anu.chem.BondType.DOUBLE_BOND = ((au.edu.anu.chem.BondType)(new au.edu.anu.chem.BondType((java.lang.System[]) null).$init(((java.lang.String)("Double bond")),
                                                                                                                                                     ((double)(2.0)),
                                                                                                                                                     x10.util.concurrent.OrderedLock.createNewLock())));
                    if (((boolean) x10.x10rt.X10JavaSerializable.CUSTOM_JAVA_SERIALIZATION) ==
                          ((boolean) true)) {
                        x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(au.edu.anu.chem.BondType.DOUBLE_BOND)),
                                                                                  (int)(au.edu.anu.chem.BondType.fieldId$DOUBLE_BOND));
                    } else {
                        x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(au.edu.anu.chem.BondType.DOUBLE_BOND)),
                                                                                  (int)(au.edu.anu.chem.BondType.fieldId$DOUBLE_BOND));
                    }
                    au.edu.anu.chem.BondType.initStatus$DOUBLE_BOND.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                }
            }
            if (((int) au.edu.anu.chem.BondType.initStatus$DOUBLE_BOND.get()) !=
                ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                x10.runtime.impl.java.InitDispatcher.lockInitialized();
                while (((int) au.edu.anu.chem.BondType.initStatus$DOUBLE_BOND.get()) !=
                       ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                    x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                }
                x10.runtime.impl.java.InitDispatcher.unlockInitialized();
            }
            return au.edu.anu.chem.BondType.DOUBLE_BOND;
        }
        
        public static void
          getDeserialized$TRIPLE_BOND(
          byte[] buf){
            if (((boolean) x10.x10rt.X10JavaSerializable.CUSTOM_JAVA_SERIALIZATION) ==
                  ((boolean) true)) {
                au.edu.anu.chem.BondType.TRIPLE_BOND = ((au.edu.anu.chem.BondType)(x10.runtime.impl.java.InitDispatcher.deserializeField(((byte[])(buf)))));
            } else {
                au.edu.anu.chem.BondType.TRIPLE_BOND = ((au.edu.anu.chem.BondType)(((au.edu.anu.chem.BondType)x10.rtt.Types.asStruct(au.edu.anu.chem.BondType.$RTT,x10.runtime.impl.java.InitDispatcher.deserializeField(((byte[])(buf)))))));
            }
            au.edu.anu.chem.BondType.initStatus$TRIPLE_BOND.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
            x10.runtime.impl.java.InitDispatcher.lockInitialized();
            x10.runtime.impl.java.InitDispatcher.notifyInitialized();
        }
        
        public static au.edu.anu.chem.BondType
          getInitialized$TRIPLE_BOND(
          ){
            if (((int) x10.lang.Runtime.hereInt$O()) ==
                ((int) 0)) {
                if (au.edu.anu.chem.BondType.initStatus$TRIPLE_BOND.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                                  (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                    au.edu.anu.chem.BondType.TRIPLE_BOND = ((au.edu.anu.chem.BondType)(new au.edu.anu.chem.BondType((java.lang.System[]) null).$init(((java.lang.String)("Triple bond")),
                                                                                                                                                     ((double)(3.0)),
                                                                                                                                                     x10.util.concurrent.OrderedLock.createNewLock())));
                    if (((boolean) x10.x10rt.X10JavaSerializable.CUSTOM_JAVA_SERIALIZATION) ==
                          ((boolean) true)) {
                        x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(au.edu.anu.chem.BondType.TRIPLE_BOND)),
                                                                                  (int)(au.edu.anu.chem.BondType.fieldId$TRIPLE_BOND));
                    } else {
                        x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(au.edu.anu.chem.BondType.TRIPLE_BOND)),
                                                                                  (int)(au.edu.anu.chem.BondType.fieldId$TRIPLE_BOND));
                    }
                    au.edu.anu.chem.BondType.initStatus$TRIPLE_BOND.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                }
            }
            if (((int) au.edu.anu.chem.BondType.initStatus$TRIPLE_BOND.get()) !=
                ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                x10.runtime.impl.java.InitDispatcher.lockInitialized();
                while (((int) au.edu.anu.chem.BondType.initStatus$TRIPLE_BOND.get()) !=
                       ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                    x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                }
                x10.runtime.impl.java.InitDispatcher.unlockInitialized();
            }
            return au.edu.anu.chem.BondType.TRIPLE_BOND;
        }
        
        public static void
          getDeserialized$QUADRUPLE_BOND(
          byte[] buf){
            if (((boolean) x10.x10rt.X10JavaSerializable.CUSTOM_JAVA_SERIALIZATION) ==
                  ((boolean) true)) {
                au.edu.anu.chem.BondType.QUADRUPLE_BOND = ((au.edu.anu.chem.BondType)(x10.runtime.impl.java.InitDispatcher.deserializeField(((byte[])(buf)))));
            } else {
                au.edu.anu.chem.BondType.QUADRUPLE_BOND = ((au.edu.anu.chem.BondType)(((au.edu.anu.chem.BondType)x10.rtt.Types.asStruct(au.edu.anu.chem.BondType.$RTT,x10.runtime.impl.java.InitDispatcher.deserializeField(((byte[])(buf)))))));
            }
            au.edu.anu.chem.BondType.initStatus$QUADRUPLE_BOND.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
            x10.runtime.impl.java.InitDispatcher.lockInitialized();
            x10.runtime.impl.java.InitDispatcher.notifyInitialized();
        }
        
        public static au.edu.anu.chem.BondType
          getInitialized$QUADRUPLE_BOND(
          ){
            if (((int) x10.lang.Runtime.hereInt$O()) ==
                ((int) 0)) {
                if (au.edu.anu.chem.BondType.initStatus$QUADRUPLE_BOND.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                                     (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                    au.edu.anu.chem.BondType.QUADRUPLE_BOND = ((au.edu.anu.chem.BondType)(new au.edu.anu.chem.BondType((java.lang.System[]) null).$init(((java.lang.String)("Quadruple bond")),
                                                                                                                                                        ((double)(4.0)),
                                                                                                                                                        x10.util.concurrent.OrderedLock.createNewLock())));
                    if (((boolean) x10.x10rt.X10JavaSerializable.CUSTOM_JAVA_SERIALIZATION) ==
                          ((boolean) true)) {
                        x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(au.edu.anu.chem.BondType.QUADRUPLE_BOND)),
                                                                                  (int)(au.edu.anu.chem.BondType.fieldId$QUADRUPLE_BOND));
                    } else {
                        x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(au.edu.anu.chem.BondType.QUADRUPLE_BOND)),
                                                                                  (int)(au.edu.anu.chem.BondType.fieldId$QUADRUPLE_BOND));
                    }
                    au.edu.anu.chem.BondType.initStatus$QUADRUPLE_BOND.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                }
            }
            if (((int) au.edu.anu.chem.BondType.initStatus$QUADRUPLE_BOND.get()) !=
                ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                x10.runtime.impl.java.InitDispatcher.lockInitialized();
                while (((int) au.edu.anu.chem.BondType.initStatus$QUADRUPLE_BOND.get()) !=
                       ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                    x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                }
                x10.runtime.impl.java.InitDispatcher.unlockInitialized();
            }
            return au.edu.anu.chem.BondType.QUADRUPLE_BOND;
        }
        
        public static void
          getDeserialized$AROMATIC_BOND(
          byte[] buf){
            if (((boolean) x10.x10rt.X10JavaSerializable.CUSTOM_JAVA_SERIALIZATION) ==
                  ((boolean) true)) {
                au.edu.anu.chem.BondType.AROMATIC_BOND = ((au.edu.anu.chem.BondType)(x10.runtime.impl.java.InitDispatcher.deserializeField(((byte[])(buf)))));
            } else {
                au.edu.anu.chem.BondType.AROMATIC_BOND = ((au.edu.anu.chem.BondType)(((au.edu.anu.chem.BondType)x10.rtt.Types.asStruct(au.edu.anu.chem.BondType.$RTT,x10.runtime.impl.java.InitDispatcher.deserializeField(((byte[])(buf)))))));
            }
            au.edu.anu.chem.BondType.initStatus$AROMATIC_BOND.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
            x10.runtime.impl.java.InitDispatcher.lockInitialized();
            x10.runtime.impl.java.InitDispatcher.notifyInitialized();
        }
        
        public static au.edu.anu.chem.BondType
          getInitialized$AROMATIC_BOND(
          ){
            if (((int) x10.lang.Runtime.hereInt$O()) ==
                ((int) 0)) {
                if (au.edu.anu.chem.BondType.initStatus$AROMATIC_BOND.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                                    (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                    au.edu.anu.chem.BondType.AROMATIC_BOND = ((au.edu.anu.chem.BondType)(new au.edu.anu.chem.BondType((java.lang.System[]) null).$init(((java.lang.String)("Aromatic bond")),
                                                                                                                                                       ((double)(1.5)),
                                                                                                                                                       x10.util.concurrent.OrderedLock.createNewLock())));
                    if (((boolean) x10.x10rt.X10JavaSerializable.CUSTOM_JAVA_SERIALIZATION) ==
                          ((boolean) true)) {
                        x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(au.edu.anu.chem.BondType.AROMATIC_BOND)),
                                                                                  (int)(au.edu.anu.chem.BondType.fieldId$AROMATIC_BOND));
                    } else {
                        x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(au.edu.anu.chem.BondType.AROMATIC_BOND)),
                                                                                  (int)(au.edu.anu.chem.BondType.fieldId$AROMATIC_BOND));
                    }
                    au.edu.anu.chem.BondType.initStatus$AROMATIC_BOND.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                }
            }
            if (((int) au.edu.anu.chem.BondType.initStatus$AROMATIC_BOND.get()) !=
                ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                x10.runtime.impl.java.InitDispatcher.lockInitialized();
                while (((int) au.edu.anu.chem.BondType.initStatus$AROMATIC_BOND.get()) !=
                       ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                    x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                }
                x10.runtime.impl.java.InitDispatcher.unlockInitialized();
            }
            return au.edu.anu.chem.BondType.AROMATIC_BOND;
        }
        
        public static void
          getDeserialized$AMIDE_BOND(
          byte[] buf){
            if (((boolean) x10.x10rt.X10JavaSerializable.CUSTOM_JAVA_SERIALIZATION) ==
                  ((boolean) true)) {
                au.edu.anu.chem.BondType.AMIDE_BOND = ((au.edu.anu.chem.BondType)(x10.runtime.impl.java.InitDispatcher.deserializeField(((byte[])(buf)))));
            } else {
                au.edu.anu.chem.BondType.AMIDE_BOND = ((au.edu.anu.chem.BondType)(((au.edu.anu.chem.BondType)x10.rtt.Types.asStruct(au.edu.anu.chem.BondType.$RTT,x10.runtime.impl.java.InitDispatcher.deserializeField(((byte[])(buf)))))));
            }
            au.edu.anu.chem.BondType.initStatus$AMIDE_BOND.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
            x10.runtime.impl.java.InitDispatcher.lockInitialized();
            x10.runtime.impl.java.InitDispatcher.notifyInitialized();
        }
        
        public static au.edu.anu.chem.BondType
          getInitialized$AMIDE_BOND(
          ){
            if (((int) x10.lang.Runtime.hereInt$O()) ==
                ((int) 0)) {
                if (au.edu.anu.chem.BondType.initStatus$AMIDE_BOND.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                                 (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                    au.edu.anu.chem.BondType.AMIDE_BOND = ((au.edu.anu.chem.BondType)(new au.edu.anu.chem.BondType((java.lang.System[]) null).$init(((java.lang.String)("Amide bond")),
                                                                                                                                                    ((double)(1.41)),
                                                                                                                                                    x10.util.concurrent.OrderedLock.createNewLock())));
                    if (((boolean) x10.x10rt.X10JavaSerializable.CUSTOM_JAVA_SERIALIZATION) ==
                          ((boolean) true)) {
                        x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(au.edu.anu.chem.BondType.AMIDE_BOND)),
                                                                                  (int)(au.edu.anu.chem.BondType.fieldId$AMIDE_BOND));
                    } else {
                        x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(au.edu.anu.chem.BondType.AMIDE_BOND)),
                                                                                  (int)(au.edu.anu.chem.BondType.fieldId$AMIDE_BOND));
                    }
                    au.edu.anu.chem.BondType.initStatus$AMIDE_BOND.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                }
            }
            if (((int) au.edu.anu.chem.BondType.initStatus$AMIDE_BOND.get()) !=
                ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                x10.runtime.impl.java.InitDispatcher.lockInitialized();
                while (((int) au.edu.anu.chem.BondType.initStatus$AMIDE_BOND.get()) !=
                       ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                    x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                }
                x10.runtime.impl.java.InitDispatcher.unlockInitialized();
            }
            return au.edu.anu.chem.BondType.AMIDE_BOND;
        }
        
        public static void
          getDeserialized$IONIC_BOND(
          byte[] buf){
            if (((boolean) x10.x10rt.X10JavaSerializable.CUSTOM_JAVA_SERIALIZATION) ==
                  ((boolean) true)) {
                au.edu.anu.chem.BondType.IONIC_BOND = ((au.edu.anu.chem.BondType)(x10.runtime.impl.java.InitDispatcher.deserializeField(((byte[])(buf)))));
            } else {
                au.edu.anu.chem.BondType.IONIC_BOND = ((au.edu.anu.chem.BondType)(((au.edu.anu.chem.BondType)x10.rtt.Types.asStruct(au.edu.anu.chem.BondType.$RTT,x10.runtime.impl.java.InitDispatcher.deserializeField(((byte[])(buf)))))));
            }
            au.edu.anu.chem.BondType.initStatus$IONIC_BOND.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
            x10.runtime.impl.java.InitDispatcher.lockInitialized();
            x10.runtime.impl.java.InitDispatcher.notifyInitialized();
        }
        
        public static au.edu.anu.chem.BondType
          getInitialized$IONIC_BOND(
          ){
            if (((int) x10.lang.Runtime.hereInt$O()) ==
                ((int) 0)) {
                if (au.edu.anu.chem.BondType.initStatus$IONIC_BOND.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                                 (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                    au.edu.anu.chem.BondType.IONIC_BOND = ((au.edu.anu.chem.BondType)(new au.edu.anu.chem.BondType((java.lang.System[]) null).$init(((java.lang.String)("Ionic bond")),
                                                                                                                                                    ((double)(0.0)),
                                                                                                                                                    x10.util.concurrent.OrderedLock.createNewLock())));
                    if (((boolean) x10.x10rt.X10JavaSerializable.CUSTOM_JAVA_SERIALIZATION) ==
                          ((boolean) true)) {
                        x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(au.edu.anu.chem.BondType.IONIC_BOND)),
                                                                                  (int)(au.edu.anu.chem.BondType.fieldId$IONIC_BOND));
                    } else {
                        x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(au.edu.anu.chem.BondType.IONIC_BOND)),
                                                                                  (int)(au.edu.anu.chem.BondType.fieldId$IONIC_BOND));
                    }
                    au.edu.anu.chem.BondType.initStatus$IONIC_BOND.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                }
            }
            if (((int) au.edu.anu.chem.BondType.initStatus$IONIC_BOND.get()) !=
                ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                x10.runtime.impl.java.InitDispatcher.lockInitialized();
                while (((int) au.edu.anu.chem.BondType.initStatus$IONIC_BOND.get()) !=
                       ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                    x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                }
                x10.runtime.impl.java.InitDispatcher.unlockInitialized();
            }
            return au.edu.anu.chem.BondType.IONIC_BOND;
        }
        
        static {
                   au.edu.anu.chem.BondType.fieldId$WEAK_BOND = x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("au.edu.anu.chem.BondType")),
                                                                                                                    ((java.lang.String)("WEAK_BOND")));
                   au.edu.anu.chem.BondType.fieldId$NO_BOND = x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("au.edu.anu.chem.BondType")),
                                                                                                                  ((java.lang.String)("NO_BOND")));
                   au.edu.anu.chem.BondType.fieldId$SINGLE_BOND = x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("au.edu.anu.chem.BondType")),
                                                                                                                      ((java.lang.String)("SINGLE_BOND")));
                   au.edu.anu.chem.BondType.fieldId$DOUBLE_BOND = x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("au.edu.anu.chem.BondType")),
                                                                                                                      ((java.lang.String)("DOUBLE_BOND")));
                   au.edu.anu.chem.BondType.fieldId$TRIPLE_BOND = x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("au.edu.anu.chem.BondType")),
                                                                                                                      ((java.lang.String)("TRIPLE_BOND")));
                   au.edu.anu.chem.BondType.fieldId$QUADRUPLE_BOND = x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("au.edu.anu.chem.BondType")),
                                                                                                                         ((java.lang.String)("QUADRUPLE_BOND")));
                   au.edu.anu.chem.BondType.fieldId$AROMATIC_BOND = x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("au.edu.anu.chem.BondType")),
                                                                                                                        ((java.lang.String)("AROMATIC_BOND")));
                   au.edu.anu.chem.BondType.fieldId$AMIDE_BOND = x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("au.edu.anu.chem.BondType")),
                                                                                                                     ((java.lang.String)("AMIDE_BOND")));
                   au.edu.anu.chem.BondType.fieldId$IONIC_BOND = x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("au.edu.anu.chem.BondType")),
                                                                                                                     ((java.lang.String)("IONIC_BOND")));
               }
    
}
