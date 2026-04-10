package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }
    public PieceType getPieceType() {
        return type;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);

        if (piece.getPieceType() == PieceType.BISHOP) { // code for how a bishop should move
            return bishopMoves(board, myPosition, piece);

        } else if (piece.getPieceType() == PieceType.KING) { // code for how a king moves
            return kingMoves(board, myPosition, piece);
        } else if (piece.getPieceType() == PieceType.KNIGHT) {
            return knightMoves(board, myPosition, piece);
        } else if (piece.getPieceType() == PieceType.PAWN) {
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                return whitePawnMoves(board, myPosition, piece);
            } else {
                return blackPawnMoves(board, myPosition, piece);
            }
        } else if (piece.getPieceType() == PieceType.QUEEN) {
            ArrayList<ChessMove> queenDiagonal = bishopMoves(board, myPosition, piece);
            queenDiagonal.addAll(rookMoves(board, myPosition, piece));
            return queenDiagonal;
        } else {
            return rookMoves(board, myPosition, piece);
        }
    }

    private ChessMove moveHelper(ChessBoard board, ChessPiece piece, ChessPosition myPosition, int row, int col) {
        ChessPosition end = new ChessPosition(row, col);
        ChessPosition checkPos = new ChessPosition((row-1), (col-1));
        if (board.checkPiece(checkPos)) {
            ChessPiece nextPiece = board.getPiece(end);
            if (nextPiece.getTeamColor() != piece.getTeamColor()) {
                return new ChessMove(myPosition, end, null);
            } else {
                return null;
            }
        }
        return new ChessMove(myPosition, end, null);
    }

    private ArrayList<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        ArrayList<ChessMove> newPieceMoves = new ArrayList<>();

        int upRow = myPosition.getRow() + 1;
        int rightCol = myPosition.getColumn() + 1;
        while (upRow <= 8 && rightCol <= 8) { // bishop moves up-right
            ChessMove newMove = moveHelper(board, piece, myPosition, upRow, rightCol);
            if (board.checkPiece(new ChessPosition((upRow - 1), (rightCol - 1)))) {
                if (newMove != null) {
                    newPieceMoves.add(newMove);
                }
                upRow = 9;
            } else {
                if (newMove != null) {
                    newPieceMoves.add(newMove);
                    upRow++;
                    rightCol++;
                }
            }
        }
        upRow = myPosition.getRow() + 1;
        int leftCol = myPosition.getColumn() - 1;
        while (upRow <= 8 && leftCol > 0) { // bishop moves up-left
            ChessMove newMove = moveHelper(board, piece, myPosition, upRow, leftCol);
            if (board.checkPiece(new ChessPosition((upRow-1), (leftCol-1)))) {
                if (newMove != null) {
                    newPieceMoves.add(newMove);
                }
                upRow = 9;
            } else {
                if (newMove != null) {
                    newPieceMoves.add(newMove);
                    upRow++;
                    leftCol--;
                }
            }
        }
        int downRow = myPosition.getRow() - 1;
        rightCol = myPosition.getColumn() + 1;
        while (downRow > 0 && rightCol <= 8) { // bishop move down-right
            ChessMove newMove = moveHelper(board, piece, myPosition, downRow, rightCol);
            if (board.checkPiece(new ChessPosition((downRow-1), (rightCol-1)))) {
                if (newMove != null) {
                    newPieceMoves.add(newMove);
                }
                downRow = 0;
            } else {
                newPieceMoves.add(newMove);
                downRow--;
                rightCol++;
            }
        }
        downRow = myPosition.getRow() - 1;
        leftCol = myPosition.getColumn() - 1;
        while (downRow > 0 && leftCol > 0) { // bishop move down-left
            ChessMove newMove = moveHelper(board, piece, myPosition, downRow, leftCol);
            if (board.checkPiece(new ChessPosition((downRow-1), (leftCol-1)))) {
                if (newMove != null) {
                    newPieceMoves.add(newMove);
                }
                downRow=0;
            } else {
                newPieceMoves.add(newMove);
                downRow--;
                leftCol--;
            }
        }
        return newPieceMoves;
    }

    private ArrayList<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece){
        ArrayList<ChessMove> myKingMoves = new ArrayList<>();

        if ((myPosition.getRow()+1) <= 8) { // king move up
            ChessMove kingMoveUp = moveHelper(board, piece, myPosition, (myPosition.getRow()+1), myPosition.getColumn());
            if (board.checkPiece(new ChessPosition((myPosition.getRow()), (myPosition.getColumn()-1)))) {
                if (kingMoveUp != null) {
                    myKingMoves.add(kingMoveUp);
                }
            } else {
                myKingMoves.add(kingMoveUp);
            }
            if ((myPosition.getColumn()+1) <= 8) {//king move up right
                ChessMove kingMoveUpRight = moveHelper(board, piece, myPosition, (myPosition.getRow()+1), (myPosition.getColumn()+1));
                if (board.checkPiece(new ChessPosition(myPosition.getRow(), myPosition.getColumn()))) {
                    if (kingMoveUpRight != null) {
                        myKingMoves.add(kingMoveUpRight);
                    }
                }  else {
                    myKingMoves.add(kingMoveUpRight);
                }
            }
            if ((myPosition.getColumn()-1) > 0) { // king move up left
                ChessMove kingMoveUpLeft = moveHelper(board, piece, myPosition, (myPosition.getRow()+1), (myPosition.getColumn()-1));
                if (board.checkPiece(new ChessPosition(myPosition.getRow(), (myPosition.getColumn()-2)))) {
                    if (kingMoveUpLeft != null) {
                        myKingMoves.add(kingMoveUpLeft);
                    }
                } else {
                    myKingMoves.add(kingMoveUpLeft);
                }
            }
        }
        if ((myPosition.getRow()-1) > 0) { // king move down
            ChessMove kingMoveDown = moveHelper(board, piece, myPosition, (myPosition.getRow()-1), (myPosition.getColumn()));
            if (board.checkPiece(new ChessPosition((myPosition.getRow()-2), myPosition.getColumn()-1))) {
                if (kingMoveDown != null) {
                    myKingMoves.add(kingMoveDown);
                }
            } else {
                myKingMoves.add(kingMoveDown);
            }
            if ((myPosition.getColumn()+1) <= 8) { // king move down right
                ChessMove kingMoveDownRight = moveHelper(board, piece, myPosition, (myPosition.getRow()-1), (myPosition.getColumn()+1));
                if (board.checkPiece(new ChessPosition((myPosition.getRow()-2), myPosition.getColumn()))) {
                    if (kingMoveDownRight != null) {
                        myKingMoves.add(kingMoveDownRight);
                    }
                } else {
                    myKingMoves.add(kingMoveDownRight);
                }
            }
            if ((myPosition.getColumn()-1) > 0) { // king move down left
                ChessMove kingMoveDownLeft = moveHelper(board, piece, myPosition, (myPosition.getRow()-1), (myPosition.getColumn()-1));
                if (board.checkPiece(new ChessPosition((myPosition.getRow()-2), (myPosition.getColumn()-2)))) {
                    if (kingMoveDownLeft != null) {
                        myKingMoves.add(kingMoveDownLeft);
                    }
                } else {
                    myKingMoves.add(kingMoveDownLeft);
                }
            }
        }
        if ((myPosition.getColumn()+1) <= 8) {// king move right
            ChessMove kingMoveRight = moveHelper(board, piece, myPosition, myPosition.getRow(), (myPosition.getColumn()+1));
            if (board.checkPiece(new ChessPosition((myPosition.getRow()-1), myPosition.getColumn()))) {
                if (kingMoveRight != null) {
                    myKingMoves.add(kingMoveRight);
                }
            } else {
                myKingMoves.add(kingMoveRight);
            }
        }
        if ((myPosition.getColumn()-1) > 0) { // king move left
            ChessMove kingMoveLeft = moveHelper(board, piece, myPosition, (myPosition.getRow()), (myPosition.getColumn()-1));
            if (board.checkPiece(new ChessPosition((myPosition.getRow()-1), (myPosition.getColumn()-2)))) {
                if (kingMoveLeft != null) {
                    myKingMoves.add(kingMoveLeft);
                }
            } else {
                myKingMoves.add(kingMoveLeft);
            }
        }
        return myKingMoves;
    }

    private ArrayList<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        ArrayList<ChessMove> myKnightMoves = new ArrayList<>();

        if (myPosition.getRow()+2 <= 8) { // move up
            if (myPosition.getColumn()+1 <=8) { // move up right
                ChessMove knightMoveUpRight = moveHelper(board, piece, myPosition, (myPosition.getRow()+2), (myPosition.getColumn()+1));
                if (board.checkPiece(new ChessPosition((myPosition.getRow()+1), myPosition.getColumn()))) {
                    if (knightMoveUpRight != null) {
                        myKnightMoves.add(knightMoveUpRight);
                    }
                } else {
                    myKnightMoves.add(knightMoveUpRight);
                }
            }
            if (myPosition.getColumn()-1 >0) { // move up left
                ChessMove knightMoveUpLeft = moveHelper(board, piece, myPosition, (myPosition.getRow()+2), (myPosition.getColumn()-1));
                if (board.checkPiece(new ChessPosition((myPosition.getRow()+1), (myPosition.getColumn()-2)))) {
                    if (knightMoveUpLeft != null) {
                        myKnightMoves.add(knightMoveUpLeft);
                    }
                } else {
                    myKnightMoves.add(knightMoveUpLeft);
                }
            }
        }
        if (myPosition.getRow()-2 > 0) { // move down
            if (myPosition.getColumn() +1 <= 8) { // move down right
                ChessMove knightMoveDownRight = moveHelper(board, piece, myPosition, (myPosition.getRow()-2), (myPosition.getColumn() +1));
                if (board.checkPiece(new ChessPosition((myPosition.getRow()-3), (myPosition.getColumn())))) {
                    if (knightMoveDownRight != null) {
                        myKnightMoves.add(knightMoveDownRight);
                    }
                } else {
                    myKnightMoves.add(knightMoveDownRight);
                }
            }
            if (myPosition.getColumn()-1 >0) { // move down left
                ChessMove knightMoveDownLeft = moveHelper(board, piece, myPosition, (myPosition.getRow()-2), (myPosition.getColumn()-1));
                if (board.checkPiece(new ChessPosition((myPosition.getRow()-3), (myPosition.getColumn()-2)))) {
                    if (knightMoveDownLeft != null) {
                        myKnightMoves.add(knightMoveDownLeft);
                    }
                } else {
                    myKnightMoves.add(knightMoveDownLeft);
                }
            }
        }
        if (myPosition.getColumn()+2 <= 8) { // move right
            if (myPosition.getRow() +1 <=8) { // move right up
                ChessMove knightMoveRightUp = moveHelper(board, piece, myPosition, (myPosition.getRow() +1), (myPosition.getColumn()+2));
                if (board.checkPiece(new ChessPosition((myPosition.getRow()), (myPosition.getColumn()+1)))) {
                    if (knightMoveRightUp != null) {
                        myKnightMoves.add(knightMoveRightUp);
                    }
                } else {
                    myKnightMoves.add(knightMoveRightUp);
                }
            }
            if (myPosition.getRow()-1 >0) { // move right down
                ChessMove knightMoveRightDown = moveHelper(board, piece, myPosition, (myPosition.getRow()-1), (myPosition.getColumn()+2));
                if (board.checkPiece(new ChessPosition((myPosition.getRow()-2), (myPosition.getColumn()+1)))) {
                    if (knightMoveRightDown != null) {
                        myKnightMoves.add(knightMoveRightDown);
                    }
                } else {
                    myKnightMoves.add(knightMoveRightDown);
                }
            }
        }
        if (myPosition.getColumn()-2 >0) { // move left
            if (myPosition.getRow()+1 <=8) { // move left up
                ChessMove knightMoveLeftUp = moveHelper(board, piece, myPosition, (myPosition.getRow()+1), (myPosition.getColumn()-2));
                if (board.checkPiece(new ChessPosition((myPosition.getRow()), (myPosition.getColumn()-3)))) {
                    if (knightMoveLeftUp != null) {
                        myKnightMoves.add(knightMoveLeftUp);
                    }
                } else {
                    myKnightMoves.add(knightMoveLeftUp);
                }
            }
            if (myPosition.getRow()-1 >0) { // move left down
                ChessMove knightMoveLeftDown = moveHelper(board, piece, myPosition, (myPosition.getRow()-1), (myPosition.getColumn()-2));
                if (board.checkPiece(new ChessPosition((myPosition.getRow()-2), (myPosition.getColumn()-3)))) {
                    if (knightMoveLeftDown != null) {
                        myKnightMoves.add(knightMoveLeftDown);
                    }
                } else {
                    myKnightMoves.add(knightMoveLeftDown);
                }
            }
        }
        return myKnightMoves;
    }

    private ArrayList<ChessMove> pawnPromoteHelper(ChessPosition myPosition, ChessPosition move) {
        ArrayList<ChessMove> newMoves = new ArrayList<>();
        ArrayList<PieceType> types = new ArrayList<>();
        Collections.addAll(types, PieceType.BISHOP, PieceType.KNIGHT, PieceType.QUEEN, PieceType.ROOK);
        for (PieceType type : types) {
            newMoves.add(new ChessMove(myPosition, move, type));
        }
        return newMoves;
    }

    private ArrayList<ChessMove> whitePawnMoves (ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        ArrayList<ChessMove> myWhitePawnMoves = new ArrayList<>();

        ChessMove moveForward = moveHelper(board, piece, myPosition, (myPosition.getRow()+1), (myPosition.getColumn()));
        if (myPosition.getRow() == 2) { // check if pawn is in starting position
            if (!board.checkPiece(new ChessPosition((myPosition.getRow()), (myPosition.getColumn()-1)))) { // move forward one
                myWhitePawnMoves.add(moveForward);
                if (!board.checkPiece(new ChessPosition((myPosition.getRow()+1), (myPosition.getColumn()-1)))) { // move forward two
                    ChessMove moveTwoForward = moveHelper(board, piece, myPosition, (myPosition.getRow()+2), myPosition.getColumn());
                    myWhitePawnMoves.add(moveTwoForward);
                }
            }
        } else if (myPosition.getRow() != 7){ // check if pawn promotes moving forward
            if (!board.checkPiece(new ChessPosition((myPosition.getRow()), (myPosition.getColumn()-1)))) { // move one
               myWhitePawnMoves.add(moveForward);
            }
        } else { // piece can be promoted
            if (!board.checkPiece(new ChessPosition((myPosition.getRow()), (myPosition.getColumn()-1)))) {
                myWhitePawnMoves.addAll(pawnPromoteHelper(myPosition, new ChessPosition((myPosition.getRow()+1), (myPosition.getColumn()))));
            }
        }
        if (myPosition.getColumn()-1 > 0) {
            ChessMove moveUpLeft = moveHelper(board, piece, myPosition, (myPosition.getRow()+1), (myPosition.getColumn()-1));
            if (board.checkPiece(new ChessPosition((myPosition.getRow()),(myPosition.getColumn()-2)))) {
                if (moveUpLeft != null) {
                    if (myPosition.getRow() != 7) {
                        myWhitePawnMoves.add(moveUpLeft);
                    } else {
                        myWhitePawnMoves.addAll(pawnPromoteHelper(myPosition,new ChessPosition((myPosition.getRow()+1),(myPosition.getColumn()-1))));
                    }
                }
            }
        }
        if (myPosition.getColumn()+1 <= 8) {
            ChessMove moveUpRight = moveHelper(board, piece, myPosition, (myPosition.getRow()+1), (myPosition.getColumn()+1));
            if (board.checkPiece(new ChessPosition((myPosition.getRow()), (myPosition.getColumn())))) {
                if (moveUpRight != null) {
                    if (myPosition.getRow() != 7) {
                        myWhitePawnMoves.add(moveUpRight);
                    } else {
                        myWhitePawnMoves.addAll(pawnPromoteHelper(myPosition,new ChessPosition((myPosition.getRow()+1),(myPosition.getColumn()+1))));
                    }
                }
            }
        }
        return myWhitePawnMoves;
    }

    private ArrayList<ChessMove> blackPawnMoves (ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        ArrayList<ChessMove> myBlackPawnMoves = new ArrayList<>();

        if (myPosition.getRow() == 7) { // check beginning position
            ChessMove moveForward = moveHelper(board, piece, myPosition, (myPosition.getRow()-1), myPosition.getColumn());
            if (moveForward != null) { // move one forward
                myBlackPawnMoves.add(moveForward);
                if (!board.checkPiece(new ChessPosition((myPosition.getRow()-3), (myPosition.getColumn()-1)))) { // move two forward
                    myBlackPawnMoves.add(moveHelper(board, piece, myPosition, (myPosition.getRow()-2), myPosition.getColumn()));
                }
            }
        } else if (myPosition.getRow() != 2){
            if (!board.checkPiece(new ChessPosition((myPosition.getRow()-2), (myPosition.getColumn()-1)))) { // move one forward if not at start
                myBlackPawnMoves.add(moveHelper(board, piece, myPosition, (myPosition.getRow()-1), myPosition.getColumn()));
            }
        } else {
            myBlackPawnMoves.addAll(pawnPromoteHelper(myPosition, new ChessPosition((myPosition.getRow()-1), myPosition.getColumn())));
        }
        if ((myPosition.getColumn()-1) > 0) {
            if (board.checkPiece(new ChessPosition((myPosition.getRow()-2), (myPosition.getColumn()-2)))) {
                ChessMove moveForwardRight = moveHelper(board, piece, myPosition, (myPosition.getRow()-1), (myPosition.getColumn()-1));
                if (moveForwardRight != null) {
                    if (myPosition.getRow() != 2) {
                        myBlackPawnMoves.add(moveForwardRight);
                    } else {
                        myBlackPawnMoves.addAll(pawnPromoteHelper(myPosition,new ChessPosition((myPosition.getRow()-1),(myPosition.getColumn()-1))));
                    }
                }
            }
        }
        ChessPosition forwardLeft = new ChessPosition((myPosition.getRow()-1), (myPosition.getColumn()+1));
        if (myPosition.getColumn()+1 <= 8) {
            ChessMove moveForwardLeft = moveHelper(board, piece, myPosition, (myPosition.getRow()-1), (myPosition.getColumn()+1));
            if (board.checkPiece(new ChessPosition((myPosition.getRow()-2), (myPosition.getColumn())))) {
                if (moveForwardLeft != null) {
                    if (myPosition.getRow() != 2) {
                        myBlackPawnMoves.add(moveForwardLeft);
                    } else {
                        myBlackPawnMoves.addAll(pawnPromoteHelper(myPosition, forwardLeft));
                    }
                }
            }
        }
        return myBlackPawnMoves;
    }

    ArrayList<ChessMove> rookMoves (ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        ArrayList<ChessMove> myRookMoves = new ArrayList<>();

        int upRow = myPosition.getRow()+1;
        while (upRow <= 8) {// move up
            ChessMove moveUp = moveHelper(board, piece, myPosition, upRow, myPosition.getColumn());
            if (board.checkPiece(new ChessPosition((upRow-1), (myPosition.getColumn()-1)))) {
                if (moveUp != null) {
                    myRookMoves.add(moveUp);
                } break;
            } else {
                myRookMoves.add(moveUp);
            } upRow++; }
        int downRow = myPosition.getRow()-1;
        while (downRow>0) { // move down
            ChessMove moveDown = moveHelper(board, piece, myPosition, downRow, myPosition.getColumn());
            if (board.checkPiece(new ChessPosition((downRow-1), (myPosition.getColumn()-1)))) {
                if (moveDown != null) {
                    myRookMoves.add(moveDown);
                } break;
            } else {
                myRookMoves.add(moveDown);
            } downRow--; }
        int rightCol = myPosition.getColumn()+1;
        while (rightCol<=8) { // move right
            ChessMove moveRight = moveHelper(board, piece, myPosition, myPosition.getRow(), rightCol);
            if (board.checkPiece(new ChessPosition((myPosition.getRow()-1), (rightCol-1)))) {
                if (moveRight != null) {
                    myRookMoves.add(moveRight);
                } break;
            } else {
                myRookMoves.add(moveRight);
            } rightCol++; }
        int leftCol = myPosition.getColumn()-1;
        while(leftCol>0) {
            ChessMove moveLeft = moveHelper(board, piece, myPosition, myPosition.getRow(), leftCol);
            if (board.checkPiece(new ChessPosition((myPosition.getRow()-1), (leftCol-1)))) {
                if (moveLeft != null) {
                    myRookMoves.add(moveLeft);
                } break;
            } else {
                myRookMoves.add(moveLeft);
            } leftCol--; }
        return myRookMoves;
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

    @Override
    public String toString() {
        return "{" + pieceColor + type + '}';
    }
}