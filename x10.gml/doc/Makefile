#
#  This file is part of the X10 project (http://x10-lang.org).
#
#  This file is licensed to You under the Eclipse Public License (EPL);
#  You may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#      http://www.opensource.org/licenses/eclipse-1.0.php
#
#  (C) Copyright IBM Corporation 2006-2014.
#

doc_path	= html
src_path 	= ../src

XDOC = x10doc

docsrc=$(shell find $(src_path) -type f -iname '*.x10')

doc		: $(docsrc)
		$(XDOC) -d $(doc_path) -sourcepath $(src_path) $^

eachdoc	: $(docsrc)
		$(foreach s, $(docsrc), $(MAKE) x10src=$(s) html;) 

#clean	:
#		rm -rf html

.PHONY: doc chk_doc_path alldoc html
