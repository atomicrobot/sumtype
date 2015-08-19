package com.madebyatomicrobot.sumtype.samples;

import com.madebyatomicrobot.sumtype.samples.QuerySumType.QuerySumTypeVisitor;

import java.util.Arrays;
import java.util.List;

public class Main implements QuerySumTypeVisitor {
    public static void main(String[] argv) {
        List<QuerySumType> values = Arrays.asList(
                QuerySumType.ofLoading(new Loading()),
                QuerySumType.ofNetworkUnavailable(),
                QuerySumType.ofError(new Error("Error")),
                QuerySumType.ofResults(new Results(Arrays.asList("Apple", "Orange", "Pear"))));

        Main main = new Main();
        for (QuerySumType querySumType : values) {
            querySumType.accept(main);
        }
    }

    @Override
    public void visitLoading(Loading loading) {
        System.out.println(loading.toString());
    }

    @Override
    public void visitResults(Results results) {
        System.out.println(results.toString());
    }

    @Override
    public void visitError(Error error) {
        System.out.println(error.toString());
    }

    @Override
    public void visitNetworkUnavailable() {
        System.out.println("Network unavailable.");
    }
}
