import chromadb
from chromadb.config import Settings

# DockerコンテナのChromaDBに接続
client = chromadb.HttpClient(host="localhost", port=8000)  # docker-composeの設定に合わせて変更

# コレクション名を指定
collection = client.get_collection("fermat")

# コレクション内の全データを取得（最大件数を指定可能）
results = collection.get()
print("登録ドキュメント数:", len(results["documents"]))
for i, doc in enumerate(results["documents"]):
    print(f"--- Document {i+1} ---")
    print(doc)
    print()
