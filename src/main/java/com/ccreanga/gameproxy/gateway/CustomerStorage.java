package com.ccreanga.gameproxy.gateway;

import com.ccreanga.gameproxy.Customer;
import java.util.Set;

public interface CustomerStorage {

    void addCustomer(Customer customer);
    void removeCustomer(Customer customer);

    /**
     * The maximum number of customers will be low (100)
     */
    Set<Customer> getCustomers();

}
