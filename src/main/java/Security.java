import java.math.BigDecimal;
import java.util.*;

public class Security {
    private List<Order> orders = new ArrayList<>();
    private List<Price> prices = new ArrayList<>();
    private String unit = "shares";

    public void addOrder(Order order) throws InvalidOrderException {
        if (orderIsValid(order) == true) {
            orders.add(order);
            Collections.sort(orders);
        } else {
            throw new InvalidOrderException("Can not sell more of a security than owned");
        }
    }

    public void addPrice(Date time, BigDecimal price) {
        prices.add(new Price(time, price));
        Collections.sort(prices);
    }

    public List<Order> getOrders() {
        return this.orders;
    }

    public Holding getHolding(Date date) {
        Integer totalQuantity = getTotalQuantity(date);
        BigDecimal value = getValue(date);

        return new Holding(date, totalQuantity, this.unit, value);
    }

    private boolean orderIsValid(Order order) {
        if (order.getType() == OrderType.SELL) {
            Integer totalQuantity = getTotalQuantity(order.getDate());

            if (-order.getQuantity() > totalQuantity) {
                return false;
            }
        }

        return true;
    }

    private Integer getTotalQuantity(Date date) {
        return orders
                .stream()
                .filter(p -> p.getDate().compareTo(date) < 0)
                .mapToInt(Order::getQuantity)
                .sum();
    }

    private BigDecimal getValue(Date date) {
        Price price = prices
                .stream()
                .filter(p -> p.getDate().compareTo(date) <= 0)
                .reduce((a, b) -> b)
                .get();


        return price.getValue();
    }
}
