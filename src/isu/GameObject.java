package isu;

import java.awt.Graphics;

public abstract class GameObject { //Template for any object that is affected by physics

    EVector pos; //Vector for position 
    EVector vel = new EVector(0, 0); //Vector for velocity
    EVector acc = new EVector(0, 0); //Vector for acceleration
    double mass; //Mass

    GameObject(double x, double y, double m) { //Initialize a GameObject with position and mass
        pos = new EVector(x, y);
        mass = m;
    }

    abstract void draw(Graphics g); //Game objects must be drawn onto a Graphics

    abstract void periodic(); //Any other periodic functions the GameObject may need

    public void update() { //This should be called periodically with a Timer
        periodic(); //Call periodic()
        vel.add(acc); //Add acceleration to velocity
        pos.add(vel); //Add velocity to position
        acc.mult(0); //Reset acceleration
    }

    public void applyForce(EVector f) { //Apply a vector force to the GameObject
        acc.add(EVector.div(f, mass)); //F = ma
    }

}
