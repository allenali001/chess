package ui;

import static ui.EscapeSequences.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class GamePlayRepl {

    private final PrintStream out;
    private final int gameID;
    private final String role;
    private final boolean isBlackPerspective;

    private static final int BOARD_SIZE_IN_SQUARES = 8;

    public GamePlayRepl(Object client, int gameID, String role){
        this.out=new PrintStream(System.out, true, StandardCharsets.UTF_8);
        this.gameID=gameID;
        this.role=role;
        this.isBlackPerspective="BLACK".equalsIgnoreCase(role);
    }

    public void run() {
        out.print(ERASE_SCREEN);
        drawBoard();
        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
    }

    private void drawHeaders() {
        out.print(SET_TEXT_COLOR_WHITE);
        out.print("   ");
        for (int i = 0; i < BOARD_SIZE_IN_SQUARES; i++) {
            char label = (char)('a'+(isBlackPerspective ? (BOARD_SIZE_IN_SQUARES-1-i):i));
            out.print(SET_TEXT_COLOR_WHITE + " " + label + " ");
        }
        out.println();
    }

    private void drawBoard() {
        drawHeaders();
        for (int row = 0; row < BOARD_SIZE_IN_SQUARES; row++) {
            int boardRow = isBlackPerspective ? row : (BOARD_SIZE_IN_SQUARES - 1 - row);
            int displayRowLabel = isBlackPerspective ? (row + 1) : (BOARD_SIZE_IN_SQUARES - row);
            out.print(SET_TEXT_COLOR_WHITE + " " + displayRowLabel + " ");
            for (int col = 0; col < BOARD_SIZE_IN_SQUARES; col++) {
                int boardCol = isBlackPerspective ? (BOARD_SIZE_IN_SQUARES - 1 - col) : col;
                boolean isLight = (row + col) % 2 == 0;
                String bgColor = isLight ? SET_BG_COLOR_WHITE: SET_BG_COLOR_BLACK;
                String piece = INITIAL_BOARD[boardRow][boardCol];
                String coloredPiece = colorizePiece(row, piece);
                out.print(bgColor + coloredPiece);
            }
            out.println(RESET_BG_COLOR + SET_TEXT_COLOR_WHITE + " " + displayRowLabel);
        }
        drawHeaders();
    }
    private String colorizePiece(int rowInd, String piece) {
        if (piece.equals(EMPTY)){
            return RESET_TEXT_COLOR + piece;
        }
        if (!isBlackPerspective) {
            if (rowInd >= 6) {
                return SET_TEXT_COLOR_RED + piece.toUpperCase();
            } else if (rowInd <= 1) {
                return SET_TEXT_COLOR_BLUE + piece.toLowerCase();
            }
        } else {
            if (rowInd <= 1) {
                return SET_TEXT_COLOR_RED + piece.toUpperCase();
            } else if (rowInd >= 6) {
                return SET_TEXT_COLOR_BLUE + piece.toLowerCase();
            }
        }
        return RESET_TEXT_COLOR + piece;
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