package com.raithanna.dairy.RaithannaDairy.repositories;

import com.azure.spring.data.cosmos.repository.Query;
import com.raithanna.dairy.RaithannaDairy.models.customer;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends CrudRepository<customer,Integer> {
    @Query("select * from where code=:?1")
    List<customer>findByCode(String code);

    customer findTopByOrderByCustnoDesc();

    //@Query("INSERT INTO customer(code, cust_no) VALUES (:code, :cust_no)")
    //void insertOne(customer Customer);

    //@Query("UPDATE book SET title = :title where id = :id")
    //void updateOne(Book book);
}
