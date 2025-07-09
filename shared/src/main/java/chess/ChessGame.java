package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor team;
    private ChessBoard board;
    private ChessPosition startPosition;

    public ChessGame() {
        this.board=new ChessBoard();
        this.team=TeamColor.WHITE;
    }


    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.team;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.team = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece==null ||piece.getTeamColor() != team) {
            return null;
        }
        Collection<ChessMove> moveoptions=piece.pieceMoves(board,startPosition);
        Collection<ChessMove> valmovelist=new ArrayList<>();
        for (ChessMove mov: moveoptions){
            ChessBoard copyBoard=new ChessBoard();
            int col;
            int row;
            row=1;
            while (row<=8) {
                col=1;
                while (col<=8) {
                    ChessPosition newPos = new ChessPosition(row, col);
                    ChessPiece opponent = board.getPiece(newPos);
                    copyBoard.addPiece(newPos, opponent);
                    col++;
                }
                row++;
            }
            ChessPiece movingPiece = board.getPiece(mov.getStartPosition());
            copyBoard.addPiece(mov.getStartPosition(),null);
            copyBoard.addPiece(mov.getEndPosition(),movingPiece);

            //have to make copy of board so can use isInCheckHelp
            ChessBoard ogBoard = this.board;
            this.board=copyBoard;
            boolean isInCheckHelp = isInCheck(team);
            //switch back to original board
            this.board=ogBoard;
            if(!isInCheckHelp){
                valmovelist.add(mov);
            }
        }
        return valmovelist;
    }
    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null) {
            throw new InvalidMoveException("No piece");
        }
        if (piece.getTeamColor()!=team){
            throw new InvalidMoveException("Not this team's turn");
        }
        Collection<ChessMove> valmoves = validMoves(move.getStartPosition());
        if(valmoves==null || !valmoves.contains(move)){
            throw new InvalidMoveException("Invalid move");
        }
        //add piecees to the board
        board.addPiece(move.getEndPosition(), piece);
        board.addPiece(move.getStartPosition(),null);
        //next teams turn
        if (this.team==TeamColor.BLACK){
            this.team=TeamColor.WHITE;
        }else{
            if(this.team==TeamColor.WHITE){
                this.team=TeamColor.BLACK;
            }
        }
    }
    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        //for testing purposes
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }
}
