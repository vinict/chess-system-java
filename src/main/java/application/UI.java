package application;

public class UI {
    
    public static void printBoard(chess.ChessPiece[][] pieces){ /* Recebe a matriz de ChessPiece */
        for(int i=0; i<pieces.length; i++){
            System.out.print((8-i)+ " "); /* Printando a tabela como no exemplo, mostrando os números das linhas ao lado esquerdo do tabuleiro de xadrez */
            for(int j=0; j<pieces.length; j++){
                printPiece(pieces[i][j]);
            }
            System.out.println();
        }
        System.out.println("  a b c d e f g h"); /* Print para mostrar a letra de cada coluna da matriz, embaixo do tabuleiro de xadrez */
    }
    
    private static void printPiece(chess.ChessPiece piece){
        if(piece == null){
            System.out.print("-");
        }
        else{
            System.out.print(piece);
        }
        System.out.print(" ");
    }
    
}
