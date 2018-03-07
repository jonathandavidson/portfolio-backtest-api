package application.orders;

import portfolio.OrderType;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name="Orders")
public class Order {

    @Id
    @GeneratedValue
    private long id;
    private OrderType type;
    private int quantity;
    private Date date;

    private Order() {
    }

    public Order(OrderType type, int quantity, Date date) {
        this.type = type;
        this.quantity = quantity;
        this.date = date;
    }

    public long getId() {
        return id;
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
