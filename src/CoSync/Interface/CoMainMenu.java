package CoSync.Interface;

import CoSync.CoController;
import CoSync.Models.CoEvent;
import CoSync.Interface.Events.InterfaceEvents;
import CoSync.Models.Cosystem;
import CoSync.Models.Couser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;

/**
 * Created by Alban on 15/06/2015.
 */
public class CoMainMenu extends CoInterface{

    private CoController controller;
    private Dimension dimInterface;

    private JPanel center;
    private JPanel east;
    private JPanel south;

    private JPanel eventsPanel;
    private JToolBar toolBar;

    private Label header;

    public CoMainMenu(CoController coController) {

        controller = coController;
        getContentPane().setLayout(new BorderLayout());
        addWindowListener(new InterfaceEvents());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        dimInterface = new Dimension(1280, 720);
        setSize(dimInterface);
        setTitle("CoSync");

        setCenter();
        setEast();
        setSouth();

        setVisible(true);
    }

    private void setCenter() {
        center = new JPanel();
        center.setLayout(new BorderLayout());

        header = new Label();
        center.add(header, BorderLayout.NORTH);

        eventsPanel = new JPanel();
        eventsPanel.add(new Label("Liste des modifications de fichiers"));
        center.add(eventsPanel, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
    }

    private void setEast() {
        east = new JPanel();
        east.setBorder(new LineBorder(Color.black));
        east.setPreferredSize(new Dimension(dimInterface.width / 5,  dimInterface.height));

        add(east, BorderLayout.EAST);
    }

    private void setSouth() {
        south = new JPanel();
        south.setLayout(new BoxLayout(south, BoxLayout.X_AXIS));
        south.setPreferredSize(new Dimension(dimInterface.width,  dimInterface.height / 20));

        final JButton quit = new JButton("Quitter");
        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        quit.setAlignmentX(RIGHT_ALIGNMENT);

        final JButton editFiles = new JButton("Mon CoSync");
        editFiles.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    controller.switchView("managefiles");
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        });
        editFiles.setAlignmentX(LEFT_ALIGNMENT);

        toolBar = new JToolBar();
        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(editFiles);
        toolBar.add(quit);

        south.add(toolBar);

        add(south, BorderLayout.SOUTH);
    }

    public void updateHeader(Couser user) {
        System.out.println("UPDATE HEADER");

        header.setText(user.getName());
        header.setFont(new Font("Sans Serif", Font.BOLD, 25));

        header.repaint();
    }

    public void updateListSystem(Couser user) {
        System.out.println("UPDATE LIST SYSTEM");
        east.removeAll();

        Box sysBox = Box.createVerticalBox();

        for(Cosystem system: user.getCosystems()) {

            Box sys = Box.createVerticalBox();
            sys.setAlignmentX(RIGHT_ALIGNMENT);
            sys.setAlignmentY(TOP_ALIGNMENT);
            sys.setBorder(new EmptyBorder(0,3,5,5));
            sys.setBackground(Color.WHITE);

            sys.setPreferredSize(new Dimension(east.getWidth(), east.getHeight() / 10 - 10));

            Label name = new Label(system.getName());
            name.setAlignment(Label.RIGHT);
            sys.add(name);

            Label ip = new Label(system.getIp());
            ip.setAlignment(Label.RIGHT);
            sys.add(ip);

            sysBox.add(sys);
        }

        sysBox.add(Box.createVerticalGlue());
        east.add(sysBox);

        east.revalidate();
    }

    public void updateListEvents(Stack<CoEvent> events) {
        System.out.println("UPDATE LIST EVENTS");
        System.out.println(events.size());
        eventsPanel.removeAll();

        Box evList = Box.createVerticalBox();
        for(CoEvent event: events) {
            System.out.println("message => "+event.getMessage());
            Box ev = Box.createVerticalBox();
            ev.setAlignmentX(RIGHT_ALIGNMENT);
            ev.setAlignmentY(TOP_ALIGNMENT);
            ev.setBorder(new EmptyBorder(0,3,5,5));
            ev.setBackground(Color.WHITE);

            ev.setPreferredSize(new Dimension(center.getWidth(), center.getHeight() / 10 - 10));

            Label type = new Label(event.getType());
            type.setAlignment(Label.RIGHT);
            ev.add(type);

            Label message = new Label(event.getMessage());
            message.setAlignment(Label.RIGHT);
            ev.add(message);

            evList.add(ev);
        }

        evList.add(Box.createVerticalGlue());
        eventsPanel.add(evList);
        eventsPanel.revalidate();
    }

    public void update() {
        updateHeader(controller.getUser());
        updateListSystem(controller.getUser());
    }
}
