package ui;

import static ui.EscapeSequences.*;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class GamePlayRepl {

    private final PrintStream out;
    private final int gameID;
    private final String role;
    private final boolean isBlackPerspective;

    private static final String EMPTY = " ";
    private static final int BOARD_SIZE_IN_SQUARES = 8;

    public GamePlayRepl(Object client, int gameID, String role){
        this.out=new PrintStream(System.out, true, StandardCharsets.UTF_8);
        this.gameID=gameID;
        this.role=role;
        this.isBlackPerspective=role.equalsIgnoreCase("BLACK");
    }

    public void run() {
        out.print(ERASE_SCREEN);
        drawHeaders();
        drawBoard();
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private void drawHeaders() {
        out.print(SET_TEXT_COLOR_WHITE);
        out.print("   ");
        for (int col = 0; col < BOARD_SIZE_IN_SQUARES; ++col) {
            int displayCol = isBlackPerspective ? (BOARD_SIZE_IN_SQUARES - 1 - col) : col;
            char file = (char) ('a' + displayCol);
            out.print(" " + file + " ");
        }
        out.println();
    }

    private void drawBoard() {
        for (int row = 0; row < BOARD_SIZE_IN_SQUARES; ++row) {
            int displayRow = isBlackPerspective ? row : (BOARD_SIZE_IN_SQUARES - 1 - row);
            out.print(" " + (displayRow + 1) + " ");
            drawRowOfSquares(displayRow);
            out.print(" " + (displayRow + 1));
            out.println();
        }
    }
    private void drawRowOfSquares(int row) {
        for (int col = 0; col < BOARD_SIZE_IN_SQUARES; ++col) {
            int displayCol = isBlackPerspective ? (BOARD_SIZE_IN_SQUARES - 1 - col) : col;
            boolean isLight = (row + displayCol) % 2 == 0;
            String piece = INITIAL_BOARD[row][displayCol];
            drawSquare(out, piece, isLight);
        }
    }

    private void drawSquare(PrintStream out, String piece, boolean isLight) {
        out.print(isLight ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_BLACK);
        if (piece.equals(EMPTY)) {
            out.print("   ");
        } else if (Character.isUpperCase(piece.charAt(0))) {
            out.print(SET_TEXT_COLOR_RED + piece);
        } else if (Character.isLowerCase(piece.charAt(0))){
            out.print(SET_TEXT_COLOR_BLUE + piece);
        }
    }

    private static final String[][] INITIAL_BOARD = {
            {BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK},
            {BLACK_PAWN, BLACK_PAWN,   BLACK_PAWN,   BLACK_PAWN,  BLACK_PAWN,  BLACK_PAWN,   BLACK_PAWN,   BLACK_PAWN},
            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
            {WHITE_PAWN, WHITE_PAWN,   WHITE_PAWN,   WHITE_PAWN,  WHITE_PAWN,  WHITE_PAWN,   WHITE_PAWN,   WHITE_PAWN},
            {WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK}
    };
}


