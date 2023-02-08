package chess;

import boardgame.Board;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

public class ChessMatch {
    
    private Board board;

    public ChessMatch(){
        board = new Board(8, 8);
        initialSetup();
    }
    
    public ChessPiece[][] getPieces(){ /* Método para retornar uma matriz de peças de xadrez correspondente à partida */
        
        ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
        
        for(int i=0; i<board.getRows(); i++){
            for(int j=0; j<board.getColumns(); j++){
                mat[i][j] = (ChessPiece) board.piece(i, j); /* Fazendo um downcasting da matriz de board pieces para a matriz de ChessPieces */
            }
        }        
        return mat;
    }
    
    private void placeNewPiece(char column, int row, ChessPiece piece){
        board.placePiece(piece, new ChessPosition(column, row).toPosition());
    }
    
    
    private void initialSetup(){
        placeNewPiece('b', 6, new Rook(board, Color.BLACK));
        
    }
    
}
