package com.odde.atddv2;

import com.odde.atddv2.entity.Order;
import com.odde.atddv2.entity.OrderLine;
import com.odde.atddv2.repo.OrderRepo;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.zh_cn.假如;
import io.cucumber.java.zh_cn.并且;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.math.BigDecimal;

public class OrderStep {
@Autowired
    OrderRepo orderRepo;
    @假如("存在如下订单:")
    public void 存在如下订单(DataTable dataTable) {
        orderRepo.deleteAll();
        dataTable.asMaps().forEach(row  ->
        {
            orderRepo.save(new Order().setCode(row.get("code"))
            .setProductName(row.get("productName"))
                    .setTotal(new BigDecimal(row.get("total")))
                    .setRecipientName(row.get("recipientName"))
                    .setRecipientMobile(row.get("recipientMobile"))
                    .setRecipientAddress(row.get("recipientAddress"))
                    .setStatus(Order.OrderStatus.valueOf(row.get("status")))
            );
        });
    }

    @并且("存在订单{string}的订单项:")
    @Transactional
    public void 存在订单的订单项(String ordercode, DataTable dataTable) {
        Order repoByCode = orderRepo.findByCode(ordercode);
        dataTable.asMaps().forEach(item -> {
            OrderLine orderLine = new OrderLine();
            orderLine.setOrder(repoByCode)
                        .setItemName(item.get("itemName"))
                        .setPrice(new BigDecimal(item.get("price")))
                        .setQuantity(Integer.valueOf(item.get("quantity")));
            repoByCode.getLines().add(orderLine);
        });
        orderRepo.save(repoByCode);
    }


}
