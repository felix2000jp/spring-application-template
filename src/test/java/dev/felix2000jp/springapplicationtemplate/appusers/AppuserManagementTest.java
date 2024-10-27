package dev.felix2000jp.springapplicationtemplate.appusers;

import dev.felix2000jp.springapplicationtemplate.appusers.internal.AppuserService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AppuserManagementTest {

    @Mock
    private AppuserService appuserService;
    @InjectMocks
    private AppuserManagement appuserManagement;

}
