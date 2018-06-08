package application.securities.prices;

import application.securities.Security;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Entity
public class Price {

    @Id
    @GeneratedValue
    private long id;

    @NotNull
    private Date date;

    @ManyToOne
    @NotNull
    private Security security;

    @NotNull
    private BigDecimal value;

    private Price() {
    }

    public Price(Date date, Security security, BigDecimal value) {
        this.date = date;
        this.security = security;
        this.value = value;
    }

    public long getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public Security getSecurity() {
        return security;
    }

    public BigDecimal getValue() {
        return value;
    }
}
