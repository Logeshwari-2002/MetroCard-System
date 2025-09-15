package Services;

import db.DBConnection;
import java.sql.*;
import java.util.*;

public class SummaryService {
    public static void printSummary() {
        try (Connection conn = DBConnection.getConnection()) {
            // Get total collection and passenger counts grouped by station & type
            String sql = "SELECT station, passenger_type, SUM(charge) as total, COUNT(*) as cnt " +
                    "FROM journey GROUP BY station, passenger_type";

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            // Map: station -> (passenger type -> count), also track total fare
            Map<String, Map<String, Integer>> stationPassengerCount = new HashMap<>();
            Map<String, Double> stationFare = new HashMap<>();

            while (rs.next()) {
                String station = rs.getString("station");
                String passenger = rs.getString("passenger_type");
                double total = rs.getDouble("total");
                int count = rs.getInt("cnt");

                stationPassengerCount.putIfAbsent(station, new HashMap<>());
                stationPassengerCount.get(station).put(passenger, count);

                stationFare.put(station, stationFare.getOrDefault(station, 0.0) + total);
            }

            // Print results
            for (String station : stationFare.keySet()) {
                System.out.println("TOTAL_COLLECTION " + station + " " + stationFare.get(station));
                System.out.println("PASSENGER_TYPE_SUMMARY");

                // Sort passengers by count (descending)
                Map<String, Integer> counts = stationPassengerCount.get(station);
                counts.entrySet().stream()
                        .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                        .forEach(entry ->
                                System.out.println(entry.getKey() + " " + entry.getValue())
                        );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
