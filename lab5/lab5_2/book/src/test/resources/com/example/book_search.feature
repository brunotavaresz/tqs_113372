Feature: Book search in the library

  Scenario: Searching for books by author
    Given the following books in the library:
      | title             | author            | published     |
      | "Java Basics"     | "John Doe"        | 2022-05-01    |
      | "Advanced Java"   | "Jane Smith"      | 2021-07-15    |
      | "Learning Java"   | "John Doe"        | 2020-11-20    |
    When the customer searches for books by author "John Doe"
    Then the library should return the following books:
      | title         | author      | published     |
      | "Java Basics" | "John Doe"  | 2022-05-01    |
      | "Learning Java" | "John Doe" | 2020-11-20    |

  Scenario: Searching for books published between two dates
    Given the following books in the library:
      | title             | author         | published     |
      | "Java Basics"     | "John Doe"     | 2022-05-01    |
      | "Advanced Java"   | "Jane Smith"   | 2021-07-15    |
      | "Learning Java"   | "John Doe"     | 2020-11-20    |
    When the customer searches for books published between 2020-01-01 and 2022-01-01
    Then the library should return the following books:
      | title           | author      | published     |
      | "Learning Java" | "John Doe"  | 2020-11-20    |

  Scenario: Searching for books with no results
    Given the following books in the library:
      | title             | author         | published     |
      | "Java Basics"     | "John Doe"     | 2022-05-01    |
      | "Advanced Java"   | "Jane Smith"   | 2021-07-15    |
    When the customer searches for books published between 2019-01-01 and 2019-12-31
    Then the library should return no books
