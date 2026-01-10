package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);

        if (piece.getPieceType() == PieceType.BISHOP) { // code for how a bishop should move
            ArrayList<ChessMove> newPieceMoves = new ArrayList<ChessMove>();
            int upRow = myPosition.getRow() + 1;
            int rightCol = myPosition.getColumn() + 1;

            while (upRow <= 8 && rightCol <= 8) { // bishop moves up-right
                ChessPosition end = new ChessPosition(upRow, rightCol);
                ChessMove newMove = new ChessMove(myPosition, end, null);
                newPieceMoves.add(newMove);
                upRow++;
                rightCol++;
            }

            upRow = myPosition.getRow() + 1;
            int leftCol = myPosition.getColumn() - 1;

            while (upRow <= 8 && leftCol > 0) { // bishop moves up-left
                ChessPosition end = new ChessPosition(upRow, leftCol);
                ChessMove newMove = new ChessMove(myPosition, end, null);
                newPieceMoves.add(newMove);
                upRow++;
                leftCol--;
            }

            int downRow = myPosition.getRow() - 1;
            rightCol = myPosition.getColumn() + 1;

            while (downRow > 0 && rightCol <= 8) { // bishop move down-right
                ChessPosition end = new ChessPosition(downRow, rightCol);
                ChessMove newMove = new ChessMove(myPosition, end, null);
                newPieceMoves.add(newMove);
                downRow--;
                rightCol++;
            }

            downRow = myPosition.getRow() - 1;
            leftCol = myPosition.getColumn() - 1;

            while (downRow > 0 && leftCol > 0) { // bishop move down-left
                ChessPosition end = new ChessPosition(downRow, leftCol);
                ChessMove newMove = new ChessMove(myPosition, end, null);
                newPieceMoves.add(newMove);
                downRow--;
                leftCol--;
            }

            return newPieceMoves;

        } else if (piece.getPieceType() == PieceType.KING) { // code for how a king moves
            return kingMoves(myPosition);
        }

        return new ArrayList<ChessMove>();
    }

    private ArrayList<ChessMove> kingMoves(ChessPosition myPosition){
        ArrayList<ChessMove> myKingMoves = new ArrayList<ChessMove>();

        if ((myPosition.getRow()+1) <= 8) { // king move up
            ChessPosition kingUp = new ChessPosition((myPosition.getRow() + 1), myPosition.getColumn());
            ChessMove kingMoveUp = new ChessMove(myPosition, kingUp, null);
            myKingMoves.add(kingMoveUp);
            if ((myPosition.getColumn()+1) <= 8) { //king move up right
                ChessPosition kingUpRight = new ChessPosition((myPosition.getRow()+1), (myPosition.getColumn()+1));
                ChessMove kingMoveUpRight = new ChessMove(myPosition, kingUpRight, null);
                myKingMoves.add(kingMoveUpRight);
            }
            if ((myPosition.getColumn()-1) > 0) { // king move up left
                ChessPosition kingUpLeft = new ChessPosition((myPosition.getRow()+1),(myPosition.getColumn()-1));
                ChessMove kingMoveUpLeft = new ChessMove(myPosition, kingUpLeft, null);
                myKingMoves.add(kingMoveUpLeft);
            }
        }

        if ((myPosition.getRow()-1) > 0) { // king move down
            ChessPosition kingDown = new ChessPosition((myPosition.getRow() - 1), myPosition.getColumn());
            ChessMove kingMoveDown = new ChessMove(myPosition, kingDown, null);
            myKingMoves.add(kingMoveDown);
            if ((myPosition.getColumn()+1) <= 8) { // king move down right
                ChessPosition kingDownRight = new ChessPosition((myPosition.getRow()-1), (myPosition.getColumn()+1));
                ChessMove kingMoveDownRight = new ChessMove(myPosition, kingDownRight, null);
                myKingMoves.add(kingMoveDownRight);
            }
            if ((myPosition.getColumn()-1) > 0) { // king move down left
                ChessPosition kingDownLeft = new ChessPosition((myPosition.getRow()-1), (myPosition.getColumn()-1));
                ChessMove kingMoveDownLeft = new ChessMove(myPosition, kingDownLeft, null);
                myKingMoves.add(kingMoveDownLeft);
            }
        }

        if ((myPosition.getColumn()+1) <= 8) { // king move right
            ChessPosition kingRight = new ChessPosition(myPosition.getRow(), (myPosition.getColumn()+1));
            ChessMove kingMoveRight = new ChessMove(myPosition, kingRight, null);
            myKingMoves.add(kingMoveRight);
        }

        if ((myPosition.getColumn()-1) > 0) { // king move left
            ChessPosition kingLeft = new ChessPosition(myPosition.getRow(), (myPosition.getColumn()-1));
            ChessMove kingMoveLeft = new ChessMove(myPosition, kingLeft, null);
            myKingMoves.add(kingMoveLeft);
        }

        return myKingMoves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
