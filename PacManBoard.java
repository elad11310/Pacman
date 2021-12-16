

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class PacManBoard extends JPanel implements ActionListener {


    private final int B_WIDTH = 760; // window width
    private final int B_HEIGHT = 800; // window height
    public static final int BLOCK_SIZE = 24; // each cell size
    public static final int N_BLOCKS = 30; // amount of cells in a row
    private final int SCREEN_SIZE = N_BLOCKS * BLOCK_SIZE; // total amount of cells
    private int DELAY = 80;  // delay for the actionPreformed Thread
    private static int pacX; // the current pacman x
    private static int pacY;
    public static int startX = 103;  // start position for pacman
    public static int startY = 199;
    private JLabel labelScore;
    private int score = 0;
    private int numOfSpecialdots = 5; // special dot - dot a pacman can take and then be able to eat the ghosts for a short certain of time.
    private int lives;
    private int currentLevel = 1;
    private final int OFFSET = 7;
    private boolean pacmanFade = false; // to know when pacman gets hit.
    private boolean ghostFading = false; // to know when ghosts are vulnerable so pacman can hit them.
    private float alpha = 1f; // for fading image when ghost can be eaten.
    boolean fadeIn = true; // to know if we fade in the image or fade out.
    private volatile int ghostSize = 4;


    private boolean leftDirection = false; // for moving left
    private boolean rightDirection = false; // for moving right
    private boolean upDirection = false;  // for moving up
    private boolean downDirection = false; // for moving down
    private static boolean inGame = true; // to know if the game still running
    private boolean wasUp; // after we moved up , want the angle of the mouth to stay up even if we stopped moving
    private boolean wasDown; // to remember the last direction of the pacman
    private boolean wasRight;
    private boolean wasLeft;
    private boolean isOpenMoth = true; // for opening and closing the mouth of the pacman while moving.

    // each x and y are mapped into point at this matrix.
    private static final Point points[][] = new Point[N_BLOCKS][N_BLOCKS];
    // array list to hold the hearts images.
    private ArrayList<Image> heartsList;
    // pacman graph which will hold the above matrix.
    private PacManGraph pacGraph;

    //timer and images objects.
    private Timer timer;
    private Timer fadeTimer;
    private Image heart;
    private Image pacUp;
    private Image pacDown;
    private Image pacRight;
    private Image pacLeft;
    private Image pacMovingUp;
    private Image pacMovingDown;
    private Image pacMovingRight;
    private Image pacMovingLeft;
    private Image redMonster;
    private Image pinkMonster;
    private Image blueMonster;
    private Image orangeMonster;

    //array list to hold the ghosts images.
    private ArrayList<Image> monstersImages;
    // a map to hold the ghost image and the ghost object .
    public ConcurrentHashMap<Image, Ghost> monsters;
    // array list to hold the ghost monsters.
    public ArrayList<Point> specialDots;
    // behaviours of the ghosts
    public String[] stringBehaviours = {"chase", "static", "patrol"};


    // the board will be saved in this matrix as we read it from a file .
    private short levelData[];


    //constructor
    public PacManBoard() {
        initBoard();


    }


    // init board and other stuff.
    private void initBoard() {


        addKeyListener(new PacManBoard.TAdapter());
        setBackground(Color.black);
        setFocusable(true);
        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        loadLevel();
        loadImages();
        loadLabels();
        initGame();


    }

    public void loadLevel() {
        // this function is used to read the current stage from a file and store it in the levelData matrix.
        int j = 0;
        int size = 0;
        String stage = "stage" + currentLevel + ".txt";
        boolean isFirstLine = true;
        try {
            File myObj = new File(stage);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {

                String data = myReader.nextLine();
                if (isFirstLine) {
                    isFirstLine = false;
                    levelData = new short[Integer.parseInt(data)];
                    // saving the size of the array
                    size = Integer.parseInt((data));

                } else {
                    for (int i = 0; i < data.length(); i++) {

                        if (data.charAt(i) >= '0' && data.charAt(i) <= '9') {
                            levelData[j++] = (short) (data.charAt(i) - 48);
                        }
                    }
                }


            }


            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }


        /// generate  random dots for the special dot.
        // 1 in the matrix means special dot
        specialDots = new ArrayList<>();
        int rand;
        rand = (int) (Math.random() * size);
        for (j = 0; j < numOfSpecialdots; j++) {

            while (levelData[rand] == 1) {
                rand = (int) (Math.random() * size);
            }

            levelData[rand] = 1;
            specialDots.add(new Point(rand / N_BLOCKS, rand % N_BLOCKS, true));
        }

        // starting the thread that in charge of the eating of special dots and the effect comes with it
        checkEatSpecialDot check = new checkEatSpecialDot();
        Thread t = new Thread(check);
        t.start();


    }


    private void initMonsters() {


        while (monsters.size() < 4) {
            Image monsterImage;
            // generate random point on screen
            Point p = generateRandomPoint(N_BLOCKS);
            // generate random behaviour
            int rand = generateRandomNumber(stringBehaviours.length);
            IChaseBehaviour chaseBehaviour;
            switch (rand) {
                case 0:
                    chaseBehaviour = new ChaseAggressive();
                    break;

                case 1:
                    chaseBehaviour = new ChaseStatic();
                    break;

                case 2:
                    chaseBehaviour = new ChasePatrol();
                    break;

                default:
                    chaseBehaviour = null;
            }
            String behaviour = stringBehaviours[rand];
            Ghost ghost = new Ghost(chaseBehaviour, p.getY() * BLOCK_SIZE + OFFSET, p.getX() * BLOCK_SIZE + OFFSET, pacGraph, 250, behaviour);
            // we don't want 2 monsters or more with the same color.
            do {
                monsterImage = monstersImages.get(generateRandomNumber(ghostSize));

            } while (monsters.containsKey(monsterImage));
            monsters.put(monsterImage, ghost);
            startGhosts(ghost);

        }

        ghostSize = monsters.size();


    }

    private Point generateRandomPoint(int rand) {
        // this function creates a random point on the matrix and checks if it's valid point.
        int startX, endX;
        do {
            startX = (int) (Math.random() * rand);
            endX = (int) (Math.random() * rand);
        } while (points[startX][endX].getIsOk() == false);

        return points[startX][endX];
    }

    private int generateRandomNumber(int rand) {
        return (int) (Math.random() * rand);
    }


    private void loadLabels() {
        labelScore = new JLabel("Score");
        this.setLayout(null);
        labelScore.setBounds(10, B_HEIGHT - 35, 70, 40);
        labelScore.setForeground(Color.RED);


        heartsList = new ArrayList<>();
        lives = 3;
        for (int i = 0; i < lives; i++) {
            heartsList.add(heart);
        }
    }

    private void loadImages() {
        monstersImages = new ArrayList<>();

        // for pac man pics with mouth open
        ImageIcon iid = new ImageIcon("images/pacman-DOWN.png");
        pacDown = iid.getImage();
        pacDown = pacDown.getScaledInstance(BLOCK_SIZE, BLOCK_SIZE, Image.SCALE_DEFAULT);


        iid = new ImageIcon("images/pacman-UP.png");
        pacUp = iid.getImage();
        pacUp = pacUp.getScaledInstance(BLOCK_SIZE, BLOCK_SIZE, Image.SCALE_DEFAULT);

        iid = new ImageIcon("images/pacman-LEFT.png");
        pacLeft = iid.getImage();
        pacLeft = pacLeft.getScaledInstance(BLOCK_SIZE, BLOCK_SIZE, Image.SCALE_DEFAULT);

        iid = new ImageIcon("images/pacman-RIGHT.png");
        pacRight = iid.getImage();
        pacRight = pacRight.getScaledInstance(BLOCK_SIZE, BLOCK_SIZE, Image.SCALE_DEFAULT);

        // for pac man pics with mouth shut

        iid = new ImageIcon("images/pacman-LEFTFULL.png");
        pacMovingLeft = iid.getImage();
        pacMovingLeft = pacMovingLeft.getScaledInstance(BLOCK_SIZE, BLOCK_SIZE, Image.SCALE_DEFAULT);

        iid = new ImageIcon("images/pacman-FULLRIGHT.png");
        pacMovingRight = iid.getImage();
        pacMovingRight = pacMovingRight.getScaledInstance(BLOCK_SIZE, BLOCK_SIZE, Image.SCALE_DEFAULT);

        iid = new ImageIcon("images/pacman-UPFULL.png");
        pacMovingUp = iid.getImage();
        pacMovingUp = pacMovingUp.getScaledInstance(BLOCK_SIZE, BLOCK_SIZE, Image.SCALE_DEFAULT);

        iid = new ImageIcon("images/pacman-DOWNFULL.png");
        pacMovingDown = iid.getImage();
        pacMovingDown = pacMovingDown.getScaledInstance(BLOCK_SIZE, BLOCK_SIZE, Image.SCALE_DEFAULT);

        // monsters images


        iid = new ImageIcon("images/redGhost.png");
        redMonster = iid.getImage();
        redMonster = redMonster.getScaledInstance(BLOCK_SIZE, BLOCK_SIZE, Image.SCALE_DEFAULT);
        monstersImages.add(redMonster);

        iid = new ImageIcon("images/purpleGhost.png");
        pinkMonster = iid.getImage();
        pinkMonster = pinkMonster.getScaledInstance(BLOCK_SIZE, BLOCK_SIZE, Image.SCALE_DEFAULT);
        monstersImages.add(pinkMonster);

        iid = new ImageIcon("images/blueGhost.png");
        blueMonster = iid.getImage();
        blueMonster = blueMonster.getScaledInstance(BLOCK_SIZE, BLOCK_SIZE, Image.SCALE_DEFAULT);
        monstersImages.add(blueMonster);


        iid = new ImageIcon("images/orangeGhost.png");
        orangeMonster = iid.getImage();
        orangeMonster = orangeMonster.getScaledInstance(BLOCK_SIZE, BLOCK_SIZE, Image.SCALE_DEFAULT);
        monstersImages.add(orangeMonster);

        // heart image
        iid = new ImageIcon("images/heart.png");
        heart = iid.getImage();
        heart = heart.getScaledInstance(BLOCK_SIZE, BLOCK_SIZE, Image.SCALE_DEFAULT);


    }


    private void initGame() {

        Thread t;
        timer = new Timer(DELAY, this); // starting the timer which calls actionPerformed until it stops
        timer.start();


        // creating points matrix
        creatingPoints();

        // init monsters.
        monsters = new ConcurrentHashMap<>();
        initMonsters();


        // this thread is for the moving of Pac-man
        pacMan pacMan = new pacMan(startX, startY);
        t = new Thread(pacMan);
        t.start();


        // this thread is for checking if monsters collide Pac man
        checkEat check = new checkEat();
        t = new Thread(check);
        t.start();

        // this thread is for the cool down of the ghost when it's getting eaten.
        ghostCoolDown ghostCoolDown = new ghostCoolDown();
        t = new Thread(ghostCoolDown);
        t.start();


    }


    private void drawLevel(Graphics2D g2d) {

        short i = 0;
        int x, y;
        int OFFSET = 5;

        for (y = 0; y < SCREEN_SIZE; y += BLOCK_SIZE) {
            for (x = 0; x < SCREEN_SIZE; x += BLOCK_SIZE) {

                g2d.setColor(new Color(0, 72, 251));
                g2d.setStroke(new BasicStroke(5));

                // if it's an obstacle
                if ((levelData[i] == 0)) {
                    g2d.fillRect(x + OFFSET, y + OFFSET, BLOCK_SIZE, BLOCK_SIZE);
                }

                // special dot
                if (levelData[i] == 1) {
                    g2d.setColor(new Color(255, 255, 255));
                    g2d.fillOval(x + OFFSET * 2, y + OFFSET * 2, BLOCK_SIZE / 2, BLOCK_SIZE / 2);
                    g2d.setColor(new Color(0, 72, 251));
                }

                // for corners
                // horizontal corner
                if ((x == 0 || x == SCREEN_SIZE - BLOCK_SIZE)) {

                    if (x != 0)
                        g2d.drawLine(x + OFFSET + BLOCK_SIZE - 1, y + OFFSET, x + OFFSET + BLOCK_SIZE - 1, y + BLOCK_SIZE - 1 + OFFSET);
                    else {
                        g2d.drawLine(x + OFFSET, y + OFFSET, x + OFFSET, y + BLOCK_SIZE - 1 + OFFSET);
                    }


                }

                // vertical
                if ((y == 0 || y == SCREEN_SIZE - BLOCK_SIZE)) {

                    if (y != 0)
                        g2d.drawLine(x + OFFSET, y + BLOCK_SIZE - 1 + OFFSET, x + BLOCK_SIZE - 1 + OFFSET, y + BLOCK_SIZE - 1 + OFFSET);
                    else {
                        g2d.drawLine(x + OFFSET, y + OFFSET, x + BLOCK_SIZE - 1 + OFFSET, y + OFFSET);
                    }
                }

                // if it's dot
                if (levelData[i] == 2) {
                    g2d.setColor(new Color(255, 255, 255));
                    g2d.fillOval(x + BLOCK_SIZE / 2, y + BLOCK_SIZE / 2, BLOCK_SIZE / 4, BLOCK_SIZE / 4);
                }

                i++;
            }
        }
    }

    private void creatingPoints() {

        //creating the matrix point and using the levelData matrix we've just read from the file
        // 0 means - not walkable point.
        int i, j;

        for (i = 0; i < N_BLOCKS; i++) {
            for (j = 0; j < N_BLOCKS; j++) {
                if (levelData[i * N_BLOCKS + j] == 0)
                    points[i][j] = new Point(i, j, false);
                else
                    points[i][j] = new Point(i, j, true);

            }
        }


        pacGraph = new PacManGraph(points);


    }

    private Image pacDirectionOpenMouth() {
        if (upDirection)
            return pacUp;
        else if (downDirection)
            return pacDown;
        else if (rightDirection)
            return pacRight;
        else if (leftDirection)
            return pacLeft;
        else if (wasUp)
            return pacUp;
        else if (wasDown)
            return pacDown;
        else if (wasRight)
            return pacRight;
        else if (wasLeft)
            return pacLeft;

        return pacLeft;
    }

    private Image pacDirectionClosedMouth() {
        if (upDirection)
            return pacMovingUp;
        else if (downDirection)
            return pacMovingDown;
        else if (rightDirection)
            return pacMovingRight;
        else if (leftDirection)
            return pacMovingLeft;
        else if (wasUp)
            return pacUp;
        else if (wasDown)
            return pacDown;
        else if (wasRight)
            return pacRight;
        else if (wasLeft)
            return pacLeft;

        return pacLeft;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        // drawing the obstacles.
        drawLevel(g2d);


        // for the angle of the mouth of the pac-man pic.
        // if its open

        //if(!ghostFading) {
        if (isOpenMoth) {
            g.drawImage(pacDirectionOpenMouth(), pacX, pacY, this);
            isOpenMoth = false;
        }
        // if its closed
        else {
            g.drawImage(pacDirectionClosedMouth(), pacX, pacY, this);
            isOpenMoth = true;
        }
        // }

        // drawing monsters

        if (!ghostFading) {
            for (Map.Entry<Image, Ghost> ghost : monsters.entrySet()) {
                g.drawImage(ghost.getKey(), ghost.getValue().getX(), ghost.getValue().getY(), this);
            }
        }


        //painting hearts
        int heartX = 80;
        int heartY = B_HEIGHT - 50;
        for (Image heart : heartsList) {
            g.drawImage(heart, heartX, heartY, this);
            heartX += 25;

        }

        labelScore.setText("Score " + score);
        g.drawString(labelScore.getText(), labelScore.getX(), labelScore.getY());

        if (ghostFading) {
            startFadeTimer();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                    alpha));
            doFade(g2d);


        }


    }

    private void doFade(Graphics g2d) {
        float add = 0.005f;
        float alphaOffset = 0.005000812f;

        if (alpha == alphaOffset) {
            fadeIn = fadeIn == true ? false : true;
            fadeTimer.stop();
            startFadeTimer();


        }

        if (fadeIn) {

            alpha -= add;
        } else {

            alpha += add;
        }

        // change to fade mode
        if (alpha == 1) {
            fadeIn = fadeIn == true ? false : true;
            fadeTimer.stop();
            startFadeTimer();
        }

        synchronized (monsters) {
            if (ghostFading) {
                for (Map.Entry<Image, Ghost> ghost : monsters.entrySet()) {
                    g2d.drawImage(ghost.getKey(), ghost.getValue().getX(), ghost.getValue().getY(), this);
                }

            }
        }


//        if (isOpenMoth) {
//            g.drawImage(pacDirectionOpenMouth(), pacX, pacY, this);
//            isOpenMoth = false;
//        }
//        // if its closed
//        else {
//            g.drawImage(pacDirectionClosedMouth(), pacX, pacY, this);
//            isOpenMoth = true;
//        }

        repaint();
    }

    public void startFadeTimer() {
        fadeTimer = new Timer(800, this);
        fadeTimer.start();
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();


    }


    private Point getPoint(int x, int y) {
        // this function gets x and y on screen and returns the proper point on the matrix.
        int locX = (x - OFFSET) / PacManBoard.BLOCK_SIZE;
        int locY = (y - OFFSET) / PacManBoard.BLOCK_SIZE;

        return points[locY][locX];
    }

    private void startGhosts(Ghost ghost) {
        // starting the ghosts threads.

        Thread t = new Thread(ghost);
        t.start();

    }

    private class ghostCoolDown implements Runnable {

        @Override
        public void run() {

            while (true) {


                if (ghostSize < 4) {
                    try {
                        Thread.sleep(5000);
                        initMonsters();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private class TAdapter extends KeyAdapter {

        // listener for the key arrows to move the pacman and to know the positions of it to draw it correctly.

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();
            if (key == KeyEvent.VK_LEFT) leftDirection = true;
            if (key == KeyEvent.VK_RIGHT) rightDirection = true;
            if (key == KeyEvent.VK_UP) upDirection = true;
            if (key == KeyEvent.VK_DOWN) downDirection = true;


        }

        @Override
        public void keyReleased(KeyEvent e) {
            wasUp = false;
            wasDown = false;
            wasRight = false;
            wasLeft = false;


            if (e.getKeyCode() == KeyEvent.VK_LEFT) {

                leftDirection = false;
                wasLeft = true;
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {

                rightDirection = false;
                wasRight = true;
            }
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                upDirection = false;
                wasUp = true;
            }
            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                downDirection = false;
                wasDown = true;
            }


        }


    }


    private class checkEat implements Runnable {
        // this thread is for the ghosts collide in the pacman,
        // after collide we give a sleep of 1000 miles to allow "fade" mode , which for a brief shot of time
        // the pacman can't be eaten again.
        // if the ghosts are in fade mode ( can happen if pacman takes special dot), so we check if pacman hits them.
        // monsters map is ConcurrentHashMap because when a thread is trying to modify hash map , the iterator Fail Fast
        // can't deal with thread map modification .

        @Override
        public void run() {


            while (true) {


                for (Map.Entry<Image, Ghost> g : monsters.entrySet()) {

                    Point ghost = getPoint(g.getValue().getX(), g.getValue().getY());


                    if (inRadius(pacX, pacY, (ghost.getY() * BLOCK_SIZE + OFFSET), (ghost.getX() * BLOCK_SIZE + OFFSET), 15)) {

                        // pacman eats the ghost
                        if (ghostFading) {
                            monsters.remove(g.getKey());
                            ghostSize = monsters.size();
                            break;
                        }
                        //other wise ghost eats pacman
                        if (heartsList.size() > 0 && !pacmanFade) {
                            heartsList.remove(0);
                            pacmanFade = true;

                            try {
                                Thread.sleep(1000);
                                pacmanFade = false;

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }


                }

            }
        }
    }

    private class checkEatSpecialDot implements Runnable {
        // this thread is for the pacman collide in the special dots,
        // after collide we give a sleep of 1000 miles to allow "fade" mode  for ghosts, which for a brief shot of time
        // the pacman can hit them.

        @Override
        public void run() {


            while (true) {

                for (Point p : specialDots) {

                    if (inRadius(pacX, pacY, (p.getY() * BLOCK_SIZE + OFFSET), (p.getX() * BLOCK_SIZE + OFFSET), 3)) {
                        ghostFading = true;

                        try {
                            Thread.sleep(5000);
                            ghostFading = false;
                            specialDots.remove(p);
                            break;

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }


        }
    }


    private class pacMan implements Runnable {


        public pacMan(int x, int y) {
            pacX = x;
            pacY = y;
        }

        public void run() {
            try {

                while (true) {


                    // converting pacX and pacY to points on the matrix
                    int locX = (pacX - OFFSET) / BLOCK_SIZE;
                    int locY = (pacY - OFFSET) / BLOCK_SIZE;
                    Point currentPacPos = points[locY][locX];
                    // using the interface neighbours function to know if pacman is able to turn up or down or right or left accordingly.
                    HashMap<String, Point> neighbours = pacGraph.neighbors(currentPacPos);
                    // pacman is  eating so update the matrix.
                    if (levelData[locY * N_BLOCKS + locX] != -1) {
                        score++;
                    }
                    levelData[locY * N_BLOCKS + locX] = -1;
                    // checking if valid move
                    if (!neighbours.containsKey("LEFT")) {
                        leftDirection = false;
                        wasLeft = true;
                    }

                    if (!neighbours.containsKey("RIGHT")) {
                        rightDirection = false;
                        wasRight = true;
                    }

                    if (!neighbours.containsKey("UP")) {
                        upDirection = false;
                        wasUp = true;
                    }

                    if (!neighbours.containsKey("DOWN")) {
                        downDirection = false;
                        wasDown = true;
                    }


                    if (leftDirection && upDirection) {
                        pacY -= BLOCK_SIZE;

                    } else if (leftDirection && downDirection) {
                        pacY += BLOCK_SIZE;

                    } else if (rightDirection && upDirection) {
                        pacY -= BLOCK_SIZE;

                    } else if (rightDirection && downDirection) {
                        pacY += BLOCK_SIZE;

                    } else if (leftDirection) {
                        pacX -= BLOCK_SIZE;

                    } else if (upDirection) {
                        pacY -= BLOCK_SIZE;

                    } else if (rightDirection) {
                        pacX += BLOCK_SIZE;


                    } else if (downDirection) {
                        pacY += BLOCK_SIZE;
                    }


                    Thread.sleep(200);


                    //Ghost.setCooradinate(pacX, pacY);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(0);
            }
        }


    }

    public static int getPacX() {
        return pacX;
    }

    public static int getPacY() {
        return pacY;
    }

    public static int getRows() {
        return N_BLOCKS;
    }


    public static int getCols() {
        return N_BLOCKS;
    }

    public static Point[][] getPoints() {
        return points;
    }

    public static boolean getInGame() {
        return inGame;
    }

    public boolean inRadius(int pX, int pY, int gX, int gY, int offset) {


        if (Math.abs(pX - gX) <= offset && Math.abs(pY - gY) <= offset) {
            return true;
        }


        return false;

    }
}

