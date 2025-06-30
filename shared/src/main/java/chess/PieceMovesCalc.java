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
            //case ROOK:
            //    return calcRookMoves(board, position, piece);
            // case PAWN:
            //   return calcPawnMoves(board, position, piece);
            //case KNIGHT:
            //   return calcKnightMoves(board, position, piece);
            default:
                return new ArrayList<>();
        }
    }

    private Collection<ChessMove> calcKingMoves(ChessBoard board, ChessPosition position, ChessPiece piece) {
        //king can go one space any direction, including diagonals
        List<ChessMove> moves = new ArrayList<>();
        int[][] directions = {
                {0, 1}, {1, 0}, {0, -1}, {-1, 0},
                // left, right, up down
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };
        //diagonals
        for (int[] mov : directions) {
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

    private Collection<ChessMove> calcQueenMoves(ChessBoard board, ChessPosition position, ChessPiece piece) {
        List<ChessMove> moves = new ArrayList<>();
        int[][] direction = {
                {0, 1}, {1, 0}, {0, -1}, {-1, 0},//left, right, up down
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}//diagonals
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
                ChessPiece target = board.getPiece(newPos);
                if (target == null) {
                    moves.add(new ChessMove(position, newPos, null));
                } else {
                    if (target.getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(position, newPos, null));
                    }
                    break;
                }
            }
        }

        return moves;
    }


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
                ChessPiece target = board.getPiece(newPos);
                if (target == null) {
                    moves.add(new ChessMove(position, newPos, null));
                } else {
                    if (target.getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(position, newPos, null));
                    }
                    break;
                }
            }
        }

        return moves;
    }
}

  //  private Collection<ChessMove> calcQueenMoves(ChessBoard board, ChessPosition position, ChessPiece piece) {
  //      List<ChessMove> moves = new ArrayList<>();
   //     int[][] directions = {

   //     }
  //  }






