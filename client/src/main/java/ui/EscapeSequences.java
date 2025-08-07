package ui;

/**
 * This class contains constants and functions relating to ANSI Escape Sequences that are useful in the Client display
 */
public class EscapeSequences {

    private static final String UNICODE_ESCAPE = "\u001b";

    public static final String ERASE_SCREEN = UNICODE_ESCAPE + "[H" + UNICODE_ESCAPE + "[2J";
    private static final String SET_TEXT_COLOR = UNICODE_ESCAPE + "[38;5;";
    private static final String SET_BG_COLOR = UNICODE_ESCAPE + "[48;5;";
    public static final String SET_TEXT_COLOR_RED = SET_TEXT_COLOR + "160m";
    public static final String SET_TEXT_COLOR_BLUE = SET_TEXT_COLOR + "12m";
    public static final String SET_TEXT_COLOR_WHITE = SET_TEXT_COLOR + "15m";
    public static final String RESET_TEXT_COLOR = UNICODE_ESCAPE + "[39m";

    public static final String SET_BG_COLOR_BLACK = SET_BG_COLOR + "0m";
     public static final String SET_BG_COLOR_GREEN = SET_BG_COLOR + "46m";    public static final String SET_BG_COLOR_YELLOW = SET_BG_COLOR + "226m";
    public static final String SET_BG_COLOR_WHITE = SET_BG_COLOR + "15m";
    public static final String RESET_BG_COLOR = UNICODE_ESCAPE + "[49m";

        public static final String WHITE_KING = " K ";
        public static final String WHITE_QUEEN = " Q ";
        public static final String WHITE_BISHOP = " B ";
        public static final String WHITE_KNIGHT = " N ";
        public static final String WHITE_ROOK = " R ";
        public static final String WHITE_PAWN = " P ";
        public static final String BLACK_KING = " k ";
        public static final String BLACK_QUEEN = " q ";
        public static final String BLACK_BISHOP = " b ";
        public static final String BLACK_KNIGHT = " n ";
        public static final String BLACK_ROOK = " r ";
        public static final String BLACK_PAWN = " p ";
        public static final String EMPTY = "   ";

    public static String moveCursorToLocation(int x, int y) { return UNICODE_ESCAPE + "[" + y + ";" + x + "H"; }
}
