package me.nes0x.utils;

import me.nes0x.TxtMaker;

import javax.swing.*;

public class FrameUtil {
    public static JFrame createFrame(int width, int high, String title, int action, boolean resizable) {
        JFrame frame = new JFrame(title);
        frame.setSize(width, high);
        frame.setDefaultCloseOperation(action);
        frame.setResizable(resizable);
        frame.setLocationRelativeTo(null);
        frame.setIconImage(
                new ImageIcon(TxtMaker.class.getClassLoader().getResource("logo.png")).getImage()
        );

        return frame;

    }
}
