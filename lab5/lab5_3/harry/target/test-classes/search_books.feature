Feature: Search for Books in the Online Library

  As a user of the online library,
  I want to search for books,
  So that I can find specific titles and authors.

  Scenario: Search for a book by title
    Given I am on the online library homepage
    When I search for "Harry Potter"
    Then I should see a book titled "Harry Potter and the Sorcerer's Stone" by "J.K. Rowling"

  Scenario: Search for a book that does not exist
    Given I am on the online library homepage
    When I search for "Nonexistent Book Title"
    Then I should see a message saying "No results found"

  Scenario: Search with an empty input
    Given I am on the online library homepage
    When I search with an empty query
    Then I should see a message prompting me to enter a search term
