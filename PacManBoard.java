import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class PacManBoard extends JPanel implements ActionListener {

    //760 ,800 , 380 ,420  ---- 30 ,15
    private final int B_WIDTH = 760; // window width
    private final int B_HEIGHT = 800; // window height
    public static final int BLOCK_SIZE = 24;
    public static final int N_BLOCKS = 30;
    private final int SCREEN_SIZE = N_BLOCKS * BLOCK_SIZE;
    private int DELAY = 80; // not final - leaving an option to change the monsters movement speed.
    private static int pacX;
    private static int pacY;
    private static int StartX = 103;
    private static int StartY = 199;
    private JLabel labelScore;
    private int score = 0;
    private int lives;
    private boolean isFade = false;
    private int currentLevel = 1;


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


    private static final Point points[][] = new Point[N_BLOCKS][N_BLOCKS];
    // private short[] screenData;
    private ArrayList<Image> heartsList;

    private PacManGraph pacGraph;

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


    public ArrayList<Ghost> monsters;
    public Ghost redGhost;
    public Ghost pinkGhost;
    public Ghost blueGhost;



    // private final short levelData[] ;
    private short levelData[];




    public PacManBoard(int stage)  {
        initBoard();


    }


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
        Point p = generateRandom();
        redGhost = new Ghost(new chaseAggresive(), p.getY() * BLOCK_SIZE + 7, p.getX() * BLOCK_SIZE + 7, pacGraph, 200, "chase");
        // generating random position for the static monster :
        p = generateRandom();
        pinkGhost = new Ghost(new chaseStatic(), p.getY() * BLOCK_SIZE + 7, p.getX() * BLOCK_SIZE + 7, pacGraph, 200, "static");
        // generating random position for the patrol monster :
        p = generateRandom();
        blueGhost = new Ghost(new chasePatrol(), p.getY() * BLOCK_SIZE + 7, p.getX() * BLOCK_SIZE + 7, pacGraph, 200, "patrol");



        monsters.add(redGhost);
        monsters.add(pinkGhost);
        monsters.add(blueGhost);

        startGhosts();
    }

    private Point generateRandom() {
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


        //locateDots();
        creatingPoints();
        monsters = new ArrayList<>();
        initMonsters();

        // this thread is for the moving of Pac-man
        pacMan pacMan = new pacMan(StartX, StartY);
        Thread t = new Thread(pacMan);
        t.start();

        // this thred is for checking if monsters collide Pac man
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
                    // if(x!=0)
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
        // System.out.println(redGhost.getX() + " "  + redGhost.getY());

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
        //g.drawImage(redMonsterLeft, 200 ,200, this);

        //g.drawImage(redMonsterDown, pinkGhost.x , pinkGhost.y , this);
        //  g.drawImage(redMonsterLeft, pacX-60, pacY-100, this);
        // painting the dots
        // g.setColor(Color.white);
//        for (int i = 0; i < dots.length; i++) {
//            if (dots[i].x >= 0)
//                g.drawOval(dots[i].x, dots[i].y, 10, 10);
//        }

        // System.out.println("PacX : " + pacX + "PacY" + pacY);
        // System.out.println("PacX : " + redGhost.x + "PacY" + redGhost.y);


    }


    @Override
    public void actionPerformed(ActionEvent e) {

        //movePacMan(); i used a Thread i did , it works also.
        repaint();


    }


    private Point getPoint(int x, int y) {
        int locX = (x - 7) / PacManBoard.BLOCK_SIZE;
        int locY = (y - 7) / PacManBoard.BLOCK_SIZE;

        return points[locY][locX];
    }

    private void startGhosts() {
        for (Ghost g : monsters) {
            Thread t = new Thread(g);
            t.start();
        }
    }


    private class TAdapter extends KeyAdapter {


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

        @Override
        public void run() {


            while (true) {
                for (Ghost g : monsters) {

                    Point ghost = getPoint(g.getX(), g.getY());


                    if (inRadious(pacX, pacY, (ghost.getY() * BLOCK_SIZE + 7), (ghost.getX() * BLOCK_SIZE + 7), 15)) {



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
                    int locX = (pacX - 7) / BLOCK_SIZE;
                    int locY = (pacY - 7) / BLOCK_SIZE;
                    Point currentPacPos = points[locY][locX];
                    HashMap<String, Point> neibhours = pacGraph.neighbors(currentPacPos);
                    // pacman eating so update the matrix.
                    if (levelData[locY * N_BLOCKS + locX] != -1) {
                        score++;
                    }
                    levelData[locY * N_BLOCKS + locX] = -1;
                    // checking if valid move
                    if (!neibhours.containsKey("LEFT")) {
                        leftDirection = false;
                        wasLeft = true;
                    }

                    if (!neibhours.containsKey("RIGHT")) {
                        rightDirection = false;
                        wasRight = true;
                    }

                    if (!neibhours.containsKey("UP")) {
                        upDirection = false;
                        wasUp = true;
                    }

                    if (!neibhours.containsKey("DOWN")) {
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

                    chaseAggresive.setCooradinate(pacX, pacY);
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

    public boolean inRadious(int pX, int pY, int gX, int gY, int offset) {


        if (Math.abs(pX - gX) <= offset && Math.abs(pY - gY) <= offset) {
            return true;
        }


        return false;

    }
}

