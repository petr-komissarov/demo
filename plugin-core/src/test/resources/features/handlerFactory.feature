@factory
Feature: handler factory

  Background:
    Given handlers are mocked
    And handlerFactory is mocked

  @db
  Scenario: db -> getHandler
    When run handlerFactory.getHandler with actionType DB
    Then handlerFactory.getHandler should return DB handler
    And handlerFactory.getHandler should be called 1 times

  @http
  Scenario: http -> getHandler
    When run handlerFactory.getHandler with actionType HTTP
    Then handlerFactory.getHandler should return HTTP handler
    And handlerFactory.getHandler should be called 1 times

  @kafka
  Scenario: kafka -> getHandler
    When run handlerFactory.getHandler with actionType KAFKA
    Then handlerFactory.getHandler should return KAFKA handler
    And handlerFactory.getHandler should be called 1 times
