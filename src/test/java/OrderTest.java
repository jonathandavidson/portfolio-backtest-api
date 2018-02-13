import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Date;

public class OrderTest {

    @Rule public ExpectedException thrown = ExpectedException.none();

    @Test public void throwInvalidOrderExceptionWhenBuyOrderHasNegativeQuantity() throws InvalidOrderException {
        thrown.expect(InvalidOrderException.class);
        thrown.expectMessage("Buy orders must have a positive quantity");
        Order order = new Order(OrderType.BUY, -1, new Date());
    }

    @Test public void throwInvalidOrderExceptionWhenSellOrderHasPositiveQuantity() throws InvalidOrderException {
        thrown.expect(InvalidOrderException.class);
        thrown.expectMessage("Sell orders must have a negative quantity");
        Order order = new Order(OrderType.SELL, 1, new Date());
    }
}
