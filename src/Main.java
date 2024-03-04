import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import javax.imageio.ImageIO;

abstract class Object extends JPanel implements Serializable {
    private int x;
    private int y;

    private Random random;
    private transient BufferedImage img;

    abstract public void BroMove();
}


class Viksa_beer extends Object implements Serializable {
    private int x;
    private int y;
    public int heartWidth = 64;
    public int heartHeight = 64;


    private final Random random;
    private transient BufferedImage img;

    Viksa_beer(int x, int y) {
        this.x = x;
        this.y = y;
        this.random = new Random();
        URL resource = getClass().getResource("1122.png");
        try {
            assert resource != null;
            img = ImageIO.read(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ImageIO.write(img, "png", byteStream);

        out.writeObject(byteStream.toByteArray());
        System.out.println("Ебать тебы во все дыры, сериализованнность");
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        byte[] imageData = (byte[]) in.readObject();

        // Deserialize byte array to BufferedImage
        ByteArrayInputStream byteStream = new ByteArrayInputStream(imageData);
        img = ImageIO.read(byteStream);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(this.img, this.getX(), this.getY(), null);
    }

}



class MyPanel extends JPanel implements MouseListener, Serializable {
    private java.util.List<Viksa_beer> Viksas_beer = new ArrayList<>();
    private BufferedImage backgroundImage;
    private Timer timer;

    MyPanel(int PanelWidth, int PanelHeight) {
        this.setPreferredSize(new Dimension(PanelWidth, PanelHeight));
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
                if (e.getKeyCode() == KeyEvent.VK_SPACE) timer.stop();
                if (e.getKeyCode() == KeyEvent.VK_M) Serialization_on_key();
                if (e.getKeyCode() == KeyEvent.VK_N) Deserialization_on_key();
            }
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                timer.restart();
            }
        });
    }
    
    public void addViksa(Viksa_beer Viksa) {Viksas_beer.add(Viksa);}

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        BufferedImage backgroundImage = getBackgroundImage();
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
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

    private void Serialization_on_key() {
        for (int i = 0; i < Viksas_beer.size(); i++) {
            String ser_name = String.format("./Serialization/Viksa_%d.ser", i);
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(ser_name))) {
                out.writeObject(Viksas_beer.get(i));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void Deserialization_on_key() {
        String directoryPath = "./Serialization";
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();
        java.util.List<File> ListFiles = Arrays.stream(files).toList();
        for (int i = 0; i < ListFiles.size(); i++) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(ListFiles.get(i)))) {
                Viksa_beer UnserializedViksa = (Viksa_beer) in.readObject();
                Viksas_beer.add(UnserializedViksa);
            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }

    private BufferedImage getBackgroundImage() {
        BufferedImage backgroundImage = null;
        URL resource = getClass().getResource("./beer_background.jpg");
        try {
            assert resource != null;
            backgroundImage = ImageIO.read(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return backgroundImage;
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


