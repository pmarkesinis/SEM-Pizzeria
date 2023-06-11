package pizzeria.order.domain.food;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pizzeria.order.domain.order.Order;
import pizzeria.order.models.GetPricesResponseModel;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The type Food price service
 * Handles the communication with the food microservice to get prices and validate foods
 */
@Service
public class FoodPriceService {
    private final transient RestTemplate restTemplate;

    @Autowired
    /**
     * Instantiates a new Food price service.
     *
     * @param restTemplateBuilder the rest template builder
     */
    public FoodPriceService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public FoodPriceService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Gets food prices from the food microservice
     *
     * @param order the order that we want the prices of
     * @return the food prices
     */
    public GetPricesResponseModel getFoodPrices(Order order) {
        List<Long> ingredients = new ArrayList<>();

        for (Food f: order.getFoods()) {
            ingredients.addAll(f.getExtraIngredients());
            ingredients.addAll(f.getBaseIngredients());
        }

        List<Long> recipes = order.getFoods().stream()
                .map(Food::getRecipeId).collect(Collectors.toList());

        ResponseEntity<GetPricesResponseModel> response = getResponse(ingredients, recipes);

        return extractPriceResponseModel(response);
    }

    private GetPricesResponseModel extractPriceResponseModel(ResponseEntity<GetPricesResponseModel> response) {
        // check response status code
        if (response.getStatusCode() != HttpStatus.OK) return null;

        GetPricesResponseModel responseModel = response.getBody();

        if (responseModel.getFoodPrices() == null) {
            responseModel.setFoodPrices(new HashMap<>());
        }
        if (responseModel.getIngredientPrices() == null) {
            responseModel.setIngredientPrices(new HashMap<>());
        }

        return responseModel;
    }

    private ResponseEntity<GetPricesResponseModel> getResponse(List<Long> ingredients, List<Long> recipes) {
        // create headers
        HttpHeaders headers = new HttpHeaders();
        // set `content-type` header
        headers.setContentType(MediaType.APPLICATION_JSON);
        // set `accept` header
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // create headers
        // create a map for post parameters
        Map<String, Object> map = new HashMap<>();
        map.put("foodIds", recipes);
        map.put("ingredientIds", ingredients);

        // build the request
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

        // send POST request
        return restTemplate.postForEntity("http://localhost:8084/price/ids", entity, GetPricesResponseModel.class);
    }

}
