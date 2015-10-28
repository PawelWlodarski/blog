package pl.pawelwlodarski.fp.masses;

import java.util.Optional;
import java.util.function.Function;

/**
 * Created by pawel on 28.10.15.
 */
public class JavaExamples {

    public static void main(String[] input){
        Function<Double,Double> f1=x->x+1.0;
        Function<Double,Integer> f2=x->(int)Math.floor(x);
        Function<Integer,String> f3=x->"LOG : "+x;

        Optional<String> result = Optional.of(1.0).map(f1).map(f2).map(f3);
        result.ifPresent(System.out::println);

    }
}
