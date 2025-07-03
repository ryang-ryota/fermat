package io.github.ryang_ryota.fermat.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
/**
 * このクラスは、Ollama（ローカルAIモデル）と通信し、AIによる回答をストリーミング形式で取得するサービスです。
 * - Spring WebFluxのWebClientを利用して非同期・ストリーミング通信を実現しています。
 * - OllamaのAPI仕様に従い、プロンプトを送信し、AIの生成する回答をトークン単位で逐次受信します。
 */
@Service
public class OllamaService {

    // Ollama APIのベースURL
    @Value("${spring.ai.ollama.base-url}")
    private String ollamaBaseUrl;

    // 使用するAIモデル名
    @Value("${spring.ai.ollama.chat.model}")
    private String ollamaModel;

    // WebClient: 非同期HTTP通信のためのSpring標準クライアント
    private final WebClient webClient = WebClient.builder().build();

    // JSONパース用のObjectMapper
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Ollama APIにプロンプトを送信し、AIの回答をストリーミングで受信する
     *
     * @param prompt ユーザーの質問やコンテキストを含むAIへの指示文
     * @return AIの回答をトークン単位で逐次返すFluxストリーム
     *
     * - Ollama API（/api/generate）にPOSTリクエストを送信
     * - "stream": true を指定することで、AIの回答を1トークンずつストリーミングで受信
     * - 受信した各行はJSON形式なので、responseフィールドのみを抽出して返却
     * - パース失敗時（不正なJSON等）は無視して次のトークンへ
     */
    public Flux<String> generateResponseStream(String prompt) {
        // OllamaのAPIエンドポイントURL
        String url = ollamaBaseUrl + "/api/generate";

        // APIリクエストボディ（モデル名・プロンプト・ストリーミング指定）
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", ollamaModel);
        requestBody.put("prompt", prompt);
        requestBody.put("stream", true);

        // WebClientでPOSTリクエストを送信し、ストリーミングレスポンスを逐次受信
        return webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                // OllamaはNDJSON（1行ごとにJSON）も返すためacceptに指定
                .accept(MediaType.APPLICATION_NDJSON, MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToFlux(String.class)
                .flatMap(line -> {
                    try {
                        // 1行ごとのJSONをパースし、"response"フィールドのみ抽出
                        JsonNode json = objectMapper.readTree(line);
                        String token = json.path("response").asText();
                        return Flux.just(token);
                    } catch (Exception e) {
                        // パース失敗時は無視して次へ
                        return Flux.empty();
                    }
                });
    }
}
