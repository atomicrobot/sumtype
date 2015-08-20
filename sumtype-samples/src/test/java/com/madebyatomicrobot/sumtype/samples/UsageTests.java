package com.madebyatomicrobot.sumtype.samples;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class UsageTests {
    @Test
    public void testVisitor() {
        QuerySumType querySumType = QuerySumType.ofLoading(new Loading());
        querySumType.accept(new FailByDefaultVisitor() {
            @Override
            public void visitLoading(Loading loading) {
                // Not failing
            }
        });

        querySumType = QuerySumType.ofProgress(50);
        querySumType.accept(new FailByDefaultVisitor() {
            @Override
            public void visitProgress(int progress) {
                // Not failing
            }
        });

        querySumType = QuerySumType.ofResults(new Results(Arrays.asList("A", "B")));
        querySumType.accept(new FailByDefaultVisitor() {
            @Override
            public void visitResults(Results results) {
                // Not failing
            }
        });

        querySumType = QuerySumType.ofError(new Error("error"));
        querySumType.accept(new FailByDefaultVisitor() {
            @Override
            public void visitError(Error error) {
                // Not failing
            }
        });


        querySumType = QuerySumType.ofNetworkUnavailable();
        querySumType.accept(new FailByDefaultVisitor() {
            @Override
            public void visitNetworkUnavailable() {
                // Not failing
            }
        });
    }

    @Test
    public void testNoValueState() {
        QuerySumType querySumType = QuerySumType.ofNetworkUnavailable();
        querySumType.networkUnavailable();  // Should be safe to call even though it doesn't do anything
    }

    @Test
    public void testCreateWithNull() {
        try {
            QuerySumType.ofLoading(null);
            fail();
        } catch (IllegalArgumentException ex) {
            // Yay!
        }
    }

    @Test
    public void testInappropriateAccess() {
        QuerySumType querySumType = QuerySumType.ofNetworkUnavailable();
        try {
            querySumType.loading();
            fail();
        } catch (IllegalStateException ex) {
            // Yay!
        }
    }

    @Test
    public void testPrimitive() {
        QuerySumType querySumType = QuerySumType.ofProgress(50);
        assertEquals(50, querySumType.progress());
    }

    private static class FailByDefaultVisitor implements QuerySumType.QuerySumTypeVisitor {
        @Override
        public void visitLoading(Loading loading) {
            fail();
        }

        @Override
        public void visitProgress(int progress) {
            fail();
        }

        @Override
        public void visitResults(Results results) {
            fail();
        }

        @Override
        public void visitError(Error error) {
            fail();
        }

        @Override
        public void visitNetworkUnavailable() {
            fail();
        }
    }
}
