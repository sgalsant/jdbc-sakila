import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sakila", "alumno", "alumno")) {
            FilmSQL filmSQL = new FilmSQL(connection);

            filmSQL.selectSQLStatement();

            // filmSQL.selectSQLWithMetaData();

            // filmSQL.selectSQLPreparedStatement("");

            // filmSQL.updateSQL(991, "WORST BANGER");

            // int id = filmSQL.insertSQL("DAW DAW DAW", 1);
            // System.out.printf("id " + id);

            // filmSQL.transaction("DAW transacción 1", "DAW transacción 2", 1);
            // filmSQL.selectSQLPreparedStatement("DAW");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
