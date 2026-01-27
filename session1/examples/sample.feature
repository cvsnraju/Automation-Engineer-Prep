Feature: Department API

  Scenario: CSE department info
    Given the mock API server is running
    When I GET "/api/cse"
    Then the response status should be 200
    And the response field "facultyCount" should be 28
    And the response field "availableToday" should be 12

  Scenario: IT department info
    Given the mock API server is running
    When I GET "/api/it"
    Then the response status should be 200
    And the response field "facultyCount" should be 18
    And the response field "availableToday" should be 7
