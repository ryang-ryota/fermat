// メッセージ型定義
export interface Message {
  text: string;
  isUser: boolean;
}

// SSEイベント型定義
export interface SSEEvent {
  content: string;
}
