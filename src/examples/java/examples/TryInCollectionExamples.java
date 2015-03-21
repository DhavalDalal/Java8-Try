package examples;

import com.tsys.utils.*;

import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TryInCollectionExamples {

    private static final Random random = new Random();

    static String capitalize(String s) throws Exception {
        if (null == s)
            throw new Exception("null");

        return s.toUpperCase();
    }

    static Try<String> prefixCapitalize(String prefix, String s) {
        if(null == prefix)
            throw new IllegalArgumentException("null prefix");

        return Try.with(() -> prefix + capitalize(s));
    }


    static void printCapitalized(String s) throws Exception {
        if (null == s)
            throw new Exception("null");

        System.out.println(s.toUpperCase());

    }

    static String generate() throws Exception {
        if(random.nextInt(3) == 0)
            throw new Exception("Could Not Generate String");

        final StringBuilder name = random.ints(97, 122)
                .limit(random.nextInt(10))
                .mapToObj(c -> Character.valueOf((char) c))
                .filter(Character::isAlphabetic)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append);

        return name.toString();
    }


    public static void main(String[] args) throws Exception {
        //Example: FunctionThrowingException, map
        List<Try<String>> mapped = Arrays.asList("Hello", null, "dance").stream()
                .map(s -> Try.with((FunctionThrowsException<String, String, Exception>) TryInCollectionExamples::capitalize, s))
                .collect(Collectors.toList());
        System.out.println("mapped = " + mapped);

        //Example: FunctionThrowingException encapsulated in Try, map
        List<Try<String>> mapEncapsulated = Arrays.asList("Hello", null, "dance").stream()
                .map(s -> TryInCollectionExamples.prefixCapitalize("--> ", s))
                .collect(Collectors.toList());
        System.out.println("mapEncapsulated = " + mapEncapsulated);

        //Example: ConsumerThrowingException
        Arrays.asList("Hello", null, "dance").stream()
                .forEach(s -> Try.with(TryInCollectionExamples::printCapitalized, s));

        //Example: SupplierThrowingException
        final List<Try<String>> strings = Stream.generate(() -> Try.with(() -> TryInCollectionExamples.generate()))
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
    }
}


