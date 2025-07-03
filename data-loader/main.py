# main.py
# フェルマーの最終定理に関する論文データをOpenAlex APIから取得し、
# ベクトルストア（ChromaDB）に保存するサンプルアプリです。
# 初心者向けに詳細なコメントとログ出力を記載しています。

import requests  # Web APIにアクセスするためのライブラリ
from sentence_transformers import SentenceTransformer  # テキストをベクトル化するライブラリ
import chromadb  # ベクトルストア用ライブラリ
import sys

def log(message):
    """ログを標準出力に出す関数"""
    print(f"[LOG] {message}")

def main():
    # 1. OpenAlex APIから論文データを取得
    DOI = "https://doi.org/10.2307/2118559"
    OPENALEX_URL = f"https://api.openalex.org/works?filter=doi:{DOI}"

    log(f"OpenAlex APIからデータ取得を開始します: {OPENALEX_URL}")
    try:
        response = requests.get(OPENALEX_URL)
        response.raise_for_status()  # HTTPエラーがあれば例外を発生
    except Exception as e:
        log(f"APIリクエストでエラーが発生しました: {e}")
        sys.exit(1)

    data = response.json()
    log(f"APIレスポンスを受信しました: {data}")

    # 2. 結果が存在するか確認
    if "results" not in data or not data["results"]:
        log("データが見つかりませんでした。プログラムを終了します。")
        sys.exit(1)

    # 3. 必要な情報（タイトル・要旨）を抽出
    work = data["results"][0]
    title = work.get("title", "")
    abstract = work.get("abstract", "")

    log(f"論文タイトル: {title}")
    log(f"論文要旨（abstract）: {abstract}")

    # 4. タイトルと要旨を1つのテキストにまとめる
    text = f"{title}\n{abstract}"

    # 5. テキストをベクトル化（Embedding生成）
    log("SentenceTransformerモデルをロードします（初回は時間がかかります）")
    try:
        model = SentenceTransformer('all-MiniLM-L6-v2')
    except Exception as e:
        log(f"モデルのロードに失敗しました: {e}")
        sys.exit(1)

    log("テキストをベクトル（数値配列）に変換します")
    try:
        embedding = model.encode([text])[0]
    except Exception as e:
        log(f"ベクトル化でエラーが発生しました: {e}")
        sys.exit(1)

    log(f"ベクトル化が完了しました（ベクトルの長さ: {len(embedding)}）")

    # 6. ChromaDBに保存
    log("ChromaDB（ベクトルストア）に接続します")
    try:
        client = chromadb.HttpClient(host='chroma-db', port=8000)
        collection = client.get_or_create_collection("fermat")  # コレクション名は任意

    except Exception as e:
        log(f"ChromaDBへの接続に失敗しました: {e}")
        sys.exit(1)

    log("ベクトルとメタデータをChromaDBに保存します")
    try:
        collection.add(
            documents=[text], # 元のテキスト
            embeddings=[embedding.tolist()], # ベクトル（リスト形式）
            metadatas=[{"doi": DOI, "title": title}], # メタデータ
            ids=["fermat-1"]
        )
    except Exception as e:
        log(f"ChromaDBへの保存でエラーが発生しました: {e}")
        sys.exit(1)

    log("保存が完了しました！プログラムを終了します。")

if __name__ == "__main__":
    main()
