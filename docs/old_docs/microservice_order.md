# commons.Order Microservice
The API allows authorised users to create, edit, delete and list orders.
Below you can find the detailed explanation of how these operations work.

## Create commons.Order
This endpoint receives a request containing an order object and a user token.
It validates the request by checking the authority of the user and the validity of the order object.
If valid, it completes the order object (by calculating price, coupons, id, etc.) and writes it to the database.
After this, the microservice notifies the store about the new order by sending a copy of it.

## Edit commons.Order
This endpoint receives a request containing an order object and a user token.
It validates the request by checking the user authority, timestamp, and order object validity.
If valid, it modifies the database entry with the new completed order object.
It then notifies the store about the change by sending a copy of the order.

## Delete commons.Order
This endpoint receives a request containing an order ID and a user token.
It checks the user authority and, if sufficient, it deletes the database entry.
It then notifies the store about the deletion.

## List Orders
This endpoint receives a request containing a user ID and a user token.
It validates the user identity and sends back a list of orders that belong to that user.
The manager can get all the orders in the database using the special admin endpoint.

## Special Manager Interactions
In case the user is a manager, both the customer and the store need to be notified of an order deletion/change.
The manager can bypass the 30-minute rule regarding order cancellation/editing.
