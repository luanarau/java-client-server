import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.io.*;
import java.util.Objects;
import java.util.Random;

abstract class Object extends JPanel implements Serializable {
    private int x;
    private int y;

    private Random random;
    private transient Image img;

    abstract public void BroMove();
}


class Viksa_beer extends Object implements Serializable {
    private int x;
    private int y;
    public int heartWidth = 64;
    public int heartHeight = 64;


    private final Random random;
    private final transient Image img;

    Viksa_beer(int x, int y) {
        this.x = x;
        this.y = y;
        this.random = new Random();
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
        double randomSeed = random.nextDouble(-10, 10) * 2 * Math.PI;

        double deltaX = 10 * Math.cos(randomSeed);
        double deltaY = 10 * Math.sin(randomSeed);

        this.x += deltaX;
        this.y += deltaY;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(this.img, this.getX(), this.getY(), null);
    }

    public void serialization() {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("seri.ser"))) {
            objectOutputStream.writeObject(this);
            System.out.println("Object has been serialized.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



class MyPanel extends JPanel implements MouseListener {
    private java.util.List<Viksa_beer> Viksas_beer = new java.util.ArrayList<>();
    private Timer timer;

    MyPanel(int PanelWidth, int PanelHeight) {
        this.setPreferredSize(new Dimension(PanelWidth, PanelHeight));
        this.setBackground(Color.PINK);
        this.addMouseListener(this);

        timer = new Timer(128, e -> {
            for (Viksa_beer Viksa : Viksas_beer) {
                Viksa.BroMove();
            }
            repaint();
        });
        timer.start();

        setFocusable(true); // Чтобы KeyListener считывал текущую панель
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    timer.stop();
                    for (Viksa_beer Viksa : Viksas_beer) {
                        serialization(Viksa);
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                timer.restart();
            }
        });
    }
    
    public void addViksa(Viksa_beer Viksa) {
        Viksas_beer.add(Viksa);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Viksa_beer Viksa : Viksas_beer) {
            Viksa.paintComponent(g);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int initialX = e.getX() - (52 / 2);  // штука чтобы сердце спавнилось по центру мышки
        int initialY = e.getY() - (76 / 2);  // доп доп ес ес
        this.addViksa(new Viksa_beer(initialX, initialY));
    }

    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}


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


