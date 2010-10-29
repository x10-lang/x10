package x10.parser;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
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
    private static int MAX_DEPTH = 14;
    public static void main(String[] args) throws IOException {
        if (args.length!=2) {
            System.err.println("You need to run AutoGenSentences with two arguments: GRAMMAR_FILE OUTPUT_FILE");
            System.exit(-1);
        }
        //only-grammar-productions.txt auto-gen-sentences.txt
        new AutoGenSentences(args);
    }
    AutoGenSentences(String[] args) throws IOException {
        ArrayList<String> grammarFile = readFile(new File(args[0]));
        File output = new File(args[1]);
        String currProd = null;
        int lineNum = 0;
        for (String line : grammarFile) {
            lineNum++;
            StringTokenizer tokenizer = new StringTokenizer(line);
            final String first = tokenizer.nextToken();
            if (!first.equals("|")) {
                currProd = first;
                String next = tokenizer.nextToken();
                assert next.equals("::=") : next;
                assert !grammar.containsKey(currProd) : currProd;
                grammar.put(currProd, new ArrayList<ArrayList<String>>());
            }
            assert currProd!=null;
            //ImportDeclarations PackageDeclaration $misplacedPackageDeclaration ImportDeclarationsopt $misplacedImportDeclarations TypeDeclarationsopt
            ArrayList<String> terms = new ArrayList<String>();
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                if (token.charAt(0)=='\'') {
                    // we escaped some tokens, like '|'  '%'  '-->'
                    assert token.charAt(token.length()-1)=='\'' : token;
                    token = token.substring(1,token.length()-1);
                }
                if (token.charAt(0)=='$') continue; // also includes $Empty
                terms.add(token);
            }
            // can be empty: assert terms.size()>=1 : currProd;
            grammar.get(currProd).add(terms);
        }
        //root is CompilationUnit
        final HashSet<String> res = gen(findRoot(), MAX_DEPTH);
        assert EMPTY_STR.size()==1 : EMPTY_STR;
        writeFile(output,res);
    }

    HashMap<String, ArrayList<ArrayList<String>>> grammar = new HashMap<String, ArrayList<ArrayList<String>>>();

    HashSet<String> EMPTY_STR = new HashSet<String>(Collections.singleton(""));

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

    String findRoot() {
        HashSet<String> res = new HashSet<String>(grammar.keySet());
        for (ArrayList<ArrayList<String>> products : grammar.values())
            for (ArrayList<String> prod : products)
                for (String s : prod)
                    res.remove(s);
        assert res.size()==1 : res;
        return res.iterator().next();
    }
    HashSet<String> getLiterals() {
        HashSet<String> res = new HashSet<String>();
        for (ArrayList<ArrayList<String>> products : grammar.values())
            for (ArrayList<String> prod : products)
                for (String s : prod)
                    if (isLiteral(s)) {
                        genLiteral(s); // to test it
                        res.add(s);
                    }
        return res;                    
    }

    ArrayList<String> readFile(File f) throws IOException {
        final BufferedReader in = new BufferedReader(new FileReader(f));
        ArrayList<String> res = new ArrayList<String>();
        String line;
        while ((line=in.readLine())!=null) {
            line = line.trim();
            if (!line.equals(""))
                res.add(line);
        }
        in.close();
        return res;
    }
    void writeFile(File f, Collection<String> lines) throws IOException {
        final BufferedWriter out = new BufferedWriter(new FileWriter(f));
        for (String s : lines) {
            out.write(s);
            out.write('\n');
        }
        out.close();
    }
}