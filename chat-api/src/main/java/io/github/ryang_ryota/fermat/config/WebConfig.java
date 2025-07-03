package io.github.ryang_ryota.fermat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * このクラスは、Spring BootアプリケーションのCORS（クロスオリジンリソースシェアリング）設定を行うための設定クラスです。
 * - フロントエンド（React, Vite, ポート5173）からのAPIリクエストを許可します。
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * CORSの詳細設定を行うメソッド。
     * 
     * @param registry CORS設定を追加するためのレジストリオブジェクト
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // すべてのパス（/**）に対してCORSを許可
        registry.addMapping("/**")
                // フロントエンド（Viteのデフォルトポート）からのアクセスのみ許可
                .allowedOrigins("http://localhost:5173")
                // 許可するHTTPメソッドを指定（GET, POST）
                .allowedMethods("GET", "POST")
                // すべてのヘッダーを許可
                .allowedHeaders("*")
                // Cookieなどの認証情報を許可
                .allowCredentials(true);
    }
}
