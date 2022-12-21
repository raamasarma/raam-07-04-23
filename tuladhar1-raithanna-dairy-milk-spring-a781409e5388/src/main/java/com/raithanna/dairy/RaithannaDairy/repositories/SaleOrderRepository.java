package com.raithanna.dairy.RaithannaDairy.repositories;

import com.azure.spring.data.cosmos.repository.Query;
import com.raithanna.dairy.RaithannaDairy.models.saleOrder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;


public interface SaleOrderRepository extends CrudRepository<saleOrder,Integer> {
    @Query("select * from sale_order where orderNo=?1")
    public saleOrder findByOrderNo(Integer orderNo);
}
