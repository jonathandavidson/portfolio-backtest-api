import java.math.BigDecimal;
import java.util.Date;

public class Holding {
    private Date date;
    private int quantity;

    private String unit;
    private BigDecimal value;

    Holding(Date date, int quantity, String unit, BigDecimal value) {
        this.date = date;
        this.quantity = quantity;
        this.unit = unit;
        this.value = value;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return this.unit;
    }

    public BigDecimal getValue() {
        return value;
    }

    public Date getDate() {
        return date;
    }
}
