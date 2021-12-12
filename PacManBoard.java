import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class PacManBoard extends JPanel implements ActionListener {


    private final int B_WIDTH = 760; // window width
    private final int B_HEIGHT = 800; // window height
    public static final int BLOCK_SIZE = 24; // each cell size
    public static final int N_BLOCKS = 30; // amount of cells in a row
    private final int SCREEN_SIZE = N_BLOCKS * BLOCK_SIZE; // total amount of cells
    private int DELAY = 80;  // delay for the actionPreformed Thread
    private static int pacX;
    private static int pacY;
    public static int startX = 103;  // start position for pacman
    public static int startY = 199;
    private JLabel labelScore;
    private int score = 0;
    private int lives;
    private boolean isFade = false; // a variable to know when pacman gets hit.
    private int currentLevel = 1;
    private final int OFFSET = 7;


    private boolean leftDirection = false; // for moving left
    private boolean rightDirection = false; // for moving right
    private boolean upDirection = false;  // for moving up
    private boolean downDirection = false; // for moving down
    private static boolean inGame = true; // to know if the game still running
    private boolean wasUp; // after we moved up , want the angle of the mouth to stay up even if we stopped moving
    private boolean wasDown;
    private boolean wasRight;
    private boolean wasLeft;
    private boolean isOpenMoth = true; // for opening and closing the mouth of the pacman while moving.

    // each x and y are mapped into point at this matrix.
    private static final Point points[][] = new Point[N_BLOCKS][N_BLOCKS];
    // array list to hold the hearts images.
    private ArrayList<Image> heartsList;
    // pacman graph which will hold the above matrix.
    private PacManGraph pacGraph;

    //timeer and images objects.
    private Timer timer;
    private Image heart;
    private Image pacUp;
    private Image pacDown;
    private Image pacRight;
    private Image pacLeft;
    private Image pacMovingUp;
    private Image pacMovingDown;
    private Image pacMovingRight;
    private Image pacMovingLeft;
    private Image redMonsterDown;
    private Image redMonsterUp;
    private Image redMonsterLeft;
    private Image redMonsterRight;
    private Image pinkMonster;
    private Image blueMonster;


    // array list to hold the ghost monsters.
    public ArrayList<Ghost> monsters;
    public Ghost redGhost;
    public Ghost pinkGhost;
    public Ghost blueGhost;



    // the board will be saved in this matrix as we read it from a file .
    private short levelData[];




    //constructor
    public PacManBoard()  {
        initBoard();


    }


    // init board and other stuff.
    private void initBoard()  {


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
        String stage = "stage" + currentLevel + ".txt";
        boolean isFirstLine = true;
        try {
            File myObj = new File(stage);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {

                String data = myReader.nextLine();
                if (isFirstLine) {
                    isFirstLine = false;
                    levelData = new short [Integer.parseInt(data)];

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



    }


    private void initMonsters() {

        // init the monsters and give them random position on the board
        Point p = generateRandom();
        redGhost = new Ghost(new chaseAggresive(), p.getY() * BLOCK_SIZE + OFFSET, p.getX() * BLOCK_SIZE + OFFSET, pacGraph, 200, "chase");
        // generating random position for the static monster :
        p = generateRandom();
        pinkGhost = new Ghost(new chaseStatic(), p.getY() * BLOCK_SIZE + OFFSET, p.getX() * BLOCK_SIZE + OFFSET, pacGraph, 200, "static");
        // generating random position for the patrol monster :
        p = generateRandom();
        blueGhost = new Ghost(new chasePatrol(), p.getY() * BLOCK_SIZE + OFFSET, p.getX() * BLOCK_SIZE + OFFSET, pacGraph, 200, "patrol");



        monsters.add(redGhost);
        monsters.add(pinkGhost);
        monsters.add(blueGhost);

        startGhosts();
    }

    private Point generateRandom() {
        // this function creates a random point on the matrix and checks if it's valid point.
        int startX, endX;
        do {
            startX = (int) (Math.random() * N_BLOCKS);
            endX = (int) (Math.random() * N_BLOCKS);
        } while (points[startX][endX].getIsOk() == false);

        return points[startX][endX];
    }


    private void loadLabels() {
        labelScore = new JLabel("Score");
        this.setLayout(null);
        labelScore.setBounds(10, B_HEIGHT - 60, 70, 40);
        labelScore.setForeground(Color.RED);
        this.add(labelScore);

        heartsList = new ArrayList<>();
        lives = 3;
        for (int i = 0; i < lives; i++) {
            heartsList.add(heart);
        }
    }

    private void loadImages() {

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

        iid = new ImageIcon("images/redU.png");
        redMonsterUp = iid.getImage();
        redMonsterUp = redMonsterUp.getScaledInstance(BLOCK_SIZE, BLOCK_SIZE, Image.SCALE_DEFAULT);

        iid = new ImageIcon("images/redD.png");
        redMonsterDown = iid.getImage();
        redMonsterDown = redMonsterDown.getScaledInstance(BLOCK_SIZE, BLOCK_SIZE, Image.SCALE_DEFAULT);

        iid = new ImageIcon("images/redR.png");
        redMonsterRight = iid.getImage();
        redMonsterRight = redMonsterRight.getScaledInstance(BLOCK_SIZE, BLOCK_SIZE, Image.SCALE_DEFAULT);

        iid = new ImageIcon("images/test.png");
        redMonsterLeft = iid.getImage();
        redMonsterLeft = redMonsterLeft.getScaledInstance(BLOCK_SIZE, BLOCK_SIZE, Image.SCALE_DEFAULT);

        iid = new ImageIcon("images/purpleGhost.png");
        pinkMonster = iid.getImage();
        pinkMonster = pinkMonster.getScaledInstance(BLOCK_SIZE, BLOCK_SIZE, Image.SCALE_DEFAULT);

        iid = new ImageIcon("images/blueGhost.png");
        blueMonster = iid.getImage();
        blueMonster = blueMonster.getScaledInstance(BLOCK_SIZE, BLOCK_SIZE, Image.SCALE_DEFAULT);


        // heart image
        iid = new ImageIcon("images/heart.png");
        heart = iid.getImage();
        heart = heart.getScaledInstance(BLOCK_SIZE, BLOCK_SIZE, Image.SCALE_DEFAULT);

    }


    private void initGame() {

        timer = new Timer(DELAY, this); // starting the timer which calls actionPerformed until it stops
        timer.start();


        creatingPoints();


        // this thread is for the moving of Pac-man
        pacMan pacMan = new pacMan(startX, startY);
        Thread t = new Thread(pacMan);
        t.start();
        //updating the pacman x and y on the ghosts class.
        Ghost.setCooradinate(startX,startY);



        monsters = new ArrayList<>();
        initMonsters();

        // this thread is for checking if monsters collide Pac man
        checkEat check = new checkEat();
        Thread t2 = new Thread(check);
        t2.start();


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


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        // drawing the obstacles.
        drawLevel(g2d);


        // for the angle of the mouth of the pac-man pic.
        // if its open
        if (isOpenMoth) {
            if (upDirection)
                g.drawImage(pacUp, pacX, pacY, this);
            else if (downDirection)
                g.drawImage(pacDown, pacX, pacY, this);
            else if (rightDirection)
                g.drawImage(pacRight, pacX, pacY, this);
            else if (leftDirection)
                g.drawImage(pacLeft, pacX, pacY, this);
            else if (wasUp)
                g.drawImage(pacUp, pacX, pacY, this);
            else if (wasDown)
                g.drawImage(pacDown, pacX, pacY, this);
            else if (wasRight)
                g.drawImage(pacRight, pacX, pacY, this);
            else if (wasLeft)
                g.drawImage(pacLeft, pacX, pacY, this);

            isOpenMoth = false;
        }
        // if its closed
        else {

            if (upDirection)
                g.drawImage(pacMovingUp, pacX, pacY, this);
            else if (downDirection)
                g.drawImage(pacMovingDown, pacX, pacY, this);
            else if (rightDirection)
                g.drawImage(pacMovingRight, pacX, pacY, this);
            else if (leftDirection)
                g.drawImage(pacMovingLeft, pacX, pacY, this);
            else if (wasUp)
                g.drawImage(pacUp, pacX, pacY, this);
            else if (wasDown)
                g.drawImage(pacDown, pacX, pacY, this);
            else if (wasRight)
                g.drawImage(pacRight, pacX, pacY, this);
            else if (wasLeft)
                g.drawImage(pacLeft, pacX, pacY, this);

            isOpenMoth = true;
        }


        // drawing monsters


        g.drawImage(redMonsterLeft, redGhost.getX(), redGhost.getY(), this);
        g.drawImage(pinkMonster, pinkGhost.getX(), pinkGhost.getY(), this);
        g.drawImage(blueMonster, blueGhost.getX(), blueGhost.getY(), this);

        labelScore.setText("Score " + score);

        //painting hearts
        int heartX = 80;
        int heartY = B_HEIGHT - 50;
        for (Image heart : heartsList) {
            g.drawImage(heart, heartX, heartY, this);
            heartX += 25;

        }



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

    private void startGhosts() {
        // starting the ghosts threads.
        for (Ghost g : monsters) {
            Thread t = new Thread(g);
            t.start();
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

        @Override
        public void run() {


            while (true) {
                for (Ghost g : monsters) {

                    Point ghost = getPoint(g.getX(), g.getY());


                    if (inRadius(pacX, pacY, (ghost.getY() * BLOCK_SIZE + 7), (ghost.getX() * BLOCK_SIZE + 7), 15)) {



                        if (heartsList.size() > 0 && !isFade) {
                            isFade = true;
                            heartsList.remove(0);

                            try {
                                Thread.sleep(1000);
                                isFade = false;

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
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


                    Ghost.setCooradinate(pacX, pacY);
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

