package pl.nesox;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.HighContrastLightTheme;
import com.github.weisj.darklaf.theme.OneDarkTheme;
import net.lingala.zip4j.exception.ZipException;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.json.JSONObject;
import pl.nesox.utils.ButtonManager;
import pl.nesox.utils.DiscordIntegration;
import pl.nesox.utils.Updater;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.prefs.Preferences;

public class TxtMaker {
    private static JFrame frame;
    private static final String VERSIONS[] = {"1.8", "1.16"};
    private static ButtonManager[] buttonsTxT;
    private static JComboBox<String> version;
    public static Preferences variables = Preferences.userNodeForPackage(TxtMaker.class);


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


    public static void main(String[] args) throws IOException, ZipException, XmlPullParserException {
        UIManager.put("OptionPane.yesButtonText", "Tak");
        UIManager.put("OptionPane.noButtonText", "Nie");


        if (variables.getBoolean("darkMode", true)) {
            LafManager.install(new OneDarkTheme());
        } else {
            LafManager.install(new HighContrastLightTheme());
        }


        //tworzenie okna, zakładek, przycisków
        frame = createFrame(600, 400, "TxtMaker", JFrame.EXIT_ON_CLOSE, false);

        JTabbedPane main = new JTabbedPane();

        JPanel[] panels = new JPanel[]{new JPanel(), new JPanel(), new JPanel(), new JPanel(), new JPanel()};
        buttonsTxT = new ButtonManager[]{
                new ButtonManager("Jabłka", "apples", "textures/items"),
                new ButtonManager("Bloki", "blocks", "textures/blocks"),
                new ButtonManager("Łuki", "bows", "textures/items"),
                new ButtonManager("Wiadra", "buckets", "textures/items"),
                new ButtonManager("Miecze", "swords", "textures/items"),
                new ButtonManager("Itemy", "items", "textures/items"),
                new ButtonManager("Sety", "armors", "textures/models/armor"),
                new ButtonManager("Gui", "guis", "textures/gui/container"),
                new ButtonManager("Ikonki", "icons", "textures/gui"),
                new ButtonManager("Particle", "particles", "textures/particle")
        };

        Arrays.stream(buttonsTxT).forEach(button -> panels[1].add(button.getButton()));
        Arrays.stream(panels).forEach(panel -> panel.setLayout(new FlowLayout()));


        version = new JComboBox<>(VERSIONS);

        version.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (ButtonManager button : buttonsTxT) {
                    if (version.getSelectedIndex() == 1) {
                        if (button.getPathToTXTFolder().contains("items")) {
                            button.setPathToTXTFolder(button.getPathToTXTFolder().replace("items", "item"));
                        } else if (button.getPathToTXTFolder().contains("blocks")) {
                            button.setPathToTXTFolder(button.getPathToTXTFolder().replace("blocks", "block"));
                        }
                    } else {
                        if (button.getPathToTXTFolder().contains("item")) {
                            button.setPathToTXTFolder(button.getPathToTXTFolder().replace("item", "items"));
                        } else if (button.getPathToTXTFolder().contains("block")) {
                            button.setPathToTXTFolder(button.getPathToTXTFolder().replace("block", "blocks"));
                        }
                    }

                }
            }
        });

        panels[0].add(ButtonManager.createTxtButton());
        panels[0].add(ButtonManager.createShowTextures());
        panels[0].add(version);

        main.add("Stwórz TXT", panels[0]);
        main.add("Dodaj tekstury", panels[1]);
        main.add("Zmiany", panels[2]);
        main.add("Ustawienia", panels[3]);
        main.add("Pomoc", panels[4]);




        JCheckBox enableDarkMode = new JCheckBox("Tryb ciemny");
        JCheckBox enableTelemetry = new JCheckBox("Telemetria(Auto aktualizacje, wiadomość w zakładce pomoc)");

        if (variables.getBoolean("darkMode", true)) {
            enableDarkMode.setSelected(true);
        }

        if (variables.getBoolean("telemetry", true)) {
            enableTelemetry.setSelected(true);
        }

        enableTelemetry.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                switch (e.getStateChange()) {
                    case ItemEvent.SELECTED:
                        variables.putBoolean("telemetry", true);
                        break;
                    case ItemEvent.DESELECTED:
                        variables.putBoolean("telemetry", false);
                        break;
                }
            }
        });

        enableDarkMode.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                switch (e.getStateChange()) {
                    case ItemEvent.SELECTED:
                        variables.putBoolean("darkMode", true);
                        LafManager.install(new OneDarkTheme());
                        break;
                    case ItemEvent.DESELECTED:
                        variables.putBoolean("darkMode", false);
                        LafManager.install(new HighContrastLightTheme());
                        break;
                }
            }
        });

        JTextArea changelog = new JTextArea();
        changelog.setText("+ Dodano możliwość wybrania gdzie ma się tworzyć txtpack (Zakładka ustawienia). \nDomyślnie tworzy się w folderze programu");
        changelog.setEditable(false);

        JScrollPane changelogPane = new JScrollPane(changelog);

        panels[2].add(changelogPane);

        JTextArea help = new JTextArea();

        if (variables.getBoolean("telemetry", true)) {
            JSONObject json = Updater.readJsonFromUrl("https://txtmaker.cf/api/message");
            if (json != null) {
                help.setText(json.getString("message"));
            } else {
                help.setText("Jeśli potrzebujesz pomocy wejdź na discorda: https://discord.gg/ZYzHhhaPVu");
            }
        } else {
            help.setText("Jeśli potrzebujesz pomocy wejdź na discorda: https://discord.gg/ZYzHhhaPVu");
        }

        help.setEditable(false);

        panels[4].add(new JScrollPane(help));

        panels[3].add(enableDarkMode);
        panels[3].add(enableTelemetry);
        panels[3].add(ButtonManager.createChooseTxtPath());
        panels[3].add(ButtonManager.resetTxtPath());
        panels[3].add(new JLabel("./ = Folder programu"));

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                DiscordIntegration.stopRPC();

            }
        });


        frame.add(main);
        frame.setVisible(true);


        //Sprawdzanie aktualizacji
        if (variables.getBoolean("telemetry", true)) {
            new Updater();
        }



        //aktywowanie statusu na discordzie
        DiscordIntegration.startRPC();

    }

    public static String[] getVERSIONS() {
        return VERSIONS;
    }

    public static JFrame getFrame() {
        return frame;
    }

    public static ButtonManager[] getButtonsTxT() {
        return buttonsTxT;
    }

    public static JComboBox<String> getVersion() {
        return version;
    }
}
