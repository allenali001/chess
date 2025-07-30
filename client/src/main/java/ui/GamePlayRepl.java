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
        this.isBlackPerspective="BLACK".equalsIgnoreCase(role);
    }

    public void run() {
        out.print(ERASE_SCREEN);
        drawHeaders();
        drawBoard();
        drawHeaders();
        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
    }

    private void drawHeaders() {
        out.print(SET_TEXT_COLOR_WHITE);
        out.print("   ");
        for (int i = 0; i < BOARD_SIZE_IN_SQUARES; ++i) {
            int displayCol = isBlackPerspective ? (BOARD_SIZE_IN_SQUARES - 1 - i) : i;
            char file = (char) ('a' + displayCol);
            out.print(" " + file + " ");
        }
        out.println();
    }

    private void drawBoard() {
        for (int i = 0; i < BOARD_SIZE_IN_SQUARES; ++i) {
            int visualRow = isBlackPerspective ? i : (BOARD_SIZE_IN_SQUARES - 1 - i);
            int actualRow = i;
            int rowLabel = isBlackPerspective ? (i+1): (BOARD_SIZE_IN_SQUARES-i);
            out.print(" " + rowLabel + " ");
            drawRowOfSquares(actualRow,visualRow);
            out.print(RESET_BG_COLOR);
            out.print(RESET_TEXT_COLOR);
            out.print(" " + rowLabel);
            out.println();
        }
    }
    private void drawRowOfSquares(int actualRow, int visualRow) {
        for (int i = 0; i < BOARD_SIZE_IN_SQUARES; ++i) {
            int visualCol = isBlackPerspective ? (BOARD_SIZE_IN_SQUARES - 1 - i) : i;
            int actualCol = i;

            boolean isLight = (visualRow + visualCol) % 2 == 1; // visual position controls square color
            String piece = INITIAL_BOARD[actualRow][actualCol];
            drawSquare(out, piece, isLight);
        }
    }

    private void drawSquare(PrintStream out, String piece, boolean isLight) {
        out.print(isLight ? SET_BG_COLOR_WHITE : SET_BG_COLOR_BLACK);
        if (piece.equals(EMPTY)) {
            out.print("   ");
        } else {
            String color =switch(piece){
                case WHITE_PAWN, WHITE_BISHOP, WHITE_KNIGHT, WHITE_KING, WHITE_ROOK, WHITE_QUEEN -> SET_TEXT_COLOR_RED;
                default -> SET_TEXT_COLOR_BLUE;
            };
            out.print(color + piece);
        }
        resetColor();
    }
    private void resetColor(){
        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
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


