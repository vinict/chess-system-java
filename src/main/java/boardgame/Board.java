package boardgame;

public class Board {
    
    private int rows, columns;
    private Piece[][] pieces;

    public Board() {
    }

    public Board(int rows, int columns){
        if(rows < 1 || columns < 1){ /* Faz-se a verificação de que para se ter um tabuleiro, necessariamente tem de haver 1 ou mais linhas e colunas */
            throw new BoardException("Error creating the board: there must be at least 1 row and 1 column.");
        }
        this.rows = rows;
        this.columns = columns;
        pieces = new Piece[rows][columns];
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }
 
    public Piece piece(int row, int column){ /* Retornar a linha e a coluna da peça */    
        if(!positionExists(row, column)){
            throw new BoardException("Position not on the board!");
        }
        return pieces[row][column];
    }
    
    public Piece piece(Position position){
        if(!positionExists(position)){
            throw new BoardException("Position not on the board!");
        }
        return pieces[position.getRow()][position.getColumn()]; /* Retornar a peça pela posição dela */
    }
    
    public void placePiece(Piece piece, Position position){ /* Pegar a matriz na posição informada (row + column) e posicionar a peça em tal posição */
        if(thereIsAPiece(position)){
            throw new BoardException("There is already a piece on position " + position);
        }
        pieces[position.getRow()][position.getColumn()] = piece;
        piece.position = position; /* Conseguimos acessar a posição da peça sendo ela protected, pois tanto Piece como Board estão no mesmo pacote */
    }
    
    public Piece removePiece(Position position){
        if(!positionExists(position)){
            throw new BoardException("Position not on the board!");
        }
        if(piece(position) == null){
            return null;
        }else{
            Piece aux = piece(position);
            aux.position = null;
            pieces[position.getRow()][position.getColumn()] = null;
            return aux;
        }
    }
    
    private boolean positionExists(int row, int column){ /* Verificação se uma posição existe, verificando as linhas e colunas da matriz */
        return (row >= 0 && row < rows && column >=0 && column < columns);  
    }
    
    public boolean positionExists(Position position){
        return positionExists(position.getRow(), position.getColumn());
    }
    
    public boolean thereIsAPiece(Position position){ /* Método para testar se há uma peça nessa posição */
        if(!positionExists(position)){
            throw new BoardException("Position not on the board!");
        }
        return piece(position) != null; 
    }
    
}
