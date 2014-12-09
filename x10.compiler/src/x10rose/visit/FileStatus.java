package x10rose.visit;

import polyglot.ast.SourceFile_c;

public class FileStatus {
        private SourceFile_c file;
        private boolean isDeclHandled;
        private boolean isDefHandled;

        public FileStatus(SourceFile_c filename) {
            file = filename;
        }

        public void handleDecl() {
            isDeclHandled = true;
        }

        public void handleDef() {
            isDefHandled = true;
        }

        public boolean isDeclHandled() {
            return isDeclHandled;
        }

        public boolean isDefHandled() {
            return isDefHandled;
        }
}
