package portfolio;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class SecurityTest {

    private Security createSecurityWithOrders(List<Order> orders) throws InvalidOrderException {
        Security security = new Security();
        security.addPrice(new Date(1514764800000L), BigDecimal.valueOf(1.00));
        security.addPrice(new Date(1515542400000L), BigDecimal.valueOf(2.1));
        security.addPrice(new Date(1516406400000L), BigDecimal.valueOf(3.5));
        security.addPrice(new Date(1515110400000L), BigDecimal.valueOf(1.5));

        for (Order order:orders) {
            security.addOrder(order);
        }

        return security;
    }


    private Order createOrder(OrderType type, int quantity, long timestamp) throws InvalidOrderException {
        return new Order(type, quantity, new Date(timestamp));
    }

    @Test public void getOrdersReturnsAddedOrdersSortedByDate() throws InvalidOrderException {
        List<Order> expectedOrders = new ArrayList<>();
        expectedOrders.add(createOrder(OrderType.BUY, 1, 1515110400000L));
        expectedOrders.add(createOrder(OrderType.SELL, -1, 1515542400000L));
        expectedOrders.add(createOrder(OrderType.BUY, 1, 1514764800000L));

        Security security = createSecurityWithOrders(expectedOrders);
        List<Order> orders = security.getOrders();

        assertEquals(expectedOrders.get(2), orders.get(0));
        assertEquals(expectedOrders.get(0), orders.get(1));
        assertEquals(expectedOrders.get(1), orders.get(2));
    }

    @Rule public ExpectedException thrown = ExpectedException.none();

    @Test public void addOrderThrowsExceptionWhenSellingMoreUnitsThanOwned() throws InvalidOrderException {
        List<Order> expectedOrders = new ArrayList<>();
        expectedOrders.add(createOrder(OrderType.BUY, 2, 1514764800000L));
        expectedOrders.add(createOrder(OrderType.SELL, -1, 1514764900000L));
        expectedOrders.add(createOrder(OrderType.BUY, 1, 1515542400000L));

        Security security = createSecurityWithOrders(expectedOrders);

        thrown.expect(InvalidOrderException.class);
        thrown.expectMessage("Can not sell more of a security than owned");

        Order order3 = createOrder(OrderType.SELL, -2, 1515110400000L);
        security.addOrder(order3);
    }

    @Test public void getHoldingReturnsCorrectlyCalculatedHolding() throws InvalidOrderException {
        List<Order> expectedOrders = new ArrayList<>();
        expectedOrders.add(createOrder(OrderType.BUY, 5, 1514764800000L));
        expectedOrders.add(createOrder(OrderType.SELL, -1, 1515110400000L));
        expectedOrders.add(createOrder(OrderType.BUY, 2, 1516406400000L));

        Security security = createSecurityWithOrders(expectedOrders);
        Holding holding = security.getHolding(new Date(1515542400000L));

        assertEquals(4, holding.getQuantity());
        assertEquals(BigDecimal.valueOf(2.1), holding.getValue());
    }
}
