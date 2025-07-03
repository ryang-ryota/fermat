package io.github.ryang_ryota.fermat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * このクラスは、チャット処理のビジネスロジックを担当するサービスクラスです。
 * - ユーザーの質問から関連コンテキストを取得（ChromaDB）
 * - AIプロンプトを構築
 * - Ollamaを呼び出し、ストリーミング形式でAI回答を取得
 */
@Service
public class ChatService {

    // ChromaDB（ベクトルストア）操作サービス
    private final ChromaService chromaService;
    // Ollama（AIモデル）操作サービス
    private final OllamaService ollamaService;

    /**
     * 依存性注入コンストラクタ
     * @param chromaService ChromaDB操作サービス
     * @param ollamaService Ollama操作サービス
     */
    @Autowired
    public ChatService(ChromaService chromaService, OllamaService ollamaService) {
        this.chromaService = chromaService;
        this.ollamaService = ollamaService;
    }

    /**
     * チャット処理のメインフロー（ストリーミング対応）
     * 1. ChromaDBから関連コンテキスト取得
     * 2. プロンプト構築
     * 3. Ollamaでストリーミング回答生成
     * @param query ユーザーからの質問
     * @return AIの回答トークンを逐次返すFluxストリーム
     */
    public Flux<String> processChatStream(String query) {
        // ステップ1: ChromaDBから関連コンテキストを取得
        // - 質問内容に基づき、ベクトル検索で関連知識を抽出
        String context = chromaService.retrieveContext(query);

        // ステップ2: AIプロンプトを構築
        // - 取得したコンテキストと質問を組み合わせた指示文を作成
        String prompt = String.format(
                "あなたは数学の専門家です。以下の文脈を参考に日本語で質問に答えてください。\n\n" +
                        "【文脈】\n%s\n\n" +
                        "【質問】%s\n" +
                        "【回答】",
                context, query
        );

        // ステップ3: OllamaでAI回答をストリーミング形式で生成
        // - 構築したプロンプトをAIモデルに送信
        // - 回答をトークン単位で逐次返すFluxストリームを返却
        return ollamaService.generateResponseStream(prompt);
    }
}
