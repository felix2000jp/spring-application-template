package dev.felix2000jp.springapplicationtemplate.appusers.internal;

import dev.felix2000jp.springapplicationtemplate.TestcontainersConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class AppuserRepositoryTest {

    @Autowired
    private AppuserRepository appuserRepository;

    private Appuser appuser;

    @BeforeEach
    void setUp() {
        appuser = new Appuser("username", "password");
        appuser.getAuthoritiesScopeValues();
        appuserRepository.save(appuser);
    }

    @AfterEach
    void tearDown() {
        appuserRepository.delete(appuser);
    }

    @Test
    void findByUsername_should_return_appuser_when_appuser_is_found() {
        var actual = appuserRepository.findByUsername(appuser.getUsername());

        assertThat(actual).usingRecursiveComparison().isEqualTo(Optional.of(appuser));
    }

    @Test
    void findByUsername_should_return_empty_when_appuser_is_not_found() {
        var actual = appuserRepository.findByUsername("username that doesn't exist");

        assertThat(actual).isEmpty();
    }

    @Test
    void existsByUsername_should_return_true_when_appuser_exists() {
        var actual = appuserRepository.existsByUsername(appuser.getUsername());

        assertThat(actual).isTrue();
    }

    @Test
    void existsByUsername_should_return_false_when_appuser_does_not_exist() {
        var actual = appuserRepository.existsByUsername("username that doesn't exist");

        assertThat(actual).isFalse();
    }

}
