package CoSync.Interface;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Alban on 11/07/2015.
 */
public class AddMenu extends JFrame {
    private JPanel fields;

    private JTextField addFile;
    private JTextField newFile;

    private JButton validButton;
    private JButton cancelButton;

    public AddMenu() throws HeadlessException {
        setLayout(new BorderLayout());

        setTitle("Ajout d'un fichier");

        createFields();
        createButtons();

        pack();
    }

    private void createFields() {
        fields = new JPanel(new GridLayout(2,2));
        //fields.setSize(new Dimension(dimension.width, dimension.height / 2));

        addFile = new JTextField();
        //addFile.setPreferredSize(new Dimension(dimension.width / 2, 10));
        addFile.setEditable(true);

        newFile = new JTextField();
        //newFile.setPreferredSize(new Dimension(dimension.width / 2, 10));
        newFile.setEditable(true);

        fields.add(new Label("Fichier à ajouter"));
        fields.add(addFile);

        fields.add(new Label("Nouveau chemin du fichier"));
        fields.add(newFile);

        add(fields, BorderLayout.CENTER);
    }

    private void createButtons() {
        validButton = new JButton("Valider");
        cancelButton = new JButton("Annuler");

        JPanel buttons = new JPanel();
        buttons.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridheight = 1; gbc.gridwidth = 1;
        gbc.gridx = 1;
        buttons.add(validButton, gbc);

        gbc.gridx = 2;
        buttons.add(new Label(""), gbc);

        gbc.gridx = 3;
        buttons.add(cancelButton, gbc);

        add(buttons, BorderLayout.SOUTH);
    }
}
