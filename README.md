atom-archive-traverser
======================

This is a java client for processing an atom feed as described in [this](http://answers.oreilly.com/topic/2153-rest-in-practice-how-to-use-atom-for-event-driven-systems/) article or the awesome [Rest In Practice book](http://www.amazon.co.uk/REST-Practice-Hypermedia-Systems-Architecture/dp/0596805829).
It uses Abdera and Jersey Client (Though it is trivial to create [FeedReaders](https://github.com/plasma147/atom-archive-traverser/blob/master/src/main/java/uk/co/optimisticpanda/atom/reader/FeedReader.java) for other REST/Http clients).

The idea is that it loads an atom feed and then examines each entry in turn until it finds the first entry we wish for it to process.
If it reaches the end of the feed, then it will follow ["prev-archive" links](http://tools.ietf.org/html/rfc5005) to traverse through all the archive feeds that have ever been published. 
When it reaches the entry we are looking for (or the first ever published event), it will then process each event in publication order, following "next-archive" links until it reaches the last published feed.

Creating a new feed traverser is trivial:
```java

import org.apache.abdera.model.Entry;
import com.sun.jersey.api.client.Client;
import static uk.co.optimisticpanda.atom.EntryFunctions.*;

public class TestDependencies {

    public static void main(String[] args) {
        // Obtain a jersey client
        Client client = new Client();

        // Create Feed Traverser
        FeedTraverser traverser = FeedTraverserBuilder.createFeedTraverser(client)//
                
                // Start processing entries that occured after the entry with an id of 0
                .foundLastProcessedEntryWhen(idEquals("0")) //
                
                // Filter the feed to only process entries that have the CREATE category
                .processEntriesWhich(haveACategoryOf("CREATE")) //
                
                // Print out each matching entry
                .whenFound(printEntryDetails) //
                
                .build();
        
        // Traverse Feed
        traverser.traverse("http://localhost:8080/service/notifications/");
    }

    // EntryVisitor that just prints out the details of each visited entry.  
    private static EntryVisitor printEntryDetails = new EntryVisitor() {
        public void visit(Entry entry) {
            System.out.printf("\t%s\t:\t%s\n", entry.getId(), entry.getTitle());
        }
    };
}

```
