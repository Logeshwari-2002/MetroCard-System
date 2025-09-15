package Services;

import db.DBConnection;
import java.sql.*;

public class CheckInService {
    public static void checkIn(String cardNumber, String passengerType, String station) {
        double baseCharge = switch (passengerType) {
            case "ADULT" -> 200;
            case "SENIOR_CITIZEN" -> 100;
            case "KID" -> 50;
            default -> 0;
        };

        double finalCharge = baseCharge;

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            // ✅ Find last journey for discount check
            PreparedStatement lastJourney = conn.prepareStatement(
                    "SELECT station FROM journey WHERE card_number = ? ORDER BY journey_time DESC LIMIT 1"
            );
            lastJourney.setString(1, cardNumber);
            ResultSet lastRs = lastJourney.executeQuery();

            if (lastRs.next()) {
                String lastStation = lastRs.getString("station");

                // If returning to a different station → 50% discount
                if (!lastStation.equals(station)) {
                    finalCharge = baseCharge / 2;
                }
            }

            // ✅ Get balance
            PreparedStatement getBalance = conn.prepareStatement(
                    "SELECT balance FROM metrocard WHERE card_number = ?"
            );
            getBalance.setString(1, cardNumber);
            ResultSet rs = getBalance.executeQuery();

            if (rs.next()) {
                double currentBalance = rs.getDouble("balance");

                if (currentBalance >= finalCharge) {
                    // Deduct balance
                    PreparedStatement update = conn.prepareStatement(
                            "UPDATE metrocard SET balance = balance - ? WHERE card_number = ?"
                    );
                    update.setDouble(1, finalCharge);
                    update.setString(2, cardNumber);
                    update.executeUpdate();

                    // Record journey
                    PreparedStatement insertJourney = conn.prepareStatement(
                            "INSERT INTO journey (card_number, passenger_type, station, charge) VALUES (?, ?, ?, ?)"
                    );
                    insertJourney.setString(1, cardNumber);
                    insertJourney.setString(2, passengerType);
                    insertJourney.setString(3, station);
                    insertJourney.setDouble(4, finalCharge);
                    insertJourney.executeUpdate();

                    conn.commit();
                    System.out.println("Checked in at " + station + " | Fare: " + finalCharge);
                } else {
                    System.out.println("❌ Insufficient balance for " + cardNumber);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
