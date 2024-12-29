package re.rule.refactor.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

import com.github.javaparser.JavaParser;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.resolution.SymbolResolver;

public class Model {
    private Path p0Path;
    private Path p1Path;
    private CompilationUnit cu;
    private List<Path> p0SourceCodeList;
    private List<Path> p0MiscList;

    public Model(String p0Path, String p1Path) {
        this.p0Path = Paths.get(p0Path);
        this.p1Path = Paths.get(p1Path);
        this.p0SourceCodeList = new ArrayList<>();
        this.p0MiscList = new ArrayList<>();
    }

    private void addBeforeStatement(Statement s, Statement newS) {
        BlockStmt parentBlock = (BlockStmt) s.getParentNode().orElseThrow();
        NodeList<Statement> ss = parentBlock.getStatements();
        int index = ss.indexOf(s);
        ss.add(index, newS);
    }

    public void parse(Path f0) throws FileNotFoundException {
        {
//            String RULE = "[1 null assignment] ";
//            cu.findAll(AssignExpr.class).stream()
//                    .filter(
//                            lambda -> lambda.getValue().toString().equals("null")
//                    )
//                    .filter(
//                            lambda -> lambda.getTarget().getSymbolResolver().calculateType()
//                    )
//                    .forEach(
//                            lambda -> {
//                                System.out.println(RULE + lambda);
////                            if (lambda.getValue() == StaticJavaParser.parseExpression("null")) {
////                                lambda.setValue(StaticJavaParser.parseExpression("new Object()"));
////                            }
//                            }
//                    );

//            Apply the regex search and replacement here:
//            Pattern: (^.* Object .* = )null;
//            Replace: $1new Object();
        }

        cu = StaticJavaParser.parse(f0.toFile());
        {
//        Rule: array index checking
            String RULE = "[1 array access] ";
            cu.findAll(ArrayAccessExpr.class).stream()
                    .filter(lambda ->
                            lambda.getParentNode().isPresent()
                    )
                    .filter(
                            lambda -> lambda.findAncestor(Statement.class).isPresent()
                    )
                    .forEach(lambda -> {
//                    Debug output
                        System.out.println(RULE + lambda.toString());
//                    System.out.println("[statement] " + theStatement.toString());
                        String theIndex = lambda.getIndex().toString();
                        String theArray = lambda.getName().toString();
                        Statement theStatement = lambda.findAncestor(Statement.class).get();

//                    Expression e = theStatement.asExpressionStmt().getExpression();
//                    if (e instanceof AssignExpr) {
//                        AssignExpr newE = (AssignExpr) e.clone();
//                        newE.setValue(StaticJavaParser.parseExpression("null"));
//                        addBeforeStatement(theStatement, new ExpressionStmt(newE));
//                    }

//                    Here a special case needs consideration:
//                    if the statement is a new declaration, it should be refactored as
//                    first declared and then assigned in if statement.
//                    BlockStmt newBlock = new BlockStmt();
                        IfStmt newStatement = new IfStmt();
                        newStatement.setCondition(
                                StaticJavaParser.parseExpression(
                                        String.format("%s >= 0 && %s < %s.length && %s != null", theIndex, theIndex, theArray, theArray)
                                )
                        );

                        newStatement.setThenStmt(new BlockStmt().addStatement(theStatement.clone()));
//                    newBlock.addStatement(newStatement);

                        System.out.println(RULE + newStatement.toString());

                        theStatement.replace(newStatement);
                    });
        }
    }

    public void write(Path f1) {
        try {
//            Do not use the commented statement, it does not full overwrite existing files.
//            Files.writeString(f1, cu.toString(), StandardOpenOption.WRITE);
            FileWriter f = new FileWriter(f1.toString(), false);
            f.write(cu.toString());
            f.close();
        } catch (IOException e) {
            try {
                boolean trash;
                trash = f1.toFile().getParentFile().mkdirs();
                trash = f1.toFile().createNewFile();
            } catch (IOException ee) {
                System.out.println(ee);
            }
        }

//        debug block below
        System.out.println(f1);
    }

    public void launch() throws IOException {
//        Read all the files under source directory,
//        parse each file and manipulate the code inside.
//        May do multithreading in future,
//        because there might be very many files.
        try (Stream<Path> paths = Files.walk(p0Path)) {
            paths
                    .forEach((lambda) -> {
                        if (lambda.toFile().isFile()) {
                            if (lambda.toString().matches(".*?\\.java")) {
                                p0SourceCodeList.add(lambda);
                            } else {
                                p0MiscList.add(lambda);
                            }
                        }
                    });
        }

//        debug block below
//        System.out.println(p0List.size());
//        for (int i = 0; i < p0List.size(); i++) {
//            System.out.println(p0List.get(i));
//        }

        for (Path f0 : p0SourceCodeList) {
            Path f1 = Paths.get(p1Path.toString(), f0.subpath(p0Path.getNameCount(), f0.getNameCount()).toString());
            parse(f0);
            write(f1);
        }

        for (Path f0 : p0MiscList) {
            Path f1 = Paths.get(p1Path.toString(), f0.subpath(p0Path.getNameCount(), f0.getNameCount()).toString());
            copyFile(f0, f1);
        }
    }

    public static void copyFile(Path f0, Path f1) {
        try {
            File f = new File(f1.toString());
            Scanner Reader = new Scanner(f0);
            while (Reader.hasNextLine()) {
                String data = Reader.nextLine();
            }
            Reader.close();

//            System.out.println(f1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}