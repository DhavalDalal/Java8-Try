package com.tsys.utils;

import java.awt.*;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TryInCollectionExamples {
    public static void main(String[] args) throws Exception {
        //Example: FunctionThrowingException, map
        List<Try<String>> mapped = Arrays.asList("Hello", null, "dance").stream()
                .map(s -> Try.with((FunctionThrowsException<String, String, Exception>) TrySpecsUtil::capitalize, s))
                .collect(Collectors.toList());
        System.out.println("mapped = " + mapped);

        //Example: FunctionThrowingException encapsulated in Try, map
        List<Try<String>> mapEncapsulated = Arrays.asList("Hello", null, "dance").stream()
                .map(s -> TrySpecsUtil.prefixCapitalize("--> ", s))
                .collect(Collectors.toList());
        System.out.println("mapEncapsulated = " + mapEncapsulated);

        //Example: ConsumerThrowingException
        Arrays.asList("Hello", null, "dance").stream()
                .forEach(s -> Try.with(TrySpecsUtil::printCapitalized, s));

        //Example: SupplierThrowingException
        final List<Try<String>> strings = Stream.generate(() -> Try.with(() -> TrySpecsUtil.generate()))
                .filter(t -> t instanceof Success)
                .limit(3)
                .collect(Collectors.toList());

        System.out.println("strings = " + strings);


        //Example: PredicateThrowingException
//        final List<String> filtered = Arrays.asList("Hello", null, "hi").stream()
//                .filter(s -> Try.with(TrySpecsUtil::gte5).test(s))
//                .collect(Collectors.toList());
//        System.out.println("filtered = " + filtered);

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
            String text = r.getString(2);
            return new Event(type, text);
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
    }
}


class Event {
    private final String type;
    private final String text;

    Event(String type, String text) {
        this.type = type;
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public String getText() {
        return text;
    }
}
