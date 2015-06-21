package CoSync.Interface;

import CoSync.Interface.Events.LoginEvents;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Alban on 16/06/2015.
 */
public class LoginMenu extends JFrame {

    private boolean isLogged;

    private LoginEvents loginEvents;
    private Dimension dimension;

    private JPanel fields;

    private JTextField login;
    private JPasswordField password;

    private JButton logButton;
    private JButton cancelButton;

    public LoginMenu() {

        isLogged = false;
        loginEvents = new LoginEvents(this);
        dimension = new Dimension(400, 300);
        setLayout(new BorderLayout());

        setTitle("Connexion");

        createFields();
        createButtons();

        pack();

        setVisible(true);
        setEnabled(true);
    }

    private void createFields() {
        fields = new JPanel(new GridLayout(2,2));
        fields.setSize(new Dimension(dimension.width, dimension.height / 2));

        login = new JTextField();
        login.setPreferredSize(new Dimension(dimension.width / 2, 10));
        login.setEditable(true);

        password = new JPasswordField();
        password.setPreferredSize(new Dimension(dimension.width / 2, 10));
        password.setEditable(true);

        fields.add(new Label("Identifiant"));
        fields.add(login);

        fields.add(new Label("Mot de Passe"));
        fields.add(password);

        add(fields, BorderLayout.CENTER);
    }

    private void createButtons() {
        logButton = new JButton("Connexion");
        cancelButton = new JButton("Annuler");

        logButton.setPreferredSize(new Dimension(dimension.width / 3, 20));
        logButton.addActionListener(loginEvents.getLoginButtonEvents());

        cancelButton.setPreferredSize(new Dimension(dimension.width / 3, 20));

        JPanel buttons = new JPanel();
        buttons.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridheight = 1; gbc.gridwidth = 1;
        gbc.gridx = 1;
        buttons.add(logButton, gbc);

        gbc.gridx = 2;
        buttons.add(new Label(""), gbc);

        gbc.gridx = 3;
        buttons.add(cancelButton, gbc);

        add(buttons, BorderLayout.SOUTH);
    }

    public JTextField getLogin() {
        return login;
    }

    public void setLogin(JTextField login) {
        this.login = login;
    }

    public JPasswordField getPassword() {
        return password;
    }

    public void setPassword(JPasswordField password) {
        this.password = password;
    }

    public boolean isLogged() {
        return this.isLogged;
    }

    public void setLogged(boolean isLogged) {
        this.isLogged = isLogged;
    }
}
