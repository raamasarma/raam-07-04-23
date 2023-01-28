package com.raithanna.dairy.RaithannaDairy.repositories;

import com.azure.spring.data.cosmos.repository.Query;
import com.raithanna.dairy.RaithannaDairy.models.dailySales;
import com.raithanna.dairy.RaithannaDairy.models.saleOrder;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DailySalesRepository extends CrudRepository<dailySales, Integer> {

    @Query("select * from daily_sales where orderNo=?1 ")
    List<dailySales> findByOrderNo(Integer orderNo);


}
