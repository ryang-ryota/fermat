/**
 * アプリケーション全体のレイアウトを定義するメインコンポーネントです。
 * - ヘッダー、メイン（チャット画面）、フッターで構成されています。
 * - Chat.tsx（チャット機能コンポーネント）を呼び出します。
 */

import React from "react";
import Chat from "./components/Chat";

/**
 * Appコンポーネント
 * アプリ全体の枠組み・デザインを担当します。
 */
const App: React.FC = () => {
  return (
    // アプリ全体の背景と最大幅を設定
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100">
      {/* ヘッダー部分：アプリタイトルと説明 */}
      <header className="bg-white shadow-sm py-4">
        <div className="max-w-2xl mx-auto px-4">
          <h1 className="text-2xl font-bold text-blue-600">
            チャットアシスタント
          </h1>
          <p className="text-gray-600">
            フェルマーの最終定理に関する質問に答えます
          </p>
        </div>
      </header>

      {/* メイン部分：チャット画面を表示 */}
      <main className="py-6">
        <Chat />
      </main>

      {/* フッター部分 */}
      <footer className="bg-white py-4 mt-8">
        <div className="max-w-2xl mx-auto px-4 text-center text-gray-500 text-sm">
          Powered by SpringAI, Ollama, and ChromaDB
        </div>
      </footer>
    </div>
  );
};

export default App;
