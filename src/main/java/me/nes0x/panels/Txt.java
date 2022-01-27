package me.nes0x.panels;

import me.nes0x.utils.ButtonUtils;
import me.nes0x.utils.FrameUtil;
import org.apache.commons.io.FileUtils;

import me.nes0x.TxtMaker;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.prefs.Preferences;

public class Txt extends JPanel {
    private final Preferences variables = TxtMaker.variables;


    public Txt() {
        setLayout(new FlowLayout());
        add(createTxt());
        add(showTextures());
    }



    //metoda która tworzy przycisk odpowiedzialny za stworzenie txtpacka
    private JButton createTxt() {
        JButton createTxt = new JButton("Stwórz TXT");


        //dodanie action listenera który pyta użytkownika o nazwe txtpacka itp.
        createTxt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                String nameTxt = JOptionPane.showInputDialog(null, "Podaj jaką chcesz nazwe teksturepacka", "Txt");

                if (nameTxt == null) {
                    return;
                }
                
                boolean isCreated = false;
                
                //dodawanie wszystkich tekstur do folderu na pulpicie
                for (ButtonUtils button : Textures.getButtons()) {
                    ArrayList<File> getFiles = button.getFiles();
                    String pathToTxtFolder = button.getPathToTxtFolder();
                    if (!getFiles.isEmpty()) {
                        
                        isCreated = true;

                        try {
                            FileUtils.copyDirectory(new File(String.valueOf(Paths.get("./", "TxtDefault"))), new File(String.valueOf(Paths.get(variables.get("txt_create_folder", "./"), nameTxt))));
                        } catch (IOException exception) {
                            exception.printStackTrace();
                        }

                        int size = getFiles.size() - 1;

                        for (int i = size; i >= 0; i--) {

                            try {
                                FileUtils.copyFileToDirectory(getFiles.get(i), new File(String.valueOf(Paths.get(variables.get("txt_create_folder", "./"), nameTxt, "assets", "minecraft", pathToTxtFolder))));
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }


                            File correctFile = new File(String.valueOf(Paths.get(variables.get("txt_create_folder", "./"), nameTxt, "assets", "minecraft", pathToTxtFolder)), getFiles.get(i).getName().replace("9", ""));
                            File wrongFile = new File(String.valueOf(Paths.get(variables.get("txt_create_folder", "./"), nameTxt, "assets", "minecraft", pathToTxtFolder)), getFiles.get(i).getName());

                            wrongFile.renameTo(correctFile);

                        }

                    }
                }
                
                if (isCreated) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Pomyślnie stworzono TxtPacka w: " + variables.get("txt_create_folder", "./").replace("./", "folderze programu"),
                            "Stworzono TXT!",
                            JOptionPane.PLAIN_MESSAGE,
                            null
                    );
                }
              

            }
        });

        return createTxt;
    }

    private JButton showTextures() {
        JButton showTextures = new JButton("Pokaż jakie mam tekstury");

        showTextures.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //stworzenie okna
                JFrame frame = FrameUtil.createFrame( 1200, 900, "Twoje wybrane tekstury", JFrame.DISPOSE_ON_CLOSE, true);
                JButton deleteAllFiles = new JButton("Usuń wszystkie tekstury");
                ArrayList<File> allFiles = new ArrayList<>();


                //sprawdzanie wszystkich tekstur użytkownika które ma wybrane i dodanie mouse listenera który usuwa tekstury
                for (ButtonUtils button : Textures.getButtons()) {
                    ArrayList<File> getFiles = button.getFiles();
                    int size = getFiles.size() - 1;

                    for (int i = size; i >= 0; i--) {


                        JLabel label = new JLabel(getFiles.get(i).getName().replace("9", "").replace(".png", ""));
                        Image image = null;
                        File texture = getFiles.get(i);
                        allFiles.add(texture);

                        try {
                            image = ImageIO.read(texture);
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }


                        label.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent event) {
                                if (getFiles.contains(texture)) {
                                    getFiles.remove(texture);
                                    JOptionPane.showMessageDialog(
                                            null,
                                            "Usunięto teksture.",
                                            "Sukces!",
                                            JOptionPane.PLAIN_MESSAGE,
                                            null
                                    );
                                    frame.remove(label);
                                    frame.repaint();
                                    frame.revalidate();
                                }
                            }
                        });


                        //ustawienia okna + labela
                        label.setHorizontalTextPosition(JLabel.CENTER);
                        label.setVerticalTextPosition(JLabel.BOTTOM);
                        label.setIcon(new ImageIcon(image.getScaledInstance(55, 55, Image.SCALE_SMOOTH)));
                        frame.add(label);

                    }

                    if (!getFiles.isEmpty()) {
                        deleteAllFiles.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                getFiles.removeAll(allFiles);
                                frame.dispose();
                            }
                        });
                    }

                }
                frame.add(deleteAllFiles);
                frame.setLayout(new FlowLayout());
                frame.setVisible(true);
            }
        });


        return showTextures;

    }


}
