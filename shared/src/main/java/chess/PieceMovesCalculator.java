package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static chess.ChessPiece.PieceType;

public class PieceMovesCalculator {

    private final ChessPosition startPosition;
    private final int positionRow;
    private final int positionCol;
    private Set<ChessMove> pieceMoves;
    private final ChessPiece piece;
    private final ChessBoard board;

    public PieceMovesCalculator(ChessBoard board, ChessPiece piece, ChessPosition startPosition) {
        this.board = board;
        this.startPosition = startPosition;
        positionRow = this.startPosition.getRow();
        positionCol = this.startPosition.getColumn();
        pieceMoves = new HashSet<>();
        this.piece = piece;
    }

    public Collection<ChessMove> getMoves() {
        if (piece.getPieceType() == PieceType.BISHOP) {
            BishopMovesCalculator();
        } else if (piece.getPieceType() == PieceType.ROOK) {
            RookMovesCalculator();
        } else if (piece.getPieceType() == PieceType.QUEEN) {
            BishopMovesCalculator();
            RookMovesCalculator();
        } else if (piece.getPieceType() == PieceType.KING) {
            KingMovesCalculator();
        } else if (piece.getPieceType() == PieceType.PAWN) {
            PawnMovesCalculator();
        } else if (piece.getPieceType() == PieceType.KNIGHT) {
            KnightMovesCalculator();
        }
        return pieceMoves;
    }

    private void CardinalMoves(int dirRow, int dirCol) {
        int distance = 0;
        while (InBoundsAxis(positionRow + (distance + 1) * dirRow)
                && InBoundsAxis(positionCol + (distance + 1) * dirCol)) {
            distance += 1;
            int newRow = positionRow + (distance * dirRow);
            int newCol = positionCol + (distance * dirCol);

            ChessPosition endPosition = new ChessPosition(newRow, newCol);
            ChessMove move = new ChessMove(startPosition, endPosition, null);
            ChessPiece targetPiece = board.getPiece(endPosition);

            if (!(targetPiece == null)) {
                if (targetPiece.getTeamColor().equals(piece.getTeamColor())) {
                    break;
                } else {
                    pieceMoves.add(move);
                    break;
                }
            }
            pieceMoves.add(move);
        }
    }

    private boolean InBoundsAxis(int axis) {
        return (axis <= 8 && axis >= 1);
    }

    private boolean InBounds(ChessPosition position) {
        return (InBoundsAxis(position.getRow()) && InBoundsAxis(position.getColumn()));
    }

    private void BishopMovesCalculator() {
        int dirRow = 1;
        int dirCol = 1;
        for (int i = 0; i < 4; i++) {

            CardinalMoves(dirRow, dirCol);

            if (dirCol == 1 && dirRow == 1) {
                dirCol = -1;
            } else if (dirCol == -1 && dirRow == 1) {
                dirRow = -1;
            } else{
                dirCol = 1;
            }
        }
    }

    private void RookMovesCalculator() {
        int dirRow = 0;
        int dirCol = 1;
        for (int i = 0; i < 4; i++) {

            CardinalMoves(dirRow, dirCol);

            if (dirCol == 1) {
                dirCol = -1;
            } else if (dirCol == -1) {
                dirCol = 0;
                dirRow = 1;
            } else if (dirRow == 1) {
                dirRow = -1;
            }
        }
    }

    private boolean CheckAddMove(int row, int col, PieceType promotionPiece) {
        ChessPosition endPosition = new ChessPosition(row, col);
        if (!InBounds(endPosition)) {
            return false;
        }
        ChessPiece targetPiece = board.getPiece(endPosition);
        if (targetPiece != null) {
            if (targetPiece.getTeamColor().equals(piece.getTeamColor())) {
                return false;
            }
        }
        ChessMove move = new ChessMove(startPosition, endPosition, promotionPiece);
        pieceMoves.add(move);
        return true;
    }

    private void KingMovesCalculator() {
        CheckAddMove(positionRow + 1, positionCol + 1, null);
        CheckAddMove(positionRow + 1, positionCol, null);
        CheckAddMove(positionRow + 1, positionCol - 1, null);
        CheckAddMove(positionRow, positionCol - 1, null);
        CheckAddMove(positionRow - 1, positionCol - 1, null);
        CheckAddMove(positionRow - 1, positionCol, null);
        CheckAddMove(positionRow - 1, positionCol + 1, null);
        CheckAddMove(positionRow, positionCol + 1, null);
    }

    private ChessGame.TeamColor GetPositionColor(int row, int col) {
        ChessPosition targetPosition = new ChessPosition(row, col);
        ChessPiece targetPiece = board.getPiece(targetPosition);
        if (targetPiece == null) {
            return null;
        }
        return targetPiece.getTeamColor();
    }

    private boolean AddPawnMove(int row, int col, boolean isDiagonal) {
        ChessPiece.PieceType[] promotionPieces = {
                PieceType.QUEEN,
                PieceType.ROOK,
                PieceType.BISHOP,
                PieceType.KNIGHT
        };
        ChessGame.TeamColor targetColor = GetPositionColor(row, col);

        // Move forward
        if (targetColor == null && !isDiagonal) {
            // Check Pawn promotion
            if (row == 8 || row == 1) {
                for (PieceType promotionPiece : promotionPieces) {
                    CheckAddMove(row, col, promotionPiece);
                }
                return true;
            }
            CheckAddMove(row, col, null);
            return true;
        }

        // Move diagonal
        if (isDiagonal && targetColor != null) {
            if (row == 8 || row == 1) {
                for (PieceType promotionPiece : promotionPieces) {
                    CheckAddMove(row, col, promotionPiece);
                }
                return true;
            }
            CheckAddMove(row, col, null);
            return true;
        }
        return false;
    }

    private void PawnMovesCalculator() {

        int direction = 1;
        boolean freeSpace;

        if (piece.getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
            direction = -1;
        }

        freeSpace = AddPawnMove(positionRow + direction, positionCol, false);
        AddPawnMove(positionRow + direction, positionCol + 1, true);
        AddPawnMove(positionRow + direction, positionCol - 1, true);
        if (piece.getTeamColor().equals(ChessGame.TeamColor.WHITE) && positionRow == 2 && freeSpace) {
            AddPawnMove(positionRow + 2, positionCol, false);
        } else if (piece.getTeamColor().equals(ChessGame.TeamColor.BLACK) && positionRow == 7 && freeSpace) {
            AddPawnMove(positionRow - 2, positionCol, false);
        }
    }

    private void KnightMovesCalculator() {
        CheckAddMove(positionRow + 2, positionCol + 1, null);
        CheckAddMove(positionRow + 2, positionCol - 1, null);
        CheckAddMove(positionRow - 2, positionCol + 1, null);
        CheckAddMove(positionRow - 2, positionCol - 1, null);
        CheckAddMove(positionRow + 1, positionCol + 2, null);
        CheckAddMove(positionRow + 1, positionCol - 2, null);
        CheckAddMove(positionRow - 1, positionCol + 2, null);
        CheckAddMove(positionRow - 1, positionCol - 2, null);

    }
}
