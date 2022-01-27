package me.nes0x.panels;

import me.nes0x.utils.ButtonUtils;

import javax.swing.*;

public class Textures extends JPanel {
    private static ButtonUtils[] buttons;
    public Textures() {
        buttons = new ButtonUtils[]{
            new ButtonUtils("Jabłka", "apples", "textures/items"),
            new ButtonUtils("Bloki", "blocks", "textures/blocks"),
            new ButtonUtils("Łuki", "bows", "textures/items"),
            new ButtonUtils("Wiadra", "buckets", "textures/items"),
            new ButtonUtils("Miecze", "swords", "textures/items"),
            new ButtonUtils("Itemy", "items", "textures/items"),
            new ButtonUtils("Sety", "armors", "textures/models/armor"),
            new ButtonUtils("Gui", "guis", "textures/gui/container"),
            new ButtonUtils("Ikonki", "icons", "textures/gui"),
            new ButtonUtils("Particle", "particles", "textures/particle")
        };

        for (ButtonUtils button: buttons) {
            add(button.getButton());
        }

    }

    public static ButtonUtils[] getButtons() {
        return buttons;
    }
}
