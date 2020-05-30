
import java.sql.*;

public class SQLiteConnector {
    // строка подключения к бд SQLite
    private static final String url = "jdbc:sqlite:Rental.db";

    // метод для получения соединения с бд
    private Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url); // получение соединения
        } catch (SQLException e) { // если соединение не удалось установить
            System.out.println(e.getMessage()); // печать содержимого ошибки
        }
        return conn; // возвращение переменной для использования соединения
    }

    public void addClient(Client client) {
        // SQL запрос для вставки записи об автомобиле
        String sqlClient = "INSERT INTO Client (Name, Login, Password) VALUES(?, ?, ?);";

        // SQL запрос для вставки записи об точке проката клиента
        String sqlClientRental = "INSERT INTO ClientRental (ClientId, RentalId) VALUES(?, ?);";

        // SQL запрос для вставки записи об клиенте
        String sqlCar = "INSERT INTO Car (Model, Manufacturer, Price, Count, RentalId) " +
                "VALUES (?, ?, ?, ?, ?);";

        // SQL запрос для вставки записи об точке проката
        String sqlRental = "INSERT INTO Rental (Point) VALUES(?);";

        ResultSet rs = null;
        Connection conn = null;
        PreparedStatement pstmt1 = null, pstmt2 = null, pstmt3 = null, pstmt4 = null;

        try {
            // соединение с бд
            conn = this.connect();
            if (conn == null)
                return;

            // установка режима auto-commit в false
            conn.setAutoCommit(false);

            // добавление клиента в бд
            pstmt1 = conn.prepareStatement(sqlClient,
                    Statement.RETURN_GENERATED_KEYS);

            pstmt1.setString(1, client.getName());
            pstmt1.setString(2, client.getLogin());
            pstmt1.setString(3, client.getPassword());
            int rowAffected = pstmt1.executeUpdate();

            // получение clientId добавленного клиента
            rs = pstmt1.getGeneratedKeys();
            if (rs.next()) {
                client.setId(rs.getInt(1));
            }

            if (rowAffected != 1) {
                conn.rollback();
            }
            // добавление точек проката
            for (int i = 0; i < client.getLength(); i++) {
                pstmt2 = conn.prepareStatement(sqlRental,
                        Statement.RETURN_GENERATED_KEYS);
                pstmt2.setString(1, client.getListItem(i).getPoint());

                rowAffected = pstmt2.executeUpdate();
                rs = pstmt2.getGeneratedKeys();

                if (rs.next()) {
                    client.setRentalId(i, rs.getInt(1));
                }

                if (rowAffected != 1) {
                    conn.rollback();
                }
            }

            // связывание точек проката и клиента
            for (int i = 0; i < client.getLength(); i++) {
                pstmt3 = conn.prepareStatement(sqlClientRental);
                pstmt3.setInt(1, client.getId());
                pstmt3.setInt(2, client.getListItem(i).getId());
                pstmt3.executeUpdate();
            }

            // добавление автомобилей в точки проката
            for (int i = 0; i < client.getLength(); i++) {
                for (int j = 0; j < client.getListItem(i).getLength(); j++) {
                    pstmt4 = conn.prepareStatement(sqlCar,
                            Statement.RETURN_GENERATED_KEYS);
                    pstmt4.setString(1, client.getListItem(i).getCar(j).getModel());
                    pstmt4.setString(2, client.getListItem(i).getCar(j).getManufacturer());
                    pstmt4.setDouble(3, client.getListItem(i).getCar(j).getPrice());
                    pstmt4.setInt(4, client.getListItem(i).getCar(j).getCount());
                    pstmt4.setInt(5, client.getListItem(i).getId());
                    rowAffected = pstmt4.executeUpdate();
                    rs = pstmt4.getGeneratedKeys();

                    if (rs.next()) {
                        client.getListItem(i).getCar(j).setId(rs.getInt(1));
                    }

                    if (rowAffected != 1) {
                        conn.rollback();
                    }
                }
            }

            // подтверждение изменений, внесенных транзакцией
            conn.commit();

        } catch (SQLException e1) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException e2) {
                System.out.println(e2.getMessage());
            }
            System.out.println(e1.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt1 != null) {
                    pstmt1.close();
                }
                if (pstmt2 != null) {
                    pstmt2.close();
                }
                if (pstmt3 != null) {
                    pstmt3.close();
                }
                if (pstmt4 != null) {
                    pstmt4.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e3) {
                System.out.println(e3.getMessage());
            }
        }
    }

    // метод для получения обьекта Client, соответствующему пользователю с clientId
    public Client getRentalList(int clientId) {
        String sqlRental = "Select Rental.RentalId, Point from Rental\n" +
                "Inner Join ClientRental\n" +
                "On Rental.RentalId = ClientRental.RentalId\n" +
                "Where ClientRental.ClientId = ?";

        String sqlCar = "Select * from Car Where RentalId = ?";
        Client client = new Client(10);
        client.setId(clientId);

        ResultSet rs = null, rs2 = null;
        Connection conn = null;
        PreparedStatement pstmt1 = null, pstmt2 = null;

        try {
            // соединение с бд
            conn = this.connect();
            // установка режима auto-commit в false
            conn.setAutoCommit(false);
            pstmt1 = conn.prepareStatement(sqlRental);
            pstmt2 = conn.prepareStatement(sqlCar);
            // установка значения первого параметра
            pstmt1.setInt(1, clientId);
            // получение результат выполнения запроса
            rs = pstmt1.executeQuery();
            int i = 0;
            // проход по строкам результата выполнения запроса
            while (rs.next()) {
                // добавление списка в обьект класса Client
                client.addListItem(new Rental(rs.getString("Point"), 100));
                client.getListItem(i).setId(rs.getInt("RentalId"));
                // установка значения первого параметра
                pstmt2.setInt(1, client.getListItem(i).getId());
                rs2 = pstmt2.executeQuery();
                // проход по строкам результата выполнения запроса
                int j = 0;

                while (rs2.next()) { // чтения строки результата выполнения запроса
                    // добавление автомобиля в список
                    client.getListItem(i).add(new Car(rs2.getString("Model"),
                            rs2.getString("Manufacturer"),
                            rs2.getDouble("Price"), rs2.getInt("Count")));
                    client.getListItem(i).getCar(j++).setId(rs2.getInt("CarId"));
                }

                i++;
            }

            // подтверждение изменений, внесенных транзакцией
            conn.commit();
        } catch (SQLException e1) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException e2) {
                System.out.println(e2.getMessage());
            }
            System.out.println(e1.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (rs2 != null) {
                    rs2.close();
                }
                if (pstmt1 != null) {
                    pstmt1.close();
                }
                if (pstmt2 != null) {
                    pstmt2.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e3) {
                System.out.println(e3.getMessage());
            }
        }
        return client;
    }

    // метод для удаления связи между клиентом и точкой проката
    public void deleteClientRental(int clientId, int rentalId) {
        String sql = "DELETE FROM ClientRental WHERE ClientId = ? AND RentalId = ?;";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, clientId);
            pstmt.setInt(2, rentalId);
            // выполнение операции удаления
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // метод для удаления клиента из бд
    public void deleteClient(int clientId) {
        String sql = "DELETE FROM Client WHERE ClientId = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, clientId);
            // выполнение операции удаления
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // метод для удаления точки проката из бд
    public void deleteRental(int rentalId) {
        String sql = "DELETE FROM Rental WHERE RentalId = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, rentalId);
            // выполнение операции удаления
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // метод для удаления автомобиля из бд
    public void deleteCar(int carId) {
        String sql = "DELETE FROM Car WHERE CarId = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, carId);
            // выполнение операции удаления
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // метод для проверки сущевствует ли клиент с заданными логином и паролем
    public boolean isClientExist(String login, String password) {
        String sql = "Select ClientId from Client \n" +
                "Where Login = ? AND Password = ?";
        boolean result = false;
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            pstmt.setString(2, password);
            // получение результата выполнения запроса
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) { // если в ответе есть строка значит существует
                result = true;
            } else {
                result = false;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public static void main(String[] args) {
        try {
            // создание обьекта класса SQLiteConnector
            SQLiteConnector sqLiteConnector = new SQLiteConnector();
            // добавление нового клиента
            Client client = new Client("Татьяна", "Tanya", "nePassword18", 100);
            Rental rental1 = new Rental("ул. Старошапкина, 122", 100);
            Rental rental2 = new Rental("ул. Велеса, 55", 100);
            Rental rental3 = new Rental("ул. Семиголового, 30", 100);
            // добавление автомобилей в точку проката вручную
            rental1.add(new Car("Melei", "Porshe", 1200000, 10));
            rental1.add(new Car("Malaga", "Lamborgini", 1900000, 16));
            rental1.add(new Car("Z 9", "Mazda", 600000, 22));
            rental1.add(new Car("Viva", "Ferrari", 6700000, 18));
            rental1.add(new Car("Acord", "Honda", 400000, 109));
            rental1.add(new Car("Astra", "Opel", 900000, 6));
            rental1.read("input.dat"); // чтения списка автомобилей из файла
            // добавление новых точек проката
            client.addListItem(rental1);
            client.addListItem(rental2);
            client.addListItem(rental3);
            sqLiteConnector.addClient(client);
            System.out.println("Точки проката клиента с Id " + client.getId());
            // запрос информации о точках проката клиента
            client = sqLiteConnector.getRentalList(client.getId());
            client.print();
            System.out.println("\n\nСписок автомобилей точки проката с Id " + client.getListItem(0).getId());
            // запрос информации об автомобилях точки проката
            client.getListItem(0).print();
            // проверка существования клиента с логином Tanya и паролем nePassword18
            if(sqLiteConnector.isClientExist("Tanya", "nePassword18"))
            {
                System.out.println("Клиент с логином Tanya и паролем nePassword18 существует.");
            }
            else
            {
                System.out.println("Клиент с логином Tanya и паролем nePassword18 не существует.");
            }
            // удаление связей между клиентом и точками проката из бд
            for(int i=0;i<client.getLength();i++) {
                sqLiteConnector.deleteClientRental(client.getId(), client.getListItem(i).getId());
            }
            // удаление клиента из бд
            sqLiteConnector.deleteClient(client.getId());
            for (int i = 0; i < client.getLength();i++) {
                for (int j = 0; j < client.getListItem(i).getLength(); j++) {
                    // удаление автомобилей из бд
                    sqLiteConnector.deleteCar(client.getListItem(i).getCar(j).getId());
                }
            }
            // удаление точек проката из бд
            for(int i=0;i<client.getLength();i++) {
                sqLiteConnector.deleteRental( client.getListItem(i).getId());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
