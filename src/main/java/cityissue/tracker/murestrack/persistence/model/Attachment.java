package cityissue.tracker.murestrack.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.sql.Blob;

@Entity
@Table(name = "`Attachment`")
@Data
@Builder()
@NoArgsConstructor
@AllArgsConstructor
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    @Column(name = "ID")
    private Long id;

    @Column(name = "file_name", length = 30)
    @Length(max = 30)
    private String fileName;

    @Column(name = "type")
    private String type;

    @Lob
    private byte[] data;
    public Attachment(String fileName, String type, byte[] data) {
        this.fileName = fileName;
        this.type = type;
        this.data = data;
    }
}
