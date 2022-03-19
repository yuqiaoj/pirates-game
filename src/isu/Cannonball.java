package isu;

import java.awt.Color;
import java.awt.Graphics;

public class Cannonball extends GameObject {

    int damage, size, speed; //Keep track of the cannonball's stats

    public Cannonball(double x, double y, double h, int type) { //Cannonballs are initialized with x position, y position, heading, and type
        super(x, y, 10); //A cannonball is a GameObject
        switch (type) { //Set stats of the cannonball depending on the type of ship that fired it
            case (0):
                size = 7;
                speed = 50;
                damage = 5;
                break;
            case (1):
                size = 7;
                speed = 50;
                damage = 5;
                break;
            case (2):
                size = 12;
                speed = 40;
                damage = 15;
                break;
            case (3):
                size = 7;
                speed = 50;
                damage = 5;
                break;
        }
        applyForce(new EVector(Math.cos(h) * speed, Math.sin(h) * speed)); //Apply a force to cannonball depending on its speed
    }

    @Override
    public void periodic() {
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillOval((int) pos.x, (int) pos.y, size, size); //Draw cannonball
    }
}
