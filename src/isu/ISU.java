/*
​ICS3U - Yuqiao Jiang ISU

Description:
Pirates! is a two-player game where each player controls a pirate ship. The goal
of the game is to sink the other player by firing cannonballs or colliding with
the other player.

How I took it to 11:
My program takes it to 11 because it uses a physics engine to realistically simulate 
water drag (depending on what direction the ship is facing), movement, collision,
and acceleration of the pirate ships. There are also 4 different kinds of ship you 
can choose from, all with different stats, allowing for more variety in game types.
*/

package isu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;
import java.util.ArrayList;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class ISU extends JPanel implements KeyListener {

    Ship plr1, plr2; //Ship objects for each player
    int engine1, rudder1, engine2, rudder2, cannon1, cannon2, sel1, sel2, screen, winner; //Each int is explained in its respective function
    final int types = 4; //Total types of ship to choose from
    boolean lock1, lock2; //Boolean to track when each player has locked in their selection
    ArrayList<Cannonball> p1Balls = new ArrayList<>(); //Player 1's cannonballs
    ArrayList<Cannonball> p2Balls = new ArrayList<>(); //Player 2's cannonballs
    BufferedImage[] imgs = new BufferedImage[10]; //Array to store all images

    public static void main(String[] args) {
        //Initialize game
        ISU game = new ISU();
        game.init();
    }

    private void init() { //Initialize game
        JFrame frame = new JFrame("Pirates!"); //Create main JFrame
        frame.pack(); //Set components to preffered size (used to get intsets)
        frame.setSize(1024, 576 + frame.getInsets().top); //Set the size of frame, accounts for size of title bar
        frame.setResizable(false); //Don't let user resize game
        frame.add(this); //Add the game to the JFrame
        frame.setVisible(true); //Set JFrame visible
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Allow X button to close the game
        frame.addKeyListener(this); //Initialize keyboard listener

        //Create Timer object and set a periodic function for animation
        Timer timer = new Timer();
        TimerScheduler tTask = new TimerScheduler();
        Date date = new Date();
        timer.scheduleAtFixedRate(tTask, date, 16); //About 60fps

        try { //ImageIO.read can throw IOException
            imgs = new BufferedImage[]{ //Load images
                ImageIO.read(getClass().getClassLoader().getResourceAsStream("images/sel_sloop.png")),
                ImageIO.read(getClass().getClassLoader().getResourceAsStream("images/sel_schooner.png")),
                ImageIO.read(getClass().getClassLoader().getResourceAsStream("images/sel_brigantine.png")),
                ImageIO.read(getClass().getClassLoader().getResourceAsStream("images/sel_galleon.png")),
                ImageIO.read(getClass().getClassLoader().getResourceAsStream("images/lock.png")),
                ImageIO.read(getClass().getClassLoader().getResourceAsStream("images/title.png")),
                ImageIO.read(getClass().getClassLoader().getResourceAsStream("images/charsel.png")),
                ImageIO.read(getClass().getClassLoader().getResourceAsStream("images/tie.png")),
                ImageIO.read(getClass().getClassLoader().getResourceAsStream("images/win1.png")),
                ImageIO.read(getClass().getClassLoader().getResourceAsStream("images/win2.png"))
            };
        } catch (IOException e) {
        }
    }

    @Override
    public void paintComponent(Graphics g) { //Method for drawing images to screen
        switch (screen) { //Display different things depending on what screen user is on
            case (0): //Title screen
                g.drawImage(imgs[5], 0, 0, null); //Draw title image
                break;
            case (1): //Character select screen
                super.paintComponent(g);
                setBackground(new Color(25, 200, 255)); //Background colour
                g.drawImage(imgs[6], 0, 0, null); //Draw the character selction screen
                g.drawImage(imgs[sel1], 155, 120, null); //Draw Player 1's current selected ship
                g.drawImage(imgs[sel2], 695, 120, null); //Draw Player 2's current selected ship
                if (lock1) { //Draw a checkmark over Player 1 if they have locked in
                    g.drawImage(imgs[4], 185, 170, null);
                }
                if (lock2) { //Draw a checkmark over Player 2 if they have locked in
                    g.drawImage(imgs[4], 725, 170, null);
                }
                break;
            case (2): //Game screen
                super.paintComponent(g);
                setBackground(new Color(25, 200, 255)); //Background colour
                plr1.draw(g); //Draw Player 1
                plr2.draw(g); //Draw Player 2
                for (Cannonball i : p1Balls) { //Draw all of Player 1's cannonballs
                    i.draw(g);
                }
                for (Cannonball i : p2Balls) { //Draw all of Player 2's cannonballs
                    i.draw(g);
                }
                break;
            case (3): //End game screen
                g.drawImage(imgs[7 + winner], 166, 100, null); //Draw the results of the game (Tie, P1 win, P2 win)
                //Winner is set once the game ends (see TimerScheduler at bottom of ISU class)
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent ke) {
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        switch (screen) { //Buttons have different functions depending on screen
            case (0): //Title screen
                if (ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.exit(0); //Pressing ESC closes the program
                } else { //Pressing any other key begins the game
                    screen = 1; //Go to character selection screen
                    break;
                }
            case (1): //Character select screen
                switch (ke.getKeyCode()) {
                    case (KeyEvent.VK_D):
                        if (!lock1) { //Player can't change selection once locked in
                            sel1--; //Player 1 scrolls left through ships
                            if (sel1 < 0) {
                                sel1 = types - 1; //If the player scrolls too far, they loop around
                            }
                        }
                        break;
                    case (KeyEvent.VK_G):
                        if (!lock1) { //Player can't change selection once locked in
                            sel1++; //Player 1 scrolls right through ships
                            if (sel1 >= types) {
                                sel1 = 0; //If the player scrolls too far, they loop around
                            }
                        }
                        break;
                    case (KeyEvent.VK_Q):
                        lock1 = true; //Player 1 lock in
                        break;
                    case (KeyEvent.VK_W):
                        lock1 = false; //Player 1 cancels lock in
                        break;
                    case (KeyEvent.VK_LEFT):
                        if (!lock2) { //Player can't change selection once locked in
                            sel2--; //Player 2 scrolls left through ships
                            if (sel2 < 0) {
                                sel2 = types - 1; //If the player scrolls too far, they loop around
                            }
                        }
                        break;
                    case (KeyEvent.VK_RIGHT):
                        if (!lock2) { //Player can't change selection once locked in
                            sel2++; //Player 2 scrolls right through ships
                            if (sel2 >= types) {
                                sel2 = 0; //If the player scrolls too far, they loop around
                            }
                        }
                        break;
                    case (KeyEvent.VK_COMMA):
                        lock2 = true; //Player 2 lock in
                        break;
                    case (KeyEvent.VK_PERIOD):
                        lock2 = false; //Player 2 cancels lock in
                        break;
                }
                break;
            case (2): //Game screen
                switch (ke.getKeyCode()) {
                    case (KeyEvent.VK_R):
                        engine1 = 1; //Player 1 sails forward
                        break;
                    case (KeyEvent.VK_F):
                        engine1 = -1; //Player 1 sails backward
                        break;
                    case (KeyEvent.VK_D):
                        rudder1 = -1; //Player 1 steers left
                        break;
                    case (KeyEvent.VK_G):
                        rudder1 = 1; //Player 1 steers right
                        break;
                    case (KeyEvent.VK_Q):
                        cannon1 = -1; //Player 1 fires a cannonball left
                        break;
                    case (KeyEvent.VK_W):
                        cannon1 = 1; //Player 1 fires a cannonball right
                        break;
                    case (KeyEvent.VK_UP):
                        engine2 = 1; //Player 2 sails forward
                        break;
                    case (KeyEvent.VK_DOWN):
                        engine2 = -1; //Player 2 sails backward
                        break;
                    case (KeyEvent.VK_LEFT):
                        rudder2 = -1; //Player 2 steers left
                        break;
                    case (KeyEvent.VK_RIGHT):
                        rudder2 = 1; //Player 2 steers right
                        break;
                    case (KeyEvent.VK_COMMA):
                        cannon2 = -1; //Player 2 fires a cannonball left
                        break;
                    case (KeyEvent.VK_PERIOD):
                        cannon2 = 1; //Player 2 fires a cannonball right
                        break;
                }
            case (3): //Game end screen
                if (ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.exit(0); //Exit program with ESC
                } else if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                    screen = 1; //Sends players back to character select screen
                    break;
                }
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        switch (screen) {
            case (2): //Game screen
                switch (ke.getKeyCode()) {
                    case (KeyEvent.VK_R):
                        engine1 = 0; //Player 1 stops sailing
                        break;
                    case (KeyEvent.VK_F):
                        engine1 = 0; //Player 1 stops sailing
                        break;
                    case (KeyEvent.VK_D):
                        rudder1 = 0; //Player 1 stops steering
                        break;
                    case (KeyEvent.VK_G):
                        rudder1 = 0; //Player 1 stops steering
                        break;
                    case (KeyEvent.VK_Q):
                        cannon1 = 0; //Player 1 stops shooting
                        break;
                    case (KeyEvent.VK_W):
                        cannon1 = 0; //Player 1 stops shooting
                        break;
                    case (KeyEvent.VK_UP):
                        engine2 = 0; //Player 2 stops sailing
                        break;
                    case (KeyEvent.VK_DOWN):
                        engine2 = 0; //Player 2 stops sailing
                        break;
                    case (KeyEvent.VK_LEFT):
                        rudder2 = 0; //Player 2 stops steering
                        break;
                    case (KeyEvent.VK_RIGHT):
                        rudder2 = 0; //Player 2 stops steering
                        break;
                    case (KeyEvent.VK_COMMA):
                        cannon2 = 0; //Player 2 stops shooting
                        break;
                    case (KeyEvent.VK_PERIOD):
                        cannon2 = 0; //Player 2 stops shooting
                        break;
                }
        }
    }

    class TimerScheduler extends TimerTask {

        @Override
        public void run() { //Periodic function that is called by the Timer
            switch (screen) { //Different periodic function depending on screen
                case (1): //Character select screen
                    if (lock1 && lock2) { //If both players are locked in
                        //Initialize player positions
                        plr1 = new Ship(10, 10, 0, sel1);
                        plr2 = new Ship(930, 510, Math.PI, sel2);

                        screen = 2; //Go to game screen
                    }
                    break;
                case (2): //Game screen
                    plr1.engine(engine1); //Move player in direction depending on what button they last pressed (see void KeyPressed)
                    plr1.rudder(rudder1); //Turn player in direction depending on what button they last pressed (see void KeyPressed)
                    plr1.cannon(cannon1, p1Balls); //Shoot in direction depending on what button they last pressed (see void KeyPressed)
                    plr1.update(); //Update the player's position
                    plr1.collide(p2Balls, plr2); //Check player's collision with other player's cannonballs
                    plr2.engine(engine2); //Move player in direction depending on what button they last pressed (see void KeyPressed)
                    plr2.rudder(rudder2); //Turn player in direction depending on what button they last pressed (see void KeyPressed)
                    plr2.cannon(cannon2, p2Balls); //Shoot in direction depending on what button they last pressed (see void KeyPressed)
                    plr2.update(); //Update the player's position
                    plr2.collide(p1Balls, plr1); //Check player's collision with other player's cannonballs
                    for (int i = 0; i < p1Balls.size(); i++) { //Update all Player 1's cannonballs
                        //Get individual cannonball and update it
                        Cannonball b = p1Balls.get(i);
                        b.update();
                        if (b.pos.x + b.size < 0 || b.pos.y + b.size < 0 || b.pos.x > 1024 || b.pos.y > 576) {
                            p1Balls.remove(i); //Remove cannonball if it is off the screen
                        }
                    }
                    for (int i = 0; i < p2Balls.size(); i++) {  //Update all Player 2's cannonballs
                        //Get individual cannonball and update it
                        Cannonball b = p2Balls.get(i);
                        b.update();
                        if (b.pos.x + b.size < 0 || b.pos.y + b.size < 0 || b.pos.x > 1024 || b.pos.y > 576) {
                            p2Balls.remove(i); //Remove cannonball if it is off the screen
                        }
                    }
                    if (plr1.health <= 0) { //If Player 1 is sunk
                        plr1.health = 0; //Make sure health does not go negative
                        if (plr2.health <= 0) { //Check if Player 2 is also sunk; this would mean a tie
                            plr2.health = 0; //Make sure health does not go negative
                            winner = 0; //Mark game result as tie
                        } else {
                            winner = 2; //Mark winner as Player 2
                        }
                        endGame();
                    } else if (plr2.health <= 0) { //If Player 2 is sunk
                        plr2.health = 0; //Make sure health does not go negative
                        winner = 1; //Mark winner as Player 1
                        endGame();
                    }
                    break;
            }
            repaint(); //Redraw the screen (see void paint)
        }

        private void endGame() {
            //Reset all variables so the game can be played again
            lock1 = false;
            lock2 = false;
            engine1 = 0;
            rudder1 = 0;
            engine2 = 0;
            rudder2 = 0;
            cannon1 = 0;
            cannon2 = 0;
            p1Balls.clear();
            p2Balls.clear();
            screen = 3;
        }
    }
}
