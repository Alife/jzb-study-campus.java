import javax.iswing.*;
import javax.iswing.JButton;
import javax.iswing.JCheckBox;
import javax.iswing.JFrame;
import javax.iswing.JLabel;
import javax.iswing.JProgressBar;
import javax.iswing.JSlider;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HelloWorldApp3 extends JFrame implements ActionListener,ReadyListener {

    private JLabel label;

    public HelloWorldApp3() {
        this.setLayout(null);
        this.setBackground(Color.WHITE);

        JButton button=new JButton("Click Me");
        label=new JLabel();
        this.add(button);
        this.add(label);
        button.addActionListener(this);

        button.setBounds(110,5,100,40);
        label.setBounds(50,50,220,50);
        label.setBackground(Color.PINK);
        label.setHorizontalAlignment(SwingConstants.CENTER);

        JDatePicker picker=new JDatePicker();
        picker.setBounds(0,105,320,220);
        this.add(picker);

        JSlider slider=new JSlider();
        this.add(slider);
        slider.setBackground(Color.WHITE);
        slider.setBounds(5,330,310,30);

        JProgressBar progress=new JProgressBar(0,100);
        this.add(progress);
        progress.setBounds(5,365,310,15);
        progress.setValue(50);

        ActivityIndicator indicator=new ActivityIndicator();
        this.add(indicator);
        indicator.setBounds(1,1,45,45);
        indicator.addReadyListener(this);

        JCheckBox check=new JCheckBox();
        this.add(check);
        check.setBounds(220,5,90,30);

        Browser browser=new Browser();
        this.add(browser);
        browser.setBounds(5,385,310,300);
        browser.addReadyListener(this);
    }

    public void onReady(ReadyEvent e) {
        Object source = e.getSource();
        if(source instanceof Browser) {
            ((Browser)source).loadUrl("http://www.google.cn/");
        } else if(source instanceof ActivityIndicator) {
            ((ActivityIndicator)source).startAnimating();
        }
    }

    public void actionPerformed(ActionEvent e) {
        label.setText("Hello World!");
    }

    public static void main(String[] args) throws Exception {
        HelloWorldApp3 frame=new HelloWorldApp3();
        frame.setVisible(true);
    }

}