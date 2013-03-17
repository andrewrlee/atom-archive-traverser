package uk.co.optimisticpanda.atom;

import javax.ws.rs.core.MediaType;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Entry;

public class TestEntryBuilder {

    private String id = "";
    private static Abdera abdera = Abdera.getInstance();
    private final String title;
    private String updated = "2013-03-10T00:00:00.001Z";
    private String mediaType = MediaType.APPLICATION_JSON;
    private String[] categories = new String[0];

    public TestEntryBuilder(String title) {
        this.title= title;
    }

    public static TestEntryBuilder create(String title) {
        return new TestEntryBuilder(title);
    }

    public TestEntryBuilder updatedOn(String updated){
        this.updated = updated;
        return this;
    }
    
    public TestEntryBuilder withMediaType(MediaType mediaType){
        this.mediaType= mediaType.toString();
        return this;
    }
    
    public TestEntryBuilder withCategories(String... categories){
        this.categories = categories;
        return this;
    }
    public TestEntryBuilder withCategory(String category){
        return withCategories(category);
    }
    
    public TestEntryBuilder withId(String id){
        this.id= id;
        return this;
    }
    
    public TestEntryBuilder withMediaType(String mediaType){
        this.mediaType= mediaType;
        return this;
    }
    
    public Entry instance(){
        Entry newEntry = abdera.newEntry();
        newEntry.setId(id);
        newEntry.setTitle(title);
        newEntry.setUpdated(updated);
        newEntry.setContent("", mediaType);
        for (String category : categories) {
            newEntry.addCategory(category);
        }
        return newEntry;
    }
    
}
