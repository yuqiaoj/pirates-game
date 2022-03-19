package isu;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Color;

public class Ship extends GameObject {

    double heading, dragCoeff, dragSide, enginePow, rudderPow;
    int maxHealth, health, cannon, reload, type, length, width;
    Shape box; //Hitbox of ship
    BufferedImage img;

    public Ship(double x, double y, double h, int t) { //Ships are initialized with x position, y position, heading, and type
        super(x, y, 0); //A Ship is a GameObject. Mass set to 0 temporarily because it is not known until later
        type = t; //Get type of ship
        try {
            switch (type) { //Initialize the stats and image for ship depending on what type it is
                case (0): //Sloop
                    img = ImageIO.read(getClass().getClassLoader().getResourceAsStream("images/sloop.png"));
                    mass = 10;
                    dragCoeff = 0.5;
                    dragSide = 1;
                    length = 40;
                    width = 30;
                    enginePow = 5;
                    rudderPow = 0.015;
                    maxHealth = 50;
                    reload = 40;
                    break;
                case (1): //Schooner
                    img = ImageIO.read(getClass().getClassLoader().getResourceAsStream("images/schooner.png"));
                    mass = 15;
                    dragCoeff = 1;
                    dragSide = 3;
                    length = 50;
                    width = 38;
                    enginePow = 5;
                    rudderPow = 0.01;
                    maxHealth = 75;
                    reload = 25;
                    break;
                case (2): //Brigantine
                    img = ImageIO.read(getClass().getClassLoader().getResourceAsStream("images/brigantine.png"));
                    mass = 20;
                    dragCoeff = 1.5;
                    dragSide = 4;
                    length = 64;
                    width = 46;
                    enginePow = 4.5;
                    rudderPow = 0.005;
                    maxHealth = 100;
                    reload = 50;
                    break;
                case (3): //Galleon
                    img = ImageIO.read(getClass().getClassLoader().getResourceAsStream("images/galleon.png"));
                    mass = 30;
                    dragCoeff = 3;
                    dragSide = 5;
                    length = 64;
                    width = 46;
                    enginePow = 3.5;
                    rudderPow = 0.0075;
                    maxHealth = 125;
                    reload = 40;
                    break;
            }
        } catch (IOException e) {
        }
        heading = h; //Set heading
        health = maxHealth; //Set health to full
    }

    @Override
    public void draw(Graphics g) { //Function to draw ship
        Graphics2D g2 = (Graphics2D) g; //Cast g to Graphics2D to allow for AffineTransform to be used

        //Draw the healthbar
        g2.setPaint(Color.RED);
        g2.fillRect((int) pos.x + length, (int) pos.y, maxHealth, 10);
        g2.setPaint(Color.GREEN);
        g2.fillRect((int) pos.x + length, (int) pos.y, health, 10);

        AffineTransform old = g2.getTransform();//Keep track of old transformation of Graphics2D

        //Rotate ship depending on heading, centered in the middle of the ship, and draw it
        g2.rotate(heading + Math.PI / 2, pos.x + length / 2.0, pos.y + width / 2.0);
        g2.drawImage(img, (int) pos.x + length / 6, (int) pos.y - width / 4, null);

        //Get the hitbox of the ship
        box = new Rectangle((int) pos.x, (int) pos.y, length, width); //Initialize hitbox position
        AffineTransform transform = g2.getTransform(); //Get current transformation of the Graphics2D
        //Because 0 degrees on a unit circle is 90 degrees on a Graphics2D, the hitbox must be rotated 90 degrees counterclockwise
        transform.rotate(-Math.PI / 2, pos.x + length / 2.0, pos.y + width / 2.0);
        box = transform.createTransformedShape(box); //Rotate the hitbox

        //Reset the transformation of the Graphics2D
        g2.setTransform(old);
    }

    @Override
    public void periodic() {
        //Make sure that the heading is always in between -180 degrees and 180 degrees, so that other math functions do not break
        if (heading > Math.PI) {
            heading = -Math.PI;
        } else if (heading < -Math.PI) {
            heading = Math.PI;
        }

        if (cannon > 0) { //Keep track of the ship's reload timer
            cannon--;
        }

        //Calculate water drag
        EVector drag = new EVector(vel.x, vel.y); //Vector to keep track of drag
        drag.normalize(); //Drag is in opposite direction of velocity, so get velocity and multiply vector by -1 later
        /* Simplified version of formula for fluid drag, D = (1/2)(C)(ρ)(v2)(A).
        The drag coefficient is also scaled depending on how rotated the ship is, 
        simulating how the ship is more hydrodynamic from the front. */
        drag.mult(-0.5 * Math.pow(vel.mag(), 2) * (dragCoeff + dragSide * (Math.abs(heading - vel.angle()) / (Math.PI / 2))));
        applyForce(drag); //Apply drag force to the ship

        //Bounce the ship if it is offscreen
        if (pos.x < 0 || pos.x + length > 1024) {
            vel.x *= -1.05;
            acc.mult(0); //Stop ship's acceleration
        }
        if (pos.y < 0 || pos.y + length > 586) {
            vel.y *= -1.05;
            acc.mult(0);  //Stop ship's acceleration
        }
    }

    public void collide(ArrayList<Cannonball> b, Ship s) { //Checking for collision
        for (int i = 0; i < b.size(); i++) { //For all enemy cannonballs
            Cannonball c = b.get(i); //Get cannonball
            if (box.intersects(c.pos.x, c.pos.y, c.size, c.size)) {
                //If ship touches cannonball, remove the cannonball and deal damage
                health -= c.damage;
                b.remove(i);
            }
        }
        try { //getBounds sometimes throws a nullpointerexception
            if (box.intersects(s.box.getBounds())) { //If player collides with other player
                //Vector to keep track of how much force to apply. Subtracting finds the direction and distance between the players.
                EVector f = EVector.sub(pos, s.pos);
                f.normalize(); //Only direction is needed, not how far the players are, because it is known that they collided
                f.mult((mass + s.mass) / 2); //Scale the force depending on the mass of ships
                applyForce(f); //Apply collision force to this player
                f.mult(-1); //Flip the direction of the force
                s.applyForce(f); //Apply collision force to other player
                double damage = 0.025 * vel.mag() * s.vel.mag() * ((mass + s.mass) / 2); //Damage scales with the speed of both players
                if (damage > 20) {
                    damage = 20; //Cap maximum collision damage at 20
                }
                //Deal damage to both players
                health -= damage;
                s.health -= damage;
            }
        } catch (NullPointerException e) {
        }
    }

    public void engine(int dir) {
        //Apply engine force to the ship depending on engine power and direction of input (see ISU class keyPressed)
        applyForce(new EVector(Math.cos(heading) * dir * enginePow, Math.sin(heading) * dir * enginePow));
    }

    public void rudder(int dir) {
        //Apply turning force to the ship depending on rudder power and direction of input (see ISU class keyPressed)
        heading += dir * rudderPow * vel.mag();
    }

    public void cannon(int dir, ArrayList b) { //Fire a cannonball
        if (dir != 0) { //Make sure the input is not 0
            if (cannon <= 0) { //If the ship has reloaded
                //Heading of cannonball is heading of ship plus or minus 90 degrees, depending on direction
                double h = heading + dir * (Math.PI / 2);
                //Add a cannonball to the array of player's cannonballs
                b.add(new Cannonball(pos.x + length / 2 + Math.cos(h) * length / 2, pos.y + width / 2 + Math.sin(h) * width / 2, h, type));
                if (type == 3) { //If the ship is a galleon, fire two more cannonballs
                    b.add(new Cannonball(pos.x - Math.cos(heading) * 10 + length / 2 + Math.cos(h) * length / 2, pos.y - Math.sin(heading) * 10 + width / 2 + Math.sin(h) * width / 2, h, type));
                    b.add(new Cannonball(pos.x + Math.cos(heading) * 10 + length / 2 + Math.cos(h) * length / 2, pos.y + Math.sin(heading) * 10 + width / 2 + Math.sin(h) * width / 2, h, type));
                }
                cannon = reload; //Keep track of ship's reload timer
            }
        }
    }
}
