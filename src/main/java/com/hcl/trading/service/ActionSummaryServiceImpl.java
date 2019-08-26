package com.hcl.trading.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcl.trading.dto.ActionSummaryResponseDto;
import com.hcl.trading.entity.Orders;
import com.hcl.trading.entity.Stocks;
import com.hcl.trading.exception.OrdersNotFoundException;
import com.hcl.trading.exception.StocksNotFoundException;
import com.hcl.trading.repository.OrdersRepository;
import com.hcl.trading.repository.StockRepository;
import com.hcl.trading.util.TradingConstants;

@Service
public class ActionSummaryServiceImpl implements ActionSummaryService {
	private static Logger logger = LoggerFactory.getLogger(ActionSummaryServiceImpl.class);
	@Autowired
	StockRepository stockRepository;
	@Autowired
	OrdersRepository ordersRepository;

	@Override
	public List<ActionSummaryResponseDto> getAllActionSummary(Integer userId) {
		List<ActionSummaryResponseDto> responseList = new ArrayList<>();
		List<Orders> orders = ordersRepository.findByUserId(userId);
		if (orders.isEmpty()) {
			throw new OrdersNotFoundException(TradingConstants.ORDERS_NOT_FOUND + userId);
		} else {
			orders.stream().forEach(o -> {
				//String status = null;

				Optional<Stocks> stocks = stockRepository.findById(o.getStockId());
				if (!stocks.isPresent()) {
					throw new StocksNotFoundException(TradingConstants.STOCKS_NOT_FOUND + o.getStockId());
				} else {
//					if (o.getStockStatus().equalsIgnoreCase("P"))
//						status = "Pending";
//					else if (o.getStockStatus().equalsIgnoreCase("C"))
//						status = "Confirmed";
//					else
//						status = "Rejected";
					ActionSummaryResponseDto actionSummaryResponseDto = ActionSummaryResponseDto.builder()
							.creationDate(o.getCreationDate()).settlementDate(o.getSettlementDate())
							.stockExchangeName(stocks.get().getStockExchangeName()).stockId(stocks.get().getStockId())
							.stockName(stocks.get().getStockName()).stockQuantity(o.getStockQuantity())
							.stockStatus(o.getStockStatus()).totalPrice(o.getTotalPrice()).build();
					responseList.add(actionSummaryResponseDto);
				}
			});
		}
		logger.info("action summary fetching {} ", responseList);
		return responseList;
	}
}
