package application.securities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
public class Security {

    @Id
    @GeneratedValue
    private long id;

    @Column(unique = true)
    @NotNull
    private String symbol;

    private Security() {
    }

    public Security(String symbol) {
        this.symbol = symbol;
    }

    public long getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

}
