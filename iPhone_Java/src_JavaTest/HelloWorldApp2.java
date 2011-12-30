import javax.iswing.JButton;
import javax.iswing.JFrame;
import javax.iswing.JLabel;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HelloWorldApp2 extends JFrame implements ActionListener {

    private JLabel label;

    public HelloWorldApp2() {
        this.setLayout(null);
        this.setBackground(Color.WHITE);

        JButton button=new JButton("Click Me");
        label=new JLabel();
        this.add(button);
        this.add(label);
        button.addActionListener(this);

        button.setBounds(110,100,100,40);
        label.setBounds(50,200,220,50);
        label.setBackground(Color.PINK);
        label.setHorizontalAlignment(SwingConstants.CENTER);
    }

    public void actionPerformed(ActionEvent e) {
        label.setText("Hello World!");
    }

    public static void main(String[] args) throws Exception {
        HelloWorldApp2 frame=new HelloWorldApp2();
        //frame.setBounds(100,100,320,480);
        //frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

}