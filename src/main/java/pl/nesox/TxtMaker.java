package pl.nesox;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.OneDarkTheme;
import pl.nesox.utils.ButtonManager;
import pl.nesox.utils.DiscordIntegration;
import pl.nesox.utils.Updater;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.prefs.Preferences;

public class TxtMaker {
    private static JFrame frame;
    private static ButtonManager[] buttonsTxT;
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


    public static void main(String[] args) throws IOException {
        LafManager.install(new OneDarkTheme());
        new Updater();

        //tworzenie okna, zakładek, przycisków
        frame = createFrame(600, 400, "TxtMaker", JFrame.EXIT_ON_CLOSE, false);

        JTabbedPane main = new JTabbedPane();

        JPanel[] panels = new JPanel[]{new JPanel(), new JPanel(), new JPanel()};
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
        };

        Arrays.stream(buttonsTxT).forEach(button -> panels[1].add(button.getButton()));
        Arrays.stream(panels).forEach(panel -> panel.setLayout(new FlowLayout()));

        panels[0].add(ButtonManager.createTxtButton());
        panels[0].add(ButtonManager.createShowTextures());

        main.add("Stwórz TXT", panels[0]);
        main.add("Dodaj tekstury", panels[1]);
        main.add("Pomoc", panels[2]);
        panels[2].add(new JLabel("Jeśli potrzebujesz pomocy: https://discord.gg/TMRnDZvmsp"));


        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                DiscordIntegration.stopRPC();
            }
        });


        frame.add(main);
        frame.setVisible(true);


        //aktywowanie statusu na discordzie
        DiscordIntegration.startRPC();

    }

    public static JFrame getFrame() {
        return frame;
    }

    public static ButtonManager[] getButtonsTxT() {
        return buttonsTxT;
    }
}
