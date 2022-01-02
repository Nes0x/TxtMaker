package pl.nesox.utils;

import org.apache.commons.io.FileUtils;
import pl.nesox.TxtMaker;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ButtonManager {
    //deklarowanie zmiennych
    private static JButton chooseTxtPath;
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
                JFrame frame = TxtMaker.createFrame(1200, 900, "Wybierz teksture " + name.toLowerCase(), JFrame.DISPOSE_ON_CLOSE, true);

                File allFilesFromFolderName = new File(String.valueOf(Paths.get(".","textures" + TxtMaker.getVERSIONS()[TxtMaker.getVersion().getSelectedIndex()], folderName)));
                String[] directories = allFilesFromFolderName.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File current, String name) {
                        return new File(current, name).isDirectory();
                    }
                });

                JTabbedPane main = new JTabbedPane();

                for (String directory : directories) {
                    JPanel panel = new JPanel();

                    ArrayList<File> allFiles = new ArrayList<>();


                    panel.setDropTarget(new DropTarget() {
                        public synchronized void drop(DropTargetDropEvent evt) {
                            try {
                                evt.acceptDrop(DnDConstants.ACTION_COPY);
                                List<File> droppedFiles = (List<File>)
                                        evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                                for (File droppedFile : droppedFiles) {
                                    if (droppedFile.getName().contains(".png")) {
                                        FileUtils.copyFileToDirectory(droppedFile, Paths.get(".","textures" + TxtMaker.getVERSIONS()[TxtMaker.getVersion().getSelectedIndex()], folderName, directory).toFile());
                                    }
                                }

                                JOptionPane.showMessageDialog(
                                        frame,
                                        "Dodano tekstury! Włącz od nowa kategorie aby je zobaczyć!",
                                        "Sukces!",
                                        JOptionPane.PLAIN_MESSAGE,
                                        null
                                );
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });

                    try (Stream<Path> paths = Files.walk(Paths.get(".","textures" + TxtMaker.getVERSIONS()[TxtMaker.getVersion().getSelectedIndex()], folderName, directory))) {
                        paths.filter(Files::isRegularFile)
                            .forEach(file -> {
                                //tworzenie labela
                                JLabel label = new JLabel(file.toFile().getName().replace("9", "").replace(".png", ""));

                                Image image = null;

                                File texture = file.toFile();

                                allFiles.add(texture);


                                try {
                                    image = ImageIO.read(texture);
                                } catch (IOException ioException) {
                                    ioException.printStackTrace();
                                }


                                //dodanie do labela mouse listenera ktory dodaje itemy do txtpacka
                                Image finalImage = image;

                                label.addMouseListener(new MouseAdapter() {
                                    @Override
                                    public void mouseClicked(MouseEvent e) {
                                        if (e.getButton() == MouseEvent.BUTTON3) {
                                            JLabel labelAfterChange = new JLabel();
                                            labelAfterChange.setIcon(new ImageIcon(finalImage.getScaledInstance(290, 290, Image.SCALE_SMOOTH)));
                                            JOptionPane.showMessageDialog(
                                                    frame,
                                                    labelAfterChange,
                                                    "Tekstura w powiększeniu!",
                                                    JOptionPane.PLAIN_MESSAGE,
                                                    null
                                            );
                                            return;
                                        }

                                        String message;

                                        if (files.contains(texture)) {
                                            files.remove(texture);
                                            message = "Usunięto teksture.";
                                        } else {
                                            files.add(texture);
                                            message = "Dodano teksture.";
                                        }

                                        JOptionPane.showMessageDialog(
                                                panel,
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
                                label.setIcon(new ImageIcon(image.getScaledInstance(55, 55, Image.SCALE_SMOOTH)));
                                panel.add(label);
                                    }
                            );
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                    main.add(directory, panel);
                }

                frame.add(main);
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

                        try {
                            FileUtils.copyDirectory(new File(String.valueOf(Paths.get(".", "TxtDefault" + TxtMaker.getVERSIONS()[TxtMaker.getVersion().getSelectedIndex()]))), new File(String.valueOf(Paths.get(TxtMaker.variables.get("txtCreationFolder", "./"), nameOfTxt))));
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }

                        int size = button.files.size() - 1;

                        for (int i = size; i >= 0; i--) {

                            try {
                                FileUtils.copyFileToDirectory(button.files.get(i), new File(String.valueOf(Paths.get(TxtMaker.variables.get("txtCreationFolder", "./"), nameOfTxt, "assets", "minecraft", button.pathToTXTFolder))));
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }


                            File correctFile = new File(String.valueOf(Paths.get(TxtMaker.variables.get("txtCreationFolder", "./"), nameOfTxt, "assets", "minecraft", button.pathToTXTFolder)), button.files.get(i).getName().replace("9", ""));
                            File wrongFile = new File(String.valueOf(Paths.get(TxtMaker.variables.get("txtCreationFolder", "./"), nameOfTxt, "assets", "minecraft", button.pathToTXTFolder)), button.files.get(i).getName());

                            wrongFile.renameTo(correctFile);

                        }

                    }
                }

                String message;

                if (TxtMaker.variables.get("txtCreationFolder", "./").equalsIgnoreCase("./")) {
                    message = isCreated ? "Stworzono teksturpacka w folderze programu." : "Błąd, nie udało się stworzyć teksturepacka. Musisz wybrać chociaż jedną teksturę.";
                } else {
                    message = isCreated ? "Stworzono teksturpacka w " + TxtMaker.variables.get("txtCreationFolder", "./") : "Błąd, nie udało się stworzyć teksturepacka. Musisz wybrać chociaż jedną teksturę.";
                }


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
                JFrame frame = TxtMaker.createFrame(1200, 900, "Wszystkie tekstury", JFrame.DISPOSE_ON_CLOSE, true);
                JButton deleteAllFiles = new JButton("Usuń wszystkie tekstury");
                ArrayList<File> allFiles = new ArrayList<>();


                //sprawdzanie wszystkich tekstur użytkownika które ma wybrane i dodanie mouse listenera który usuwa tekstury
                for (ButtonManager button : TxtMaker.getButtonsTxT()) {
                    int size = button.files.size() - 1;

                    for (int i = size; i >= 0; i--) {
                        JLabel label = new JLabel(button.files.get(i).getName().replace("9", "").replace(".png", ""));

                        Image image = null;

                        File texture = button.files.get(i);

                        allFiles.add(texture);

                        try {
                            image = ImageIO.read(texture);
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }


                        label.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
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
                        label.setHorizontalTextPosition(JLabel.CENTER);
                        label.setVerticalTextPosition(JLabel.BOTTOM);
                        label.setIcon(new ImageIcon(image.getScaledInstance(55, 55, Image.SCALE_SMOOTH)));
                        frame.add(label);

                    }
                    if (!button.files.isEmpty()) {
                        deleteAllFiles.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                button.files.removeAll(allFiles);
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


    public static JButton createChooseTxtPath() {
        chooseTxtPath = new JButton("Wybierz scieżke stworzenia txtpacka. \nAktualnie: " + TxtMaker.variables.get("txtCreationFolder", "./"));

        chooseTxtPath.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fileChooser.showOpenDialog(TxtMaker.getFrame());
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    TxtMaker.variables.put("txtCreationFolder", file.getAbsolutePath());
                    chooseTxtPath.setText("Wybierz scieżke stworzenia txtpacka. \nAktualnie: " + TxtMaker.variables.get("txtCreationFolder", "./"));

                }
            }
        });
        return chooseTxtPath;
    }


    public static JButton resetTxtPath() {
        JButton resetPath = new JButton("Zresetuj scieżke stworzenia txtpacka");

        resetPath.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TxtMaker.variables.put("txtCreationFolder", "./");
                chooseTxtPath.setText("Wybierz scieżke stworzenia txtpacka. \nAktualnie: " + TxtMaker.variables.get("txtCreationFolder", "./"));

            }
        });
        return resetPath;
    }


    public JButton getButton() {
        return button;
    }

    public void setPathToTXTFolder(String pathToTXTFolder) {
        this.pathToTXTFolder = pathToTXTFolder;
    }

    public String getPathToTXTFolder() {
        return pathToTXTFolder;
    }
}
