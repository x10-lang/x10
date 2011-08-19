package x10.parser;

import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.HashSet;
import java.util.Collections;
import java.util.Random;
import java.util.Collection;
import java.util.Set;
import java.util.Map;

/*
Not used:
all the rules at the beginning including
MethodPrimaryPrefix ::= Primary . ErrorId $ErrorId
MethodSuperPrefix ::= super . ErrorId $ErrorId


PlaceType ::= PlaceExpression
PlaceExpressionSingleListopt ::= $Empty
                               | PlaceExpressionSingleList


delete:
-> 
duplications at the beginning
 */
public class AutoGenSentences {
    private static int MAX_DEPTH = 10;
    private static String CompilationUnit = "CompilationUnit";
    private static String TypeDeclaration = "TypeDeclaration";

    static class Rule extends ArrayList<String> {
        private final String symbol;
        private final int line;
        private final ArrayList<String> names = new ArrayList<String>();
        private final ArrayList<String> code = new ArrayList<String>();

        Rule(String symbol, int line) {
            this.symbol = symbol;
            this.line = line;
        }
    }
    public static void main(String[] args) {
        if (args.length!=2) {
            System.err.println("You need to run AutoGenSentences with two arguments: GRAMMAR_FILE OUTPUT_FILE\nFor example: java AutoGenSentences x10.g Output.x10\n");
            System.exit(-1);
        }
        //only-grammar-productions.txt auto-gen-sentences.txt
        new AutoGenSentences(args);
    }
    AutoGenSentences(String[] args) {
        ArrayList<String> grammarFile = readFile(new File(args[0]));
        ArrayList<String> newFile = new ArrayList<String>();
        String currProd = null;
        int lineNum = -1;
        try {
            boolean isTypes = false;
            Map<String, ArrayList<Rule>> rules = null;
            boolean inJavaCode = false;
            for (String line : grammarFile) {
                lineNum++;
                if (!isTypes) newFile.add(line);
                line = line.trim();
                if (line.equals("")) continue;
                
                if (line.equals("%Rules")) {
                    rules = grammar;
                    continue;
                }
                if (line.equals("%Types")) {
                    isTypes = true;
                    rules = CollectionFactory.newHashMap();
                    continue;
                }
                if (line.equals("%End")) {
                    if (isTypes) {
                        isTypes = false;
                        // assert all productions are of size 1
                        for (String type : rules.keySet())
                            for (ArrayList<String> prod : rules.get(type)) {
                                assert prod.size()==1;
                                String nonTerminal = prod.get(0);
                                assert !isLiteral(nonTerminal);
                                assert !types.containsKey(nonTerminal);
                                types.put(nonTerminal,type);
                            }
                    }
                    rules = null;
                }
                if (rules==null) continue;

                if (line.startsWith("--")) continue; // ignore comments
                // Ignore: /.$NullAction./
                if (line.equals("/.$NullAction./")) continue;
                if (line.startsWith("/.")) {
                    assert line.equals("/.$BeginJava") : line;
                    inJavaCode = true;
                    ArrayList<Rule> prods = rules.get(currProd);
                    final int prodNum = prods.size() - 1;
                    final Rule rule = prods.get(prodNum);
                    ArrayList<String> ruleArgs = new ArrayList<String>();
                    int k=0;
                    for (String name : rule.names) {
                        String id = rule.get(k++);
                        if (hasArg(id,name))
                            ruleArgs.add(name);
                    }
                    newFile.add("\t\t\tr.rule_"+rule.symbol+prodNum+"("+simpleJoin(ruleArgs,",")+");");
                    continue;
                }
                if (line.endsWith("./")) {
                    assert inJavaCode;
                    assert line.equals("./") || line.startsWith("$EndJava") : line;
                    inJavaCode = false;
                    continue;
                }
                if (inJavaCode) {
                    ArrayList<Rule> prods = rules.get(currProd);
                    final Rule rule = prods.get(prods.size() - 1);
                    int k=1;
                    for (String name : rule.names)
                        line = line.replace("$"+name,""+(k++));
                    line = line.replace("$sym_type","X10Parsersym");
                    if (!line.equals("$EndJava")) rule.code.add(line);
                    newFile.remove(newFile.size()-1);
                    continue;
                }

                StringTokenizer tokenizer = new StringTokenizer(line);
                final String first = tokenizer.nextToken();
                if (!first.equals("|")) {
                    currProd = first;
                    currProd = unescape(currProd);
                    String next = tokenizer.nextToken();
                    assert next.equals("::=") || next.equals("::=?") : next;
                }
                assert currProd!=null;
                ArrayList<Rule> prods = rules.get(currProd); // AssignmentExpression is stated in 2 different rules
                if (prods==null) {
                    prods = new ArrayList<Rule>();
                    rules.put(currProd, prods);
                }
                //ImportDeclarations PackageDeclaration$misplacedPackageDeclaration ImportDeclarationsopt$misplacedImportDeclarations TypeDeclarationsopt
                Rule terms = new Rule(currProd, lineNum);
                while (tokenizer.hasMoreTokens()) {
                    String token = tokenizer.nextToken();
                    if (token.equals("--")) break; // comments

                    if (token.equals("|")) {
                        prods.add(terms);
                        terms = new Rule(currProd, lineNum);
                        continue;
                    }
                    token = unescape(token);
                    assert token.charAt(0)!='$';
                    int indexDollar = token.indexOf('$');
                    String name = token;
                    if (indexDollar!=-1) {
                        name = token.substring(indexDollar+1);
                        token = token.substring(0,indexDollar); // ImportDeclarationsopt$misplacedImportDeclarations
                    }
                    if (token.equals("%Empty")) continue;
                    terms.add(token);
                    terms.names.add(name);
                }
                // can be empty: assert terms.size()>=1 : currProd;
                prods.add(terms);
            }
        } catch (Throwable e) {
            System.err.println("Error on line "+lineNum);
            e.printStackTrace();
        }

        // removing unused symbols and types
        findUsedSymbols(CompilationUnit);
        Set<String> unusedSymbols = CollectionFactory.newHashSet(grammar.keySet());
        unusedSymbols.removeAll(usedSymbols);
        if (unusedSymbols.size()>0) {
            System.out.println("Unused symbols are: "+unusedSymbols); 
            for (String s : unusedSymbols)
                grammar.remove(s);
        }
        Set<String> unusedTypes = CollectionFactory.newHashSet(types.keySet());
        unusedTypes.removeAll(usedSymbols);
        if (unusedTypes.size()>0) {
            System.out.println("Unused types are: "+unusedTypes);
            for (String s : unusedTypes)
                types.remove(s);            
        }

        final ArrayList<String> nonTerminals = getNonTerminals();
        System.out.println("Roots are: "+findRoots());
        System.out.println("Literals are: "+getLiterals());
        System.out.println("Non-terminals are: "+ nonTerminals);

        // consistency checks
        Set<String> nonTerminalsWithoutType = CollectionFactory.newHashSet(nonTerminals);
        nonTerminalsWithoutType.removeAll(types.keySet());
        assert nonTerminalsWithoutType.size()==0 : nonTerminalsWithoutType;
        assert grammar.keySet().containsAll(types.keySet());

        // printing output
        newFile.add("%Types");
        newFile.add("\tObject ::= "+simpleJoin(types.keySet()," | "));
        newFile.add("%End");

        File output = new File(args[1]);
        if (true) {
            if (false) printSingletons();
            if (false) printGrammar(CompilationUnit,CollectionFactory.<String>newHashSet());
            writeFile(output,newFile);

            for (ArrayList<Rule> prods : grammar.values()) {
                int prodNum = -1;
                for (Rule rule : prods) { prodNum++;
                    if (rule.code.size()>0) {
                        int k = 0;
                        ArrayList<String> ruleArgs = new ArrayList<String>();
                        ArrayList<String> castArgs = new ArrayList<String>();
                        for (String name : rule.names) {
                            String id = rule.get(k++);
                            if (hasArg(id,name)) {
                                ruleArgs.add("Object _"+name);
                                String type = types.get(id);
                                if (type==null) type = "IToken";
                                castArgs.add(type+" "+name+" = ("+type+") _"+name+";");
                            }
                        }
                        System.out.println("\t// Production: "+rule.symbol+" ::= "+ join(rule," "));
                        System.out.println("\tvoid rule_"+rule.symbol+prodNum+"("+simpleJoin(ruleArgs,", ")+") {");
                        for (String s : castArgs)
                            System.out.println("\t\t"+s);
                        for (String c : rule.code)
                            System.out.println("\t\t"+c);
                        System.out.println("\t}");
                    }
                }
            }
            return;            
        }
        //x10.g root is CompilationUnit, but we want to generate many TypeDeclaration
        printGrammar(TypeDeclaration,CollectionFactory.<String>newHashSet());

        final Set<String> res = gen(TypeDeclaration, MAX_DEPTH);
        assert EMPTY_STR.size()==1 : EMPTY_STR;

        writeFile(output,res);
    }
    private static String unescape(String token) {
        if (token.charAt(0)=='\'') {
            // we escaped some tokens, like '|'  '%'  '-->'
            assert token.charAt(token.length()-1)=='\'' : token;
            token = token.substring(1,token.length()-1);
        }
        return token;
    }

    final Map<String, ArrayList<Rule>> grammar = CollectionFactory.newHashMap();
    final Map<String, String> types = CollectionFactory.newHashMap();


    Set<String> EMPTY_STR = CollectionFactory.newHashSet(Collections.singleton(""));

    String join(Collection<String> arr, String sep) {
        if (arr.size()==0) return "%Empty";
        String res = "";
        for (String s : arr) {
            final char c = s.charAt(0);
            res = res + (res.equals("") ? "" : sep) + (Character.isLetterOrDigit(c) ? s : "'"+s+"'");
        }
        return res;
    }
    String simpleJoin(Collection<String> arr, String sep) {
        if (arr.size()==0) return "";
        String res = "";
        for (String s : arr) {
            res = res + (res.equals("") ? "" : sep) + s;
        }
        return res;
    }


    Map<String,Set<String>> graph = CollectionFactory.newHashMap();
    Map<String,Integer> visited = CollectionFactory.newHashMap();
    int currID = 0;
    void printSingletons() {
        // I want to make sure the singletons don't have cycles
        for (String symbol : grammar.keySet()) {
            final Set<String> set = CollectionFactory.newHashSet();
            for (ArrayList<String> prods : grammar.get(symbol)) {
                if (prods.size()==1) {
                    final String other = prods.get(0);
                    set.add(other);
                }
            }
            if (set.size()>0)
                graph.put(symbol, set);
        }
        // do a DFS and assert we do not have a cycle
        for (String symbol : graph.keySet())
            dfs(symbol);

        for (String symbol : graph.keySet()) {
            int id = visited.get(symbol);
            for (String child : graph.get(symbol)) {
                int id2 = visited.get(child);
                assert id2<id;
                System.out.println(symbol+"("+id+") -> "+ child+"("+id2+")");
            }
        }
    }
    void dfs(String v) {
        final Integer i = visited.get(v);
        assert i==null || i.intValue()!=-1;
        if (i!=null) return; // already visited
        visited.put(v,-1);
        final Set<String> children = graph.get(v);
        if (children==null) {
            //assert isLiteral(v); , e.g., DepNamedType
        } else {
            for (String child : children) {
                dfs(child);
            }
        }
        visited.put(v,currID++);
    }
    void printGrammar(String symbol, Set<String> alreadyPrinted) {
        if (alreadyPrinted.contains(symbol)) return;
        alreadyPrinted.add(symbol);
        ArrayList<Rule> prods = grammar.get(symbol);
        if (prods==null) {
            // literal
            genLiteral(symbol); // for testing
            return;
        }
        System.out.println(symbol+" ::= " + (prods.size()==0 ? "" : join(prods.get(0)," ")));
        for (int i=1; i<prods.size(); i++)
            System.out.println("\t| "+join(prods.get(i)," "));

        for (ArrayList<String> prod : prods)
            for (String s : prod)
                printGrammar(s,alreadyPrinted);
    }

    Set<String> genProd(ArrayList<String> prod, int depth) {
        final int prodNum = prod.size();
        if (prodNum==0) return EMPTY_STR;
        ArrayList<Set<String>> acc = new ArrayList<Set<String>>(prodNum);
        int size = 0;
        for (String s : prod) {
            Set<String> set = gen(s,depth-1);
            if (set==null) return null;
            size += set.size();
            acc.add(set);
        }
        if (prodNum ==1) return acc.get(0);

        ArrayList<String[]> acc2 = new ArrayList<String[]>(prodNum);
        for (Set<String> s : acc)
            acc2.add(s.toArray(new String[s.size()]));

        // should be the cartesian prod of all sets, but it is too big, so we sum the sets
        size *= 2;
        Set<String> res = CollectionFactory.newHashSet(2*size);
        Random r = new Random();
        for (int i=0; i<size; i++) {
            StringBuilder s = new StringBuilder();
            for (String[] arr : acc2) {
                s.append(arr[r.nextInt(arr.length)]);
            }
            res.add(s.toString());
        }
        return res;
    }
    Set<String> gen(String rule, int depth) {
        Set<String> res = CollectionFactory.newHashSet();
        ArrayList<Rule> prods = grammar.get(rule);
        if (prods==null) {
            // literal
            res.add(genLiteral(rule)+" ");
        } else {
            if (depth<=0) return null;
            for (ArrayList<String> prod : prods) {
                Set<String> acc = genProd(prod,depth);
                if (acc!=null) {
                    assert acc.size()>0 : rule;
                    res.addAll(acc);
                }
            }
            if (res.size()==0) return null;
        }
        return res;
    }

    boolean isLiteral(String s) { return !grammar.containsKey(s); }

    static boolean random() { return Math.random()<0.5; }
    static boolean hasArg(String id, String name) {
        return !isLiteral2(id);
    }
    static boolean isLiteral2(String s) { // because we need to know if something is a literal before we parse the entire file
        return genLiteral2(s)!=null;
    }
    static String genLiteral2(String s) {
        char first = s.charAt(0);
        if (first>='A' && first<='Z') {
            // special literal
            // special literals (all start with uppercase):
            if (s.equals("UnsignedIntegerLiteral")) {
                return random() ? "0u" : "1u";
            } else if (s.equals("UnsignedLongLiteral")) {
                return random() ? "0ul" : "1ul";
            } else if (s.equals("IntegerLiteral")) {
                return random() ? "0" : "1";
            } else if (s.equals("LongLiteral")) {
                return random() ? "0l" : "1l";
            } else if (s.equals("FloatingPointLiteral")) {
                return random() ? "0.0f" : "1.1f";
            } else if (s.equals("DoubleLiteral")) {
                return random() ? "0.0" : "1.1";
            } else if (s.equals("StringLiteral")) {
                return random() ? "\"\"" : "\"a\"";
            } else if (s.equals("CharacterLiteral")) {
                return random() ? "' '" : "'a'";
            } else if (s.equals("IDENTIFIER")) {
                return random() ? "x" : "y";
            } else if (s.equals("ErrorId")) {
                return "ERR";
            } else {
                return null;
            }
        }
        return s;

    }
    static String genLiteral(String s) {
        String res = genLiteral2(s);
        assert res!=null;
        return res;
    }


    Set<String> usedSymbols = CollectionFactory.newHashSet();
    void findUsedSymbols(String v) {
        if (usedSymbols.contains(v)) return;
        usedSymbols.add(v);
        final ArrayList<Rule> prods = grammar.get(v);
        if (prods==null) return;
        for (ArrayList<String> prod : prods)
            for (String s : prod)
                findUsedSymbols(s);
    }

    Set<String> findRoots() {
        Set<String> res = CollectionFactory.newHashSet(grammar.keySet());
        for (ArrayList<Rule> products : grammar.values())
            for (ArrayList<String> prod : products)
                for (String s : prod)
                    res.remove(s);
        return res;
    }
    ArrayList<String> getNonTerminals() {
        ArrayList<String> res = new ArrayList<String>(grammar.keySet());
        res.removeAll(getLiterals());
        Collections.sort(res);
        return res;
    }
    ArrayList<String> getLiterals() {
        Set<String> res = CollectionFactory.newHashSet();
        for (ArrayList<Rule> products : grammar.values())
            for (ArrayList<String> prod : products)
                for (String s : prod)
                    if (isLiteral(s)) {
                        genLiteral(s); // to test it
                        res.add(s);
                    }

        ArrayList<String> sorted = new ArrayList<String>(res);
        Collections.sort(sorted);
        return sorted;                    
    }

    public static ArrayList<String> readFile(File f) {
        try {
            final BufferedReader in = new BufferedReader(new FileReader(f));
            ArrayList<String> res = new ArrayList<String>();
            String line;
            while ((line=in.readLine())!=null) {
                res.add(line);
            }
            in.close();
            return res;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static final String NL = System.getProperty("line.separator");
    public static void writeFile(File f, Collection<String> lines)  {
        try {
            final BufferedWriter out = new BufferedWriter(new FileWriter(f));
            for (String s : lines) {
                out.write(s);
                out.write(NL);
            }
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}