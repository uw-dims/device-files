SHELL = /bin/bash

OS = Linux

PWD = $(shell pwd)

ifndef JDK_HOME
$(error JDK_HOME make variable missing.  Copy $(PWD)/Makefile.env.sample to $(PWD)/Makefile.env and edit)
endif

# The package and artifact name expected passed down by the Maven
# invocation of this build. We supply some bogus ones if not aleady
# set.  This will obviously lead to failure at runtime!
PREFIX  ?= P
LIBNAME ?= L

BASEDIR = $(abspath $(NATIVEHOME)/../../..)

CFLAGS += -Wall -Werror -fPIC -std=c99

CPPFLAGS += -I$(BASEDIR)/target/native/javah

CPPFLAGS += -I$(JDK_HOME)/include -I$(JDK_HOME)/include/linux

LDFLAGS += -shared

MAINSRCS = $(shell cd $(NATIVEHOME)/Linux && ls *.c)

MAINOBJS = $(MAINSRCS:.c=.o)

VPATH = $(NATIVEHOME)/Linux

LINK.so = $(CC) $(LDFLAGS) $(LDLIBS)

# the name of the library to build
LIB		= lib$(LIBNAME).so

# link the C objects into a shared library
$(LIB): $(MAINOBJS)
	$(CC) $(LDFLAGS) $^ $(LDLIBS) $(OUTPUT_OPTION)

PREFIXASPATH = $(subst .,/,$(PREFIX))

TARGET_DIR = $(BASEDIR)/src/main/resources/$(PREFIXASPATH)/native/$(OS)/$(ARCH)

default: install

install: $(LIB)
	@mkdir -p $(TARGET_DIR)
	cp $(LIB) $(TARGET_DIR)

clean:
	-rm $(LIB) *.o

init: Makefile.env

Makefile.env : Makefile.env.sample
	cp Makefile.env.sample Makefile.env

############################## Test/debug targets #######################

# eof
