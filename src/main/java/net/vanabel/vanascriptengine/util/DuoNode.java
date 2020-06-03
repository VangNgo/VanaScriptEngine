package net.vanabel.vanascriptengine.util;

public class DuoNode<L, R> {

    private L l = null;
    private R r = null;

    public DuoNode() {}

    public DuoNode(L left, R right) {
        l = left;
        r = right;
    }

    public L getLeft() {
        return l;
    }

    public void setLeft(L newLeft) {
        l = newLeft;
    }

    public R getRight() {
        return r;
    }

    public void setRight(R newRight) {
        r = newRight;
    }
}
