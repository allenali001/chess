package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PieceMovesCalc {
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position, ChessPiece piece) {
        switch (piece.getPieceType()) {
            case KING:
                return calcKingMoves(board, position, piece);
            case QUEEN:
                return calcQueenMoves(board, position, piece);
            case BISHOP:
                return calcBishopMoves(board, position, piece);
            case ROOK:
                return calcRookMoves(board, position, piece);
            case PAWN:
                return calcPawnMoves(board, position, piece);
            case KNIGHT:
                return calcKnightMoves(board, position, piece);
            default:
                return new ArrayList<>();
        }
    }

    //KING
    private Collection<ChessMove> calcKingMoves(ChessBoard board, ChessPosition position, ChessPiece piece) {
        //king can go one space any direction, including diagonals
        List<ChessMove> moves = new ArrayList<>();
        int[][] direction = {
                {0, 1}, {1, 0}, {0, -1}, {-1, 0},
                // left, right, up down
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };
        //diagonals
        for (int[] mov : direction) {
            int newRow = position.getRow() + mov[0];
            int newCol = position.getColumn() + mov[1];
            if (newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8) {
                break;
            }
            ChessPosition newPos = new ChessPosition(newRow, newCol);
            ChessPiece opponent = board.getPiece(newPos);
            if (opponent == null) {
                moves.add(new ChessMove(position, newPos, null));
            } else {
                if (opponent.getTeamColor() != piece.getTeamColor()) {
                    moves.add(new ChessMove(position, newPos, null));

                }
            }
        }
        return moves;
    }


    //QUEEN
    private Collection<ChessMove> calcQueenMoves(ChessBoard board, ChessPosition position, ChessPiece piece) {
        List<ChessMove> moves = new ArrayList<>();
        int[][] direction = {
                {0, 1}, {1, 0}, {0, -1}, {-1, 0},//left, right, up down
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}//diagonals
        };
        for (int[] mov : direction) {
            int newRow = position.getRow();
            int newCol = position.getColumn();
            while (true) {
                newRow += mov[0];
                newCol += mov[1];
                if (newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8) {
                    break;
                }
                ChessPosition newPos = new ChessPosition(newRow, newCol);
                ChessPiece opponent = board.getPiece(newPos);
                if (opponent == null) {
                    moves.add(new ChessMove(position, newPos, null));
                } else {
                    if (opponent.getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(position, newPos, null));
                    }
                    break;
                }
            }
        }
        return moves;
    }


    //BISHOP
    private Collection<ChessMove> calcBishopMoves(ChessBoard board, ChessPosition position, ChessPiece piece) {
        List<ChessMove> moves = new ArrayList<>();
        int[][] direction = {
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1} //up right, up left, down right, down left
        };
        for (int[] dir : direction) {
            int newRow = position.getRow();
            int newCol = position.getColumn();
            while (true) {
                newRow += dir[0];
                newCol += dir[1];
                if (newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8) { // make sure it's in the bounds of the chess board
                    //cant be less than one or greater than 8
                    break;
                }
                ChessPosition newPos = new ChessPosition(newRow, newCol);
                ChessPiece opponent = board.getPiece(newPos);
                if (opponent == null) {
                    moves.add(new ChessMove(position, newPos, null));
                } else {
                    if (opponent.getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(position, newPos, null));
                    }
                    break;
                }
            }
        }

        return moves;
    }

    //PAWN
    private Collection<ChessMove> calcPawnMoves(ChessBoard board, ChessPosition position, ChessPiece piece) {
        List<ChessMove> moves = new ArrayList<>();
        int dir = 1;
        int strt = 2;
        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            dir = -1;
            strt = 7;
        }
        int row = position.getRow();
        int col = position.getColumn();

        //moveone
        int oneclear=row+dir;
        if (oneclear >=1 &&oneclear<=8 && col>=1 && col<=8){
            ChessPosition moveone=new ChessPosition(oneclear,col);
            if (board.getPiece(moveone)==null){
                if (promorow(oneclear, piece.getTeamColor())){
                    applypromo(moves,position,moveone);
                }else{
                moves.add(new ChessMove(position,moveone,null));
            }
        //movetwo if first
            if (row == strt) {
                ChessPosition movetwo = new ChessPosition(row + 2 * dir, col);
                int tworow = movetwo.getRow();
                ChessPiece piecethere = board.getPiece(movetwo);
                if (tworow >= 1 && tworow <= 8 && piecethere == null) {
                    moves.add(new ChessMove(position, movetwo, null));
                }
            }
                }
            }


            //capture left
                int leftrow = row+dir;
                int leftcol = col-1;
                if (leftrow>=1 &&leftrow<=8 &&leftcol>=1 && leftcol<=8) {
                    ChessPosition leftcap = new ChessPosition(leftrow, leftcol);
                    ChessPiece opponent = board.getPiece(leftcap);
                    if (opponent != null && opponent.getTeamColor() != piece.getTeamColor()) {
                        if (promorow(leftrow, piece.getTeamColor())) {
                            applypromo(moves, position, leftcap);
                        } else {
                            moves.add(new ChessMove(position, leftcap, null));
                        }
                    }
                }
                //capright
                int rightrow=row+dir;
                int rightcol=col+1;
                if (rightrow>=1 && rightrow<= 8 && rightcol>=1 &&rightcol<=8) {
                    ChessPosition rightcap = new ChessPosition(rightrow, rightcol);
                    ChessPiece opponent = board.getPiece(rightcap);
                    if (opponent != null && opponent.getTeamColor() != piece.getTeamColor()) {
                        if (promorow(rightrow, piece.getTeamColor())) {
                            applypromo(moves, position, rightcap);
                        }else{
                            moves.add(new ChessMove(position, rightcap, null));
                        }
                    }
                }
        return moves;
        }

    private boolean promorow(int row, ChessGame.TeamColor color) {
        if (color == ChessGame.TeamColor.WHITE && row == 8) {
            return true;
        } else {
            if (color == ChessGame.TeamColor.BLACK && row == 1) {
                return true;
            }else{
                return false;
            }
        }
    }

    private void applypromo(List<ChessMove> moves, ChessPosition start, ChessPosition end ) {
        moves.add(new ChessMove(start, end, ChessPiece.PieceType.QUEEN));
        moves.add(new ChessMove(start, end, ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(start, end, ChessPiece.PieceType.KNIGHT));
        moves.add(new ChessMove(start, end, ChessPiece.PieceType.BISHOP));
    }





    //ROOK
    private Collection<ChessMove> calcRookMoves(ChessBoard board, ChessPosition position, ChessPiece piece) {
        List<ChessMove> moves = new ArrayList<>();
        int[][] direction = {
                {1, 0}, {0, 1}, {-1, 0}, {0, -1}}; //up down left right
        for (int[] dir : direction) {
            int newRow = position.getRow();
            int newCol = position.getColumn();
            while (true) {
                newRow += dir[0];
                newCol += dir[1];
                if (newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8) { // make sure it's in the bounds of the chess board
                    //cant be less than one or greater than 8
                    break;
                }
                ChessPosition newPos = new ChessPosition(newRow, newCol);
                ChessPiece opponent = board.getPiece(newPos);
                if (opponent == null) {
                    moves.add(new ChessMove(position, newPos, null));
                } else {
                    if (opponent.getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(position, newPos, null));
                    }
                    break;
                }
            }
        }
        return moves;
    }


    //KNIGHT
private Collection<ChessMove> calcKnightMoves(ChessBoard board, ChessPosition position, ChessPiece piece) {
    List<ChessMove> moves = new ArrayList<>();
    int[][] direction = {{1, 2}, {2, 1}, {-1, 2}, {1, -2},
            {2, -1}, {-2, 1}, {-2,-1}, {-1,-2}};
    for (int[] dir : direction) {
        int newRow = position.getRow() + dir[0];
        int newCol = position.getColumn() + dir[1];
        if (newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8) { // make sure it's in the bounds of the chess board
            //cant be less than one or greater than 8
            continue;
        }
        ChessPosition newPos = new ChessPosition(newRow, newCol);
        ChessPiece opponent = board.getPiece(newPos);
        if (opponent == null) {
            moves.add(new ChessMove(position, newPos, null));
        }else {
            if (opponent.getTeamColor() != piece.getTeamColor()) {
                moves.add(new ChessMove(position, newPos, null));
            }
        }
        }
        return moves;
    }
}






