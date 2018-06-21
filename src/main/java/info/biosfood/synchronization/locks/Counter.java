package info.biosfood.synchronization.locks;

public class Counter {

    int counter = 0;

    public void increment() {
        counter += 1;
    }

    public int count() {
        return counter;
    }

}
