package re.rule.refactor.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

public class Model {
    private Path p0Path;
    private Path p1Path;
    private CompilationUnit cu;
    private List<Path> p0List;

    public Model(String p0Path, String p1Path) {
        this.p0Path = Paths.get(p0Path);
        this.p1Path = Paths.get(p1Path);
        this.p0List = new ArrayList<>();
    }

    public void parse(Path f0) throws FileNotFoundException {
        cu = StaticJavaParser.parse(f0.toFile());
    }

    public void write(Path f1) {
        try {
            Files.writeString(f1, cu.toString(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            try {
                f1.toFile().getParentFile().mkdirs();
                f1.toFile().createNewFile();
            } catch (IOException ee) {
                System.out.println(f1);
            }
        }
    }

    public void launch() throws IOException {
//        Read all the files under source directory,
//        parse each file and manipulate the code inside.
//        May do multithreading in future,
//        because there might be very many files.
        try (Stream<Path> paths = Files.walk(p0Path)) {
            paths
                    .forEach((lambda) -> {
                        if (lambda.toFile().isFile() && lambda.toString().matches(".*?\\.java")) {
                            p0List.add(lambda);
                        }
                    });
        }

//        debug block below
//        System.out.println(p0List.size());
//        for (int i = 0; i < p0List.size(); i++) {
//            System.out.println(p0List.get(i));
//        }

        for (Path f0 : p0List) {
            Path f1 = Paths.get(p1Path.toString(), f0.subpath(p0Path.getNameCount(), f0.getNameCount()).toString());
            parse(f0);
            write(f1);
        }
    }

    //    Utilities
    public static boolean isJavaClass(Path path) {
        return path.endsWith(".java");
    }
}