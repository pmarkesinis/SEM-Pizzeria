package pizzeria.food.communication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;
import pizzeria.food.models.allergens.GetAllergiesFromUserResponseModel;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"test"})
class HttpRequestServiceTest {

    private transient HttpRequestService httpRequestService;
    private transient RestTemplate restTemplate;

    @BeforeEach
    void setup(){
        restTemplate = Mockito.mock(RestTemplate.class);
        httpRequestService = new HttpRequestService(restTemplate);
    }
    @Test
    void getUserAllergens() {
        List<String> allergens = List.of("gluten", "lactose");
        String token = "a b";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.split(" ")[1]);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(headers);
        GetAllergiesFromUserResponseModel responseModel = new GetAllergiesFromUserResponseModel();
        responseModel.setAllergies(allergens);
        ResponseEntity<GetAllergiesFromUserResponseModel> response = ResponseEntity.status(HttpStatus.OK).body(responseModel);
        when(restTemplate.exchange("http://localhost:8083/allergies/get_allergies", HttpMethod.GET, entity, GetAllergiesFromUserResponseModel.class)).thenReturn(response);
        Optional<List<String>> result = httpRequestService.getUserAllergens(token);
        assertTrue(result.isPresent());
        assertEquals(allergens, result.get());
    }

    @Test
    void getUserAllergensBadRequest(){
        String token = "a b";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.split(" ")[1]);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<GetAllergiesFromUserResponseModel> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        when(restTemplate.exchange("http://localhost:8083/allergies/get_allergies", HttpMethod.GET, entity, GetAllergiesFromUserResponseModel.class)).thenReturn(response);
        Optional<List<String>> result = httpRequestService.getUserAllergens(token);
        assertFalse(result.isPresent());
    }
}