package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    public TeamColor teamTurn = TeamColor.WHITE;
    public ChessBoard gameBoard = new ChessBoard();

    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
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
        ChessBoard board = getBoard();
        ChessPiece piece = board.getPiece(startPosition);
        TeamColor color = piece.getTeamColor();

        ArrayList<ChessMove> valid = new ArrayList<>();
        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
        for (ChessMove move : moves) {
            ChessPosition end = move.getEndPosition();
            ChessPosition checkEnd = new ChessPosition((end.getRow()-1), (end.getColumn()-1));
            boolean capture = false;
            ChessPiece capturePiece = null;
            if (board.checkPiece(checkEnd)) {
                capturePiece = board.getPiece(end);
                capture = true;
            }
            board.addPiece(end, piece);
            board.removePiece(startPosition);
            if (isInCheck(color)) {
                board.addPiece(startPosition, piece);
                board.removePiece(end);
                if (capture) {
                    board.addPiece(end, capturePiece);
                }
            } else {
                board.addPiece(startPosition, piece);
                board.removePiece(end);
                valid.add(move);
                if (capture) {
                    board.addPiece(end, capturePiece);
                }
            }
        }
        return valid;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        Collection<ChessMove> moves = validMoves(start);
        ChessBoard board = getBoard();
        ChessPiece piece = board.getPiece(start);
        ChessPosition end = move.getEndPosition();

        if (moves.contains(move)) {
            board.addPiece(end, piece);
            board.removePiece(start);
        } else {
            throw new InvalidMoveException("Invalid move");
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessBoard board = getBoard();
        ChessPosition kingPos = board.findPiece(ChessPiece.PieceType.KING, teamColor, board);

        for (int i=1; i<=8; i++) {
            for (int j=1; j<=8; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                ChessPosition checkPos = new ChessPosition((i-1), (j-1));
                if (board.checkPiece(checkPos)) {
                    ChessPiece piece = board.getPiece(pos);
                    if ((piece.getTeamColor() != teamColor)&&(piece.getPieceType() != ChessPiece.PieceType.PAWN)) {
                        Collection<ChessMove> moves = piece.pieceMoves(board, pos);
                        ChessMove check = new ChessMove(pos, kingPos, null);
                        if (moves.contains(check)) {
                            return true;
                        }
                    } else if (piece.getTeamColor()!=teamColor) {
                        ArrayList<ChessMove> pawns = addPawns(pos, kingPos);
                        Collection<ChessMove> moves = piece.pieceMoves(board, pos);
                        for (ChessMove move : pawns) {
                            if (moves.contains(move)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    private ArrayList<ChessMove> addPawns(ChessPosition start, ChessPosition end) {
        ArrayList<ChessMove> pawns = new ArrayList<>();
        ChessMove pawnRook = new ChessMove(start, end, ChessPiece.PieceType.ROOK);
        ChessMove pawnQueen = new ChessMove(start, end, ChessPiece.PieceType.QUEEN);
        ChessMove pawnBishop = new ChessMove(start, end, ChessPiece.PieceType.BISHOP);
        ChessMove pawnKnight = new ChessMove(start, end, ChessPiece.PieceType.KNIGHT);
        ChessMove pawnNull = new ChessMove(start, end, null);
        pawns.add(pawnRook);
        pawns.add(pawnKnight);
        pawns.add(pawnBishop);
        pawns.add(pawnQueen);
        pawns.add(pawnNull);
        return pawns;
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
        gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
