package re.rule.refactor.model;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Launcher {
    public static void main(String[] args) throws IOException {
//        p0 for source directory
//        p1 for target directory
        String p0Path, p1Path;
//        if (args == null) {
//            p0Path = "/home/desktop/cs645-software-research/complexity-verification-project/dataset6";
//            p1Path = "p1";
//        } else {
//            p0Path = args[1];
//            p1Path = args[2];
//        }
        p0Path = args[0];
        p1Path = args[1];

        Model m = new Model(p0Path, p1Path);
        m.launch();
    }
}
