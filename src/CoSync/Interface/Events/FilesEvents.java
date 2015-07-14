package CoSync.Interface.Events;

import CoSync.CoController;
import CoSync.Interface.CoFileMenu;
import CoSync.Services.FilesServices;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Alban on 11/07/2015.
 */
public class FilesEvents {
    CoFileMenu fileMenu;
    CoController controller;

    public FilesEvents(CoFileMenu fileMenu, CoController controller) {
        this.fileMenu = fileMenu;
        this.controller = controller;
    }

    private class AddButtonEvent implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            fileMenu.setAddMenuVisibility(true);
        }
    }

    private class DeleteButtonEvent implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if(fileMenu.getSelected() != null) {
                    FilesServices.deleteFile(fileMenu.getSelected());
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    public DeleteButtonEvent getDeleteButtonEvent() {
        return new DeleteButtonEvent();
    }

    public AddButtonEvent getAddButtonEvent() {
        return new AddButtonEvent();
    }
}
