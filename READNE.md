To Run: Execute the main class
Main Class : com.myinterview.orderprocess.OrderProcessingMain

Applied Rules: 
1) For a given symbol if more then 3 request comes less then one second gap ignore the order after the 3rd request
until time gap is great 1 sec.
2) Request symbol part of active symbols
3) If LIMIT request the price > 0.0

OrderProcessingMain : Main class defined here.

LimitOrder : For limit order
MarketOrder : For Market order
EventFactory: 
- Loads static list of active symbols. 
- Setup listener thread for incoming events
- Validates the incoming events
- Publishes the valid event for further processing to the matching engine to match order
StreamOrder:
- Open file stream for the order input data file
- Creates the Limit/Market order event for incoming request and pushes
them to the event factory for validation and further processing.
TradeMatchingEngine:
- Takes the valid events coming from the event factory and execute them applying the matching rules.
- Matching rules are defined inside the matching class as ExecuteOrder
OrphanSweep:
- Waits for given amount of time and then runs to generate the 3 output file and exit (or keep it running).
- Need directory /tmp/

Results files: Three files are generated and the should be inside the resource directory.
-RejectedOrders.txt
-TradeExecuted.txt
-OrphanTrades.txt