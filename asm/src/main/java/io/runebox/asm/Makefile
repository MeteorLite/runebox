# All files in the distribution of BLOAT (Bytecode Level Optimization and
# Analysis tool for Java(tm)) are Copyright 1997-2001 by the Purdue
# Research Foundation of Purdue University.  All rights reserved.
# 
# This library is free software; you can redistribute it and/or
# modify it under the terms of the GNU Lesser General Public
# License as published by the Free Software Foundation; either
# version 2.1 of the License, or (at your option) any later version.
#
# This library is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public
# License along with this library; if not, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA


SUBDIRS = util \
	  reflect \
	  file \
	  editor \
	  inline \
	  tree \
	  cfg \
	  ssa \
	  tbaa \
	  trans \
	  diva \
	  codegen \
	  context \
	  decorate \
	  optimize \
	  shrink \
	  strip \
	  dump \
	  tools \
	  benchmark

all:
	@for i in $(SUBDIRS) ""; do \
	    if [ "x$$i" != "x" ]; then \
		$(MAKE) -C $$i all; \
	    fi; \
	done

clean:
	@for i in $(SUBDIRS) ""; do \
	    if [ "x$$i" != "x" ]; then \
		$(MAKE) -C $$i clean; \
	    fi; \
	done

docs:
	javadoc -d ../../../../../docs -sourcepath ../../../.. \
	  io.runebox.asm.util \
	  io.runebox.asm.reflect \
	  io.runebox.asm.file \
	  io.runebox.asm.editor \
	  io.runebox.asm.tree \
	  io.runebox.asm.cfg \
	  io.runebox.asm.ssa \
	  io.runebox.asm.tbaa \
	  io.runebox.asm.trans \
	  io.runebox.asm.diva \
	  io.runebox.asm.codegen \
	  io.runebox.asm.context \
	  io.runebox.asm.decorate \
	  io.runebox.asm.optimize \
	  io.runebox.asm.shrink \
	  io.runebox.asm.strip \
	  io.runebox.asm.dump \
	  io.runebox.asm.benchmark
