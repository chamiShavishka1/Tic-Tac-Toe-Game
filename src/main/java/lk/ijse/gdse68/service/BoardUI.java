package lk.ijse.gdse68.service;

public interface BoardUI {
    void update(int col, int row, boolean isHuman);
    void notifyWinner(Piece winner);
}