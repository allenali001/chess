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
            int rowLabel = isBlackPerspective ? (i + 1) : (BOARD_SIZE_IN_SQUARES - i);
            out.print(" " + rowLabel + " ");
            drawRowOfSquares(visualRow);
            out.print(RESET_BG_COLOR);
            out.print(RESET_TEXT_COLOR);
            out.print(" " + rowLabel);
            out.println();
        }
    }
    private void drawRowOfSquares(int visualRow) {
        boolean isBottomRow = (isBlackPerspective && visualRow == 0) || (!isBlackPerspective && visualRow == BOARD_SIZE_IN_SQUARES - 1);

        for (int visualCol = 0; visualCol < BOARD_SIZE_IN_SQUARES; ++visualCol) {
            int whitePerspectiveRow = isBlackPerspective ? (BOARD_SIZE_IN_SQUARES - 1 - visualRow) : visualRow;
            boolean isLight = (whitePerspectiveRow + visualCol) % 2 == 1;

            int actualRow = isBlackPerspective ? (BOARD_SIZE_IN_SQUARES - 1 - visualRow) : visualRow;
            int actualCol = isBlackPerspective ? (BOARD_SIZE_IN_SQUARES - 1 - visualCol) : visualCol;

            String piece = INITIAL_BOARD[actualRow][actualCol];
            drawSquare(out, piece, isLight, isBottomRow);
        }
    }
    private void drawSquare(PrintStream out, String piece, boolean isLight, boolean isBottomRow) {
        out.print(isLight ? SET_BG_COLOR_WHITE : SET_BG_COLOR_BLACK);

        if (piece.equals(EMPTY)) {
            out.print("   ");
        } else {
            boolean isWhitePiece = Character.isUpperCase(piece.charAt(1));
            String color = isWhitePiece
                    ? (isBlackPerspective ? SET_TEXT_COLOR_BLUE : SET_TEXT_COLOR_RED)
                    : (isBlackPerspective ? SET_TEXT_COLOR_RED : SET_TEXT_COLOR_BLUE);
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


