package uk.co.optimisticpanda.atom;

import org.apache.abdera.model.Entry;

import com.sun.jersey.api.client.Client;
import static uk.co.optimisticpanda.atom.EntryFunctions.*;

public class TestDependencies {

    public static void main(String[] args) {
        // Obtain a jersey client
        Client client = new Client();

        // Create Feed Traverser
        FeedTraverser traverser = FeedTraverserBuilder.createFeedTraverser(client)//
                .foundStartingEntryWhen(idEquals("0")) //
                .processEntriesWhich(hasCategory("CREATE")) //
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
