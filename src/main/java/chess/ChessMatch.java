package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import chess.pieces.Queen;
import chess.pieces.Rook;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChessMatch {
    
    private Board board;
    private int turn;
    private Color currentPlayer;
    private boolean check;
    private boolean checkMate;
    private List<Piece> piecesOnTheBoard = new ArrayList<>(); /* Lista de peças no tabuleiro */
    private List<Piece> capturedPieces = new ArrayList<>(); /*Lista de peças capturadas */
    private ChessPiece enPassantVulnerable;
    private ChessPiece promoted;
    
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
    
    public ChessPiece getEnPassantVulnerable(){
        return enPassantVulnerable;
    }
    
    public boolean getCheck(){
        return check;
    }
    
    public boolean getCheckMate(){
        return checkMate;
    }
    
    public ChessPiece getPromoted(){
        return promoted;
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
    
    private void placeNewPiece(char column, int row, ChessPiece piece){ /* Método para inicialização do tabuleiro, colocando as peças nos lugares */
        board.placePiece(piece, new ChessPosition(column, row).toPosition());
        piecesOnTheBoard.add(piece); 
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
        
        if(testCheck(currentPlayer)){
            undoMove(source, target, capturedPiece);
            throw new ChessException("You cant't put yourself in check!");
        }
        
        ChessPiece movedPiece = (ChessPiece)board.piece(target);
        
        /* Movimento Especial Promotion */
        promoted = null;
        if(movedPiece instanceof Pawn){
            if(movedPiece.getColor() == Color.WHITE && target.getRow() == 0 || movedPiece.getColor() == Color.BLACK && target.getRow() == 7){
                promoted = (ChessPiece)board.piece(target);
                promoted = replacePromotedPiece("Q");
            }
        }
        
        check = (testCheck(opponent(currentPlayer))) ? true : false;
        
        if(testCheckMate(opponent(currentPlayer))){
            checkMate = true;
        }else{
            nextTurn();
        }
        
        /* Movimento especial En Passant */
        if(movedPiece instanceof Pawn && (target.getRow() == source.getRow() -2 || target.getRow() == source.getRow() + 2)){
            enPassantVulnerable = movedPiece;
        }else{
            enPassantVulnerable = null;
        }
        
        return (ChessPiece) capturedPiece;
    }
    
    public ChessPiece replacePromotedPiece(String type){
        if(promoted == null){
            throw new IllegalStateException("There is no piece to be promoted!");
        }
        if(!type.equals("B") && !type.equals("N") && !type.equals("R") && !type.equals("Q")){
            return promoted;
        }
        Position pos = promoted.getChessPosition().toPosition();
        Piece p = board.removePiece(pos);
        piecesOnTheBoard.remove(p);
        
        ChessPiece newPiece = newPiece(type, promoted.getColor());
        board.placePiece(newPiece, pos);
        piecesOnTheBoard.add(newPiece);
        return newPiece;
    }
    
    private ChessPiece newPiece(String type, Color color){
        if(type.equals("B")) return new Bishop(board, color);
        if(type.equals("N")) return new Knight(board, color);
        if(type.equals("Q")) return new Queen(board, color);
        return new Rook(board, color);
    }
    
    private Piece makeMove(Position source, Position target){
        ChessPiece p = (ChessPiece)board.removePiece(source); /* Removendo a peça da posição de origem */
        p.increaseMoveCount();
        Piece capturedPiece = board.removePiece(target); /* Removendo a possivel peça que está na posição de destino */
        board.placePiece(p, target);
        
        if(capturedPiece != null){ /* Se a peça capturada for diferente de nulo, significa que tem uma peça capturada! */
            piecesOnTheBoard.remove(capturedPiece); /* Removendo a peça capturada do tabuleiro */
            capturedPieces.add(capturedPiece); /* Adicionando a peça capturada na lista de peças capturadas */
        }
        
        /* Movimento especial Castling */
        if(p instanceof King && target.getColumn() == source.getColumn()+2){
            Position sourceT = new Position(source.getRow(), source.getColumn() + 3);
            Position targetT = new Position(source.getRow(), source.getColumn() + 1);
            ChessPiece rook = (ChessPiece)board.removePiece(sourceT);
            board.placePiece(rook, targetT);
            rook.increaseMoveCount();
        }
        
        if(p instanceof King && target.getColumn() == source.getColumn()-2){
            Position sourceT = new Position(source.getRow(), source.getColumn() - 4);
            Position targetT = new Position(source.getRow(), source.getColumn() - 1);
            ChessPiece rook = (ChessPiece)board.removePiece(sourceT);
            board.placePiece(rook, targetT);
            rook.increaseMoveCount();
        }
        
        /* Movimento especial En Passant */
        if(p instanceof Pawn){
            if(source.getColumn() != target.getColumn() && capturedPiece == null){
                Position pawnPosition;
                if(p.getColor() == Color.WHITE){
                    pawnPosition = new Position(target.getRow()+1, target.getColumn());
                }
                else{
                    pawnPosition = new Position(target.getRow()-1, target.getColumn());  
                }
                capturedPiece = board.removePiece(pawnPosition);
                capturedPieces.add(capturedPiece);
                piecesOnTheBoard.remove(capturedPiece);
            }
        }
        return capturedPiece;
    }
    
    private void undoMove(Position source, Position target, Piece capturedPiece){ /* Método para desfazer um movimento */
         ChessPiece p = (ChessPiece)board.removePiece(target);
         p.decreaseMoveCount();
         board.placePiece(p, source);
         if(capturedPiece != null){
             board.placePiece(capturedPiece, target);
             capturedPieces.remove(capturedPiece);
             piecesOnTheBoard.add(capturedPiece);
         }
         
         if(p instanceof King && target.getColumn() == source.getColumn()+2){
            Position sourceT = new Position(source.getRow(), source.getColumn() + 3);
            Position targetT = new Position(source.getRow(), source.getColumn() + 1);
            ChessPiece rook = (ChessPiece)board.removePiece(targetT);
            board.placePiece(rook, sourceT);
            rook.decreaseMoveCount();
        }
        
        if(p instanceof King && target.getColumn() == source.getColumn()-2){
            Position sourceT = new Position(source.getRow(), source.getColumn() - 4);
            Position targetT = new Position(source.getRow(), source.getColumn() - 1);
            ChessPiece rook = (ChessPiece)board.removePiece(targetT);
            board.placePiece(rook, sourceT);
            rook.decreaseMoveCount();
        }
        
        /* Movimento especial En Passant */
        if(p instanceof Pawn){
            if(source.getColumn() != target.getColumn() && capturedPiece == enPassantVulnerable){
                ChessPiece pawn = (ChessPiece)board.removePiece(target);
                Position pawnPosition;
                if(p.getColor() == Color.WHITE){
                    pawnPosition = new Position(3, target.getColumn());
                }
                else{
                    pawnPosition = new Position(4, target.getColumn());  
                }
                board.placePiece(pawn, pawnPosition);                                               
            }
        }
    }
    
    private Color opponent(Color color){
        return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }
    
    private ChessPiece king(Color color){ /* Procurar na lista de peças o rei de tal cor */
        List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
        for(Piece p : list){
            if(p instanceof King){
                return (ChessPiece)p;
            }
        }
        throw new IllegalStateException("There is no " + color + "king on the board");
    }
    
    private boolean testCheck(Color color){
        Position kingPosition = king(color).getChessPosition().toPosition();
        List<Piece> opponentPieces = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == opponent(color)).collect(Collectors.toList());
        for(Piece p : opponentPieces){
            boolean[][] mat = p.possibleMoves();
            if(mat[kingPosition.getRow()][kingPosition.getColumn()]){
                return true;
            }
        }
        return false;
    }
    
    private boolean testCheckMate(Color color){
        if(!testCheck(color)){
            return false;
        }
        List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
        
        for(Piece p: list){
            boolean[][] mat = p.possibleMoves();
            for(int i=0; i<board.getRows();i++){
                for(int j=0; j<board.getColumns(); j++){
                    if(mat[i][j]){
                        Position source = ((ChessPiece)p).getChessPosition().toPosition();
                        Position target = new Position(i, j);
                        Piece capturedPiece = makeMove(source, target);
                        boolean testCheck = testCheck(color);
                        undoMove(source, target, capturedPiece);
                        if(!testCheck){
                            return false;
                        }
                    }
                }
            }
        }
        return true;
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

    private void initialSetup() {
        placeNewPiece('a', 1, new Rook(board, Color.WHITE));
        placeNewPiece('b', 1, new Knight(board, Color.WHITE));
        placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('d', 1, new Queen(board, Color.WHITE));
        placeNewPiece('e', 1, new King(board, Color.WHITE, this));
        placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('g', 1, new Knight(board, Color.WHITE));
        placeNewPiece('h', 1, new Rook(board, Color.WHITE));
        placeNewPiece('a', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('b', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('c', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('d', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('e', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('f', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('g', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('h', 2, new Pawn(board, Color.WHITE, this));

        placeNewPiece('a', 8, new Rook(board, Color.BLACK));
        placeNewPiece('b', 8, new Knight(board, Color.BLACK));
        placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('d', 8, new Queen(board, Color.BLACK));
        placeNewPiece('e', 8, new King(board, Color.BLACK, this));
        placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('g', 8, new Knight(board, Color.BLACK));
        placeNewPiece('h', 8, new Rook(board, Color.BLACK));
        placeNewPiece('a', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('b', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('c', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('d', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('e', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('f', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('g', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('h', 7, new Pawn(board, Color.BLACK, this));
    }
}
