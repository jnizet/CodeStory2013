package com.ninja_squad.jb.codestory.action;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Sets;
import com.ninja_squad.jb.codestory.Action;
import com.ninja_squad.jb.codestory.ContentTypes;
import com.ninja_squad.jb.codestory.HttpHeaders;
import com.ninja_squad.jb.codestory.HttpRequest;
import com.ninja_squad.jb.codestory.HttpResponse;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Set;

/**
 * Skalaskel action. Returns all the possible changes in JSON format for an amount between 1 and 100.
 * @author JB
 */
public class SkalaskelAction implements Action {

    @Override
    public HttpResponse execute(HttpRequest request) {
        String amountAsString = request.getPath().substring(request.getPath().lastIndexOf('/') + 1);
        try {
            int amount = Integer.parseInt(amountAsString);
            if (amount > 0 && amount <= 100) {
                return executeSkalaskel(amount);
            }
        }
        catch (NumberFormatException e) {
            // ignore : send bad request
        }
        return HttpResponse.badRequest("Bad amount: " + amountAsString);
    }

    private HttpResponse executeSkalaskel(int amount) {
        Set<Change> changes = change(amount);
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        Joiner.on(',').appendTo(builder, FluentIterable.from(changes).transform(Change.TO_JSON));
        builder.append(']');
        return new HttpResponse(HttpResponse.Status._200_OK,
                                HttpHeaders.builder()
                                           .setContentType(ContentTypes.APPLICATION_JSON, StandardCharsets.US_ASCII)
                                           .build(),
                                builder.toString().getBytes(StandardCharsets.US_ASCII));
    }

    @VisibleForTesting
    protected Set<Change> change(int amount) {
        Set<Change> result = Sets.newHashSet();
        Change init = new Change(0, 0, 0, 0);
        change(init, amount, result);
        return result;
    }

    private void change(Change init, int amount, Set<Change> result) {
        if (amount >= 21) {
            change(init.incrementBaz(), amount - 21, result);
        }
        if (amount >= 11) {
            change(init.incrementQix(), amount - 11, result);
        }
        if (amount >= 7) {
            change(init.incrementBar(), amount - 7, result);
        }
        result.add(init.incrementFoo(amount));
    }

    public static final class Change {
        public static final Function<Change, String> TO_JSON =
            new Function<Change, String>() {
                @Override
                public String apply(Change input) {
                    return input.toJSON();
                }
            };

        private final int baz;
        private final int qix;
        private final int bar;
        private final int foo;

        public Change(int baz, int qix, int bar, int foo) {
            this.baz = baz;
            this.qix = qix;
            this.bar = bar;
            this.foo = foo;
        }

        public Change incrementBaz() {
            return new Change(baz + 1, qix, bar, foo);
        }

        public Change incrementQix() {
            return new Change(baz, qix + 1, bar, foo);
        }

        public Change incrementBar() {
            return new Change(baz, qix, bar + 1, foo);
        }

        public Change incrementFoo(int count) {
            return new Change(baz, qix, bar, foo + count);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Change change = (Change) o;

            return qix == change.qix
                   && bar == change.bar
                   && baz == change.baz
                   && foo == change.foo;
        }

        @Override
        public int hashCode() {
            return Objects.hash(baz, qix, bar, foo);
        }

        @Override
        public String toString() {
            return "[" + baz + ", " + qix + ", " + bar + ", " + foo + "]";
        }

        public String toJSON() {
            return "{\"baz\":\""
                   + baz
                   + "\";\"qix\":\""
                   + qix
                   + "\";\"bar\":\""
                   + bar
                   + "\";\"foo\":\""
                   + foo
                   + "\"}";
        }
    }
}
