package io.github.ryang_ryota.fermat.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

class OllamaServiceDiffblueTest {
    /**
     * Test {@link OllamaService#generateResponseStream(String)}.
     *
     * <ul>
     *   <li>When {@code model}.
     * </ul>
     *
     * <p>Method under test: {@link OllamaService#generateResponseStream(String)}
     */
    @Test
    @DisplayName("Test generateResponseStream(String); when 'model'")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"Flux OllamaService.generateResponseStream(String)"})
    void testGenerateResponseStream_whenModel() {
        // Arrange and Act
        Flux<String> actualGenerateResponseStreamResult =
                new OllamaService().generateResponseStream("model");

        // Assert
        assertEquals(-1, actualGenerateResponseStreamResult.buffer().getPrefetch());
        assertEquals(-1, actualGenerateResponseStreamResult.checkpoint().getPrefetch());
        assertEquals(-1, actualGenerateResponseStreamResult.elapsed().getPrefetch());
        assertEquals(-1, actualGenerateResponseStreamResult.timestamp().getPrefetch());
        assertEquals(256, actualGenerateResponseStreamResult.parallel().getPrefetch());
        assertEquals(Integer.MAX_VALUE, actualGenerateResponseStreamResult.cache().getPrefetch());
        assertEquals(Integer.SIZE, actualGenerateResponseStreamResult.getPrefetch());
    }

    /**
     * Test {@link OllamaService#generateResponseStream(String)}.
     *
     * <ul>
     *   <li>When {@code Prompt}.
     * </ul>
     *
     * <p>Method under test: {@link OllamaService#generateResponseStream(String)}
     */
    @Test
    @DisplayName("Test generateResponseStream(String); when 'Prompt'")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"Flux OllamaService.generateResponseStream(String)"})
    void testGenerateResponseStream_whenPrompt() {
        // Arrange and Act
        Flux<String> actualGenerateResponseStreamResult =
                new OllamaService().generateResponseStream("Prompt");

        // Assert
        assertEquals(-1, actualGenerateResponseStreamResult.buffer().getPrefetch());
        assertEquals(-1, actualGenerateResponseStreamResult.checkpoint().getPrefetch());
        assertEquals(-1, actualGenerateResponseStreamResult.elapsed().getPrefetch());
        assertEquals(-1, actualGenerateResponseStreamResult.timestamp().getPrefetch());
        assertEquals(256, actualGenerateResponseStreamResult.parallel().getPrefetch());
        assertEquals(Integer.MAX_VALUE, actualGenerateResponseStreamResult.cache().getPrefetch());
        assertEquals(Integer.SIZE, actualGenerateResponseStreamResult.getPrefetch());
    }
}
