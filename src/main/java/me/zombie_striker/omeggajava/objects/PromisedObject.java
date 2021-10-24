package me.zombie_striker.omeggajava.objects;

public class PromisedObject {

    private Object promise = null;

    public PromisedObject(){

    }
    public void setPromise(Object o){
        this.promise = o;
    }
    public boolean fulfilledPromise(){
        return promise != null;
    }

    public Object getPromise() {
        return promise;
    }
}
