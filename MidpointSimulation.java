/**
 * @author Miłosz Demendecki s24611
 */

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * I have created an example in Excel with chart and data copied from this program
 */
public class MidpointSimulation extends JFrame{
    private static final int g = 10;
    private static boolean inRadians = false;

    private static final ArrayList<Double> xCenterOfMassList = new ArrayList<>();
    private static final ArrayList<Double> yCenterOfMassList = new ArrayList<>();
    private static final ArrayList<Double> xRotationAngleList = new ArrayList<>();
    private static final ArrayList<Double> yRotationAngleList = new ArrayList<>();
    private static final ArrayList<Double> ekList = new ArrayList<>();
    private static final ArrayList<Double> epList = new ArrayList<>();
    private static final ArrayList<Double> etList = new ArrayList<>();

    public static void main(String[] args) {
        createFrame();
    }

    /**
     * Function that adds records from list specified as parameter to text area,
     * so user can easily copy records and paste it to e.g. excel
     */
    public static void addListToArea(ArrayList<Double> list, String str, JTextArea textArea, JButton button, int rows){
        for (int i = 0; i < rows; i++) {
            textArea.append(list.get(i).toString().replace(".",",") + "\n");
            button.setText(str);
        }
    }

    /**
     * Function that calculates x and y for both motions (motion of the centre of mass and rolling motion)
     * also method calculates Ek, Ep and Et
     */
    public static void calculationsForSimulation(int rows, double alpha, double mass, double h, double r, double dt, boolean isHollow) {
        double sX_R = 0;
        double sY_R = r;
        double v = 0;
        double I;
        if(isHollow){
            I = (double) 2 / 3 * mass * Math.pow(r,2);
        } else {
            I = (double) 2 / 5 * mass * Math.pow(r,2);
        }
        double a = g * Math.sin(alpha)/(1 + I /(mass * Math.pow(r, 2)));

        double beta = 0;
        double omega = 0;
        double epsilon = a / r;
        for (int i = 0; i < rows; i++) {
            //Calculations for the motion of the center of mass
            double v2 = v + a * dt / 2;
            double xCenter = sX_R * Math.cos(-alpha) - sY_R * Math.sin(-alpha);
            xCenterOfMassList.add(xCenter);

            double yCenter = sX_R * Math.sin(-alpha) + sY_R * Math.cos(-alpha) + h;
            yCenterOfMassList.add(yCenter);

            double dSX_R = v2 * dt;
            double dv = a * dt;


            //Calculations for rolling motion
            double omega2 = omega + epsilon * dt / 2;
            double dBeta = omega2 * dt;
            double dOmega = epsilon * dt;

            double xRotation = r * Math.sin(beta) + xCenter;
            xRotationAngleList.add(xRotation);
            double yRotation = r * Math.cos(beta) + yCenter;
            yRotationAngleList.add(yRotation);

            //Calculations for Energy graph
            double _h = yCenter - r;

            double ep = mass * g * _h;
            epList.add(ep);

            double ek = mass * Math.pow(v, 2) / 2 + I * Math.pow(omega, 2) / 2;
            ekList.add(ek);

            double et = ep + ek;
            etList.add(et);

            beta += dBeta;
            omega += dOmega;

            sX_R += dSX_R;
            v += dv;
        }
    }

    /**
     * Constructor that takes multiple parameters to calculate all values needed for
     * xCenter, yCenter (for the motion of the center of mass), xRotation, yRotation (for the rolling motion), Ek, Ep and Et to be computed
     * and adds them to corresponding lists. After that correlated text areas and buttons are added to help user to utilize
     * calculated data easily.
     */
    public MidpointSimulation(int rows, double alpha, double mass, double h, double r, double dt, boolean isHollow) {

        if(!inRadians) {
            alpha = Math.toRadians(alpha);
        }
        calculationsForSimulation(rows, alpha, mass, h, r, dt, isHollow);

        setTitle("Midpoint");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 300);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        for (int i = 1; i <= 7; i++) {
            JTextArea textArea = new JTextArea();
            JButton copyButton = new JButton();
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            textArea.setEditable(false);
            switch (i) {
                case 1 -> addListToArea(xCenterOfMassList,"Copy xCenter", textArea, copyButton, rows);
                case 2 -> addListToArea(yCenterOfMassList,"Copy yCenter", textArea, copyButton, rows);
                case 3 -> addListToArea(xRotationAngleList,"Copy xRotation", textArea, copyButton, rows);
                case 4 -> addListToArea(yRotationAngleList,"Copy yRotation", textArea, copyButton, rows);
                case 5 -> addListToArea(ekList,"Copy Ek", textArea, copyButton, rows);
                case 6 -> addListToArea(epList,"Copy Ep", textArea, copyButton, rows);
                case 7 -> addListToArea(etList,"Copy Et", textArea, copyButton, rows);
            }
            copyButton.addActionListener(e -> {
                textArea.selectAll();
                textArea.copy();
            });
            JPanel scrollPanel = new JPanel(new BorderLayout());
            scrollPanel.add(copyButton, BorderLayout.NORTH);
            scrollPanel.add(scrollPane, BorderLayout.CENTER);
            panel.add(scrollPanel);
        }
        getContentPane().add(panel);
        setVisible(true);
        setLocationRelativeTo(null);
    }

    /**
     * Function that creates user interface
     */
    public static void createFrame(){
        JFrame startFrame = new JFrame("Choose Calculation");
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel label1 = new JLabel("Provide number of rows:");
        JTextField textField = new JTextField();

        JLabel label2 = new JLabel("Provide α value in degrees:");
        JTextField alphaField = new JTextField();

        JLabel label3 = new JLabel("Provide mass of an object:");
        JTextField massField = new JTextField();

        JLabel label4 = new JLabel("Provide h value:");
        JTextField hField = new JTextField();

        JLabel label5 = new JLabel("Provide r value:");
        JTextField rField = new JTextField();

        JLabel label6 = new JLabel("Provide Δt value:");
        JTextField deltaTField = new JTextField();

        JButton calculateButton = new JButton("<html>Perform calculations using <br> Midpoint method for hollow sphere</html>");
        calculateButton.addActionListener(e -> {
            try {
                new MidpointSimulation(Integer.parseInt(textField.getText()),
                        Double.parseDouble(alphaField.getText()),Double.parseDouble(massField.getText()),
                        Double.parseDouble(hField.getText()), Double.parseDouble(rField.getText()), Double.parseDouble(deltaTField.getText()), true);
            } catch (NumberFormatException ex){
                System.out.println("Try again, remember to pass all arguments");
            }
        });
        JButton calculateButton2 = new JButton("<html>Perform calculations using <br> Midpoint method for full sphere</html>");
        calculateButton2.addActionListener(e -> {
            try {
                new MidpointSimulation(Integer.parseInt(textField.getText()),
                        Double.parseDouble(alphaField.getText()),Double.parseDouble(massField.getText()),
                        Double.parseDouble(hField.getText()), Double.parseDouble(rField.getText()), Double.parseDouble(deltaTField.getText()), false);
            } catch (NumberFormatException ex){
                System.out.println("Try again, remember to pass all arguments");
            }
        });


        JButton changeToRadiansOrDegreesButton = new JButton("Click me to provide alpha in radians");
        changeToRadiansOrDegreesButton.addActionListener(e -> {
            if(inRadians) {
                inRadians = false;
                label2.setText("Provide α value in degrees:");
                changeToRadiansOrDegreesButton.setText("Click me to provide α in radians");
            } else {
                inRadians = true;
                label2.setText("Provide α value in radians:");
                changeToRadiansOrDegreesButton.setText("Click me to provide α in degrees");
            }
        });

        startFrame.setResizable(true);
        panel.add(label1);
        panel.add(textField);

        panel.add(label2);
        panel.add(alphaField);

        panel.add(label3);
        panel.add(massField);

        panel.add(label4);
        panel.add(hField);

        panel.add(label5);
        panel.add(rField);

        panel.add(label6);
        panel.add(deltaTField);

        panel.add(calculateButton);
        panel.add(calculateButton2);

        panel.add(changeToRadiansOrDegreesButton);

        startFrame.getContentPane().add(panel);
        startFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        startFrame.setLocationRelativeTo(null);
        startFrame.setVisible(true);
        startFrame.pack();
    }
}