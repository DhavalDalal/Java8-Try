package examples;

import com.tsys.utils.Failure;
import com.tsys.utils.FunctionThrowsException;
import com.tsys.utils.Try;

import java.io.Console;

public class TryExample {

    static Try<Integer> divide() {
        Console console = System.console();
        if (console == null) {
            return new Failure(new UnsupportedOperationException("Empty console"));
        }
        String numer = console.readLine("Enter an Int that you'd like to divide:\n");
        String denom = console.readLine("Enter an Int that you'd like to divide by:\n");

        FunctionThrowsException<String, Integer, NumberFormatException> toInteger = s -> Integer.parseInt(s);
        Try<Integer> dividend = Try.with(toInteger, numer);
        Try<Integer> divisor = Try.with(toInteger, denom);
        return dividend.flatMap(x -> divisor.map(y -> x/y))
                .recoverWith(t -> divide());
    }

    public static void main(String[] args) {
        divide().get();//.forEach(System.out::println);
    }
}
