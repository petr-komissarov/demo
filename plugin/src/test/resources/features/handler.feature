Feature: handler

  @http
  @handler
  Scenario: handler.processEvent.http
    Given mock services with values:
      | HTTP  | -10 |
      | DB    | -11 |
      | KAFKA | -12 |
    When run handler.processEvent with actionType "HTTP"
    Then assert handler.processEvent return value -10

  @db
  @handler
  Scenario: handler.processEvent.db
    Given mock services with values:
      | HTTP  | -13 |
      | DB    | -14 |
      | KAFKA | -15 |
    When run handler.processEvent with actionType "DB"
    Then assert handler.processEvent return value -14

  @kafka
  @handler
  Scenario: handler.processEvent.kafka
    Given mock services with values:
      | HTTP  | -16 |
      | DB    | -17 |
      | KAFKA | -18 |
    When run handler.processEvent with actionType "KAFKA"
    Then assert handler.processEvent return value -18
