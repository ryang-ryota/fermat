package io.github.ryang_ryota.fermat.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;

import java.util.ArrayList;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;

@ContextConfiguration(classes = {ChatService.class})
@DisabledInAotMode
@ExtendWith(SpringExtension.class)
class ChatServiceDiffblueTest {
    @Autowired
    private ChatService chatService;

    @MockitoBean
    private ChromaService chromaService;

    @MockitoBean
    private OllamaService ollamaService;

    /**
     * Test {@link ChatService#processChatStream(String)}.
     *
     * <ul>
     *   <li>Then return buffer Prefetch is minus one.
     * </ul>
     *
     * <p>Method under test: {@link ChatService#processChatStream(String)}
     */
    @Test
    @DisplayName("Test processChatStream(String); then return buffer Prefetch is minus one")
    @Tag("ContributionFromDiffblue")
    @MethodsUnderTest({"Flux ChatService.processChatStream(String)"})
    void testProcessChatStream_thenReturnBufferPrefetchIsMinusOne() {
        // Arrange
        ChromaService chromaService = mock(ChromaService.class);
        when(chromaService.retrieveContext(Mockito.<String>any())).thenReturn("Retrieve Context");

        // Act
        Flux<String> actualProcessChatStreamResult =
                new ChatService(chromaService, new OllamaService()).processChatStream("Query");

        // Assert
        verify(chromaService).retrieveContext(eq("Query"));
        assertEquals(-1, actualProcessChatStreamResult.buffer().getPrefetch());
        assertEquals(-1, actualProcessChatStreamResult.checkpoint().getPrefetch());
        assertEquals(-1, actualProcessChatStreamResult.elapsed().getPrefetch());
        assertEquals(-1, actualProcessChatStreamResult.timestamp().getPrefetch());
        assertEquals(256, actualProcessChatStreamResult.parallel().getPrefetch());
        assertEquals(Integer.MAX_VALUE, actualProcessChatStreamResult.cache().getPrefetch());
        assertEquals(Integer.SIZE, actualProcessChatStreamResult.getPrefetch());
    }

    /**
     * Test {@link ChatService#processChatStream(String)}.
     *
     * <ul>
     *   <li>Then return fromIterable {@link ArrayList#ArrayList()}.
     * </ul>
     *
     * <p>Method under test: {@link ChatService#processChatStream(String)}
     */
    @Test
    @DisplayName("Test processChatStream(String); then return fromIterable ArrayList()")
    @Tag("ContributionFromDiffblue")
    @MethodsUnderTest({"Flux ChatService.processChatStream(String)"})
    void testProcessChatStream_thenReturnFromIterableArrayList() {
        // Arrange
        when(chromaService.retrieveContext(Mockito.<String>any())).thenReturn("Retrieve Context");
        Flux<String> fromIterableResult = Flux.fromIterable(new ArrayList<>());
        when(ollamaService.generateResponseStream(Mockito.<String>any()))
                .thenReturn(fromIterableResult);

        // Act
        Flux<String> actualProcessChatStreamResult = chatService.processChatStream("Query");

        // Assert
        verify(chromaService).retrieveContext(eq("Query"));
        verify(ollamaService)
                .generateResponseStream(
                        eq(
                                "あなたは数学の専門家です。以下の文脈を参考に日本語で質問に答えてください。\n\n【文脈】\nRetrieve Context\n\n【質問】Query\n【回答】"));
        assertSame(fromIterableResult, actualProcessChatStreamResult);
    }
}
