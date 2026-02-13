Feature: services

  @http
  @services
  Scenario: services.http.post
    Given mock http.post with value -19
    When run http.post
    Then assert http.post return value -19

  @db
  @services
  Scenario: services.db.update
    Given mock db.update with value -20
    When run db.update
    Then assert db.update return value -20

  @kafka
  @services
  Scenario: services.kafka.send
    Given mock kafka.send with value -21
    When run kafka.send
    Then assert kafka.send return value -21
