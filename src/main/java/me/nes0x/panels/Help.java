package me.nes0x.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;

public class Help extends JPanel {

    public Help() {
        setLayout(new FlowLayout());
        JLabel discord = new JLabel("Jeśli potrzebujesz pomocy kliknij mnie. (Przekierowanie na discorda)");
        discord.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                try {
                    Desktop.getDesktop().browse(URI.create("https://discord.gg/3FPZpFubyH"));
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        });

        add(discord);
    }

}
