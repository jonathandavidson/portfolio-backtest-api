package application.securities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Security {

    @Id
    @GeneratedValue
    private long id;

    @Column(unique = true)
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
