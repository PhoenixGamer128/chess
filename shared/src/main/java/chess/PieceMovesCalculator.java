package chess;

import java.util.Collection;
import java.util.HashSet;

public class PieceMovesCalculator {

    Collection<ChessMove> moveSet;
    ChessBoard board;
    ChessPosition startPosition;
    ChessPiece piece;
    int row;
    int col;

    public void begin(ChessBoard board, ChessPosition position) {
        moveSet = new HashSet<>();
        this.board = board;
        startPosition = position;
        piece = board.getPiece(position);
        row = position.getRow();
        col = position.getColumn();
    }

    public Collection<ChessMove> calculateMoves() {
        ChessPiece.PieceType type = piece.getPieceType();
        if (type == ChessPiece.PieceType.BISHOP) {
            bishopMoveCalculator();
        } else if (type == ChessPiece.PieceType.ROOK) {
            rookMoveCalculator();
        } else if (type == ChessPiece.PieceType.QUEEN) {
            bishopMoveCalculator();
            rookMoveCalculator();
        } else if (type == ChessPiece.PieceType.KING) {
            kingMoveCalculator();
        } else if (type == ChessPiece.PieceType.KNIGHT) {
            knightMoveCalculator();
        } else if (type == ChessPiece.PieceType.PAWN) {
            pawnMoveCalculator();
        }
        return moveSet;
    }

    private boolean checkInBounds(int row, int col) {
        boolean inRow = (row >= 1 && row <= 8);
        boolean inCol = (col >= 1 && col <= 8);
        return inRow && inCol;
    }

    private boolean CheckAddMove(int row, int col, ChessPiece.PieceType promotionPiece) {
        ChessPosition targetPosition = new ChessPosition(row, col);
        if (!checkInBounds(row, col)) {
            return false;
        }
        if (board.getPiece(targetPosition) == null) {
            ChessMove move = new ChessMove(startPosition, targetPosition, promotionPiece);
            moveSet.add(move);
            return true;
        }

        if (!(board.getPiece(targetPosition).getTeamColor().equals(piece.getTeamColor()))) {
            ChessMove move = new ChessMove(startPosition, targetPosition, promotionPiece);
            moveSet.add(move);
            return false;
        }
        return false;
    }

    private void cardinalMoveCalculator(int dirRow, int dirCol) {
        int distance = 1;
        int targetRow = row + (distance * dirRow);
        int targetCol = col + (distance * dirCol);

        while (checkInBounds(targetRow, targetCol)) {
            if (!CheckAddMove(targetRow, targetCol, null)) {
                break;
            }
            distance++;
            targetRow = row + (distance * dirRow);
            targetCol = col + (distance * dirCol);
        }
    }

    private void bishopMoveCalculator() {
        int dirRow = 1;
        int dirCol = 1;
        for (int i = 0; i < 4; i++) {
            cardinalMoveCalculator(dirRow, dirCol);
            if (dirRow == 1 && dirCol == 1) {
                dirCol = -1;
            } else if (dirRow == 1 && dirCol == -1) {
                dirRow = -1;
            } else {
                dirCol = 1;
            }
        }
    }

    private void rookMoveCalculator() {
        int dirRow = 0;
        int dirCol = 1;
        for(int i = 0; i < 4; i++) {
            cardinalMoveCalculator(dirRow, dirCol);
            if (dirRow == 0 && dirCol == 1) {
                dirRow = 1;
                dirCol = 0;
            } else if (dirRow == 1 && dirCol == 0) {
                dirRow = 0;
                dirCol = -1;
            } else {
                dirRow = -1;
                dirCol = 0;
            }
        }
    }

    private void kingMoveCalculator() {
        CheckAddMove(row + 1, col + 1, null);
        CheckAddMove(row + 1, col - 1, null);
        CheckAddMove(row - 1, col + 1, null);
        CheckAddMove(row - 1, col - 1, null);
        CheckAddMove(row + 1, col, null);
        CheckAddMove(row - 1, col, null);
        CheckAddMove(row, col + 1, null);
        CheckAddMove(row, col - 1, null);

        if (piece.canUseSpecial) {
            // int castleRow = piece.getTeamColor().equals(ChessGame.TeamColor.WHITE) ? 1 : 8;
            boolean leftRookAvailable = false;
            boolean rightRookAvailable = false;
            int targetCol = 1;
            for (int i = 0; i < 2; i++) {
                ChessPosition position = new ChessPosition(row, targetCol);
                ChessPiece piece = board.getPiece(position);
                if (piece != null) {
                    if (targetCol == 1) {
                        leftRookAvailable = piece.canUseSpecial;
                    }
                    else {
                        rightRookAvailable = piece.canUseSpecial;
                    }
                }
                targetCol = 8;
            }
            if (leftRookAvailable) {
                CheckAddMove(row, col - 2, null);
            }
            if (rightRookAvailable) {
                CheckAddMove(row, col + 2, null);
            }
        }
    }

    private void knightMoveCalculator() {
        CheckAddMove(row + 2, col + 1, null);
        CheckAddMove(row + 2, col - 1, null);
        CheckAddMove(row - 2, col + 1, null);
        CheckAddMove(row - 2, col - 1, null);
        CheckAddMove(row + 1, col + 2, null);
        CheckAddMove(row + 1, col - 2, null);
        CheckAddMove(row - 1, col + 2, null);
        CheckAddMove(row - 1, col - 2, null);
    }

    private void pawnMoveCalculator() {
        ChessGame.TeamColor color = piece.getTeamColor();
        boolean firstMove = false;
        int direction = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? 1 : -1;
        if (color == ChessGame.TeamColor.WHITE && row == 2) {
            firstMove = true;
        } else if (color == ChessGame.TeamColor.BLACK && row == 7) {
            firstMove = true;
        }

        // Move Forward
        ChessPosition targetPosition = new ChessPosition(row + direction, col);
        if (board.getPiece(targetPosition) == null) {
            pawnAddPromotion(row + direction, col, piece.getTeamColor());
            targetPosition = new ChessPosition(row + (2 * direction), col);
            // Add double-step
            if (firstMove && board.getPiece(targetPosition) == null) {
                pawnAddPromotion(row + (2 * direction), col, piece.getTeamColor());
            }
        }
        // Allow capturing diagonally
        targetPosition = new ChessPosition(row + direction, col - 1);
        if (board.getPiece(targetPosition) != null) {
            pawnAddPromotion(row + direction, col - 1, color);
        }
        targetPosition = new ChessPosition(row + direction, col + 1);
        if (board.getPiece(targetPosition) != null) {
            pawnAddPromotion(row + direction, col + 1, color);
        }
        // Check En Passant

    }

    private void pawnAddPromotion(int row, int col, ChessGame.TeamColor color) {
        ChessPiece.PieceType[] promotionPieces;
        if ((row == 8 && color == ChessGame.TeamColor.WHITE)
                || row == 1 && color == ChessGame.TeamColor.BLACK) {
            promotionPieces = new ChessPiece.PieceType[]{
                    ChessPiece.PieceType.KNIGHT,
                    ChessPiece.PieceType.QUEEN,
                    ChessPiece.PieceType.ROOK,
                    ChessPiece.PieceType.BISHOP
            };
        } else {
            promotionPieces = new ChessPiece.PieceType[]{null};
        }
        for (ChessPiece.PieceType type : promotionPieces) {
            CheckAddMove(row, col, type);
        }
    }
}
