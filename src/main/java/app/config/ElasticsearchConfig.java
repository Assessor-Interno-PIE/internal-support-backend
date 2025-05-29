// app/config/ElasticsearchConfig.java
package app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "app.repository.elasticsearch")
public class ElasticsearchConfig {
}
