package examples;

import com.tsys.utils.*;

import java.io.Console;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Supplier;

public class TryExample {

    static String readKeyboard(String prompt) {
        Scanner in = new Scanner(System.in);
        System.out.print(prompt);
        return in.nextLine();
    }

    static Try<Integer> divide() {
        String numer = readKeyboard("Enter an Int that you'd like to divide:");
        String denom = readKeyboard("Enter an Int that you'd like to divide by:");

        FunctionThrowsException<String, Integer, NumberFormatException> toInteger = s -> Integer.parseInt(s);
        Try<Integer> dividend = Try.with(toInteger, numer);
        Try<Integer> divisor = Try.with(toInteger, denom);
        return dividend.flatMap(x -> divisor.map(y -> x / y))
                .recoverWith(t -> divide());
    }


    public static void main(String[] args) {
        divide().forEach(System.out::println);


        FunctionThrowsException<String, Connection, SQLException> getConnection = DriverManager::getConnection;
        String url = "jdbc:oracle:oci8:scott/tiger@myhost";
        Try<Connection> connection = Try.with(getConnection, url);
        System.out.println("connection = " + connection);

        FunctionThrowsException<Connection, Statement, SQLException> createStatement = c -> c.createStatement();
        Try<Try<Statement>> statement = connection.map(c -> Try.with(createStatement, c));
        System.out.println("statement = " + statement);

        BiFunctionThrowsException<Statement, String, ResultSet, SQLException> execute =
                (s, sql) -> {
                    s.execute(sql);
                    return s.getResultSet();
                };

        String sql = "select * from events";
        Try<Try<Try<ResultSet>>> resultSet = statement.map(s -> s.map(x -> Try.with(execute, x, sql)));
        System.out.println("resultSet = " + resultSet);

        FunctionThrowsException<ResultSet, Event, SQLException> toEvent = r -> {
            String type = r.getString(1);
            return new Event(type);
        };

        Try<Try<Try<Try<Event>>>> event = resultSet.map(c -> c.map(s -> s.map(r -> Try.with(toEvent, r))));
        System.out.println("event = " + event);

        Try<Try<Try<Try<Event>>>> nestedEvent =
                Try.with(getConnection, url)
                        .map(c -> Try.with(createStatement, c))
                        .map(c -> c.map(s -> Try.with(execute, s, sql)))
                        .map(c -> c.map(s -> s.map(r -> Try.with(toEvent, r))));
        System.out.println("nestedEvent = " + nestedEvent);

        Try<Event> flattenedEvent =
                Try.with(getConnection, url)
                        .flatMap(c -> Try.with(createStatement, c))
                        .flatMap(s -> Try.with(execute, s, sql))
                        .flatMap(r -> Try.with(toEvent, r));
        System.out.println("flattenedEvent = " + flattenedEvent);

        List<Event> events = new ArrayList<>();
        Sql.execute(url, sql, rs -> {
            while (rs.next()) {
              String type = rs.getString(0);
              events.add(new Event(type));
            }
        });

    }
}


class Sql {
    static void execute(String dburl, String sql, ConsumerThrowsException<ResultSet, SQLException> consumer) {
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

