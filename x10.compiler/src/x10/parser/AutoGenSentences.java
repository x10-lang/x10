package x10.parser;

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
        File output = new File(args[1]);
        String currProd = null;
        int lineNum = 0;
        try {
            boolean foundRules = false;
            boolean inJavaCode = false;
            for (String line : grammarFile) {
                line = line.trim();
                if (line.equals("")) continue;
                
                if (line.equals("%Rules")) {
                    foundRules = true;
                    continue;
                }
                if (!foundRules) continue;
                if (line.equals("%End")) break;

                if (line.startsWith("--")) continue; // ignore comments
                // Ignore: /.$NullAction./
                if (line.startsWith("/.")) {
                    inJavaCode = true;
                }
                if (line.endsWith("./")) {
                    assert inJavaCode;
                    inJavaCode = false;
                    continue;
                }
                if (inJavaCode) continue;


                lineNum++;
                StringTokenizer tokenizer = new StringTokenizer(line);
                final String first = tokenizer.nextToken();
                if (!first.equals("|")) {
                    currProd = first;
                    String next = tokenizer.nextToken();
                    assert next.equals("::=") || next.equals("::=?") : next;
                    // AssignmentExpression is stated in 2 different rules
                    if (!grammar.containsKey(currProd))
                        grammar.put(currProd, new ArrayList<ArrayList<String>>());
                }
                assert currProd!=null;
                //ImportDeclarations PackageDeclaration $misplacedPackageDeclaration ImportDeclarationsopt $misplacedImportDeclarations TypeDeclarationsopt
                ArrayList<String> terms = new ArrayList<String>();
                while (tokenizer.hasMoreTokens()) {
                    String token = tokenizer.nextToken();
                    if (token.equals("--")) break; // comments
                    
                    if (token.charAt(0)=='\'') {
                        // we escaped some tokens, like '|'  '%'  '-->'
                        assert token.charAt(token.length()-1)=='\'' : token;
                        token = token.substring(1,token.length()-1);
                    }
                    assert token.charAt(0)!='$';
                    int indexDollar = token.indexOf('$');
                    if (indexDollar!=-1) token = token.substring(0,indexDollar); // ImportDeclarationsopt$misplacedImportDeclarations
                    if (token.equals("%Empty")) continue;
                    terms.add(token);
                }
                // can be empty: assert terms.size()>=1 : currProd;
                grammar.get(currProd).add(terms);
            }
        } catch (Throwable e) {
            System.err.println("Error on line "+lineNum);
            e.printStackTrace();
        }

        // removing unused symbols
        findUsedSymbols(CompilationUnit);
        HashSet<String> unusedSymbols = new HashSet<String>(grammar.keySet());
        unusedSymbols.removeAll(usedSymbols);
        if (unusedSymbols.size()>0) {
            System.out.println("Unused symbols are: "+unusedSymbols); 
            for (String s : unusedSymbols)
                grammar.remove(s);
        }

        System.out.println("Roots are: "+findRoots());
        System.out.println("Literals are: "+getLiterals());
        if (true) {
            printSingletons();
            //printGrammar(CompilationUnit,new HashSet<String>());
            return;            
        }
        //x10.g root is CompilationUnit, but we want to generate many TypeDeclaration
        printGrammar(TypeDeclaration,new HashSet<String>());

        final HashSet<String> res = gen(TypeDeclaration, MAX_DEPTH);
        assert EMPTY_STR.size()==1 : EMPTY_STR;
        writeFile(output,res);
    }

    HashMap<String, ArrayList<ArrayList<String>>> grammar = new HashMap<String, ArrayList<ArrayList<String>>>();

    HashSet<String> EMPTY_STR = new HashSet<String>(Collections.singleton(""));

    String join(ArrayList<String> arr, String sep) {
        if (arr.size()==0) return "%Empty";
        String res = "";
        for (String s : arr) {
            final char c = s.charAt(0);
            res = res + (res.equals("") ? "" : sep) + (Character.isLetterOrDigit(c) ? s : "'"+s+"'");
        }
        return res;
    }


    HashMap<String,HashSet<String>> graph = new HashMap<String, HashSet<String>>();
    HashMap<String,Integer> visited = new HashMap<String,Integer>();
    int currID = 0;
    void printSingletons() {
        // I want to make sure the singletons don't have cycles
        for (String symbol : grammar.keySet()) {
            final HashSet<String> set = new HashSet<String>();
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
        final HashSet<String> children = graph.get(v);
        if (children==null) {
            //assert isLiteral(v); , e.g., DepNamedType
        } else {
            for (String child : children) {
                dfs(child);
            }
        }
        visited.put(v,currID++);
    }
    void printGrammar(String symbol, HashSet<String> alreadyPrinted) {
        if (alreadyPrinted.contains(symbol)) return;
        alreadyPrinted.add(symbol);
        ArrayList<ArrayList<String>> prods = grammar.get(symbol);
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

    HashSet<String> genProd(ArrayList<String> prod, int depth) {
        final int prodNum = prod.size();
        if (prodNum==0) return EMPTY_STR;
        ArrayList<HashSet<String>> acc = new ArrayList<HashSet<String>>(prodNum);
        int size = 0;
        for (String s : prod) {
            HashSet<String> set = gen(s,depth-1);
            if (set==null) return null;
            size += set.size();
            acc.add(set);
        }
        if (prodNum ==1) return acc.get(0);

        ArrayList<String[]> acc2 = new ArrayList<String[]>(prodNum);
        for (HashSet<String> s : acc)
            acc2.add(s.toArray(new String[s.size()]));

        // should be the cartesian prod of all sets, but it is too big, so we sum the sets
        size *= 2;
        HashSet<String> res = new HashSet<String>(2*size);
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
    HashSet<String> gen(String rule, int depth) {
        HashSet<String> res = new HashSet<String>();
        ArrayList<ArrayList<String>> prods = grammar.get(rule);
        if (prods==null) {
            // literal
            res.add(genLiteral(rule)+" ");
        } else {
            if (depth<=0) return null;
            for (ArrayList<String> prod : prods) {
                HashSet<String> acc = genProd(prod,depth);
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
    boolean random() { return Math.random()<0.5; }
    String genLiteral(String s) {
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
            } else {
                assert false : "Missing special literal="+s;
            }
        }
        return s;            
    }


    HashSet<String> usedSymbols = new HashSet<String>();
    void findUsedSymbols(String v) {
        if (usedSymbols.contains(v)) return;
        usedSymbols.add(v);
        final ArrayList<ArrayList<String>> prods = grammar.get(v);
        if (prods==null) return;
        for (ArrayList<String> prod : prods)
            for (String s : prod)
                findUsedSymbols(s);
    }

    HashSet<String> findRoots() {
        HashSet<String> res = new HashSet<String>(grammar.keySet());
        for (ArrayList<ArrayList<String>> products : grammar.values())
            for (ArrayList<String> prod : products)
                for (String s : prod)
                    res.remove(s);
        return res;
    }
    ArrayList<String> getLiterals() {
        HashSet<String> res = new HashSet<String>();
        for (ArrayList<ArrayList<String>> products : grammar.values())
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