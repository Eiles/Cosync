package Interface;

import Controllers.CoController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;

/**
 * Created by Alban on 15/07/2015.
 */
public class CoDownloadMenu extends CoInterface {
    private HashMap<String, String> dlFiles;

    private JPanel center;
    private JPanel north;
    private Box filesList;
    private JScrollPane filesPanel;

    public CoDownloadMenu(CoController controller) throws HeadlessException {
        super(controller);
        this.dlFiles = new HashMap<>();
        dlFiles.put("File 1", "test");
        dlFiles.put("File 2", "test2");

        setLayout(new BorderLayout());
        setSize(400, 600);
        setTitle("Synchronisations");

        setCenter();
    }

    private void setCenter() {
        center = new JPanel();
        center.setLayout(new BorderLayout());

        filesList = Box.createVerticalBox();
        filesList.add(new Label("Liste des fichiers en cours de synchronisation"));
        filesPanel = new JScrollPane(filesList);

        center.add(filesPanel, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
    }

    public void updateFilesList() {
        System.out.println("UPDATE FILES LIST");

        filesList.removeAll();
        filesList.add(new JLabel("Nombre de fichiers en cours de synchronisation :"+dlFiles.size()));

        for(String file: dlFiles.values()) {
            Box f = Box.createVerticalBox();
            f.setAlignmentX(LEFT_ALIGNMENT);
            f.setAlignmentY(TOP_ALIGNMENT);
            f.setBorder(new EmptyBorder(0, 3, 5, 5));
            f.setBackground(Color.WHITE);

            f.setMaximumSize(new Dimension(filesList.getWidth(), filesList.getHeight() / 10));

            Label type = new Label(file);
            type.setAlignment(Label.LEFT);
            f.add(type);

            //Label message = new Label(file.getMessage());
            //message.setAlignment(Label.RIGHT);
            //ev.add(message);

            filesList.add(f);
        }

        filesList.add(Box.createVerticalGlue());
        center.revalidate();
    }

    public void update() {
        updateFilesList();

        revalidate();
    }
}
