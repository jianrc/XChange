package org.knowm.xchange.huobi;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.dto.trade.OpenOrders;
import org.knowm.xchange.huobi.dto.account.HuobiAccount;
import org.knowm.xchange.huobi.service.HuobiAccountService;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.trade.TradeService;

public class HuobiPrivateApiIntegration {

  private HuobiProperties properties;
  private Exchange exchange;

  @Before
  public void setup() throws IOException {
    properties = new HuobiProperties();
    Assume.assumeTrue("Ignore tests because credentials are missing", properties.isValid());
    exchange =
        ExchangeFactory.INSTANCE.createExchange(
            HuobiExchange.class, properties.getApiKey(), properties.getSecretKey());
  }

  @After
  public void teardown() throws IOException {
    if (exchange != null) {
      for (LimitOrder order : exchange.getTradeService().getOpenOrders().getOpenOrders()) {
        exchange.getTradeService().cancelOrder(order.getId());
      }
    }
  }

  @Test
  public void getAccountTest() {
    try {
      HuobiAccountService accountService = (HuobiAccountService) exchange.getAccountService();
      HuobiAccount[] accounts = accountService.getAccounts();
      System.out.println(Arrays.toString(accounts));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 获取对应资产的地址
   *
   * @throws Exception
   */
  @Test
  public void getDepositAddressTest() throws Exception {
    HuobiAccountService accountService = (HuobiAccountService) exchange.getAccountService();
    String requestDepositAddress = accountService.requestDepositAddress(Currency.ETH);
    System.out.println(requestDepositAddress);
  }

  /**
   * 获取对应资产的余额
   *
   * @throws IOException
   */
  @Test
  public void getBalanceTest() throws IOException {
    AccountService accountService = exchange.getAccountService();
    Balance balance = accountService.getAccountInfo().getWallet().getBalance(Currency.ETH);
    System.out.println(balance.toString());
    assertThat(balance).isNotNull();
  }

  @Test
  public void getOpenOrdersTest() throws IOException {
    TradeService tradeService = exchange.getTradeService();
    OpenOrders openOrders = tradeService.getOpenOrders();
    System.out.println(openOrders.toString());
    assertThat(openOrders).isNotNull();
  }

  @Test
  public void getOrderTest() throws IOException {
    TradeService tradeService = exchange.getTradeService();
    Collection<Order> orders = tradeService.getOrder("26981387170");
    System.out.println(orders.toString());
    assertThat(orders).isNotNull();
  }

  /**
   * 下单（限价定单）
   *
   * @throws IOException
   */
  @Test
  public void placeLimitOrderTest() throws IOException {
    String orderId = placePendingOrder();
    System.out.println(orderId);
  }

  private String placePendingOrder() throws IOException {
    try {
      TradeService tradeService = exchange.getTradeService();
      HuobiAccountService accountService = (HuobiAccountService) exchange.getAccountService();
      HuobiAccount[] accounts = accountService.getAccounts();
      LimitOrder limitOrder =
          new LimitOrder(
              OrderType.BID,
              new BigDecimal("1.9"),
              new CurrencyPair("NAS", "ETH"),
              String.valueOf(accounts[0].getId()),
              null,
              new BigDecimal("0.00500"));
      return tradeService.placeLimitOrder(limitOrder);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * 下单（市场订单）
   *
   * @throws IOException
   */
  @Test
  public void placeMarketOrderTest() throws IOException {
    TradeService tradeService = exchange.getTradeService();
    HuobiAccountService accountService = (HuobiAccountService) exchange.getAccountService();
    HuobiAccount[] accounts = accountService.getAccounts();
    MarketOrder marketOrder =
        new MarketOrder(
            OrderType.ASK,
            new BigDecimal("0.0002"),
            new CurrencyPair("BTC", "USDT"),
            String.valueOf(accounts[0].getId()),
            null);
    String orderId = tradeService.placeMarketOrder(marketOrder);
    System.out.println(orderId);
  }

  @Test
  @Ignore("Use it for manual")
  public void cancelOrderTest() throws IOException {
    try {
      TradeService tradeService = exchange.getTradeService();
      boolean result = tradeService.cancelOrder("26981387170");
      System.out.println(result);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
