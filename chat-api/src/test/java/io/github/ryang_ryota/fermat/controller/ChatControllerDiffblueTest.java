package io.github.ryang_ryota.fermat.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import io.github.ryang_ryota.fermat.service.ChatService;

import java.util.ArrayList;
import java.util.function.Function;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.RequestHeadersSpec;
import org.springframework.test.web.reactive.server.WebTestClient.RequestHeadersUriSpec;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;

@ContextConfiguration(classes = {ChatController.class})
@DisabledInAotMode
@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = {ChatController.class})
class ChatControllerDiffblueTest {
    @Autowired
    private ChatController chatController;

    @MockitoBean
    private ChatService chatService;

    @Autowired
    private WebTestClient webTestClient;

    /**
     * Test {@link ChatController#chatStream(String)}.
     *
     * <ul>
     *   <li>Given {@link ChatService} {@link ChatService#processChatStream(String)} return
     *       fromIterable {@link ArrayList#ArrayList()}.
     * </ul>
     *
     * <p>Method under test: {@link ChatController#chatStream(String)}
     */
    @Test
    @DisplayName(
            "Test chatStream(String); given ChatService processChatStream(String) return fromIterable ArrayList()")
    @Tag("ContributionFromDiffblue")
    @MethodsUnderTest({"Flux ChatController.chatStream(String)"})
    void testChatStream_givenChatServiceProcessChatStreamReturnFromIterableArrayList() {
        // Arrange
        Flux<String> fromIterableResult = Flux.fromIterable(new ArrayList<>());
        when(chatService.processChatStream(Mockito.<String>any())).thenReturn(fromIterableResult);
        RequestHeadersUriSpec<?> getResult = webTestClient.get();

        // Act
        ResponseSpec actualExchangeResult =
                ((RequestHeadersSpec<?>)
                        getResult.uri(
                                UriComponentsBuilder.fromPath("/chat/stream")
                                        .queryParam("query", "Query")
                                        .build()
                                        .toUriString()))
                        .exchange();

        // Assert
        verify(chatService).processChatStream(eq("Query"));
        Class<ServerSentEvent> elementClass = ServerSentEvent.class;
        Flux<ServerSentEvent> responseBody =
                actualExchangeResult.returnResult(elementClass).getResponseBody();
        assertEquals(-1, responseBody.getPrefetch());
        assertEquals(-1, responseBody.buffer().getPrefetch());
        assertEquals(-1, responseBody.checkpoint().getPrefetch());
        assertEquals(-1, responseBody.elapsed().getPrefetch());
        assertEquals(-1, responseBody.timestamp().getPrefetch());
        assertEquals(256, responseBody.parallel().getPrefetch());
        assertEquals(Integer.MAX_VALUE, responseBody.cache().getPrefetch());
    }

    /**
     * Test {@link ChatController#chatStream(String)}.
     *
     * <ul>
     *   <li>Given {@link Flux} {@link Flux#map(Function)} return fromIterable {@link
     *       ArrayList#ArrayList()}.
     *   <li>Then calls {@link Flux#map(Function)}.
     * </ul>
     *
     * <p>Method under test: {@link ChatController#chatStream(String)}
     */
    @Test
    @DisplayName(
            "Test chatStream(String); given Flux map(Function) return fromIterable ArrayList(); then calls map(Function)")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"Flux ChatController.chatStream(String)"})
    void testChatStream_givenFluxMapReturnFromIterableArrayList_thenCallsMap() {
        // Arrange
        Flux<String> flux = mock(Flux.class);
        Flux<Object> fromIterableResult = Flux.fromIterable(new ArrayList<>());
        when(flux.map(Mockito.<Function<String, Object>>any())).thenReturn(fromIterableResult);
        when(chatService.processChatStream(Mockito.<String>any())).thenReturn(flux);
        RequestHeadersUriSpec<?> getResult = webTestClient.get();

        // Act
        ResponseSpec actualExchangeResult =
                ((RequestHeadersSpec<?>)
                        getResult.uri(
                                UriComponentsBuilder.fromPath("/chat/stream")
                                        .queryParam("query", "Query")
                                        .build()
                                        .toUriString()))
                        .exchange();

        // Assert
        verify(chatService).processChatStream(eq("Query"));
        verify(flux).map(isA(Function.class));
        Class<ServerSentEvent> elementClass = ServerSentEvent.class;
        Flux<ServerSentEvent> responseBody =
                actualExchangeResult.returnResult(elementClass).getResponseBody();
        assertEquals(-1, responseBody.getPrefetch());
        assertEquals(-1, responseBody.buffer().getPrefetch());
        assertEquals(-1, responseBody.checkpoint().getPrefetch());
        assertEquals(-1, responseBody.elapsed().getPrefetch());
        assertEquals(-1, responseBody.timestamp().getPrefetch());
        assertEquals(256, responseBody.parallel().getPrefetch());
        assertEquals(Integer.MAX_VALUE, responseBody.cache().getPrefetch());
    }
}
