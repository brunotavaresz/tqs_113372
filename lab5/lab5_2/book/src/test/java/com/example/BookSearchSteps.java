package com.example;

import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.datatable.DataTable;
import static org.junit.jupiter.api.Assertions.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class BookSearchSteps {

    private Library library = new Library();
    private List<Book> result;

    @ParameterType(".*")
    public Date iso8601Date(String dateStr) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.parse(dateStr);
    }

    @Given("the following books in the library:")
    public void givenTheFollowingBooksInTheLibrary(DataTable table) throws Exception {
        List<Map<String, String>> rows = table.asMaps(String.class, String.class);
        for (Map<String, String> row : rows) {
            String title = row.get("title");
            String author = row.get("author");
            Date published = new SimpleDateFormat("yyyy-MM-dd").parse(row.get("published"));
            library.addBook(new Book(title, author, published));
        }
    }

    @When("the customer searches for books by author {string}")
    public void whenCustomerSearchesForBooksByAuthor(String author) {
        result = library.findBooksByAuthor(author);
    }

    @When("the customer searches for books published between {iso8601Date} and {iso8601Date}")
    public void whenCustomerSearchesForBooksPublishedBetweenDates(Date from, Date to) {
        result = library.findBooks(from, to);
    }

    @Then("the library should return the following books:")
    public void thenLibraryShouldReturnTheFollowingBooks(DataTable table) throws Exception {
        List<Map<String, String>> expectedBooks = table.asMaps(String.class, String.class);
        for (Map<String, String> expected : expectedBooks) {
            String expectedTitle = expected.get("title");
            String expectedAuthor = expected.get("author");
            Date expectedPublished = new SimpleDateFormat("yyyy-MM-dd").parse(expected.get("published"));

            Book foundBook = library.findBookByTitle(expectedTitle);

            assertNotNull(foundBook, "Expected book with title '" + expectedTitle + "' not found.");

            assertEquals(expectedAuthor, foundBook.getAuthor(), "Author mismatch for book: " + expectedTitle);
            
            assertEquals(expectedPublished.getTime(), foundBook.getPublished().getTime(), "Publication date mismatch for book: " + expectedTitle);
        }
    }

    @Then("the library should return no books")
    public void thenLibraryShouldReturnNoBooks() {
        assertTrue(result.isEmpty(), "Expected no books, but found some");
    }
}
