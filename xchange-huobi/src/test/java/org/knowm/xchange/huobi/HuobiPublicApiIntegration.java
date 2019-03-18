package org.knowm.xchange.huobi;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trades;
import org.knowm.xchange.service.marketdata.MarketDataService;

public class HuobiPublicApiIntegration {

	private Exchange exchange;

	@Before
	public void setup() {
		exchange = ExchangeFactory.INSTANCE.createExchange(HuobiExchange.class.getName());
	}

	@Test
	public void getTickerTest() throws Exception {
		MarketDataService marketDataService = exchange.getMarketDataService();
		Ticker ticker = marketDataService.getTicker(CurrencyPair.BTC_USDT);
		System.out.println(ticker.toString());
		ticker = marketDataService.getTicker(CurrencyPair.ETC_BTC);
		System.out.println(ticker.toString());

		assertThat(ticker).isNotNull();
		assertThat(ticker.getBid()).isGreaterThan(BigDecimal.ZERO);
		assertThat(ticker.getAsk()).isGreaterThan(BigDecimal.ZERO);
	}

	@Test
	public void getExchangeSymbolsTest() {
		List<CurrencyPair> exchangeSymbols = exchange.getExchangeSymbols();
		System.out.println(exchangeSymbols.toString());
		assertThat(exchangeSymbols).isNotNull();
		assertThat(exchangeSymbols).size().isGreaterThan(0);
	}

	/**
	 * 测试所有交易对行情
	 * 
	 * @author liuhb
	 */
	@Test
	public void getTickersTest() throws Exception {
		MarketDataService marketDataService = exchange.getMarketDataService();
		List<Ticker> tickers = marketDataService.getTickers(null);
		System.out.println(tickers);
		assertThat(tickers).isNotNull();
		assertThat(tickers).size().isGreaterThan(0);
	}

	/**
	 * 测试订单薄
	 * 
	 * @author liuhb
	 * @throws Exception
	 */
	@Test
	public void getOrderBookTest() throws Exception {
		MarketDataService marketDataService = exchange.getMarketDataService();
		OrderBook orderBook = marketDataService.getOrderBook(CurrencyPair.ETH_BTC);
		System.out.println(orderBook.toString());

		assertThat(orderBook.getAsks()).isNotNull();
		assertThat(orderBook.getBids()).isNotNull();

	}

	/**
	 * 测试交易所当前交易对的所有交易数据
	 * 
	 * @throws Exception
	 */
	@Test
	public void getTradesTest() throws Exception {
		MarketDataService marketDataService = exchange.getMarketDataService();
		Trades trades = marketDataService.getTrades(CurrencyPair.ETH_BTC);
		System.out.println(trades.toString());
		assertThat(trades).isNotNull();
		assertThat(trades.getTrades()).size().isGreaterThan(0);

	}
}
