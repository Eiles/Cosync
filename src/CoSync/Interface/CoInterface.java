package CoSync.Interface;

import CoSync.Interface.Events.InterfaceEvents;
import CoSync.Models.Couser;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Created by Alban on 15/06/2015.
 */
public class CoInterface extends JFrame{

    private Dimension dimInterface;

    private JPanel center;
    private JPanel east;

    private Label header;

    public CoInterface () {
        setLayout(new BorderLayout());
        addWindowListener(new InterfaceEvents());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        dimInterface = new Dimension(800, 600);
        setSize(dimInterface);
        setTitle("CoSync");

        setCenter();
        setEast();

        setVisible(true);
    }

    private void setCenter() {
        center = new JPanel();
        center.setLayout(new BorderLayout());

        header = new Label();
        center.add(header, BorderLayout.NORTH);

        add(center, BorderLayout.CENTER);
    }

    private void setEast() {
        east = new JPanel();
        east.setBorder(new LineBorder(Color.black));
        east.setSize(new Dimension(50, (int)dimInterface.getHeight()));

        east.add(new Label("EAST PANEL"));

        add(east, BorderLayout.EAST);
    }

    public void updateHeader(Couser user) {
        System.out.println("UPDATE HEADER");

        header.setText(user.getName());
        header.repaint();
    }

    public void updateListSystem(Couser user) {
        System.out.println("UPDATE LIST SYSTEM");

        east.add(new Label(user.getCosystems().get(0).getName()));
        east.repaint();
    }
}
