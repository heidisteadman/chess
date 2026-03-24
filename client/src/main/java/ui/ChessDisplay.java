package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class ChessDisplay {

    // board dimensions
    private static final int BOARD_WIDTH = 8;
    private static final int BOARD_HEIGHT = 8;
    private static final int SQUARE_SIZE_PADDED = 1;

    // characters
    private static final String EMPTY = EscapeSequences.EMPTY;
    public static final String WHITE_KING = EscapeSequences.WHITE_KING;
    public static final String WHITE_QUEEN = EscapeSequences.WHITE_QUEEN;
    public static final String WHITE_BISHOP = EscapeSequences.WHITE_BISHOP;
    public static final String WHITE_KNIGHT = EscapeSequences.WHITE_KNIGHT;
    public static final String WHITE_ROOK = EscapeSequences.WHITE_ROOK;
    public static final String WHITE_PAWN = EscapeSequences.WHITE_PAWN;
    public static final String BLACK_KING = EscapeSequences.BLACK_KING;
    public static final String BLACK_QUEEN = EscapeSequences.BLACK_QUEEN;
    public static final String BLACK_BISHOP = EscapeSequences.BLACK_BISHOP;
    public static final String BLACK_KNIGHT = EscapeSequences.BLACK_KNIGHT;
    public static final String BLACK_ROOK = EscapeSequences.BLACK_ROOK;
    public static final String BLACK_PAWN = EscapeSequences.BLACK_PAWN;

    private final ChessBoard board;

    public ChessDisplay(ChessBoard board) {
        this.board = board;
    }

    public void displayBoard(ChessGame.TeamColor color) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(EscapeSequences.ERASE_SCREEN);

        if (color == ChessGame.TeamColor.BLACK) {
            drawHeadersBlack(out);
            drawBoard(out, color);
            drawHeadersBlack(out);

            out.print(SET_BG_COLOR_BLACK);
            out.print(SET_TEXT_COLOR_WHITE);
        } else {
            drawHeadersWhite(out);
            drawBoard(out, color);
            drawHeadersWhite(out);
        }
    }


    private void drawBoard(PrintStream out, ChessGame.TeamColor color) {
        if (color == ChessGame.TeamColor.BLACK) {
            for (int boardRow=0; boardRow<BOARD_HEIGHT; ++boardRow) {
                drawRowSquares(out, boardRow);
                if (boardRow<BOARD_HEIGHT-1) {
                    setBlack(out);
                }
            }
        } else {
            for (int boardRow=BOARD_HEIGHT-1; boardRow>=0; --boardRow) {
                drawRowSquares(out, boardRow);
                if (boardRow<BOARD_HEIGHT-1) {
                    setBlack(out);
                }
            }
        }
    }


    private void drawRowSquares(PrintStream out, int colNum) {
        for (int squareRow=0; squareRow<SQUARE_SIZE_PADDED; ++squareRow) {
            for (int boardCol=0; boardCol<BOARD_HEIGHT; ++boardCol) {
                String backgroundColor = SET_BG_COLOR_BLACK;
                int sideHeader = colNum;
                if (boardCol == 0) {
                    out.print(backgroundColor + SET_TEXT_COLOR_GREEN + ++sideHeader);
                }

                if ((boardCol % 2 == 0) && (colNum%2 == 0)) {
                    setGray(out);
                    backgroundColor = SET_BG_COLOR_LIGHT_GREY;
                } else if ((boardCol % 2 != 0) && (colNum %2 != 0)){
                    setGray(out);
                    backgroundColor = SET_BG_COLOR_LIGHT_GREY;
                } else {
                    setWhite(out);
                    backgroundColor = SET_BG_COLOR_WHITE;
                }

                int prefixLength = SQUARE_SIZE_PADDED / 2;
                int suffixLength = 0;

                out.print(EMPTY.repeat(prefixLength));
                ChessPosition square = new ChessPosition(colNum+1, boardCol+1);
                ChessPiece piece = board.getPiece(square);
                String symbol = EMPTY;

                if (piece != null) {
                    if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                        symbol = getWhitePiece(piece);
                    } else {
                        symbol = getBlackPiece(piece);
                    }
                }

                printPlayer(out, symbol, backgroundColor);
                out.print(backgroundColor);
                out.print(EMPTY.repeat(suffixLength));

                setBlack(out);
                if (boardCol == 7) {
                    out.print(SET_TEXT_COLOR_GREEN + ++sideHeader);
                }
            }
            out.println();
        }
    }

    private String getWhitePiece(ChessPiece piece) {
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            return WHITE_KING;
        } else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
            return WHITE_QUEEN;
        } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            return WHITE_ROOK;
        } else if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            return WHITE_BISHOP;
        } else if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            return WHITE_PAWN;
        } else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            return WHITE_KNIGHT;
        }

        return null;
    }

    private String getBlackPiece(ChessPiece piece) {
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            return BLACK_KING;
        } else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
            return BLACK_QUEEN;
        } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            return BLACK_ROOK;
        } else if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            return BLACK_BISHOP;
        } else if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            return BLACK_PAWN;
        } else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            return BLACK_KNIGHT;
        }

        return null;
    }

    private void drawHeadersBlack(PrintStream out) {
        setBlack(out);
        out.print("0");
        String[] headers = {" H ", "  G ", " F ", "  E ", "  D ", " C ", "  B ", " A "};
        for (int boardCol=0; boardCol<BOARD_WIDTH; ++boardCol) {
            printPlayer(out, headers[boardCol], SET_BG_COLOR_BLACK);

        }

        out.println();

    }

    private void drawHeadersWhite(PrintStream out) {
        setBlack(out);
        out.print("0");

        String[] headers = {" A ", "  B ", " C ", "  D ", "  E ", " F ", "  G ", " H "};
        for (int boardCol=0; boardCol<BOARD_WIDTH; ++boardCol) {
            printPlayer(out, headers[boardCol], SET_BG_COLOR_BLACK);

        }

        out.println();
    }


    private void printPlayer(PrintStream out, String player, String bgColor) {
        out.print(bgColor);
        out.print(SET_TEXT_COLOR_GREEN);

        out.print(player);

        setBlack(out);
    }

    private void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
    }

    private void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }


    private void setGray(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_LIGHT_GREY);
    }
}
