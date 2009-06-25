Annotations and compiler plugins
--------------------------------

X10 source code can be extended with annotations.  Compiler plugins can
be used to process these annotations.

Annotations can be placed on most syntactic constructs in the source
code: declarations, statements, expressions, and types.  See the
language specification for more details.  This example presents an
annotation on integral types that specifies the number of bits in values
of the type.

Types are annotated with the annotation @Bits(k) that specifies values
of that type fit in k bits.  The plugin will check that only values of
the appropriate width are assigned into annotated variables.  For
example:

    int @Bits(3) x = 6; // okay; 6 fits in 3 bits
    int @Bits(3) y = 9; // compile-time error caught by plugin; 9 needs 4 bits
    int @Bits(1) z = x; // compile-time error caught by plugin; x needs 3 bits

Declaring the annotation
------------------------

The declaration of the Bits annotation is straightforward.  Write the
following code in bits/Bits.x10:

    package bits;

    import x10.lang.annotations.*;

    public interface Bits(int n) extends TypeAnnotation { }

Annotations on types must extend x10.lang.annotations.TypeAnnotation.
There are other interfaces for other syntactic constructs.

Files can use the annotation by importing the interface
bits.plugin.Bits or by using its fully qualified name directly.

Writing the plugin
------------------

Plugins are implemented in Java using the x10c implementation classes.
x10c is built on top of the Polyglot compiler framework; plugins can
execute one or more Polyglot passes.

The class SimpleTypeAnnotationPlugin provided by x10c takes care of
creating the passes for you; the plugin writer just has implement the
checking code for the annotation.

The following plugin checks that annotations are used properly.  A more
sophisticated plugin could also rewrites instanceof and cast expressions to
check the bit width and to coerce to the appropriate width if necessary, or
could propagate bit widths through arithmetic expressions.

    package bits.plugin;

    import java.util.List;

    import polyglot.ast.Binary;
    import polyglot.ast.Expr;
    import polyglot.ast.IntLit;
    import polyglot.ext.x10.ast.X10Cast;
    import polyglot.ext.x10.ast.X10Instanceof;
    import polyglot.ext.x10.ast.X10NodeFactory;
    import polyglot.ext.x10.extension.X10Ext;
    import polyglot.ext.x10.plugin.SimpleTypeAnnotationPlugin;
    import polyglot.ext.x10.types.X10ClassType;
    import polyglot.ext.x10.types.X10Context;
    import polyglot.ext.x10.types.X10Type;
    import polyglot.ext.x10.types.X10TypeSystem;
    import polyglot.types.SemanticException;
    import polyglot.util.Position;

    public class BitsTypePlugin extends SimpleTypeAnnotationPlugin {
        public BitsTypePlugin() {
            super();
        }

        /** Check assignments and argument passing. */
        public Expr checkImplicitCoercion(X10Type toType,
                Expr fromExpr,
                X10Context context,
                X10TypeSystem ts,
                X10NodeFactory nf) throws SemanticException {


            Position pos = fromExpr.position();
            
            if (fromExpr == null) {
                return fromExpr;
            }

            if (! toType.isLongOrLess()) {
                return fromExpr;
            }
            
            int maxBits = bitsInType(toType);
            
            X10ClassType bitsType = (X10ClassType) ts.systemResolver().find("bits.Bits");
            List<X10ClassType> toATs = toType.annotationMatching(bitsType);

            if (toATs.isEmpty()) {
                return fromExpr;
            }

            X10ClassType toAT = toATs.get(0);
            
            int bits = getBitsFromAnnotation(toAT, maxBits);
            
            List<X10ClassType> fromATs =
                ((X10Type) fromExpr.type()).annotationMatching(bitsType);
            
              if (! fromATs.isEmpty()) {
                // OK!
                int rbits = getBitsFromAnnotation(fromATs.get(0), 64);
                if (rbits > bits) {
                    throw new SemanticException("Cannot assign to Bits(" +
                        bits + ") variable; unknown width.", pos);
                }
            }
            else if (fromExpr.isConstant()) {
                if (fromExpr.type().isLongOrLess()) {
                    long x = ((Number) fromExpr.constantValue()).longValue();
                    long mask = (0xffffffffffffffffL << bits);
                    if ((x & mask) != 0L) {
                        throw new SemanticException("Cannot assign to Bits(" +
                            bits + ") variable; too wide.", pos);
                    }
                }
                else {
                    throw new SemanticException("Cannot assign to Bits(" +
                        bits + ") variable; unknown width.", pos);
                }
            }
            
            return fromExpr;
        }

        public int bitsInType(X10Type toType) throws SemanticException {
            int maxBits = 64;
            
            if (toType.isInt()) {
                maxBits = 32;
            }
            else if (toType.isShort() || toType.isChar()) {
                maxBits = 16;
            }
            else if (toType.isByte()) {
                maxBits = 8;
            }
            
            X10ClassType bitsType = (X10ClassType)
                toType.typeSystem().systemResolver().find("bits.Bits");
            List<X10ClassType> ats = toType.annotationMatching(bitsType);

            if (ats.isEmpty()) {
                return maxBits;
            }

            return getBitsFromAnnotation(ats.get(0), maxBits);
        }

        protected int getBitsFromAnnotation(X10ClassType at, int maxBits) throws SemanticException {
            Expr lhsBits = at.propertyExpr(0);
            if (lhsBits instanceof IntLit) {
                IntLit lhsLit = (IntLit) lhsBits;
                Object val = lhsLit.constantValue();
                if (val instanceof Number) {
                    int bits = ((Number) val).intValue();
                    
                    if (bits < 1 || bits > maxBits) {
                        throw new SemanticException("Bits property must be between 1 and " + maxBits + ".");
                    }
                    
                    return bits;
                }
            }
            
            throw new SemanticException("Bits property must be an integer literal.");
        }
    }



Running the plugin
------------------

To check a program with the new plugin, first compile the plugin 
with x10c.jar, x10c.jar, and polyglot.jar in the classpath.

   javac -classpath lib/x10c.jar:lib/x10c.jar:lib/polyglot.jar \
       bits/plugin/BitsPlugin.java

The plugin can used by putting it in the classpath and then running
x10c on an annotated source file with the following arguments.

   x10c -PLUGINS=bits.Bits Foo.x10


