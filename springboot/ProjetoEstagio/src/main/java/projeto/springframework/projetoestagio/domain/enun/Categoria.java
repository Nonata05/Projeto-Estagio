package projeto.springframework.projetoestagio.domain.enun;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.util.Map;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)

@Getter
public enum Categoria {

    PROGRAMACAO("Programação", "#7C3AED"), // Roxo
    DADOS_LOGICA("Dados & Lógica", "#059669"), // Verde
    DESIGN("Design", "#2563EB"); // Exemplo de outra categoria (Azul)

    private final String displayName;
    private final String corHex;

    Categoria(String displayName, String corHex) {
        this.displayName = displayName;
        this.corHex = corHex;
    }

    public String getCod() {
        return this.name(); // Retorna "PROGRAMACAO", "DADOS_LOGICA", etc.
    }
    @JsonCreator
    public static Categoria fromValue(Object value) {
        if (value == null) {
            return null;
        }

        // Se o frontend enviar uma String simples (ex: "PROGRAMACAO")
        if (value instanceof String) {
            return Categoria.valueOf(((String) value).toUpperCase().trim());
        }

        // Se o frontend enviar um objeto JSON (ex: {"cod": "PROGRAMACAO"})
        if (value instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) value;
            Object cod = map.get("cod");
            if (cod == null) {
                cod = map.get("name");
            }
            if (cod != null) {
                return Categoria.valueOf(cod.toString().toUpperCase().trim());
            }
        }

        throw new IllegalArgumentException("Categoria inválida: " + value);
    }

}
