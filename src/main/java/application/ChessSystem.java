package application;
import java.util.Scanner;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;


public class ChessSystem {

    public static void main(String[] args) {
        
        Scanner sc = new Scanner(System.in);
        chess.ChessMatch chessMatch = new ChessMatch();
        
        while(true){
            UI.printBoard(chessMatch.getPieces()); /* User interface para receber a matriz de peças da partida e mostrá-la*/
            
            System.out.println();
            System.out.print("Source: ");
            ChessPosition source = UI.readChessPosition(sc);
            
            System.out.println();
            System.out.print("Target: ");
            ChessPosition target = UI.readChessPosition(sc);
            
            ChessPiece capturedPiece = chessMatch.performChessMove(source, target);
            
        }   
        
        
        
    }
}
