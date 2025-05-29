// LogRepository.java
package app.repository.elasticsearch;

import app.entity.Log;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElasticLogRepository extends ElasticsearchRepository<Log, String> {
}
