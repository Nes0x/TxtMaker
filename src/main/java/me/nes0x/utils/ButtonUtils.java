package me.nes0x.utils;

import org.apache.commons.io.FileUtils;
import me.nes0x.panels.Settings;

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

public class ButtonUtils {
    private JButton button;
    private String name, folderName, pathToTxtFolder;
    private ArrayList<File> files;

    public ButtonUtils(final String name, final String folderName, final String pathToTxtFolder) {
        this.name = name;
        this.folderName = folderName;
        this.pathToTxtFolder = pathToTxtFolder;
        files = new ArrayList<>();
        button = new JButton(name);
        setOptionsOfButton(button);
    }


    private void setOptionsOfButton(JButton button) {
        button.setPreferredSize(new Dimension(280, 30));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //tworzenie kolejnego okna
                JFrame frame = FrameUtil.createFrame(1200, 900, "Wybierz teksture " + name.toLowerCase(), JFrame.DISPOSE_ON_CLOSE, true);

                String pickedVersion =  Settings.getVersion().getSelectedItem().toString().replace("V", "").replace("_", ".");

                File allFilesFromFolderName = new File(String.valueOf(Paths.get(".","textures" + pickedVersion, folderName)));
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
                        public synchronized void drop(DropTargetDropEvent event) {
                            try {
                                event.acceptDrop(DnDConstants.ACTION_COPY);
                                List<File> droppedFiles = (List<File>)
                                        event.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                                for (File droppedFile : droppedFiles) {
                                    if (droppedFile.getName().contains(".png")) {
                                        FileUtils.copyFileToDirectory(droppedFile, Paths.get(".","textures" + pickedVersion, folderName, directory).toFile());
                                    }
                                }

                                JOptionPane.showMessageDialog(
                                        null,
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

                    try (Stream<Path> paths = Files.walk(Paths.get(".","textures" + pickedVersion, folderName, directory))) {
                        paths.filter(Files::isRegularFile)
                            .forEach(file -> {
                                //tworzenie labela
                                JLabel label = new JLabel(file.toFile().getName().replace("9", "").replace(".png", ""));
                                Image image = null;
                                File texture = file.toFile();
                                allFiles.add(texture);
                                try {
                                    image = ImageIO.read(texture);
                                } catch (IOException exception) {
                                    exception.printStackTrace();
                                }



                                Image finalImage = image;

                                //dodanie do labela mouse listenera ktory dodaje itemy do txtpacka
                                label.addMouseListener(new MouseAdapter() {
                                    @Override
                                    public void mouseClicked(MouseEvent event) {
                                        if (event.getButton() == MouseEvent.BUTTON3) {
                                            JLabel labelAfterChange = new JLabel();
                                            labelAfterChange.setIcon(new ImageIcon(finalImage.getScaledInstance(290, 290, Image.SCALE_SMOOTH)));
                                            JOptionPane.showMessageDialog(
                                                    null,
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
                                                null,
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

    public JButton getButton() {
        return button;
    }

    public ArrayList<File> getFiles() {
        return files;
    }

    public String getName() {
        return name;
    }

    public String getFolderName() {
        return folderName;
    }

    public  String getPathToTxtFolder() {
        return pathToTxtFolder;
    }
}
