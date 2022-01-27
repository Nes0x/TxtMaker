package me.nes0x;


import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.OneDarkTheme;
import me.nes0x.panels.*;
import me.nes0x.utils.FrameUtil;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import me.nes0x.utils.Updater;

import javax.swing.*;
import java.io.IOException;
import java.util.prefs.Preferences;

public class TxtMaker {
    public static Preferences variables = Preferences.userNodeForPackage(TxtMaker.class);


    public static void main(String[] args) throws XmlPullParserException, IOException {
        UIManager.put("OptionPane.yesButtonText", "Tak");
        UIManager.put("OptionPane.noButtonText", "Nie");
        UIManager.put("OptionPane.cancelButtonText", "Anuluj");
        UIManager.put("OptionPane.okButtonText", "Okej");
        LafManager.install(new OneDarkTheme());

        //tworzenie okna, zakładek
        JFrame frame = FrameUtil.createFrame(650, 400, "TxtMaker", JFrame.EXIT_ON_CLOSE, false);
        JTabbedPane main = new JTabbedPane();

        main.add("Stwórz TxT", new Txt());
        main.add("Dodaj tekstury", new Textures());
        main.add("Ustawienia", new Settings());
        main.add("Zmiany", new ChangeLog());
        main.add("Pomoc", new Help());


        frame.add(main);
        frame.setVisible(true);

        if (variables.getBoolean("auto_update", true)) {
            new Updater();
        }

    }


}
