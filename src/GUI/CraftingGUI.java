package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;



public class CraftingGUI extends JFrame {

    // public static CraftingGUI instance;
    public JButton startButton;
    public JButton stopButton;
    public JCheckBox leapingSturgeonCheckBox;
    public JCheckBox chocolateBarCheckBox;
    public JCheckBox fillerCheckBox;
    public JPanel MainPanel;
    public JPanel SettingsPanel;
    public JPanel CraftingPanel;

    public boolean isRunning = false;
    public boolean shouldStop = false;

    public List<String> craftables = new ArrayList<>();

    public CraftingGUI() {
        // Set up the UI on the Event Dispatch Thread (EDT)
        // instance = this;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Set up JFrame settings
                setTitle("Crafting");
                setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                //setLocationRelativeTo(Client.getCanvas());
                setLocationRelativeTo(null);
                setPreferredSize(new Dimension(300, 170));


                // Add your panels (MainPanel) to the frame
                add(MainPanel);
                pack(); // This sizes the frame based on the contents
                setVisible(true); // Make sure the frame is visible

                startButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        isRunning = true;
                    }
                });
                stopButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        isRunning = false;
                    }
                });

                leapingSturgeonCheckBox.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (leapingSturgeonCheckBox.isSelected()) {
                            craftables.add(leapingSturgeonCheckBox.getText());
                        }
                    }
                });
                chocolateBarCheckBox.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (chocolateBarCheckBox.isSelected()) {
                            craftables.add(chocolateBarCheckBox.getText());
                        }
                    }
                });
            }
        });

    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean shouldStop() {
        return shouldStop;
    }

    public List<String> getCraftables() {
        return craftables;
    }

}
