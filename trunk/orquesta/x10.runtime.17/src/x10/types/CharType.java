package x10.types;

import x10.core.fun.Fun_0_1;
import x10.core.fun.Fun_0_2;


public class CharType extends RuntimeType<Character> {
    public CharType() {
        super(char.class);
    }
    
    @Override
    public Object makeArray(int length) {
        return new char[length];
    }
    
    @Override
    public Character getArray(Object array, int i) {
        return ((char[]) array)[i];
    }
    
    @Override
    public void setArray(Object array, int i, Character v) {
        ((char[]) array)[i] = v;
    }
    
    @Override
    public Fun_0_1<Character, Character> absOperator() {
        return new Fun_0_1<Character, Character>() {
            public Character apply(Character x) {
                return (char) (x > 0 ? x : -x);
            }
        };
    }
    @Override
    public Fun_0_1<Character, Character> scaleOperator(final int k) {
        return new Fun_0_1<Character, Character>() {
            public Character apply(Character x) {
                return (char) (x * k);
            }
        };
    }
    
    @Override
    public Fun_0_2<Character, Character, Character> addOperator() {
        return new Fun_0_2<Character, Character, Character>() {
            public Character apply(Character x, Character y) {
                return (char) (x + y);
            }
        };
    }
    @Override
    public Fun_0_2<Character, Character, Character> subOperator() {
        return new Fun_0_2<Character, Character, Character>() {
            public Character apply(Character x, Character y) {
                return (char) (x - y);
            }
        };
    }
    @Override
    public Fun_0_2<Character, Character, Character> mulOperator() {
        return new Fun_0_2<Character, Character, Character>() {
            public Character apply(Character x, Character y) {
                return (char) (x * y);
            }
        };
    }
    @Override
    public Fun_0_2<Character, Character, Character> divOperator() {
        return new Fun_0_2<Character, Character, Character>() {
            public Character apply(Character x, Character y) {
                return (char) (x / y);
            }
        };
    }
    @Override
    public Fun_0_2<Character, Character, Character> modOperator() {
        return new Fun_0_2<Character, Character, Character>() {
            public Character apply(Character x, Character y) {
                return (char) (x % y);
            }
        };
    }
    @Override
    public Fun_0_2<Character, Character, Character> maxOperator() {
        return new Fun_0_2<Character, Character, Character>() {
            public Character apply(Character x, Character y) {
                return (x > y ? x : y);
            }
        };
    }
    @Override
    public Fun_0_2<Character, Character, Character> minOperator() {
        return new Fun_0_2<Character, Character, Character>() {
            public Character apply(Character x, Character y) {
                return (x < y ? x : y);
            }
        };
    }
    
    @Override
    public Fun_0_2<Character, Character, Character> andOperator() {
        return new Fun_0_2<Character, Character, Character>() {
            public Character apply(Character x, Character y) {
                return (char) (x & y);
            }
        };
    }

    @Override
    public Fun_0_2<Character, Character, Character> orOperator() {
        return new Fun_0_2<Character, Character, Character>() {
            public Character apply(Character x, Character y) {
                return (char) (x | y);
            }
        };
    }
    
    @Override
    public Fun_0_2<Character, Character, Character> xorOperator() {
        return new Fun_0_2<Character, Character, Character>() {
            public Character apply(Character x, Character y) {
                return (char) (x ^ y);
            }
        };
    }

    @Override
    public Fun_0_1<Character, Character> negOperator() {
        return new Fun_0_1<Character, Character>() {
            public Character apply(Character x) {
                return (char) -x;
            }
        };
    }
    
    @Override
    public Fun_0_1<Character, Character> posOperator() {
        return new Fun_0_1<Character, Character>() {
            public Character apply(Character x) {
                return (char) +x;
            }
        };
    }
    
    @Override
    public Fun_0_1<Character, Character> invOperator() {
        return new Fun_0_1<Character, Character>() {
            public Character apply(Character x) {
                return (char) ~x;
            }
        };
    }
    
    @Override
    public Character minValue() {
        return Character.MIN_VALUE;
    }

    @Override
    public Character maxValue() {
        return Character.MAX_VALUE;
    }
    
    @Override
    public Character zeroValue() {
        return (char) 0;
    }
    
    @Override
    public Character unitValue() {
        return (char) 1;
    }
}
