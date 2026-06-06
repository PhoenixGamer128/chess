package model;

import chess.ChessGame;

public record ChessPlayersInfo(
        String playerUsername,
        String enemyUsername,
        ChessGame.TeamColor playerColor,
        ChessGame.TeamColor enemyColor) {
}
