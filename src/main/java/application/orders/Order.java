package application.orders;

import application.portfolios.Portfolio;
import application.securities.Security;
import portfolio.OrderType;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Orders")
public class Order {

    @Id
    @GeneratedValue
    private long id;

    @ManyToOne
    private Portfolio portfolio;

    @ManyToOne
    private Security security;

    private OrderType type;

    private int quantity;

    private Date date;

    private Order() {
    }

    public Order(Portfolio portfolio, OrderType type, Security security, int quantity, Date date) {
        this.portfolio = portfolio;
        this.type = type;
        this.security = security;
        this.quantity = quantity;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public Security getSecurity() {
        return security;
    }

    public OrderType getType() {
        return type;
    }

    public int getQuantity() {
        return quantity;
    }

    public Date getDate() {
        return date;
    }

    public long getPortfolioId() {
        return portfolio.getId();
    }

    public void update(Order order) {
        this.type = order.getType();
        this.security = order.getSecurity();
        this.quantity = order.getQuantity();
        this.date = order.getDate();
    }

}
