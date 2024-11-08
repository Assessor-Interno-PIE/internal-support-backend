package app.dto;

import app.entity.Department;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentRequestDTO {

    private String title;
    private Department department;

    @Size(max = 999999999, message = "O tamanho do PDF em Base64 não pode exceder 10MB") // Limite de 10MB (10MB * 1024 * 1024 = 10485760 bytes)
    private String pdfBase64;
}
