package me.nes0x.panels;

import me.nes0x.TxtMaker;
import me.nes0x.utils.Version;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.prefs.Preferences;

public class Settings extends JPanel {
    private final Preferences variables = TxtMaker.variables;
    private static JComboBox<Version> version;

    public Settings() {
        setLayout(new FlowLayout());


        JLabel versionLabel = new JLabel("Wybierz wersje na którą robisz texturepacka: ");
        versionLabel.setPreferredSize(new Dimension(500, 20));

        version = new JComboBox<>(Version.values());
        version.setPreferredSize(new Dimension(500, 20));

        add(chooseTxtPath());
        add(autoUpdate());
        add(versionLabel);
        add(version);
    }

    private JLabel chooseTxtPath() {
        JLabel chooseTxtPath = new JLabel("Wybierz scieżke stworzenia txtpacka. Aktualnie: " + variables.get("txt_create_folder", "./").replace("./", "folder programu"));
        chooseTxtPath.setPreferredSize(new Dimension(500, 20));
        chooseTxtPath.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getButton() == MouseEvent.BUTTON3) {
                    variables.put("txt_create_folder", "./");
                    chooseTxtPath.setText("Wybierz scieżke stworzenia txtpacka. Aktualnie: " + variables.get("txt_create_folder", "./").replace("./", "folder programu"));
                } else {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    int result = fileChooser.showOpenDialog(null);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File file = fileChooser.getSelectedFile();
                        variables.put("txt_create_folder", file.getAbsolutePath());
                        chooseTxtPath.setText("Wybierz scieżke stworzenia txtpacka. Aktualnie: " + variables.get("txt_create_folder", "./").replace("./", "folder programu"));

                    }
                }

            }
        });

        return chooseTxtPath;
    }

    private JCheckBox autoUpdate() {
        JCheckBox autoUpdate = new JCheckBox("Automatyczne aktualizacje");
        autoUpdate.setPreferredSize(new Dimension(500, 20));
        autoUpdate.setSelected(variables.getBoolean("auto_update", true));
        autoUpdate.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                switch (event.getStateChange()) {
                    case ItemEvent.SELECTED:
                        variables.putBoolean("auto_update", true);
                        break;
                    case ItemEvent.DESELECTED:
                        variables.putBoolean("auto_update", false);
                        break;
                }
            }
        });

        return autoUpdate;
    }


    public static JComboBox<Version> getVersion() {
        return version;
    }
}

