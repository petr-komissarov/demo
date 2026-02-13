@handlers
Feature: handlers

  @db
  Scenario: db -> processEvent
    Given dbHandler.processEvent is mocked
    When run processEvent
    Then processEvent should be called 1 times

  @http
  Scenario: http -> processEvent
    Given httpHandler.processEvent is mocked
    When run processEvent
    Then processEvent should be called 1 times

  @kafka
  Scenario: kafka -> processEvent
    Given kafkaHandler.processEvent is mocked
    When run processEvent
    Then processEvent should be called 1 times
