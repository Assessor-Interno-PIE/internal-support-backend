package app.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "internal_logs")
public class Log {
    @Id
    private String id = UUID.randomUUID().toString();  // já gera UUID automaticamente

    private String acao;
    private String usuario;
    private String dataHora;
    private String endpoint;
    private String descricao;

    // Se quiser garantir que sempre gere um id ao criar uma instância sem id, pode criar construtor custom:
    public Log(String acao, String usuario, String dataHora, String endpoint, String descricao) {
        this.id = UUID.randomUUID().toString();
        this.acao = acao;
        this.usuario = usuario;
        this.dataHora = dataHora;
        this.endpoint = endpoint;
        this.descricao = descricao;
    }
}
