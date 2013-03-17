package uk.co.optimisticpanda.atom;

import static java.util.Collections.sort;
import static org.junit.Assert.assertEquals;
import static uk.co.optimisticpanda.atom.EntryFunctions.contentHasMediaType;
import static uk.co.optimisticpanda.atom.EntryFunctions.dontHaveCategory;
import static uk.co.optimisticpanda.atom.EntryFunctions.earliestToLatest;
import static uk.co.optimisticpanda.atom.EntryFunctions.hasCategory;
import static uk.co.optimisticpanda.atom.EntryFunctions.idEquals;
import static uk.co.optimisticpanda.atom.EntryFunctions.latestToEarliest;
import static uk.co.optimisticpanda.atom.TestEntryBuilder.create;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.abdera.model.Entry;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
public class EntryFunctionsTest {

    @Test
    public void checkLatestToEarliestComparing(){
        Entry entry1 = create("1").updatedOn("2013-03-10T00:00:00.001Z").instance();
        Entry entry2 = create("2").updatedOn("2013-03-10T00:00:00.002Z").instance();
        Entry entry3 = create("3").updatedOn("2013-03-10T00:00:00.003Z").instance();
        
        assertEquals("3,2,1", titlesOf(sortBy(latestToEarliest(), entry1, entry2, entry3)));
        assertEquals("3,2,1", titlesOf(sortBy(latestToEarliest(), entry1, entry3, entry2)));
        assertEquals("3,2,1", titlesOf(sortBy(latestToEarliest(), entry3, entry1, entry2)));
        assertEquals("3,2,1", titlesOf(sortBy(latestToEarliest(), entry3, entry2, entry1)));
        assertEquals("3,2,1", titlesOf(sortBy(latestToEarliest(), entry2, entry1, entry3)));
        assertEquals("3,2,1", titlesOf(sortBy(latestToEarliest(), entry2, entry3, entry1)));
    }
 
    @Test
    public void checkEarliestToLatestComparing(){
        Entry entry1 = create("1").updatedOn("2013-03-10T00:00:00.001Z").instance();
        Entry entry2 = create("2").updatedOn("2013-03-10T00:00:00.002Z").instance();
        Entry entry3 = create("3").updatedOn("2013-03-10T00:00:00.003Z").instance();
        
        assertEquals("1,2,3", titlesOf(sortBy(earliestToLatest(), entry1, entry2, entry3)));
        assertEquals("1,2,3", titlesOf(sortBy(earliestToLatest(), entry1, entry3, entry2)));
        assertEquals("1,2,3", titlesOf(sortBy(earliestToLatest(), entry3, entry1, entry2)));
        assertEquals("1,2,3", titlesOf(sortBy(earliestToLatest(), entry3, entry2, entry1)));
        assertEquals("1,2,3", titlesOf(sortBy(earliestToLatest(), entry2, entry1, entry3)));
        assertEquals("1,2,3", titlesOf(sortBy(earliestToLatest(), entry2, entry3, entry1)));
    }

    @Test
    public void checkNullsDontKillDateComparison(){
        Entry entry1 = create("1").updatedOn(null).instance();
        Entry entry2 = create("2").updatedOn("2013-03-10T00:00:00.002Z").instance();
        assertEquals("2,1", titlesOf(sortBy(earliestToLatest(), entry2, entry1)));
        assertEquals("1,2", titlesOf(sortBy(latestToEarliest(), entry2, entry1)));
    }
    
    @Test
    public void checkIdEqualsPredicate(){
        Entry entry1 = create("1").withId("1").instance();
        Entry entry2 = create("2").withId("2").instance();
        Entry entry3 = create("3").withId("3").instance();
        
        assertEquals("1", titlesOf(filterBy(idEquals("1"), entry1, entry2, entry3)));
        assertEquals("2", titlesOf(filterBy(idEquals("2"), entry1, entry2, entry3)));
        assertEquals("3", titlesOf(filterBy(idEquals("3"), entry1, entry2, entry3)));
    }
    
    @Test
    public void checkHasCategoryPredicate(){
        Entry entry1 = create("1").withCategories("A", "B").instance();
        Entry entry2 = create("2").withCategory("B").instance();
        Entry entry3 = create("3").withCategories("A", "B", "C").instance();

        assertEquals("1,3", titlesOf(filterBy(hasCategory("A"), entry1, entry2, entry3)));
        assertEquals("1,2,3", titlesOf(filterBy(hasCategory("B"), entry1, entry2, entry3)));
        assertEquals("3", titlesOf(filterBy(hasCategory("C"), entry1, entry2, entry3)));
        assertEquals("1,2,3", titlesOf(filterBy(hasCategory("B","C"), entry1, entry2, entry3)));
        assertEquals("1,2,3", titlesOf(filterBy(hasCategory("A", "B","C"), entry1, entry2, entry3)));
    }
    
    @Test
    public void checkDontHaveCategoryPredicate(){
        Entry entry1 = create("1").withCategories("A", "B").instance();
        Entry entry2 = create("2").withCategory("B").instance();
        Entry entry3 = create("3").withCategories("A", "B", "C").instance();

        assertEquals("2", titlesOf(filterBy(dontHaveCategory("A"), entry1, entry2, entry3)));
        assertEquals("", titlesOf(filterBy(dontHaveCategory("B"), entry1, entry2, entry3)));
        assertEquals("1,2", titlesOf(filterBy(dontHaveCategory("C"), entry1, entry2, entry3)));
        assertEquals("", titlesOf(filterBy(dontHaveCategory("B","C"), entry1, entry2, entry3)));
        assertEquals("", titlesOf(filterBy(dontHaveCategory("A", "B","C"), entry1, entry2, entry3)));
    }
    
    @Test
    public void checkContentHasMediaTypePredicate(){
        Entry entry1 = create("1").withMediaType(MediaType.TEXT_HTML).instance();
        Entry entry2 = create("2").withMediaType(MediaType.APPLICATION_JSON).instance();
        Entry entry3 = create("3").withMediaType(MediaType.TEXT_HTML).instance();

        assertEquals("2", titlesOf(filterBy(contentHasMediaType(MediaType.APPLICATION_JSON_TYPE), entry1, entry2, entry3)));
        assertEquals("1,3", titlesOf(filterBy(contentHasMediaType(MediaType.TEXT_HTML_TYPE), entry1, entry2, entry3)));
        assertEquals("", titlesOf(filterBy(contentHasMediaType(MediaType.APPLICATION_SVG_XML_TYPE), entry1, entry2, entry3)));
    }
    
    private static <D> List<D> sortBy(Comparator<? super D> comparator, D... entries){
        List<D> list = Lists.newArrayList(entries);
        sort(list, comparator);
        return list;
    }
    
    private static <D> Collection<D> filterBy(Predicate<? super D> predicate, D... entries){
        List<D> list = Lists.newArrayList(entries);
        return Collections2.filter(list, predicate);
    }
    
    private static String titlesOf(Collection<Entry> entries){
        Collection<String> titles = Collections2.transform(entries, new Function<Entry, String>() {
            public String apply(Entry input) {
                return input.getTitle();
            }
        });
        return Joiner.on(",").join(titles);
    }
}
