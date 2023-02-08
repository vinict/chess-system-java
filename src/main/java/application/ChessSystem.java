package application;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;

public class ChessSystem {

    public static void main(String[] args) {
        
        chess.ChessMatch chessMatch = new ChessMatch();
        
        UI.printBoard(chessMatch.getPieces()); /* User interface para receber a matriz de peças da partida e mostrá-la*/
        
    }
}
