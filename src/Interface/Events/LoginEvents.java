package Interface.Events;

import Controllers.CoController;
import Interface.CoLoginMenu;
import sun.security.util.Password;

import javax.swing.*;
import javax.swing.text.PasswordView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Alban on 17/06/2015.
 */
public class LoginEvents {

    private CoLoginMenu loginMenu;
    private CoController controller;

    public LoginEvents(CoController controller, CoLoginMenu loginMenu) {
        this.controller = controller;
        this.loginMenu = loginMenu;
    }


    private class LoginButtonEvents implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if((loginMenu.getLogin().getText().length() > 0) && (loginMenu.getPassword().getPassword().length > 0)) {

                //Vérification des données saisies
                try {
                    controller.logIn(loginMenu.getLogin().getText(), loginMenu.getPassword().getText());
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            else {
                JOptionPane.showMessageDialog(null, "Veuillez saisir toutes les informations demandées");
            }
        }
    }

    public LoginButtonEvents getLoginButtonEvents() {
        return new LoginButtonEvents();
    }
}
