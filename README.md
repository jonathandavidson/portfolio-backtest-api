# Portfolio Backtesting API

## Resources

### Portfolios

A Portfolio is a resource that houses a group of Holdings over a period of time.

* `GET` `/application.portfolios` - Returns a list of application.portfolios for the current user
* `GET` `/application.portfolios/{id}` - Returns a single portfolio
* `PUT` `/application.portfolios/{id}` - Updates a portfolio
* `POST` `/application.portfolios` - Creates a new portfolio
* `DELETE` `/application.portfolios/{id}` - Deletes a single portfolio

### Orders

An order represents a purchase or sale of a security within a portfolio.

* `GET` `/portfolio/{id}/orders` - Returns the list of orders for a given portfolio
* `PUT` `/portfolio/{id}/orders/{id}` - Updates an order
* `POST` `/portfolio/{id}/orders` - Creates a new order in a given application.portfolios
* `DELETE` `/portfolio/{id}/orders/{id}` - Deletes an order

### Securities

A financial security that can be purchased or sold through an order on a portfolio.

* `GET` `/securities` - Returns a list of securities

### Holdings

A snapshot of values and statistics of securities held over a period of time

* `GET` `portfolio/{id}/holdings&start={mm/dd/yyyy}&end={mm/dd/yyyy}&interval={days}` - Returns holdings between the date range and interval specified
