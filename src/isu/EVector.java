package isu;

public class EVector {
    //EVector means Euclidian vector a vector that has magnitude and direction

    double x, y; //Store vector as an x component and y component

    public EVector(double xIn, double yIn) { //Create a vector with x and y components
        x = xIn;
        y = yIn;
    }

    public void add(EVector that) { //Add a vector to this vector
        //Add x and y components
        x += that.x;
        y += that.y;
    }

    public void sub(EVector that) { //Subtract a vector from this vector
        //Subtract x and y components
        x -= that.x;
        y -= that.y;
    }

    public void mult(double m) { //Multiply two vectors
        //Multiply x and y components
        x *= m;
        y *= m;
    }

    public void div(double d) { //Divide two vectors
        if (d != 0) { //Don't divide by 0
            //Divide x and y components
            x /= d;
            y /= d;
        }
    }

    public double mag() { //Get magnitude of this vector
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)); //Pythagorean theorem
    }

    public double angle() { //Get the direction of this vector
        if (x != 0) { //Make sure atan() doesn't divide by 0
            return Math.atan2(y, x);
        } else if (y > 0) { //Return 90 degrees if there is a positive y component and x is 0
            return Math.PI / 2;
        } else if (y < 0) { //Return -90 degrees if there is a negative y component and x is 0
            return -Math.PI / 2;
        } else { //If both x and y are 0, return 0 degrees
            return 0;
        }
    }

    public void normalize() { //Normalizing a vector makes its magnitude 1
        div(mag()); //Divide the vector by its magnitude
    }

    public static EVector div(EVector v, double d) { //Static version of divion function
        EVector r; //Vector to return
        if (d != 0) { //Don't divide by 0
            r = new EVector(v.x /= d, v.y /= d); //Divide x and y components
        } else {
            r = new EVector(0, 0);
        }
        return r; //Return the value 
    }

    public static EVector sub(EVector that, EVector that2) { //Static version of subtraction function
        return new EVector(that.x - that2.x, that.y - that2.y); //Subtract x and y components
    }
}
