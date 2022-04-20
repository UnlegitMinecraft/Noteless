package net.ccbluex.liquidbounce.utils;

import java.io.*;

public class NativeLoader {
    public static void loadDLL(String resourceName,String writeName) throws IOException {
        final File file = new File(writeName);
        final InputStream resource = System.class.getResourceAsStream("/" + resourceName);

        if (resource != null) {
            final FileOutputStream fos = new FileOutputStream(file);
            final byte[] buffer = new byte[128];

            int i;

            while ((i = resource.read(buffer)) != -1) {
                fos.write(buffer,0,i);
            }

            fos.close();
            resource.close();

            System.load(file.getAbsolutePath());
        } else {
            System.err.println("找不到DLL " + resourceName);
        }
    }
}
