import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.io.IOException;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


abstract class object extends JFrame {
    public int StartingPosX;
    public int StartingPosY;

//    public BufferedImage img;

    abstract public void MoveImg();
    abstract public void StopImg();
    abstract public void DeleteImg();

}


class HeartThread extends Thread {

    private MyPanel panel;
    private Heart heart;

    HeartThread(MyPanel panel, int x, int y) {
        this.panel = panel;
        this.heart = new Heart(x, y);
    }

    @Override
    public void run() {
        panel.addHeart(heart);
    }
}

class Heart extends JPanel {
    private int x;
    private int y;
    public int heartWidth = 64;
    public int heartHeight = 64;

    private Random random;
    Image img;
    Timer timer;

    Heart(int x, int y) {
        this.x = x;
        this.y = y;
        img = new ImageIcon(Objects.requireNonNull(getClass().getResource("/1122.png"))).getImage();
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public Image getImg() {
        return this.img;
    }

    public void BroMove() {
        double randomSeed = ThreadLocalRandom.current().nextDouble() * 2 * Math.PI;

        double deltaX = Math.cos(randomSeed);
        double deltaY = Math.sin(randomSeed);

        this.x += deltaX;
        this.y += deltaY;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(this.img, this.getX(), this.getY(), null);
    }
}

class MyPanel extends JPanel implements MouseListener {
    private java.util.List<Heart> hearts = new java.util.ArrayList<>();
    private Timer timer;

    MyPanel(int PanelWidth, int PanelHeight) {
        this.setPreferredSize(new Dimension(PanelWidth, PanelHeight));
        this.setBackground(Color.PINK);
        this.addMouseListener(this);

        timer = new Timer(32, e -> {
            for (Heart heart : hearts) {
                heart.BroMove();
            }
            repaint();
        });
        timer.start();
    }

    public void addHeart(Heart heart) {
        hearts.add(heart);
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (Heart heart : hearts) {
            heart.paintComponent(g);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int initialX = e.getX() - (52 / 2);  // штука чтобы сердце спавнилось по центру мышки
        int initialY = e.getY() - (76 / 2);  // доп доп ес ес

        HeartThread heartThread = new HeartThread(this, initialX, initialY);
        heartThread.start();

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}

class Window  {

    final private int width = 720;
    final private int height = 1280;

    JFrame window_frame = new JFrame();
    MyPanel panel = new MyPanel(width, height);

    public Window() {
        window_frame.setSize(width, height);
        window_frame.setTitle("Shitty Java Program");
        window_frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        window_frame.add(panel);
        window_frame.pack();
        window_frame.setLocationRelativeTo(null);
        window_frame.setResizable(false);
        window_frame.setVisible(true);
    }
}



public class Main {
    public static void main(String[] args) throws IOException {
        Window a = new Window();
    }
}


