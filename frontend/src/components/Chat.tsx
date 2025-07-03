/**
 * 質問をAIに送信し、ストリーミングで回答を受信・表示するチャット画面コンポーネント。
 *
 * - ユーザーが質問を送信すると、バックエンド（Spring Boot）へリクエストが送られます。
 * - AI（Ollama）が返すストリーミングレスポンスを、リアルタイムで画面に表示します。
 * - メッセージはユーザーとAIで左右に分けて表示されます。
 */

import React, { useState, useRef, useEffect } from "react";
import { type Message } from "../types";

// バックエンドAPIのベースURL
const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

/**
 * Chatコンポーネント
 * チャットの送信・受信・表示を行うメインUI
 */
const Chat: React.FC = () => {
  // 画面に表示する全メッセージのリスト（ユーザー・AI両方）
  const [messages, setMessages] = useState<Message[]>([]);
  // 入力欄の内容（ユーザーが今入力している質問）
  const [inputValue, setInputValue] = useState<string>("");
  // メッセージリストの一番下の要素への参照（自動スクロール用）
  const messagesEndRef = useRef<HTMLDivElement>(null);
  // サーバーとのSSE（ストリーミング）接続の参照
  const eventSourceRef = useRef<EventSource | null>(null);
  // AIメッセージの一時バッファ（ストリーミングで受信した内容を逐次結合）
  const aiMessageBuffer = useRef<string>("");

  /**
   * メッセージが追加されたとき、画面を自動で一番下までスクロールする
   */
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  /**
   * コンポーネントがアンマウント（画面から消える）時にSSE接続をクローズする
   * リソースリークや不要な通信を防ぐため
   */
  useEffect(() => {
    return () => {
      if (eventSourceRef.current) {
        eventSourceRef.current.close();
      }
    };
  }, []);

  /**
   * フォーム送信時の処理
   * - ユーザーの質問をmessagesに追加
   * - AIメッセージのプレースホルダーを追加
   * - サーバーとのSSE接続を開始し、AIの回答をストリーミングで受信
   */
  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!inputValue.trim()) return; // 空文字は送信しない

    // ユーザーメッセージをリストに追加
    const userMessage: Message = { text: inputValue, isUser: true };
    setMessages((prev) => [...prev, userMessage]);
    setInputValue(""); // 入力欄をクリア

    // AIメッセージの空プレースホルダーを追加（ここにストリームで内容を追加していく）
    setMessages((prev) => [...prev, { text: "", isUser: false }]);
    aiMessageBuffer.current = ""; // バッファも初期化

    // 既存のSSE接続があればクローズ（多重接続を防ぐため）
    if (eventSourceRef.current) {
      eventSourceRef.current.close();
    }

    // サーバーに質問を送り、SSEでAIの回答を受信
    eventSourceRef.current = new EventSource(
      `${API_BASE_URL}/chat/stream?query=${encodeURIComponent(
        userMessage.text
      )}`
    );

    /**
     * サーバーからストリーミングでAIの回答（トークン単位）が届くたびに呼ばれる
     * - aiMessageBufferに内容を追加
     * - 最新のAIメッセージ（リストの末尾）にバッファの内容を反映
     */
    eventSourceRef.current.onmessage = (event: MessageEvent) => {
      aiMessageBuffer.current += event.data;
      setMessages((prev) => {
        const newMessages = [...prev];
        const lastIndex = newMessages.length - 1;
        // 最後のメッセージがAIの場合のみ内容を更新
        if (lastIndex >= 0 && !newMessages[lastIndex].isUser) {
          newMessages[lastIndex] = {
            ...newMessages[lastIndex],
            text: aiMessageBuffer.current,
          };
        }
        return newMessages;
      });
    };

    /**
     * エラー発生時の処理
     * - 接続をクローズしてリソースを解放
     * - 必要に応じてエラーメッセージ表示も追加可能
     */
    eventSourceRef.current.onerror = () => {
      if (eventSourceRef.current) {
        eventSourceRef.current.close();
      }
    };
  };

  return (
    <div className="flex flex-col h-screen max-w-2xl mx-auto p-4 bg-gray-50">
      {/* メッセージ表示エリア */}
      <div className="flex-1 overflow-y-auto mb-4 space-y-3">
        {messages.map((msg, index) => (
          <div
            key={index}
            // ユーザーとAIで色や配置を切り替え
            className={`p-3 rounded-lg max-w-xs md:max-w-md break-words ${
              msg.isUser
                ? "bg-blue-500 text-white self-end ml-auto"
                : "bg-gray-200 self-start"
            }`}
          >
            {msg.text}
          </div>
        ))}
        {/* スクロール用のダミー要素 */}
        <div ref={messagesEndRef} />
      </div>

      {/* 入力フォームエリア */}
      <form onSubmit={handleSubmit} className="flex gap-2">
        <input
          type="text"
          value={inputValue}
          onChange={(e) => setInputValue(e.target.value)}
          className="flex-1 p-2 border rounded-lg focus:ring-2 focus:ring-blue-300"
          placeholder="質問を入力..."
        />
        <button
          type="submit"
          className="bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-600 transition"
        >
          送信
        </button>
      </form>
    </div>
  );
};

export default Chat;
