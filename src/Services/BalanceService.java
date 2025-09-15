package Services;

import db.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BalanceService {
    public static void addBalance(String cardNumber, double balance) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO metrocard (card_number, balance) VALUES (?, ?) " +
                            "ON DUPLICATE KEY UPDATE balance = balance + ?"
            );
            stmt.setString(1, cardNumber);
            stmt.setDouble(2, balance);
            stmt.setDouble(3, balance);
            stmt.executeUpdate();
            System.out.println("Balance updated for card: " + cardNumber);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
