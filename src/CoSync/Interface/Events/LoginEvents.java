package CoSync.Interface.Events;

import CoSync.Interface.LoginMenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Alban on 17/06/2015.
 */
public class LoginEvents {

    private LoginMenu loginMenu;

    public LoginEvents(LoginMenu loginMenu) {
        this.loginMenu =loginMenu;
    }

    private class LoginButtonEvents implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if((loginMenu.getLogin().getText().length() > 0) && (loginMenu.getPassword().getPassword().length > 0)) {
                //Vérification des données saisies
                loginMenu.setLogged(true);
            }
        }
    }

    public LoginButtonEvents getLoginButtonEvents() {
        return new LoginButtonEvents();
    }
}
