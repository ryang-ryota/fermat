package io.github.ryang_ryota.fermat.controller;

import io.github.ryang_ryota.fermat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * このクラスは、チャット用のAPIエンドポイントを提供するコントローラーです。
 * - フロントエンドからのリクエストを受け取り、AIによる回答をストリーミング（SSE）で返します。
 * - Spring WebFluxのFluxとServerSentEventを利用し、リアルタイムに応答を送信します。
 */
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    // チャット処理を担当するサービスクラス
    private final ChatService chatService;

    /**
     * AIチャットのストリーミング応答を返すエンドポイント。
     * フロントエンドからGETリクエストで呼び出されます。
     *
     * @param query ユーザーからの質問内容（クエリパラメータで受け取る）
     * @return AIの回答を逐次送信するServerSentEvent（SSE）ストリーム
     * <p>
     * - produces = MediaType.TEXT_EVENT_STREAM_VALUE により、SSE形式でレスポンスを返す
     * - Flux<String>でAIからの回答トークンを逐次取得し、SSEイベントとしてクライアントに送信
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chatStream(@RequestParam("query") String query) {
        return chatService.processChatStream(query)
                // 各トークンをSSEイベントとしてラップして返す
                .map(token -> ServerSentEvent.builder(token).build());
    }
}

