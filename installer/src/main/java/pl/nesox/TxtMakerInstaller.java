package pl.nesox;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.OneDarkTheme;
import mslinks.ShellLink;
import net.lingala.zip4j.core.ZipFile;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class TxtMakerInstaller {
    private static boolean isInstalling;
    private static ArrayList<String> allVersions = new ArrayList<>();

    private static JSONObject getAllVersions() throws IOException {
        URL site = new URL ("https://txtmaker.cf/api/all-versions");
        HttpURLConnection connection = (HttpURLConnection) site.openConnection();
        connection.setRequestMethod ("GET");
        connection.connect();
        int code = connection.getResponseCode();
        if (!(code != 200)) {
            InputStream is = new URL("https://txtmaker.cf/api/all-versions").openStream();
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                StringBuilder sb = new StringBuilder();
                int cp;
                while ((cp = rd.read()) != -1) {
                    sb.append((char) cp);
                }
                String jsonText = sb.toString();
                JSONObject json = new JSONObject(jsonText);
                return json;
            } finally {
                is.close();
            }
        }


        return null;
    }


    public static void main(String[] args) throws BackingStoreException, IOException {

        JSONObject json = getAllVersions();

        if (json != null) {
            JSONArray allVersionsTxtMaker = json.getJSONArray("body");
            for (Object version : allVersionsTxtMaker) {
                JSONObject versionToArray = new JSONObject(version.toString());
                allVersions.add((String) versionToArray.get("version"));

            }
        }

        Preferences variables = Preferences.userNodeForPackage(TxtMakerInstaller.class);

        LafManager.install(new OneDarkTheme());

        JFrame frame = new JFrame("Instalator TxtMaker");


        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setIconImage(
                new ImageIcon(TxtMakerInstaller.class.getClassLoader().getResource("logo.png")).getImage()
        );

        JPanel[] panels = new JPanel[]{new JPanel(), new JPanel()};

        //panels[0]

        JLabel selectVersion = new JLabel("Wybierz wersje (beta): ");

        String[] version = new String[allVersions.size()];
        version = allVersions.toArray(version);
        JComboBox<String> versionApp = new JComboBox<>(version);
        JButton downloadApp = new JButton("Pobierz i zainstaluj");
        JLabel successDownload = new JLabel("Instalowanie aplikacji...");
        JTabbedPane main = new JTabbedPane();


        downloadApp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isInstalling) {
                    Thread install = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                isInstalling = true;
                                File txtMaker = new File(String.valueOf(Paths.get(variables.get("txtMakerPath", "./"), "TxtMaker" + allVersions.get(versionApp.getSelectedIndex()))));
                                File zip = new File(String.valueOf(Paths.get(variables.get("txtMakerPath", "./"), "TxtMaker" + allVersions.get(versionApp.getSelectedIndex()), "txtmaker_" + allVersions.get(versionApp.getSelectedIndex()) + ".zip")));
                                URL url = new URL("https://txtmaker.cf/download/" + allVersions.get(versionApp.getSelectedIndex()));

                                txtMaker.mkdir();


                                FileUtils.copyURLToFile(url, zip);

                                ZipFile zipFile = new ZipFile(zip);
                                zipFile.extractAll(String.valueOf(Paths.get(variables.get("txtMakerPath", "./"), "TxtMaker" + allVersions.get(versionApp.getSelectedIndex()))));
                                zip.delete();

                                if (System.getProperty("os.name").contains("Windows")) {
                                    ShellLink.createLink(Paths.get(variables.get("txtMakerPath", "./"), "TxtMaker" + allVersions.get(versionApp.getSelectedIndex())) + "/TxtMaker.jar", variables.get("txtMakerShortcutPath", "./") + "/TxtMaker" + allVersions.get(versionApp.getSelectedIndex()) + ".lnk");
                                }


                            } catch (Exception exception) {
                                exception.printStackTrace();
                            } finally {

                                JOptionPane.showMessageDialog(
                                        null,
                                        "Zakończono pobieranie TxtMaker'a.",
                                        "Sukces!",
                                        JOptionPane.PLAIN_MESSAGE
                                );

                                isInstalling = false;

                                panels[0].remove(successDownload);
                                panels[0].repaint();
                                panels[0].revalidate();
                            }
                        }
                    });

                    install.start();

                    panels[0].add(successDownload);
                    panels[0].repaint();
                    panels[0].revalidate();
                } else {
                    JOptionPane.showMessageDialog(
                            null,
                            "Pobierasz już TxtMaker'a.",
                            "Błąd!",
                            JOptionPane.ERROR_MESSAGE
                    );
                }


            }
        });

        //panels[1]
        JButton setPathTxtMaker = new JButton("Wybierz scieżke instalacji.");
        JButton setPathShortcut = new JButton("Wybierz scieżke stworzenia skrótu (Tylko windows).");
        JButton resetPaths = new JButton("Zresetuj scieżki do podstawowych.");
        JLabel pathTxtMaker = new JLabel("Aktualnie: " + variables.get("txtMakerPath", "./"));
        JLabel pathShortCut = new JLabel("Aktualnie: " + variables.get("txtMakerShortcutPath", "./"));


        resetPaths.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                variables.put("txtMakerPath", "./");
                variables.put("txtMakerShortcutPath", "./");
                pathTxtMaker.setText("Aktualnie: ./");
                pathShortCut.setText("Aktualnie: ./");
            }
        });

        setPathTxtMaker.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    variables.put("txtMakerPath", file.getAbsolutePath());
                    pathTxtMaker.setText("Aktualnie: " + file.getAbsolutePath());
                }
            }
        });

        setPathShortcut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fileChooser.showOpenDialog(null);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    variables.put("txtMakerShortcutPath", file.getAbsolutePath());
                    pathShortCut.setText("Aktualnie: " + file.getAbsolutePath());
                }
            }
        });



        Arrays.stream(panels).forEach(panel -> panel.setLayout(new FlowLayout()));

        panels[0].add(selectVersion);
        panels[0].add(versionApp);
        panels[0].add(downloadApp);

        panels[1].add(setPathTxtMaker);
        panels[1].add(pathTxtMaker);
        panels[1].add(setPathShortcut);
        panels[1].add(pathShortCut);
        panels[1].add(resetPaths);


        main.add("Głowne", panels[0]);
        main.add("Ustawienia", panels[1]);


        frame.add(main);
        frame.setVisible(true);

    }
}
