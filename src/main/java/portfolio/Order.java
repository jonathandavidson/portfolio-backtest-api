package portfolio;

import java.util.Date;

public class Order implements Comparable<Order> {
    private OrderType type;
    private int quantity;
    private Date date;

    Order(OrderType type, int quantity, Date date) throws InvalidOrderException {
        if (type == OrderType.BUY && quantity < 0) {
            throw new InvalidOrderException("Buy orders must have a positive quantity");
        } else if (type == OrderType.SELL && quantity > 0) {
            throw new InvalidOrderException("Sell orders must have a negative quantity");
        }

        this.type = type;
        this.quantity = quantity;
        this.date = date;
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

    @Override
    public int compareTo(Order order) {
        return this.getDate().compareTo(order.getDate());
    }
}
