package ir.aryanmo.advancerecyclerview;

public interface OnListItemListener {
    void onClick(int id);
    void onLongClick(int id);
    void on(String action, int id, int pos);
}
