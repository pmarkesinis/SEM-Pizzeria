# Architecture Draft meeting 29th Nov 2022

- Participants: all except Pavlos (personal matter)
- Time: 13h45 to 16h00
- Focus: assignment 1

## First points

- we need to get the bounded contexts and requirements ready for the TA meeting thursday
- meet for starting on the UML diagrams thursday morning before TA meeting
- determine the points for the agenda on thursday (questions)
- agree on a common grade for trying to get in the project, 8.5

## Bounded contexts

- coupons: better to have them be a different context (scalable)
- stores: chain of stores (each store receives different orders)
- user selects a store at which to pickup, if a store has too many orders it is not available as an option
- question: do the ingredients relate to the store? in terms of supply
- what do we save in the DB: ingredients
- per pizza set of ingredients default + personalized, price

### Relationships:

- user to auth
- user to order
- user to pizza
- ingredient to pizza
- coupon to order
- order to store
- (store to pizza)

## Microservices

- for each microservice we need 20 class files
- a lot of complexity and a lot of testing -> make less but bigger microservices, say 5

We make the following microservices:
- user
- order
- pizza + ingredients
- store
- coupon
- authentication