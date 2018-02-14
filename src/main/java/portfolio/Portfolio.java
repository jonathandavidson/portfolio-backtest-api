package portfolio;

import java.util.ArrayList;
import java.util.List;

public class Portfolio {
    private List<Security> securities = new ArrayList<>();

    public void addSecurity(Security security) {
        this.securities.add(security);
    }

    public List<Security> getSecurities() {
        return securities;
    }
}
