# Five nights at Annie's

Backend for a pizzeria chain order management solution, implemented using microservices.

Credits go to [Raul Cotar](gitlab.ewi.tudelft.nl/rcotar), [Borislav Semerdzhiev](gitlab.ewi.tudelft.nl/bsemerdzhiev), [Juan Tarazona](https://gitlab.ewi.tudelft.nl/jtarazonarodri), [Laurens Michielsen](gitlab.ewi.tudelft.nl/llmichielsen), [Pavlos Markesinis](gitlab.ewi.tudelft.nl/pmarkesinis), [Francisco Ruas Vaz](gitlab.ewi.tudelft.nl/fruasvaz).

## Abilities

Our solution primarily provides a way for clients to interact with the pizzerias and manage their orders and for the stores to receive these orders. On top of that, it provides a way for regional managers to analyze and do arbitrary changes to the data managed by the system (e.g: orders, coupons).

## Structure

This project contains 4 microservices:
- authentication-microservice
- user-microservice
- food-microservice
- order-microservice

The `authentication-microservice` is responsible for authenticating and authorizing users. After successful authentication, this microservice will provide a JWT token which can be used to prove a user's identity to the other microservices. Users never communicate directly with this server.

The `user-microservice` is responsible for registering and logging in uor clients (and managers). It acts as a proxy for the Auth server, and it also stores user data such as allergens.

The `food-microservice` handles the manu and the available foods and ingredients that are available for purchase. It is relied upon by the frontend as well as the next microservice.

The `order-microservice` is the biggest microservice in our system. It handles order placement/editing/deletion and all the associated checks and functionality. It communicates with the previous 2 microservices in order to ensure that orders are processed correctly. Most of the core business logic is implemented in here (e.g: coupons, price calculation, order placement).

## Testing

Our project has a close to 100% meaningful test coverage, made possible by our extensive suite of unit and integration tests. We want to make users that our clients have a save and reliable solution.

## Running the microservices

You can run the two microservices individually by starting the Spring applications. There are 4 microservices: `Authentication` (start first), `User`, `Food`, and `Order`. You can manually test the system by using [Postman](https://www.postman.com/) to access the API.


## Endpoints
`IF YOU USE THE ENDPOINTS IN AN INCORRECT WAY - YOU WILL GET ERROR CODES(BAD REQUEST, ETC..), THEY USUALLY
CONTAIN ALSO A WARNING MESSAGE WHICH CAN BE USED FOR DEBUGGING. IF YOU DO NOT HAVE A CORRECT JWT TOKEN
YOU GET UNAUTHORISED`


### Authentication Micro-Service
We need to run the authentication Micro-Service first, as we insert 5 managers upon initializing the user microservice.
The customer does not directly access the Authentication Micro-Service, but instead uses the User Micro-Service to accomplish that.

Even though the endpoints are not accessed directly, we should mention their expected parameters.

#### Endpoint - authenticate - POST REQUEST
It expects an AuthenticationRequestModel which includes the id of the user and a non-hashed password
In JSON format <br> 
```
{
    "id":"9f866199-5d70-461a-8f98-1fb3937c3c56",
    "password":"123"
}
```

is an example of a correctly formatted JSON request

#### Endpoint - register - POST REQUEST
It expects a RegistrationRequestModel which includes the id and a non-hashed password of the user<br>

```
{
    "id":"357128e8-cfc2-41f7-bf59-de42a486faea",
    "password":"aas"
}
```

is an example of a correctly formatted JSON request

## User Micro-Service
When we first start the User Micro-Service, we register 5 manager accounts and send a register request to the authentication microservice with their details<br>
The user Micro-Service has the following endpoints, organised in order of a possible workflow

#### Endpoint - create_user - POST REQUEST
This endpoint registers a user with role customer inside our database and sends a register request to Authentication Micro-Service

It expects a UserRegisterModel which contains the email of the user, his corresponding allergies, his name and his password<br>
```
{
    "email":"test2@abv.bg",
    "allergies":["Allergy1", "Allergy2"],
    "name":"Borislav",
    "password":"123"
}
```

is an example of a correctly formatted JSON request

#### Endpoint - login - GET REQUEST
This endpoint allows the user to login, effectively giving him a JWT which is required for interacting with some other endpoints

It expects a LoginModel which contains the email of the user and his non-hashed password<br>

```
{
    "email":"test2@abv.bg",
    "password":"123"
}
```

is an example of a correctly formatted JSON request

#### Endpoint - delete_user - REQUIRES A VALID JWT - DELETE REQUEST

This endpoint allows the user to delete his account, but requires a correct JWT token. It extracts the id from 
it, and deletes the record from the database.

It does not expect anything in the body, but you need a valid JWT in the authorization header

#### Endpoint - update_allergies - REQUIRES A VALID JWT - PUT REQUEST
This endpoint allows the user to change his allergies, provided he puts a valid JWT in the authorization header and also an 
AllergiesModel which contains a list of the NEW allergies<br>

```
{
    "allergies":["Allergy1", "Allergy2"]
}
```

is an example of a correctly formatted JSON request

#### Endpoint - get_allergies - REQUIRES A VALID JWT - GET REQUEST
This endpoint responds with a list of the user allergies, provided there is a valid JWT in the authorization header

It responds with a AllergiesResponseModel which contains a list of the user allergies

A possible response is 

```
{
    "allergies": [
        "Allergy1",
        "Allergy2"
    ]
}
```

## Food Micro-Service
The typical workflow of a food Micro-Service is to first save ingredients, then save recipes, and only after that
the price endpoint can be used to receive the price of the ingredients and also the recipes<br>

And after we have already registered some users we can request their allergies with a correct JWT

### Ingredient related endpoints

#### Endpoint - save - REQUIRES A VALID JWT WITH ROLE MANAGER - POST REQUEST
This endpoint allows a manager to save a new ingredient inside the food database 
if we have first provided a valid manager JWT in the header and also
a SaveIngredientRequestModel inside the body<br>

```
{
    "ingredient" : {
        "name" : "black_olive",
        "price": 0.5,
        "allergens":[]
    }
}
```

is an example of a correctly formatted JSON request

#### Endpoint - update - REQUIRES A VALID JWT WITH ROLE MANAGER - POST REQUEST
This endpoint allows a manager to update an already existing ingredient inside the food database
it requires a valid JWT of a manager and a UpdateIngredientRequestModel, which contains the id of the ingredient
we want to update and an ingredient object

```
{
    "id": 1,
    "ingredient" : {
        "name" : "tomato",
        "price": 0.5,
        "allergens":[]
    }
}
```

is an example of a correctly formatted JSON request

#### Endpoint - delete - REQUIRES A VALID JWT WITH ROLE MANAGER - DELETE REQUEST
This endpoint allows a manager to delete an already existing ingredient inside the food database
It requires a valid JWT of a manager and a DeleteIngredientRequestModel which contains the id of the ingredient

```
{
    "id":"1"
}
```

is an example of a correctly formatted JSON request

#### Endpoint - extraToppings - GET REQUEST

This endpoint allows anyone to get a list OF ALL the toppings in the food database
It requires nothing in the request body and returns a ExtraToppingsResponseModel

```
{
    "ingredients": [
        {
            "id": 3,
            "name": "Sausss6111",
            "price": 5.0,
            "allergens": [
                "a",
                "b",
                "c"
            ]
        }
    ]
}
```

is an example of a correct response

### Price related endpoints

#### Endpoint - ids - POST REQUEST
This is an endpoint used particularly by the order Micro-Service to request the prices of recipes and ingredients
contained in an order

It expects a GetPricesRequestModel which contains a list of food ids and a list of ingredients ids

```
{
    "foodsIds":[],
    "ingredientIds":[3]
}
```

is an example of a correct request

### Allergen related endpoints

#### Endpoint - menu - REQUIRES A VALID JWT - GET REQUEST

This endpoint filters a menu based on the allergies that the user who own the JWT has

It requires a valid JWT 

```
{
    "recipes": [
        {
            "id": 5,
            "name": "no_lactose_margherita",
            "baseToppings": [
                3
            ],
            "basePrice": 8.0,
            "foodType": "PIZZA"
        },
        {
            "id": 6,
            "name": "no_lactose_margherita4",
            "baseToppings": [
                3
            ],
            "basePrice": 8.0,
            "foodType": "PIZZA"
        }
    ]
}
```
is an example of a possible response

#### Endpoint - warn - REQUIRES A VALID JWT - GET REQUEST

This endpoint returns whether the allergens which are contained in the recipe are safe for the user

It requires a valid JWT and a CheckIfRecipeIsSafeRequestModel which contains the id of the recipe

```
{
    "id":6
}
```

is an example of a correct request

and 

```
true
```

is an example of a possible response

### Recipe related endpoints

#### Endpoint - save - REQUIRES A VALID JWT WITH ROLE MANAGER - POST REQUEST
Note: we recommend saving ingredients beforehand so that they can be used in saving a recipe

This endpoint allows a manager to save a recipe inside the food database

It expects a manager JWT and a SaveFoodRequestModel which contains a Recipe object

```
{
    "recipe": {
        "name": "no_lactose_margherita",
        "baseToppings": [1],
        "basePrice": 8.0
    }
}
```

is an example of a correct request

#### Endpoint - update - REQUIRES A VALID JWT WITH ROLE MANAGER - POST REQUEST

This endpoint allows a manager to update an already existing recipe

It expects a manager JWT and a UpdateFoodRequestModel which contains an id of the recipe we want to update
and a Recipe object

```
{
    "id":4,
    "recipe": {
        "name": "test",
        "baseToppings": [3],
        "basePrice": 8.0
    }
}
```

is an example of a correct request

#### Endpoint - delete - REQUIRES A VALID JWT WITH ROLE MANAGER - DELETE REQUEST

This endpoint allows a manager to delete an already existing recipe

It expects a manager JWT and a DeleteFoodRequestModel which contains the recipe id

```
{
    "id":4
}
```

is an example of a correct request

#### Endpoint - menu - GET REQUEST

This endpoint allows anyone to receive a menu with all the existing recipes

It requires nothing in the body

```
{
    "menu": [
        {
            "id": 5,
            "name": "no_lactose_margherita",
            "baseToppings": [
                3
            ],
            "basePrice": 8.0,
            "foodType": "PIZZA"
        },
        {
            "id": 6,
            "name": "no_lactose_margherita4",
            "baseToppings": [
                3
            ],
            "basePrice": 8.0,
            "foodType": "PIZZA"
        }
    ]
}
```

is an example of a possible response

#### Endpoint - getBaseToppings - GET REQUEST

This endpoint allows anyone to receive a list of all the ingredients that a recipe contains

It requires a GetBaseToppingsRequestModel which contains the id of the recipe, we want to learn the ingredients of

```
{
    "recipeId":6
}
```
is an example of a possible request

and

```
{
    "baseToppings": [
        {
            "id": 3,
            "name": "Sausss6111",
            "price": 5.0,
            "allergens": [
                "a",
                "b",
                "c"
            ]
        }
    ]
}
```
is an example of a possible response

## Order Micro-Service

### Coupon related endpoints

#### Endpoint - create - REQUIRES A VALID JWT WITH ROLE MANAGER - POST REQUEST

This endpoint allows a manger with a valid JWT to create a new coupon

It requires a JWT and a CouponModel which contains couponId, percentage(if its a percentage coupon)
and a coupon type("PERCENTAGE" or "TWO_FOR_ONE"")

```
{
    "id":"Example Token",
    "percentage":0.5,
    "type":"PERCENTAGE"
}
```

is an example of a correct percentage coupon request

```
{
    "id":"Example Token Two",
    "type":"TWO_FOR_ONE"
}
```

is an example of a correct two for one coupon request

### Store related endpoints

#### Endpoint - create - REQUIRES A VALID JWT WITH ROLE MANAGER - POST REQUEST

This endpoint allows a manager with a valid JWT to create a store.

It requires a valid JWT and a StoreModel which contains contact info(email) and a location(ex. NL-2624ME)

```
{
    "location": "2526BN",
    "contact": "jjtaracan2@gmail.com"
}
```
is an example of a correct request

`NOTE THAT THIS IS THE EMAIL WE ARE GOING TO NOTIFY WHEN WE PLACE/EDIT/DELETE AN ORDER`

#### Endpoint - edit - REQUIRES A VALID JWT WITH ROLE MANAGER - PUT REQUEST

This endpoint allows a manager with a valid JWT to edit a store

It requires a valid JWT and a StoreModel which contains new contact info(email), new location(ex. NL-2624ME),
and the id of the store we want to update

```
{
    "id":1,
    "location": "NL-2624ME",
    "contact": "tomsfighter@gmail.com"
}
```

is an example of a correct request

#### Endpoint - delete - REQUIRES A VALID JWT WITH ROLE MANAGER - DELETE REQUEST

This endpoint allows a manager to delete an already existing store

It requires a valid JWT and a DeleteStoreModel which contains the id of the store we want to delete

```
{
    "id":1
}
```
is an example of a correct request

#### Endpoint - get_stores - REQUIRES A VALID JWT - GET REQUEST

This endpoint allows anyone with a valid JWT to see all the stores saved in our database

It requires a valid JWT and does not require anything in the body

```
[
    {
        "id": 2,
        "location": "2526BN",
        "contact": "jjtaracan2@gmail.com"
    }
]
```

is an example of a possible response

### Order related endpoints

#### Endpoint - place - REQUIRES A VALID JWT - POST REQUEST
`NOTE THE userID ISNIDE THE ORDER MUST MATCH THE JWT ID`

This endpoint allows anyone with a valid JWT to place an order

It requires a valid JWT and a Order object

```
{
    "foods": [
        {
            "recipeId": 5,
            "baseIngredients": [3],
            "extraIngredients": []
        }
    ],
    "storeId": 2,
    "userId": "91c6bfff-a5c7-4371-9861-cf7d32bb81e9",
    "pickupTime": "2022-12-25 10:25:00",
    "price": 8.0,
    "couponIds": []
}
```

is an example of a possible request

and

```
{
    "orderId": 3,
    "foods": [
        {
            "id": 4,
            "recipeId": 5,
            "baseIngredients": [
                3
            ],
            "extraIngredients": []
        }
    ],
    "storeId": 2,
    "userId": "91c6bfff-a5c7-4371-9861-cf7d32bb81e9",
    "pickupTime": "2022-12-25 10:25:00",
    "price": 8.0,
    "couponIds": []
}
```

is an example of a possible response

an email is sent to the store's email containing

`Order with orderId : X(number of order) has been created`

#### Endpoint - edit - REQUIRES A VALID JWT - POST REQUEST
`NOTE THE userID ISNIDE THE ORDER MUST MATCH THE JWT ID`

This endpoint allows a user to edit an already existing order

It requires a valid JWT and a Order object

```
{
    "orderId": 3,
    "foods": [
        {
            "id": 4,
            "recipeId": 5,
            "baseIngredients": [
                3
            ],
            "extraIngredients": []
        }
    ],
    "storeId": 2,
    "userId": "91c6bfff-a5c7-4371-9861-cf7d32bb81e9",
    "pickupTime": "2023-12-25 10:25:00",
    "price": 8.0,
    "couponIds": []
}
```
is an example of a possible request

and

```
{
    "orderId": 3,
    "foods": [
        {
            "id": 4,
            "recipeId": 5,
            "baseIngredients": [
                3
            ],
            "extraIngredients": []
        }
    ],
    "storeId": 2,
    "userId": "91c6bfff-a5c7-4371-9861-cf7d32bb81e9",
    "pickupTime": "2023-12-25 10:25:00",
    "price": 8.0,
    "couponIds": []
}
```

is an example of a possible response

an email is sent to the store's email containing

`Order with orderId : X(number of order) has been edited`

#### Endpoint - delete - REQUIRES A VALID JWT - POST REQUEST
`NOTE THE userID ISNIDE THE ORDER MUST MATCH THE JWT ID OR YOU CAN BYPASS THAT IF YOU ARE A MANAGER`

This endpoint allows a user to delete an already existing order

It requires a valid JWT and a DeleteModel which contains orderId

```
{
    "orderId": 5
}
```

is an example of a possible request

an email is sent to the store's email containing

`Order with orderId : X(number of order) has been deleted`

#### Endpoint - list - REQUIRES A VALID JWT - GET REQUEST

This endpoint returns all the orders made by the user, who owns the JWT

It requires a valid JWT and nothing in the body

```
{
    "orders": [
        {
            "orderId": 7,
            "foods": [
                {
                    "id": 8,
                    "recipeId": 5,
                    "baseIngredients": [
                        3
                    ],
                    "extraIngredients": []
                }
            ],
            "storeId": 2,
            "userId": "91c6bfff-a5c7-4371-9861-cf7d32bb81e9",
            "pickupTime": "2022-12-25 10:25:00",
            "price": 8.0,
            "couponIds": []
        }
    ]
}
```

is an example of a possible response

#### Endpoint - listAll - REQUIRES A VALID JWT WITH ROLE MANAGER - GET REQUEST

This endpoint returns all the orders in our database

It requires a valid JWT of a manager and nothing in the body

```
{
    "orders": [
        {
            "orderId": 7,
            "foods": [
                {
                    "id": 8,
                    "recipeId": 5,
                    "baseIngredients": [
                        3
                    ],
                    "extraIngredients": []
                }
            ],
            "storeId": 2,
            "userId": "91c6bfff-a5c7-4371-9861-cf7d32bb81e9",
            "pickupTime": "2022-12-25 10:25:00",
            "price": 8.0,
            "couponIds": []
        }
    ]
}
```

is an example of a possible response

### Ending
All the aforementioned end points have been tested extensively not only in unit tests and integration tests, but also 
manually using Postman. 

We provide our postman endpoints in the file named `SEMProject_collection.postman_collection.json`