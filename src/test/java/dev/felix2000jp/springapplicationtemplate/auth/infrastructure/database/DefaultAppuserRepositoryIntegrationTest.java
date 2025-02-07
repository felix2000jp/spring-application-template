package dev.felix2000jp.springapplicationtemplate.auth.infrastructure.database;

import dev.felix2000jp.springapplicationtemplate.auth.domain.Appuser;
import org.junit.jupiter.api.AfterEach;
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

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DataJpaTest
@Testcontainers
@Import(DefaultAppuserRepository.class)
class DefaultAppuserRepositoryIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres");

    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private DefaultAppuserRepository appuserRepository;

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
    void findAll_given_page_with_data_then_return_list_with_Appusers() {
        var actual = appuserRepository.findAll(0);

        assertThat(actual).isNotEmpty();
    }

    @Test
    void findAll_given_page_with_no_data_then_return_empty_list() {
        var actual = appuserRepository.findAll(1);

        assertThat(actual).isEmpty();
    }

    @Test
    void findById_given_id_of_appuser_then_return_appuser() {
        var actual = appuserRepository.findById(appuser.getId());

        assertThat(actual).isPresent();
    }

    @Test
    void findById_given_not_found_id_then_return_empty_optional() {
        var actual = appuserRepository.findById(UUID.randomUUID());

        assertThat(actual).isNotPresent();
    }

    @Test
    void existsById_given_id_of_appuser_then_return_true() {
        var actual = appuserRepository.existsById(appuser.getId());

        assertThat(actual).isTrue();
    }

    @Test
    void existsById_given_not_found_id_then_return_false() {
        var actual = appuserRepository.existsById(UUID.randomUUID());

        assertThat(actual).isFalse();
    }

    @Test
    void findByUsername_given_username_of_appuser_then_return_appuser() {
        var actual = appuserRepository.findByUsername(appuser.getUsername());

        assertThat(actual).isPresent();
    }

    @Test
    void findByUsername_given_not_found_username_then_return_empty_optional() {
        var actual = appuserRepository.findByUsername("non existent username");

        assertThat(actual).isNotPresent();
    }

    @Test
    void existsByUsername_given_username_of_appuser_then_return_true() {
        var actual = appuserRepository.existsByUsername(appuser.getUsername());

        assertThat(actual).isTrue();
    }

    @Test
    void existsByUsername_given_not_found_username_then_return_false() {
        var actual = appuserRepository.existsByUsername("non existent username");

        assertThat(actual).isFalse();
    }

    @Test
    void deleteById_given_id_of_appuser_then_delete_appuser() {
        appuserRepository.deleteById(appuser.getId());
        testEntityManager.flush();
        testEntityManager.clear();

        var deletedUser = testEntityManager.find(Appuser.class, appuser.getId());
        assertThat(deletedUser).isNull();
    }

    @Test
    void deleteById_given_not_found_id_then_fail_without_throwing() {
        assertThatCode(() -> {
            appuserRepository.deleteById(UUID.randomUUID());
            testEntityManager.flush();
        }).doesNotThrowAnyException();
    }

    @Test
    void save_given_appuser_then_save_appuser() {
        var appuserToCreate = new Appuser("new username", "new password");

        appuserRepository.save(appuserToCreate);
        testEntityManager.flush();
        testEntityManager.clear();

        var createdAppuser = testEntityManager.find(Appuser.class, appuser.getId());
        assertThat(createdAppuser).isNotNull();
    }

    @ParameterizedTest
    @MethodSource
    void save_given_invalid_appuser_to_create_then_throw_exception(Appuser appuserToCreate) {
        assertThatThrownBy(() -> {
            appuserRepository.save(appuserToCreate);
            testEntityManager.flush();
        }).isInstanceOf(Exception.class);
    }

    private static Stream<Arguments> save_given_invalid_appuser_to_create_then_throw_exception() {
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
