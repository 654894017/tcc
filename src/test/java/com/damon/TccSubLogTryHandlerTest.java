package com.damon;

import com.damon.tcc.annotation.SubBizId;
import com.damon.tcc.sub_handler.TccSubLogTryHandler;
import com.damon.tcc.sub_log.ITccSubLogService;
import com.damon.tcc.sub_log.TccSubLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TccSubLogTryHandlerTest {

    @Mock
    private ITccSubLogService tccSubLogService;

    @Mock
    private Function<SubBizId, String> tryPhaseFunction;
    @InjectMocks
    private TccSubLogTryHandler<String, TestCommand> tccSubLogTryHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void execute_shouldCallTryPhaseFunctionAndCreateSubLog() {
        // Arrange
        TestCommand subBizId = new TestCommand();
        subBizId.setBizId(1L);
        subBizId.setSubBizId(2L);
        String expectedResult = "result";
        when(tryPhaseFunction.apply(any(TestCommand.class))).thenReturn(expectedResult);

        // Act
        String result = tccSubLogTryHandler.execute(subBizId);

        // Assert
        verify(tccSubLogService).create(any(TccSubLog.class));
        verify(tryPhaseFunction).apply(subBizId);
        assert result.equals(expectedResult);
    }

    @Test
    void execute_shouldCatchExceptionAndLogError() {
        // Arrange
        TestCommand subBizId = new TestCommand();
        subBizId.setBizId(1L);
        subBizId.setSubBizId(2L);
        when(tryPhaseFunction.apply(any(TestCommand.class))).thenThrow(new RuntimeException("Exception"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> tccSubLogTryHandler.execute(subBizId));
        verify(tccSubLogService, times(1)).create(any(TccSubLog.class));
    }
}
