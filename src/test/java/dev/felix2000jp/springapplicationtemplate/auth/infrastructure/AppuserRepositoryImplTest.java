package dev.felix2000jp.springapplicationtemplate.auth.infrastructure;

import dev.felix2000jp.springapplicationtemplate.auth.domain.Appuser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DataJpaTest
@Testcontainers
@Import(AppuserRepositoryImpl.class)
class AppuserRepositoryImplTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres");

    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private AppuserRepositoryImpl appuserRepository;

    private Appuser appuser;

    @BeforeEach
    void setUp() {
        appuser = new Appuser("username", "password");
        testEntityManager.persistAndFlush(appuser);

        testEntityManager.clear();
    }

    @AfterEach
    void tearDown() {
        testEntityManager.clear();

        testEntityManager
                .getEntityManager()
                .createNativeQuery("TRUNCATE TABLE appuser CASCADE")
                .executeUpdate();
    }

    @Test
    void should_find_appusers_when_page_is_not_empty() {
        var actual = appuserRepository.findAll(0);

        assertFalse(actual.isEmpty());
    }

    @Test
    void should_not_find_appusers_when_page_is_empty() {
        var actual = appuserRepository.findAll(1);

        assertTrue(actual.isEmpty());
    }

    @Test
    void should_find_appuser_with_id_when_appuser_exists() {
        var actual = appuserRepository.findById(appuser.getId());

        assertNotNull(actual);
    }

    @Test
    void should_not_find_appuser_with_id_when_appuser_does_not_exist() {
        var actual = appuserRepository.findById(UUID.randomUUID());

        assertNull(actual);
    }

    @Test
    void should_return_true_if_appuser_with_id_exists() {
        var actual = appuserRepository.existsById(appuser.getId());

        Assertions.assertTrue(actual);
    }

    @Test
    void should_return_false_if_appuser_with_id_does_not_exist() {
        var actual = appuserRepository.existsById(UUID.randomUUID());

        Assertions.assertFalse(actual);
    }

    @Test
    void should_find_appuser_with_username_when_appuser_exists() {
        var actual = appuserRepository.findByUsername(appuser.getUsername());

        assertNotNull(actual);
    }

    @Test
    void should_not_find_appuser_with_username_when_appuser_does_not_exist() {
        var actual = appuserRepository.findByUsername("non existent username");

        assertNull(actual);
    }

    @Test
    void should_return_true_if_appuser_with_username_exists() {
        var actual = appuserRepository.existsByUsername(appuser.getUsername());

        Assertions.assertTrue(actual);
    }

    @Test
    void should_return_false_if_appuser_with_username_does_not_exist() {
        var actual = appuserRepository.existsByUsername("non existent username");

        Assertions.assertFalse(actual);
    }

    @Test
    void should_delete_appuser_with_id_when_note_exists() {
        appuserRepository.deleteById(appuser.getId());

        testEntityManager.flush();
        testEntityManager.clear();

        assertNull(testEntityManager.find(Appuser.class, appuser.getId()));
    }

    @Test
    void should_not_throw_when_trying_to_delete_appuser_with_id_when_appuser_does_not_exist() {
        assertDoesNotThrow(() -> {
            appuserRepository.deleteById(UUID.randomUUID());
            testEntityManager.flush();
        });
    }

    @Test
    void should_save_appuser_successfully() {
        var appuserToCreate = new Appuser("new username", "new password");

        appuserRepository.save(appuserToCreate);

        testEntityManager.flush();
        testEntityManager.clear();

        assertNotNull(testEntityManager.find(Appuser.class, appuserToCreate.getId()));
    }

    @ParameterizedTest
    @MethodSource
    void should_fail_to_save_appuser_when_appuser_data_is_invalid(Appuser appuserToCreate) {
        assertThrows(Exception.class, () -> {
            appuserRepository.save(appuserToCreate);
            testEntityManager.flush();
        });
    }

    private static Stream<Arguments> should_fail_to_save_appuser_when_appuser_data_is_invalid() {
        return Stream.of(
                arguments(new Appuser()),
                arguments(new Appuser(null, "new password")),
                arguments(new Appuser("", "new password")),
                arguments(new Appuser(" ", "new password")),
                arguments(new Appuser("lol", "new password")),
                arguments(new Appuser("a".repeat(501), "new password")),
                arguments(new Appuser("new username", null)),
                arguments(new Appuser("new username", "")),
                arguments(new Appuser("new username", " ")),
                arguments(new Appuser("new username", "lol")),
                arguments(new Appuser("new username", "a".repeat(501)))
        );
    }

}
