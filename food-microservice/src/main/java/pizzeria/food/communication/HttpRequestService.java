package pizzeria.food.communication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pizzeria.food.models.allergens.GetAllergiesFromUserResponseModel;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class HttpRequestService {

    private final transient RestTemplate restTemplate;

    @Autowired
    public HttpRequestService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    /**
     * @param restTemplate The restTemplate that is used to send the request
     */
    public HttpRequestService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * @param token The token of the user
     * @return the list of the allergens of the user associated with the JWT token from the user ms.
     */
    public Optional<List<String>> getUserAllergens(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.split(" ")[1]);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<GetAllergiesFromUserResponseModel> response = this.restTemplate.exchange("http://localhost:8083/allergies/get_allergies", HttpMethod.GET, entity, GetAllergiesFromUserResponseModel.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return Optional.of(response.getBody().getAllergies());
        } else {
            return Optional.empty();
        }
    }
}
