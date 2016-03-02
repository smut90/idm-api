package net.cake.idm.core.service.impl.util;

import java.io.File;

/**
 * @author pankajanc on 1/21/15.
 * @author Supun Muthutantrige
 */
public class TestUtil {
    public static File relativeToBaseDir(String path, int upCount) {
        File currentFile = new File(System.getProperty("user.dir"));
        System.out.println("CURRENT DIR:" + currentFile.getAbsolutePath());
        for (int i = 1; i < upCount; i++) {
            currentFile = currentFile.getParentFile();
        }
        String baseDirPath = currentFile.getParent();

        return new File(baseDirPath, path);
    }
}
