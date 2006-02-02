$Headers
/.
        //
        // These functions are only needed in order to use a lexer as
        // a special purpose "diff" program. To do so, one can invoke
        // the main program below directly from the command line.
        //
        private static int LINES = 0,
                           TOKENS = 1;
        private static int differ_mode = LINES; // default
        private static String extension = "";
        private static int changeCount = 0,
                           insertCount = 0,
                           deleteCount = 0,
                           moveCount = 0;

        private static void compareFiles(String old_file, String new_file)
        {
            try
            {
                $action_type old_lexer, new_lexer;
                if (old_file.equals(""))
                {
                    char [] input_chars = new char[0];
                    old_lexer = new $action_type(input_chars, "null_file");
                }
                else old_lexer = new $action_type(old_file);

                if (new_file.equals(""))
                {
                    char [] input_chars = new char[0];
                    new_lexer = new $action_type(input_chars, "null_file");
                }
                else new_lexer = new $action_type(new_file);
    
                PrsStream old_stream = new PrsStream(old_lexer);
                old_lexer.lexer(old_stream);
    
                PrsStream new_stream = new PrsStream(new_lexer);
                new_lexer.lexer(new_stream);

                Differ diff = (differ_mode == TOKENS ? (Differ) new DifferTokens(old_stream, new_stream)
                                                     : (Differ) new DifferLines(old_stream, new_stream));
                diff.compare();

                if (diff.getChangeCount() > 0)
                {
                    diff.outputChanges();

                    changeCount += diff.getChangeCount();
                    insertCount += (diff.getInsertCount() + diff.getReplaceInsertCount());
                    deleteCount += (diff.getDeleteCount() + diff.getReplaceDeleteCount());
                    moveCount += diff.getMoveCount();
                }
            }
            catch (Exception e)
            {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        }

        private static void compareDirectories(java.io.File old_dir, java.io.File new_dir)
        {
            try
            {
                java.io.File old_file[] = old_dir.listFiles(),
                             new_file[] = new_dir.listFiles();
                HashMap old_map = new HashMap();
                for (int i = 0; i < old_file.length; i++)
                {
                    String name = old_file[i].getName();
                    if (old_file[i].isDirectory() || name.endsWith(extension))
                        old_map.put(name, old_file[i]);
                }

                for (int i = 0; i < new_file.length; i++)
                {
                    java.io.File file = (java.io.File) old_map.get(new_file[i].getName());
                    if (file != null)
                    {
                        old_map.remove(new_file[i].getName());

                        if (file.isDirectory() && new_file[i].isDirectory())
                             compareDirectories(file, new_file[i]);
                        else compareFiles(file.getPath(), new_file[i].getPath());
                    }
                    else if (new_file[i].isDirectory() ||
                             new_file[i].getName().endsWith(extension))
                    {
                        String s = new_file[i].getName() +
                                   " found in directory " + 
                                   new_dir.getPath() +
                                   " does not exist in directory " +
                                   old_dir.getPath();
                        System.err.println("*Warning: " + s);
                        
                        if (! new_file[i].isDirectory())
                            compareFiles("", new_file[i].getPath());
                    }
                }

                for (Iterator i = old_map.entrySet().iterator(); i.hasNext(); )
                {
                    Map.Entry e = (Map.Entry) i.next();
                    java.io.File file = (java.io.File) e.getValue();
                    
                    String s = file.getName() +
                               " not found in directory " +
                               new_dir.getPath();
                    System.err.println("*Warning: " + s);
                    
                    if (! file.isDirectory())
                        compareFiles(file.getPath(), "");
                }
            }
            catch (Exception e)
            {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        }

        public static void main(String[] args)
        {
            String new_file = null,
                   old_file = null;
            boolean help = false;

            int i;
            for (i = 0; i < args.length; i++)
            {
                if (args[i].charAt(0) == '-')
                {
                    if (args[i].equals("-ext"))
                         extension = (i + 1 < args.length ? args[++i] : "");
                    else if (args[i].equals("-h"))
                         help = true;
                    else if (args[i].equals("-l"))
                         differ_mode = LINES;
                    else if (args[i].equals("-t"))
                         differ_mode = TOKENS;
                }
                else break;
            }
            if (i < args.length) 
            {
                new_file = args[i++];
                old_file = new_file; // assume only one file is specified
            }
            if (i < args.length) 
                old_file = args[i++];
            for (; i < args.length; i++)
                System.err.println("Invalid argument: " + args[i]);

            if (help || (new_file == null &&  old_file == null))
            {
                System.out.println();
                System.out.println("Usage: diff [OPTION]... file1 [file2]");
                System.out.println("Compute stats for file1 or compare file1 to file2 statement by statement.");
                System.out.println();
                System.out.println("  -ext s -- if file1 and file2 are directories, compare only files that end\n" +
                                   "            with the extension (suffix) s.");
                System.out.println("  -h     -- print this help message");
                System.out.println("  -l     -- compare line by line instead of statement by statement");
                System.out.println("  -t     -- compare token by token instead of statement by statement");
            }
            else if (old_file.equals(new_file))
            {
                java.io.File old_dir = new java.io.File(old_file);
                // if (old_dir.isDirectory())
                //     computeStats(old_dir);
                // else computeStats(old_file);

                System.out.println("*** No difference ***");
                // System.out.println("    Number of files: " + fileCount);
                // System.out.println("    Number of lines: " + lineCount);
                // System.out.println("    Number of types (classes/interfaces): " + (classCount + interfaceCount) + " (" + classCount + "/" + interfaceCount + ")");
                // System.out.println("    Number of statements: " + statementCount);
                // System.out.println("    Number of braces (left/right): (" + leftBraceCount + "/" + rightBraceCount + ")");
            }
            else
            {
                java.io.File old_dir = new java.io.File(old_file),
                     new_dir = new java.io.File(new_file);
                if (old_dir.isDirectory() && new_dir.isDirectory())
                     compareDirectories(old_dir, new_dir);
                else compareFiles(old_file, new_file);

                if (changeCount == 0)
                    System.out.println("***** No difference *****");
                else
                {
                    System.out.println("***** " +
                                       changeCount +
                                       " different " +
                                       (changeCount == 1 ? "section" : "sections") + " *****");
                    System.out.println("    " + moveCount    + " statements moved");
                    System.out.println("    " + insertCount  + " statements inserted");
                    System.out.println("    " + deleteCount  + " statements deleted");
                }
            }

            return;
        }
./
$End