package chess.pieces;

import boardgame.Board;
import chess.ChessPiece;
import chess.Color;

public class Rook extends ChessPiece { /* Criação da peça TORRE */

    public Rook(Board board, Color color) {
        super(board, color);
    }

    @Override
    public String toString(){ /* Transformando a peça ROOK(TORRE) em R pois a letra R representará a torre no nosso tabuleiro */
        return "R";
    }

}
