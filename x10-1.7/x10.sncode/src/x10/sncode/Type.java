package x10.sncode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Type {
    static final byte Type_Void = 0;
    static final byte Type_Ref = 1;
    static final byte Type_Struct = 2;
    static final byte Type_Scoped = 3;
    static final byte Type_Param = 4;
    static final byte Type_Constrained = 5;
    static final byte Type_Fun = 6;

    public abstract String desc();

    public abstract void writeInto(ConstantPool cp, ByteBuffer w);

    public static class ConstrainedType extends Type {
        private Type base;
        private Constraint c;

        public ConstrainedType(Type base, Constraint c) {
            this.base = base;
            this.c = c;
            assert base != null;
            assert c != null;
        }

        public ConstrainedType(ConstantPoolParser cp, ByteBuffer r) throws InvalidClassFileException {
            this.base = cp.getCPType(r.getCPIndex());
            this.c = cp.getCPConstraint(r.getCPIndex());
        }

        @Override
        public String desc() {
            return base.desc();
        }

        public void writeInto(ConstantPool cp, ByteBuffer w) {
            w.addByte(Type_Constrained);
            w.addCPIndex(cp.addCPType(base));
            w.addCPIndex(cp.addCPConstraint(c));
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof ConstrainedType) {
                ConstrainedType t = (ConstrainedType) o;
                if (base.equals(t.base) && c.equals(t.c))
                    return true;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return base.hashCode() + c.hashCode() + 1433;
        }
    }

    public static class VoidType extends Type {
        public VoidType() {}

        public String toString() {
            return "void";
        }

        @Override
        public String desc() {
            return "V";
        }

        public void writeInto(ConstantPool cp, ByteBuffer w) {
            w.addByte(Type_Void);
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof VoidType;
        }

        @Override
        public int hashCode() {
            return 389;
        }
    }

    public static class FunType extends Type {
        private final List<Type> args;
        private final Type ret;

        public FunType(List<Type> a, Type r) {
            args = a;
            ret = r;
        }

        public FunType(ConstantPoolParser f, ByteBuffer r) throws InvalidClassFileException {
            int n = r.getCount();
            args = new ArrayList<Type>(n);
            for (int i = 0; i < n; i++) {
                args.add(f.getCPType(r.getCPIndex()));
            }
            ret = f.getCPType(r.getCPIndex());
        }

        public String toString() {
            return "(" + getArgs().toString().substring(1, getArgs().toString().length() - 1) + ") => " + getRet();
        }

        public List<Type> getArgs() {
            return args;
        }

        public Type getRet() {
            return ret;
        }

        @Override
        public String desc() {
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            for (Type t : args) {
                sb.append(t.desc());
            }
            sb.append(")");
            sb.append(ret.desc());
            return sb.toString();
        }

        public void writeInto(ConstantPool cp, ByteBuffer w) {
            w.addByte(Type_Fun);
            w.addCount(args.size());
            for (Type t : args)
                w.addCPIndex(cp.addCPType(t));
            w.addCPIndex(cp.addCPType(ret));
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof FunType) {
                FunType t = (FunType) o;
                if (t.args.size() != args.size())
                    return false;
                if (!t.ret.equals(ret))
                    return false;
                for (int i = 0; i < args.size(); i++)
                    if (!t.args.get(i).equals(args.get(i)))
                        return false;
                return true;
            }
            return false;
        }

        @Override
        public int hashCode() {
            int h = 23907;
            for (Type t : args) {
                h += t.hashCode();
            }
            h += ret.hashCode();
            return h;
        }
    }

    public static class AnnotatedType extends Type {
        Type annotated;
        Type annotation;
        List<String> args;

        public AnnotatedType(Type t1, Type t2) {
            annotated = t1;
            annotation = t2;
            args = Collections.EMPTY_LIST;
        }

        @Override
        public String desc() {
            StringBuilder sb = new StringBuilder();
            sb.append("%");
            sb.append(annotated.desc());
            sb.append(annotation.desc());
            if (args.size() > 0) {
                sb.append("(");
                for (String t : args) {
                    sb.append(t);
                }
                sb.append(");");
            }
            return sb.toString();
        }

        public void writeInto(ConstantPool cp, ByteBuffer w) {
            annotated.writeInto(cp, w);
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof AnnotatedType) {
                AnnotatedType t = (AnnotatedType) o;
                if (!t.annotated.equals(annotated))
                    return false;
                if (!t.annotation.equals(annotation))
                    return false;
                if (t.args.size() != args.size())
                    return false;
                for (int i = 0; i < args.size(); i++)
                    if (!t.args.get(i).equals(args.get(i)))
                        return false;
                return true;
            }
            return false;
        }

        @Override
        public int hashCode() {
            int h = 4905;
            h += annotated.hashCode();
            h += annotation.hashCode();
            for (String t : args) {
                h += t.hashCode();
            }
            return h;
        }

    }

    public static class ScopedType extends Type {
        private final String name;
        final List<Type> args;

        public ScopedType(String s, List<Type> a) {
            name = s;
            args = a;
        }

        public ScopedType(ConstantPoolParser f, ByteBuffer r) throws InvalidClassFileException {
            name = f.getCPUtf8(r.getCPIndex());
            int n = r.getCount();
            args = new ArrayList<Type>(n);
            for (int i = 0; i < n; i++) {
                args.add(f.getCPType(r.getCPIndex()));
            }
        }

        public String toString() {
            return getName() + (args.isEmpty() ? "" : args.toString());
        }

        public String getName() {
            return name;
        }

        @Override
        public String desc() {
            StringBuilder sb = new StringBuilder();
            sb.append("&");
            sb.append(name);
            if (args.size() > 0) {
                sb.append("[");
                for (Type t : args) {
                    sb.append(t.desc());
                }
                sb.append("]");
            }
            sb.append(";");
            return sb.toString();
        }

        public void writeInto(ConstantPool cp, ByteBuffer w) {
            w.addByte(Type_Scoped);
            w.addCPIndex(cp.addCPUtf8(name));
            w.addCount(args.size());
            for (Type t : args)
                w.addCPIndex(cp.addCPType(t));
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof ScopedType) {
                ScopedType t = (ScopedType) o;
                if (!t.name.equals(name))
                    return false;
                if (t.args.size() != args.size())
                    return false;
                for (int i = 0; i < args.size(); i++)
                    if (!t.args.get(i).equals(args.get(i)))
                        return false;
                return true;
            }
            return false;
        }

        @Override
        public int hashCode() {
            int h = "&".hashCode();
            h += name.hashCode();
            for (Type t : args) {
                h += t.hashCode();
            }
            return h;
        }

    }

    public static class RefType extends Type {
        private final String name;
        final List<Type> args;

        public RefType(String s, List<Type> a) {
            name = s;
            args = a;
        }

        public RefType(ConstantPoolParser f, ByteBuffer r) throws InvalidClassFileException {
            name = f.getCPUtf8(r.getCPIndex());
            int n = r.getCount();
            args = new ArrayList<Type>(n);
            for (int i = 0; i < n; i++) {
                args.add(f.getCPType(r.getCPIndex()));
            }
        }

        public String toString() {
            return getName() + (args.isEmpty() ? "" : args.toString());
        }

        public String getName() {
            return name;
        }

        @Override
        public String desc() {
            StringBuilder sb = new StringBuilder();
            sb.append("*");
            sb.append(name);
            if (args.size() > 0) {
                sb.append("[");
                for (Type t : args) {
                    sb.append(t.desc());
                }
                sb.append("]");
            }
            sb.append(";");
            return sb.toString();
        }

        public void writeInto(ConstantPool cp, ByteBuffer w) {
            w.addByte(Type_Ref);
            w.addCPIndex(cp.addCPUtf8(name));
            w.addCount(args.size());
            for (Type t : args)
                w.addCPIndex(cp.addCPType(t));
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof RefType) {
                RefType t = (RefType) o;
                if (!t.name.equals(name))
                    return false;
                if (t.args.size() != args.size())
                    return false;
                for (int i = 0; i < args.size(); i++)
                    if (!t.args.get(i).equals(args.get(i)))
                        return false;
                return true;
            }
            return false;
        }

        @Override
        public int hashCode() {
            int h = "*".hashCode();
            h += name.hashCode();
            for (Type t : args) {
                h += t.hashCode();
            }
            return h;
        }

    }

    public static class StructType extends Type {
        final String name;
        final List<Type> args;

        public StructType(String s, List<Type> a) {
            name = s;
            args = a;
        }

        public StructType(ConstantPoolParser f, ByteBuffer r) throws InvalidClassFileException {
            name = f.getCPUtf8(r.getCPIndex());
            int n = r.getCount();
            args = new ArrayList<Type>(n);
            for (int i = 0; i < n; i++) {
                args.add(f.getCPType(r.getCPIndex()));
            }
        }

        public String toString() {
            return name + "!" + (args.isEmpty() ? "" : args.toString());
        }

        @Override
        public String desc() {
            StringBuilder sb = new StringBuilder();
            sb.append("!");
            sb.append(name);
            if (args.size() > 0) {
                sb.append("[");
                for (Type t : args) {
                    sb.append(t.desc());
                }
                sb.append("]");
            }
            sb.append(";");
            return sb.toString();
        }

        public void writeInto(ConstantPool cp, ByteBuffer w) {
            w.addByte(Type_Struct);
            w.addCPIndex(cp.addCPUtf8(name));
            w.addCount(args.size());
            for (Type t : args)
                w.addCPIndex(cp.addCPType(t));
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof StructType) {
                StructType t = (StructType) o;
                if (!t.name.equals(name))
                    return false;
                if (t.args.size() != args.size())
                    return false;
                for (int i = 0; i < args.size(); i++)
                    if (!t.args.get(i).equals(args.get(i)))
                        return false;
                return true;
            }
            return false;
        }

        @Override
        public int hashCode() {
            int h = "!".hashCode();
            h += name.hashCode();
            for (Type t : args) {
                h += t.hashCode();
            }
            return h;
        }
    }

    public static class ParamType extends Type {
        final String name;

        public ParamType(String s) {
            name = s;
        }

        public ParamType(ConstantPoolParser f, ByteBuffer r) throws InvalidClassFileException {
            name = f.getCPUtf8(r.getCPIndex());
        }

        public String getName() {
            return name;
        }

        public String toString() {
            return name;
        }

        @Override
        public String desc() {
            StringBuilder sb = new StringBuilder();
            sb.append("?");
            sb.append(name);
            sb.append(";");
            return sb.toString();
        }

        public void writeInto(ConstantPool cp, ByteBuffer w) {
            w.addByte(Type_Param);
            w.addCPIndex(cp.addCPUtf8(name));
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof ParamType) {
                ParamType t = (ParamType) o;
                if (!t.name.equals(name))
                    return false;
                return true;
            }
            return false;
        }

        @Override
        public int hashCode() {
            int h = 2389;
            h += name.hashCode();
            return h;
        }
    }

    static int size(ConstantPoolParser f, ByteBuffer b) throws InvalidClassFileException {
        int o = b.offset();
        byte k = b.getByte(o);
        switch (k) {
        case Type_Constrained:
            // tag, type, constraint
            return 1 + 2 * 4;
        case Type_Fun: {
            int n = b.getCount(o + 1);
            // tag, count, args, ret
            return 1 + 4 + n * 4 + 4;
        }
        case Type_Param:
            return 1 + 4;
        case Type_Ref:
        case Type_Scoped:
        case Type_Struct: {
            int n = b.getCount(o + 5);
            // tag, name, count, args
            return 1 + 4 + 4 + n * 4;
        }
        case Type_Void:
            return 1;
        }

        throw new InvalidClassFileException(b.offset(), "base type");
    }

    static Type readFrom(ConstantPoolParser f, ByteBuffer b) throws InvalidClassFileException {
        byte k = b.getByte();
        switch (k) {
        case Type_Constrained:
            return new ConstrainedType(f, b);
        case Type_Fun:
            return new FunType(f, b);
        case Type_Param:
            return new ParamType(f, b);
        case Type_Ref:
            return new RefType(f, b);
        case Type_Scoped:
            return new ScopedType(f, b);
        case Type_Struct:
            return new StructType(f, b);
        case Type_Void:
            return new VoidType();
        }

        throw new InvalidClassFileException(b.offset(), "base type");
    }

    static Type parse(ByteBuffer b, int offset, int len) throws InvalidClassFileException {
        b.seek(offset);
        Type t = parse(b);
        if (b.offset() != offset + len)
            throw new InvalidClassFileException(offset, "bad type");
        return t;
    }

    static String parseClassName(ByteBuffer b) throws InvalidClassFileException {
        String s = b.getUtf8ToDelimiter("[(;");
        if (s.length() > 0) {
            s = s.substring(0, s.length() - 1);
            b.skip(-1);
        }
        else {
            throw new InvalidClassFileException(b.offset(), "bad class name");
        }
        return s;
    }

    static Type parse(ByteBuffer b) throws InvalidClassFileException {
        int offset = b.offset();
        char k = (char) b.getByte();

        switch (k) {
        case 'V': {
            return new VoidType();
        }
        case '*': {
            // *C[T1;T2];
            String s = parseClassName(b);
            char last = (char) b.getByte();
            if (last == '[') {
                List<Type> args = parseTypeList(b, ']');
                last = (char) b.getByte();
                if (last == ']') {
                    last = (char) b.getByte();
                    if (last == ';')
                        return new RefType(s, args);
                }
            }
            else if (last == ';') {
                return new RefType(s, Collections.EMPTY_LIST);
            }
            break;
        }
        case '!': {
            // !C[T1;T2];
            String s = parseClassName(b);
            char last = (char) b.getByte();
            if (last == '[') {
                List<Type> args = parseTypeList(b, ']');
                last = (char) b.getByte();
                if (last == ']') {
                    last = (char) b.getByte();
                    if (last == ';')
                        return new StructType(s, args);
                }
            }
            else if (last == ';') {
                return new StructType(s, Collections.EMPTY_LIST);
            }
            break;
        }
        case '&': {
            // &C[T1;T2];
            String s = parseClassName(b);
            char last = (char) b.getByte();
            if (last == '[') {
                List<Type> args = parseTypeList(b, ']');
                last = (char) b.getByte();
                if (last == ']') {
                    last = (char) b.getByte();
                    if (last == ';')
                        return new ScopedType(s, args);
                }
            }
            else if (last == ';') {
                return new ScopedType(s, Collections.EMPTY_LIST);
            }
            break;
        }
        case '(': {
            // (T1;T2;)T3;
            List<Type> args = parseTypeList(b, ')');
            char last = (char) b.getByte();
            if (last == ')') {
                Type ret = parse(b);
                return new FunType(args, ret);
            }
        }
        case '%': {
            // %T1;T2;(...)
            Type t1 = parse(b);
            char last = (char) b.getByte();
            if (last == ';') {
                Type t2 = parse(b);
                last = (char) b.getByte();
                if (last == ';') {
                    return new AnnotatedType(t1, t2);
                }
            }
        }
        case '?': {
            String s = parseClassName(b);
            char last = (char) b.getByte();
            if (last == ';')
                return new ParamType(s);
        }
        }

        throw new InvalidClassFileException(offset, "bad type " + (char) k);
    }

    private static List<Type> parseTypeList(ByteBuffer b, char end) throws InvalidClassFileException {
        List<Type> types = new ArrayList<Type>();
        while (true) {
            char lookahead = (char) b.getByte(b.offset());
            if (lookahead == end)
                break;
            Type t = parse(b);
            types.add(t);
        }
        return types;
    }

    public static Type parse(String s) throws InvalidClassFileException {
        return parse(new ByteBuffer(s.getBytes()));
    }
}
