package examples;

import com.tsys.utils.ConsumerThrowsException;
import com.tsys.utils.FunctionThrowsException;
import com.tsys.utils.Try;

import java.sql.*;

public class Sql {

    //close the resultset, statement and connection manually.
    public static Try<ResultSet> execute(String url, String sql) {
        FunctionThrowsException<String, Connection, SQLException> getConnection = DriverManager::getConnection;
        FunctionThrowsException<Connection, Statement, SQLException> createStatement = Connection::createStatement;
        FunctionThrowsException<Statement, ResultSet, SQLException> execute =
                s -> {
                    s.execute(sql);
                    return s.getResultSet();
                };

        return Try.with(getConnection, url)
           .flatMap(c -> Try.with(createStatement, c))
           .flatMap(s -> Try.with(execute, s));
    }

    public static void execute(String dburl, String sql, ConsumerThrowsException<ResultSet, SQLException> consumer) {
        try (Connection connection = DriverManager.getConnection(dburl)) {
            try (Statement statement = connection.createStatement()) {
                statement.execute(sql);
                try (ResultSet resultSet = statement.getResultSet()) {
                    consumer.accept(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
