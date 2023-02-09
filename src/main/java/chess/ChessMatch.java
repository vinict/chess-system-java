package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

public class ChessMatch {
    
    private Board board;
    private int turn;
    private Color currentPlayer;
    
    public ChessMatch(){
        board = new Board(8, 8);
        turn = 1;
        currentPlayer = Color.WHITE;
        initialSetup();
    }
    
    public int getTurn(){
        return turn;
    }
    
    public Color getCurrentPlayer(){
        return currentPlayer;
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
    
    public boolean[][] possiblesMoves(ChessPosition sourcePosition){ /* Imprimir os movimentos possiveis a partir de um ponto de origem */
        Position position = sourcePosition.toPosition();
        validateSourcePosition(position);
        return board.piece(position).possibleMoves();
    }
    
    public ChessPiece performChessMove(ChessPosition sourcePostion, ChessPosition targetPosition){
        Position source = sourcePostion.toPosition();
        Position target = targetPosition.toPosition();
        validateSourcePosition(source);
        validateTargetPosition(source, target);
        Piece capturedPiece = makeMove(source, target);
        nextTurn();
        return (ChessPiece) capturedPiece;
    }
    
    private Piece makeMove(Position source, Position target){
        Piece p = board.removePiece(source); /* Removendo a peça da posição de origem */
        Piece capturedPiece = board.removePiece(target); /* Removendo a possivel peça que está na posição de destino */
        board.placePiece(p, target);
        
        return capturedPiece;
    }
    
    private void validateSourcePosition(Position position){
        if(!board.thereIsAPiece(position)){
            throw new ChessException("There is no piece on source position.");
        }
        if(currentPlayer != ((ChessPiece) board.piece(position)).getColor()){ /* Se a cor do atual jogador for DIFERENTE da peça escolhida, dará erro, pois é uma peça adversária */
            throw new ChessException("The chosen piece isn't yours.");
        }
        if(!board.piece(position).isThereAnyPossibleMove()){
            throw new ChessException("There is no possible moves for the chosen piece.");
        }
        
    }
    
    private void validateTargetPosition(Position source, Position target){
        if(!board.piece(source).possibleMove(target)){ /* Se pra peça de origem a posição de destino não é um movimento possível, então não posso mover a peça para lá */
            throw new ChessException("The chosen piece can't move to target.");
        }
    }
    
    private void nextTurn(){
        turn++;
        currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    private void initialSetup(){
        placeNewPiece('c', 1, new Rook(board, Color.WHITE));
        placeNewPiece('c', 2, new Rook(board, Color.WHITE));
        placeNewPiece('d', 2, new Rook(board, Color.WHITE));
        placeNewPiece('e', 2, new Rook(board, Color.WHITE));
        placeNewPiece('e', 1, new Rook(board, Color.WHITE));
        placeNewPiece('d', 1, new King(board, Color.WHITE));

        placeNewPiece('c', 7, new Rook(board, Color.BLACK));
        placeNewPiece('c', 8, new Rook(board, Color.BLACK));
        placeNewPiece('d', 7, new Rook(board, Color.BLACK));
        placeNewPiece('e', 7, new Rook(board, Color.BLACK));
        placeNewPiece('e', 8, new Rook(board, Color.BLACK));
        placeNewPiece('d', 8, new King(board, Color.BLACK));
        
    }
    
}
