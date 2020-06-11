package controller;

import model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import service.CustomerService;

import java.util.List;

@RestController
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/customers/")
    public ResponseEntity <List<Customer>> getAllCustomer(){
        List<Customer> customers = customerService.findAll();
        if(customers.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(customers,HttpStatus.OK);
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity <Customer> getCustomerById(@PathVariable ("id") Long id){
        Customer customer = customerService.findById(id);
        if(customer==null){
            return  new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(customer,HttpStatus.OK);
    }

    @RequestMapping(value = "/customers/", method = RequestMethod.POST)
    public ResponseEntity<Void> createCustomer(@RequestBody Customer customer, UriComponentsBuilder ucBuilder) {
        System.out.println("Creating Customer " + customer.getLastname());
        customerService.save(customer);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/customers/{id}").buildAndExpand(customer.getId()).toUri());
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/customers/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Customer> updateCustomer(@PathVariable("id") long id, @RequestBody Customer customer) {
        System.out.println("Updating Customer " + id);

        Customer currentCustomer = customerService.findById(id);

        if (currentCustomer == null) {
            System.out.println("Customer with id " + id + " not found");
            return new ResponseEntity<Customer>(HttpStatus.NOT_FOUND);
        }

        currentCustomer.setFirstname(customer.getFirstname());
        currentCustomer.setLastname(customer.getLastname());
        currentCustomer.setId(customer.getId());

        customerService.save(currentCustomer);
        return new ResponseEntity<Customer>(currentCustomer, HttpStatus.OK);
    }

    @RequestMapping(value = "/customers/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Customer> deleteCustomer(@PathVariable("id") long id) {
        System.out.println("Fetching & Deleting Customer with id " + id);

        Customer customer = customerService.findById(id);
        if (customer == null) {
            System.out.println("Unable to delete. Customer with id " + id + " not found");
            return new ResponseEntity<Customer>(HttpStatus.NOT_FOUND);
        }

        customerService.remove(id);
        return new ResponseEntity<Customer>(HttpStatus.NO_CONTENT);
    }

}
