import java.sql.*;
import java.util.Scanner;

public class HotelManagement {
    private static final String url = "jdbc:mysql://localhost:3306/db1";
    private static final String username = "root";
    private static final String password = "passwordm"; 

    public static void main(String[] args) throws ClassNotFoundException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            while (true) {
                System.out.println();
                System.out.println("Hotel Management System");
                Scanner scanner = new Scanner(System.in);
                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservation");
                System.out.println("3. Get room number");
                System.out.println("4. Update Reservation");
                System.out.println("5. Delete Reservation");
                System.out.println("0. Exit");
                System.out.print("Choose an option: ");

                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        reserveRoom(connection, scanner);
                        break;
                    case 2:
                        viewReservation(connection);
                        break;
                    case 3:
                        getRoomNumber(connection, scanner);
                        break;
                    case 4:
                        updateReservation(connection, scanner);
                        break;
                    case 5:
                        deleteReservation(connection, scanner);
                        break;
                    case 0:
                        System.out.println("Exiting the system.");
                        return;
                    default:
                        System.out.println("Invalid choice! Try Again.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public static void reserveRoom(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter Guest Name: ");
            String guestName = scanner.next();
            scanner.nextLine();

            System.out.print("Enter room no.: ");
            int roomNo = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Enter contact: ");
            String contact = scanner.next();

            String query = "INSERT INTO reservation (guest_name, room_number, contact_number) VALUES ('"
                    + guestName + "', " + roomNo + ", '" + contact + "');";
            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(query);
                if (affectedRows > 0) {
                    System.out.println("Reserved Successfully!");
                } else {
                    System.out.println("Reservation failed!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void viewReservation(Connection connection) {
        String query = "SELECT reservation_id, guest_name, contact_number, reservation_date FROM reservation";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            System.out.println("Current Reservations:");
            while (resultSet.next()) {
                int reservationId = resultSet.getInt("reservation_id");
                String guestName = resultSet.getString("guest_name");
                String contact = resultSet.getString("contact_number");
                String reservationDate = resultSet.getString("reservation_date");

                System.out.printf("ID: %d | Name: %s | Contact: %s | Date: %s%n",
                        reservationId, guestName, contact, reservationDate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void getRoomNumber(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter Reservation ID: ");
            int reservationId = scanner.nextInt();
            scanner.nextLine(); // Consume the newline left by nextInt()

            System.out.print("Enter Guest Name: ");
            String guestName = scanner.nextLine(); // Use nextLine() to capture full name

            // Constructing the SQL query
            String query = "SELECT room_number FROM reservation " +
                    "WHERE reservation_id = " + reservationId +
                    " AND guest_name = '" + guestName + "'";

            // Debugging: Print the query
            System.out.println("Executing Query: " + query);

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {

                if (resultSet.next()) {
                    int roomNumber = resultSet.getInt("room_number");
                    System.out.println("Room number for Reservation ID " + reservationId +
                            " and guest name " + guestName + " is: " + roomNumber);
                } else {
                    System.out.println("No reservation found for the given ID and guest name.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error occurred: " + e.getMessage());
            e.printStackTrace();
        }


}

    public static void updateReservation(Connection connection, Scanner scanner) {
        try {
            System.out.println("Enter reservation id to update resrvation: ");
            int reservation_id = scanner.nextInt();
            scanner.nextLine();

            if (!reservationExists(connection, reservation_id)) {
                System.out.println("Reservation not found!");
                return;
            }
            System.out.println("enter a new guest name: ");
            String new_guestName = scanner.nextLine();
            System.out.println("Enter new roomNumber: ");
            int new_room = scanner.nextInt();

            System.out.println("enter new contact number: ");
            String new_contact = scanner.next();

            String query = "UPDATE reservation SET guest_name = '" + new_guestName + "', " +
                    "room_number = " + new_room + ", " +
                    "contact_number = '" + new_contact + "' " +
                    "WHERE reservation_id = " + reservation_id;


            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(query);
                if (affectedRows > 0) {
                    System.out.println("Reservation Update Successfuly!");
                } else {
                    System.out.println("Reservation update failed!");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteReservation(Connection connection, Scanner scanner) {
        try {
            System.out.println("Enter reservation ID to delete reservation: ");
            int reservation_id = scanner.nextInt();

            // Check if the reservation exists
            if (!reservationExists(connection, reservation_id)) {
                System.out.println("Reservation not found for this reservation ID!");
                return;
            }

            // SQL query to delete the reservation
            String query = "DELETE FROM reservation WHERE reservation_id = " + reservation_id;

            // Execute the query
            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(query);
                if (affectedRows > 0) {
                    System.out.println("Reservation deleted successfully!");
                } else {
                    System.out.println("Failed to delete the reservation!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static boolean reservationExists(Connection connection, int reservation_id) {
        try {
            String query = "SELECT reservation_id FROM reservation WHERE reservation_id = " + reservation_id;
            try (Statement statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery(query);
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
