Feature: Search for Harry Potter book

  Scenario: User searches for "Harry Potter" book
    When I navigate to "https://cover-bookstore.onrender.com/"
    And I search for "Harry Potter"
    And I click submit
    Then the user should see "Harry Potter and the Sorcerer's Stone" in the search results