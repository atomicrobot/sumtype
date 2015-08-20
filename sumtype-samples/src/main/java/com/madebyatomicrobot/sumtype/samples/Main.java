package com.madebyatomicrobot.sumtype.samples;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.madebyatomicrobot.sumtype.samples.QuerySumType.QuerySumTypeVisitor;

import java.util.Arrays;

public class Main {
    public static void main(String[] argv) {
        new Main().runShowDemo();
    }

    private EventBus bus = new EventBus();

    private void runShowDemo() {
        bus.register(this);

        bus.post(QuerySumType.ofNetworkUnavailable());
        bus.post(QuerySumType.ofLoading(new Loading()));
        bus.post(QuerySumType.ofProgress(50));  // SUPPRESS CHECKSTYLE MagicNumber
        bus.post(QuerySumType.ofError(new Error("Oh snap!")));
        bus.post(QuerySumType.ofResults(new Results(Arrays.asList("Apple", "Orange", "Pear"))));
    }

    @Subscribe
    public void handleQuerySumType(QuerySumType querySumType) {
        querySumType.accept(new QuerySumTypeVisitor() {
            @Override
            public void visitLoading(Loading loading) {
                System.out.println(loading.toString());
            }

            @Override
            public void visitProgress(int progress) {
                System.out.println("Progress: " + progress);
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
        });
    }
}
