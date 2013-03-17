package uk.co.optimisticpanda.atom;

import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.in;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.abdera.model.Category;
import org.apache.abdera.model.Entry;

import com.google.common.base.Predicate;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;

public enum EntryFunctions {
    ;

    /** If any category is present then return true */
    public static Predicate<Entry> hasCategory(String... categories) {
        final List<String> list = Arrays.asList(categories);
        return new Predicate<Entry>() {
            public boolean apply(Entry input) {
                for (Category category : input.getCategories()) {
                    if (in(list).apply(category.getTerm())) {
                        return true;
                    }
                }
                return false;
            }
        };
    };

    /** If any category is present then return false */
    public static Predicate<Entry> dontHaveCategory(String... categories) {
        final List<String> list = Arrays.asList(categories);
        return new Predicate<Entry>() {
            public boolean apply(Entry input) {
                for (Category category : input.getCategories()) {
                    if (in(list).apply(category.getTerm())) {
                        return false;
                    }
                }
                return true;
            }
        };
    };

    /** Return true if we have an entry that has an id that matches this*/
    public static Predicate<Entry> idEquals(final String id) {
        return new Predicate<Entry>() {
            public boolean apply(Entry input) {
                if (input.getId() != null) {
                    return id.equals(input.getId().toString());
                } else {
                    return false;
                }
            }
        };
    };

    /** Return true if we have an entry that has content that matches this mediatype*/
    public static Predicate<Entry> contentHasMediaType(final MediaType mediaType) {
        return new Predicate<Entry>() {
            public boolean apply(Entry input) {
                return equalTo(mediaType.toString()).apply(input.getContentMimeType().toString());
            }
        };
    };

    /** Order entries by updatedDate - earliest to latest*/
    public static Ordering<Entry> earliestToLatest() {
        return new Ordering<Entry>() {
            public int compare(Entry o1, Entry o2) {
                return ComparisonChain.start() //
                        .compare(o1.getUpdated(), o2.getUpdated(), Ordering.natural().nullsLast()) //
                        .result();
            }
        };
    }

    /** Order entries by updatedDate - latest to earliest*/
    public static Ordering<Entry> latestToEarliest() {
        return earliestToLatest().reverse();
    }

    /** Do not order entries*/
    public static Ordering<Entry> naturalOrdering() {
        return new Ordering<Entry>() {
            @Override
            public int compare(Entry left, Entry right) {
                return 0;
            }
        };
    }
}
