package io.github.ryang_ryota.fermat.service;

import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;
/**
 * ChromaService.java
 *
 * このクラスは、ChromaDB（ベクトルデータベース）と通信し、
 * ユーザーの質問に関連する知識（コンテキスト）を検索・取得するサービスです。
 * - クエリを埋め込みベクトルに変換し、ChromaDBに類似検索リクエストを送信します。
 * - 最も関連性の高いドキュメントを複数取得し、AIへのプロンプト生成に利用します。
 *
 * @author YourName
 * @version 1.0
 */

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * ChromaDBとの連携を担当するサービスクラス。
 * ベクトル検索により、関連知識を取得します。
 */
@Service
public class ChromaService {

    // ChromaDBのホスト名
    @Value("${chroma.host}")
    private String chromaHost;

    // ChromaDBのポート番号
    @Value("${chroma.port}")
    private int chromaPort;

    // ChromaDBのテナント名
    @Value("${chroma.tenant}")
    private String chromaTenant;

    // ChromaDBのデータベース名
    @Value("${chroma.database}")
    private String chromaDatabase;

    // 検索対象のコレクションID
    @Value("${chroma.collection-id}")
    private String collectionId;

    // HTTP通信を行うためのRestTemplate
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * ユーザーの質問（query）に基づき、ChromaDBから関連コンテキストを取得します。
     * 1. クエリを埋め込みベクトルに変換
     * 2. ChromaDBのREST APIにベクトル検索リクエストを送信
     * 3. 最も関連性の高いドキュメントを複数取得し、結合して返却
     * @param query ユーザーからの質問文
     * @return 検索で得られた関連知識（複数ドキュメントを結合した文字列）
     */
    public String retrieveContext(String query) {

        // 1. クエリを埋め込みベクトルに変換
        var embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        var embedding = embeddingModel.embed(query).content().vector();

        // ベクトルをJSONArray形式に変換（ChromaDB API仕様に合わせる）
        JSONArray embeddingArray = new JSONArray();
        for (Float value : embedding) {
            embeddingArray.put(value);
        }
        // ChromaDB API仕様：クエリベクトルは2次元配列で渡す必要がある
        JSONArray queryEmbeddings = new JSONArray();
        queryEmbeddings.put(embeddingArray);

        // 2. ChromaDBのREST APIエンドポイントURLを組み立て
        String url = String.format("http://%s:%d/api/v2/tenants/%s/databases/%s/collections/%s/query",
                chromaHost, chromaPort, chromaTenant, chromaDatabase, collectionId);

        // 3. ChromaDB API仕様に合わせたリクエストボディを作成
        JSONObject requestBody = new JSONObject();
        // "include"で取得する情報を指定（距離とドキュメント本文）
        requestBody.put("include", new JSONArray(List.of("distances", "documents")));
        // 取得する件数（ここでは上位3件）
        requestBody.put("n_results", 3);
        // クエリベクトルを指定
        requestBody.put("query_embeddings", queryEmbeddings);

        // HTTPヘッダー設定（JSON送信）
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // HTTPリクエストエンティティを作成
        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

        try {
            // 4. ChromaDBにPOSTリクエストを送信し、検索結果を取得
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            // レスポンスが正常なら、ドキュメント部分を抽出して結合
            if (response.getStatusCode().is2xxSuccessful()) {
                JSONObject body = new JSONObject(response.getBody());
                // 検索結果のドキュメント部分（最も類似したものから順に配列で取得）
                JSONArray documents = body.getJSONArray("documents").getJSONArray(0);
                // 複数ドキュメントを改行区切りで結合
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < documents.length(); i++) {
                    sb.append(documents.getString(i)).append("\n");
                }
                return sb.toString();
            }
        } catch (Exception e) {
            // エラー発生時は詳細を標準エラー出力に出力し、空文字を返す
            System.err.println("ChromaDBエラー: " + e.getMessage());
        }
        // 正常に取得できなかった場合は空文字を返す
        return "";
    }
}
