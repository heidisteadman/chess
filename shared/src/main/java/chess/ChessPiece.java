package chess;

import java.util.ArrayList;
import java.util.Collection;
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
            return bishopMoves(board, myPosition);

        } else if (piece.getPieceType() == PieceType.KING) { // code for how a king moves
            return kingMoves(board, myPosition);
        } else if (piece.getPieceType() == PieceType.KNIGHT) {
            return knightMoves(board, myPosition);
        } else if (piece.getPieceType() == PieceType.PAWN) {
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                return whitePawnMoves(board, myPosition);
            } else {
                return blackPawnMoves(board, myPosition);
            }
        } else if (piece.getPieceType() == PieceType.QUEEN) {
            ArrayList<ChessMove> queenDiagonal = bishopMoves(board, myPosition);
            ArrayList<ChessMove> queenSideways = rookMoves(board, myPosition);
            queenDiagonal.addAll(queenSideways);
            return queenDiagonal;
        } else {
            return rookMoves(board, myPosition);
        }
    }

    private ChessMove moveHelper(ChessBoard board,
                                 ChessPiece piece,
                                 ChessPosition myPosition,
                                 int row, int col) {
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

    private ArrayList<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
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

    private ArrayList<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition){
        ArrayList<ChessMove> myKingMoves = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);

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

    private ArrayList<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        ArrayList<ChessMove> myKnightMoves = new ArrayList<>();

        if (myPosition.getRow()+2 <= 8) { // move up
            if (myPosition.getColumn()+1 <=8) { // move up right
                ChessPosition upRight = new ChessPosition((myPosition.getRow()+2), (myPosition.getColumn()+1));
                ChessMove knightMoveUpRight = new ChessMove(myPosition, upRight, null);
                ChessPosition checkPos = new ChessPosition((myPosition.getRow()+1), myPosition.getColumn());
                if (board.checkPiece(checkPos)) {
                    ChessPiece upRightPiece = board.getPiece(upRight);
                    if (upRightPiece.getTeamColor() != piece.getTeamColor()) {
                        myKnightMoves.add(knightMoveUpRight);
                    }
                } else {
                    myKnightMoves.add(knightMoveUpRight);
                }
            }
            if (myPosition.getColumn()-1 >0) { // move up left
                ChessPosition upLeft = new ChessPosition((myPosition.getRow()+2), (myPosition.getColumn()-1));
                ChessMove knightMoveUpLeft = new ChessMove(myPosition, upLeft, null);
                ChessPosition checkUpLeft = new ChessPosition((myPosition.getRow()+1), (myPosition.getColumn()-2));
                if (board.checkPiece(checkUpLeft)) {
                    ChessPiece upLeftPiece = board.getPiece(upLeft);
                    if (upLeftPiece.getTeamColor() != piece.getTeamColor()) {
                        myKnightMoves.add(knightMoveUpLeft);
                    }
                } else {
                    myKnightMoves.add(knightMoveUpLeft);
                }
            }

        }

        if (myPosition.getRow()-2 > 0) { // move down
            if (myPosition.getColumn() +1 <= 8) { // move down right
                ChessPosition downRight = new ChessPosition((myPosition.getRow()-2), (myPosition.getColumn()+1));
                ChessMove knightMoveDownRight = new ChessMove(myPosition, downRight, null);
                ChessPosition checkDownRight = new ChessPosition((myPosition.getRow()-3), (myPosition.getColumn()));
                if (board.checkPiece(checkDownRight)) {
                    ChessPiece downRightPiece = board.getPiece(downRight);
                    if (downRightPiece.getTeamColor() != piece.getTeamColor()) {
                        myKnightMoves.add(knightMoveDownRight);
                    }
                } else {
                    myKnightMoves.add(knightMoveDownRight);
                }
            }
            if (myPosition.getColumn()-1 >0) { // move down left
                ChessPosition downLeft = new ChessPosition((myPosition.getRow()-2), (myPosition.getColumn()-1));
                ChessMove knightMoveDownLeft = new ChessMove(myPosition, downLeft, null);
                ChessPosition checkDownLeft = new ChessPosition((myPosition.getRow()-3), (myPosition.getColumn()-2));
                if (board.checkPiece(checkDownLeft)) {
                    ChessPiece downLeftPiece = board.getPiece(downLeft);
                    if (downLeftPiece.getTeamColor() != piece.getTeamColor()) {
                        myKnightMoves.add(knightMoveDownLeft);
                    }
                } else {
                    myKnightMoves.add(knightMoveDownLeft);
                }
            }
        }

        if (myPosition.getColumn()+2 <= 8) { // move right
            if (myPosition.getRow() +1 <=8) { // move right up
                ChessPosition rightUp = new ChessPosition((myPosition.getRow()+1), (myPosition.getColumn()+2));
                ChessMove knightMoveRightUp = new ChessMove(myPosition, rightUp, null);
                ChessPosition checkRightUp = new ChessPosition((myPosition.getRow()), (myPosition.getColumn()+1));
                if (board.checkPiece(checkRightUp)) {
                    ChessPiece rightUpPiece = board.getPiece(rightUp);
                    if (rightUpPiece.getTeamColor() != piece.getTeamColor()) {
                        myKnightMoves.add(knightMoveRightUp);
                    }
                } else {
                    myKnightMoves.add(knightMoveRightUp);
                }
            }
            if (myPosition.getRow()-1 >0) { // move right down
                ChessPosition rightDown = new ChessPosition((myPosition.getRow()-1), (myPosition.getColumn()+2));
                ChessMove knightMoveRightDown = new ChessMove(myPosition, rightDown, null);
                ChessPosition checkRightDown = new ChessPosition((myPosition.getRow()-2), (myPosition.getColumn()+1));
                if (board.checkPiece(checkRightDown)) {
                    ChessPiece rightDownPiece = board.getPiece(rightDown);
                    if (rightDownPiece.getTeamColor() != piece.getTeamColor()) {
                        myKnightMoves.add(knightMoveRightDown);
                    }
                } else {
                    myKnightMoves.add(knightMoveRightDown);
                }
            }
        }

        if (myPosition.getColumn()-2 >0) { // move left
            if (myPosition.getRow()+1 <=8) { // move left up
                ChessPosition leftUp = new ChessPosition((myPosition.getRow()+1), (myPosition.getColumn()-2));
                ChessMove knightMoveLeftUp = new ChessMove(myPosition, leftUp, null);
                ChessPosition checkLeftUp = new ChessPosition((myPosition.getRow()), (myPosition.getColumn()-3));
                if (board.checkPiece(checkLeftUp)) {
                    ChessPiece leftUpPiece = board.getPiece(leftUp);
                    if (leftUpPiece.getTeamColor() != piece.getTeamColor()) {
                        myKnightMoves.add(knightMoveLeftUp);
                    }
                } else {
                    myKnightMoves.add(knightMoveLeftUp);
                }
            }
            if (myPosition.getRow()-1 >0) { // move left down
                ChessPosition leftDown = new ChessPosition((myPosition.getRow()-1), (myPosition.getColumn()-2));
                ChessMove knightMoveLeftDown = new ChessMove(myPosition, leftDown, null);
                ChessPosition checkLeftDown = new ChessPosition((myPosition.getRow()-2), (myPosition.getColumn()-3));
                if (board.checkPiece(checkLeftDown)) {
                    ChessPiece leftDownPiece = board.getPiece(leftDown);
                    if (leftDownPiece.getTeamColor() != piece.getTeamColor()) {
                        myKnightMoves.add(knightMoveLeftDown);
                    }
                } else {
                    myKnightMoves.add(knightMoveLeftDown);
                }
            }
        }
        return myKnightMoves;
    }

    private ArrayList<ChessMove> whitePawnMoves (ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> myWhitePawnMoves = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);

        ChessPosition checkUp = new ChessPosition((myPosition.getRow()), (myPosition.getColumn()-1));
        ChessPosition oneForward = new ChessPosition((myPosition.getRow()+1), (myPosition.getColumn()));
        ChessMove moveForward = new ChessMove(myPosition, oneForward, null);
        if (myPosition.getRow() == 2) { // check if pawn is in starting position
            if (!board.checkPiece(checkUp)) { // move forward one
                myWhitePawnMoves.add(moveForward);
                ChessPosition checkTwoUp = new ChessPosition((myPosition.getRow()+1), (myPosition.getColumn()-1));
                if (!board.checkPiece(checkTwoUp)) { // move forward two
                    ChessPosition twoForward = new ChessPosition((myPosition.getRow()+2), myPosition.getColumn());
                    ChessMove moveTwoForward = new ChessMove(myPosition, twoForward, null);
                    myWhitePawnMoves.add(moveTwoForward);
                }
            }
        } else if (myPosition.getRow() != 7){ // check if pawn promotes moving forward
            if (!board.checkPiece(checkUp)) { // move one
               myWhitePawnMoves.add(moveForward);
            }
        } else { // piece can be promoted
            if (!board.checkPiece(checkUp)) {
                ChessMove moveUpPromoteRook = new ChessMove(myPosition, oneForward, PieceType.ROOK);
                ChessMove moveUpPromoteKnight = new ChessMove(myPosition, oneForward, PieceType.KNIGHT);
                ChessMove moveUpPromoteQueen = new ChessMove(myPosition, oneForward, PieceType.QUEEN);
                ChessMove moveUpPromoteBishop = new ChessMove(myPosition, oneForward, PieceType.BISHOP);
                myWhitePawnMoves.add(moveUpPromoteBishop);
                myWhitePawnMoves.add(moveUpPromoteKnight);
                myWhitePawnMoves.add(moveUpPromoteQueen);
                myWhitePawnMoves.add(moveUpPromoteRook);
            }
        }
        ChessPosition upLeft = new ChessPosition((myPosition.getRow()+1), (myPosition.getColumn()-1));
        ChessPosition checkUpLeft = new ChessPosition((myPosition.getRow()),(myPosition.getColumn()-2));
        if (myPosition.getColumn()-1 > 0) {
            if (board.checkPiece(checkUpLeft)) {
                ChessPiece upLeftPiece = board.getPiece(upLeft);
                if (upLeftPiece.getTeamColor() != piece.getTeamColor()) {
                    if (myPosition.getRow() != 7) {
                        ChessMove moveUpLeft = new ChessMove(myPosition, upLeft, null);
                        myWhitePawnMoves.add(moveUpLeft);
                    } else {
                        ChessMove upLeftPromoteRook = new ChessMove(myPosition, upLeft, PieceType.ROOK);
                        myWhitePawnMoves.add(upLeftPromoteRook);
                        ChessMove upLeftPromoteKnight = new ChessMove(myPosition, upLeft, PieceType.KNIGHT);
                        myWhitePawnMoves.add(upLeftPromoteKnight);
                        ChessMove upLeftPromoteBishop = new ChessMove(myPosition, upLeft, PieceType.BISHOP);
                        myWhitePawnMoves.add(upLeftPromoteBishop);
                        ChessMove upLeftPromoteQueen = new ChessMove(myPosition, upLeft, PieceType.QUEEN);
                        myWhitePawnMoves.add(upLeftPromoteQueen);
                    }
                }
            }
        }
        ChessPosition upRight = new ChessPosition((myPosition.getRow()+1), (myPosition.getColumn()+1));
        ChessPosition checkUpRight = new ChessPosition((myPosition.getRow()), (myPosition.getColumn()));
        if (myPosition.getColumn()+1 <= 8) {
            if (board.checkPiece(checkUpRight)) {
                ChessPiece upRightPiece = board.getPiece(upRight);
                if (upRightPiece.getTeamColor() != piece.getTeamColor()) {
                    if (myPosition.getRow() != 7) {
                        ChessMove moveUpRight = new ChessMove(myPosition, upRight, null);
                        myWhitePawnMoves.add(moveUpRight);
                    } else {
                        ChessMove upRightPromoteRook = new ChessMove(myPosition, upRight, PieceType.ROOK);
                        myWhitePawnMoves.add(upRightPromoteRook);
                        ChessMove upRightPromoteKnight = new ChessMove(myPosition, upRight, PieceType.KNIGHT);
                        myWhitePawnMoves.add(upRightPromoteKnight);
                        ChessMove upRightPromoteBishop = new ChessMove(myPosition, upRight, PieceType.BISHOP);
                        myWhitePawnMoves.add(upRightPromoteBishop);
                        ChessMove upRightPromoteQueen = new ChessMove(myPosition, upRight, PieceType.QUEEN);
                        myWhitePawnMoves.add(upRightPromoteQueen);
                    }
                }
            }
        }
        return myWhitePawnMoves;
    }

    private ArrayList<ChessMove> blackPawnMoves (ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> myBlackPawnMoves = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);

        ChessPosition oneForward = new ChessPosition((myPosition.getRow()-1), myPosition.getColumn());
        ChessPosition checkForward = new ChessPosition((myPosition.getRow()-2), (myPosition.getColumn()-1));
        ChessMove moveForward = new ChessMove(myPosition, oneForward, null);
        if (myPosition.getRow() == 7) { // check beginning position
            if (!board.checkPiece(checkForward)) { // move one forward
                myBlackPawnMoves.add(moveForward);
                ChessPosition twoForward = new ChessPosition((myPosition.getRow()-2), myPosition.getColumn());
                ChessPosition checkTwoForward = new ChessPosition((myPosition.getRow()-3), (myPosition.getColumn()-1));
                if (!board.checkPiece(checkTwoForward)) { // move two forward
                    ChessMove moveTwoForward = new ChessMove(myPosition, twoForward, null);
                    myBlackPawnMoves.add(moveTwoForward);
                }
            }
        } else if (myPosition.getRow() != 2){
            if (!board.checkPiece(checkForward)) { // move one forward if not at start
                myBlackPawnMoves.add(moveForward);
            }
        } else {
            ChessMove moveForwardPromoteRook = new ChessMove(myPosition, oneForward, PieceType.ROOK);
            myBlackPawnMoves.add(moveForwardPromoteRook);
            ChessMove moveForwardPromoteKnight = new ChessMove(myPosition, oneForward, PieceType.KNIGHT);
            myBlackPawnMoves.add(moveForwardPromoteKnight);
            ChessMove moveForwardPromoteBishop = new ChessMove(myPosition, oneForward, PieceType.BISHOP);
            myBlackPawnMoves.add(moveForwardPromoteBishop);
            ChessMove moveForwardPromoteQueen = new ChessMove(myPosition, oneForward, PieceType.QUEEN);
            myBlackPawnMoves.add(moveForwardPromoteQueen);
        }
        ChessPosition checkForwardRight = new ChessPosition((myPosition.getRow()-2), (myPosition.getColumn()-2));
        ChessPosition forwardRight = new ChessPosition((myPosition.getRow()-1), (myPosition.getColumn()-1));
        if ((myPosition.getColumn()-1) > 0) {
            if (board.checkPiece(checkForwardRight)) {
                ChessPiece forwardRightPiece = board.getPiece(forwardRight);
                if (forwardRightPiece.getTeamColor() != piece.getTeamColor()) {
                    if (myPosition.getRow() != 2) {
                        ChessMove moveForwardRight = new ChessMove(myPosition, forwardRight, null);
                        myBlackPawnMoves.add(moveForwardRight);
                    } else {
                        ChessMove moveForwardRightPromoteRook = new ChessMove(myPosition, forwardRight, PieceType.ROOK);
                        myBlackPawnMoves.add(moveForwardRightPromoteRook);
                        ChessMove moveForwardRightPromoteKnight = new ChessMove(myPosition, forwardRight, PieceType.KNIGHT);
                        myBlackPawnMoves.add(moveForwardRightPromoteKnight);
                        ChessMove moveForwardRightPromoteBishop = new ChessMove(myPosition, forwardRight, PieceType.BISHOP);
                        myBlackPawnMoves.add(moveForwardRightPromoteBishop);
                        ChessMove moveForwardRightPromoteQueen = new ChessMove(myPosition, forwardRight, PieceType.QUEEN);
                        myBlackPawnMoves.add(moveForwardRightPromoteQueen);
                    }
                }
            }
        }
        ChessPosition checkForwardLeft = new ChessPosition((myPosition.getRow()-2), (myPosition.getColumn()));
        ChessPosition forwardLeft = new ChessPosition((myPosition.getRow()-1), (myPosition.getColumn()+1));
        if (myPosition.getColumn()+1 <= 8) {
            if (board.checkPiece(checkForwardLeft)) {
                ChessPiece forwardLeftPiece = board.getPiece(forwardLeft);
                if (forwardLeftPiece.getTeamColor() != piece.getTeamColor()) {
                    if (myPosition.getRow() != 2) {
                        ChessMove moveForwardLeft = new ChessMove(myPosition, forwardLeft, null);
                        myBlackPawnMoves.add(moveForwardLeft);
                    } else {
                        ChessMove moveForwardLeftPromoteRook = new ChessMove(myPosition, forwardLeft, PieceType.ROOK);
                        myBlackPawnMoves.add(moveForwardLeftPromoteRook);
                        ChessMove moveForwardLeftPromoteKnight = new ChessMove(myPosition, forwardLeft, PieceType.KNIGHT);
                        myBlackPawnMoves.add(moveForwardLeftPromoteKnight);
                        ChessMove moveForwardLeftPromoteBishop = new ChessMove(myPosition, forwardLeft, PieceType.BISHOP);
                        myBlackPawnMoves.add(moveForwardLeftPromoteBishop);
                        ChessMove moveForwardLeftPromoteQueen = new ChessMove(myPosition, forwardLeft, PieceType.QUEEN);
                        myBlackPawnMoves.add(moveForwardLeftPromoteQueen);
                    }
                }
            }
        }
        return myBlackPawnMoves;
    }


    ArrayList<ChessMove> rookMoves (ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> myRookMoves = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);

        int upRow = myPosition.getRow()+1;
        while (upRow <= 8) { // move up
            ChessPosition upOne = new ChessPosition(upRow, myPosition.getColumn());
            ChessPosition checkUp = new ChessPosition((upRow-1), (myPosition.getColumn()-1));
            if (board.checkPiece(checkUp)) {
                ChessPiece upPiece = board.getPiece(upOne);
                if (upPiece.getTeamColor() != piece.getTeamColor()) {
                    ChessMove moveUp = new ChessMove(myPosition, upOne, null);
                    myRookMoves.add(moveUp);
                }
                break;
            } else {
                ChessMove moveUp = new ChessMove(myPosition, upOne, null);
                myRookMoves.add(moveUp);
            }
            upRow++;
        }

        int downRow = myPosition.getRow()-1;
        while (downRow>0) { // move down
            ChessPosition downOne = new ChessPosition(downRow, myPosition.getColumn());
            ChessPosition checkDown = new ChessPosition((downRow-1), (myPosition.getColumn()-1));
            if (board.checkPiece(checkDown)) {
                ChessPiece downPiece = board.getPiece(downOne);
                if (downPiece.getTeamColor()!=piece.getTeamColor()) {
                    ChessMove moveDown = new ChessMove(myPosition, downOne, null);
                    myRookMoves.add(moveDown);
                }
                break;
            } else {
                ChessMove moveDown = new ChessMove(myPosition, downOne, null);
                myRookMoves.add(moveDown);
            }
            downRow--;
        }

        int rightCol = myPosition.getColumn()+1;
        while (rightCol<=8) { // move right
            ChessPosition rightOne = new ChessPosition(myPosition.getRow(), rightCol);
            ChessPosition checkRight = new ChessPosition((myPosition.getRow()-1), (rightCol-1));
            if (board.checkPiece(checkRight)) {
                ChessPiece rightPiece = board.getPiece(rightOne);
                if (rightPiece.getTeamColor()!=piece.getTeamColor()) {
                    ChessMove moveRight = new ChessMove(myPosition, rightOne, null);
                    myRookMoves.add(moveRight);
                }
                break;
            } else {
                ChessMove moveRight = new ChessMove(myPosition, rightOne, null);
                myRookMoves.add(moveRight);
            }
            rightCol++;
        }

        int leftCol = myPosition.getColumn()-1;
        while(leftCol>0) {
            ChessPosition leftOne = new ChessPosition(myPosition.getRow(), leftCol);
            ChessPosition checkLeft = new ChessPosition((myPosition.getRow()-1), (leftCol-1));
            if (board.checkPiece(checkLeft)) {
                ChessPiece leftPiece = board.getPiece(leftOne);
                if (leftPiece.getTeamColor()!=piece.getTeamColor()) {
                    ChessMove moveLeft = new ChessMove(myPosition, leftOne, null);
                    myRookMoves.add(moveLeft);
                }
                break;
            } else {
                ChessMove moveLeft = new ChessMove(myPosition, leftOne, null);
                myRookMoves.add(moveLeft);
            }
            leftCol--;
        }

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
        return "{" +
                pieceColor +
                type +
                '}';
    }
}
