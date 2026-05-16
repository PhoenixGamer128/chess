package model;

import chess.ChessGame;

public record GameData(GameID gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
}
