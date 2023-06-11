# Requirements

## Functional Requirements

### Must Have Requirements

* Customers must be able to create an account.
* Customers must be able to place orders.
* Stores must be notified when a customer places an order.
* Stores must be notified when a customer cancels an order.
* The chain must be able to upload the pizzas they offer.
* The chain must be able to upload the default topping set they offer.
* The chain must be able to edit the pizzas and the topping set they offer.

### Should Have Requirements

* Customers should be able to indicate their allergies.
* Customers should be warned when they add a pizza that contains one of their allergies.
* Customers should be able to filter out the pizzas containing their allergies.
* Customers should be able to create custom pizzas.
* Customers should be able to add toppings from a default set of toppings.
* Customers should be able to add coupon codes to their order.
* Customers should be able to edit their order up until 30 minutes before pickup time.
* Customers should be able to cancel their order up until 30 minutes before pickup time.
* Stores should be able to upload custom coupon codes.
* The system should select the coupon code that offers the largest discount in case that multiple coupons are passed through.

### Could Have Requirements

* Coupons could have expiration dates.
* The regional manager could be able to see all currently placed orders.
* The regional manager could be able to delete any selection of orders.
* Stores could have opening times.

### Won't Have Requirements

* The system won't have a GUI.


## Non-functional Requirements

* The system must be easily extendable through the use of microservices.
* The system must allow for easy integration with other systems through the use of an API.
* The individual components of the system must be scalable by being implemented using microservices.
* The system should be written in the Java programming language using version 11.
* All the interactions with the system are handled using an API.
* The system should be build using Gradle and Spring boot.
* Users have a unique id that is a string.
* Users' passwords should be stored safely.
* commons.Coupon codes should have an activation code that consists of firstly 4 alphabetical characters followed by 2 numbers.
* The system must support two types of coupon codes: default x% off and buy-one-get-one-free.

