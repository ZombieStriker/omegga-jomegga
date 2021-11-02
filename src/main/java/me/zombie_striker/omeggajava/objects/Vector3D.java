package me.zombie_striker.omeggajava.objects;

public class Vector3D {

    private double x,y,z;

    public Vector3D(double x, double y,  double z){
        this.x=x;
        this.y=y;
        this.z=z;
    }

    public double getZ() {
        return z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double distanceSquared(Vector3D location){
        double xdist = getX()-location.getX();
        double ydist = getY()-location.getY();
        double zdist = getZ()-location.getZ();
        return (xdist*xdist)+(ydist*ydist)+(zdist*zdist);
    }
    public double distance(Vector3D location){
        return Math.sqrt(distanceSquared(location));
    }
}
