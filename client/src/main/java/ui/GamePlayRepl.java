package ui;

import chess.*;
import exception.ResponseException;
import ui.websocket.WebSocketFacade;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.ResignCommand;

import static ui.EscapeSequences.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

public class GamePlayRepl {
    private final ChessClient client;
    private final PrintStream out;
    private final String authToken;
    private final WebSocketFacade ws;
    private final String username;
    private final int gameID;
    private final boolean isBlackPerspective;
    private ChessPosition highlightedPiece = null;
    private Collection<ChessMove> highlightedMoves = List.of();

    private static final int BOARD_SIZE_IN_SQUARES = 8;

    public GamePlayRepl(ChessClient client, int gameID, String role, String authToken, String username, WebSocketFacade ws) {
        this.out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        this.gameID = gameID;
        this.isBlackPerspective = "BLACK".equalsIgnoreCase(role);
        this.client = client;
        this.username = username;
        this.authToken = authToken;
        this.ws = ws;
    }

    public void run() {
        ws.send(new ConnectCommand(authToken, gameID));
        out.print(ERASE_SCREEN);
        drawBoard();
        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Entered gameplay/observe mode. Type 'help' for options.");
        while (true) {
            printPrompt();
            String line = scanner.nextLine().trim();
            var tokens = line.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            try {
                switch (cmd) {
                    case "help" -> System.out.println(help());
                    case "redraw" -> doRedraw();
                    case "leave" -> doLeave();
                    case "makemove" -> doMakeMove(params);
                    case "resign" -> doResign();
                    case "highlight" -> doHighlight();
                    default -> System.out.print("Command not recognized. " +
                            "Check spelling and try again, or type 'help' for options.");
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public String help() {

        return """
                - redraw - chess board
                - leave - chess game
                - makemove <STARTPOSITION> <ENDPOSITION> - move chess pieces
                - highlight - possible moves for piece
                - resign - from game
                - help - with possible commands
                """;
    }

    private void doRedraw() {
        out.print(ERASE_SCREEN);
        drawBoard();
        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
    }

    private void doLeave() {
        try {
            ws.send(new LeaveCommand(authToken, gameID));
            System.out.println("You have successfully left the game");
        } catch (Exception ex) {
            System.out.println("Error while leaving game" + ex.getMessage());
        }
        client.transitionToPostLogin(authToken, username);
    }


    private void doMakeMove(String[] params) throws ResponseException {
        if (params.length >= 2) {
            ChessPosition startPos = getPosition(params[0]);
            ChessPosition endPos = getPosition(params[1]);
            ChessMove move = new ChessMove(startPos, endPos, null);
            ws.send(new MakeMoveCommand(authToken, gameID, move));
        } else {
            throw new ResponseException(400, "Must enter starting position and end position \n Example: a2 a3");
        }
    }

    private ChessPosition getPosition(String input) {
        input = input.toLowerCase();
        if (!input.matches("[a-h][1-8]")) {
            throw new IllegalArgumentException("Position entered is invalid. \nTry entering a different position.");
        }
        int col = input.charAt(0) - 'a' + 1;
        int row = Character.getNumericValue(input.charAt(1));
        return new ChessPosition(row, col);
    }

    private void doResign() throws ResponseException {
        System.out.println("Are you sure you want to resign?\n " +
                "Reply 'Y' for 'Yes', 'N' for 'No'");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine().trim();
        var res = input.toUpperCase();
        if (res.equals("Y")) {
            ws.send(new ResignCommand(authToken, gameID));
            System.out.println("You have successfully resigned from the game");
        } else if (res.equals("N")) {
            System.out.println("Cancelled resignation request");
        } else {
            throw new ResponseException(400, "Please enter 'Y' to resign, or 'N' to cancel request");
        }
    }

    private void doHighlight() {
        System.out.println("Enter the position of the piece you'd like to view.\n Legal moves will be highlighted");
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine().trim();
        try {
            ChessPosition chessPosition = getPosition(line);
            ChessGame game = client.getServer().getGame(gameID);
            ChessBoard board = game.getBoard();
            ChessPiece piece = board.getPiece(chessPosition);
            if (piece == null) {
                System.out.println("The position you have entered does not have a piece on it.\n Check coordinates and try again.");
                return;
            }
            this.highlightedPiece = chessPosition;
            this.highlightedMoves = piece.pieceMoves(board, chessPosition);
            doRedraw();
        } catch (IllegalArgumentException ex) {
            System.out.println("Input not recognized. Make sure valid coordinate entered.");
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    private void drawHeaders() {
        out.print(SET_TEXT_COLOR_WHITE);
        out.print("   ");
        for (int i = 0; i < BOARD_SIZE_IN_SQUARES; i++) {
            char label = (char) ('a' + (isBlackPerspective ? (BOARD_SIZE_IN_SQUARES - 1 - i) : i));
            out.print(SET_TEXT_COLOR_WHITE + " " + label + " ");
        }
        out.println();
    }

    public void printPrompt() {
        System.out.print("\n" + "[GAMEPLAY] >>> ");
    }


    private void drawBoard() {
        ChessGame game;
        try {
            game = client.getServer().getGame(gameID);
            if (game == null) {
                out.println("Try redraw");
                return;
            }
        } catch (ResponseException ex) {
        out.println("Couldn't load game.");
        out.println("Details: " + ex.getMessage());
        return;
    }

        ChessBoard board = game.getBoard();
        drawHeaders();
        for (int row = 0; row < BOARD_SIZE_IN_SQUARES; row++) {
            int boardRow = isBlackPerspective ? row : (BOARD_SIZE_IN_SQUARES - 1 - row);
            int displayRowLabel = isBlackPerspective ? (row + 1) : (BOARD_SIZE_IN_SQUARES - row);
            out.print(SET_TEXT_COLOR_WHITE + " " + displayRowLabel + " ");
            for (int col = 0; col < BOARD_SIZE_IN_SQUARES; col++) {
                int boardCol = isBlackPerspective ? (BOARD_SIZE_IN_SQUARES - 1 - col) : col;
                boolean isLight = (row + col) % 2 == 0;
                int actualRow = displayRowLabel;
                int actualCol = boardCol + 1;
                ChessPosition chessPosition = new ChessPosition(actualRow, actualCol);
                String bgColor;
                if (highlightedPiece != null && highlightedPiece.equals(chessPosition)) {
                    bgColor = SET_BG_COLOR_YELLOW;
                } else if (highlightedMoves != null && highlightedMoves.stream().anyMatch(m -> m.getEndPosition().equals(chessPosition))) {
                    bgColor = SET_BG_COLOR_GREEN;
                } else {
                    bgColor = isLight ? SET_BG_COLOR_WHITE : SET_BG_COLOR_BLACK;
                }
                ChessPiece piece = board.getPiece(chessPosition);
                String symbol = (piece == null) ? EMPTY : getSymbol(piece);
                String coloredPiece = colorizePiece(row, symbol);
                out.print(bgColor + coloredPiece);
            }
            out.println(RESET_BG_COLOR + SET_TEXT_COLOR_WHITE + " " + displayRowLabel);
        }
        drawHeaders();
    }
    private String getSymbol(ChessPiece piece){
        return switch(piece.getTeamColor()){
            case WHITE -> switch(piece.getPieceType()){
                case KING -> WHITE_KING;
                case QUEEN -> WHITE_QUEEN;
                case ROOK -> WHITE_ROOK;
                case PAWN -> WHITE_PAWN;
                case KNIGHT -> WHITE_KNIGHT;
                case BISHOP -> WHITE_BISHOP;
            };
            case BLACK -> switch (piece.getPieceType()){
                case KING -> BLACK_KING;
                case QUEEN -> BLACK_QUEEN;
                case BISHOP -> BLACK_BISHOP;
                case ROOK -> BLACK_ROOK;
                case KNIGHT -> BLACK_KNIGHT;
                case PAWN -> BLACK_PAWN;
            };
        };
    }

    private String colorizePiece(int rowInd, String piece) {
        if (piece.equals(EMPTY)) {
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
}