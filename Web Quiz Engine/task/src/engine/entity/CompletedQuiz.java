package engine.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.cache.spi.support.SimpleTimestamper;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "CompletedQuizzes")
public class CompletedQuiz {
    @Id
    @GeneratedValue
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int idNumber;
    private int id;
    private LocalDateTime completedAt;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String name;

    public CompletedQuiz() {}

    public CompletedQuiz(int id, String name) {
        this.id = id;
        this.name = name;
        completedAt = LocalDateTime.now();
    }

    public int getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(int idNumber) {
        this.idNumber = idNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
