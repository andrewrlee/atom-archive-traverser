package uk.co.optimisticpanda.atom;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.co.optimisticpanda.atom.FeedTraverserBuilder.createFeedTraverser;
import static uk.co.optimisticpanda.atom.TestEntryBuilder.create;

import java.util.List;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.junit.Test;

import uk.co.optimisticpanda.atom.reader.FeedReader;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;

public class FeedTraverserTest {

    @Test
    public void checkFeedNoArchive() {
        // Given
        FeedReader reader = mock(FeedReader.class);
        TestEntryVisitor visit = new TestEntryVisitor();
        FeedTraverser traverser = createFeedTraverser(reader).whenFound(visit).build();

        // When
        when(reader.load(anyString())).thenReturn(feedOfRecentEvents(1));
        when(reader.getPreviousArchive(any(Feed.class))).thenReturn(Optional.<Feed> absent());
        when(reader.getNextArchive(any(Feed.class))).thenReturn(Optional.<Feed> absent());
        traverser.traverse("feed url");

        // Then
        assertEquals("1:1|1:2|1:3", visit.getVisited());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void checkFeedWithSingleArchive() {
        // Given
        FeedReader reader = mock(FeedReader.class);
        TestEntryVisitor visit = new TestEntryVisitor();
        FeedTraverser traverser = createFeedTraverser(reader).whenFound(visit).build();
        Feed feedOfRecentEvents = feedOfRecentEvents(2);

        // When
        when(reader.load(anyString())).thenReturn(feedOfRecentEvents);
        // We have one previous feed and then no further
        when(reader.getPreviousArchive(any(Feed.class))).thenReturn( //
                Optional.of(archiveFeed(1)), //
                Optional.<Feed> absent());

        // When we traverse back to the most recent entry, we go straight to the
        // working feed.
        when(reader.getNextArchive(any(Feed.class))).thenReturn(//
                Optional.of(workingFeed(2)), //
                Optional.<Feed> absent());

        traverser.traverse("feed url");

        // Then
        assertEquals("1:1|1:2|1:3|2:1|2:2|2:3", visit.getVisited());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void checkFeedWithLargeHistory() {
        // Given
        FeedReader reader = mock(FeedReader.class);
        TestEntryVisitor visit = new TestEntryVisitor();
        FeedTraverser traverser = createFeedTraverser(reader).whenFound(visit).build();

        // When
        when(reader.load(anyString())).thenReturn(feedOfRecentEvents(4));
        // We travel back through several archived feeds
        when(reader.getPreviousArchive(any(Feed.class))).thenReturn( //
                Optional.of(archiveFeed(3)), //
                Optional.of(archiveFeed(2)), //
                Optional.of(archiveFeed(1)), //
                Optional.<Feed> absent());

        // When we traverse back to the most recent entry, we go through several
        // archived feeds until we reach the working feed.
        when(reader.getNextArchive(any(Feed.class))).thenReturn(//
                Optional.of(archiveFeed(2)), //
                Optional.of(archiveFeed(3)), //
                Optional.of(workingFeed(4)), //
                Optional.<Feed> absent());

        traverser.traverse("feed url");

        // Then
        assertEquals("1:1|1:2|1:3|2:1|2:2|2:3|3:1|3:2|3:3|4:1|4:2|4:3", visit.getVisited());
    }

    private Feed workingFeed(int i ) {
        return getFeed(i);
    }

    // For all intents and purposes the feed of recent events acts the same as
    // the working feed
    private Feed feedOfRecentEvents(int i) {
        return workingFeed(i);
    }

    private Feed archiveFeed(int i) {
        return getFeed(i);
    }

    private Feed getFeed(int i) {
        Feed feed = Abdera.getInstance().newFeed();
        feed.addEntry(create(i + ":1").updatedOn("2013-0" + i + "-10T00:00:00.001Z").instance());
        feed.addEntry(create(i + ":2").updatedOn("2013-0" + i + "-10T00:00:00.002Z").instance());
        feed.addEntry(create(i + ":3").updatedOn("2013-0" + i + "-10T00:00:00.003Z").instance());
        return feed;
    }

    private class TestEntryVisitor implements EntryVisitor {
        private List<Entry> entries = newArrayList();

        public void visit(Entry entry) {
            entries.add(entry);
        }

        public String getVisited() {
            return Joiner.on('|').join(transform(entries, new Function<Entry, String>() {
                public String apply(Entry input) {
                    return input.getTitle();
                }
            }));
        }
    }

}
