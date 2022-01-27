package me.nes0x.panels;

import javax.swing.*;
import java.awt.*;

public class ChangeLog extends JPanel {
    public ChangeLog () {
        setLayout(new FlowLayout());
        add(new JLabel("+ Zreformatowanie kodu"));
        add(new JLabel("- Tryb jasny, integracja z discordem oraz automatyczne pobierane aktualizacji (były problematyczne)"));
    }
}
