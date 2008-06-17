package x10.lang;

abstract class TypeDefs {

    static type boolean = Boolean;
    static type byte = Byte;
    static type short = Short;
    static type char = Char;
    static type int = Int;
    static type long = Long;
    static type float = Float;
    static type double = Double;
    
    static type int8 = byte;
    static type nat7 = byte{self>=0};
    static type int16 = short;
    static type nat15 = short{self>=0};
    static type int32 = int;
    static type nat31 = int{self>=0};
    static type int64 = long;
    static type nat63 = long{self>=0};

    static type true = boolean{self==true};
    static type false = boolean{self==false};

    static type nat = int{self >= 0};
    static type nat(v: int) = nat{self == v};
    static type nat(r: region{rank==1}) = nat{r.low <= self, self <= r.high};
    static type zero=nat(0);
    static type one=nat(1);
    static type two=nat(2);
    static type three=nat(3);
    static type four=nat(4);
    static type five=nat(5);
    static type six=nat(6);
    static type seven=nat(7);
    static type eight=nat(8);
    static type nine=nat(9);

    static type int(v: int) = int{self==v};
    static type int(r: region{rank==1}) = int{r.low <= self, self <= r.high};
    static type negone=int(-1);
    static type negtwo=int(-2);
    static type negthree=int(-3);
    static type negfour=int(-4);
    static type negfive=int(-5);
    static type negsix=int(-6);
    static type negseven=int(-7);
    static type negeight=int(-8);
    static type negnine=int(-9);

    static type Indexable[T](x : region) =Indexable[T]{self.base==x};
    static type Settable[T](x : region) = Settable[T]{self.base==x};

    static type ValRail[T](x: nat)= ValRail[T]{self.length==x};
    static type Rail[T](x: nat)= Rail[T]{self.length==x};

    static type NativeValRail[T](x: nat)= NativeValRail[T]{self.length==x};
    static type NativeRail[T](x: nat)= NativeRail[T]{self.length==x};

    static type point = ValRail[int];
    static type point(n: nat) = point{self.length==n);
    static type point(r: region) = point{self in r};



}
