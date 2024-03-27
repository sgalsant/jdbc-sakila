import java.sql.*;
import java.util.Arrays;

public class FilmSQL {
    private Connection connection;

    public FilmSQL(Connection connection) {
        this.connection = connection;
    }


    /**
     * Ejemplo básico de una consulta SQL realizada con Statement
     */
    public void selectSQLStatement() {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("select film_id, title, rating, special_features from film limit 3")) {

            while (resultSet.next()) {
                System.out.println("-------------");
                System.out.println("film_id: " + resultSet.getInt("film_id"));

                System.out.println("title: " + resultSet.getString(2)); // se puede acceder usando el número de la columna (index). IMPORTANTE: empiezan en 1

                System.out.println("rating: " + resultSet.getString("rating")); // una columna de tipo enumerado se accede como String

                String[] special_features = resultSet.getString("special_features").split(","); // special_features está declarada en MySQL de tipo Set
                System.out.println("special_features" + Arrays.toString(special_features));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * Ejemplo de una consulta básica usando ResultSetMetadata para conocer número y nombre de las columnas recuperadas
     */
    public void selectSQLWithMetaData() {
        try (Statement statement = this.connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery("select * from film order limit 3")) {
                ResultSetMetaData rsmd = resultSet.getMetaData();

                while (resultSet.next()) {
                    System.out.println("------------------");
                    for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                        System.out.println(rsmd.getColumnLabel(i) + ":" + resultSet.getString(i));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * Usar preparedStatement cuando debemos usar parámetros en la sentencia SQL -- Evita Sql injection, realiza automáticamente la conversión de tipos
     *
     * @param title Se muestra todos los films cuyo título contenga este texto
     */
    public void selectSQLPreparedStatement(String title) {
        try (PreparedStatement statement = this.connection.prepareStatement("select film_id, title from film where title like ? order by film_id")) {
            statement.setString(1, "%" + title + "%");
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    System.out.println("-------------");
                    System.out.println("film_id: " + resultSet.getInt(1));

                    System.out.println("title: " + resultSet.getString(2)); // se puede acceder usando el número de la columna (index). IMPORTANTE: empiezan en 1

                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Modifica el título del film con el id especificado
     *
     * @param filmId
     * @param newTitle
     * @return true si modifico un film
     */
    public boolean updateSQL(int filmId, String newTitle) {
        try (PreparedStatement statement = this.connection.prepareStatement("update film set title = ? where film_id = ? limit 1")) {
            statement.setString(1, newTitle);
            statement.setInt(2, filmId);
            return statement.executeUpdate() > 0; // executeUpdate devuelve el número de filas modificadas

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * Ejemplo de una operación de inserción de film
     *
     * @param title
     * @param language_id
     * @return id asignado automáticamente por la base de datos
     */
    public int insertSQL(String title, int language_id) {
        try (PreparedStatement statement = this.connection.prepareStatement("insert into film (title, language_id) values (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, title);
            statement.setInt(2, language_id);
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                generatedKeys.next();
                return generatedKeys.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }


    public boolean transaction(String title1, String title2, int languageId) {
        Boolean autoCommit = null;
        try {
            autoCommit = this.connection.getAutoCommit();
            this.connection.setAutoCommit(false);

            insertSQL(title1, languageId);
            insertSQL(title2, languageId);

            this.connection.commit();
            return true;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());

            try {
                connection.rollback();  // si se produce cualquier excepción, hacemos rollback
            } catch (SQLException ex2) {
                System.out.println(ex2.getMessage());
            }

            return false;
        } finally {
            try { // restauramos el autocommit a su valor original
                if (autoCommit != null && autoCommit != connection.getAutoCommit()) {
                    this.connection.setAutoCommit(autoCommit);
                }
            } catch (SQLException ex2) {
                System.out.println(ex2.getMessage());
            }
        }
    }
}
