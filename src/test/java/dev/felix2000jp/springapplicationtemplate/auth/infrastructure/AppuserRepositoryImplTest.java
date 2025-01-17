package dev.felix2000jp.springapplicationtemplate.auth.infrastructure;

import dev.felix2000jp.springapplicationtemplate.auth.domain.Appuser;
import org.junit.jupiter.api.*;
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
    void givenPageNumberWithAppusers_whenFindAll_thenReturnListOfAppusers() {
        // given
        var pageNumber = 0;

        // when
        var actual = appuserRepository.findAll(pageNumber);

        // then
        assertFalse(actual.isEmpty());
    }

    @Test
    void givenPageNumberWithNoAppusers_whenFindAll_thenReturnEmptyListOfAppusers() {
        // given
        var pageNumber = 0;

        // when
        var actual = appuserRepository.findAll(pageNumber);

        // then
        assertTrue(actual.isEmpty());
    }

    @Test
    void givenAppuserId_whenFindById_thenReturnAppuser() {
        // given
        var id = appuser.getId();

        // when
        var actual = appuserRepository.findById(id);

        // then
        assertTrue(actual.isPresent());
    }

    @Test
    void givenNonExistentId_whenFindById_thenReturnNoAppuser() {
        // given
        var id = UUID.randomUUID();

        // when
        var actual = appuserRepository.findById(id);

        // then
        assertFalse(actual.isPresent());
    }

    @Test
    void givenAppuserId_whenExistsById_thenReturnTrue() {
        // given
        var id = appuser.getId();

        // when
        var actual = appuserRepository.existsById(id);

        // then
        assertTrue(actual);
    }

    @Test
    void givenNonExistentId_whenExistsById_thenReturnFalse() {
        // given
        var id = UUID.randomUUID();

        // when
        var actual = appuserRepository.existsById(id);

        // then
        assertFalse(actual);
    }

    @Test
    void givenAppuserUsername_whenFindByUsername_thenReturnAppuser() {
        // given
        var username = appuser.getUsername();

        // when
        var actual = appuserRepository.findByUsername(username);

        // then
        assertNotNull(actual);
    }

    @Test
    void givenNonExistentUsername_whenFindByUsername_thenReturnNoAppuser() {
        // when
        var actual = appuserRepository.findByUsername("non existent username");

        // then
        assertNull(actual);
    }

    @Test
    void givenAppuserUsername_whenExistsByUsername_thenReturnTrue() {
        // given
        var username = appuser.getUsername();

        // when
        var actual = appuserRepository.existsByUsername(username);

        // then
        assertTrue(actual);
    }

    @Test
    void givenNonExistentUsername_whenExistsByUsername_thenReturnFalse() {
        // when
        var actual = appuserRepository.existsByUsername("non existent username");

        // then
        assertFalse(actual);
    }

    @Test
    void givenAppuserId_WhenDeleteById_thenDeleteAppuser() {
        // given
        var id = appuser.getId();

        // when
        appuserRepository.deleteById(id);
        testEntityManager.flush();
        testEntityManager.clear();

        // then
        assertNull(testEntityManager.find(Appuser.class, appuser.getId()));
    }

    @Test
    void givenNonExistentId_WhenDeleteById_thenDoNotThrow() {
        // given
        var id = UUID.randomUUID();

        // when and then
        assertDoesNotThrow(() -> {
            appuserRepository.deleteById(id);
            testEntityManager.flush();
        });
    }

    @Test
    void givenValidAppuser_whenSave_thenSaveAppuser() {
        // given
        var appuserToCreate = new Appuser("new username", "new password");

        // when
        appuserRepository.save(appuserToCreate);
        testEntityManager.flush();
        testEntityManager.clear();

        // then
        assertNotNull(testEntityManager.find(Appuser.class, appuserToCreate.getId()));
    }

    @ParameterizedTest
    @MethodSource
    void givenInvalidValidAppuser_whenSave_thenSaveAppuser(Appuser appuserToCreate) {
        // when and then
        assertThrows(Exception.class, () -> {
            appuserRepository.save(appuserToCreate);
            testEntityManager.flush();
        });
    }

    private static Stream<Arguments> givenInvalidValidAppuser_whenSave_thenSaveAppuser() {
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
