# AIチャットアプリ

**OpenAlex API** から取得したフェルマーの最終定理に関する論文をベクトルストア（Chroma）に保存し、Spring Boot＋Ollama（Mistral 7B）＋React でチャットできるAIアプリケーションです。

---

## システム構成

```
fermat-project/
├── data-loader/ # データセットをChromaに登録するPythonアプリ
├── chat-api/ # Spring Boot チャットAPI
├── frontend/ # React + TypeScript チャットUI
├── docker-compose.yml
└── .env
```

---

## 主な技術

- **データベース**: Chroma (ベクトルストア)
- **AIモデル**: Ollama (Mistral 7B)
- **ベクトル化モデル**: all-MiniLM-L6-v2 (langchain4j)
- **APIサーバー**: Spring Boot (Java, WebFlux, SSE)
- **フロントエンド**: React + Vite + TypeScript + Tailwind CSS
- **データ取得**: OpenAlex API

---

## クイックスタート

### 1. リポジトリをクローン

```commandline
git clone https://github.com/your-username/fermat-project.git
cd fermat-project
```

### 2. 必要な環境

- Docker

### 3. .envファイルを作成

```
DATAVERSE_DOI=doi:10.2307/2118559
OLLAMA_MODEL=mistral
CHROMA_COLLECTION=fermat-collection
```

### 4. サービス起動

1. Chroma, Ollama, API, Frontendを起動
```commandline
docker compose up -d 
```

2. Ollamaコンテナ内でモデルをpull
```commandline
docker exec -it ollama ollama pull mistral
```

3. 必要に応じてデータ登録
```commandline
docker compose run --rm data-loader
```

### 5. アクセス

- フロントエンド: [http://localhost:5173](http://localhost:5173)
- チャットAPI: [http://localhost:8080/chat/stream](http://localhost:8080/chat/stream)

---

## 使い方

1. 質問をチャット画面に入力
2. AIがChromaベクトルストアから関連知識を検索
3. Ollama（Mistral 7B）が日本語で分かりやすく回答

---

## ディレクトリ説明

- **data-loader/**  
  OpenAlex APIから論文データを取得し、Chromaに登録するPythonスクリプト

- **chat-api/**  
  Spring Boot製APIサーバー。Chromaから文脈を検索し、Ollamaにプロンプトを送ってSSEでストリーミング応答

- **frontend/**  
  React + TypeScript + Tailwind CSSのチャットUI。SSEでAIの回答をリアルタイム表示

---

## 開発・カスタマイズ

- **Ollamaモデルの変更**  
  `.env`の`OLLAMA_MODEL`を変更し、`docker exec -it ollama ollama pull <model名>`でモデルをpullしてください。

- **Chromaコレクション名の変更**  
  `.env`の`CHROMA_COLLECTION`を編集してください。

- **データセットの追加**  
  `data-loader/main.py`を編集し、OpenAlex APIの検索条件や登録件数を調整してください。

---

## ライセンス

MIT License

---

## クレジット

- [Ollama](https://ollama.com/)
- [ChromaDB](https://www.trychroma.com/)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [OpenAlex](https://openalex.org/)
- [Mistral 7B](https://mistral.ai/)
- [React](https://react.dev/)

