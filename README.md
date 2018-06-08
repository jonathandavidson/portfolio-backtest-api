# Portfolio Backtesting API

## Resources

### Portfolios

A Portfolio is a resource that houses a group of Holdings over a period of time.

* `GET` `/portfolios` - Returns a list of portfolios for the current user
* `GET` `/portfolios/{id}` - Returns a single portfolio
* `PUT` `/portfolios/{id}` - Updates a portfolio
* `POST` `/portfolios` - Creates a new portfolio
* `DELETE` `/portfolios/{id}` - Deletes a single portfolio

### Orders

An order represents a purchase or sale of a security within a portfolio.

* `GET` `/portfolios/{id}/orders` - Returns the list of orders for a given portfolio
* `PUT` `/portfolios/{id}/orders/{id}` - Updates an order
* `POST` `/portfolios/{id}/orders` - Creates a new order in a given application.portfolios
* `DELETE` `/portfolios/{id}/orders/{id}` - Deletes an order

### Securities

A financial security that can be purchased or sold through an order on a portfolio.

* `GET` `/securities` - Returns a list of securities

### Prices

Prices for a given security at a specific time

* `GET` `/securities/{id}/prices` - Returns all prices for a security
* `GET` `/securities/{id}/prices?start={mm/dd/yyyy}&end={mm/dd/yyyy}` - Returns prices for a security between the date range specified

### Holdings

A snapshot of values and statistics of securities held over a period of time

* `GET` `portfolios/{id}/holdings?start={mm/dd/yyyy}&end={mm/dd/yyyy}&interval={days}` - Returns holdings between the date range and interval specified
