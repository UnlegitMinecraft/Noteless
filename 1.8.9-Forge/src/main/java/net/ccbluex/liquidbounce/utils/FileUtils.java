package net.ccbluex.liquidbounce.utils;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class FileUtils {
    public static void unpackFile(File file,String name) throws IOException {
        if(!file.exists()) {
            FileOutputStream fos=new FileOutputStream(file);
            IOUtils.copy(Objects.requireNonNull(FileUtils.class.getClassLoader().getResourceAsStream(name)), fos);
            fos.close();
        }
    }
}
