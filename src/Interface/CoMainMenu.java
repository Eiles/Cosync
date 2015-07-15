package Interface;

import Controllers.CoController;
import Models.CoEvent;
import Interface.Events.InterfaceEvents;
import Models.Cosystem;
import Models.Couser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;

/**
 * Created by Alban on 15/06/2015.
 */
public class CoMainMenu extends CoInterface{

    private Dimension dimInterface;

    private JPanel center;
    private JPanel east;
    private JPanel south;

    private JScrollPane eventsPanel;
    private JScrollPane sysPanel;
    private Box evList;
    private Box sysList;

    private JToolBar toolBar;

    private Label header;

    public CoMainMenu(CoController coController) {

        super(coController);

        getContentPane().setLayout(new BorderLayout());
        addWindowListener(new InterfaceEvents());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        dimInterface = new Dimension(800, 600);
        setSize(dimInterface);
        setTitle("CoSync");

        setCenter();
        setSouth();
        setEast();
    }

    private void setCenter() {
        center = new JPanel();
        center.setLayout(new BorderLayout());

        header = new Label();
        center.add(header, BorderLayout.NORTH);

        evList = Box.createVerticalBox();
        evList.add(new Label("Liste des modifications de fichiers"));
        eventsPanel = new JScrollPane(evList);

        center.add(eventsPanel, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
    }

    private void setEast() {
        east = new JPanel();
        east.setLayout(new BorderLayout());
        east.setPreferredSize(new Dimension(dimInterface.width / 5, dimInterface.height - south.getHeight()));

        east.add(new JLabel("Mes Systèmes"), BorderLayout.NORTH);
        east.setBorder(new EmptyBorder(0, 0, 0, 0));

        sysList = Box.createVerticalBox();
        sysPanel = new JScrollPane(sysList);

        east.add(sysPanel, BorderLayout.CENTER);
        add(east, BorderLayout.EAST);
    }

    private void setSouth() {
        south = new JPanel();
        south.setLayout(new BoxLayout(south, BoxLayout.X_AXIS));
        south.setPreferredSize(new Dimension(dimInterface.width,  dimInterface.height / 20));

        final JButton quit = new JButton("Déconnexion");
        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    controller.logOut();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
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

        final JButton downloads = new JButton("Téléchargements");
        downloads.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    controller.showView("downloads");
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        });
        downloads.setAlignmentX(LEFT_ALIGNMENT);

        toolBar = new JToolBar();
        toolBar.add(editFiles);
        toolBar.add(downloads);
        toolBar.add(Box.createHorizontalGlue());
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
        sysList.removeAll();

        for(Cosystem system: user.getCosystems()) {

            Box sys = Box.createVerticalBox();
            sys.setAlignmentX(RIGHT_ALIGNMENT);
            sys.setAlignmentY(TOP_ALIGNMENT);

            if(system.getOnline())
                sys.setBackground(Color.WHITE);
            else
                sys.setBackground(Color.GRAY);

            sys.setBorder(new EmptyBorder(0,3,5,5));

            sys.setMaximumSize(new Dimension((int) east.getWidth() - 10, (int) (east.getHeight() / 5 - 10)));

            Label name = new Label(system.getName());
            name.setAlignment(Label.LEFT);
            sys.add(name);

            Label ip = new Label(system.getIp());
            ip.setAlignment(Label.RIGHT);
            sys.add(ip);

            sysList.add(sys);
        }

        sysList.add(Box.createVerticalGlue());
        east.revalidate();
    }


    public void updateListEvents(Stack<CoEvent> events) {
        System.out.println("UPDATE LIST EVENTS");
        System.out.println(events.size());

        evList.removeAll();

        for(CoEvent event: events) {
            Box ev = Box.createVerticalBox();
            ev.setAlignmentX(LEFT_ALIGNMENT);
            ev.setAlignmentY(TOP_ALIGNMENT);
            ev.setBorder(new EmptyBorder(0,3,5,5));
            ev.setBackground(Color.WHITE);

            ev.setMaximumSize(new Dimension(evList.getWidth(), evList.getHeight() / 10));

            Label type = new Label(event.getType());
            type.setAlignment(Label.RIGHT);
            ev.add(type);

            Label message = new Label(event.getMessage());
            message.setAlignment(Label.RIGHT);
            ev.add(message);

            evList.add(ev);
        }

        evList.add(Box.createVerticalGlue());
        center.revalidate();
    }

    public void update() {
        updateHeader(controller.getUser());
        updateListSystem(controller.getUser());
        updateListEvents(controller.getEvents());

        revalidate();
    }
}
