package model;

import chess.ChessGame;

public record ChessPlayerInfo(
        String playerUsername,
        String enemyUsername,
        ChessGame.TeamColor playerColor,
        ChessGame.TeamColor enemyColor) {
}
