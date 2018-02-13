import java.math.BigDecimal;
import java.util.Date;

class Price implements Comparable<Price> {
    Date date;
    BigDecimal value;

    Price(Date date, BigDecimal value) {
        this.date = date;
        this.value = value;
    }

    public Date getDate() {
        return date;
    }

    public BigDecimal getValue() {
        return value;
    }

    @Override
    public int compareTo(Price price) {
        return this.getDate().compareTo(price.getDate());
    }
}