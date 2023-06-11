package pizzeria.user.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import pizzeria.user.authentication.AuthManager;
import pizzeria.user.authentication.JwtTokenVerifier;
import pizzeria.user.communication.HttpRequestService;
import pizzeria.user.domain.user.User;
import pizzeria.user.domain.user.UserRepository;
import pizzeria.user.integration.utils.JsonUtil;
import pizzeria.user.models.AllergiesModel;
import pizzeria.user.models.LoginModel;
import pizzeria.user.models.LoginResponseModel;
import pizzeria.user.models.UserRegisterModel;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockHttpRequestService", "mockTokenVerifier", "mockAuthenticationManager"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class UsersTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private transient UserRepository userRepository;

    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;

    @Autowired
    private transient HttpRequestService httpRequestService;

    @Autowired
    private transient AuthManager mockAuthManager;

    @Test
    public void register_withValidData_worksCorrectly() throws Exception {
        // Arrange
        final String testPassword = "password123";
        final String testEmail = "Borislav@gmail.com";
        final String testName = "borislav";
        final List<String> testAllergies = List.of("Allergy");

        UserRegisterModel model = new UserRegisterModel();
        model.setPassword(testPassword);
        model.setEmail(testEmail);
        model.setName(testName);
        model.setAllergies(testAllergies);

        when(httpRequestService.registerUser(any(User.class), any(String.class))).thenReturn(true);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/user/create_user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isCreated());

        User savedUser = userRepository.findUserByEmail(testEmail).orElseThrow();

        assertThat(savedUser.getEmail()).isEqualTo(testEmail);
        assertThat(savedUser.getAllergies()).containsExactlyElementsOf(testAllergies);
        assertThat(savedUser.getName()).isEqualTo(testName);
    }

    @ParameterizedTest
    @MethodSource("registerInvalidSuite")
    public void register_withInvalid(String password, String email, String name) throws Exception {
        // Arrange
        final String testPassword = password;
        final String testEmail = email;
        final String testName = name;
        final List<String> testAllergies = List.of("Allergy");

        UserRegisterModel model = new UserRegisterModel();
        model.setPassword(testPassword);
        model.setEmail(testEmail);
        model.setName(testName);
        model.setAllergies(testAllergies);

        when(httpRequestService.registerUser(any(User.class), any(String.class))).thenReturn(true);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/user/create_user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isExpectationFailed());
    }

    static Stream<Arguments> registerInvalidSuite() {
        return Stream.of(
          Arguments.of("", "email@gmail.com", "Borislav"),
          Arguments.of(null, "email@gmail.com", "Borislav"),
          Arguments.of("MockedPassword", null, "Borislav"),
          Arguments.of("MockedPassword", "", "Borislav"),
          Arguments.of("MockedPassword", "borislav@gmail.com", ""),
          Arguments.of("MockedPassword", "borislav@gmail.com", null)
        );
    }

    @Test
    void register_httpRequestCannotRegister() throws Exception {
        final String testPassword = "password";
        final String testEmail = "correctmail@gmail.com";
        final String testName = "CoolName";
        final List<String> testAllergies = List.of("Allergy");

        UserRegisterModel model = new UserRegisterModel();
        model.setPassword(testPassword);
        model.setEmail(testEmail);
        model.setName(testName);
        model.setAllergies(testAllergies);

        when(httpRequestService.registerUser(any(User.class), any(String.class))).thenReturn(false);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/user/create_user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void register_alreadyInuse() throws Exception {
        final String testPassword = "password";
        final String testEmail = "correctmail@gmail.com";
        final String testName = "CoolName";
        final List<String> testAllergies = List.of("Allergy");

        userRepository.save(new User(testName, testEmail, testAllergies));

        UserRegisterModel model = new UserRegisterModel();
        model.setPassword(testPassword);
        model.setEmail(testEmail);
        model.setName(testName);
        model.setAllergies(testAllergies);

        when(httpRequestService.registerUser(any(User.class), any(String.class))).thenReturn(false);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/user/create_user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isConflict());
    }

    @ParameterizedTest
    @MethodSource("invalidEmailsSuite")
    void register_invalidEmail(String invalidEmail) throws Exception {
        final String testPassword = "password";
        final String testName = "CoolName";
        final List<String> testAllergies = List.of("Allergy");

        UserRegisterModel model = new UserRegisterModel();
        model.setPassword(testPassword);
        model.setEmail(invalidEmail);
        model.setName(testName);
        model.setAllergies(testAllergies);

        when(httpRequestService.registerUser(any(User.class), any(String.class))).thenReturn(true);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/user/create_user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model)));

        // Assert
        resultActions.andExpect(status().isConflict());
    }

    @Test
    void deleteUser_worksCorrectly() throws Exception {
        // we save the user
        final String testEmail = "correctmail@gmail.com";
        final String testName = "CoolName";
        final List<String> testAllergies = List.of("Allergy");
        User currentUser = userRepository.save(new User(testName, testEmail, testAllergies));

        when(mockAuthManager.getNetId()).thenReturn(currentUser.getId());
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);


        // Act
        ResultActions resultActions = mockMvc.perform(delete("/user/delete_user")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        // Assert
        resultActions.andExpect(status().isOk());
    }

    @Test
    void deleteUser_notAuthenticated() throws Exception {
        // we save the user
        final String testEmail = "correctmail@gmail.com";
        final String testName = "CoolName";
        final List<String> testAllergies = List.of("Allergy");
        User currentUser = userRepository.save(new User(testName, testEmail, testAllergies));

        when(mockAuthManager.getNetId()).thenReturn(currentUser.getId());
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(false);

        // Act
        ResultActions resultActions = mockMvc.perform(delete("/user/delete_user")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        // Assert
        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    void deleteUser_noToken() throws Exception {
        // we save the user
        final String testEmail = "correctmail@gmail.com";
        final String testName = "CoolName";
        final List<String> testAllergies = List.of("Allergy");
        User currentUser = userRepository.save(new User(testName, testEmail, testAllergies));

        when(mockAuthManager.getNetId()).thenReturn(currentUser.getId());

        // Act
        ResultActions resultActions = mockMvc.perform(delete("/user/delete_user")
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    void deleteUser_noSuchUser() throws Exception {
        // we save the user
        final String testEmail = "correctmail@gmail.com";
        final String testName = "CoolName";
        final List<String> testAllergies = List.of("Allergy");
        userRepository.save(new User(testName, testEmail, testAllergies));

        when(mockAuthManager.getNetId()).thenReturn("Not The Correct Id");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);

        // Act
        ResultActions resultActions = mockMvc.perform(delete("/user/delete_user")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        // Assert
        resultActions.andExpect(status().isExpectationFailed());
    }

    static Stream<Arguments> invalidEmailsSuite() {
        return Stream.of(
          Arguments.of("invalid@gmail."),
          Arguments.of("invalid@.com"),
          Arguments.of("@gmail.com"),
          Arguments.of("invalidgmail."),
          Arguments.of("invalidgmail.com")
        );
    }

    @Test
    public void updateAllergies_worksCorrectly() throws Exception {
        final String testEmail = "Borislav@gmail.com";
        final String testName = "borislav";
        final List<String> testAllergies = List.of("Allergy");
        final List<String> newAllergies = List.of("Allergy2");

        when(mockJwtTokenVerifier.validateToken(any())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("asdasdasdas");

        userRepository.save(new User(testName, testEmail, testAllergies));

        User currentUser = userRepository.findUserByEmail(testEmail).get();

        String id = currentUser.getId();

        when(mockAuthManager.getNetId()).thenReturn(id);

        AllergiesModel model = new AllergiesModel();
        model.setAllergies(newAllergies);

        // Act
        ResultActions resultActions = mockMvc.perform(put("/allergies/update_allergies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        resultActions.andExpect(status().isOk());

        User tempUser = userRepository.findUserByEmail(testEmail).get();

        assertThat(tempUser.getAllergies()).containsExactlyElementsOf(newAllergies);
    }

    @Test
    public void updateAllergies_nullAllergies() throws Exception {
        final String testEmail = "Borislav@gmail.com";
        final String testName = "borislav";
        final List<String> testAllergies = List.of("Allergy");
        final List<String> newAllergies = null;

        when(mockJwtTokenVerifier.validateToken(any())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("asdasdasdas");

        userRepository.save(new User(testName, testEmail, testAllergies));

        User currentUser = userRepository.findUserByEmail(testEmail).get();

        String id = currentUser.getId();

        when(mockAuthManager.getNetId()).thenReturn(id);

        AllergiesModel model = new AllergiesModel();
        model.setAllergies(newAllergies);

        // Act
        ResultActions resultActions = mockMvc.perform(put("/allergies/update_allergies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void updateAllergies_noSuchUser() throws Exception {
        final String testEmail = "Borislav@gmail.com";
        final String testName = "borislav";
        final List<String> testAllergies = List.of("Allergy");
        final List<String> newAllergies = List.of("Allergy2");

        userRepository.save(new User(testName, testEmail, testAllergies));

        when(mockJwtTokenVerifier.validateToken(any())).thenReturn(true);
        when(mockAuthManager.getNetId()).thenReturn("NotTheSameId");

        AllergiesModel model = new AllergiesModel();
        model.setAllergies(newAllergies);

        // Act
        ResultActions resultActions = mockMvc.perform(put("/allergies/update_allergies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(model))
                .header("Authorization", "Bearer MockedToken"));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void getAllergies_worksCorrectly() throws Exception {
        final String testEmail = "Borislav@gmail.com";
        final String testName = "borislav";
        final List<String> testAllergies = List.of("Allergy", "Allergy2", "Allergy3");

        when(mockJwtTokenVerifier.validateToken(any())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("MockedID");

        userRepository.save(new User(testName, testEmail, testAllergies));

        User currentUser = userRepository.findUserByEmail(testEmail).get();

        String id = currentUser.getId();

        when(mockAuthManager.getNetId()).thenReturn(id);

        // Act
        ResultActions resultActions = mockMvc.perform(get("/allergies/get_allergies")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        resultActions.andExpect(status().isOk());

        User tempUser = userRepository.findUserByEmail(testEmail).get();

        assertThat(tempUser.getAllergies()).containsExactlyElementsOf(testAllergies);
    }

    @Test
    public void getAllergies_noSuchUser() throws Exception {
        final String testEmail = "Borislav@gmail.com";
        final String testName = "borislav";
        final List<String> testAllergies = List.of("Allergy", "Allergy2", "Allergy3");

        userRepository.save(new User(testName, testEmail, testAllergies));

        when(mockJwtTokenVerifier.validateToken(any())).thenReturn(true);
        when(mockAuthManager.getNetId()).thenReturn("NotTheSameId");

        // Act
        ResultActions resultActions = mockMvc.perform(get("/allergies/get_allergies")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void loginUser_worksCorrectly() throws Exception {
        final String testEmail = "Borislav@gmail.com";
        final String testPassword = "password123";
        final String testName = "Borislav";
        final List<String> testAllergies = List.of("Allergy", "Allergy2", "Allergy3");

        userRepository.save(new User(testName, testEmail, testAllergies));

        String id = "5aa88856-718e-4a45-9919-e7e9e14f6d5d";

        String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI1YWE4ODg1Ni03MThlLTRhN" +
                "DUtOTkxOS1lN2U5ZTE0ZjZkNWQiLCJyb2xlIjoiW1JPTEVfTUFOQUdFUl0iLCJl" +
                "eHAiOjE2NzEwMjMzOTksImlhdCI6MTY3MDkzNjk5OX0.aGtHkbWJfZCxb98l-wr1Ejs" +
                "CA3uAguFKwGN912yufi6X2enPTTK9kmcSdSBpLRlLmybN_km06rqYYAMDGly6CA";

        when(httpRequestService.loginUser(any(), any()))
                .thenReturn(Optional.of(token));

        LoginModel loginModel = new LoginModel();
        loginModel.setEmail(testEmail);
        loginModel.setPassword(testPassword);

        // Act
        ResultActions resultActions = mockMvc.perform(get("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(loginModel)));

        MvcResult result = resultActions
                .andExpect(status().isOk())
                .andReturn();

        LoginResponseModel responseModel = JsonUtil.deserialize(result.getResponse().getContentAsString(),
                LoginResponseModel.class);

        String actualToken = responseModel.getJwtToken();

        assertThat(actualToken).isEqualTo(token);
    }

    @ParameterizedTest
    @MethodSource("loginInvalidSuite")
    public void loginUser_invalidParameters(String email, String password) throws Exception {
        LoginModel loginModel = new LoginModel();
        loginModel.setEmail(email);
        loginModel.setPassword(password);

        // Act
        ResultActions resultActions = mockMvc.perform(get("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(loginModel)));

        resultActions.andExpect(status().isExpectationFailed());
    }

    static Stream<Arguments> loginInvalidSuite() {
        return Stream.of(
                Arguments.of("", "email@gmail.com"),
                Arguments.of(null, "email@gmail.com"),
                Arguments.of("MockedPassword", null),
                Arguments.of("MockedPassword", "")
        );
    }

    @Test
    public void loginUser_noSuchUser() throws Exception {
        LoginModel loginModel = new LoginModel();
        loginModel.setEmail("goodEmail@gmail.com");
        loginModel.setPassword("goodPassword@gmail.com");

        // Act
        ResultActions resultActions = mockMvc.perform(get("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(loginModel)));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void loginUser_httpRequestNotFound() throws Exception {
        LoginModel loginModel = new LoginModel();
        loginModel.setEmail("goodEmail@gmail.com");
        loginModel.setPassword("goodPassword@gmail.com");

        userRepository.save(new User("MockMail", loginModel.getEmail(), List.of()));

        when(httpRequestService.loginUser(anyString(), anyString())).thenReturn(Optional.empty());

        // Act
        ResultActions resultActions = mockMvc.perform(get("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(loginModel)));

        resultActions.andExpect(status().isBadRequest());
    }
}

