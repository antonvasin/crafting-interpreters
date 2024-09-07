BUILD_DIR := build
TOOL_SOURCES := tool/pubspec.lock $(shell find tool -name '*.dart')
BUILD_SNAPSHOT := $(BUILD_DIR)/build.dart.snapshot
TEST_SNAPSHOT := $(BUILD_DIR)/test.dart.snapshot

default: jlox

# Remove all build outputs and intermediate files.
clean:
	@ rm -rf $(BUILD_DIR)
	@ rm -rf gen

# Compile and run the AST generator.
generate_ast:
	@ $(MAKE) -f java.make DIR=jlox PACKAGE=tool
	@ java -cp build/jlox com.craftinginterpreters.tool.GenerateAst \
			jlox/com/craftinginterpreters/lox

# Compile the Java interpreter .java files to .class files.
jlox: generate_ast
	@ $(MAKE) -f java.make DIR=jlox PACKAGE=lox

test: jlox
	@ java -cp build/jlox com.craftinginterpreters.lox.Lox < examples/basic.lox

repl: jlox
	@ java -cp build/jlox com.craftinginterpreters.lox.Lox

run_generate_ast = @ java -cp build/gen/$(1) \
			com.craftinginterpreters.tool.GenerateAst \
			gen/$(1)/com/craftinginterpreters/lox

.PHONY: jlox
