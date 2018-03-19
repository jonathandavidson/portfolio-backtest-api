package application.orders;

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
    private Security security;

    private OrderType type;

    private int quantity;

    private Date date;

    private Order() {
    }

    public Order(OrderType type, Security security, int quantity, Date date) {
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

}