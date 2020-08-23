#### [Thực hành] Quản lý khách hàng sử dụng RESTful
###### Mục tiêu
Luyện tập triển khai RESTful webservice trong một ứng dụng Spring MVC.

###### Mô tả
Trong phần này, chúng ta sẽ phát triển một ứng dụng quản lý khách hàng, ứng dụng này chỉ bao gồm phần back-end, cung cấp các API để thao tác với khách hàng:

Trả về danh sách khách hàng  
Thêm khách hàng mới  
Xoá khách hàng  
Chỉnh sửa thông tin khách hàng  
Sử dụng POSTMAN để kiểm tra hoạt động của các RESTful API.

Hướng dẫn
Bước 1: Tạo dự án mới

New project > Gradle, chọn Java và Web

Đặt GroupId là com.codegym và ArtifactId là customer-management

Bước 2: Thêm các thư viện cần thiết vào file build.gradle

Danh sách các thư viện:

dependencies {
    compile group: 'javax.servlet', name: 'javax.servlet-api', version: '3.1.0'
    compile group: 'org.springframework', name: 'spring-core', version: '4.3.17.RELEASE'
    compile group: 'org.springframework', name: 'spring-context', version: '4.3.17.RELEASE'
    compile group: 'org.springframework', name: 'spring-beans', version: '4.3.17.RELEASE'
    compile group: 'org.springframework', name: 'spring-web', version: '4.3.17.RELEASE'
    compile group: 'org.hibernate', name: 'hibernate-core', version: '5.3.0.Final'
    compile group: 'org.hibernate', name: 'hibernate-entitymanager', version: '5.3.0.Final'
    compile group: 'org.springframework', name: 'spring-orm', version: '4.3.17.RELEASE'
    compile group: 'org.springframework', name: 'spring-webmvc', version: '4.3.17.RELEASE'
    compile group: 'mysql', name: 'mysql-connector-java', version: '8.0.11'
    compile group: 'org.springframework.data', name: 'spring-data-jpa', version: '1.11.12.RELEASE'
    // https://mvnrepository.com/artifact/org.springframework/spring-tx
    compile group: 'org.springframework', name: 'spring-tx', version: '4.3.17.RELEASE'
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
    compile group: 'com.fasterxml.jackson.core', name: 'jackson-databind', version: '2.5.3'
}
Bước 3: Tạo cấu trúc của ứng dụng

Lớp ApplicationInitializer là lớp cấu hình khởi tạo cho ứng dụng (thay thế file web.xml nếu dùng cấu hình .xml):

package com.codegym.cms;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class ApplicationInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{ApplicationConfig.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[0];
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }
}
Bước 4: Tạo lớp cấu hình cho ứng dụng

Lớp ApplicationConfig được dùng để cấu hình cho toàn bộ ứng dụng (thay thế cho file dispatcher-servlet.xml nếu dùng cấu hình .xml)

package com.codegym.cms;

import com.codegym.cms.repository.CustomerRepository;
import com.codegym.cms.repository.impl.CustomerRepositoryImpl;
import com.codegym.cms.service.CustomerService;
import com.codegym.cms.service.impl.CustomerServiecImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableWebMvc
@EnableTransactionManagement
@ComponentScan("com.codegym.cms")
public class ApplicationConfig extends WebMvcConfigurerAdapter implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Bean
    public CustomerRepository customerRepository(){
        return new CustomerRepositoryImpl();
    }

    @Bean
    public CustomerService customerService(){
        return new CustomerServiecImpl();
    }

    //JPA configuration

    @Bean
    @Qualifier(value = "entityManager")
    public EntityManager entityManager(EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.createEntityManager();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan(new String[]{"com.codegym.cms.model"});

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(additionalProperties());
        return em;
    }

    @Bean
    public DataSource dataSource(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/cms");
        dataSource.setUsername( "root" );
        dataSource.setPassword( "khanhtung@123" );
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf){
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }

    Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "update");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
        return properties;
    }
}
Trong file cấu hình này, chúng ta thực hiện việc cấu hình cho JPA.

Bước 5: Cấu hình Tomcat Server

Chạy thử ứng dụng để đảm bảo các bước cài đặt và cấu hình đã hoạt động tốt.

Lưu ý: Nếu xuất hiện lỗi  java.lang.ClassNotFoundException:org.springframework.web.context.ContextLoaderListener thì cần xử lý bằng cách đi vào Project Structure -> Artifactts -> Put into Output Root để thêm tất cả các thư viện vào trong output khi build.



Bước 6: Tạo lớp model Customer

Lớp com.codegym.cms.model.Customer

package com.codegym.cms.model;

import javax.persistence.*;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String firstName;
    private String lastName;

    public Customer() {}

    public Customer(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return String.format("Customer[id=%d, firstName='%s', lastName='%s']", id, firstName, lastName);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
Bước 7: Tạo repository

Interface Repository khai báo các phương thức chung cho tất cả các repository.

package com.codegym.cms.repository;

import java.util.List;

public interface Repository <T> {
    List<T> findAll();

    T findById(Long id);

    void save(T model);

    void remove(Long id);
}
Interface CustomerRepository khai báo các phương thức riêng cho Customer, trong trường hợp này, CustomerRepository không có phương thức tuỳ biến nào cả, do đó chỉ đơn giản là kế thừa từ interface Repository.

package com.codegym.cms.repository;

import com.codegym.cms.model.Customer;

public interface CustomerRepository extends Repository<Customer> {
}
Lớp com.codegym.cms.repository.impl.CustomerRepositoryImpl:

package com.codegym.cms.repository.impl;

import com.codegym.cms.model.Customer;
import com.codegym.cms.repository.CustomerRepository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Transactional
public class CustomerRepositoryImpl implements CustomerRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Customer> findAll() {
        TypedQuery<Customer> query = em.createQuery("select c from Customer c", Customer.class);
        return query.getResultList();
    }

    @Override
    public Customer findById(Long id) {
        TypedQuery<Customer> query = em.createQuery("select c from Customer c where  c.id=:id", Customer.class);
        query.setParameter("id", id);
        try {
            return query.getSingleResult();
        }catch (NoResultException e){
            return null;
        }
    }

    @Override
    public void save(Customer model) {
        if(model.getId() != null){
            em.merge(model);
        } else {
            em.persist(model);
        }
    }

    @Override
    public void remove(Long id) {
        Customer customer = findById(id);
        if(customer != null){
            em.remove(customer);
        }
    }
}
Bước 8: Tạo các service

Interface com.codegym.cms.service.CustomerService

package com.codegym.cms.service;

import com.codegym.cms.model.Customer;

import java.util.List;

public interface CustomerService {
    List<Customer> findAll();

    Customer findById(Long id);

    void save(Customer customer);

    void remove(Long id);
}
Lớp com.codegym.cms.service.impl.CustomerServiceImpl

package com.codegym.cms.service.impl;

import com.codegym.cms.model.Customer;
import com.codegym.cms.repository.CustomerRepository;
import com.codegym.cms.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class CustomerServiecImpl implements CustomerService{

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public Customer findById(Long id) {
        return customerRepository.findById(id);
    }

    @Override
    public void save(Customer customer) {
        customerRepository.save(customer);
    }

    @Override
    public void remove(Long id) {
        customerRepository.remove(id);
    }
}
Bước 9: Khai báo các service và repository bean trong ApplicationConfig

Bổ sung khai báo 2 bean mới vào trong lớp ApplicationConfig

@Bean
public CustomerRepository customerRepository(){
    return new CustomerRepositoryImpl();
}

@Bean
public CustomerService customerService(){
    return new CustomerServiecImpl();
}
Bước 10: Tạo CustomerController

Sau đây là controller dựa trên REST, thi hành REST API.

GET request "/api/customers/" trả về danh sách các khách hàng
GET request "/api/customers/1" trả về khách hàng với ID 1
POST request "/api/customers/" với một JSON object tạo một khách hàng mới
PUT request "/api/customers/3" với một JSON object cập nhật khách hàng có ID 3
DELETE request "/api/customers/3" xóa khách hàng có ID 3
package com.codegym.cms.controller;

import java.util.List;

import com.codegym.cms.model.Customer;
import com.codegym.cms.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    //-------------------Retrieve All Customers--------------------------------------------------------

    @RequestMapping(value = "/customers/", method = RequestMethod.GET)
    public ResponseEntity<List<Customer>> listAllCustomers() {
        List<Customer> customers = customerService.findAll();
        if (customers.isEmpty()) {
            return new ResponseEntity<List<Customer>>(HttpStatus.NO_CONTENT);//You many decide to return HttpStatus.NOT_FOUND
        }
        return new ResponseEntity<List<Customer>>(customers, HttpStatus.OK);
    }

    //-------------------Retrieve Single Customer--------------------------------------------------------

    @RequestMapping(value = "/customers/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Customer> getCustomer(@PathVariable("id") long id) {
        System.out.println("Fetching Customer with id " + id);
        Customer customer = customerService.findById(id);
        if (customer == null) {
            System.out.println("Customer with id " + id + " not found");
            return new ResponseEntity<Customer>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Customer>(customer, HttpStatus.OK);
    }

    //-------------------Create a Customer--------------------------------------------------------

    @RequestMapping(value = "/customers/", method = RequestMethod.POST)
    public ResponseEntity<Void> createCustomer(@RequestBody Customer customer, UriComponentsBuilder ucBuilder) {
        System.out.println("Creating Customer " + customer.getLastName());
        customerService.save(customer);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/customers/{id}").buildAndExpand(customer.getId()).toUri());
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    //------------------- Update a Customer --------------------------------------------------------

    @RequestMapping(value = "/customers/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Customer> updateCustomer(@PathVariable("id") long id, @RequestBody Customer customer) {
        System.out.println("Updating Customer " + id);

        Customer currentCustomer = customerService.findById(id);

        if (currentCustomer == null) {
            System.out.println("Customer with id " + id + " not found");
            return new ResponseEntity<Customer>(HttpStatus.NOT_FOUND);
        }

        currentCustomer.setFirstName(customer.getFirstName());
        currentCustomer.setLastName(customer.getLastName());
        currentCustomer.setId(customer.getId());

        customerService.save(currentCustomer);
        return new ResponseEntity<Customer>(currentCustomer, HttpStatus.OK);
    }

    //------------------- Delete a Customer --------------------------------------------------------

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
Trong đó:

@RestController là kết hợp của @Controller và @ResponseBody
@RequestBody: Nếu tham số phương thức được chú thích bằng @RequestBody, Spring sẽ liên kết phần thân yêu cầu HTTP đến với tham số đó.
ResponseEntity đại diện cho toàn bộ phản hồi HTTP
@PathVariable chỉ ra rằng tham số phương thức sẽ được liên kết với URI (id).
Bước 11: Triển khai và test API

Để test API, chúng ta sử dụng POSTMAN

11.1. Hiển thị danh sách khách hàng:

Mở POSTMAN tool, chọn loại request (GET trong trường hợp này). Chỉ định URI (http://localhost:8080/customers/). Nhấn Send:



11.2. Hiển thị khách hàng theo ID:

Mở POSTMAN tool, chọn loại request (GET trong trường hợp này). Chỉ định URI (http://localhost:8080/customers/10). Nhấn Send:



11.3. Thêm mới khách hàng:

Mở POSTMAN tool, chọn loại request (POST trong trường hợp này). Chỉ định URI (http://localhost:8080/customers/). Chọn tab "Body" , chọn "raw", chọn "JSON(application/json)". Bạn thêm một đối tượng Json(customer). Nhấn Send:



11.4. Sửa thông tin khách hàng:

Mở POSTMAN tool, chọn loại request (PUT trong trường hợp này). Chỉ định URI (http://localhost:8080/customers/10). Chọn tab "Body" , chọn "raw", chọn "JSON(application/json)". Bạn add đối tượng Json(customer) cần sửa đổi. Nhấn Send:



11.5. Xóa khách hàng:

Mở POSTMAN tool, chọn loại request (DELETE trong trường hợp này). Chỉ định URI (http://localhost:8080/customers/10). Nhấn Send:



Mã nguồn tham khảo: https://github.com/codegym-vn/java-spring-customer-management-restfull

Để hoàn thành bài thực hành, học viên cần: Đưa mã nguồn lên GitHub Dán link của repository lên phần nộp bài trên CodeGymX
