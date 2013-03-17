package uk.co.optimisticpanda.atom.reader;

import org.apache.abdera.model.Feed;

import com.google.common.base.Optional;

/**
 * An object that loads and deserializes feeds. 
 */
public interface FeedReader {

    /**
     * Load a feed from a specific location
     */
    Feed load(String location);
    
    /**
     * Load the next archive in relation to the passed in feed
     */
    Optional<Feed> getNextArchive(Feed feed);
    
    /**
     * Load the previous archive in relation to the passed in feed
     */
    Optional<Feed> getPreviousArchive(Feed feed);
    
}
