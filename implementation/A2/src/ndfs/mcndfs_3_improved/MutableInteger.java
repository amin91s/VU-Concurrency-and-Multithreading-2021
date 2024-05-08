package ndfs.mcndfs_3_improved;

public class MutableInteger {
    private int val;

    public MutableInteger(int val) {
        this.val = val;
    }

    public int get() {
        return val;
    }

    public MutableInteger set(int val) {
        this.val = val;
        return this;
    }
    public MutableInteger incCounter(){
        this.val++;
        return this;
    }
    public MutableInteger decCounter(){
        this.val--;
        return this;
    }
}
