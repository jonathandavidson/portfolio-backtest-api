package application.portfolios;

import application.orders.Order;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
public class Portfolio {

    @Id
    @GeneratedValue
    private long id;

    @NotNull
    private String name;

    private String description;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.REMOVE)
    private List<Order> orders;

    private Portfolio() {
    }

    public Portfolio(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void update(Portfolio portfolio) {
        if (portfolio.getName() != null)
            this.name = portfolio.name;

        if (portfolio.getDescription() != null)
            this.description = portfolio.description;
    }
}
