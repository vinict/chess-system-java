package boardgame;

public class Board {
    
    private int rows, columns;
    private Piece[][] pieces;

    public Board() {
    }

    public Board(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        pieces = new Piece[rows][columns];
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }
    
    public Piece piece(int row, int column){ /* Retornar a linha e a coluna da peça */
    
        return pieces[row][column];
    }
    
    public Piece piece(Position position){
        
        return pieces[position.getRow()][position.getColumn()]; /* Retornar a peça pela posição dela */
    }
    
    public void placePiece(Piece piece, Position position){ /* Pegar a matriz na posição informada (row + column) e posicionar a peça em tal posição */
        pieces[position.getRow()][position.getColumn()] = piece;
        piece.position = position; /* Conseguimos acessar a posição da peça sendo ela protected, pois tanto Piece como Board estão no mesmo pacote */
    }
    
    
    
}
