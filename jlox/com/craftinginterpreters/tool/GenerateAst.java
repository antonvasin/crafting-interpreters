package com.craftinginterpreters.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      System.err.println("Usage: generate_ast <output directory>");
      System.exit(64);
    }

    String outputDir = args[0];

    defineAst(outputDir, "Expr", Arrays.asList(
        //
        "Binary   : Expr left, Token operator, Expr right",
        //
        "Grouping : Expr expression",
        //
        "Literal  : Token operator, Expr right",
        //
        "Unary    : Token operator, Expr right"));
  }

  private static void defineAst(String outputDir, String baseName, List<String> types) throws IOException {
    String path = outputDir + "/" + baseName + ".java";
    PrintWriter writer = new PrintWriter(path, "UTF-8");

    writer.println("package com.craftinginterpreters.lox;");
    writer.println();
    writer.println("import java.util.List;");
    writer.println();

    // Base class
    writer.println("abstract class " + baseName + " {");
    for (String type : types) {
      String className = type.split(":")[0].trim();
      String fields = type.split(":")[1].trim();
      defineType(writer, baseName, className, fields);
    }
    writer.println();
    writer.println("}");
    writer.close();
  }

  public static void defineType(PrintWriter writer, String baseName, String className, String fieldList) {
    // Declaration
    // 1 level indent
    writer.println("  static class " + className + " extends " + baseName + " {");

    // Constructor
    // 2 level indent
    writer.println("    " + className + "(" + fieldList + ") {");

    // Params
    String[] fields = fieldList.split(", ");
    for (String field : fields) {
      String name = field.split(" ")[1];
      // 3 level indent
      writer.println("      this." + name + " = " + name + ";");
    }

    // 2 level indent
    writer.println("    }");

    // Fields
    writer.println();
    for (String field : fields) {
      // 2 level indent
      writer.println("    final " + field + ";");
    }

    // Close
    // 1 level indent
    writer.println("  }");
    writer.println();
  }
}
