import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import com.thoughtworks.xstream.*;

abstract class Object extends JPanel implements Serializable {
    private int x;
    private int y;

    transient Random random;
    transient BufferedImage img;

    @Serial
    private static final long serialVersionUID = 7380655875865432001L;

    public abstract void BroMove();

    public abstract void txtOut();
    public abstract void txtIn();

    public abstract void BinaryOut();
    public abstract void BinaryIn();
}

class Viksa_beer extends Object implements Serializable {
    private int x;
    private int y;
    public int heartWidth = 64;
    public int heartHeight = 64;
    public boolean stateFlag;

    transient URL resource;

    private final String ImagePath = "1122.png";

    Viksa_beer(int x, int y) {
        this.x = x;
        this.y = y;
        this.stateFlag = false;
        super.random = new Random();
        resource = getClass().getResource(ImagePath);
        try {
            assert resource != null;
            super.img = ImageIO.read(resource);
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
        double deltaX = 0;
        double deltaY = 0;
        if (!this.stateFlag) {
            double randomSeed = random.nextDouble(-10, 10) * 2 * Math.PI;
            deltaX = 10 * Math.cos(randomSeed);
            deltaY = 10 * Math.sin(randomSeed);
        }
        this.x += deltaX;
        this.y += deltaY;
    }


    @Override
    public void txtOut() {
        final String txtOutPath = String.format("src%smain%sresources%stxtOut%stxtViksas.txt",
                File.separator, File.separator, File.separator, File.separator);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(txtOutPath, true))) {
            writer.write(this.dataToString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void txtIn() {

    }

    @Override
    public void BinaryOut() {

    }

    @Override
    public void BinaryIn() {

    }

    public void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ImageIO.write(img, "png", byteStream);
        out.writeObject(byteStream.toByteArray());
    }

    public void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        byte[] imageData = (byte[]) in.readObject();
        ByteArrayInputStream byteStream = new ByteArrayInputStream(imageData);
        img = ImageIO.read(byteStream);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(this.img, this.getX(), this.getY(), null);
    }

    private String dataToString() {
        return "Viksa " + this.getX() + ' ' + this.getY() + ' ' + this.ImagePath + '\n';
    }
}



class MyPanel extends JPanel implements MouseListener, Serializable {
    private static Logger log = Logger.getLogger(Viksa_beer.class.getName());
    private java.util.List<Viksa_beer> Viksas_beer = new ArrayList<>();
    private BufferedImage backgroundImage;
    private Timer timer;

    MyPanel(int PanelWidth, int PanelHeight) {
        this.setPreferredSize(new Dimension(PanelWidth, PanelHeight));
        this.addMouseListener(this);

        timer = new Timer(32, e -> {
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
                if (e.getKeyCode() == KeyEvent.VK_1) binary_serialization_on_key();
                if (e.getKeyCode() == KeyEvent.VK_2) binary_deserialization_on_key();
                if (e.getKeyCode() == KeyEvent.VK_3) {
                    try {
                        txt_ot_on_key();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_4) xml_serialization_on_key();
                if (e.getKeyCode() == KeyEvent.VK_C) Viksas_beer.clear();
                if (e.getKeyCode() == KeyEvent.VK_DELETE) drop_all_ser();
            }
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    super.keyReleased(e);
                    timer.restart();
                }
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
        boolean thisViksa = false;
        for (Viksa_beer Viksa : Viksas_beer) {
            Rectangle ImageBounds = new Rectangle(Viksa.getX(), Viksa.getY(), 52, 76);
            if (ImageBounds.contains(e.getX(), e.getY())) {
                if (Viksa.stateFlag) {
                    Viksa.stateFlag = false;
                } else {
                    Viksa.stateFlag = true;
                }
                thisViksa = true;
                break;
            }
        }
        if (!thisViksa) {
            int initialX = e.getX() - (52 / 2);
            int initialY = e.getY() - (76 / 2);
            this.addViksa(new Viksa_beer(initialX, initialY));
        }
    }

    private void drop_all_ser() {
        String directoryPath = "./Serialization/";
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    file.delete();
                }
            }
        }
        log.info("\"Dropped all ser's\"");
    }

    private void binary_serialization_on_key() {
        int successfully_serialized = 0;
        for (int i = 0; i < Viksas_beer.size(); i++) {
            String ser_name = String.format("src%smain%sresources%sSerialization%sViksa_%d.ser",
                    File.separator, File.separator, File.separator, File.separator, i);
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(ser_name))) {
                out.writeObject(Viksas_beer.get(i));
                successfully_serialized++;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        log.info(String.format("%d objects has been serialized", successfully_serialized));
    }



    private void binary_deserialization_on_key() {
        int successfully_unserialized = 0;
        String directoryPath = String.format("src%smain%sresources%sSerialization%s",
                File.separator, File.separator, File.separator, File.separator);
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();
        java.util.List<File> ListFiles = Arrays.stream(files).toList();
        for (int i = 0; i < ListFiles.size(); i++) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(ListFiles.get(i)))) {
                Viksa_beer UnserializedViksa = (Viksa_beer) in.readObject();
                if (UnserializedViksa != null) {
                    successfully_unserialized++;
                }
                Viksas_beer.add(UnserializedViksa);
            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        log.info(String.format("%d objects has been unserialized", successfully_unserialized));
    }

    private void xml_serialization_on_key() {
        XStream xstream = new XStream();
        for (Viksa_beer Viksa : Viksas_beer) {
            System.out.println(xstream.toXML(Viksa));
        }
    }

    private void xml_deserialization_on_key() {

    }


    private void txt_ot_on_key() throws IOException {
        int successfully_txt_out = 0;
        String directoryPath = String.format("src%smain%sresources%ssrc%stxtOut%stxtViksas.txt",
                File.separator, File.separator, File.separator, File.separator, File.separator);
        File file = new File(directoryPath);
        if (file.isFile()) file.delete();
        for (Viksa_beer viksaBeer : Viksas_beer) {
            viksaBeer.txtOut();
            successfully_txt_out++;
        }
        log.info(String.format("%d objects has been written in txt", successfully_txt_out));
    }

    private java.util.List<String> get_FileLines() {
        String filePath = "example.txt"; // Путь к вашему файлу
        java.util.List<String> listLine = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                listLine.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listLine;
    }


    private BufferedImage getBackgroundImage() {
        BufferedImage backgroundImage = null;
        URL resource = getClass().getResource("./beer_background.jpg");
        try {
            if (resource != null) {
                backgroundImage = ImageIO.read(resource);
            }
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