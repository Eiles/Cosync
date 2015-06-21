package CoSync.Interface.Events;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by Alban on 16/06/2015.
 */
public class InterfaceEvents extends WindowAdapter {
    @Override
    public void windowClosing(WindowEvent e) {
        if(JOptionPane.showConfirmDialog(null, "Êtes-vous sûr de vouloir quitter CoSync?") == 0) {
            System.exit(0);
        }
    }
}
