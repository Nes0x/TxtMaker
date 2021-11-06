package pl.nesox.utils;

import org.apache.commons.io.FileUtils;
import pl.nesox.TxtMaker;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

public class ButtonManager {
    //deklarowanie zmiennych
    private JButton button;
    private String name, folderName, pathToTXTFolder;
    private ArrayList<File> files;


    //tworzenie przycisku
    public ButtonManager(String name, String folderName, String pathToTXTFolder) {
        this.name = name;
        this.folderName = folderName;
        this.pathToTXTFolder = pathToTXTFolder;
        files = new ArrayList<>();
        button = new JButton(name);
        setOptionsOfButton(button);
    }



    // ustawianie action listenerów itd dla danego przycisku
    private void setOptionsOfButton(JButton button) {
        button.setPreferredSize(new Dimension(280, 30));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //tworzenie kolejnego okna
                JFrame frame = TxtMaker.createFrame(600, 500, "Wybierz teksture " + name.toLowerCase(), JFrame.DISPOSE_ON_CLOSE, true);

                //pobieranie wszystkich plików z danej kategorii
                try (Stream<Path> paths = Files.walk(Paths.get("textures/" + folderName))) {
                    paths.filter(Files::isRegularFile)
                            .forEach(file -> {
                                //tworzenie labela
                                JLabel label = new JLabel(file.toString().replace("9", "").substring(file.toString().lastIndexOf("\\")).replace("\\", "").replace(".png", ""));

                                Image image = null;

                                File texture = new File(file + "");

                                try {
                                    image = ImageIO.read(texture);
                                } catch (IOException ioException) {
                                    ioException.printStackTrace();
                                }


                                //dodanie do labela mouse listenera ktory dodaje itemy do txtpacka
                                label.addMouseListener(new MouseAdapter() {
                                    @Override
                                    public void mouseClicked(MouseEvent e) {
                                        String message;

                                        if (files.contains(texture)) {
                                            files.remove(texture);
                                            message = "Usunięto teksture.";
                                        } else {
                                            files.add(texture);
                                            message = "Dodano teksture.";
                                        }

                                        JOptionPane.showMessageDialog(
                                                frame,
                                                message,
                                                "Sukces!",
                                                JOptionPane.PLAIN_MESSAGE,
                                                null
                                        );
                                    }
                                });
                                //dodanie labela do okna + ustawienie do niego ikonki
                                label.setHorizontalTextPosition(JLabel.CENTER);
                                label.setVerticalTextPosition(JLabel.BOTTOM);
                                label.setFont(new Font("Calibri", Font.PLAIN, 11));
                                label.setIcon(new ImageIcon(image.getScaledInstance(35, 35, Image.SCALE_SMOOTH)));
                                frame.add(label);
                                    }
                            );
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

                //wyswietlenie okna
                frame.setLayout(new FlowLayout());
                frame.setVisible(true);
            }
        });
    }

    //metoda która tworzy przycisk odpowiedzialny za stworzenie txtpacka
    public static JButton createTxtButton() {
        JButton createTxt = new JButton("Stwórz TXT");


        //dodanie action listenera który pyta użytkownika o nazwe txtpacka itp.
        createTxt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nameOfTxt = JOptionPane.showInputDialog(TxtMaker.getFrame(), "Podaj jaką chcesz nazwe teksturepacka", "TxtCreation");

                //ustawianie wartosci czy txt zostal poprawnie stworzony
                boolean isCreated = false;

                //dodawanie wszystkich tekstur do folderu na pulpicie
                for (ButtonManager button : TxtMaker.getButtonsTxT()) {
                    if (!button.files.isEmpty()) {
                        isCreated = true;
                        String userHomeFolder = System.getProperty("user.home");
                        try {
                            FileUtils.copyDirectory(new File("TxtDefault"), new File(userHomeFolder + "\\Desktop\\" + nameOfTxt));
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }

                        int size = button.files.size() - 1;

                        for (int i = size; i >= 0; i--) {
                            String subString = button.files.get(i).toString().substring(button.files.get(i).toString().lastIndexOf("\\"));


                            try {
                                FileUtils.copyFileToDirectory(button.files.get(i), new File(userHomeFolder + "\\Desktop\\" + nameOfTxt + "\\assets\\minecraft", button.pathToTXTFolder));
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }


                            File correctFile = new File(userHomeFolder + "/Desktop/" + nameOfTxt + "/assets/minecraft", button.pathToTXTFolder + "/" + subString.replace("9", ""));
                            File wrongFile = new File(userHomeFolder + "/Desktop/" + nameOfTxt +"/assets/minecraft", button.pathToTXTFolder + "/" + subString);

                            wrongFile.renameTo(correctFile);

                        }

                    }
                }

                String message = isCreated ? "Stworzono  teksturpacka na pulpicie." : "Błąd, nie udało się stworzyć teksturepacka.";

                if (isCreated) {
                    TxtMaker.variables.putInt("valueOfCreatedTxT", TxtMaker.variables.getInt("valueOfCreatedTxT",0) + 1);
                }

                JOptionPane.showMessageDialog(
                        TxtMaker.getFrame(),
                        message,
                        "Stworzenie teksturepacka",
                        JOptionPane.PLAIN_MESSAGE,
                        null
                );

            }
        });

        return createTxt;
    }

    //metoda która tworzy przycisk do sprawdzania aktualnych tekstur
    public static JButton createShowTextures() {
        JButton showTextures = new JButton("Pokaż jakie mam tekstury");

        showTextures.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //stworzenie okna
                JFrame frame = TxtMaker.createFrame(500, 500, "Wszystkie tekstury", JFrame.DISPOSE_ON_CLOSE, true);


                //sprawdzanie wszystkich tekstur użytkownika które ma wybrane i dodanie mouse listenera który usuwa tekstury
                for (ButtonManager button : TxtMaker.getButtonsTxT()) {
                    int size = button.files.size() - 1;

                    for (int i = size; i >= 0; i--) {
                        JLabel label = new JLabel(button.files.get(i).toString().replace("9", "").substring(button.files.get(i).toString().lastIndexOf("\\")).replace("\\", "").replace(".png", ""));

                        Image image = null;

                        try {
                            image = ImageIO.read(new File(button.files.get(i) + ""));
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }


                        int finalI = i;
                        label.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                File texture = new File(button.files.get(finalI) + "");
                                if (button.files.contains(texture)) {
                                    button.files.remove(texture);
                                    JOptionPane.showMessageDialog(
                                            frame,
                                            "Usunięto teksture.",
                                            "Sukces!",
                                            JOptionPane.PLAIN_MESSAGE,
                                            null
                                    );
                                }
                            }
                        });


                        //ustawienia okna + labela
                        label.setFont(new Font("Calibri", Font.PLAIN, 11));
                        label.setHorizontalTextPosition(JLabel.CENTER);
                        label.setVerticalTextPosition(JLabel.BOTTOM);
                        label.setIcon(new ImageIcon(image.getScaledInstance(35, 35, Image.SCALE_SMOOTH)));
                        frame.add(label);
                        frame.setLayout(new FlowLayout());
                        frame.setVisible(true);

                    }


                }
            }
        });


        return showTextures;

    }

    //metoda która tworzy przycisk umożliwiający zmiane nazw plików
    public static JButton createChangeName() {
        JButton button = new JButton("Zmień nazwy plików");

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String valueOfNine =
                        JOptionPane.showInputDialog(
                                TxtMaker.getFrame(),
                                "Podaj ilość dziewiątek",
                                "1");

                Integer valueOfNineInt = Integer.parseInt(valueOfNine);
                StringBuilder nine = new StringBuilder();
                for (int i = valueOfNineInt; i > 0; i--) {
                    nine.append("9");
                }

                //zmienianie nazw plikow z folderu toChange w odpowiednia ilosc 9
                try (Stream<Path> paths = Files.walk(Paths.get("textures/toChange"))) {
                    paths.filter(Files::isRegularFile)
                            .forEach(file -> {
                                String fileChange = file.toString().substring(file.toString().lastIndexOf("\\")).replace("\\", "");
                                File correctFile = new File(file.toString().substring(0, file.toString().lastIndexOf("\\")) + "\\" + nine + fileChange);
                                File wrongFile = new File(file + "");


                                wrongFile.renameTo(correctFile);

                                }
                            );
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

                JOptionPane.showMessageDialog(
                        TxtMaker.getFrame(),
                        "Zmieniono nazwy tekstur.",
                        "Sukces!",
                        JOptionPane.PLAIN_MESSAGE,
                        null
                );
            }


        });


        return button;


    }

    public JButton getButton() {
        return button;
    }
}
